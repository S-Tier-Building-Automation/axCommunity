package org.axcommunity.niagara.math;

import javax.baja.log.Log;
import javax.baja.naming.BOrd;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDynamicEnum;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BLink;
import javax.baja.sys.BObject;
import javax.baja.sys.BRelTime;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.units.BUnit;
import javax.baja.util.BFormat;
import javax.baja.util.TextUtil;

/**
This object tracks part counts and based on shift and break schedules,<br>
provides parts per shift as well as calculate parts per hour and cycle<br>
time.  Also outputs total breaks and total time on break.<br> 

<br>
<br>
The schedule linking logic was borrowed && modified from axCommunity's BDynamicLinkNumeric, written by Mike Arnott, Kors Engineering.<br>
<br>
@author    Eric Bishop 
@creation  23 Mar 12
@version   $Revision: 6$ $Date: 02/10/2017 10:30 AM$
<br>
Updates:<br>
        2015-03-09 - Fixed roundToNearestSecond(BAbsTime).  When the input was 59 seconds, it threw errors when attempting to round.<br>
        2016-07-26 - Added targets, and fixed the removal of the *_AvgCycleTime slots.<br>
        2017-02-02 - Fixed target data refreshing and set schedule links at midnight.<br>
        2017-02-10 - Fixed an issue with the total break hours going negative (caused by recording breaks for shifts that do not contain any data)<br>
*/

public class BProductionCounter extends BComponent
{
  public static final Action inGood = newAction(Flags.OPERATOR,null);
  public void inGood() {invoke(inGood,null,null);}
  public void doInGood()
  {
    if(!getOnlyTrackPartsDurringValidShift() || getInCurrentShift().getValue() >=1) adjustGoodParts(1);
  }
  
  public static final Action inBad = newAction(Flags.OPERATOR,null);
  public void inBad() {invoke(inBad,null,null);}
  public void doInBad()
  {
    if(!getOnlyTrackPartsDurringValidShift() || getInCurrentShift().getValue() >=1) adjustBadParts(1);
  }
  /**Adds a good part to the good and total part counts*/
  public static final Property inGoodTransaction = newProperty(Flags.SUMMARY, false);
  /**Adds a good part to the good and total part counts*/
  public boolean getInGoodTransaction() { return getBoolean(inGoodTransaction);}
  /**Adds a good part to the good and total part counts*/
  public void setInGoodTransaction(boolean v) {setBoolean(inGoodTransaction,v);}
  
  /**Adds a bad part to the bad and total part counts*/
  public static final Property inBadTransaction = newProperty(Flags.SUMMARY, false);
  /**Adds a bad part to the bad and total part counts*/
  public boolean getInBadTransaction() { return getBoolean(inBadTransaction);}
  /**Adds a bad part to the bad and total part counts*/
  public void setInBadTransaction(boolean v) {setBoolean(inBadTransaction,v);}
  
  /**Sets the number of shifts to track.  Automatically updates if the current shift number is higher.*/
  public static final Property inNumberOfShifts = newProperty(0, 0, BFacets.make(BFacets.MIN, BInteger.make(1), BFacets.MAX, BInteger.make(60)));
  /**Sets the number of shifts to track.  Automatically updates if the current shift number is higher.*/
  public int getInNumberOfShifts() { return getInt(inNumberOfShifts); }
  /**Sets the number of shifts to track.  Automatically updates if the current shift number is higher.*/
  public void setInNumberOfShifts(int v) {setInt(inNumberOfShifts,v);}
  
  /**Sets the shift that resets all shift counts.  Set to -1 to only overwrite values.*/
  public static final Property inShiftToResetCountsOn = newProperty(0, -1, BFacets.make(BFacets.MIN, BInteger.make(-1), BFacets.MAX, BInteger.make(60)));
  /**Sets the shift that resets all shift counts.  Set to -1 to only overwrite values.*/
  public int getInShiftToResetCountsOn() { return getInt(inShiftToResetCountsOn); }
  /**Sets the shift that resets all shift counts.  Set to -1 to only overwrite values.*/
  public void setInShiftToResetCountsOn(int v) {setInt(inShiftToResetCountsOn,v);}
  
  /**Array used to determine how the hourly counts are calculated<br>
  * 0 = All parts<br>
  * 1 = Good parts only*/
  static final String[] hourlyCountChoices = {"All$20Parts", "Good$20Parts$20Only"};
  
  /**Determines how the hourly counts are calculated, using the string array "hourlyCountChoices".<br>
   * 0 = All parts<br>
   * 1 = Good parts only*/
  public static final Property determinePartsPerHourAndCycleTimeBasedOn = newProperty(0, BDynamicEnum.make(1, BEnumRange.make(hourlyCountChoices)));
  /**Determines how the hourly counts are calculated, using the string array "hourlyCountChoices".<br>
   * 0 = All parts<br>
   * 1 = Good parts only*/
  public BDynamicEnum getDeterminePartsPerHourAndCycleTimeBasedOn() { return (BDynamicEnum)get(determinePartsPerHourAndCycleTimeBasedOn); }
  /**Determines how the hourly counts are calculated, using the string array "hourlyCountChoices".<br>
   * 0 = All parts<br>
   * 1 = Good parts only*/
  public void setDeterminePartsPerHourAndCycleTimeBasedOn(BDynamicEnum v) { set(determinePartsPerHourAndCycleTimeBasedOn,v,null); }
  
  /**If true, any shift where no transactions are processed will record the time as "Off Shift Hours".  Otherwise, the time will go against the "Avg Parts Per Hour" and "Avg Cycle Time".*/
  public static final Property ignoreShiftsWithoutAnyTransactions = newProperty(0, true);
  /**If true, any shift where no transactions are processed will record the time as "Off Shift Hours".  Otherwise, the time will go against the "Avg Parts Per Hour" and "Avg Cycle Time".*/
  public boolean getIgnoreShiftsWithoutAnyTransactions() { return getBoolean(ignoreShiftsWithoutAnyTransactions);}
  /**If true, any shift where no transactions are processed will record the time as "Off Shift Hours".  Otherwise, the time will go against the "Avg Parts Per Hour" and "Avg Cycle Time".*/
  public void setIgnoreShiftsWithoutAnyTransactions(boolean v) {setBoolean(ignoreShiftsWithoutAnyTransactions,v);}
  
  /**If true, part counts will only increase while inCurrentShift >= 1.  This is necessary if a lot of parts will be produced during off-shift hours, since the part counts are used in determining the "Avg Parts Per Hour" and "Avg Cycle Time".*/
  public static final Property onlyTrackPartsDurringValidShift = newProperty(0, true);
  /**If true, part counts will only increase while inCurrentShift >= 1.  This is necessary if a lot of parts will be produced during off-shift hours, since the part counts are used in determining the "Avg Parts Per Hour" and "Avg Cycle Time".*/
  public boolean getOnlyTrackPartsDurringValidShift() { return getBoolean(onlyTrackPartsDurringValidShift);}
  /**If true, part counts will only increase while inCurrentShift >= 1.  This is necessary if a lot of parts will be produced during off-shift hours, since the part counts are used in determining the "Avg Parts Per Hour" and "Avg Cycle Time".*/
  public void setOnlyTrackPartsDurringValidShift(boolean v) {setBoolean(onlyTrackPartsDurringValidShift,v);}
  
