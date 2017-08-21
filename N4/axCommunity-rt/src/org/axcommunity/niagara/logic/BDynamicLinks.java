package org.axcommunity.niagara.logic;


import java.nio.charset.Charset;

import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import com.tridium.util.StringEscapeUtils;

/**
 * The primary function of this object is to be able to link from several objects anywhere on the station using a list of ords and/or
 * format ords, and slots.	In the CSV list, the ord or format ord is the first value, then the source slot, then the target slot.	The
 * target slot is automatically created using the name you specify, and the type is automatically determined by the source slot type.<br>
 * <br>
 * The secondary function of this object is to resolve formats to strings.	This is done by leaving the source slot empty in the CSV 
 * string.	These slots will not have a link shown on the object.<br>
 * <br>
 * <br>
 * Provide a CSV string structured as such:<br>
 * [format ord or string],[source slot name (leave blank if the ord is a string)],[Target slot name]<i>\n</i><br>
 * [format ord or string],[source slot name (leave blank if the ord is a string)],[Target slot name]<i>\n</i><br>
 * etc...<br>
 * <br>
 * <br>
 * If the format ord is a string output (such as %parent.name%, which would return the parent folder's name), 
 * leave the source slot name blank.	The target slot name, however, must always be provided.<br>
 * <br>
 * If the intention is to link the slot to another object, the proper formatting for the ord 
 * (or the value returned by the format ord) should be "station:|slot:/dir/object"<br>
 * <br>
 * <br>
 * Example:<br>
 * station:|%parent.sampleObject.slotPath%,out,someSlotFromAnObjectInMyParentFolder<br>
 * %parent.parent.name.substring(8,11)%,,PartOfANameInMyPath<br>
 * <br>
 * <br>
 * Eric's example from a production server:<br>
 * %parent.parent.parent.name%,,WorkcenterName<br>
 * station:|slot:/Global/ProprietaryData/CSVData,out,ProprietaryDataCsv<br>
 * station:|%parent.parent.slotPath%/Machine_Status/Status_Manual,out,Manual<br>
 * <br>
 * <br>
 * Notes:<br>
 *		- If the only the ord changes (source and target slot names remain the same), the link on the existing slot will be updated to the new ord<br>
 *		- If the only the target slot name changes (source ord and slot name remain the same), the target slot name will be renamed (not removed) and any links in or out of that slot will be updated to the new name.<br>
 *		- If the source slot name changes and the new source slot is the same type as the old source slot, the link is updated to the new source slot.<br>
 *		- If the source slot name changes and the new source slot is a different type, the old target slot is removed and a new one is created using the new type.<br>
 * <br>
 * @author Eric Bishop
 * @creation Aug 1, 2016
 */
