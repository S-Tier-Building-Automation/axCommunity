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
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

/**
@author    Eric Bishop 
@creation  23 Mar 12
@version   $Revision: 3$ $Date: 04/12/2012 06:34 AM$
<br>
This object tracks part counts and based on shift and break schedules,<br>
provides parts per shift as well as calculate parts per hour and cycle<br>
time.  Also outputs total breaks and total time on break.<br> 

<br>
<br>
The schedule linking logic was borrowed && modified from axCommunity's BDynamicLinkNumeric, written by Mike Arnott, Kors Engineering.<br>
<br>
Updates:<br>
        2015-03-09 - Fixed roundToNearestSecond(BAbsTime).  When the input was 59 seconds, it threw errors when attempting to round.<br>
*/

public class BProductionCounter extends BComponent
{
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
  
  /**This slot is the receiving end of a link from the "inShiftScheduleOrd"*/
  public static final Property inCurrentShift = newProperty(Flags.HIDDEN, new BStatusNumeric(0, BStatus.nullStatus));
  /**This slot is the receiving end of a link from the "inShiftScheduleOrd"*/
  public BStatusNumeric getInCurrentShift() { return (BStatusNumeric)get(inCurrentShift);}
  /**This slot is the receiving end of a link from the "inShiftScheduleOrd"*/
  public void setInCurrentShift(BStatusNumeric v) {set(inCurrentShift,v);}
  
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or end of "off shift")*/
  public static final Property inShiftEndTime = newProperty(Flags.HIDDEN, BAbsTime.make(), BFacets.make(BFacets.SHOW_SECONDS,true));
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or end of "off shift")*/
  public BAbsTime getInShiftEndTime() { return (BAbsTime)get(inShiftEndTime);}
  /**Ending time of the current shift.  If the current shift = 0, then this is the start of the next shift (or end of "off shift")*/
  public void setInShiftEndTime(BAbsTime v) {set(inShiftEndTime,v);}
  
  /**Ord of a boolean schedule that is set to true whenever a break occurs.  Creates a link to "inScheduledBreak". If no break is active, set the output to false (If using a boolean schedule, set default output to false, NOT null)
   * Example: station:|slot:/Global/ShiftSetup/breakSchedule*/
  public static final Property inBreakScheduleOrd = newProperty(0, "station:|slot:/Global/ShiftSetup/scheduledBreaks");
  /**Ord of a boolean schedule that is set to true whenever a break occurs.  Creates a link to "inScheduledBreak". If no break is active, set the output to false (If using a boolean schedule, set default output to false, NOT null)
   * Example: station:|slot:/Global/ShiftSetup/breakSchedule*/
  public String getInBreakScheduleOrd() { return getString(inBreakScheduleOrd);}
  /**Ord of a boolean schedule that is set to true whenever a break occurs.  Creates a link to "inScheduledBreak". If no break is active, set the output to false (If using a boolean schedule, set default output to false, NOT null)
   * Example: station:|slot:/Global/ShiftSetup/breakSchedule*/
  public void setInBreakScheduleOrd(String v) {setString(inBreakScheduleOrd,v);}
  
  /**Normally hidden and linked to the object listed in "inBreakScheduleOrd".  If desired, this slot can be directly linked to a boolean input if "inBreakScheduleOrd" is left blank.*/
  public static final Property inScheduledBreak = newProperty(Flags.HIDDEN, new BStatusBoolean(false, BStatus.nullStatus));
  /**Normally hidden and linked to the object listed in "inBreakScheduleOrd".  If desired, this slot can be directly linked to a boolean input if "inBreakScheduleOrd" is left blank.*/
  public BStatusBoolean getInScheduledBreak() { return (BStatusBoolean)get(inScheduledBreak);}
  /**Normally hidden and linked to the object listed in "inBreakScheduleOrd".  If desired, this slot can be directly linked to a boolean input if "inBreakScheduleOrd" is left blank.*/
  public void setInScheduledBreak(BStatusBoolean v) {set(inScheduledBreak,v);}
  
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
  
  /**The average number of parts processed per hour (excluding breaks) during the current shift.*/
  public static final Property currentShiftAvgPartsPerHour = newProperty(Flags.SUMMARY|Flags.READONLY, 0, BFacets.make(BFacets.PRECISION, BInteger.make(1)));
  /**The average number of parts processed per hour (excluding breaks) during the current shift.*/
  public double getCurrentShiftAvgPartsPerHour() { return getDouble(currentShiftAvgPartsPerHour);}
  /**The average number of parts processed per hour (excluding breaks) during the current shift.*/
  public void setCurrentShiftAvgPartsPerHour(double v) {setDouble(currentShiftAvgPartsPerHour,v);}
  
  /**The average cycle time (excluding breaks) during the current shift.*/
  public static final Property currentShiftAvgCycleTime = newProperty(Flags.SUMMARY|Flags.READONLY, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS,false));
  /**The average cycle time (excluding breaks) during the current shift.*/
  public BRelTime getCurrentShiftAvgCycleTime() { return (BRelTime)get(currentShiftAvgCycleTime);}
  /**The average cycle time (excluding breaks) during the current shift.*/
  public void setCurrentShiftAvgCycleTime(BRelTime v) {set(currentShiftAvgCycleTime,v);}
  
  /**The total number of good and bad parts processed during the current shift*/
  public static final Property currentShiftTotalParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good and bad parts processed during the current shift*/
  public int getCurrentShiftTotalParts() { return getInt(currentShiftTotalParts);}
  /**The total number of good and bad parts processed during the current shift*/
  public void setCurrentShiftTotalParts(int v) {setInt(currentShiftTotalParts,v);}
  
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
  
  /**The total number of good and bad parts processed today.*/
  public static final Property todayTotalParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good and bad parts processed today.*/
  public int getTodayTotalParts() { return getInt(todayTotalParts);}
  /**The total number of good and bad parts processed today.*/
  public void setTodayTotalParts(int v) {setInt(todayTotalParts,v);}
  
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
  
  /**The total number of good and bad parts processed yesterday.*/
  public static final Property yesterdayTotalParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good and bad parts processed yesterday.*/
  public int getYesterdayTotalParts() { return getInt(yesterdayTotalParts);}
  /**The total number of good and bad parts processed yesterday.*/
  public void setYesterdayTotalParts(int v) {setInt(yesterdayTotalParts,v);}
  
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
  
  /**The total number of good and bad parts processed.*/
  public static final Property totalParts = newProperty(Flags.SUMMARY|Flags.READONLY, 0);
  /**The total number of good and bad parts processed.*/
  public int getTotalParts() { return getInt(totalParts);}
  /**The total number of good and bad parts processed.*/
  public void setTotalParts(int v) {setInt(totalParts,v);}
  
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
  /**Sets the name of the link slot used for the shift schedule input*/
  static final String SHIFT_SCHEDULE_CONTAINER_LINK = "ShiftScheduleLink";
  static final String SHIFT_END_SCHEDULE_CONTAINER_LINK = "ShiftEndScheduleLink";
  /**Sets the name of the link slot used for the break schedule input*/
  static final String BREAK_SCHEDULE_CONTAINER_LINK = "BreakScheduleLink";
  /**Timer used to reset the daily values at midnight each night*/
  Clock.Ticket midnightTimer;
  /**Timer used for refreshing "currentShiftElapsedTime", "currentShiftRemainingTime", "currentShiftAvgPartsPerHour", and "currentShiftAvgCycleTime"*/
  Clock.Ticket refreshTimer;
  
  //private int possibleOffShiftBreaks = 0;
  //private long possibleOffShiftBreakHours = 0;
  
  /**started override.  Checks links, sets outputs, and sets midnight timer.*/
  public void started() throws Exception
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    
    //At this point, the object is either new or just copied
    doResetCurrentShiftPartCounts();
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
    startupRoutine();
  }
  
  private void startupRoutine()
  {
    if(getInCurrentShift().getStatus().isValid() && getInCurrentShift().getValue() >= 1 && getInCurrentShift().getValue() != getCurrentShift().getValue())
    {
      //This should trigger changed method and automatically shift the counts around as needed. 
      getCurrentShift().setValue(getInCurrentShift().getValue());
      getCurrentShift().setStatus(0);
    }
    else if(getInCurrentShift().getValue() < 0) getCurrentShift().setStatusFault(true);
    else if(getInCurrentShift().getStatus().isValid() && !getCurrentShift().getStatus().isValid()) getCurrentShift().setStatus(0);
    
    if(getScheduledBreak().getValue() != getInScheduledBreak().getValue()) getScheduledBreak().setValue(getInScheduledBreak().getValue());
    if(getScheduledProduction().getValue() != getInCurrentShift().getValue() >= 1 && !getInScheduledBreak().getValue()) getScheduledProduction().setValue(getInCurrentShift().getValue() >= 1 && !getInScheduledBreak().getValue());
    
    if(getCurrentShiftAvgPartsPerHour() < 0)              {setCurrentShiftAvgPartsPerHour(0);                 logger.message(getSlotPath().toString() + " Negative value for: CurrentShiftAvgPartsPerHour");}
    if(getCurrentShiftAvgCycleTime().getMillis() < 0)     {setCurrentShiftAvgCycleTime(BRelTime.make(0));     logger.message(getSlotPath().toString() + " Negative value for: CurrentShiftAvgCycleTime");}
    if(getCurrentShiftTotalBreakHours().getMillis() < 0)  {setCurrentShiftTotalBreakHours(BRelTime.make(0));  logger.message(getSlotPath().toString() + " Negative value for: CurrentShiftTotalBreakHours");}
    if(getCurrentShiftAvgPartsPerHour() < 0)              {setCurrentShiftAvgPartsPerHour(0);                 logger.message(getSlotPath().toString() + " Negative value for: CurrentShiftAvgPartsPerHour");}
    if(getCurrentShiftAvgCycleTime().getMillis() < 0)     {setCurrentShiftAvgCycleTime(BRelTime.make(0));     logger.message(getSlotPath().toString() + " Negative value for: CurrentShiftAvgCycleTime");}
    if(getCurrentShiftTotalBreakHours().getMillis() < 0)  {setCurrentShiftTotalBreakHours(BRelTime.make(0));  logger.message(getSlotPath().toString() + " Negative value for: CurrentShiftTotalBreakHours");}
    if(getTodayAvgPartsPerHour() < 0)                     {setTodayAvgPartsPerHour(0);                        logger.message(getSlotPath().toString() + " Negative value for: TodayAvgPartsPerHour");}
    if(getTodayAvgCycleTime().getMillis() < 0)            {setTodayAvgCycleTime(BRelTime.make(0));            logger.message(getSlotPath().toString() + " Negative value for: TodayAvgCycleTime");}
    if(getTodayTotalBreakHours().getMillis() < 0)         {setTodayTotalBreakHours(BRelTime.make(0));         logger.message(getSlotPath().toString() + " Negative value for: TodayTotalBreakHours");}
    if(getTodayTotalHours().getMillis() < 0)              {setTodayTotalHours(BRelTime.make(0));              logger.message(getSlotPath().toString() + " Negative value for: TodayTotalHours");}
    if(getTodayTotalOffShiftHours().getMillis() < 0)      {setTodayTotalOffShiftHours(BRelTime.make(0));      logger.message(getSlotPath().toString() + " Negative value for: TodayTotalOffShiftHours");}
    if(getYesterdayAvgPartsPerHour() < 0)                 {setYesterdayAvgPartsPerHour(0);                    logger.message(getSlotPath().toString() + " Negative value for: YesterdayAvgPartsPerHour");}
    if(getYesterdayAvgCycleTime().getMillis() < 0)        {setYesterdayAvgCycleTime(BRelTime.make(0));        logger.message(getSlotPath().toString() + " Negative value for: YesterdayAvgCycleTime");}
    if(getYesterdayTotalBreakHours().getMillis() < 0)     {setYesterdayTotalBreakHours(BRelTime.make(0));     logger.message(getSlotPath().toString() + " Negative value for: YesterdayTotalBreakHours");}
    if(getYesterdayTotalHours().getMillis() < 0)          {setYesterdayTotalHours(BRelTime.make(0));          logger.message(getSlotPath().toString() + " Negative value for: YesterdayTotalHours");}
    if(getYesterdayTotalOffShiftHours().getMillis() < 0)  {setYesterdayTotalOffShiftHours(BRelTime.make(0));  logger.message(getSlotPath().toString() + " Negative value for: YesterdayTotalOffShiftHours");}
    if(getTotalAvgPartsPerHour() < 0)                     {setTotalAvgPartsPerHour(0);                        logger.message(getSlotPath().toString() + " Negative value for: TotalAvgPartsPerHour");}
    if(getTotalAvgCycleTime().getMillis() < 0)            {setTotalAvgCycleTime(BRelTime.make(0));            logger.message(getSlotPath().toString() + " Negative value for: TotalAvgCycleTime");}
    if(getTotalBreakHours().getMillis() < 0)              {setTotalBreakHours(BRelTime.make(0));              logger.message(getSlotPath().toString() + " Negative value for: TotalBreakHours");}
    if(getTotalHours().getMillis() < 0)                   {setTotalHours(BRelTime.make(0));                   logger.message(getSlotPath().toString() + " Negative value for: TotalHours");}
    if(getTotalOffShiftHours().getMillis() < 0)           {setTotalOffShiftHours(BRelTime.make(0));           logger.message(getSlotPath().toString() + " Negative value for: TotalOffShiftHours");}
    
    scheduleMidnightTimer();
    doRefreshValuesForToday();
    updateTimer();
  }
  
  
  /**Checks to see what changed and sets outputs accordingly.*/
  public void changed(Property p, Context cx)
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    final BAbsTime absTimeThisChanged = roundToNearestSecond(BAbsTime.now());
    
    
    super.changed(p, cx);
    if(p.equals(inScheduledBreak))
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
        
        //TODO: Fix total break hours (it keeps going negative)
        setCurrentShiftTotalBreakHours(roundToNearestSecond(getCurrentShiftTotalBreakHours().getMillis() + breakStartTime.delta(absTimeThisChanged).getMillis()));
        setTotalBreakHours(roundToNearestSecond(getTotalBreakHours().getMillis() + breakStartTime.delta(absTimeThisChanged).getMillis()));
        breakStartTime = null;
      }
      if(!getInScheduledBreak().getValue()) breakStartTime = null;
      getScheduledBreak().setValue(getInScheduledBreak().getValue());
      getScheduledBreak().setStatus(getInScheduledBreak().getStatus());
    }
    
    if(p.equals(inShiftScheduleOrd) | p.equals(inBreakScheduleOrd)) doSetScheduleLinks();
    
    if(p.equals(inNumberOfShifts) | getInNumberOfShifts() < (int)getInCurrentShift().getValue()) shiftSlots(getInNumberOfShifts(), cx);
    if(getInNumberOfShifts() < (int)getInCurrentShift().getValue()) setInNumberOfShifts((int)getInCurrentShift().getValue());
    
    if(p.equals(inGoodTransaction) && getInGoodTransaction())
    {
      if(!getOnlyTrackPartsDurringValidShift() || getInCurrentShift().getValue() >=1)
      {
        if(getInCurrentShift().getValue() >=1 )
        {
          setCurrentShiftGoodParts(getCurrentShiftGoodParts() + 1);
          setCurrentShiftTotalParts(getCurrentShiftTotalParts() + 1);
        }
        setTodayGoodParts(getTodayGoodParts() + 1);
        setTodayTotalParts(getTodayTotalParts() + 1);
        setTotalGoodParts(getTotalGoodParts() + 1);
        setTotalParts(getTotalParts() + 1);
      }
    }
    
    if(p.equals(inBadTransaction) && getInBadTransaction())
    {
      if(!getOnlyTrackPartsDurringValidShift() || getInCurrentShift().getValue() >=1)
      {
        if(getInCurrentShift().getValue() >=1 )
        {
          setCurrentShiftBadParts(getCurrentShiftBadParts() + 1);
          setCurrentShiftTotalParts(getCurrentShiftTotalParts() + 1);
        }
        setTodayBadParts(getTodayBadParts() + 1);
        setTodayTotalParts(getTodayTotalParts() + 1);
        setTotalBadParts(getTotalBadParts() + 1);
        setTotalParts(getTotalParts() + 1);
      }
    }
    
    //if((p.equals(inGoodTransaction) && getInGoodTransaction()) || (p.equals(inBadTransaction) && getInBadTransaction()))
    //{
    //  possibleOffShiftBreaks = 0;
    //  possibleOffShiftBreakHours = 0;
    //}
    if((p.equals(inGoodTransaction) && getInGoodTransaction()) || (p.equals(inBadTransaction) && getInBadTransaction()) || (p.equals(determinePartsPerHourAndCycleTimeBasedOn)))
    {
      if((getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 0 && getCurrentShiftTotalParts() > 0) || (getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 1 && getCurrentShiftGoodParts() > 0))
      {
        refresh(absTimeThisChanged);
      }
    }
    
    if(p == inCurrentShift) 
    {
      if(getInCurrentShift().getStatus().isValid() && getCurrentShift().getStatus().isValid() && getInCurrentShift().getValue() != getCurrentShift().getValue())
      {
        int previousShift = (int)getCurrentShift().getValue();
        
        if(getInShiftToResetCountsOn() == (int)getInCurrentShift().getValue()) doResetShiftPartCounts();
        
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
          ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_GoodParts"))).setValue((double)getCurrentShiftGoodParts());
          ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_BadParts"))).setValue((double)getCurrentShiftBadParts());
          ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_TotalParts"))).setValue((double)getCurrentShiftTotalParts());
          ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_AvgPartsPerHour"))).setValue((double)getCurrentShiftAvgPartsPerHour());
          set("shift_"+previousShift+"_AvgCycleTime", getCurrentShiftAvgCycleTime());
          set("shift_"+previousShift+"_Hours", getCurrentShiftElapsedTime());
          set("shift_"+previousShift+"_BreakHours", getCurrentShiftTotalBreakHours());
          ((BStatusNumeric) ((BObject)get("shift_"+previousShift+"_Breaks"))).setValue(getCurrentShiftTotalBreaks());
          
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
      else if(getInCurrentShift().getValue() < 0) getCurrentShift().setStatusFault(true);
      else if(getInCurrentShift().getStatus().isValid() && !getCurrentShift().getStatus().isValid()) getCurrentShift().setStatus(0);
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
    }
    if(p.equals(inRefreshInterval))
    {
      updateTimer();
      refresh(absTimeThisChanged);
    }
  }
  
  
  /**Moves today's values to yesterday and resets today's values to 0.*/
  public void doMoveTodaysPartCountsToYesterday()
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    scheduleMidnightTimer();
    final BAbsTime absTimeThisChanged = roundToNearestSecond(BAbsTime.now());
    final BAbsTime lastMidnight = absTimeThisChanged.timeOfDay(0, 0, 0, 0);
    
    if(!getIgnoreShiftsWithoutAnyTransactions() || getTodayTotalParts() > 0)
    {
      setYesterdayGoodParts(getTodayGoodParts());
      setYesterdayBadParts(getTodayBadParts());
      setYesterdayTotalParts(getTodayTotalParts());
      setYesterdayTotalOffShiftHours(getTodayTotalOffShiftHours());
      
      //TODO: Fix total break hours (it keeps going negative)
      if(breakStartTime == null) setYesterdayTotalBreakHours(getTodayTotalBreakHours());
      else if(breakStartTime.isAfter(lastMidnight)) setYesterdayTotalBreakHours(getTodayTotalBreakHours());
      else setYesterdayTotalBreakHours(roundToNearestSecond(lastMidnight.delta(breakStartTime).getMillis() + getTodayTotalBreakHours().getMillis()));
      
      if(getInCurrentShift().getValue() >= 1) setYesterdayTotalHours(getTodayTotalHours());
      else if(getCurrentShiftStartTime().isAfter(lastMidnight)) setYesterdayTotalHours(getTodayTotalHours());
      else setYesterdayTotalHours(BRelTime.make(BAbsTime.now().timeOfDay(0, 0, 0, 0).getMillis() - roundToNearestSecond(getCurrentShiftStartTime().getMillis() + getTodayTotalHours().getMillis()).getMillis()));
    }
    
    setYesterdayTotalBreaks(getTodayTotalBreaks());
    setYesterdayAvgPartsPerHour(getTodayAvgPartsPerHour());
    setYesterdayAvgCycleTime(getTodayAvgCycleTime());
    
    setTodayAvgPartsPerHour(0);
    setTodayAvgCycleTime(BRelTime.make(0));
    setTodayTotalBreakHours(BRelTime.make(0));
    setTodayTotalHours(BRelTime.make(0));
    setTodayTotalOffShiftHours(BRelTime.make(0));
    setTodayTotalBreaks(0);
    
    setTodayGoodParts(0);
    setTodayBadParts(0);
    setTodayTotalParts(0);
  }
  
  /**Resets the current shift values to 0*/
  public void doResetCurrentShiftPartCounts()
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    setCurrentShiftGoodParts(0);
    setCurrentShiftBadParts(0);
    setCurrentShiftTotalParts(0);
    setCurrentShiftAvgPartsPerHour(0);
    setCurrentShiftAvgCycleTime(BRelTime.make(0));
    setCurrentShiftTotalBreakHours(BRelTime.make(0));
    setCurrentShiftStartTime(roundToNearestSecond(BAbsTime.now()));
    setCurrentShiftTotalBreaks(0);
    getCurrentShift().setValue(getInCurrentShift().getValue());
    getCurrentShift().setStatus(0);
  }
  
  /**Resets the current shift values to 0 and sets the shift start time to the BAbsTime input*/
  public void doResetCurrentShiftPartCounts(BAbsTime shiftChangeTime)
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    setCurrentShiftGoodParts(0);
    setCurrentShiftBadParts(0);
    setCurrentShiftTotalParts(0);
    setCurrentShiftAvgPartsPerHour(0);
    setCurrentShiftAvgCycleTime(BRelTime.make(0));
    setCurrentShiftTotalBreakHours(BRelTime.make(0));
    setCurrentShiftStartTime(shiftChangeTime);
    setCurrentShiftTotalBreaks(0);
    getCurrentShift().setValue(getInCurrentShift().getValue());
    getCurrentShift().setStatus(0);
  }
  
  /**Resets all shift values to 0 (except the current shift)*/
  public void doResetShiftPartCounts()
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
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
      }
    }
    catch (Exception e)
    {
      logger.error(getSlotPath().toString() + e.getMessage());
      e.printStackTrace();
    }
  }
  
  /**Resets today's part counts to 0*/
  public void doResetTodaysPartCounts()
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    setTodayGoodParts(0);
    setTodayBadParts(0);
    setTodayTotalParts(0);
    setTodayAvgPartsPerHour(0);
    setTodayAvgCycleTime(BRelTime.make(0));
    setTodayTotalBreakHours(BRelTime.make(0));
    setTodayTotalHours(BRelTime.make(0));
    setTodayTotalOffShiftHours(BRelTime.make(0));
    setTodayTotalBreaks(0);
  }
  
  /**Resets yesterday's part counts to 0*/
  public void doResetYesterdaysPartCounts()
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    setYesterdayGoodParts(0);
    setYesterdayBadParts(0);
    setYesterdayTotalParts(0);
    setYesterdayAvgPartsPerHour(0);
    setYesterdayAvgCycleTime(BRelTime.make(0));
    setYesterdayTotalBreakHours(BRelTime.make(0));
    setYesterdayTotalHours(BRelTime.make(0));
    setYesterdayTotalOffShiftHours(BRelTime.make(0));
    setYesterdayTotalBreaks(0);
  }
  
  /**Resets the total part counts to 0*/
  public void doResetTotalPartCounts()
  {
    if(!Sys.atSteadyState()|| !isRunning()) return;
    setTotalGoodParts(0);
    setTotalBadParts(0);
    setTotalParts(0);
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
    if(!Sys.atSteadyState() || !isRunning()) return;
    doResetCurrentShiftPartCounts();
    doResetShiftPartCounts();
    doResetTotalPartCounts();
    doResetTodaysPartCounts();
    doResetYesterdaysPartCounts();
    scheduleMidnightTimer();
  }
  
  /**Checks the schedule links and recreates if needed. This is run after copying the object to ensure it is still linked properly.
   * The linking logic was borrowed && modified from axCommunity's BDynamicLinkNumeric, written by Mike Arnott, Kors Engineering.*/
  public void doSetScheduleLinks()
  {
    //see if the object already has a link
    BLink[] shiftLinks = this.getLinks(this.getSlot("inCurrentShift"));
    if(shiftLinks.length>0)
    {
      //will only alter link 0, not meant as a many to 1!!!
      //try to make ord from input link string
      BOrd ord = BOrd.make(getInShiftScheduleOrd());
      if(isOrdValid(ord))
      {
        shiftLinks[0].setSourceOrd(ord);
        getCurrentShift().setStatus(0);
      }
      else
      {
        shiftLinks[0].deactivate();
        this.remove(shiftLinks[0]);
        getCurrentShift().setStatusNull(true);
      }
    }
    else
    {
      //no link, create one if possible
      BOrd ord = BOrd.make(getInShiftScheduleOrd());
      if(isOrdValid(ord))
      {
        BLink shiftLink = new BLink(ord,"out","inCurrentShift",true);
        this.add(SHIFT_SCHEDULE_CONTAINER_LINK, shiftLink);
        shiftLink.activate();
        getCurrentShift().setStatus(0);
      }
      else
      {
        getCurrentShift().setStatusNull(true);
      }
    }
    shiftLinks = null;
  
    //see if the object already has a link
    BLink[] breakLinks = this.getLinks(this.getSlot("inScheduledBreak"));
    if(breakLinks.length>0)
    {
      //will only alter link 0, not meant as a many to 1!!!
      //try to make ord from input link string
      BOrd ord = BOrd.make(getInBreakScheduleOrd());
      if(isOrdValid(ord))
      {
        breakLinks[0].setSourceOrd(ord);
        getScheduledBreak().setStatus(0);
        getScheduledProduction().setStatus(0);
      }
      else
      {
        breakLinks[0].deactivate();
        this.remove(breakLinks[0]);
        getScheduledBreak().setStatusNull(true);
        getScheduledProduction().setStatusNull(true);
      }
    }
    else
    {
      //no link, create one if possible
      BOrd ord = BOrd.make(getInBreakScheduleOrd());
      if(isOrdValid(ord))
      {
        BLink breakLink = new BLink(ord,"out","inScheduledBreak",true);
        this.add(BREAK_SCHEDULE_CONTAINER_LINK, breakLink);
        breakLink.activate();
        getScheduledBreak().setStatus(0);
        getScheduledProduction().setStatus(0);
      }
      else
      {
        getScheduledBreak().setStatusNull(true);
        getScheduledProduction().setStatusNull(true);
      }
    }
    breakLinks = null;
    
    
    //see if the object already has a link
    BLink[] shiftEndLinks = this.getLinks(this.getSlot("inShiftEndTime"));
    if(shiftEndLinks.length>0)
    {
      //will only alter link 0, not meant as a many to 1!!!
      //try to make ord from input link string
      BOrd ord = BOrd.make(getInShiftScheduleOrd());
      if(isOrdValid(ord))
      {
        shiftEndLinks[0].setSourceOrd(ord);
      }
      else
      {
        shiftEndLinks[0].deactivate();
        this.remove(shiftEndLinks[0]);
      }
    }
    else
    {
      //no link, create one if possible
      BOrd ord = BOrd.make(getInShiftScheduleOrd());
      if(isOrdValid(ord))
      {
        BLink shiftEndLink = new BLink(ord,"nextTime","inShiftEndTime",true);
        this.add(SHIFT_END_SCHEDULE_CONTAINER_LINK, shiftEndLink);
        shiftEndLink.activate();
      }
    }
    shiftEndLinks = null;

  }
  
  void updateTimer()
  {
    if (refreshTimer != null) refreshTimer.cancel();
    refreshTimer = Clock.schedulePeriodically(this, getInRefreshInterval(), refreshValuesForToday, null);
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
        setCurrentShiftAvgPartsPerHour(getCurrentShiftTotalParts()/hours);
        setCurrentShiftAvgCycleTime(BRelTime.make((long) (mSeconds/getCurrentShiftTotalParts())));
      }
      else if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 1 && getCurrentShiftGoodParts() > 0)
      {
        setCurrentShiftAvgPartsPerHour(getCurrentShiftGoodParts()/hours);
        setCurrentShiftAvgCycleTime(BRelTime.make((long) (mSeconds/getCurrentShiftGoodParts())));
      }
      else
      {
        setCurrentShiftAvgPartsPerHour(0);
        setCurrentShiftAvgCycleTime(BRelTime.make(0));
      }
    }
    
    mSeconds = getTodayTotalHours().getMillis() + absTimeThisChanged.getMillis();
    mSeconds = mSeconds - getCurrentShiftStartTime().getMillis();
    mSeconds = mSeconds - getTodayTotalBreakHours().getMillis();
    hours = mSeconds / 1000;
    hours = hours / 60;
    hours = hours / 60;
    if(hours == 0) hours = 0.0001;
    
    if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 0 && getTodayTotalParts() > 0)
    {
      setTodayAvgPartsPerHour(getTodayTotalParts()/hours);
      setTodayAvgCycleTime(BRelTime.make((long) (mSeconds/getTodayTotalParts())));
    }
    else if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 1 && getTodayGoodParts() > 0)
    {
      setTodayAvgPartsPerHour(getTodayGoodParts()/hours);
      setTodayAvgCycleTime(BRelTime.make((long) (mSeconds/getTodayGoodParts())));
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
      setTotalAvgPartsPerHour(getTotalParts()/hours);
      setTotalAvgCycleTime(BRelTime.make((long) (mSeconds/getTotalParts())));
    }
    else if(getDeterminePartsPerHourAndCycleTimeBasedOn().getOrdinal() == 1 && getTotalGoodParts() > 0)
    {
      setTotalAvgPartsPerHour(getTotalGoodParts()/hours);
      setTotalAvgCycleTime(BRelTime.make((long) (mSeconds/getTotalGoodParts())));
    }
    else
    {
      setTotalAvgPartsPerHour(0);
      setTotalAvgCycleTime(BRelTime.make(0));
    }
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
  public void shiftSlots(int SlotCount, Context cx)
  {
    try
    {
      for(int i=1; i<SlotCount+1; i++)
      { 
        
        //TODO: Create a weekly counter
        //TODO: Create max good && max bad for each shift
        //TODO: Track average production per shift 
        
        if(((BObject)get("shift_"+i+"_GoodParts"))==null) {this.add(("shift_"+i+"_GoodParts"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(0)), cx);}
        if(((BObject)get("shift_"+i+"_BadParts"))==null) {this.add(("shift_"+i+"_BadParts"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(0)), cx);}
        if(((BObject)get("shift_"+i+"_AvgPartsPerHour"))==null) {this.add(("shift_"+i+"_AvgPartsPerHour"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(1)), cx);}
        if(((BObject)get("shift_"+i+"_AvgCycleTime"))==null) {this.add(("shift_"+i+"_AvgCycleTime"), BRelTime.make(0), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.SHOW_MILLISECONDS,false), cx);}
        if(((BObject)get("shift_"+i+"_TotalParts"))==null) {this.add(("shift_"+i+"_TotalParts"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(0)), cx);}
        if(((BObject)get("shift_"+i+"_Hours"))==null) {this.add(("shift_"+i+"_Hours"), BRelTime.make(0), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.SHOW_MILLISECONDS,false), cx);}
        if(((BObject)get("shift_"+i+"_Breaks"))==null) {this.add(("shift_"+i+"_Breaks"), new BStatusNumeric(), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.PRECISION, BInteger.make(0)), cx);}
        if(((BObject)get("shift_"+i+"_BreakHours"))==null) {this.add(("shift_"+i+"_BreakHours"), BRelTime.make(0), Flags.SUMMARY|Flags.READONLY, BFacets.make(BFacets.SHOW_MILLISECONDS,false), cx);}
      }
      
      for(int i=SlotCount+1;
          (BObject)get("shift_"+i+"_GoodParts")!=null | 
          (BObject)get("shift_"+i+"_BadParts")!=null | 
          (BObject)get("shift_"+i+"_AvgPartsPerHour")!=null | 
          (BObject)get("shift_"+i+"_AvgCycleTime")!=null | 
          (BObject)get("shift_"+i+"_TotalParts")!=null | 
          (BObject)get("shift_"+i+"_Hours")!=null | 
          (BObject)get("shift_"+i+"_Breaks")!=null | 
          (BObject)get("shift_"+i+"_BreakHours")!=null;
          i++)
      {
        if(((BObject)get("shift_"+i+"_GoodParts"))!=null) {this.remove("shift_"+i+"_GoodParts");}             
        if(((BObject)get("shift_"+i+"_BadParts"))!=null) {this.remove("shift_"+i+"_BadParts");}             
        if(((BObject)get("shift_"+i+"_AvgPartsPerHour"))!=null) {this.remove("shift_"+i+"_AvgPartsPerHour");}             
        if(((BObject)get("shift_"+i+"_AvgCycleTime"))!=null) {this.remove("shift_"+i+"_AvgPartsPerHour");}             
        if(((BObject)get("shift_"+i+"_TotalParts"))!=null) {this.remove("shift_"+i+"_TotalParts");}             
        if(((BObject)get("shift_"+i+"_Hours"))!=null) {this.remove("shift_"+i+"_Hours");}             
        if(((BObject)get("shift_"+i+"_Breaks"))!=null) {this.remove("shift_"+i+"_Breaks");}             
        if(((BObject)get("shift_"+i+"_BreakHours"))!=null) {this.remove("shift_"+i+"_BreakHours");}             
      }
    }
    catch (Exception e)
    {
      logger.error(getSlotPath().toString() + e.getMessage());
      e.printStackTrace();
    }
  }
  
  private void scheduleMidnightTimer()
  {
    //Create a random number that is between 0-3000 seconds
    int randomNumber = (int) ((Math.random()) * 3000);
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
    midnightTimer = Clock.schedule(this, nextMidnight, moveTodaysPartCountsToYesterday, null);
  }
  
  private BRelTime roundToNearestSecond(long rawMillis)
  {
    int temp = (int) rawMillis / 1000;
    temp = temp * 1000;
    if(rawMillis - (long)temp >= 500) temp = temp + 1000; 
    return BRelTime.make((long) temp);
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
  public static final Log logger = Log.getLog(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
}