  /**Specify a refresh interval used ONLY for "currentShiftElapsedTime", "currentShiftRemainingTime", all "AvgPartsPerHour" outputs, and all "AvgCycleTime" outputs.  Executes the action "refreshValuesForToday"*/
  public static final Property inRefreshInterval = newProperty(0, BRelTime.make(60000), BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE, BFacets.MIN, BRelTime.make(100)));
  /**Specify a refresh interval used ONLY for "currentShiftElapsedTime", "currentShiftRemainingTime", all "AvgPartsPerHour" outputs, and all "AvgCycleTime" outputs.  Executes the action "refreshValuesForToday"*/
  public BRelTime getInRefreshInterval() { return (BRelTime)get(inRefreshInterval);}
  /**Specify a refresh interval used ONLY for "currentShiftElapsedTime", "currentShiftRemainingTime", all "AvgPartsPerHour" outputs, and all "AvgCycleTime" outputs.  Executes the action "refreshValuesForToday"*/
  public void setInRefreshInterval(BRelTime v) {set(inRefreshInterval,v);}
  
  /**Set this to the ord of a numeric schedule that outputs the current shift.  If no shift is active, set the output to 0 (If using a numeric schedule, set default output to 0, NOT null).
   * Example: station:|slot:/Global/ShiftSetup/shiftSchedule*/
  public static final Property inShiftScheduleOrd = newProperty(0, "station:|slot:/Global/ShiftSetup/shiftSchedule");
  /**Set this to the ord of a numeric schedule that outputs the current shift.  If no shift is active, set the output to 0 (If using a numeric schedule, set default output to 0, NOT null).
   * Example: station:|slot:/Global/ShiftSetup/shiftSchedule*/
  public String getInShiftScheduleOrd() { return getString(inShiftScheduleOrd);}
  /**Set this to the ord of a numeric schedule that outputs the current shift.  If no shift is active, set the output to 0 (If using a numeric schedule, set default output to 0, NOT null).
   * Example: station:|slot:/Global/ShiftSetup/shiftSchedule*/
  public void setInShiftScheduleOrd(String v) {setString(inShiftScheduleOrd,v);}
  
  /**Ord of a boolean schedule that is set to true whenever a break occurs.  Creates a link to "inScheduledBreak". If no break is active, set the output to false (If using a boolean schedule, set default output to false, NOT null)
   * Example: station:|slot:/Global/ShiftSetup/breakSchedule*/
  public static final Property inBreakScheduleOrd = newProperty(0, "station:|slot:/Global/ShiftSetup/scheduledBreaks");
  /**Ord of a boolean schedule that is set to true whenever a break occurs.  Creates a link to "inScheduledBreak". If no break is active, set the output to false (If using a boolean schedule, set default output to false, NOT null)
   * Example: station:|slot:/Global/ShiftSetup/breakSchedule*/
  public String getInBreakScheduleOrd() { return getString(inBreakScheduleOrd);}
  /**Ord of a boolean schedule that is set to true whenever a break occurs.  Creates a link to "inScheduledBreak". If no break is active, set the output to false (If using a boolean schedule, set default output to false, NOT null)
   * Example: station:|slot:/Global/ShiftSetup/breakSchedule*/
  public void setInBreakScheduleOrd(String v) {setString(inBreakScheduleOrd,v);}
  
  public static final Property inShiftTargetsOrd = newProperty(0, "station:|slot:/Global/ShiftSetup/ShiftTargets");
  public String getInShiftTargetsOrd() { return getString(inShiftTargetsOrd);}
  public void setInShiftTargetsOrd(String v) {setString(inShiftTargetsOrd,v);}
  
  /**This slot is the receiving end of a link from the "inShiftScheduleOrd"*/
  public static final Property inCurrentShift = newProperty(Flags.HIDDEN, new BStatusNumeric(0, BStatus.nullStatus));
  /**This slot is the receiving end of a link from the "inShiftScheduleOrd"*/
  public BStatusNumeric getInCurrentShift() { return (BStatusNumeric)get(inCurrentShift);}
  /**This slot is the receiving end of a link from the "inShiftScheduleOrd"*/
  public void setInCurrentShift(BStatusNumeric v) {set(inCurrentShift,v);}
  
  /**Normally hidden and linked to the object listed in "inBreakScheduleOrd".  If desired, this slot can be directly linked to a boolean input if "inBreakScheduleOrd" is left blank.*/
  public static final Property inScheduledBreak = newProperty(Flags.HIDDEN, new BStatusBoolean(false, BStatus.nullStatus));
  /**Normally hidden and linked to the object listed in "inBreakScheduleOrd".  If desired, this slot can be directly linked to a boolean input if "inBreakScheduleOrd" is left blank.*/
  public BStatusBoolean getInScheduledBreak() { return (BStatusBoolean)get(inScheduledBreak);}
  /**Normally hidden and linked to the object listed in "inBreakScheduleOrd".  If desired, this slot can be directly linked to a boolean input if "inBreakScheduleOrd" is left blank.*/
  public void setInScheduledBreak(BStatusBoolean v) {set(inScheduledBreak,v);}
  
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or end of "off shift")*/
  public static final Property inShiftEndTime = newProperty(Flags.HIDDEN, BAbsTime.make(), BFacets.make(BFacets.SHOW_SECONDS,true));
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or end of "off shift")*/
  public BAbsTime getInShiftEndTime() { return (BAbsTime)get(inShiftEndTime);}
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or end of "off shift")*/
  public void setInShiftEndTime(BAbsTime v) {set(inShiftEndTime,v);}
  
  public static final Property inTargetData = newProperty(Flags.HIDDEN, "", BFacets.make(BFacets.MULTI_LINE, true));
  public String getInTargetData() { return getString(inTargetData);}
  public void setInTargetData(String v) {setString(inTargetData,v);}
  
  public static final Property inShiftToResetCountsOnOrd = newProperty(0, "");
  public String getInShiftToResetCountsOnOrd() { return getString(inShiftToResetCountsOnOrd);}
  public void setInShiftToResetCountsOnOrd(String v) {setString(inShiftToResetCountsOnOrd,v);}
  
  public static final Property shiftToResetCountsOn = newProperty(Flags.HIDDEN, new BStatusNumeric());
  public BStatusNumeric getShiftToResetCountsOn() { return (BStatusNumeric)get(shiftToResetCountsOn);}
  public void setShiftToResetCountsOn(BStatusNumeric v) {set(shiftToResetCountsOn,v);}
  
  /**Value pulled from "inScheduledBreak"*/
  public static final Property scheduledBreak = newProperty(Flags.READONLY, new BStatusBoolean(false, BStatus.nullStatus));
  /**Value pulled from "inScheduledBreak"*/
  public BStatusBoolean getScheduledBreak() { return (BStatusBoolean)get(scheduledBreak);}
  /**Value pulled from "inScheduledBreak"*/
  public void setScheduledBreak(BStatusBoolean v) {set(scheduledBreak,v);}
  
  /**True if "scheduledBreak" is true and "currentShift" > 0*/
  public static final Property scheduledProduction = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusBoolean(false, BStatus.nullStatus));
  /**True if "scheduledBreak" is true and "currentShift" > 0*/
  public BStatusBoolean getScheduledProduction() { return (BStatusBoolean)get(scheduledProduction);}
  /**True if "scheduledBreak" is true and "currentShift" > 0*/
  public void setScheduledProduction(BStatusBoolean v) {set(scheduledProduction,v);}
  
  /**Value pulled from "inCurrentShift"*/
  public static final Property currentShift = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusNumeric(0, BStatus.nullStatus), BFacets.make(BFacets.PRECISION, BInteger.make(0)));
  /**Value pulled from "inCurrentShift"*/
  public BStatusNumeric getCurrentShift() { return (BStatusNumeric)get(currentShift);}
  /**Value pulled from "inCurrentShift"*/
  public void setCurrentShift(BStatusNumeric v) {set(currentShift,v);}
  
  /**Starting time of the current shift.  If the current shift = 0, then this is the end of the last shift (or start of "off shift")*/
  public static final Property currentShiftStartTime = newProperty(Flags.SUMMARY|Flags.READONLY, BAbsTime.make(), BFacets.make(BFacets.SHOW_SECONDS,true));
  /**Starting time of the current shift.  If the current shift = 0, then this is the end of the last shift (or start of "off shift")*/
  public BAbsTime getCurrentShiftStartTime() { return (BAbsTime)get(currentShiftStartTime);}
  /**Starting time of the current shift.  If the current shift = 0, then this is the end of the last shift (or start of "off shift")*/
  public void setCurrentShiftStartTime(BAbsTime v) {set(currentShiftStartTime,v);}
  
  /**Outputs the current shift's elapsed time.  Refreshes at the rate specified in "inRefreshInterval"*/
  public static final Property currentShiftElapsedTime = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**Outputs the current shift's elapsed time.  Refreshes at the rate specified in "inRefreshInterval"*/
  public BRelTime getCurrentShiftElapsedTime() { return (BRelTime)get(currentShiftElapsedTime);}
  /**Outputs the current shift's elapsed time.  Refreshes at the rate specified in "inRefreshInterval"*/
  public void setCurrentShiftElapsedTime(BRelTime v) {set(currentShiftElapsedTime,v);}
  
  /**Outputs the current shift's remaining time.  Refreshes at the rate specified in "inRefreshInterval"*/
  public static final Property currentShiftRemainingTime = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**Outputs the current shift's remaining time.  Refreshes at the rate specified in "inRefreshInterval"*/
  public BRelTime getCurrentShiftRemainingTime() { return (BRelTime)get(currentShiftRemainingTime);}
  /**Outputs the current shift's remaining time.  Refreshes at the rate specified in "inRefreshInterval"*/
  public void setCurrentShiftRemainingTime(BRelTime v) {set(currentShiftRemainingTime,v);}
  
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or End of "off shift")*/
  public static final Property currentShiftEndTime = newProperty(Flags.SUMMARY|Flags.READONLY, BAbsTime.make(), BFacets.make(BFacets.SHOW_SECONDS,true));
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or End of "off shift")*/
  public BAbsTime getCurrentShiftEndTime() { return (BAbsTime)get(currentShiftEndTime);}
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or End of "off shift")*/
  public void setCurrentShiftEndTime(BAbsTime v) {set(currentShiftEndTime,v);}
  
  public static final Property currentShiftScheduledDuration = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_SECONDS, BBoolean.FALSE, BFacets.SHOW_SECONDS, BBoolean.FALSE));
  public BRelTime getCurrentShiftScheduledDuration() { return (BRelTime)get(currentShiftScheduledDuration);}
  public void setCurrentShiftScheduledDuration(BRelTime v) {set(currentShiftScheduledDuration,v);}
  
  /**Total number of good parts processed during this shift.*/
  public static final Property currentShiftGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**Total number of good parts processed during this shift.*/
  public int getCurrentShiftGoodParts() { return getInt(currentShiftGoodParts);}
  /**Total number of good parts processed during this shift.*/
  public void setCurrentShiftGoodParts(int v) {setInt(currentShiftGoodParts,v);}
  
  /**Total number of bad parts processed during this shift.*/
  public static final Property currentShiftBadParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**Total number of bad parts processed during this shift.*/
  public int getCurrentShiftBadParts() { return getInt(currentShiftBadParts);}
  /**Total number of bad parts processed during this shift.*/
  public void setCurrentShiftBadParts(int v) {setInt(currentShiftBadParts,v);}
  
  /**The total number of good and bad parts processed during the current shift*/
  public static final Property currentShiftTotalParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good and bad parts processed during the current shift*/
  public int getCurrentShiftTotalParts() { return getInt(currentShiftTotalParts);}
  /**The total number of good and bad parts processed during the current shift*/
  public void setCurrentShiftTotalParts(int v) {setInt(currentShiftTotalParts,v);}
  