public class BDynamicLinks extends BComponent
{
	private static BFacets fctStrMulti = BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100));
	
	/**See the class description for more information*/
	public static final Property slotInfoCsv = newProperty(0, "%parent.name%,,MyParentName\nstation:|%slotPath%,,SampleSlotPath", fctStrMulti);
	/**See the class description for more information*/
	public String getSlotInfoCsv() { return getString(slotInfoCsv);}
	/**See the class description for more information*/
	public void setSlotInfoCsv(String v) {setString(slotInfoCsv,v);}

	/**Under development.	This doesn't do anything yet, so it is hidden.*/
	public static final Property enableDynamicKnobs = newProperty(Flags.HIDDEN, true);
	/**Under development.	This doesn't do anything yet, so it is hidden.*/
	public boolean getEnableDynamicKnobs() { return getBoolean(enableDynamicKnobs);}
	/**Under development.	This doesn't do anything yet, so it is hidden.*/
	public void setEnableDynamicKnobs(boolean v) {setBoolean(enableDynamicKnobs,v);}
	
	/**Under development.	This doesn't do anything yet.*/
	public static final Property knobInfoCsv = newProperty(Flags.HIDDEN, "", fctStrMulti);
	/**Under development.	This doesn't do anything yet.*/
	public String getKnobInfoCsv() { return getString(knobInfoCsv);}
	/**Under development.	This doesn't do anything yet.*/
	public void setKnobInfoCsv(String v) {setString(knobInfoCsv,v);}
	
	public static final Property executeFindAndReplace = newProperty(0, false);
	public boolean getExecuteFindAndReplace() { return getBoolean(executeFindAndReplace);}
	public void setExecuteFindAndReplace(boolean v) {setBoolean(executeFindAndReplace,v);}
	
	public static final Property findStringInCsv = newProperty(0, "", fctStrMulti);
	public String getFindStringInCsv() { return getString(findStringInCsv);}
	public void setFindStringInCsv(String v) {setString(findStringInCsv,v);}
	
	public static final Property replaceStringInCsv = newProperty(0, "", fctStrMulti);
	public String getReplaceStringInCsv() { return getString(replaceStringInCsv);}
	public void setReplaceStringInCsv(String v) {setString(replaceStringInCsv,v);}
	

	/**Set to zero seconds to disable*/
	public static final Property refreshInterval = newProperty(0, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.FALSE, BFacets.MIN, BRelTime.make(0)));
	/**Set to zero seconds to disable*/
	public BRelTime getRefreshInterval() { return (BRelTime)get(refreshInterval);}
	/**Set to zero seconds to disable*/
	public void setRefreshInterval(BRelTime v) {set(refreshInterval,v);}
	
	public static final Property refreshLinksAtMidnight = newProperty(0, true);
	public boolean getRefreshLinksAtMidnight() { return getBoolean(refreshLinksAtMidnight);}
	public void setRefreshLinksAtMidnight(boolean v) {setBoolean(refreshLinksAtMidnight,v);}
	
	public static final Property ignoreMissingObjects = newProperty(0, false);
	public boolean getIgnoreMissingObjects() { return getBoolean(ignoreMissingObjects);}
	public void setIgnoreMissingObjects(boolean v) {setBoolean(ignoreMissingObjects,v);}
	
	public static final Property statusForInvalidOrds = newProperty(0, BStatus.makeAlarm(BStatus.stale, true), null);
	public BStatus getStatusForInvalidOrds() { return (BStatus)get(statusForInvalidOrds); }
	public void setStatusForInvalidOrds(BStatus v) { set(statusForInvalidOrds,v,null); }
	
	public static final Property reorderSlotsBasedOnCsvString = newProperty(0, false);
	public boolean getReorderSlotsBasedOnCsvString() { return getBoolean(reorderSlotsBasedOnCsvString);}
	public void setReorderSlotsBasedOnCsvString(boolean v) {setBoolean(reorderSlotsBasedOnCsvString,v);}
	
	public static final Property useAreaZoneStation = newProperty(Flags.HIDDEN, false);
	public boolean getUseAreaZoneStation() { return getBoolean(useAreaZoneStation); }
	public void setUseAreaZoneStation(boolean v) { setBoolean(useAreaZoneStation, v); }
	
	public static final Property enableDelimittedValues = newProperty(0, false);
	public boolean getEnableDelimittedValues() { return getBoolean(enableDelimittedValues); }
	public void setEnableDelimittedValues(boolean v) { setBoolean(enableDelimittedValues, v); }

	public static final Property outDelimitedSlotNames = newProperty(0, BString.DEFAULT, BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public String getOutDelimitedSlotNames() { return getString(outDelimitedSlotNames); }
	public void setOutDelimitedSlotNames(String v) { setString(outDelimitedSlotNames,v,null); }
	
	public static final Property outDelimitedSlotValues = newProperty(0, BString.DEFAULT, BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public String getOutDelimitedSlotValues() { return getString(outDelimitedSlotValues); }
	public void setOutDelimitedSlotValues(String v) { setString(outDelimitedSlotValues,v,null); }
	
	public static final Property outDelimitedSlotNameValuePairs = newProperty(0, BString.DEFAULT, BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public String getOutDelimitedSlotNameValuePairs() { return getString(outDelimitedSlotNameValuePairs); }
	public void setOutDelimitedSlotNameValuePairs(String v) { setString(outDelimitedSlotNameValuePairs,v,null); }
	
	public static final Property inPairsDelimiter = newProperty(0, "\\u007C", BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public String getInPairsDelimiter() { return getString(inPairsDelimiter); }
	public void setInPairsDelimiter(String v) { setString(inPairsDelimiter,v,null); }
	
	public static final Property inDelimiter = newProperty(0, "\\u002C", BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public String getInDelimiter() { return getString(inDelimiter); }
	public void setInDelimiter(String v) { setString(inDelimiter,v,null); }
	
	
	/**This will refresh any links and any string values*/
	public static final Action refreshLinks = newAction(Flags.OPERATOR,null);
	/**This will refresh any links and any string values*/
	public void refreshLinks() {invoke(refreshLinks,null,null);}
	
	public static final Action midnightTimerExpired = newAction(Flags.HIDDEN,null);
	public void midnightTimerExpired() {invoke(midnightTimerExpired,null,null);}
	
	/**This is fired every time the links are refreshed regardless if any changes occurred.*/
	public static final Topic LinksRefreshed = newTopic(0);
	public void fireLinksRefreshed(BBoolean event){fire(LinksRefreshed,event,null);}
	
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public static final Topic CsvSlotNames = newTopic(0);
	public void fireCsvSlotNames(BString event){fire(CsvSlotNames,event,null);}
	
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public static final Topic CsvSlotValues = newTopic(0);
	public void fireCsvSlotValues(BString event){fire(CsvSlotValues,event,null);}
	
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public static final Topic CsvSlotNameValuePairs = newTopic(0);
	public void fireCsvSlotNameValuePairs(BString event){fire(CsvSlotNameValuePairs,event,null);}
	
	
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BDynamicLinks.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
	public static final Log logger = Log.getLog(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
	
	private static final BFacets statusForInvalidOrdsFacets =	BFacets.make(BFacets.FIELD_EDITOR, BString.make("kitControl:PropagateFlagsFE"));
	public BFacets getSlotFacets(Slot slot)
	{
		if (slot.getName().equals(statusForInvalidOrds)) return statusForInvalidOrdsFacets;
		else return super.getSlotFacets(slot);
	}
		
	static int colSourceOrd = 0;
	static int colSourceSlotName = 1;
	static int colTargetSlotName = 2;	
	/*------------------------------------------------------------------------------------------------------------------------*/
	
	String [][] arrSlotInfo = new String[0][0];
	Clock.Ticket midnightTimer;
	Clock.Ticket refreshTimer;
	
	//This just makes it easier to copy this source into a program object.
	final BComponent destinationComp = this;
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void started() throws Exception
	{
		if(!Sys.atSteadyState() || !isRunning()) return;
		//At this point, we know the object was just created (or copied).
		try
		{
			startupRoutine();
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath() + "\n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace() + "\n" + "TO STRING: \n" + e.toString()); 
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void atSteadyState() throws Exception
	{
		if(!Sys.atSteadyState() || !isRunning()) return;

		try
		{
			startupRoutine();
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath() + "\n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace() + "\n" + "TO STRING: \n" + e.toString()); 
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void stopped()
	{
		if (refreshTimer != null) refreshTimer.cancel();
		if(midnightTimer != null) midnightTimer.cancel();
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void changed(Property p, Context cx)
	{
		if(!Sys.atSteadyState() || !destinationComp.isRunning()) return;
		
		if(p.equals(slotInfoCsv) || p.equals(statusForInvalidOrds) || p.equals(useAreaZoneStation)) 
		{
			doRefreshLinks();
			return;
		}
		
		if(p.equals(refreshInterval))
		{
			updateTimer();
			return;
		}
		
		if(p.equals(refreshLinksAtMidnight)) 
		{
			scheduleMidnightTimer();
			return;
		}
		
		if(p.equals(enableDynamicKnobs))
		{
			if(getEnableDynamicKnobs())
			{
				removeSlotFlag(knobInfoCsv, Flags.HIDDEN);
				//Call new knob void here
			}
			else
			{
				addSlotFlag(knobInfoCsv, Flags.HIDDEN);
				//I'm still not sure if any knobs should be deleted yet, but if so, it should be called here
			}
			return;
		}
		
		if(p.equals(executeFindAndReplace) && 
				getExecuteFindAndReplace() && 
				getFindStringInCsv().length() > 0 && 
				getSlotInfoCsv().length() > 0)
		{
			setSlotInfoCsv(replaceString(getSlotInfoCsv(), getFindStringInCsv(), getReplaceStringInCsv()));
			setFindStringInCsv("");
			setExecuteFindAndReplace(false);
			return;
		}
		
		if(p.equals(reorderSlotsBasedOnCsvString) && getReorderSlotsBasedOnCsvString())
		{
			String targetSlotName = null;
			for (int i = 0; i < arrSlotInfo.length; i++)
			{
				targetSlotName = arrSlotInfo[i][colTargetSlotName];
				if(targetSlotName != null)
				{
					if(targetSlotName.length() > 0)
					{
						try
						{
							if(destinationComp.getProperty(targetSlotName).isDynamic())
							{
								destinationComp.reorderToBottom(destinationComp.getProperty(SlotPath.escape(targetSlotName)));
							}
						}
						catch (Exception e)
						{
							logger.error(destinationComp.getSlotPath().toString() + " - Could not reorder slot: " + targetSlotName);
							logger.trace(e.getMessage());
							if(logger.isTraceOn()) e.printStackTrace();
						}
					}
				}
			}
			
			makeDelimitedOutput();
			return;
		}
		
		if( p.equals(enableDelimittedValues) ) 
		{
			makeDelimitedOutput();
			return;
		}
		
		if( !p.equals(outDelimitedSlotNames) && !p.equals(outDelimitedSlotValues) ) 
		{
			makeDelimitedOutput();
			return;
		}
		
		
		
		
	}
	
	String escape(String s){return com.tridium.util.EscUtil.slot.escape(s);}
	String unescape(String s){return com.tridium.util.EscUtil.slot.unescape(s);}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * makeCsvOutput was added on 2017.05.04 by Justin Koffler
	 */
	public void makeDelimitedOutput()
	{
		if(getEnableDelimittedValues()==true)
		{
			String csvNames = "";
			String csvValues = "";
			String pairs = "";

			try
			{

				//Normal Delimiter Setup..........
				String delim = getInDelimiter();
				try
				{
					if(getInDelimiter().substring(0, 2).toString().equalsIgnoreCase("\\u"))
					{
						String tempDelim = getInDelimiter();
						tempDelim = tempDelim.replace("\\","");
						String[] arr = tempDelim.split("u");
						delim = "";
						for(int i = 1; i < arr.length; i++)
						{
							int hexVal = Integer.parseInt(arr[i], 16);
							delim += (char)hexVal;
						}
					}
				}
				catch (Exception e){}
				
				
				//Pairs Delimiter Setup..........
				String parsDelim = getInPairsDelimiter();
				try
				{
					if(getInPairsDelimiter().substring(0, 2).toString().equalsIgnoreCase("\\u"))
					{
						String tempPairsDelim = getInPairsDelimiter();
						tempPairsDelim = tempPairsDelim.replace("\\","");
						String[] arr = tempPairsDelim.split("u");
						parsDelim = "";
						for(int i = 1; i < arr.length; i++)
						{
							int hexVal = Integer.parseInt(arr[i], 16);
							parsDelim += (char)hexVal;
						}
					}
				}
				catch (Exception e){}
				
				
				//Iterate through each slot and build our delimited values...
				Property[] dyProps = this.getDynamicPropertiesArray();
				
				for(int i = 0; i < dyProps.length; i++)
				{
					Property property = dyProps[i];
					
					if(  !property.isAction() && !property.isTopic() && !property.getType().is(BLink.TYPE) && !property.getType().is(BWsAnnotation.TYPE) )
					{
						String name = unescape(property.getName());
						String value = "";
						
						if(property.getType().toString().toUpperCase().indexOf("STATUS") > -1)
						{
							BStatusValue sv = (BStatusValue) get(property).asValue();
							value = sv.getValueValue().toString();
						}
						else{value = get(property).asValue().toString();}
						
						logger.trace("\t" + getSlotPath() + "\t" + "slotName: '" + name + "', slotValue: '" + value + "', Type: " + property.getType().toString() );
		
						if(csvNames.length()<=0){csvNames = name;}
						else{csvNames = csvNames + delim + name;}
		
						if(csvValues.length()<=0){csvValues = value;}
						else{csvValues = csvValues + delim + value;}
						
						if(pairs.length()<=0){pairs = name + parsDelim + value;}
						else{pairs = pairs + delim + name + parsDelim + value;}
					}
					else
					{
						logger.trace("\t" + getSlotPath() + "\t" + "'" + property.getName() + "' is type: '" + property.getType() + "' and will not be included in csv.");
					}
				}

				logger.trace("\n" + getSlotPath() + "\n" + "Names:\n" + csvNames + "\nValues:\n" + csvValues);

				setOutDelimitedSlotNames(csvNames);
				setOutDelimitedSlotValues(csvValues);
				setOutDelimitedSlotNameValuePairs(pairs);
				fireCsvSlotNames(BString.make(csvNames));
				fireCsvSlotValues(BString.make(csvValues));
				fireCsvSlotNameValuePairs(BString.make(pairs));
			}
			catch (Exception e)
			{
				logger.error("\n" + getSlotPath() + "\n" + "makeCsvOutput()\n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace() + "\n" + "TO STRING: \n" + e.toString()); 
			}
		}
		else
		{
			if(getOutDelimitedSlotNames().length()>0)			{setOutDelimitedSlotNames("");			fireCsvSlotNames(BString.make(""));				}
			if(getOutDelimitedSlotValues().length()>0)			{setOutDelimitedSlotValues("");			fireCsvSlotValues(BString.make(""));			}
			if(getOutDelimitedSlotNameValuePairs().length()>0)	{setOutDelimitedSlotNameValuePairs("");	fireCsvSlotNameValuePairs(BString.make(""));	}
		}
	}
	
	
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	void startupRoutine()
	{
		scheduleMidnightTimer();
		updateTimer();
		doRefreshLinks();
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	void updateTimer()
	{
		try
		{
			if (refreshTimer != null) refreshTimer.cancel();
			if(getRefreshInterval().getSeconds() > 0) refreshTimer = Clock.schedulePeriodically(destinationComp, getRefreshInterval(), refreshLinks, null);
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath() + "\n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace() + "\n" + "TO STRING: \n" + e.toString()); 
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private void scheduleMidnightTimer()
	{
		try
		{
			if(midnightTimer != null) midnightTimer.cancel();
			if(getRefreshLinksAtMidnight())
			{
				//Create a random number that is between 0-300 seconds
				int randomNumber = (int) ((Math.random()) * 300000);
				int offsetMillis;
				int offsetSeconds = 0;
				int offsetMinutes = 0;
				if(randomNumber >= 1000)
				{
					offsetSeconds = randomNumber / 1000;
					offsetMillis = randomNumber % 1000;
				}
				else offsetMillis = randomNumber;
				
	      if(offsetSeconds > 59)
	      {
	        offsetMinutes = offsetSeconds / 60;
	        offsetSeconds = offsetSeconds % 60;
	      }
	      
	      BAbsTime nextMidnight = BAbsTime.now().timeOfDay(0, offsetMinutes, offsetSeconds, offsetMillis).nextDay();
				midnightTimer = Clock.schedule(destinationComp, nextMidnight, midnightTimerExpired, null);
			}
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath() + "\n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace() + "\n" + "TO STRING: \n" + e.toString()); 
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void doMidnightTimerExpired()
	{
		doRefreshLinks();
		scheduleMidnightTimer();
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void doRefreshLinks()
	{
		if(!Sys.atSteadyState() || !destinationComp.isRunning()) return;
		
		if(getSlotInfoCsv().length() < 4)
		{
			logger.error(destinationComp.getSlotPath().toString() + " - Invalid CSV string! Please read the DymanicLinks Bajadoc!");
			return;
		}
		
		String[][] strOrds;

		try {strOrds = split(BFormat.make(getSlotInfoCsv()).format(destinationComp), "\n", ",");}
		catch (Exception e)
		{
			logger.error(destinationComp.getSlotPath().toString() + " - Could not parse CSV string!");
			logger.trace(e.getMessage());
			if(logger.isTraceOn()) e.printStackTrace();
			return;
		}
		
		//If the CSV input is less than 3 columns, exit gracefully with an error in the application manager.
		if(strOrds[0].length < 3)
		{
			logger.error(destinationComp.getSlotPath().toString() + " - Invalid number of fields provided in CSV string! Please read the DymanicLinks Bajadoc!");
			return;
		}

		if(strOrds.length < 1) return;
		boolean validLinks = false;
		
		for (int i = 0; i < strOrds.length; i ++)
		{
			validLinks = false;
			String formatOrd = strOrds[i][colSourceOrd];
			String sourceSlotName = strOrds[i][colSourceSlotName];
			String targetSlotName = strOrds[i][colTargetSlotName];
			BOrd ord = null;
			BComponent com = null;
			BValue sourceBValue = null;
			Slot sourceSlot = null;
			boolean slotAdded = false;
			boolean sourceIsActionOrTopic = false;
			boolean renamedOldSlot = false;
			BLink[] links;
			boolean invalidSourceOrd = false;
			boolean linkAdded = false;
			
			if(formatOrd == null || targetSlotName == null) continue;
			
			formatOrd = formatOrd.trim();
			if(sourceSlotName != null) sourceSlotName = sourceSlotName.trim();
			targetSlotName = SlotPath.escape(targetSlotName);
			
			if(formatOrd.length() > 0 && sourceSlotName.length() > 0)
			{
				try
				{
					ord = BOrd.make(BFormat.make(formatOrd).format(destinationComp));
					if(isOrdValid(ord))
					{
						com = (BComponent)ord.relativizeToHost().get();
						sourceSlot = com.getSlot(sourceSlotName);
						sourceIsActionOrTopic = sourceSlot.isAction() || sourceSlot.isTopic();
						if(!sourceIsActionOrTopic) sourceBValue = com.get(sourceSlotName);
						else if(sourceSlot.isAction()) sourceBValue = new BCompositeAction();
						else if(sourceSlot.isTopic()) sourceBValue = new BCompositeTopic();
					}
					else
					{
						try
						{
							ord = BOrd.make(BFormat.make("station:|" + formatOrd).format(destinationComp));
							if(isOrdValid(ord))
							{
								com = (BComponent)ord.relativizeToHost().get();
								sourceSlot = com.getSlot(sourceSlotName);
								sourceIsActionOrTopic = sourceSlot.isAction() || sourceSlot.isTopic();
								if(!sourceIsActionOrTopic) sourceBValue = com.get(sourceSlotName);
								else if(sourceSlot.isAction()) sourceBValue = new BCompositeAction();
								else if(sourceSlot.isTopic()) sourceBValue = new BCompositeTopic();
							}
							else
							{
								invalidSourceOrd = true;
								if(!getIgnoreMissingObjects())
								{
									logger.error(destinationComp.getSlotPath().toString() + " - Could not resolve ord!");
									logger.error("Ord: " + ord);
									logger.error("Source Slot Name: " + sourceSlotName);
								}
								else
								{
									logger.trace(destinationComp.getSlotPath().toString() + " - Target slot missing and invalid source ord!");
									logger.trace("Ord: " + ord);
									logger.trace("Source Slot Name: " + sourceSlotName);
								}
							}
						}
						catch (Exception f)
						{
							invalidSourceOrd = true;
							if(!getIgnoreMissingObjects())
							{
								logger.error(destinationComp.getSlotPath().toString() + " - Could not retrieve ord/slot details!");
								logger.error("Ord: " + ord);
								logger.error("Source Slot Name: " + sourceSlotName);
								logger.trace(f.getMessage());
								if(logger.isTraceOn()) f.printStackTrace();
							}
						}
					}
				}
				catch (Exception e)
				{
					invalidSourceOrd = true;
					if(!getIgnoreMissingObjects())
					{
						logger.error(destinationComp.getSlotPath().toString() + " - Could not retrieve ord/slot details!");
						logger.error("Ord: " + ord);
						logger.error("Source Slot Name: " + sourceSlotName);
						logger.trace(e.getMessage());
						if(logger.isTraceOn()) e.printStackTrace();
						continue;
					}
				}
			}
			else sourceBValue = new BStatusString();
			
			logger.trace(destinationComp.getSlotPath().toString());
			logger.trace("Ord: " + ord);
			logger.trace("Source Slot Name: " + sourceSlotName);
			try{logger.trace("Source Type: " + sourceBValue.getTypeDisplayName(null));} catch (Exception e){}
			
			//Check to see if the target slot needs to be renamed
			try
			{
				if(((BObject) destinationComp.get(targetSlotName))==null)
				{
					logger.trace("Target slot name not found: " + targetSlotName);
					for (int j = 0; j < arrSlotInfo.length; j++)
					{
						String oldFormatOrd = arrSlotInfo[j][colSourceOrd];
						String oldSourceSlotName = arrSlotInfo[j][colSourceSlotName];
						String oldTargetSlotName = arrSlotInfo[j][colTargetSlotName];
						
						if(oldTargetSlotName == null || oldFormatOrd == null) continue;
						if(oldTargetSlotName.length() == 0 || oldFormatOrd.length() == 0) continue;
						
						if(formatOrd.equals(oldFormatOrd) && sourceSlotName.equals(oldSourceSlotName))
						{
							oldTargetSlotName = SlotPath.escape(oldTargetSlotName);
							if(((BObject) destinationComp.get(oldTargetSlotName)) != null)
							{
								try
								{
									links = destinationComp.getLinks(destinationComp.getSlot(oldTargetSlotName));
									Knob[] knobs = destinationComp.getKnobs(destinationComp.getSlot(oldTargetSlotName));
									
									logger.trace(destinationComp.getSlotPath().toString() + " - Renaming slot from " + oldTargetSlotName + " to " + targetSlotName);
									destinationComp.rename(destinationComp.getProperty(oldTargetSlotName), targetSlotName);
									renamedOldSlot = true;
									
									if(links.length>0)
									{
										for (int k = 0; k < links.length; k++)
										{
											//If the source slot is blank, this should be a BQL query and not a linked slot, so remove the link.
											if(sourceSlotName.length() == 0) destinationComp.remove(links[k]);
											
											//If there is a source slot listed, then update any links to the new name.	There should only be 1
											//link here, unless the slot is a topic or event, in which case someone might have manually linked
											//something else to the slot.
											else
											{
												if(links[k].getTargetSlotName().equalsIgnoreCase(oldTargetSlotName))
												{
													logger.trace(destinationComp.getSlotPath().toString() + " - Setting target on link#" + k + " from " + oldTargetSlotName + " to " + targetSlotName);
													links[k].setTargetSlotName(targetSlotName);
													validLinks = true;
												}
												else logger.trace(destinationComp.getSlotPath().toString() + " - Did not update target on link#" + k + ": " + links[k].getTargetSlotName() + " != " + oldTargetSlotName);
											}
										}
									}
									
									
									if(knobs.length>0)
									{
										for (int k = 0; k < knobs.length; k++)
										{
											if(knobs[k].getSourceSlotName().equalsIgnoreCase(oldTargetSlotName))
											{
												logger.trace(destinationComp.getSlotPath().toString() + " - Setting source on knob#" + k + " from " + oldTargetSlotName + " to " + targetSlotName);
												knobs[k].getLink().setSourceSlotName(targetSlotName);
											}
											else logger.trace(destinationComp.getSlotPath().toString() + " - Did not update source on knob#" + k + ": " + knobs[k].getSourceSlotName() + " != " + oldTargetSlotName);
										}
									}
								}
								catch (Exception e)
								{
									logger.error(destinationComp.getSlotPath().toString() + " - Could not rename old slot: " + oldTargetSlotName + " to: " + targetSlotName);
									logger.trace(e.getMessage());
									if(logger.isTraceOn()) e.printStackTrace();
								}
							}
							break;
						}
					}
					
					//Add the new target slot, if needed
					if(!renamedOldSlot && !invalidSourceOrd)
					{
						logger.trace("Adding new slot name: " + targetSlotName);
						destinationComp.add(targetSlotName, sourceBValue.newCopy(), Flags.SUMMARY, null, null);
						slotAdded = true;
					}
				}
			}
			catch (Exception e)
			{
				logger.error(destinationComp.getSlotPath().toString() + " - Could not create new slot: " + targetSlotName);
				logger.trace(e.getMessage());
				if(logger.isTraceOn()) e.printStackTrace();
				continue;
			}
			
			if(!slotAdded && !invalidSourceOrd)
			{
				Type sourceSlotType = null;
				Type targetSlotType = null;
				
				try {sourceSlotType = sourceBValue.getType();}
				catch (Exception e)
				{
					logger.error(destinationComp.getSlotPath().toString() + " - Could not read target slot type for: " + ord + ", slot name: " + sourceSlotName);
					logger.trace(e.getMessage());
					if(logger.isTraceOn()) e.printStackTrace();
					continue;
				}
				
				try {targetSlotType = ((BObject) destinationComp.get(targetSlotName)).getType();}
				catch (Exception e)
				{
					logger.error(destinationComp.getSlotPath().toString() + " - Could not read source slot type for: " + targetSlotName);
					logger.trace(e.getMessage());
					if(logger.isTraceOn()) e.printStackTrace();
					continue;
				}
				
				if(sourceSlotType != targetSlotType)
				{
					try {destinationComp.remove(targetSlotName);}
					catch (Exception e)
					{
						logger.error(destinationComp.getSlotPath().toString() + " - Could not remove slot: " + targetSlotName + " (this slot is the incorrect type)");
						logger.trace(e.getMessage());
						if(logger.isTraceOn()) e.printStackTrace();
						continue;
					}
					
					try {destinationComp.add(targetSlotName, sourceBValue.newCopy(), Flags.SUMMARY, null, null);}
					catch (Exception e)
					{
						logger.error(destinationComp.getSlotPath().toString() + " - Could not create new slot: " + targetSlotName);
						logger.trace(e.getMessage());
						if(logger.isTraceOn()) e.printStackTrace();
						continue;
					}
				}
			}
			
			if(getReorderSlotsBasedOnCsvString())
			{
				try
				{
					if(destinationComp.getProperty(targetSlotName).isDynamic())
					{
						logger.trace(destinationComp.getSlotPath().toString() + "\t[doRefreshLinks()]\t" + "Reordering slot: " + targetSlotName);
						destinationComp.reorderToBottom(destinationComp.getProperty(targetSlotName));
					}
				}
				catch (Exception e)
				{
					logger.error(destinationComp.getSlotPath().toString() + " - Could not reorder slot: " + targetSlotName);
					logger.trace(e.getMessage());
					if(logger.isTraceOn()) e.printStackTrace();
				}
			}
			
			
			
			
			
			
			
			
			try
			{
				links = destinationComp.getLinks(destinationComp.getSlot(targetSlotName));
				
				//If the source slot name is blank, resolve the format and write it to the target slot
				if(sourceSlotName.length() == 0)
				{
					if(links.length>0)
					{
						logger.trace("Removing link from: " + targetSlotName);
						destinationComp.remove(links[0]);
					}
					
					logger.trace("Setting static value to : " + targetSlotName);
					try
					{
						((BStatusString) ((BObject) destinationComp.get(targetSlotName))).setValue(BFormat.make(formatOrd).format(destinationComp));
					}
					catch (Exception f)
					{
						logger.error(destinationComp.getSlotPath().toString() + " - Could not resolve format: " + targetSlotName);
						logger.trace(f.getMessage());
						if(logger.isTraceOn()) f.printStackTrace();
					}
					continue;
				}
				
				if(links.length>0)
				{
					//will only alter link 0, not meant as a many to 1!!!
					//try to make ord from input link string
					if(isOrdValid(ord))
					{
						boolean ordMismatch = !com.getHandleOrd().toString().equalsIgnoreCase(links[0].getSourceOrd().toString());
						boolean slotNameMismatch = !sourceSlotName.equals(links[0].getSourceSlotName());
						
						if(ordMismatch && slotNameMismatch) links[0].setEnabled(false);
						
						if(ordMismatch)
						{
							logger.trace("Setting link source ord for target slot: " + targetSlotName);
							if(links[0].isActive()) links[0].deactivate();
							links[0].setSourceOrd(com.getHandleOrd());
						}
						
						if(slotNameMismatch)
						{
							logger.trace("Setting link source slot name for target slot: " + targetSlotName);
							if(links[0].isActive()) links[0].deactivate();
							links[0].setSourceSlotName(sourceSlotName);
						}
						
						links[0].setEnabled(true);
						links[0].activate();
						validLinks = true;
					}
					else
					{
						//invalid ord, remove link 0
						logger.trace("Invalid ord, removing link on target slot: " + targetSlotName);
						destinationComp.remove(links[0]);
					}
				}
				else
				{
					//no link, create one if possible
					if(isOrdValid(ord))
					{
						logger.trace("Link not found, creating a new link for target slot: " + targetSlotName);
						BLink link = new BLink(com.getHandleOrd(),sourceSlotName,targetSlotName,true);
						destinationComp.add(null, link);
						validLinks = true;
						linkAdded = true;
					}
					else
					{
						logger.trace("Invalid source ord specified for target slot: " + targetSlotName);
						if(!invalidSourceOrd) logger.error(destinationComp.getSlotPath().toString() + " - Invalid ord: " + ord);
						validLinks = false;
					}
				}
			}
			catch (Exception e)
			{
				logger.error(destinationComp.getSlotPath().toString() + " - Link create/modify error: " + ord + ", source slot: " + sourceSlotName	+ ", Target slot:" + targetSlotName);
				logger.trace(e.getMessage());
				if(logger.isTraceOn()) e.printStackTrace();
				validLinks = false;
			}
			
			boolean newCodeFailed = true;
			
			/**
			 * TODO: This doesn't work.	It is supposed to retrieve the default 
			 * value for the slot and set that value, but getDefaultValue() only 
			 * works for frozen properties.
			 * 
			 * The code is commented out because there is no point to trying here.
			 */
//			if(!validLinks)
//			{
//				logger.trace("Link is not valid on target: " + targetSlotName + "; attempting to set value to default");
//				try
//				{
//					destinationComp.set(targetSlotName, destinationComp.getProperty(targetSlotName).getDefaultValue());
//					newCodeFailed = false;
//				}
//				catch (Exception e)
//				{
//					newCodeFailed = true;
//					logger.trace(destinationComp.getSlotPath().toString() + " - Could not get default value (using NEW code) for: " + targetSlotName);
//					logger.trace(e.getMessage());
//					if(logger.isTraceOn()) e.printStackTrace();
//				}
//			}
						
			BObject targetSlotAsObject = null;
			BValue targetSlotAsValue = null;
			boolean targetSlotIsStatusValue = false;
			try
			{
				targetSlotAsObject = ((BObject) destinationComp.get(targetSlotName));
				if(targetSlotAsObject != null)
					if(targetSlotAsObject instanceof BIStatusValue)
						targetSlotIsStatusValue = true;
			}
			catch (Exception e){}
			
			try
			{
				targetSlotAsValue = destinationComp.get(targetSlotName);
			}
			catch (Exception e){}

			
			if(targetSlotIsStatusValue)
			{
				if(validLinks)
				{
					if(linkAdded && !sourceIsActionOrTopic)
					{
						logger.trace("Copying status from source slot to target slot: " + targetSlotName);
						try {((BStatusValue) ((BObject) destinationComp.get(targetSlotName))).setStatus(((BStatusValue)((BObject) com.get(sourceSlotName))).getStatus());}
						catch (Exception e)
						{
							logger.trace(destinationComp.getSlotPath().toString() + " - Could not read source slot status");
							logger.trace("Format: " + formatOrd);
							logger.trace("Ord: " + ord);
							logger.trace("Source Slot Name: " + sourceSlotName);
							logger.trace("Target Slot Name: " + targetSlotName);
							logger.trace(e.getMessage());
							if(logger.isTraceOn()) e.printStackTrace();
						}
					}
				}
				else
				{
					if(sourceSlotName.length() == 0)
					{
						logger.trace("Target slot is a static value; setting status to OK for slot: " + targetSlotName);
						try {((BStatusValue) ((BObject) destinationComp.get(targetSlotName))).setStatus(0);}
						catch (Exception e)
						{
							logger.error(destinationComp.getSlotPath().toString() + " - Source slot name is blank, so target slot should be a BStatusString, but failed to change the status to OK!");
							logger.error("Format: " + formatOrd);
							logger.error("Ord: " + ord);
							logger.error("Target Slot Name: " + targetSlotName);
							logger.error(e.getMessage());
							if(logger.isTraceOn()) e.printStackTrace();
						}
					}
					else
					{
						//TODO: Remove this once the code above actually works
						if(newCodeFailed && targetSlotAsValue != null)
						{
							if(targetSlotAsValue instanceof BStatusBoolean)
								destinationComp.set(targetSlotName, new BStatusBoolean(false, getStatusForInvalidOrds()));
							else if(targetSlotAsValue instanceof BStatusNumeric)
								destinationComp.set(targetSlotName, new BStatusNumeric(0, getStatusForInvalidOrds()));
							else if(targetSlotAsValue instanceof BStatusEnum)
								destinationComp.set(targetSlotName, new BStatusEnum(BDynamicEnum.DEFAULT, getStatusForInvalidOrds()));
							else if(targetSlotAsValue instanceof BStatusString)
								destinationComp.set(targetSlotName, new BStatusString("", getStatusForInvalidOrds()));
						}
						
						logger.trace("Link is not valid on target: " + targetSlotName + "; attempting to set status to: " + getStatusForInvalidOrds().flagsToString(null));
						try {((BStatusValue) ((BObject) destinationComp.get(targetSlotName))).setStatus(getStatusForInvalidOrds());}
						catch (Exception e){}
					}
				}
			}
			
			//TODO: Remove this once the code above actually works
			else if(!validLinks && newCodeFailed && targetSlotAsValue != null)
			{
				if(targetSlotAsValue instanceof BBoolean)
					destinationComp.set(targetSlotName, BBoolean.DEFAULT);
				else if(targetSlotAsValue instanceof BInteger)
					destinationComp.set(targetSlotName, BInteger.DEFAULT);
				else if(targetSlotAsValue instanceof BDouble)
					destinationComp.set(targetSlotName, BDouble.DEFAULT);
				else if(targetSlotAsValue instanceof BString)
					destinationComp.set(targetSlotName, BString.DEFAULT);
				else if(targetSlotAsValue instanceof BLong)
					destinationComp.set(targetSlotName, BLong.DEFAULT);
				else if(targetSlotAsValue instanceof BFloat)
					destinationComp.set(targetSlotName, BFloat.DEFAULT);
				else if(targetSlotAsValue instanceof BAbsTime)
					destinationComp.set(targetSlotName, BAbsTime.DEFAULT);
				else if(targetSlotAsValue instanceof BRelTime)
					destinationComp.set(targetSlotName, BRelTime.DEFAULT);
			}
		}

		for (int i = 0; i < arrSlotInfo.length; i++)
		{
			String oldTargetSlotName = arrSlotInfo[i][colTargetSlotName];
			if(oldTargetSlotName == null) continue;
			
			oldTargetSlotName = SlotPath.escape(oldTargetSlotName);
			boolean foundSlot = false;
			
			if(strOrds.length > i)
			{
				String newTargetSlotName = strOrds[i][colTargetSlotName];
				if(newTargetSlotName != null) if(oldTargetSlotName.equals(SlotPath.escape(newTargetSlotName))) foundSlot = true;
			}

			if(!foundSlot)
			{
				for (int j = 0; j < strOrds.length; j++)
				{
					String newTargetSlotName = strOrds[j][colTargetSlotName];
					if(newTargetSlotName != null) if(oldTargetSlotName.equals(SlotPath.escape(newTargetSlotName)))
					{
						foundSlot = true;
						break;
					}
				}
			}
			
			if(!foundSlot && ((BObject) destinationComp.get(oldTargetSlotName))!=null)
			{
				logger.trace("Removing unused slot: " + oldTargetSlotName);
				try {destinationComp.remove(oldTargetSlotName);}
				catch (Exception e)
				{
					logger.error(destinationComp.getSlotPath().toString() + " - Could not remove unnecessary slot: " + oldTargetSlotName);
					logger.trace(e.getMessage());
					if(logger.isTraceOn()) e.printStackTrace();
					continue;
				}
			}
		}
		arrSlotInfo = strOrds;
		
		fireLinksRefreshed(BBoolean.make(true));
		
		makeDelimitedOutput();
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
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
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private static String[][] split(String inString, String delim1, String delim2)
	{									
		if(inString.indexOf(delim1) == -1 && inString.indexOf(delim2) == -1) 
		{
			if (inString.length() == 0) return new String[0][0];
			else
			{
				String[][] outputArray;
				if(inString.indexOf(delim1) == -1 && inString.indexOf(delim2) > -1)
				{
					String[] tempArray = split(inString, delim2);
					outputArray = new String[1][tempArray.length];
					
					for(int i = 0; i < tempArray.length; i++) outputArray[0][i] = tempArray[i];
					return outputArray;
				}
				else
				{
					String[] tempArray = split(inString, delim1);
					outputArray = new String[tempArray.length][1];
					
					for(int i = 0; i < tempArray.length; i++) outputArray[i][0] = tempArray[i];
					return outputArray;
				}
			}
		}
		else
		{
			String[] arrDelim1 = split(inString, delim1);
			String[][] list = new String[arrDelim1.length][8];
			String[] arrDelim2;
			int secondDimensionSize = 0;
			
			for (int i = 0; i < arrDelim1.length; i++)
			{
				arrDelim2 = split(arrDelim1[i], delim2);
				secondDimensionSize = Math.max(secondDimensionSize, arrDelim2.length);
				list = resizeArray(list, arrDelim1.length, secondDimensionSize);
				
				for (int j = 0; j < arrDelim2.length; j++) list[i][j] = arrDelim2[j];
			}
			
			if(list[0].length == secondDimensionSize) return list;
			else
			{
				String[][] trim = new String[list.length][secondDimensionSize];
				for(int i = 0; i < trim.length; i++)
					for(int j = 0; j < trim[i].length; j++)
						trim[i][j] = list[i][j];
				
				return trim;
			}
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private static String[][] resizeArray(String[][] inArray, int len1, int len2)
	{
		if (len1 <= inArray.length && len2 <= inArray[0].length) return inArray;
    
    int newLength1 = 100;
    
    if(len1 <= inArray.length) newLength1 = inArray.length;
    {
      newLength1 = Math.max(newLength1, inArray.length + 50);
      newLength1 = Math.min(newLength1, inArray.length * 2);
      newLength1 = Math.max(newLength1, len1);
    }
    
    
    int newLength2 = 100;
    
    if(len2 <= inArray[0].length) newLength2 = inArray[0].length;
    {
      newLength2 = Math.max(newLength2, inArray[0].length + 50);
      newLength2 = Math.min(newLength2, inArray[0].length * 2);
      newLength2 = Math.max(newLength2, len2);
    }
    
		String[][] expand = new String[newLength1][newLength2];
		
		for(int i = 0; i < inArray.length; i++)
			for(int j = 0; j < inArray[i].length; j++)
				expand[i][j] = inArray[i][j];
		
		return expand;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private static String[] split(String inString, String delim)
	{									
		if (inString.indexOf(delim) == -1) 
		{
			if (inString.length() == 0) return new String[0];
			else return new String[] { inString };
		}

		String[] list = new String[8];
		int firstChar = 0;
		int lastChar = 0;
		int index = 0;
		
		if(inString.startsWith("\""))
		{
			int secondQuote = inString.lastIndexOf("\"" + delim);
			if(secondQuote > 2)
			{
				//quoted string found, set the value
				firstChar = 1;
				String value = inString.substring(firstChar, secondQuote);
				value = replaceString(value, "\"\"", "\"");
				list = resizeArray(list, index);
				list[index++] = value;
				lastChar = secondQuote + delim.length() + 1;
				firstChar = lastChar;
			}
		}
		
		while (lastChar < inString.length())
		{
			if (inString.substring(lastChar).startsWith(delim))
			{
				list = resizeArray(list, index);
				list[index++] = inString.substring(firstChar, lastChar);
				logger.trace("Parameter found in CSV string: " + inString.substring(firstChar, lastChar));
				lastChar = lastChar + delim.length();
				firstChar = lastChar;
			}
			else lastChar++;
		}
		list = resizeArray(list, index);
		list[index++] = inString.substring(firstChar, inString.length());

		if (index == list.length) return list;
		else
		{
			String[] trim = new String[index];
			System.arraycopy(list, 0, trim, 0, index);
			return trim;
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private static String[] resizeArray(String[] inArray, int len)
	{
		if (len < inArray.length) return inArray;
		//int newLength = Math.min(100, inArray.length*2);
    int newLength = 100;
    newLength = Math.max(newLength, inArray.length + 50);
    newLength = Math.min(newLength, inArray.length * 2);
    newLength = Math.max(newLength, len);
    
		String[] expand = new String[newLength];
		System.arraycopy(inArray, 0, expand, 0, inArray.length);
		return expand;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private static String replaceString(String sourceStr, String oldStr, String newStr)
	{
		int idx = sourceStr.lastIndexOf(oldStr);
		if (idx != -1) 
		{
			StringBuffer results = new StringBuffer(sourceStr);
			results.replace( idx, idx+oldStr.length(), newStr);
			while( (idx=sourceStr.lastIndexOf(oldStr, idx-1)) != -1 ) results.replace(idx, idx+oldStr.length(), newStr);
			
			return results.toString();
		}
		else return sourceStr;
	}

	
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public String seguinStationPath()
	{
		String station = seguinStationSlotPath().toString();
		if(station != null)
			if(station.length() > 0)
				station = "station:|" + station;
		return station;
	}

	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public SlotPath seguinStationSlotPath()
	{
		String pointsString = "points";
		String thisSlotPath = destinationComp.getSlotPath().getBody();
		
		String station = null;
		
		if(getUseAreaZoneStation()==true)
		{
			int areaFolderStringBegin = thisSlotPath.indexOf(pointsString) + pointsString.length() + 1;
			int areaFolderStringLength = (thisSlotPath.substring(areaFolderStringBegin)).indexOf("/");
			int areaFolderStringEnd	 = areaFolderStringLength + areaFolderStringBegin;
			
			int zoneFolderStringBegin = areaFolderStringEnd + 1;
			int zoneFolderStringLength = (thisSlotPath.substring(zoneFolderStringBegin)).indexOf("/");
			int zoneFolderStringEnd	 = zoneFolderStringLength + zoneFolderStringBegin;
			
			int stationFolderStringBegin = zoneFolderStringEnd + 1;
			int stationFolderStringLength = (thisSlotPath.substring(stationFolderStringBegin)).indexOf("/");
			int stationFolderStringEnd	 = stationFolderStringLength + stationFolderStringBegin;
			
			if(stationFolderStringLength > 0) station = thisSlotPath.substring(0, stationFolderStringEnd);
		}
		else
		{
			int zoneFolderStringBegin = thisSlotPath.indexOf(pointsString) + pointsString.length() + 1;
			int zoneFolderStringLength = (thisSlotPath.substring(zoneFolderStringBegin)).indexOf("/");
			int zoneFolderStringEnd	 = zoneFolderStringLength + zoneFolderStringBegin;
			
			int stationFolderStringBegin = zoneFolderStringEnd + 1;
			int stationFolderStringLength = (thisSlotPath.substring(stationFolderStringBegin)).indexOf("/");
			int stationFolderStringEnd	 = stationFolderStringLength + stationFolderStringBegin;
			
			if(stationFolderStringLength > 0) station = thisSlotPath.substring(0, stationFolderStringEnd);
		}
		
		return new SlotPath("slot", station);
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public String seguinZonePath()
	{
		String zone = seguinZoneSlotPath().toString();
		if(zone != null)
			if(zone.length() > 0)
				zone = "station:|" + zone;
		return zone;
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public SlotPath seguinZoneSlotPath()
	{
		String pointsString = "points";
		String thisSlotPath = destinationComp.getSlotPath().getBody();
		String zone = null;
		
		if(getUseAreaZoneStation()==true)
		{
			int areaFolderStringBegin = thisSlotPath.indexOf(pointsString) + pointsString.length() + 1;
			int areaFolderStringLength = (thisSlotPath.substring(areaFolderStringBegin)).indexOf("/");
			int areaFolderStringEnd	 = areaFolderStringLength + areaFolderStringBegin;
			
			int zoneFolderStringBegin = areaFolderStringEnd + 1;
			int zoneFolderStringLength = (thisSlotPath.substring(zoneFolderStringBegin)).indexOf("/");
			int zoneFolderStringEnd	 = zoneFolderStringLength + zoneFolderStringBegin;
			
			if(zoneFolderStringLength > 0) zone = thisSlotPath.substring(0, zoneFolderStringEnd);
		}
		else
		{
			int zoneFolderStringBegin = thisSlotPath.indexOf(pointsString) + pointsString.length() + 1;
			int zoneFolderStringLength = (thisSlotPath.substring(zoneFolderStringBegin)).indexOf("/");
			int zoneFolderStringEnd	 = zoneFolderStringLength + zoneFolderStringBegin;
			
			if(zoneFolderStringLength > 0) zone = thisSlotPath.substring(0, zoneFolderStringEnd);
		}
		
		return new SlotPath("slot", zone);
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public BComponent seguinZone()
	{
		return getComponentFromPath(seguinZonePath());
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public BComponent seguinStation()
	{
		return getComponentFromPath(seguinStationPath());
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private BComponent getComponentFromPath(String path)
	{
		BOrd ord = null;
		BComponent com = null;
		try
		{
			ord = BOrd.make(BFormat.make(path).format(destinationComp));
			com = (BComponent)ord.relativizeToHost().get();
		}
		catch (Exception e)
		{
			logger.trace(e.getMessage());
			if(logger.isTraceOn()) e.printStackTrace();
		}
		return com;
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private void addSlotFlag(Property inPropertyName, int flag)
	{
		Slot updateSlot = destinationComp.getSlot(inPropertyName.getName());
		destinationComp.setFlags(updateSlot, (destinationComp.getFlags(updateSlot) | flag));
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private void removeSlotFlag(Property inPropertyName, int flag)
	{
		Slot updateSlot = destinationComp.getSlot(inPropertyName.getName());
		destinationComp.setFlags(updateSlot, (destinationComp.getFlags(updateSlot) & ~flag));
	}
}