//  public static final Property currentShiftPercentageGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.tryMake("showUnits=b:true|units=u:percent;%;;;|precision=i:1"));
  public static final Property currentShiftPercentageGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getCurrentShiftPercentageGoodParts() { return getDouble(currentShiftPercentageGoodParts);}
  public void setCurrentShiftPercentageGoodParts(double v) {setDouble(currentShiftPercentageGoodParts,v);}
  
  public static final Property currentShiftPercentageBadParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getCurrentShiftPercentageBadParts() { return getDouble(currentShiftPercentageBadParts);}
  public void setCurrentShiftPercentageBadParts(double v) {setDouble(currentShiftPercentageBadParts,v);}
  
  /**The average number of parts processed per hour (excluding breaks) during the current shift.*/
  public static final Property currentShiftAvgPartsPerHour = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  /**The average number of parts processed per hour (excluding breaks) during the current shift.*/
  public double getCurrentShiftAvgPartsPerHour() { return getDouble(currentShiftAvgPartsPerHour);}
  /**The average number of parts processed per hour (excluding breaks) during the current shift.*/
  public void setCurrentShiftAvgPartsPerHour(double v) {setDouble(currentShiftAvgPartsPerHour,v);}
  
  public static final Property currentShiftTargetPartsPerHour = newProperty(Flags.SUMMARY|Flags.READONLY, 0, BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  public double getCurrentShiftTargetPartsPerHour() { return getDouble(currentShiftTargetPartsPerHour);}
  public void setCurrentShiftTargetPartsPerHour(double v) {setDouble(currentShiftTargetPartsPerHour,v);}
  
  public static final Property currentShiftPercentageOfTargetPph = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getCurrentShiftPercentageOfTargetPph() { return getDouble(currentShiftPercentageOfTargetPph);}
  public void setCurrentShiftPercentageOfTargetPph(double v) {setDouble(currentShiftPercentageOfTargetPph,v);}
  
  /**The average cycle time (excluding breaks) during the current shift.*/
  public static final Property currentShiftAvgCycleTime = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The average cycle time (excluding breaks) during the current shift.*/
  public BRelTime getCurrentShiftAvgCycleTime() { return (BRelTime)get(currentShiftAvgCycleTime);}
  /**The average cycle time (excluding breaks) during the current shift.*/
  public void setCurrentShiftAvgCycleTime(BRelTime v) {set(currentShiftAvgCycleTime,v);}
  
  public static final Property currentShiftTargetCycleTime = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  public BRelTime getCurrentShiftTargetCycleTime() { return (BRelTime)get(currentShiftTargetCycleTime);}
  public void setCurrentShiftTargetCycleTime(BRelTime v) {set(currentShiftTargetCycleTime,v);}
  
  public static final Property currentShiftPercentageOfTargetCycleTime = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getCurrentShiftPercentageOfTargetCycleTime() { return getDouble(currentShiftPercentageOfTargetCycleTime);}
  public void setCurrentShiftPercentageOfTargetCycleTime(double v) {setDouble(currentShiftPercentageOfTargetCycleTime,v);}
  
  /**The total time the current shift has been on break.*/
  public static final Property currentShiftTotalBreakHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total time the current shift has been on break.*/
  public BRelTime getCurrentShiftTotalBreakHours() { return (BRelTime)get(currentShiftTotalBreakHours);}
  /**The total time the current shift has been on break.*/
  public void setCurrentShiftTotalBreakHours(BRelTime v) {set(currentShiftTotalBreakHours,v);}
  
  /**The total number of breaks the current shift has taken.*/
  public static final Property currentShiftTotalBreaks = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of breaks the current shift has taken.*/
  public int getCurrentShiftTotalBreaks() { return getInt(currentShiftTotalBreaks);}
  /**The total number of breaks the current shift has taken.*/
  public void setCurrentShiftTotalBreaks(int v) {setInt(currentShiftTotalBreaks,v);}
  
  public static final Property currentShiftTarget = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusNumeric(0,BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  public BStatusNumeric getCurrentShiftTarget() { return (BStatusNumeric)get(currentShiftTarget);}
  public void setCurrentShiftTarget(BStatusNumeric v) {set(currentShiftTarget,v);}
  
  public static final Property currentShiftPercentageOfTarget = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusNumeric(0,BStatus.ok), BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public BStatusNumeric getCurrentShiftPercentageOfTarget() { return (BStatusNumeric)get(currentShiftPercentageOfTarget);}
  public void setCurrentShiftPercentageOfTarget(BStatusNumeric v) {set(currentShiftPercentageOfTarget,v);}
  
  
  /**The total number of good parts processed today.*/
  public static final Property todayGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good parts processed today.*/
  public int getTodayGoodParts() { return getInt(todayGoodParts);}
  /**The total number of good parts processed today.*/
  public void setTodayGoodParts(int v) {setInt(todayGoodParts,v);}
  
  /**The total number of bad parts processed today.*/
  public static final Property todayBadParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of bad parts processed today.*/
  public int getTodayBadParts() { return getInt(todayBadParts);}
  /**The total number of bad parts processed today.*/
  public void setTodayBadParts(int v) {setInt(todayBadParts,v);}
  
  /**The total number of good and bad parts processed today.*/
  public static final Property todayTotalParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good and bad parts processed today.*/
  public int getTodayTotalParts() { return getInt(todayTotalParts);}
  /**The total number of good and bad parts processed today.*/
  public void setTodayTotalParts(int v) {setInt(todayTotalParts,v);}
  
  public static final Property todayPercentageGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getTodayPercentageGoodParts() { return getDouble(todayPercentageGoodParts);}
  public void setTodayPercentageGoodParts(double v) {setDouble(todayPercentageGoodParts,v);}
  
  public static final Property todayPercentageBadParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getTodayPercentageBadParts() { return getDouble(todayPercentageBadParts);}
  public void setTodayPercentageBadParts(double v) {setDouble(todayPercentageBadParts,v);}
  
  /**The average number of parts processed per hour (excluding breaks) today.*/
  public static final Property todayAvgPartsPerHour = newProperty(Flags.SUMMARY|Flags.READONLY, 0, BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  /**The average number of parts processed per hour (excluding breaks) today.*/
  public double getTodayAvgPartsPerHour() { return getDouble(todayAvgPartsPerHour);}
  /**The average number of parts processed per hour (excluding breaks) today.*/
  public void setTodayAvgPartsPerHour(double v) {setDouble(todayAvgPartsPerHour,v);}
  
  /**The average cycle time (excluding breaks) today.*/
  public static final Property todayAvgCycleTime = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The average cycle time (excluding breaks) today.*/
  public BRelTime getTodayAvgCycleTime() { return (BRelTime)get(todayAvgCycleTime);}
  /**The average cycle time (excluding breaks) today.*/
  public void setTodayAvgCycleTime(BRelTime v) {set(todayAvgCycleTime,v);}
  
  /**The total hours run (excluding breaks and off-shift) today.*/
  public static final Property todayTotalHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total hours run (excluding breaks and off-shift) today.*/
  public BRelTime getTodayTotalHours() { return (BRelTime)get(todayTotalHours);}
  /**The total hours run (excluding breaks and off-shift) today.*/
  public void setTodayTotalHours(BRelTime v) {set(todayTotalHours,v);}
  
  /**The total time on break today.*/
  public static final Property todayTotalBreakHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total time on break today.*/
  public BRelTime getTodayTotalBreakHours() { return (BRelTime)get(todayTotalBreakHours);}
  /**The total time on break today.*/
  public void setTodayTotalBreakHours(BRelTime v) {set(todayTotalBreakHours,v);}
  
  /**The total number of breaks taken during the current shift.*/
  public static final Property todayTotalBreaks = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of breaks taken during the current shift.*/
  public int getTodayTotalBreaks() { return getInt(todayTotalBreaks);}
  /**The total number of breaks taken during the current shift.*/
  public void setTodayTotalBreaks(int v) {setInt(todayTotalBreaks,v);}
  
  /**The total hours a shift has not run (currentShfit = 0) today.*/
  public static final Property todayTotalOffShiftHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total hours a shift has not run (currentShfit = 0) today.*/
  public BRelTime getTodayTotalOffShiftHours () { return (BRelTime)get(todayTotalOffShiftHours );}
  /**The total hours a shift has not run (currentShfit = 0) today.*/
  public void setTodayTotalOffShiftHours (BRelTime v) {set(todayTotalOffShiftHours ,v);}
  
  public static final Property todayTarget = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusNumeric(0,BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  public BStatusNumeric getTodayTarget() { return (BStatusNumeric)get(todayTarget);}
  public void setTodayTarget(BStatusNumeric v) {set(todayTarget,v);}
  
  public static final Property todayPercentageOfTarget = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusNumeric(0,BStatus.ok), BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public BStatusNumeric getTodayPercentageOfTarget() { return (BStatusNumeric)get(todayPercentageOfTarget);}
  public void setTodayPercentageOfTarget(BStatusNumeric v) {set(todayPercentageOfTarget,v);}
  
  
  /**The total number of good parts processed yesterday.*/
  public static final Property yesterdayGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good parts processed yesterday.*/
  public int getYesterdayGoodParts() { return getInt(yesterdayGoodParts);}
  /**The total number of good parts processed yesterday.*/
  public void setYesterdayGoodParts(int v) {setInt(yesterdayGoodParts,v);}
  
  /**The total number of bad parts processed yesterday.*/
  public static final Property yesterdayBadParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of bad parts processed yesterday.*/
  public int getYesterdayBadParts() { return getInt(yesterdayBadParts);}
  /**The total number of bad parts processed yesterday.*/
  public void setYesterdayBadParts(int v) {setInt(yesterdayBadParts,v);}
  
  /**The total number of good and bad parts processed yesterday.*/
  public static final Property yesterdayTotalParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good and bad parts processed yesterday.*/
  public int getYesterdayTotalParts() { return getInt(yesterdayTotalParts);}
  /**The total number of good and bad parts processed yesterday.*/
  public void setYesterdayTotalParts(int v) {setInt(yesterdayTotalParts,v);}
  
  public static final Property yesterdayPercentageGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getYesterdayPercentageGoodParts() { return getDouble(yesterdayPercentageGoodParts);}
  public void setYesterdayPercentageGoodParts(double v) {setDouble(yesterdayPercentageGoodParts,v);}
  
  public static final Property yesterdayPercentageBadParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getYesterdayPercentageBadParts() { return getDouble(yesterdayPercentageBadParts);}
  public void setYesterdayPercentageBadParts(double v) {setDouble(yesterdayPercentageBadParts,v);}
  
  /**The average number of parts processed per hour (excluding breaks) yesterday.*/
  public static final Property yesterdayAvgPartsPerHour = newProperty(Flags.SUMMARY|Flags.READONLY, 0, BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  /**The average number of parts processed per hour (excluding breaks) yesterday.*/
  public double getYesterdayAvgPartsPerHour() { return getDouble(yesterdayAvgPartsPerHour);}
  /**The average number of parts processed per hour (excluding breaks) yesterday.*/
  public void setYesterdayAvgPartsPerHour(double v) {setDouble(yesterdayAvgPartsPerHour,v);}
  
  /**The average cycle time (excluding breaks) yesterday.*/
  public static final Property yesterdayAvgCycleTime = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The average cycle time (excluding breaks) yesterday.*/
  public BRelTime getYesterdayAvgCycleTime() { return (BRelTime)get(yesterdayAvgCycleTime);}
  /**The average cycle time (excluding breaks) yesterday.*/
  public void setYesterdayAvgCycleTime(BRelTime v) {set(yesterdayAvgCycleTime,v);}
  
  /**The total hours run (excluding breaks and off-shift) yesterday.*/
  public static final Property yesterdayTotalHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total hours run (excluding breaks and off-shift) yesterday.*/
  public BRelTime getYesterdayTotalHours() { return (BRelTime)get(yesterdayTotalHours);}
  /**The total hours run (excluding breaks and off-shift) yesterday.*/
  public void setYesterdayTotalHours(BRelTime v) {set(yesterdayTotalHours,v);}
  
  /**The total time on break yesterday.*/
  public static final Property yesterdayTotalBreakHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total time on break yesterday.*/
  public BRelTime getYesterdayTotalBreakHours() { return (BRelTime)get(yesterdayTotalBreakHours);}
  /**The total time on break yesterday.*/
  public void setYesterdayTotalBreakHours(BRelTime v) {set(yesterdayTotalBreakHours,v);}
  
  /**The total number of breaks taken yesterday.*/
  public static final Property yesterdayTotalBreaks = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of breaks taken yesterday.*/
  public int getYesterdayTotalBreaks() { return getInt(yesterdayTotalBreaks);}
  /**The total number of breaks taken yesterday.*/
  public void setYesterdayTotalBreaks(int v) {setInt(yesterdayTotalBreaks,v);}
  
  /**The total hours a shift has not run (currentShfit = 0) yesterday.*/
  public static final Property yesterdayTotalOffShiftHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total hours a shift has not run (currentShfit = 0) yesterday.*/
  public BRelTime getYesterdayTotalOffShiftHours () { return (BRelTime)get(yesterdayTotalOffShiftHours );}
  /**The total hours a shift has not run (currentShfit = 0) yesterday.*/
  public void setYesterdayTotalOffShiftHours (BRelTime v) {set(yesterdayTotalOffShiftHours ,v);}
  
  public static final Property yesterdayTarget = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusNumeric(0,BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  public BStatusNumeric getYesterdayTarget() { return (BStatusNumeric)get(yesterdayTarget);}
  public void setYesterdayTarget(BStatusNumeric v) {set(yesterdayTarget,v);}
  
  public static final Property yesterdayPercentageOfTarget = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusNumeric(0,BStatus.ok), BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public BStatusNumeric getYesterdayPercentageOfTarget() { return (BStatusNumeric)get(yesterdayPercentageOfTarget);}
  public void setYesterdayPercentageOfTarget(BStatusNumeric v) {set(yesterdayPercentageOfTarget,v);}
  
  
  /**The total number of good parts processed.*/
  public static final Property totalGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good parts processed.*/
  public int getTotalGoodParts() { return getInt(totalGoodParts);}
  /**The total number of good parts processed.*/
  public void setTotalGoodParts(int v) {setInt(totalGoodParts,v);}
  
  /**The total number of bad parts processed.*/
  public static final Property totalBadParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of bad parts processed.*/
  public int getTotalBadParts() { return getInt(totalBadParts);}
  /**The total number of bad parts processed.*/
  public void setTotalBadParts(int v) {setInt(totalBadParts,v);}
  
  /**The total number of good and bad parts processed.*/
  public static final Property totalParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good and bad parts processed.*/
  public int getTotalParts() { return getInt(totalParts);}
  /**The total number of good and bad parts processed.*/
  public void setTotalParts(int v) {setInt(totalParts,v);}
  
  public static final Property totalPercentageGoodParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getTotalPercentageGoodParts() { return getDouble(totalPercentageGoodParts);}
  public void setTotalPercentageGoodParts(double v) {setDouble(totalPercentageGoodParts,v);}
  
  public static final Property totalPercentageBadParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0.0, BFacets.make(BFacets.SHOW_UNITS, BBoolean.TRUE, BFacets.UNITS, BUnit.getUnit("percent"), BFacets.PRECISION, BInteger.make(1)));
  public double getTotalPercentageBadParts() { return getDouble(totalPercentageBadParts);}
  public void setTotalPercentageBadParts(double v) {setDouble(totalPercentageBadParts,v);}
  
  /**The average number of parts processed per hour (excluding breaks).*/
  public static final Property totalAvgPartsPerHour = newProperty(Flags.SUMMARY|Flags.READONLY, 0, BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  /**The average number of parts processed per hour (excluding breaks).*/
  public double getTotalAvgPartsPerHour() { return getDouble(totalAvgPartsPerHour);}
  /**The average number of parts processed per hour (excluding breaks).*/
  public void setTotalAvgPartsPerHour(double v) {setDouble(totalAvgPartsPerHour,v);}
  
  /**The average cycle time (excluding breaks).*/
  public static final Property totalAvgCycleTime = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The average cycle time (excluding breaks).*/
  public BRelTime getTotalAvgCycleTime() { return (BRelTime)get(totalAvgCycleTime);}
  /**The average cycle time (excluding breaks).*/
  public void setTotalAvgCycleTime(BRelTime v) {set(totalAvgCycleTime,v);}
  
  /**The total hours run (excluding breaks and off-shift).*/
  public static final Property totalHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total hours run (excluding breaks and off-shift).*/
  public BRelTime getTotalHours() { return (BRelTime)get(totalHours);}
  /**The total hours run (excluding breaks and off-shift).*/
  public void setTotalHours(BRelTime v) {set(totalHours,v);}
  
  /**The total time on break.*/
  public static final Property totalBreakHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total time on break.*/
  public BRelTime getTotalBreakHours() { return (BRelTime)get(totalBreakHours);}
  /**The total time on break.*/
  public void setTotalBreakHours(BRelTime v) {set(totalBreakHours,v);}
  
  /**The total number of breaks taken.*/
  public static final Property totalBreaks = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of breaks taken.*/
  public int getTotalBreaks() { return getInt(totalBreaks);}
  /**The total number of breaks taken.*/
  public void setTotalBreaks(int v) {setInt(totalBreaks,v);}
  
  /**The total hours a shift has not run (currentShfit = 0).*/
  public static final Property totalOffShiftHours = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The total hours a shift has not run (currentShfit = 0).*/
  public BRelTime getTotalOffShiftHours () { return (BRelTime)get(totalOffShiftHours );}
  /**The total hours a shift has not run (currentShfit = 0).*/
  public void setTotalOffShiftHours (BRelTime v) {set(totalOffShiftHours ,v);}

  public static BInteger lastAdjustmentForGoodParts = BInteger.make(0);
  public static final Action adjustGoodCountBy = newAction(Flags.OPERATOR, lastAdjustmentForGoodParts);
  public BInteger adjustGoodCountBy(BInteger v) {return (BInteger)invoke(adjustGoodCountBy,v,null);} 
  public BInteger doAdjustGoodCountBy(BInteger v)
  {
    lastAdjustmentForGoodParts = v;
    adjustGoodParts(lastAdjustmentForGoodParts.getInt());
    return lastAdjustmentForGoodParts;
  }
  
  public static BInteger lastAdjustmentForBadParts = BInteger.make(0);
  public static final Action adjustBadCountBy = newAction(Flags.OPERATOR, lastAdjustmentForBadParts);
  public BInteger adjustBadCountBy(BInteger v) {return (BInteger)invoke(adjustBadCountBy,v,null);} 
  public BInteger doAdjustBadCountBy(BInteger v)
  {
    lastAdjustmentForBadParts = v;
    adjustBadParts(lastAdjustmentForBadParts.getInt());
    return lastAdjustmentForBadParts;
  }
  
  /**Moves today's values to yesterday and resets today's values to 0.*/
  public static final Action moveTodaysPartCountsToYesterday = newAction(Flags.HIDDEN,null);
  /**Moves today's values to yesterday and resets today's values to 0.*/
  public void moveTodaysPartCountsToYesterday() { invoke(moveTodaysPartCountsToYesterday,null,null);}

  /**Resets the current shift values to 0*/
  public static final Action resetCurrentShiftPartCounts = newAction(Flags.OPERATOR,null);
  /**Resets the current shift values to 0*/
  public void resetCurrentShiftPartCounts() { invoke(resetCurrentShiftPartCounts,null,null);}

  /**Resets all shift values to 0 (except the current shift)*/
  public static final Action resetShiftPartCounts = newAction(Flags.OPERATOR,null);
  /**Resets all shift values to 0 (except the current shift)*/
  public void resetShiftPartCounts() { invoke(resetShiftPartCounts,null,null);}

  /**Resets today's part counts to 0*/
  public static final Action resetTodaysPartCounts = newAction(Flags.OPERATOR,null);
  /**Resets today's part counts to 0*/
  public void resetTodaysPartCounts() { invoke(resetTodaysPartCounts,null,null);}

  /**Resets yesterday's part counts to 0*/
  public static final Action resetYesterdaysPartCounts = newAction(Flags.OPERATOR,null);
  /**Resets yesterday's part counts to 0*/
  public void resetYesterdaysPartCounts() { invoke(resetYesterdaysPartCounts,null,null);}

  /**Resets the total part counts to 0*/
  public static final Action resetTotalPartCounts = newAction(Flags.OPERATOR,null);
  /**Resets the total part counts to 0*/
  public void resetTotalPartCounts() { invoke(resetTotalPartCounts,null,null);}
  
  /**Resets all values to 0*/
  public static final Action resetAll = newAction(Flags.OPERATOR,null);
  /**Resets all values to 0*/
  public void resetAll() { invoke(resetAll,null,null);}
  
  /**Checks the schedule links and recreates if needed. This is run after copying the object to ensure it is still linked properly.*/
  public static final Action setScheduleLinks = newAction(Flags.OPERATOR,null);
  /**Checks the schedule links and recreates if needed. This is run after copying the object to ensure it is still linked properly.*/
  public void setScheduleLinks() { invoke(setScheduleLinks,null,null);}
  
  /**Refresh ONLY "currentShiftElapsedTime", "currentShiftRemainingTime", all "AvgPartsPerHour" outputs, and all "AvgCycleTime" outputs.  Automatically executed at the rate specified in "inRefreshInterval"*/
  public static final Action refreshValuesForToday = newAction(Flags.OPERATOR,null);
  /**Refresh ONLY "currentShiftElapsedTime", "currentShiftRemainingTime", all "AvgPartsPerHour" outputs, and all "AvgCycleTime" outputs.  Automatically executed at the rate specified in "inRefreshInterval"*/
  public void refreshValuesForToday() { invoke(refreshValuesForToday,null,null);}
  
  /**Get the icon.*/
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
    
  public static final Type TYPE = Sys.loadType(BProductionCounter.class);
  public Type getType() { return TYPE; }
  
  /** */
  private BAbsTime breakStartTime;
  /**Timer used to reset the daily values at midnight each night*/
  Clock.Ticket midnightTimer;
  /**Timer used for refreshing "currentShiftElapsedTime", "currentShiftRemainingTime", "currentShiftAvgPartsPerHour", and "currentShiftAvgCycleTime"*/
  Clock.Ticket refreshTimer;
  private boolean secondAttemptAtWritingPreviousShiftData;
  boolean shiftTargetValuesLinkActive = false;
  
  //private int possibleOffShiftBreaks = 0;
  //private long possibleOffShiftBreakHours = 0;
  
  private final BComponent mySelf() {return this;}
  
  /**started override.  Checks links, sets outputs, and sets midnight timer.*/
  public void started() throws Exception
  {
    if(!Sys.atSteadyState() || !mySelf().isRunning()) return;
    
    //At this point, the object is either new or just copied
    doResetCurrentShiftPartCounts(roundToNearestSecond());
    doSetScheduleLinks();
    startupRoutine();
  }
  
  public void stopped()
  {
    if (refreshTimer != null) refreshTimer.cancel();
    if(midnightTimer != null) midnightTimer.cancel();
  }
  
  /**atSteadyState override.  Sets outputs and sets midnight timer*/
  public void atSteadyState() throws Exception
  {
    if(!isRunning()) return;
    
    //At this point, the system has just restarted
    shiftSlots(getInNumberOfShifts(), null);
    startupRoutine();
  }
  
  private void startupRoutine()
  {
    //Shift the counts around as needed 
    checkShift(roundToNearestSecond());
    
    if(getScheduledBreak().getValue() != getInScheduledBreak().getValue()) getScheduledBreak().setValue(getInScheduledBreak().getValue());
    if(getScheduledProduction().getValue() != getInCurrentShift().getValue() >= 1 && !getInScheduledBreak().getValue()) getScheduledProduction().setValue(getInCurrentShift().getValue() >= 1 && !getInScheduledBreak().getValue());
    
    if(getTotalBreakHours().getMillis() >= getTotalHours().getMillis())
    {
      logger.message(mySelf().getSlotPath().toString() + " Bad values detected (created by an older version of this object).  Attempting to salvage data.");
      
      if(
          getYesterdayTotalHours().getSeconds() <= 0 ||
          getTotalHours().getSeconds() <= 0 ||
          getYesterdayTotalBreaks() <= 0
        )
        setTotalBreaks(0);
      else
      {
        //Find the number of actual shifts, and multiply that by the number of breaks that were last recorded
        setTotalBreaks((getTotalHours().getSeconds() / getYesterdayTotalHours().getSeconds()) * getYesterdayTotalBreaks());
      }
      
      if(
          getTotalBreaks() <= 0 ||
          getYesterdayTotalBreakHours().getMillis() <= 0 ||
          getYesterdayTotalBreaks() <= 0
        )
        setTotalBreakHours(BRelTime.make(0));
      else
      {
        //Find the average amount of time per break and multiply it by the number of breaks we just swagged. 
        setTotalBreakHours(roundToNearestSecond(getTotalBreaks() * (getYesterdayTotalBreakHours().getMillis() / getYesterdayTotalBreaks())));
      }
    }
    
    if(getCurrentShiftAvgPartsPerHour() < 0)              {setCurrentShiftAvgPartsPerHour(0);                 logger.message(mySelf().getSlotPath().toString() + " Negative value for: CurrentShiftAvgPartsPerHour");}
    if(getCurrentShiftAvgCycleTime().getMillis() < 0)     {setCurrentShiftAvgCycleTime(BRelTime.make(0));     logger.message(mySelf().getSlotPath().toString() + " Negative value for: CurrentShiftAvgCycleTime");}
    if(getCurrentShiftTotalBreakHours().getMillis() < 0)  {setCurrentShiftTotalBreakHours(BRelTime.make(0));  logger.message(mySelf().getSlotPath().toString() + " Negative value for: CurrentShiftTotalBreakHours");}
    if(getCurrentShiftAvgPartsPerHour() < 0)              {setCurrentShiftAvgPartsPerHour(0);                 logger.message(mySelf().getSlotPath().toString() + " Negative value for: CurrentShiftAvgPartsPerHour");}
    if(getCurrentShiftAvgCycleTime().getMillis() < 0)     {setCurrentShiftAvgCycleTime(BRelTime.make(0));     logger.message(mySelf().getSlotPath().toString() + " Negative value for: CurrentShiftAvgCycleTime");}
    if(getCurrentShiftTotalBreakHours().getMillis() < 0)  {setCurrentShiftTotalBreakHours(BRelTime.make(0));  logger.message(mySelf().getSlotPath().toString() + " Negative value for: CurrentShiftTotalBreakHours");}
    if(getTodayAvgPartsPerHour() < 0)                     {setTodayAvgPartsPerHour(0);                        logger.message(mySelf().getSlotPath().toString() + " Negative value for: TodayAvgPartsPerHour");}
    if(getTodayAvgCycleTime().getMillis() < 0)            {setTodayAvgCycleTime(BRelTime.make(0));            logger.message(mySelf().getSlotPath().toString() + " Negative value for: TodayAvgCycleTime");}
    if(getTodayTotalBreakHours().getMillis() < 0)         {setTodayTotalBreakHours(BRelTime.make(0));         logger.message(mySelf().getSlotPath().toString() + " Negative value for: TodayTotalBreakHours");}
    if(getTodayTotalHours().getMillis() < 0)              {setTodayTotalHours(BRelTime.make(0));              logger.message(mySelf().getSlotPath().toString() + " Negative value for: TodayTotalHours");}
    if(getTodayTotalOffShiftHours().getMillis() < 0)      {setTodayTotalOffShiftHours(BRelTime.make(0));      logger.message(mySelf().getSlotPath().toString() + " Negative value for: TodayTotalOffShiftHours");}
    if(getYesterdayAvgPartsPerHour() < 0)                 {setYesterdayAvgPartsPerHour(0);                    logger.message(mySelf().getSlotPath().toString() + " Negative value for: YesterdayAvgPartsPerHour");}
    if(getYesterdayAvgCycleTime().getMillis() < 0)        {setYesterdayAvgCycleTime(BRelTime.make(0));        logger.message(mySelf().getSlotPath().toString() + " Negative value for: YesterdayAvgCycleTime");}
    if(getYesterdayTotalBreakHours().getMillis() < 0)     {setYesterdayTotalBreakHours(BRelTime.make(0));     logger.message(mySelf().getSlotPath().toString() + " Negative value for: YesterdayTotalBreakHours");}
    if(getYesterdayTotalHours().getMillis() < 0)          {setYesterdayTotalHours(BRelTime.make(0));          logger.message(mySelf().getSlotPath().toString() + " Negative value for: YesterdayTotalHours");}
    if(getYesterdayTotalOffShiftHours().getMillis() < 0)  {setYesterdayTotalOffShiftHours(BRelTime.make(0));  logger.message(mySelf().getSlotPath().toString() + " Negative value for: YesterdayTotalOffShiftHours");}
    if(getTotalAvgPartsPerHour() < 0)                     {setTotalAvgPartsPerHour(0);                        logger.message(mySelf().getSlotPath().toString() + " Negative value for: TotalAvgPartsPerHour");}
    if(getTotalAvgCycleTime().getMillis() < 0)            {setTotalAvgCycleTime(BRelTime.make(0));            logger.message(mySelf().getSlotPath().toString() + " Negative value for: TotalAvgCycleTime");}
    if(getTotalBreakHours().getMillis() < 0)              {setTotalBreakHours(BRelTime.make(0));              logger.message(mySelf().getSlotPath().toString() + " Negative value for: TotalBreakHours");}
    if(getTotalHours().getMillis() < 0)                   {setTotalHours(BRelTime.make(0));                   logger.message(mySelf().getSlotPath().toString() + " Negative value for: TotalHours");}
    if(getTotalOffShiftHours().getMillis() < 0)           {setTotalOffShiftHours(BRelTime.make(0));           logger.message(mySelf().getSlotPath().toString() + " Negative value for: TotalOffShiftHours");}
    if(getYesterdayTotalBreaks() < 0)                     {setYesterdayTotalBreaks(0);                        logger.message(mySelf().getSlotPath().toString() + " Negative value for: YesterdayTotalBreaks");}
    
    
    
    shiftTargetValuesLinkActive = getInTargetData().length() > 0;
    
    scheduleMidnightTimer();
    doRefreshValuesForToday();
    updateTimer();
    updateDurration();
    refreshTargetData();
  }
  
  /**Checks to see what changed and sets outputs accordingly.*/
  public void changed(Property p, Context cx)
  {
    if(!Sys.atSteadyState() || !mySelf().isRunning()) return;
    final BAbsTime absTimeThisChanged = roundToNearestSecond();
    
    
    super.changed(p, cx);
    if(p.equals(inScheduledBreak) && (!getIgnoreShiftsWithoutAnyTransactions() || getCurrentShiftTotalParts() > 0))
    {
      if(getInScheduledBreak().getValue() && getInCurrentShift().getValue() >= 1 && getInScheduledBreak().getStatus().isValid() && !getScheduledBreak().getValue())
      {
        //if(getIgnoreShiftsWithoutAnyTransactions() && getCurrentShiftTotalParts() == 0) possibleOffShiftBreaks++;
        breakStartTime = absTimeThisChanged;
        setCurrentShiftTotalBreaks(getCurrentShiftTotalBreaks() + 1);
        setTodayTotalBreaks(getTodayTotalBreaks() + 1);
        setTotalBreaks(getTotalBreaks() + 1);
      }
      else if(breakStartTime != null)
      {
        BAbsTime lastMidnight = absTimeThisChanged.timeOfDay(0, 0, 0, 0);
        if(breakStartTime.isAfter(lastMidnight))
        {
          long breakTimeDelta = breakStartTime.delta(absTimeThisChanged).getMillis();
          setTodayTotalBreakHours(roundToNearestSecond(getTodayTotalBreakHours().getMillis() + breakTimeDelta));
          //if(getIgnoreShiftsWithoutAnyTransactions() && getCurrentShiftTotalParts() == 0) possibleOffShiftBreakHours = possibleOffShiftBreakHours + breakTimeDelta;
        }
        else
        {
          long breakTimeDelta = lastMidnight.delta(absTimeThisChanged).getMillis();
          setTodayTotalBreakHours(roundToNearestSecond(breakTimeDelta));
          //if(getIgnoreShiftsWithoutAnyTransactions() && getCurrentShiftTotalParts() == 0) possibleOffShiftBreakHours = breakTimeDelta;
        }
        
        setCurrentShiftTotalBreakHours(roundToNearestSecond(getCurrentShiftTotalBreakHours().getMillis() + breakStartTime.delta(absTimeThisChanged).getMillis()));
        setTotalBreakHours(roundToNearestSecond(getTotalBreakHours().getMillis() + breakStartTime.delta(absTimeThisChanged).getMillis()));
        breakStartTime = null;
        updateDurration();
      }
      if(!getInScheduledBreak().getValue()) breakStartTime = null;
      getScheduledBreak().setValue(getInScheduledBreak().getValue());
      getScheduledBreak().setStatus(getInScheduledBreak().getStatus());
    }
    
    if(p.equals(inShiftScheduleOrd) || p.equals(inBreakScheduleOrd) || p.equals(inShiftTargetsOrd) || p.equals(inShiftToResetCountsOnOrd)) doSetScheduleLinks();
    
    if(getInNumberOfShifts() < (int)getInCurrentShift().getValue() || p.equals(inNumberOfShifts)) shiftSlots(getInNumberOfShifts(), cx);
    if(getInNumberOfShifts() < (int)getInCurrentShift().getValue()) setInNumberOfShifts((int)getInCurrentShift().getValue());
    
      if(!getOnlyTrackPartsDurringValidShift() || getInCurrentShift().getValue() >=1)
      {
      if(p.equals(inGoodTransaction) && getInGoodTransaction()) adjustGoodParts(absTimeThisChanged, 1);
      if(p.equals(inBadTransaction) && getInBadTransaction()) adjustBadParts(absTimeThisChanged, 1);
    }
    
    if(p.equals(determinePartsPerHourAndCycleTimeBasedOn))
    {
      if((getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 0 && getCurrentShiftTotalParts() > 0) 
      || (getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 1 && getCurrentShiftGoodParts() > 0))
      {
        refresh(absTimeThisChanged);
      }
    }
    
    if(p == inCurrentShift) 
    {
      checkShift(absTimeThisChanged);
    }
    
    if(p.equals(inScheduledBreak) || p == inCurrentShift)
    {
      //set the scheduled production output based on the linked schedules
      getScheduledProduction().setValue(getInCurrentShift().getValue() >= 1 && !getInScheduledBreak().getValue() && getInCurrentShift().getStatus().isValid() && getInScheduledBreak().getStatus().isValid());
      
      if(!getInScheduledBreak().getStatus().isValid()) getScheduledProduction().setStatus(getInScheduledBreak().getStatus());
      else if (!getInCurrentShift().getStatus().isValid()) getScheduledProduction().setStatus(getInCurrentShift().getStatus());
      else getScheduledProduction().setStatus(0);
    }
    
    if(p.equals(inShiftEndTime))
    {
      refresh(absTimeThisChanged);
      setCurrentShiftEndTime(getInShiftEndTime());
      updateDurration();
    }
    
    if(p.equals(inRefreshInterval))
    {
      updateTimer();
      refresh(absTimeThisChanged);
    }
    
    if(p.equals(inTargetData) || (getInTargetData().length() == 0 && p.getName().endsWith("_Target")))
    {
      shiftTargetValuesLinkActive = getInTargetData().length() > 0;
      refreshTargetData();
    }
    
    if(p.equals(shiftToResetCountsOn) && getShiftToResetCountsOn().getStatus().isValid())
    {
      int shiftNum = (int) getShiftToResetCountsOn().getValue();
      if(shiftNum >= 0 && shiftNum <= 60) setInShiftToResetCountsOn(shiftNum);
      else setInShiftToResetCountsOn(-1);
    }
  }
  
  private void adjustGoodParts(int partCount)
  {
    adjustGoodParts(BAbsTime.now(), partCount);
  }
  
  private void adjustBadParts(int partCount)
  {
    adjustBadParts(BAbsTime.now(), partCount);
  }
  
  private void adjustGoodParts(BAbsTime absTimeThisChanged, int partCount)
  {
    if(getInCurrentShift().getValue() >=1 )
    {
      setCurrentShiftGoodParts(Math.max(0, getCurrentShiftGoodParts() + partCount));
      setCurrentShiftTotalParts(Math.max(0, getCurrentShiftTotalParts() + partCount));
    }
    setTodayGoodParts(Math.max(0, getTodayGoodParts() + partCount));
    setTodayTotalParts(Math.max(0, getTodayTotalParts() + partCount));
    setTotalGoodParts(Math.max(0, getTotalGoodParts() + partCount));
    setTotalParts(Math.max(0, getTotalParts() + partCount));
    
    refresh(absTimeThisChanged);
  }
  
  private void adjustBadParts(BAbsTime absTimeThisChanged, int partCount)
  {
    if(getInCurrentShift().getValue() >=1 )
    {
      setCurrentShiftBadParts(Math.max(0, getCurrentShiftBadParts() + partCount));
      setCurrentShiftTotalParts(Math.max(0, getCurrentShiftTotalParts() + partCount));
    }
    setTodayBadParts(Math.max(0, getTodayBadParts() + partCount));
    setTodayTotalParts(Math.max(0, getTodayTotalParts() + partCount));
    setTotalBadParts(Math.max(0, getTotalBadParts() + partCount));
    setTotalParts(Math.max(0, getTotalParts() + partCount));
    
    refresh(absTimeThisChanged);
  }
  
  private void checkShift(BAbsTime absTimeThisChanged)
  {
    if(getInCurrentShift().getStatus().isValid() && !getCurrentShift().getStatus().isValid()) getCurrentShift().setStatus(0);
    secondAttemptAtWritingPreviousShiftData = false;
    if(
        getInCurrentShift().getStatus().isValid() && 
        getInCurrentShift().getValue() != getCurrentShift().getValue()) 
      shiftChange(absTimeThisChanged);
    else if(getInCurrentShift().getValue() < 0) getCurrentShift().setStatusFault(true);
    else if(getCurrentShift().getStatus() != getInCurrentShift().getStatus()) getCurrentShift().setStatus(getInCurrentShift().getStatus());
  }
  
  private void shiftChange(BAbsTime absTimeThisChanged)
  {
    int previousShift = (int)getCurrentShift().getValue();
    if(getInShiftToResetCountsOn() == (int)getInCurrentShift().getValue())
    {
      doResetCurrentShiftPartCounts(absTimeThisChanged);
      doResetShiftPartCounts();
      doResetTodaysPartCounts();
    } 
    
    //check to see if the current shift was just changed FROM 0 to a number HIGHER THAN 0  -OR-  current shift changed && ignore no-production shift && production count = 0
    if((getInCurrentShift().getValue() >= 1 && getCurrentShift().getValue() < 1) || (getIgnoreShiftsWithoutAnyTransactions() && getCurrentShiftTotalParts() == 0))
    {
      //if the shift start time is after today at 0:00:00.000, then add the current shift's hours to today's total off-shift hours 
      if(getCurrentShiftStartTime().isAfter(absTimeThisChanged.timeOfDay(0, 0, 0, 0))) setTodayTotalOffShiftHours(BRelTime.make(getTodayTotalOffShiftHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
      //if the shift start time is before today at 0:00:00.000, then subtract midnight.millis from absTimeThisChanged and add that total to today's total off-shift hours 
      else setTodayTotalOffShiftHours(absTimeThisChanged.timeOfDay(0, 0, 0, 0).delta(absTimeThisChanged));
      //Always add these hours to the total off-shift hours
      setTotalOffShiftHours(BRelTime.make(getTotalOffShiftHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
    }
    else if(getCurrentShift().getValue() > 0)
    {
      refresh(absTimeThisChanged);
      
      try
      {
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_GoodParts"))).setValue((double)getCurrentShiftGoodParts());
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_BadParts"))).setValue((double)getCurrentShiftBadParts());
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_TotalParts"))).setValue((double)getCurrentShiftTotalParts());
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_PercentageGoodParts"))).setValue((double)getCurrentShiftPercentageGoodParts());
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_PercentageBadParts"))).setValue((double)getCurrentShiftPercentageBadParts());
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_AvgPartsPerHour"))).setValue((double)getCurrentShiftAvgPartsPerHour());
        set("shift_"+previousShift+"_AvgCycleTime", getCurrentShiftAvgCycleTime());
        set("shift_"+previousShift+"_Hours", getCurrentShiftElapsedTime());
        set("shift_"+previousShift+"_BreakHours", getCurrentShiftTotalBreakHours());
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_Breaks"))).setValue(getCurrentShiftTotalBreaks());
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_Target"))).setValue(getCurrentShiftTarget().getValue());
        ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_PercentageOfTarget"))).setValue(getCurrentShiftPercentageOfTarget().getValue());
        
      }
      catch (Exception e)
      {
        if(secondAttemptAtWritingPreviousShiftData)
        {
          logger.error(mySelf().getSlotPath().toString() + " - Could not update one or more slots for shift number " + previousShift + ", after attempting to recreate the needed slots.  2 attempts were made to write to the slot(s).");
          logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
          if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
        }
        else
        {
          logger.trace(mySelf().getSlotPath().toString() + " - Could not update one or more slots for shift number " + previousShift + ", Attemtping to recreate the slot(s) and try again.");
          refreshSlotsAndRetryShiftChange(absTimeThisChanged);
        }
      }
      
      updateDurration();
      
      //if the shift start time is after today at 0:00:00.000, then add the current shift's hours to today's total shift hours 
      if(getCurrentShiftStartTime().isAfter(absTimeThisChanged.timeOfDay(0, 0, 0, 0))) setTodayTotalHours(BRelTime.make(getTodayTotalHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
      //if the shift start time is before today at 0:00:00.000, then subtract midnight.millis from absTimeThisChanged and add that total to today's total shift hours 
      else setTodayTotalHours(absTimeThisChanged.timeOfDay(0, 0, 0, 0).delta(roundToNearestSecond(absTimeThisChanged)));
      //Always add these hours to the total shift hours
      setTotalHours(BRelTime.make(getTotalHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
    }
    
    doResetCurrentShiftPartCounts(absTimeThisChanged);
    breakStartTime = null;
    if(getScheduledBreak().getValue()) getScheduledBreak().setValue(false);
  }
  
  private void updateDurration()
  {
    if(getInShiftEndTime() == BAbsTime.NULL)
    {
      setCurrentShiftScheduledDuration(BRelTime.make(0));
      return;
    }
    
    boolean useDefaultTime = true;
    //If the current shift is > 0, pull the previous hours & breaks for the shift.
    if(getCurrentShift().getValue() > 0)
    {
      BRelTime previousHours = null;
      BRelTime previousBreaks = null;
      try
      {
        previousHours = ((BRelTime) ((BObject)get("shift_"+getInCurrentShift().getValue()+"_Hours")));
        previousBreaks = ((BRelTime) ((BObject)get("shift_"+getInCurrentShift().getValue()+"_BreakHours")));
      }
      catch (Exception e){}
      
      if(previousHours != null && previousBreaks != null)
      {
        int currentShiftDuration = roundToNearestMinute(getCurrentShiftStartTime().delta(getInShiftEndTime())).getMinutes();
        previousHours = roundToNearestMinute(previousHours);
        previousBreaks = roundToNearestMinute(previousBreaks);
        int timeDifference = currentShiftDuration - previousHours.getMinutes() - previousBreaks.getMinutes();
        
        if(timeDifference < 5 && timeDifference > -5)
        {
          setCurrentShiftScheduledDuration(BRelTime.makeMinutes(currentShiftDuration - previousBreaks.getMinutes()));
          useDefaultTime = false;
        }
      }
    }
    
    //If the current shift is 0, just figure out how long it will be 0.
    if(useDefaultTime) setCurrentShiftScheduledDuration(BRelTime.makeMinutes(roundToNearestMinute(getCurrentShiftStartTime().delta(getInShiftEndTime())).getMinutes() - getCurrentShiftTotalBreakHours().getMinutes()));
  }
  
  private void refreshSlotsAndRetryShiftChange(BAbsTime absTimeThisChanged)
  {
    shiftSlots(getInNumberOfShifts(), null);
    shiftChange(absTimeThisChanged);
  }
  
  private void refreshTargetData()
  {
    double dailyTarget = 0;
    int shiftNumber;
    double tempValue = 0;
    double currentShiftTarget = 0;
    
    String[] test = null;
    if(shiftTargetValuesLinkActive) test = TextUtil.split(getInTargetData(), '\n');
    
    for (int i = 0; i < getInNumberOfShifts(); i++)
    {
      try
      {
        shiftNumber = i + 1;
        
        //If there is any linked target data available, pull the data from there and update the shift slots
        if(shiftTargetValuesLinkActive)
        {
          if(i >= test.length) tempValue = 0;
          else tempValue = Double.parseDouble(test[i]);
          ((BStatusNumeric) ((BObject)get("shift_"+shiftNumber+"_Target"))).setValue(tempValue);
        }
        
        //If there isn't any linked target data, pull the shift target data from the shift slot (data that is manually entered)
        else tempValue = ((BStatusNumeric) ((BObject)get("shift_"+shiftNumber+"_Target"))).getValue();
        
        dailyTarget = dailyTarget + tempValue;
        
        //Update the percentage
        ((BStatusNumeric) ((BObject)get("shift_"+shiftNumber+"_PercentageOfTarget"))).setValue(((((BStatusNumeric) ((BObject)get("shift_"+shiftNumber+"_GoodParts"))).getValue()) / tempValue) * 100);
        if(shiftNumber == getInCurrentShift().getValue()) currentShiftTarget = tempValue;
      }
      catch (Exception e)
      {
        logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
        if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
      }
    }
    
    getCurrentShiftTarget().setValue(currentShiftTarget);
    getTodayTarget().setValue(dailyTarget);
    
    if(getInShiftEndTime() != BAbsTime.NULL && currentShiftTarget > 0)
    {
      setCurrentShiftTargetPartsPerHour(currentShiftTarget / (((double)getCurrentShiftScheduledDuration().getMinutes()) / 60));
      setCurrentShiftTargetCycleTime(BRelTime.make((long) (getCurrentShiftScheduledDuration().getMillis() / currentShiftTarget)));
    }
    else
    {
      setCurrentShiftTargetPartsPerHour(0);
      setCurrentShiftTargetCycleTime(BRelTime.make(0));
    }
    doRefreshValuesForToday();
  }
  
  
  /**Moves today's values to yesterday and resets today's values to 0.*/
  public void doMoveTodaysPartCountsToYesterday()
  {
    if(!Sys.atSteadyState() || !mySelf().isRunning()) return;
    scheduleMidnightTimer();
    final BAbsTime absTimeThisChanged = roundToNearestSecond();
    final BAbsTime lastMidnight = absTimeThisChanged.timeOfDay(0, 0, 0, 0);
    
    doSetScheduleLinks();
    if(!getIgnoreShiftsWithoutAnyTransactions() || getTodayTotalParts() > 0)
    {
      setYesterdayGoodParts(getTodayGoodParts());
      setYesterdayBadParts(getTodayBadParts());
      setYesterdayTotalParts(getTodayTotalParts());
      
      if((getIgnoreShiftsWithoutAnyTransactions() && getCurrentShiftTotalParts() == 0 && getInCurrentShift().getValue() >= 1) || getInCurrentShift().getValue() == 0)
        setYesterdayTotalOffShiftHours(BRelTime.make(getTodayTotalOffShiftHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
      else
      setYesterdayTotalOffShiftHours(getTodayTotalOffShiftHours());
      
      if(breakStartTime == null) setYesterdayTotalBreakHours(getTodayTotalBreakHours());
      else if(breakStartTime.isAfter(lastMidnight)) setYesterdayTotalBreakHours(getTodayTotalBreakHours());
      else setYesterdayTotalBreakHours(roundToNearestSecond(lastMidnight.delta(breakStartTime).getMillis() + getTodayTotalBreakHours().getMillis()));
      
      if(getInCurrentShift().getValue() >= 1) setYesterdayTotalHours(getTodayTotalHours());
      else if(getCurrentShiftStartTime().isAfter(lastMidnight)) setYesterdayTotalHours(getTodayTotalHours());
      else setYesterdayTotalHours(BRelTime.make(BAbsTime.now().timeOfDay(0, 0, 0, 0).getMillis() - roundToNearestSecond(getCurrentShiftStartTime().getMillis() + getTodayTotalHours().getMillis()).getMillis()));
    
    setYesterdayTotalBreaks(getTodayTotalBreaks());
    setYesterdayAvgPartsPerHour(getTodayAvgPartsPerHour());
    setYesterdayAvgCycleTime(getTodayAvgCycleTime());
      getYesterdayTarget().setValue(getTodayTarget().getValue());
      getYesterdayPercentageOfTarget().setValue(getTodayPercentageOfTarget().getValue());
      setYesterdayPercentageGoodParts(getTodayPercentageGoodParts());
      setYesterdayPercentageBadParts(getTodayPercentageBadParts());
    }
    
    setTodayGoodParts(0);
    setTodayBadParts(0);
    setTodayTotalParts(0);
    setTodayAvgPartsPerHour(0);
    setTodayAvgCycleTime(BRelTime.make(0));
    setTodayTotalBreakHours(BRelTime.make(0));
    setTodayTotalHours(BRelTime.make(0));
    setTodayTotalOffShiftHours(BRelTime.make(0));
    setTodayTotalBreaks(0);
    getTodayPercentageOfTarget().setValue(0);
    setTodayPercentageGoodParts(0);
    setTodayPercentageBadParts(0);
    
    refreshTargetData();
  }
  
  /**Resets the current shift values to 0.
   * This also copies the current shift times to today's time slots*/
  public void doResetCurrentShiftPartCounts()
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    BAbsTime absTimeThisChanged = roundToNearestSecond();
    
    if(getInCurrentShift().getValue() >= 1)
    {
      //if the shift start time is after today at 0:00:00.000, then add the current shift's hours to today's total shift hours 
      if(getCurrentShiftStartTime().isAfter(absTimeThisChanged.timeOfDay(0, 0, 0, 0))) setTodayTotalHours(BRelTime.make(getTodayTotalHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
      //if the shift start time is before today at 0:00:00.000, then subtract midnight.millis from absTimeThisChanged and add that total to today's total shift hours 
      else setTodayTotalHours(absTimeThisChanged.timeOfDay(0, 0, 0, 0).delta(absTimeThisChanged));
      //Always add these hours to the total shift hours
      setTotalHours(BRelTime.make(getTotalHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
    }
    else
    {
      //if the shift start time is after today at 0:00:00.000, then add the current shift's hours to today's total off-shift hours 
      if(getCurrentShiftStartTime().isAfter(absTimeThisChanged.timeOfDay(0, 0, 0, 0))) setTodayTotalOffShiftHours(BRelTime.make(getTodayTotalOffShiftHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
      //if the shift start time is before today at 0:00:00.000, then subtract midnight.millis from absTimeThisChanged and add that total to today's total off-shift hours 
      else setTodayTotalOffShiftHours(absTimeThisChanged.timeOfDay(0, 0, 0, 0).delta(absTimeThisChanged));
      //Always add these hours to the total off-shift hours
      setTotalOffShiftHours(BRelTime.make(getTotalOffShiftHours().getMillis() + roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged)).getMillis()));
    }
    
    doResetCurrentShiftPartCounts(absTimeThisChanged);
    refreshTargetData();
  }
  
  /**
   * Resets the current shift values to 0 and sets the shift start time to the BAbsTime input.
   * DOES NOT COPY CURRENT SHIFT TIMES TO TODAY TIMES.
   */
  public void doResetCurrentShiftPartCounts(BAbsTime absTimeThisChanged)
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    
    setCurrentShiftGoodParts(0);
    setCurrentShiftBadParts(0);
    setCurrentShiftTotalParts(0);
    setCurrentShiftPercentageGoodParts(0);
    setCurrentShiftPercentageBadParts(0);
    setCurrentShiftAvgPartsPerHour(0);
    setCurrentShiftAvgCycleTime(BRelTime.make(0));
    setCurrentShiftTotalBreakHours(BRelTime.make(0));
    setCurrentShiftStartTime(absTimeThisChanged);
    setCurrentShiftTotalBreaks(0);
    getCurrentShift().setValue(getInCurrentShift().getValue());
    getCurrentShift().setStatus(0);
    getCurrentShiftPercentageOfTarget().setValue(0);
    refreshTargetData();
  }
  
  /**Resets all shift values to 0 (except the current shift)*/
  public void doResetShiftPartCounts()
  {
    if(!Sys.atSteadyState() || !mySelf().isRunning()) return;
    try
    {
      for(int i=1; i<getInNumberOfShifts()+1; i++)
      { 
        ((BStatusNumeric) ((BObject)get("shift_"+i+"_GoodParts"))).setValue(0);
        ((BStatusNumeric) ((BObject)get("shift_"+i+"_BadParts"))).setValue(0);
        ((BStatusNumeric) ((BObject)get("shift_"+i+"_AvgPartsPerHour"))).setValue(0);
        set("shift_"+i+"_AvgCycleTime",BRelTime.make(0));
        ((BStatusNumeric) ((BObject)get("shift_"+i+"_TotalParts"))).setValue(0);
        set("shift_"+i+"_Hours",BRelTime.make(0));
        set("shift_"+i+"_BreakHours",BRelTime.make(0));
        ((BStatusNumeric) ((BObject)get("shift_"+i+"_Breaks"))).setValue(0);
        ((BStatusNumeric) ((BObject)get("shift_"+i+"_PercentageOfTarget"))).setValue(0);
        ((BStatusNumeric) ((BObject)get("shift_"+i+"_PercentageGoodParts"))).setValue(0);
        ((BStatusNumeric) ((BObject)get("shift_"+i+"_PercentageBadParts"))).setValue(0);
      }
    }
    catch (Exception e)
    {
      logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
      if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
    }
    refreshTargetData();
  }
  
  /**Resets today's part counts to 0*/
  public void doResetTodaysPartCounts()
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    doResetCurrentShiftPartCounts(roundToNearestSecond());
    setTodayGoodParts(0);
    setTodayBadParts(0);
    setTodayTotalParts(0);
    setTodayPercentageGoodParts(0);
    setTodayPercentageBadParts(0);
    setTodayAvgPartsPerHour(0);
    setTodayAvgCycleTime(BRelTime.make(0));
    setTodayTotalBreakHours(BRelTime.make(0));
    setTodayTotalHours(BRelTime.make(0));
    setTodayTotalOffShiftHours(BRelTime.make(0));
    setTodayTotalBreaks(0);
    getTodayPercentageOfTarget().setValue(0);
    refreshTargetData();
  }
  
  /**Resets yesterday's part counts to 0*/
  public void doResetYesterdaysPartCounts()
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    setYesterdayGoodParts(0);
    setYesterdayBadParts(0);
    setYesterdayTotalParts(0);
    setYesterdayPercentageGoodParts(0);
    setYesterdayPercentageBadParts(0);
    setYesterdayAvgPartsPerHour(0);
    setYesterdayAvgCycleTime(BRelTime.make(0));
    setYesterdayTotalBreakHours(BRelTime.make(0));
    setYesterdayTotalHours(BRelTime.make(0));
    setYesterdayTotalOffShiftHours(BRelTime.make(0));
    setYesterdayTotalBreaks(0);
    getYesterdayTarget().setValue(0);
    getYesterdayPercentageOfTarget().setValue(0);
  }
  
  /**Resets the total part counts to 0*/
  public void doResetTotalPartCounts()
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    setTotalGoodParts(0);
    setTotalBadParts(0);
    setTotalParts(0);
    setTotalPercentageGoodParts(0);
    setTotalPercentageBadParts(0);
    setTotalAvgPartsPerHour(0);
    setTotalAvgCycleTime(BRelTime.make(0));
    setTotalBreakHours(BRelTime.make(0));
    setTotalHours(BRelTime.make(0));
    setTotalOffShiftHours(BRelTime.make(0));
    setTotalBreaks(0);
  }
  
  /**Resets all values to 0*/
  public void doResetAll()
  {
    if(!Sys.atSteadyState() || !mySelf().isRunning()) return;
    doResetCurrentShiftPartCounts();
    doResetShiftPartCounts();
    doResetTotalPartCounts();
    doResetTodaysPartCounts();
    doResetYesterdaysPartCounts();
    scheduleMidnightTimer();
    refreshTargetData();
  }
  
  /**
   *Checks the schedule links and recreates if needed. This is run after copying the object to ensure it is still linked properly.
   * The linking logic was borrowed && modified from axCommunity's BDynamicLinkNumeric, written by Mike Arnott, Kors Engineering.
   */
  public void doSetScheduleLinks()
  {
    if(linkSlots(getInShiftScheduleOrd(),        "out",      inCurrentShift,       "ShiftScheduleLink"))
      getCurrentShift().setStatusNull(false);
      else
        getCurrentShift().setStatusNull(true);
    
    if(!linkSlots(getInShiftScheduleOrd(),       "nextTime", inShiftEndTime,       "ShiftEndScheduleLink"))
      setInShiftEndTime(BAbsTime.NULL);
    
    if(linkSlots(getInBreakScheduleOrd(),        "out",      inScheduledBreak,     "BreakScheduleLink"))
      getScheduledBreak().setStatusNull(false);
    else
      getScheduledBreak().setStatusNull(true);
    if(linkSlots(getInShiftToResetCountsOnOrd(), "out",      shiftToResetCountsOn, "ShiftToResetCountsLink"))
      addSlotFlag(inShiftToResetCountsOn, Flags.READONLY);
      else
      removeSlotFlag(inShiftToResetCountsOn, Flags.READONLY);
    
    if(linkSlots(getInShiftTargetsOrd(),         "data",     inTargetData,         "TargetsLink"))
    {
      if(!shiftTargetValuesLinkActive)
      {
        shiftTargetValuesLinkActive = true;
        refreshTargetData();
      }
      
      //Make the slots read only
      for(int i = 1; i <= getInNumberOfShifts(); i++) addSlotFlag("shift_" + i + "_Target", Flags.READONLY);
      }
      else
      {
      if(shiftTargetValuesLinkActive)
      {
        shiftTargetValuesLinkActive = false;
        refreshTargetData();
      }
      
      //Make the slots read/write
      for(int i = 1; i <= getInNumberOfShifts(); i++) removeSlotFlag("shift_" + i + "_Target", Flags.READONLY);
    }
    
    updateDurration();
    getScheduledProduction().setStatusNull(getCurrentShift().getStatus().isNull() || getScheduledBreak().getStatus().isNull());
  }
  
  void updateTimer()
  {
    if (refreshTimer != null) refreshTimer.cancel();
    refreshTimer = Clock.schedulePeriodically(mySelf(), getInRefreshInterval(), refreshValuesForToday, null);
  }
  
  /**Executes the private void "refresh", which ONLY refreshes "currentShiftElapsedTime", "currentShiftRemainingTime", all "AvgPartsPerHour" outputs, and all "AvgCycleTime" outputs.  Automatically executed at the rate specified in "inRefreshInterval"*/
  public void doRefreshValuesForToday()
  {
    refresh(BAbsTime.now());
  }
  
  /**Refresh ONLY "currentShiftElapsedTime", "currentShiftRemainingTime", all "AvgPartsPerHour" outputs, and all "AvgCycleTime" outputs.*/
  private void refresh(BAbsTime absTimeThisChanged)
  {
    setCurrentShiftElapsedTime(roundToNearestSecond(getCurrentShiftStartTime().delta(absTimeThisChanged).getMillis()));
    
    if(getInShiftEndTime() == BAbsTime.NULL)
      setCurrentShiftRemainingTime(BRelTime.make(0));
    else
    setCurrentShiftRemainingTime(absTimeThisChanged.delta(getInShiftEndTime()));
    
    double mSeconds;
    double hours;
    
    if(getCurrentShift().getValue() > 0)
    {
      mSeconds = absTimeThisChanged.getMillis();
      mSeconds = mSeconds - getCurrentShiftStartTime().getMillis();
      mSeconds = mSeconds - getCurrentShiftTotalBreakHours().getMillis();
      hours = mSeconds / 1000;
      hours = hours / 60;
      hours = hours / 60;
      if(hours == 0) hours = 0.0001;
      
      if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 0 && getCurrentShiftTotalParts() > 0)
      {
        setCurrentShiftAvgPartsPerHour(((double) getCurrentShiftTotalParts()) / hours);
        setCurrentShiftAvgCycleTime(BRelTime.make((long) (mSeconds / getCurrentShiftTotalParts())));
        getCurrentShiftPercentageOfTarget().setValue((((double) getCurrentShiftTotalParts()) / getCurrentShiftTarget().getValue()) * 100.0);
      }
      else if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 1 && getCurrentShiftGoodParts() > 0)
      {
        setCurrentShiftAvgPartsPerHour(((double) getCurrentShiftGoodParts()) / hours);
        setCurrentShiftAvgCycleTime(BRelTime.make((long) (mSeconds / getCurrentShiftGoodParts())));
        getCurrentShiftPercentageOfTarget().setValue((((double) getCurrentShiftGoodParts()) / getCurrentShiftTarget().getValue()) * 100.0);
      }
      else
      {
        setCurrentShiftAvgPartsPerHour(0);
        setCurrentShiftAvgCycleTime(BRelTime.make(0));
      }
    }
    setCurrentShiftPercentageOfTargetPph((getCurrentShiftAvgPartsPerHour() / getCurrentShiftTargetPartsPerHour()) * 100);
    setCurrentShiftPercentageOfTargetCycleTime((((double)getCurrentShiftTargetCycleTime().getMillis()) / ((double) getCurrentShiftAvgCycleTime().getMillis())) * 100.0);
    
    mSeconds = getTodayTotalHours().getMillis() + absTimeThisChanged.getMillis();
    mSeconds = mSeconds - getCurrentShiftStartTime().getMillis();
    mSeconds = mSeconds - getTodayTotalBreakHours().getMillis();
    hours = mSeconds / 1000;
    hours = hours / 60;
    hours = hours / 60;
    if(hours == 0) hours = 0.0001;
    
    if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 0 && getTodayTotalParts() > 0)
    {
      setTodayAvgPartsPerHour(((double) getTodayTotalParts()) / hours);
      setTodayAvgCycleTime(BRelTime.make((long) (mSeconds / getTodayTotalParts())));
      getTodayPercentageOfTarget().setValue(((double) getTodayTotalParts() / getTodayTarget().getValue()) * 100);
    }
    else if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 1 && getTodayGoodParts() > 0)
    {
      setTodayAvgPartsPerHour(((double) getTodayGoodParts()) / hours);
      setTodayAvgCycleTime(BRelTime.make((long) (mSeconds / getTodayGoodParts())));
      getTodayPercentageOfTarget().setValue(((double) getTodayGoodParts() / getTodayTarget().getValue()) * 100);
    }
    else
    {
      setTodayAvgPartsPerHour(0);
      setTodayAvgCycleTime(BRelTime.make(0));
    }

    mSeconds = getTotalHours().getMillis() + absTimeThisChanged.getMillis();
    mSeconds = mSeconds - getCurrentShiftStartTime().getMillis();
    mSeconds = mSeconds - getTotalBreakHours().getMillis();
    hours = mSeconds / 1000;
    hours = hours / 60;
    hours = hours / 60;
    if(hours == 0) hours = 0.0001;
    
    if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 0 && getTotalParts() > 0)
    {
      setTotalAvgPartsPerHour(((double) getTotalParts()) / hours);
      setTotalAvgCycleTime(BRelTime.make((long) (mSeconds / getTotalParts())));
    }
    else if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 1 && getTotalGoodParts() > 0)
    {
      setTotalAvgPartsPerHour(((double) getTotalGoodParts()) / hours);
      setTotalAvgCycleTime(BRelTime.make((long) (mSeconds / getTotalGoodParts())));
    }
    else
    {
      setTotalAvgPartsPerHour(0);
      setTotalAvgCycleTime(BRelTime.make(0));
    }
    
    setCurrentShiftPercentageGoodParts(((double) getCurrentShiftGoodParts() / (double) getCurrentShiftTotalParts()) * 100);
    setCurrentShiftPercentageBadParts(((double) getCurrentShiftBadParts() / (double) getCurrentShiftTotalParts()) * 100);
    setTodayPercentageGoodParts(((double) getTodayGoodParts() / (double) getTodayTotalParts()) * 100);
    setTodayPercentageBadParts(((double) getTodayBadParts() / (double) getTodayTotalParts()) * 100);
    setTotalPercentageGoodParts(((double) getTotalGoodParts() / (double) getTotalParts()) * 100);
    setTotalPercentageBadParts(((double) getTotalBadParts() / (double) getTotalParts()) * 100);
  }
  
  /**
   *Checks the schedule links and recreates if needed. This is run after copying the object to ensure it is still linked properly.
   * The linking logic was borrowed && modified from axCommunity's BDynamicLinkNumeric, written by Mike Arnott, Kors Engineering.
   * 
   * @param sourceOrd - The ord of the source object.
   * @param sourceSlotName - The name of the source slot.
   * @param targetSlotName - The name of the target slot name.
   * @param linkName - The name of the link slot that will be created.
   * @return Returns true if the link is good.
   */
  private boolean linkSlots(String sourceOrd, String sourceSlotName, Slot targetSlotName, String linkName)
  {
    sourceOrd = BFormat.make(sourceOrd).format(this.asComponent());
    boolean linkIsOk = false;
    
    //see if the object already has a link
    BLink[] links = this.getLinks(targetSlotName);
    if(links.length>0)
    {
      //will only alter link 0, not meant as a many to 1!!!
      //try to make ord from input link string
      BOrd ord = BOrd.make(sourceOrd);
      if(isOrdValid(ord))
      {
        logger.trace(mySelf().getSlotPath().toString() + " - link already exists and ord is valid\r\nOrd specified: " + sourceOrd);
        boolean linkIsActive = false;
        try
        {
          links[0].setSourceOrd(ord);
          linkIsActive = links[0].isActive();
          if(linkIsActive) logger.trace(mySelf().getSlotPath().toString() + " - existing link is active");
          else logger.trace(mySelf().getSlotPath().toString() + " - existing link is NOT active");
          linkIsOk = true;
        }
        catch (Exception e)
        {
          linkIsOk = false;
          if(!logger.isTraceOn() && !linkIsActive) logger.error(mySelf().getSlotPath().toString() + " - existing link is NOT active");
          logger.error(mySelf().getSlotPath().toString() + " - Could not set source ord on existing link!");
          logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
          if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
          
          logger.trace(mySelf().getSlotPath().toString() + " - Deactivating and removing existing link");
          try
          {
            links[0].deactivate();
            this.remove(links[0]);
          }
          catch (Exception f)
          {
            linkIsOk = false;
            logger.error(mySelf().getSlotPath().toString() + " - Could not deactivate and remove invalid link!");
            logger.error(mySelf().getSlotPath().toString() + " - " + f.getMessage());
            if(logger.getSeverity() <= Log.ERROR) f.printStackTrace();
          }
        }
        
        if(!linkIsActive)
        {
          logger.trace(mySelf().getSlotPath().toString() + " - Link is inactive, attempting to activate link");
          try
          {
            links[0].activate();
            linkIsOk = true;
          }
          catch (Exception e)
          {
            linkIsOk = false;
            logger.error(mySelf().getSlotPath().toString() + " - Could not activate link! Source slot does not exist!\r\nSource ord specified: " + sourceOrd + "\r\nSource slot specified: " + sourceSlotName);
            logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
            if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
          }
          
          logger.trace(mySelf().getSlotPath().toString() + " - Removing existing link");
          try
          {
            this.remove(links[0]);
          }
          catch (Exception e)
          {
            linkIsOk = false;
            logger.error(mySelf().getSlotPath().toString() + " - Could not deactivate and remove invalid link!  This needs to be resolved manually!");
            logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
            if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
          }
        }
      }
      else
      {
        logger.trace(mySelf().getSlotPath().toString() + " - Invalid source ord provided!\r\nOrd specified: " + sourceOrd);
        logger.trace(mySelf().getSlotPath().toString() + " - Removing existing link");
        links[0].deactivate();
        this.remove(links[0]);
        linkIsOk = false;
      }
    }
    else
    {
      logger.trace(mySelf().getSlotPath().toString() + " - Link does not exist, attempting to create one.\r\nOrd specified: " + sourceOrd);
      //no link, create one if possible
      BOrd ord = BOrd.make(sourceOrd);
      if(isOrdValid(ord))
      {
        logger.trace(mySelf().getSlotPath().toString() + " - Ord is valid");
        BLink link = new BLink(ord,sourceSlotName,targetSlotName.getName(),true);
        
        try {this.add(linkName, link);}
        catch (Exception e)
        {
          logger.error(mySelf().getSlotPath().toString() + " - Could not create a new slot named " + linkName + ", because this slot name already exixts!");
          logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
          if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
          links = null;
          return false;
        }
        
        logger.trace(mySelf().getSlotPath().toString() + " - Activating link");
        try
        {
          link.activate();
          linkIsOk = true;
        }
        catch (Exception e)
        {
          logger.error(mySelf().getSlotPath().toString() + " - Source slot does not exist!\r\nSource ord specified: " + sourceOrd + "\r\nSource slot specified: " + sourceSlotName);
          logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
          if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
          linkIsOk = false;
        }
        
        if(!linkIsOk)
        {
          logger.trace(mySelf().getSlotPath().toString() + " - Could not activate the link, attempting to remove the slot");
          try {this.remove(linkName);}
          catch (Exception e)
          {
            logger.error(mySelf().getSlotPath().toString() + " - Could not remove the link to the invalid slot on the source!");
            logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
            if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
          }
        }
      }
      else linkIsOk = false;
    }
    links = null;
    return linkIsOk;
  }
  
  /**Checks to ensure the ord is valid before linking.*/
  private boolean isOrdValid(BOrd ord)
  {
    try
    {
      //try to create the component - if it fails, false
      BComponent com = (BComponent)ord.relativizeToHost().get();
      //This gets rid of the "unused variable" warning Eclipse gives me
      com = (BComponent)com;
      return true;
    }
    catch(Exception e)
    {
      return false;
    }
  }
  
  /**Creates all of the shift_*_* slots used to track part counts and times*/
  private void shiftSlots(int SlotCount, Context cx)
  {
    try
    {
      for(int i=1; i<=SlotCount; i++)
      { 
        
        //TODO: Create a weekly counter
        //TODO: Create max good && max bad for each shift
        //TODO: Track average production per shift 
        
        if(((BObject)get("shift_"+i+"_GoodParts"))==null) {this.add(("shift_"+i+"_GoodParts"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(0)), cx);}
        if(((BObject)get("shift_"+i+"_BadParts"))==null) {this.add(("shift_"+i+"_BadParts"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(0)), cx);}
        if(((BObject)get("shift_"+i+"_PercentageGoodParts"))==null) {this.add(("shift_"+i+"_PercentageGoodParts"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.tryMake("showUnits=b:true|units=u:percent;%;;;|precision=i:1"), cx);}
        if(((BObject)get("shift_"+i+"_PercentageBadParts"))==null) {this.add(("shift_"+i+"_PercentageBadParts"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.tryMake("showUnits=b:true|units=u:percent;%;;;|precision=i:1"), cx);}
        if(((BObject)get("shift_"+i+"_TotalParts"))==null) {this.add(("shift_"+i+"_TotalParts"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(0)), cx);}
        if(((BObject)get("shift_"+i+"_AvgPartsPerHour"))==null) {this.add(("shift_"+i+"_AvgPartsPerHour"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(1)), cx);}
        if(((BObject)get("shift_"+i+"_AvgCycleTime"))==null) {this.add(("shift_"+i+"_AvgCycleTime"), BRelTime.make(0), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.SHOW_MILLISECONDS,false), cx);}
        if(((BObject)get("shift_"+i+"_Hours"))==null) {this.add(("shift_"+i+"_Hours"), BRelTime.make(0), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.SHOW_MILLISECONDS,false), cx);}
        if(((BObject)get("shift_"+i+"_Breaks"))==null) {this.add(("shift_"+i+"_Breaks"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(0)), cx);}
        if(((BObject)get("shift_"+i+"_BreakHours"))==null) {this.add(("shift_"+i+"_BreakHours"), BRelTime.make(0), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.SHOW_MILLISECONDS,false), cx);}
        if(((BObject)get("shift_"+i+"_Target"))==null)
        {
          if(shiftTargetValuesLinkActive) this.add(("shift_"+i+"_Target"), new BStatusNumeric(0,BStatus.ok), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(1)), cx);
          else this.add(("shift_"+i+"_Target"), new BStatusNumeric(0,BStatus.ok), Flags.SUMMARY, BFacets.make(BFacets.PRECISION, BInteger.make(1)), cx);
        }
        if(((BObject)get("shift_"+i+"_PercentageOfTarget"))==null) {this.add(("shift_"+i+"_PercentageOfTarget"), new BStatusNumeric(0,BStatus.ok), Flags.SUMMARY|Flags.READONLY, BFacets.tryMake("showUnits=b:true|units=u:percent;%;;;|precision=i:0"), cx);}
      }
      
      for(int i=SlotCount+1;
          (BObject)get("shift_"+i+"_GoodParts")!=null ||
          (BObject)get("shift_"+i+"_BadParts")!=null ||
          (BObject)get("shift_"+i+"_PercentageGoodParts")!=null || 
          (BObject)get("shift_"+i+"_PercentageBadParts")!=null || 
          (BObject)get("shift_"+i+"_AvgPartsPerHour")!=null || 
          (BObject)get("shift_"+i+"_AvgCycleTime")!=null ||
          (BObject)get("shift_"+i+"_TotalParts")!=null ||
          (BObject)get("shift_"+i+"_Hours")!=null ||
          (BObject)get("shift_"+i+"_Breaks")!=null ||
          (BObject)get("shift_"+i+"_BreakHours")!=null || 
          (BObject)get("shift_"+i+"_Target")!=null || 
          (BObject)get("shift_"+i+"_PercentageOfTarget")!=null;
          i++)
      {
        if(((BObject)get("shift_"+i+"_GoodParts"))!=null) {this.remove("shift_"+i+"_GoodParts");}             
        if(((BObject)get("shift_"+i+"_BadParts"))!=null) {this.remove("shift_"+i+"_BadParts");}             
        if(((BObject)get("shift_"+i+"_PercentageGoodParts"))!=null) {this.remove("shift_"+i+"_PercentageGoodParts");}             
        if(((BObject)get("shift_"+i+"_PercentageBadParts"))!=null) {this.remove("shift_"+i+"_PercentageBadParts");}             
        if(((BObject)get("shift_"+i+"_TotalParts"))!=null) {this.remove("shift_"+i+"_TotalParts");}             
        if(((BObject)get("shift_"+i+"_AvgPartsPerHour"))!=null) {this.remove("shift_"+i+"_AvgPartsPerHour");}             
        if(((BObject)get("shift_"+i+"_AvgCycleTime"))!=null) {this.remove("shift_"+i+"_AvgCycleTime");}             
        if(((BObject)get("shift_"+i+"_Hours"))!=null) {this.remove("shift_"+i+"_Hours");}             
        if(((BObject)get("shift_"+i+"_Breaks"))!=null) {this.remove("shift_"+i+"_Breaks");}             
        if(((BObject)get("shift_"+i+"_BreakHours"))!=null) {this.remove("shift_"+i+"_BreakHours");}             
        if(((BObject)get("shift_"+i+"_Target"))!=null) {this.remove("shift_"+i+"_Target");}             
        if(((BObject)get("shift_"+i+"_PercentageOfTarget"))!=null) {this.remove("shift_"+i+"_PercentageOfTarget");}             
      }
    }
    catch (Exception e)
    {
      logger.error(mySelf().getSlotPath().toString() + " - " + e.getMessage());
      if(logger.getSeverity() <= Log.ERROR) e.printStackTrace();
    }
    
    refreshTargetData();
  }
  
  private void scheduleMidnightTimer()
  {
    //Create a random number that is between 0-300 seconds
    int randomNumber = (int) ((Math.random()) * 300000);
    int offsetMillis;
    int offsetSeconds = 0;
    if(randomNumber >= 1000)
    {
      offsetSeconds = randomNumber / 1000;
      offsetMillis = randomNumber % 1000;
    }
    else offsetMillis = randomNumber;
    
    BAbsTime nextMidnight = BAbsTime.now().timeOfDay(0, 0, offsetSeconds, offsetMillis).nextDay();
    if(midnightTimer != null) midnightTimer.cancel();
    midnightTimer = Clock.schedule(mySelf(), nextMidnight, moveTodaysPartCountsToYesterday, null);
  }
  
  private BRelTime roundToNearestSecond(long rawMillis)
  {
    int temp = (int) rawMillis / 1000;
    temp = temp * 1000;
    if(rawMillis - (long)temp >= 500) temp = temp + 1000; 
    return BRelTime.make((long) temp);
  }
  
  private BAbsTime roundToNearestSecond()
  {
    return roundToNearestSecond(BAbsTime.now());
  }
  
  private BAbsTime roundToNearestSecond(BAbsTime rawAbsTime)
  {
    BAbsTime tempTime = rawAbsTime;
    if(rawAbsTime.getMillisecond()<500) tempTime.subtract(BRelTime.make(rawAbsTime.getMillisecond()));
    else tempTime.add(BRelTime.make(1000 - rawAbsTime.getMillisecond()));
    return BAbsTime.make(tempTime.getYear(), tempTime.getMonth(), tempTime.getDay(), tempTime.getHour(), tempTime.getMinute(), tempTime.getSecond(), 0);
  }
  
  private BRelTime roundToNearestSecond(BRelTime rawRelTime)
  {
    int temp = (int) rawRelTime.getMillis() / 1000;
    temp = temp * 1000;
    if(rawRelTime.getMillis() - (long)temp >= 500) temp = temp + 1000; 
    return BRelTime.make((long) temp);
  }
  
  private BRelTime roundToNearestMinute(BRelTime rawRelTime)
  {
    BRelTime result = BRelTime.makeMinutes(rawRelTime.getMinutes());
    if(roundToNearestSecond(rawRelTime).getSeconds() - result.getSeconds() >= 30) result = BRelTime.makeMinutes(result.getMinutes() + 1);
    return result;
  }
  
  private void addSlotFlag(Property inPropertyName, int flag)
  {
    Slot updateSlot = mySelf().getSlot(inPropertyName.getName());
    mySelf().setFlags(updateSlot, (mySelf().getFlags(updateSlot) | flag));
  }
  
  private void removeSlotFlag(Property inPropertyName, int flag)
  {
    Slot updateSlot = mySelf().getSlot(inPropertyName.getName());
    mySelf().setFlags(updateSlot, (mySelf().getFlags(updateSlot) & ~flag));
  }
  
  private void addSlotFlag(String inPropertyName, int flag)
  {
    Slot updateSlot = mySelf().getSlot(inPropertyName);
    mySelf().setFlags(updateSlot, (mySelf().getFlags(updateSlot) | flag));
  }
  
  private void removeSlotFlag(String inPropertyName, int flag)
  {
    Slot updateSlot = mySelf().getSlot(inPropertyName);
    mySelf().setFlags(updateSlot, (mySelf().getFlags(updateSlot) & ~flag));
  }
  
  public String getPlexContainerStatusString(double v)
  {
    String containerStatusPlexString = ""+v;
    
    switch((int) v)
    {
      case 0: containerStatusPlexString = "0"; break;
      case 1: containerStatusPlexString = "OK"; break;
      case 2: containerStatusPlexString = "Hold"; break;
      case 4: containerStatusPlexString = "Rejected"; break;
      case 8: containerStatusPlexString = "Pass-Through"; break;
    }
    return containerStatusPlexString;
  }
  
  public static final Log logger = Log.getLog(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
}