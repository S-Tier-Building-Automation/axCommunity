package org.axcommunity.niagara.logic;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.baja.log.Log;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.registry.Registry;
import javax.baja.registry.TypeInfo;
import javax.baja.status.BIStatusValue;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.status.BStatusValue;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComplex;
import javax.baja.sys.BComponent;
import javax.baja.sys.BConversionLink;
import javax.baja.sys.BDouble;
import javax.baja.sys.BDynamicEnum;
import javax.baja.sys.BFacets;
import javax.baja.sys.BFloat;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BLink;
import javax.baja.sys.BLong;
import javax.baja.sys.BObject;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Knob;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;
import javax.baja.util.BCompositeAction;
import javax.baja.util.BCompositeTopic;
import javax.baja.util.BConverter;
import javax.baja.util.BFormat;
import javax.baja.util.BWsAnnotation;

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
 * Other hidden jems:<br>
 * &nbsp; %seguinZonePath% returns the path to the subfolder within the points folder. <br>&nbsp; &nbsp; Ex: <b>station:|</b>slot:/Drivers/OpcNetwork/KEPServerEX/points/Training<br>
 * &nbsp; %seguinZoneSlotPath% returns the slot path to the subfolder within the points folder. <br>&nbsp; &nbsp; Ex: slot:/Drivers/OpcNetwork/KEPServerEX/points/Training<br>
 * &nbsp; %seguinZone% returns the BComponent instance of the subfolder within the points folder. This is useful when you need to use the folder path in a BFormat string.<br>
 * <br>
 * <br>
 * &nbsp; The following are the exact same as the "Zone" options above, except they return the sub-subfolder within the points folder:<br>
 * &nbsp; %seguinStationPath% returns the path to the sub-subfolder within the points folder. <br>&nbsp; &nbsp; Ex: <b>station:|</b>slot:/Drivers/OpcNetwork/KEPServerEX/points/Training/L1_0010_L2<br>
 * &nbsp; %seguinStationSlotPath% returns the slot path to the sub-subfolder within the points folder. <br>&nbsp; &nbsp; Ex: slot:/Drivers/OpcNetwork/KEPServerEX/points/Training/L1_0010_L2<br>
 * &nbsp; %seguinStation% returns the BComponent instance of the sub-subfolder within the points folder. This is useful when you need to use the folder path in a BFormat string.<br>
 * <br>
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
	
	/**These facets will get applied to all dynamic slots of this component.*/
	public static final Property facetsForDynamicSlots = newProperty(0, BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)),null);
	public BFacets getFacetsForDynamicSlots() { return (BFacets)get(facetsForDynamicSlots); }
	public void setFacetsForDynamicSlots(BFacets v) { set(facetsForDynamicSlots,v,null); }
	
	public static final Property inDebug = newProperty(0, false);
	public boolean getInDebug() { return getBoolean(inDebug); }
	public void setInDebug(boolean v) { setBoolean(inDebug, v, null); }
	
	/**This should be the format slotPath comma slotName to the component you wish to link to this component's 'enableLinks' slot.*/
	public static final Property pathToEnableLinks = newProperty(Flags.HIDDEN, "station:|slot:/Path_To_Your_Component/Your_Component_Name,Slot_Name", BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	/**This should be the format slotPath comma slotName to the component you wish to link to this component's 'enableLinks' slot.*/
	public String getPathToEnableLinks() { return getString(pathToEnableLinks); }
	/**This should be the format slotPath comma slotName to the component you wish to link to this component's 'enableLinks' slot.*/
	public void setPathToEnableLinks(String v) { setString(pathToEnableLinks, v, null); }
	
	/**When this is false all links to any dynamic slot on this component will be removed and the slot's status will be set to the status of slot 'statusForInvalidOrds'.*/
	public static final Property enableLinks = newProperty(Flags.HIDDEN, true);
	/**When this is false all links to any dynamic slot on this component will be removed and the slot's status will be set to the status of slot 'statusForInvalidOrds'.*/
	public boolean getEnableLinks() { return getBoolean(enableLinks); }
	/**When this is false all links to any dynamic slot on this component will be removed and the slot's status will be set to the status of slot 'statusForInvalidOrds'.*/
	public void setEnableLinks(boolean v) { setBoolean(enableLinks, v, null); }


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
	/**This is fired every time the links are refreshed regardless if any changes occurred.*/
	public void fireLinksRefreshed(BBoolean event){fire(LinksRefreshed,event,null);}
	
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public static final Topic CsvSlotNames = newTopic(0);
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public void fireCsvSlotNames(BString event){fire(CsvSlotNames,event,null);}
	
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public static final Topic CsvSlotValues = newTopic(0);
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public void fireCsvSlotValues(BString event){fire(CsvSlotValues,event,null);}
	
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public static final Topic CsvSlotNameValuePairs = newTopic(0);
	/**This is fired every time outDelimitedSlotNames or outDelimitedSlotValues is updated.*/
	public void fireCsvSlotNameValuePairs(BString event){fire(CsvSlotNameValuePairs,event,null);}
	
	private static final BFacets statusForInvalidOrdsFacets =	BFacets.make(BFacets.FIELD_EDITOR, BString.make("kitControl:PropagateFlagsFE"));
	public BFacets getSlotFacets(Slot slot)
	{
		if (slot.getName().equals(statusForInvalidOrds)){ return statusForInvalidOrdsFacets;}
		else if(slot.isDynamic()){return getFacetsForDynamicSlots();}
		else {return super.getSlotFacets(slot);}
	}
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BDynamicLinks.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
	public static final Log logger = Log.getLog(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
	
	/**Represents the column number within the csv that contains the source ord path.*/
	static int colSourceOrd = 0;
	/**Represents the column number within the csv that contains the source slot name.*/
	static int colSourceSlotName = 1;
	/**Represents the column number within the csv that contains the name to be given to this component's slot name.*/
	static int colTargetSlotName = 2;	
	/**Represents the column number within the csv that contains the outgoing target ord path.*/
	static int colOutTargetOrd = 3;	
	/**Represents the column number within the csv that contains the target slot name to link out to.*/
	static int colOutTargetSlotName = 4;
	
	private 	String			div = "------------------------------------------------------------------------------------------------------------------------------";
	/*------------------------------------------------------------------------------------------------------------------------*/
	
	String [][] glblArrSlotInfo = new String[0][0];
	Clock.Ticket glblMidnightTimer;
	Clock.Ticket glblRefreshTimer;
	
	/**
	 * Represent 'this' component.
	 * <br>This just makes it easier to copy this source into a program object.
	 */
	final BComponent thisComp = this;
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void started() throws Exception
	{
		if(!Sys.atSteadyState() || !thisComp.isRunning()) return;
		//At this point, we know the object was just created (or copied).
		try {startupRoutine();}
		catch (Exception e) 
		{
			messageHandler(Level.SEVERE, "Exception error in method 'started()'.", e);
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void atSteadyState() throws Exception
	{
		if(!Sys.atSteadyState() || !thisComp.isRunning()) return;

		try
		{
			startupRoutine();
		}
		catch (Exception e) 
		{
			messageHandler(Level.SEVERE, "Exception error in method 'atSteadyState()'.", e);
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void stopped()
	{
		if (glblRefreshTimer != null) glblRefreshTimer.cancel();
		if(glblMidnightTimer != null) glblMidnightTimer.cancel();
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public void changed(Property p, Context cx)
	{
		if(!Sys.atSteadyState() || !thisComp.isRunning()) return;
		
		if(p.equals(slotInfoCsv) || p.equals(statusForInvalidOrds) || p.equals(useAreaZoneStation) || p.equals(enableLinks)) 
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
			for (int i = 0; i < glblArrSlotInfo.length; i++)
			{
				targetSlotName = glblArrSlotInfo[i][colTargetSlotName];
				if(targetSlotName != null)
				{
					if(targetSlotName.length() > 0)
					{
						try
						{
							if(thisComp.getProperty(targetSlotName).isDynamic())
							{
								thisComp.reorderToBottom(thisComp.getProperty(escape(targetSlotName)));
							}
						}
						catch (Exception e)
						{
							String msg = "Could not reorder slot: " + targetSlotName;
							if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
							
						}
					}
				}
			}
			
			makeDelimitedOutput();
			return;
		}
		
		if( p.equals(pathToEnableLinks) ) 
		{
			doPathToEnableLinks();
		}
		
		if( p.equals(enableDelimittedValues) ) 
		{
			makeDelimitedOutput();
			return;
		}
		
		//Make sure this is the last thing listed in this method.
		if( !p.equals(outDelimitedSlotNames) && !p.equals(outDelimitedSlotValues) ) 
		{
			makeDelimitedOutput();
			return;
		}
		
		
		
		
		
	}
	
	String escape(String s){return SlotPath.escape(s);}
	String unescape(String s){return SlotPath.unescape(s);}

	/**
	 * Converts a 'java.util.logging.Level' to a 'javax.baja.log.Log.severity' int value.</br>
	 * </br>
	 * <table>
	 * <tr><td>OFF</td><td>=</td><td>NONE</td><td>=</td><td>4</td></tr>
	 * <tr><td>SEVERE</td><td>=</td><td>ERROR</td><td>=</td><td>3</td></tr>
	 * <tr><td>WARNING</td><td>=</td><td>WARNING</td><td>=</td><td>2</td></tr>
	 * <tr><td>INFO</td><td>=</td><td>MESSAGE</td><td>=</td><td>1</td></tr>
	 * <tr><td>CONFIG</td><td>=</td><td>MESSAGE</td><td>=</td><td>1</td></tr>
	 * <tr><td>FINE</td><td>=</td><td>TRACE</td><td>=</td><td>0</td></tr>
	 * <tr><td>FINER</td><td>=</td><td>TRACE</td><td>=</td><td>0</td></tr>
	 * <tr><td>FINEST</td><td>=</td><td>TRACE</td><td>=</td><td>0</td></tr>
	 * <tr><td>ALL</td><td>=</td><td>TRACE</td><td>=</td><td>0</td></tr>
	 * </table>
	 * 
	 * @param level
	 * @return int
	 * 
	 * @since September 22, 2017
	 * @author Justin Koffler
	 */
	private int levelToInt(Level level)
	{
		int result = 4;
		String strLevel = level.getName().toString();
		
		if(strLevel.equalsIgnoreCase("OFF"))			{result = 4;}
		else if(strLevel.equalsIgnoreCase("SEVERE"))	{result = 3;}
		else if(strLevel.equalsIgnoreCase("WARNING"))	{result = 2;}
		else if(strLevel.equalsIgnoreCase("INFO"))		{result = 1;}
		else if(strLevel.equalsIgnoreCase("CONFIG"))	{result = 1;}
		else if(strLevel.equalsIgnoreCase("FINE"))		{result = 0;}
		else if(strLevel.equalsIgnoreCase("FINER"))		{result = 0;}
		else if(strLevel.equalsIgnoreCase("FINEST"))	{result = 0;}
		else if(strLevel.equalsIgnoreCase("ALL"))		{result = 0;}

		return result;
	}

	/**
	 * Handles creation of a log message in a more consistent manner.</br>
	 * 
	 * @param level - Level of log you want to create.
	 * @param msg - String representing the message you want to prefix the exception message.
	 * @param e - Exception from your catch.
	 * 
	 * @since September 22, 2017
	 * @author Justin Koffler
	 */
	private void messageHandler(Level level, String msg, Exception e)
	{
		msg	= msg + "\n" + "MESSAGE: \n" + e.getMessage().trim() + "\n" + "STACKTRACE: \n" + e.getStackTrace().toString().trim();
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		msg = "\n" + msg.trim() + "\n" + "PRINTSTACKTRACE: \n" + errors.toString().trim();
		messageHandler( level, msg );
	}

	/**
	 * 
	 * @param level - Level of log you want to create.
	 * @param msg - String representing the message you want create a log entry with.
	 * 
	 * @since September 22, 2017
	 * @author Justin Koffler
	 */
	private void messageHandler(Level level, String msg)
	{
		msg = "\t" + thisComp.getSlotPath() + "\t" + msg;
		if(getInDebug())
		{
			System.out.println(msg);
		}
		else
		{
			logger.log(levelToInt(level), msg, null);
		}
	}

	
	//----------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Finds the correct converter to use between two disparate types.</br>
	 * 
	 * @param typeFrom
	 * @param typeTo
	 * @return BConverter 
	 * @since September 22, 2017
	 * @author Justin Koffler
	 */
	private BConverter findConverter(Type typeFrom, Type typeTo)
	{
		BConverter converter = null;
		try
		{
			Registry	registry	= Sys.getRegistry();
			TypeInfo[]	adapters	= registry.getAdapters(typeFrom.getTypeInfo(), typeTo.getTypeInfo());
			
			for (int i = adapters.length - 1; i >= 0; i--)
			{
				messageHandler(Level.FINEST,  adapters[i].getInstance().getTypeDisplayName(null) );
				if ( registry.isAgent( adapters[i], BConversionLink.TYPE.getTypeInfo() ) )
				{
					messageHandler(Level.FINEST, "Found the correct converter: '" + (BConverter) adapters[i].getInstance() + "'" );
					converter = (BConverter) adapters[i].getInstance();
					return converter;
				}
			}
		}
		catch (Exception e)
		{
			messageHandler(Level.FINEST, "Exception in method 'findConverter()'.", e);
		}
		
		return converter;
	}
	
		
	/*------------------------------------------------------------------------------------------------------------------------*/  
	/**
	 * Creates link to the component specified in slot 'PathToEnableLinks' which will be linked to this component's 'enableLinks' slot.</br>
	 * 
	 * @since September 22, 2017
	 * @author Justin Koffler
	 */
	private void doPathToEnableLinks()
	{
		if( getPathToEnableLinks().length() > 0 && !getPathToEnableLinks().trim().equalsIgnoreCase(pathToEnableLinks.getDefaultValue().toString()) )
		{
			String[]	pathParts				= getPathToEnableLinks().split(",");
			
			if(pathParts.length == 2)
			{
				removeLinkToEnableLinks();
				
				String 		sourceOrdString 		= pathParts[0];
				String 		sourceSlotName 			= pathParts[1];
				String 		targetSlotName 			= "enableLinks";
				BOrd 		sourceOrd 				= null;
				BComponent 	sourceComp 				= null;
				
				try
				{
					try
					{
						sourceOrd = BOrd.make(BFormat.make(sourceOrdString).format(thisComp));
						if( isOrdValid(sourceOrd) )
						{
							sourceComp = (BComponent)sourceOrd.relativizeToHost().get();
						}
					}
					catch(Exception e)
					{
						messageHandler(Level.FINEST, "Exception in method 'doPathToEnableLinks()'.", e);
					}
					
					if( !isOrdValid(sourceOrd) )
					{
						try
						{
							sourceOrd = BOrd.make(BFormat.make("station:|" + sourceOrdString).format(thisComp));
							if(isOrdValid(sourceOrd))
							{
								sourceComp = (BComponent)sourceOrd.relativizeToHost().get();
							}
						}
						catch(Exception e)
						{
							messageHandler(Level.FINEST, "Exception in method 'doPathToEnableLinks()'.", e);
						}
					}
				}
				catch(Exception e)
				{
					messageHandler(Level.FINEST, "Exception in method 'doPathToEnableLinks()'.", e);
				}
				
				
				if( isOrdValid(sourceOrd) )
				{
					Type srcType = sourceComp.get(sourceSlotName).getType();
					Type dstType = thisComp.get(targetSlotName).getType();
					
					if(srcType.is(dstType))
					{
						BLink link = new BLink(sourceComp.getHandleOrd(),sourceSlotName,targetSlotName,true);
						thisComp.add(null, link);
					}
					else
					{
						BConverter converter = findConverter(srcType,dstType);
						
						if( !converter.isNull() )
						{
							BConversionLink cLink = new BConversionLink(sourceComp.getHandleOrd(),sourceSlotName,targetSlotName,true,findConverter(srcType,dstType) );
							thisComp.add(null, cLink);
						}
						else
						{
							messageHandler(Level.FINEST, "Could not determine the converter from type '" + srcType + "', to type '" + dstType + "', link was not created.");
						}
					}
				}
			}
			else
			{
				//You done gone and messed up boy!
			}
		}
		else if( getPathToEnableLinks().trim().equalsIgnoreCase(pathToEnableLinks.getDefaultValue().toString()) )
		{
			//Meh, nothing to do.
		}
		else
		{
			removeLinkToEnableLinks();
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * Removes the all links to slot 'enableLinks' on this component.
	 * 
	 * @since September 22, 2017
	 * @author Justin Koffler
	 */
	private void removeLinkToEnableLinks()
	{
		try
		{
			BLink[] links = this.getLinks(enableLinks);
			if (links.length > 0)
			{
				for (int i = 0; i < links.length; i++)
				{
					this.remove(links[i].getName());
				}
			}
		}
		catch (Exception e)
		{
			messageHandler(Level.SEVERE, "Exception in removeLinkToEnableLinks() method!", e);
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * Removes the link to all dynamic slots on this component.
	 * 
	 * @since September 20, 2017
	 * @author Justin Koffler
	 */
	private void removeLinks()
	{
		try
		{
			BLink[] links = this.getLinks();
			for (int i = 0; i < links.length; i++)
			{
				if ((this.getSlot(links[i].getTargetSlot().getName()).isDynamic()) == true)
				{
					this.remove(links[i].getName());
				}

			}
			updateSlotStatus();
		}
		catch (Exception e)
		{
			messageHandler(Level.SEVERE, "Exception in removeLinks() method!", e);
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * Updates all dynamic slots status to status from slot 'statusForInvalidOrds'.</br></br>
	 * <i>&nbsp&nbsp&nbsp&nbsp(This logic was copied and modified from the 'doRefreshLinks()' method.)</i>
	 * 
	 * @since September 20, 2017
	 * @author Justin Koffler
	 */
	private void updateSlotStatus()
	{
		try
		{
			Property[] properties = this.getDynamicPropertiesArray();
			
			for(int i=0; i<properties.length; i++)
			{
				
				BObject 	targetSlotAsObject 			= null;
				BValue 		targetSlotAsValue 			= null;
				boolean 	targetSlotIsStatusValue 	= false;
				String 		targetSlotName 				= properties[i].getName().toString();
						
				try
				{
					targetSlotAsObject = ((BObject) thisComp.get(targetSlotName));
					
					if(targetSlotAsObject != null)
					{
						if(targetSlotAsObject instanceof BIStatusValue)
						{
							targetSlotIsStatusValue = true;
						}
					}
				}
				catch (Exception e){}
				
				try
				{
					targetSlotAsValue = thisComp.get(targetSlotName);
				}
				catch (Exception e){}

				
				if(targetSlotIsStatusValue)
				{
					if(targetSlotAsValue != null)
					{
						if(targetSlotAsValue instanceof BStatusBoolean)
						{
							thisComp.set(targetSlotName, new BStatusBoolean(false, getStatusForInvalidOrds()));
						}
						else if(targetSlotAsValue instanceof BStatusNumeric)
						{
							thisComp.set(targetSlotName, new BStatusNumeric(0, getStatusForInvalidOrds()));
						}
						else if(targetSlotAsValue instanceof BStatusEnum)
						{
							thisComp.set(targetSlotName, new BStatusEnum(BDynamicEnum.DEFAULT, getStatusForInvalidOrds()));
						}
						else if(targetSlotAsValue instanceof BStatusString)
						{
							thisComp.set(targetSlotName, new BStatusString("", getStatusForInvalidOrds()));
						}
					}
					
					try {((BStatusValue) ((BObject) thisComp.get(targetSlotName))).setStatus(getStatusForInvalidOrds());}
					catch (Exception e){}
				}
			}
		}
		catch (Exception e)
		{
			messageHandler(Level.FINEST, "Exception in updateSlotStatus() method!", e);
		}
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * Creates the delimited string output.
	 * 
	 * @since May 4, 2017
	 * @author Justin Koffler
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
					if(getInDelimiter().length() >= 2)
					{
						if(getInDelimiter().substring(0, 2).toString().equalsIgnoreCase("\\u"))
						{
							String		tempDelim	= getInDelimiter();
							tempDelim 				= replaceString(tempDelim, "\\", "");
							String[] 	arr 		= split(tempDelim, "u");
							delim 					= "";
							for(int i = 1; i < arr.length; i++)
							{
								int hexVal = Integer.parseInt(arr[i], 16);
								delim += (char)hexVal;
							}
						}
					}
				}
				catch (Exception e){}
				
				
				//Pairs Delimiter Setup..........
				String parsDelim = getInPairsDelimiter();
				try
				{
					if(getInPairsDelimiter().length() >= 2)
					{
						if(getInPairsDelimiter().substring(0, 2).toString().equalsIgnoreCase("\\u"))
						{
							String 		tempPairsDelim 	= getInPairsDelimiter();
							tempPairsDelim 				= replaceString(tempPairsDelim, "\\", "");
							String[] 	arr 			= split(tempPairsDelim, "u");
							parsDelim 					= "";
							for(int i = 1; i < arr.length; i++)
							{
								int hexVal = Integer.parseInt(arr[i], 16);
								parsDelim += (char)hexVal;
							}
						}
					}
				}
				catch (Exception e){}
				
				
				//Iterate through each slot and build our delimited values...
				Property[] dyProps = thisComp.getDynamicPropertiesArray();
				
				int item = 0;
				
				for(int i = 0; i < dyProps.length; i++)
				{
					Property property = dyProps[i];
					
					if(!property.isAction() && !property.isTopic() && !property.getType().is(BLink.TYPE) && !property.getType().is(BWsAnnotation.TYPE) && !property.getType().is(BStatus.TYPE))
					{
						String name = unescape(property.getName());
						String value = "";
						
						if(property.getType().toString().toUpperCase().indexOf("STATUS") > -1)
						{
							BStatusValue sv = (BStatusValue) get(property).asValue();
							value = sv.getValueValue().toString();
						}
						else{value = get(property).asValue().toString();}
						
						messageHandler( Level.FINE, "slotName: '" + name + "', slotValue: '" + value + "', Type: " + property.getType().toString() );
		
						//if(csvNames.length()<=0){csvNames = name;}
						if(item<=0){csvNames = name;}
						else{csvNames = csvNames + delim + name;}
		
						//if(csvValues.length()<=0){csvValues = value;}
						if(item<=0){csvValues = value;}
						else{csvValues = csvValues + delim + value;}
						
						//if(pairs.length()<=0){pairs = name + parsDelim + value;}
						if(item<=0){pairs = name + parsDelim + value;}
						else{pairs = pairs + delim + name + parsDelim + value;}
						
						item++;
					}
					else
					{
						messageHandler( Level.FINE, "'" + property.getName() + "' is type: '" + property.getType() + "' and will not be included in csv." );
					}
				}

				messageHandler( Level.FINE, "\n" + "Names:\n" + csvNames + "\nValues:\n" + csvValues );
				
				setOutDelimitedSlotNames(csvNames);
				setOutDelimitedSlotValues(csvValues);
				setOutDelimitedSlotNameValuePairs(pairs);
				fireCsvSlotNames(BString.make(csvNames));
				fireCsvSlotValues(BString.make(csvValues));
				fireCsvSlotNameValuePairs(BString.make(pairs));
			}
			catch (Exception e)
			{
				messageHandler(Level.SEVERE, "Exception in makeCsvOutput() method!", e);
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
		if( getEnableLinks()==true )
		{
			scheduleMidnightTimer();
			updateTimer();
		}
		doRefreshLinks();
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	void updateTimer()
	{
		try
		{
			if (glblRefreshTimer != null) glblRefreshTimer.cancel();
			if(getRefreshInterval().getSeconds() > 0) glblRefreshTimer = Clock.schedulePeriodically(thisComp, getRefreshInterval(), refreshLinks, null);
		}
		catch (Exception e) 
		{
			messageHandler(Level.SEVERE, "Exception in updateTimer() method!", e);
		}
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private void scheduleMidnightTimer()
	{
		try
		{
			if(glblMidnightTimer != null) glblMidnightTimer.cancel();
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
				glblMidnightTimer = Clock.schedule(thisComp, nextMidnight, midnightTimerExpired, null);
			}
		}
		catch (Exception e) 
		{
			messageHandler(Level.SEVERE, "Exception in scheduleMidnightTimer() method!", e);
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
		messageHandler( Level.FINEST, "\t" + "doRefreshLinks() method called.");
		
		if(!Sys.atSteadyState() || !thisComp.isRunning()) return;
		
		if( getEnableLinks()==true )
		{
			if(getSlotInfoCsv().length() < 4)
			{
				messageHandler(Level.SEVERE, "Invalid CSV string! Please read the DymanicLinks Bajadoc!");
				return;
			}
			
			String[][] strOrds;
	
			messageHandler( Level.FINEST, "\t" + "doRefreshLinks(), getSlotInfoCsv():                                       '" + getSlotInfoCsv() + "'");
			messageHandler( Level.FINEST, "\t" + "doRefreshLinks(), BFormat.make(getSlotInfoCsv()):                         '" + BFormat.make(getSlotInfoCsv()) + "'");
			messageHandler( Level.FINEST, "\t" + "doRefreshLinks(), BFormat.make(getSlotInfoCsv()).format(destinationComp): '" + BFormat.make(getSlotInfoCsv()).format(thisComp) + "'");
			
			
			try {strOrds = split(BFormat.make(getSlotInfoCsv()).format(thisComp), "\n", ",");}
			catch (Exception e)
			{
				String msg = "Could not parse CSV string!";
				if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
				return;
			}
			
			debugStrOrds(strOrds);
			
			//If the CSV input is less than 3 columns, exit gracefully with an error in the application manager.
			if(strOrds[0].length < 3)
			{
				messageHandler(Level.SEVERE, "Invalid number of fields provided in CSV string! Please read the DymanicLinks Bajadoc!");
				return;
			}
	
			if(strOrds.length < 1) return;
			boolean validLinks = false;
			
			for (int i = 0; i < strOrds.length; i ++)
			{
				validLinks 							= false;
				String 		sourceFormatOrd 		= strOrds[i][colSourceOrd];
				String 		sourceSlotName 			= strOrds[i][colSourceSlotName];
				String 		targetSlotName 			= strOrds[i][colTargetSlotName];
				
				String		outgoingTrgFormatOrd 	= null;
				String		outgoingTrgSlotName	 	= null;
				String		outgoingSrcSlotName		= null;
				
				BOrd 		sourceOrd 				= null;
				BComponent 	sourceComp 				= null;
				BValue 		sourceBValue 			= null;
				Slot 		sourceSlot 				= null;
				boolean 	slotAdded 				= false;
				boolean 	sourceIsActionOrTopic 	= false;
				boolean 	renamedOldSlot 			= false;
				BLink[] 	links;
				boolean 	invalidSourceOrd 		= false;
				boolean 	linkAdded 				= false;
				
				messageHandler( Level.FINEST, "\t" + "doRefreshLinks(), formatOrd: '" + sourceFormatOrd + "'");
				
				
				boolean formatOrdBad 		= true;
				boolean targetSlotNameBad 	= true;
				
				try{formatOrdBad 		= (sourceFormatOrd == null 	|| sourceFormatOrd.trim().length()<=0);}catch(Exception e) {}
				try{targetSlotNameBad	= (targetSlotName == null 	|| targetSlotName.trim().length()<=0);}catch(Exception e) {}
				
				if( formatOrdBad || targetSlotNameBad )
				{
					try
					{
						if (strOrds[i].length == 5)
						{
							messageHandler(Level.FINEST, "doRefreshLinks(), no incoming link, only outgoing...");
							
							if (strOrds[i][colOutTargetOrd] != null && strOrds[i][colOutTargetSlotName] != null && targetSlotName != null)
							{
								outgoingSrcSlotName 	= escape(targetSlotName.trim());
								outgoingTrgFormatOrd 	= strOrds[i][colOutTargetOrd];
								outgoingTrgSlotName 	= strOrds[i][colOutTargetSlotName];
								createOutgoingLink(outgoingSrcSlotName, outgoingTrgFormatOrd, outgoingTrgSlotName);
								continue;
							}
							else
							{
								messageHandler(Level.FINEST, "doRefreshLinks(), strOrds["+i+"].length: "+strOrds[i].length+", something was null");
								continue;
							}
						}
						else
						{
							messageHandler(Level.FINEST, "doRefreshLinks(), no incoming nor outgoing links");
							continue;
						} 
					}
					catch (Exception e)
					{
						messageHandler(Level.FINEST, "doRefreshLinks(), if(formatOrd == null || targetSlotName == null)", e);
						continue;
					}
				}
				else
				{
					messageHandler(Level.FINEST, "doRefreshLinks(), NOT NULL formatOrd: '"+sourceFormatOrd+"', targetSlotName: '"+targetSlotName+"'");
				}
				
				sourceFormatOrd = sourceFormatOrd.trim();
				
				if(sourceSlotName != null)
				{
					sourceSlotName = sourceSlotName.trim();
				}
				
				targetSlotName = escape(targetSlotName);
				
				if(sourceFormatOrd.length() > 0 && sourceSlotName.length() > 0)
				{
					try
					{
						sourceOrd = BOrd.make(BFormat.make(sourceFormatOrd).format(thisComp));
						if(isOrdValid(sourceOrd))
						{
							sourceComp = (BComponent)sourceOrd.relativizeToHost().get();
							sourceSlot = sourceComp.getSlot(sourceSlotName);
							sourceIsActionOrTopic = sourceSlot.isAction() || sourceSlot.isTopic();
							if(!sourceIsActionOrTopic) sourceBValue = sourceComp.get(sourceSlotName);
							else if(sourceSlot.isAction()) sourceBValue = new BCompositeAction();
							else if(sourceSlot.isTopic()) sourceBValue = new BCompositeTopic();
						}
						else
						{
							try
							{
								sourceOrd = BOrd.make(BFormat.make("station:|" + sourceFormatOrd).format(thisComp));
								if(isOrdValid(sourceOrd))
								{
									sourceComp = (BComponent)sourceOrd.relativizeToHost().get();
									sourceSlot = sourceComp.getSlot(sourceSlotName);
									sourceIsActionOrTopic = sourceSlot.isAction() || sourceSlot.isTopic();
									if(!sourceIsActionOrTopic) sourceBValue = sourceComp.get(sourceSlotName);
									else if(sourceSlot.isAction()) sourceBValue = new BCompositeAction();
									else if(sourceSlot.isTopic()) sourceBValue = new BCompositeTopic();
								}
								else
								{
									invalidSourceOrd = true;
									if(!getIgnoreMissingObjects())
									{
										messageHandler(Level.SEVERE, "Could not resolve ord: '" + sourceOrd + "', Source Slot Name: '" + sourceSlotName + "'");
									}
									else
									{
										messageHandler(Level.FINE, "Target slot missing and invalid source ord: '" + sourceOrd + "', Source Slot Name: '" + sourceSlotName + "'");
									}
								}
							}
							catch (Exception e)
							{
								invalidSourceOrd = true;
								if(!getIgnoreMissingObjects())
								{
									String msg = "Could not retrieve ord/slot details, ord: '" + sourceOrd + "', Source Slot Name: '" + sourceSlotName + "'";
									if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
								}
							}
						}
					}
					catch (Exception e)
					{
						invalidSourceOrd = true;
						if(!getIgnoreMissingObjects())
						{
							String msg = "Could not retrieve ord/slot details, ord: '" + sourceOrd + "', Source Slot Name: '" + sourceSlotName + "'";
							if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
							continue;
						}
					}
				}
				else
				{
					sourceBValue = new BStatusString();
				}
				
				String msgSuffix = "";
				try{msgSuffix = "Source Type: '" + sourceBValue.getTypeDisplayName(null)+"'";} catch (Exception e){}
				messageHandler(Level.FINE, "Ord: '" + sourceOrd + "' Source Slot Name: " + sourceSlotName + msgSuffix );
				
				
				//Check to see if the target slot needs to be renamed
				try
				{
					if(((BObject) thisComp.get(targetSlotName))==null)
					{
						messageHandler(Level.FINE, "Target slot name not found: " + targetSlotName);
						for (int j = 0; j < glblArrSlotInfo.length; j++)
						{
							String oldFormatOrd = glblArrSlotInfo[j][colSourceOrd];
							String oldSourceSlotName = glblArrSlotInfo[j][colSourceSlotName];
							String oldTargetSlotName = glblArrSlotInfo[j][colTargetSlotName];
							
							if(oldTargetSlotName == null || oldFormatOrd == null) continue;
							if(oldTargetSlotName.length() == 0 || oldFormatOrd.length() == 0) continue;
							
							if(sourceFormatOrd.equals(oldFormatOrd) && sourceSlotName.equals(oldSourceSlotName))
							{
								oldTargetSlotName = escape(oldTargetSlotName);
								if(((BObject) thisComp.get(oldTargetSlotName)) != null)
								{
									try
									{
										links = thisComp.getLinks(thisComp.getSlot(oldTargetSlotName));
										Knob[] knobs = thisComp.getKnobs(thisComp.getSlot(oldTargetSlotName));
										
										messageHandler(Level.FINE, "Renaming slot from " + oldTargetSlotName + " to " + targetSlotName);
										thisComp.rename(thisComp.getProperty(oldTargetSlotName), targetSlotName);
										renamedOldSlot = true;
										
										if(links.length>0)
										{
											for (int k = 0; k < links.length; k++)
											{
												//If the source slot is blank, this should be a BQL query and not a linked slot, so remove the link.
												if(sourceSlotName.length() == 0) thisComp.remove(links[k]);
												
												//If there is a source slot listed, then update any links to the new name.	There should only be 1
												//link here, unless the slot is a topic or event, in which case someone might have manually linked
												//something else to the slot.
												else
												{
													if(links[k].getTargetSlotName().equalsIgnoreCase(oldTargetSlotName))
													{
														messageHandler(Level.FINE, " - Setting target on link#" + k + " from " + oldTargetSlotName + " to " + targetSlotName);
														links[k].setTargetSlotName(targetSlotName);
														validLinks = true;
													}
													else 
													{
														messageHandler(Level.FINE, " - Did not update target on link#" + k + ": " + links[k].getTargetSlotName() + " != " + oldTargetSlotName);
													}
												}
											}
										}
										
										
										if(knobs.length>0)
										{
											for (int k = 0; k < knobs.length; k++)
											{
												if(knobs[k].getSourceSlotName().equalsIgnoreCase(oldTargetSlotName))
												{
													messageHandler(Level.FINE, " - Setting source on knob#" + k + " from " + oldTargetSlotName + " to " + targetSlotName);
													knobs[k].getLink().setSourceSlotName(targetSlotName);
												}
												else messageHandler(Level.FINE, " - Did not update source on knob#" + k + ": " + knobs[k].getSourceSlotName() + " != " + oldTargetSlotName);
											}
										}
									}
									catch (Exception e)
									{
										String msg = "Could not rename old slot: " + oldTargetSlotName + " to: " + targetSlotName;
										if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
									}
								}
								break;
							}
						}
						
						//Add the new target slot, if needed
						if(!renamedOldSlot && !invalidSourceOrd)
						{
							messageHandler(Level.FINE, "Adding new slot name: " + targetSlotName);
							thisComp.add(targetSlotName, sourceBValue.newCopy(), Flags.SUMMARY, BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)), null);
							slotAdded = true;
						}
					}
				}
				catch (Exception e)
				{
					String msg = "Could not create new slot: " + targetSlotName;
					if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
					continue;
				}
			
				if(!slotAdded && !invalidSourceOrd)
				{
					Type sourceSlotType = null;
					Type targetSlotType = null;
					
					try {sourceSlotType = sourceBValue.getType();}
					catch (Exception e)
					{
						String msg = "Could not read target slot type for: " + sourceOrd + ", slot name: " + sourceSlotName;
						if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
						continue;
					}
					
					try {targetSlotType = ((BObject) thisComp.get(targetSlotName)).getType();}
					catch (Exception e)
					{
						String msg = "Could not read source slot type for: " + targetSlotName;
						if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
						continue;
					}
					
					if(sourceSlotType != targetSlotType)
					{
						try {thisComp.remove(targetSlotName);}
						catch (Exception e)
						{
							String msg = "Could not remove slot: " + targetSlotName + " (this slot is the incorrect type)";
							if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
							continue;
						}
						
						try {thisComp.add(targetSlotName, sourceBValue.newCopy(), Flags.SUMMARY, BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)), null);}
						catch (Exception e)
						{
							String msg = "Could not create new slot: " + targetSlotName;
							if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
							continue;
						}
					}
				}
				
				if(getReorderSlotsBasedOnCsvString())
				{
					try
					{
						if(thisComp.getProperty(targetSlotName).isDynamic())
						{
							messageHandler(Level.FINE, "\t" + "doRefreshLinks(), Reordering slot: " + targetSlotName);
							thisComp.reorderToBottom(thisComp.getProperty(targetSlotName));
						}
					}
					catch (Exception e)
					{
						String msg = "Could not reorder slot: " + targetSlotName;
						if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
					}
				}
				

				
			
			
			
			
			
				try
				{
					links = thisComp.getLinks(thisComp.getSlot(targetSlotName));
					
					//If the source slot name is blank, resolve the format and write it to the target slot
					if(sourceSlotName.length() == 0)
					{
						if(links.length>0)
						{
							messageHandler(Level.FINE, "Removing link from: " + targetSlotName);
							thisComp.remove(links[0]);
						}
						
						messageHandler(Level.FINE, "Setting static value to : " + targetSlotName);
						try
						{
							((BStatusString) ((BObject) thisComp.get(targetSlotName))).setValue(BFormat.make(sourceFormatOrd).format(thisComp));
							
							
							if( !(((BStatusValue) ((BObject) thisComp.get(targetSlotName))).getStatus().isValid()) )
							{
								((BStatusValue) ((BObject) thisComp.get(targetSlotName))).setStatus( BStatus.ok  );
							}
							
							
						}
						catch (Exception e)
						{
							String msg = "Could not resolve format: " + targetSlotName;
							if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
						}
						continue;
					}
					
					if(links.length>0)
					{
						//will only alter link 0, not meant as a many to 1!!!
						//try to make ord from input link string
						if(isOrdValid(sourceOrd))
						{
							boolean ordMismatch = !sourceComp.getHandleOrd().toString().equalsIgnoreCase(links[0].getSourceOrd().toString());
							boolean slotNameMismatch = !sourceSlotName.equals(links[0].getSourceSlotName());
							
							if(ordMismatch && slotNameMismatch) links[0].setEnabled(false);
							
							if(ordMismatch)
							{
								messageHandler(Level.FINE, "Setting link source ord for target slot: " + targetSlotName);
								if(links[0].isActive()) links[0].deactivate();
								links[0].setSourceOrd(sourceComp.getHandleOrd());
							}
							
							if(slotNameMismatch)
							{
								messageHandler(Level.FINE, "Setting link source slot name for target slot: " + targetSlotName);
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
							messageHandler(Level.FINE, "Invalid ord, removing link on target slot: " + targetSlotName);
							thisComp.remove(links[0]);
						}
					}
					else
					{
						//no link, create one if possible
						if(isOrdValid(sourceOrd))
						{
							messageHandler(Level.FINE, "Link not found, creating a new link for target slot: " + targetSlotName);
							BLink link = new BLink(sourceComp.getHandleOrd(),sourceSlotName,targetSlotName,true);
							thisComp.add(null, link);
							validLinks = true;
							linkAdded = true;
						}
						else
						{
							messageHandler(Level.FINE, "Invalid source ord specified for target slot: " + targetSlotName);
							if(!invalidSourceOrd) messageHandler(Level.SEVERE, " - Invalid ord: " + sourceOrd);
							validLinks = false;
						}
					}
				}
				catch (Exception e)
				{
					String msg = "Link create/modify error: " + sourceOrd + ", source slot: " + sourceSlotName	+ ", Target slot:" + targetSlotName;
					if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
					validLinks = false;
				}
				
				
				updateValues(targetSlotName, validLinks, linkAdded, sourceIsActionOrTopic, sourceSlotName, sourceFormatOrd, sourceOrd, sourceComp);
				
				
				try
				{
					if (strOrds[i].length == 5)
					{
						outgoingSrcSlotName = targetSlotName;
						outgoingTrgFormatOrd = strOrds[i][colOutTargetOrd];
						outgoingTrgSlotName = strOrds[i][colOutTargetSlotName];
						createOutgoingLink(outgoingSrcSlotName, outgoingTrgFormatOrd, outgoingTrgSlotName);
					} 
				}
				catch (Exception e)
				{
					messageHandler(Level.FINE, "doRefreshLinks(), creating outgoing links.", e);
				}
				
			}
			// END OF MAIN FOR LOOP

			
			
			
			
			cleanupUnusedSlots(strOrds);
			
			fireLinksRefreshed(BBoolean.make(true));
		
			makeDelimitedOutput();
		
		}
		else
		{
			removeLinks();
			updateSlotStatus();
			makeDelimitedOutput();
		}
	}
	
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private void updateValues(String inTargetSlotName, boolean inValidLinks, boolean inLinkAdded, boolean inSourceIsActionOrTopic
	                          , String inSourceSlotName, String inFormatOrd, BOrd inOrd, BComponent inComp)
	{
		try
		{
			boolean newCodeFailed = true;
			
			/**
			 * TODO: This doesn't work.	It is supposed to retrieve the default 
			 * value for the slot and set that value, but getDefaultValue() only 
			 * works for frozen properties.
			 * 
			 * The code is commented out because there is no point to trying here.
			 */
//			if(!validLinks && !sourceIsActionOrTopic)
//			{
//				messageHandler(Level.FINE, "Link is not valid on target: " + targetSlotName + "; attempting to set value to default");
//				try
//				{
//					destinationComp.set(targetSlotName, destinationComp.getProperty(targetSlotName).getDeclaringType().getTypeSpec().asValue());
//					destinationComp.set(targetSlotName, destinationComp.getProperty(targetSlotName).getDefaultValue());
//					newCodeFailed = false;
//				}
//				catch (Exception e)
//				{
//					newCodeFailed = true;
//					messageHandler(Level.FINE, " - Could not get default value (using NEW code) for: " + targetSlotName);
//					messageHandler(Level.FINE, e.getMessage());
//					if(logger.isTraceOn()) e.printStackTrace();
//				}
//			}
						
			BObject targetSlotAsObject = null;
			BValue targetSlotAsValue = null;
			boolean targetSlotIsStatusValue = false;
			try
			{
				targetSlotAsObject = ((BObject) thisComp.get(inTargetSlotName));
				if(targetSlotAsObject != null)
					if(targetSlotAsObject instanceof BIStatusValue)
						targetSlotIsStatusValue = true;
			}
			catch (Exception e){}
			
			try
			{
				targetSlotAsValue = thisComp.get(inTargetSlotName);
			}
			catch (Exception e){}

			
			if(targetSlotIsStatusValue)
			{
				if(inValidLinks)
				{
					if(inLinkAdded && !inSourceIsActionOrTopic)
					{
						messageHandler(Level.FINE, "Copying status from source slot to target slot: " + inTargetSlotName);
						try {((BStatusValue) ((BObject) thisComp.get(inTargetSlotName))).setStatus(((BStatusValue)((BObject) inComp.get(inSourceSlotName))).getStatus());}
						catch (Exception e)
						{
							String msg = "Could not read source slot status, Format: '" + inFormatOrd + "', Ord: '" + inOrd + "', Source Slot Name: '" + inSourceSlotName + "', Target Slot Name: '" + inTargetSlotName + "'" ;
							if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
						}
					}
				}
				else
				{
					if(inSourceSlotName.length() == 0)
					{
						messageHandler(Level.FINE, "Target slot is a static value; setting status to OK for slot: " + inTargetSlotName);
						try {((BStatusValue) ((BObject) thisComp.get(inTargetSlotName))).setStatus(0);}
						catch (Exception e)
						{
							String msg = "Source slot name is blank, so target slot should be a BStatusString, but failed to change the status to OK, Format: '" + inFormatOrd + "', Ord: '" + inOrd + "', Target Slot Name: '" + inTargetSlotName + "'";
							if(logger.isTraceOn()){messageHandler(Level.FINE, msg, e);}else{messageHandler(Level.SEVERE, msg);}
						}
					}
					else
					{
						//TODO: Remove this once the code above actually works
						if(newCodeFailed && targetSlotAsValue != null)
						{
							if(targetSlotAsValue instanceof BStatusBoolean)
								thisComp.set(inTargetSlotName, new BStatusBoolean(false, getStatusForInvalidOrds()));
							else if(targetSlotAsValue instanceof BStatusNumeric)
								thisComp.set(inTargetSlotName, new BStatusNumeric(0, getStatusForInvalidOrds()));
							else if(targetSlotAsValue instanceof BStatusEnum)
								thisComp.set(inTargetSlotName, new BStatusEnum(BDynamicEnum.DEFAULT, getStatusForInvalidOrds()));
							else if(targetSlotAsValue instanceof BStatusString)
								thisComp.set(inTargetSlotName, new BStatusString("", getStatusForInvalidOrds()));
						}
						
						messageHandler(Level.FINE, "Link is not valid on target: " + inTargetSlotName + "; attempting to set status to: " + getStatusForInvalidOrds().flagsToString(null));
						try {((BStatusValue) ((BObject) thisComp.get(inTargetSlotName))).setStatus(getStatusForInvalidOrds());}
						catch (Exception e){}
					}
				}
			}
			
			//TODO: Remove this once the code above actually works
			else if(!inValidLinks && newCodeFailed && targetSlotAsValue != null)
			{
				if(targetSlotAsValue instanceof BBoolean)
					thisComp.set(inTargetSlotName, BBoolean.DEFAULT);
				else if(targetSlotAsValue instanceof BInteger)
					thisComp.set(inTargetSlotName, BInteger.DEFAULT);
				else if(targetSlotAsValue instanceof BDouble)
					thisComp.set(inTargetSlotName, BDouble.DEFAULT);
				else if(targetSlotAsValue instanceof BString)
					thisComp.set(inTargetSlotName, BString.DEFAULT);
				else if(targetSlotAsValue instanceof BLong)
					thisComp.set(inTargetSlotName, BLong.DEFAULT);
				else if(targetSlotAsValue instanceof BFloat)
					thisComp.set(inTargetSlotName, BFloat.DEFAULT);
				else if(targetSlotAsValue instanceof BAbsTime)
					thisComp.set(inTargetSlotName, BAbsTime.DEFAULT);
				else if(targetSlotAsValue instanceof BRelTime)
					thisComp.set(inTargetSlotName, BRelTime.DEFAULT);
			}
		}
		catch (Exception e)
		{
			messageHandler(Level.FINEST, "updateValues()", e);
		}
	}
	
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private void cleanupUnusedSlots(String[][] inStrOrds)
	{
		try
		{
			for (int i = 0; i < glblArrSlotInfo.length; i++)
			{
				String oldTargetSlotName = glblArrSlotInfo[i][colTargetSlotName];
				if (oldTargetSlotName == null)
				{
					continue;
				}
				
				oldTargetSlotName = escape(oldTargetSlotName);
				boolean foundSlot = false;
				
				if (inStrOrds.length > i)
				{
					String newTargetSlotName = inStrOrds[i][colTargetSlotName];
					if (newTargetSlotName != null)
					{
						if (oldTargetSlotName.equals(escape(newTargetSlotName)))
						{
							foundSlot = true;
						}
					}
				}
				
				if (!foundSlot)
				{
					for (int j = 0; j < inStrOrds.length; j++)
					{
						String newTargetSlotName = inStrOrds[j][colTargetSlotName];
						
						if (newTargetSlotName != null)
						{
							if (oldTargetSlotName.equals(escape(newTargetSlotName)))
							{
								foundSlot = true;
								break;
							}
						}
					}
				}
				
				if (!foundSlot && ((BObject) thisComp.get(oldTargetSlotName)) != null)
				{
					messageHandler(Level.FINEST, "cleanupUnusedSlots(), Removing unused slot: " + oldTargetSlotName);
					try
					{
						thisComp.remove(oldTargetSlotName);
					}
					catch (Exception e)
					{
						String msg = "cleanupUnusedSlots(), Could not remove unnecessary slot: " + oldTargetSlotName;
						messageHandler(Level.FINEST, msg, e);
						continue;
					}
				}
			}
			
			glblArrSlotInfo = inStrOrds;
		}
		catch (Exception e)
		{
			messageHandler(Level.FINEST, "cleanupUnusedSlots()", e);
		}
	}
	
	
	
	
	
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * SOURCE = IS THIS COMPONENT</br>
	 * TARGET = OTHER COMPONENT WE'RE GOING OUT TO</br>
	 * 
	 * @param inSourceSlotName This component's slot name
	 * @param inTargetOrdStr Ord string of component we want to link OUT to.
	 * @param inTargetSlotName Slot name on the other component.
	 */
	private void createOutgoingLink(String inSourceSlotName, String inTargetOrdStr, String inTargetSlotName)
	{
		messageHandler(Level.FINEST, "");
		messageHandler(Level.FINEST, div);
		messageHandler(Level.FINEST, "createOutgoingLink(), method called with inSourceSlotName: '" + inSourceSlotName + "', inTargetOrdStr: '"+ inTargetOrdStr+"', inTargetSlotStr: '"+inTargetSlotName+"'");
		
		try
		{
			boolean validOrd = false;
			
			BOrd targetOrd = BOrd.make(BFormat.make(inTargetOrdStr).format(thisComp));
			
			if(isOrdValid(targetOrd))
			{
				validOrd = true;
			}
			else
			{
				targetOrd = BOrd.make(BFormat.make("station:|" + inTargetOrdStr).format(thisComp));
				
				if(isOrdValid(targetOrd))
				{
					validOrd = true;
				}
			}
			
			
			if( validOrd )
			{
				BOrd		sourceOrd	= thisComp.getHandleOrd();
				BComponent	targetComp	= (BComponent)targetOrd.relativizeToHost().get();
				
				boolean		sourceSlotExists	= false;
				boolean		targetSlotExists	= false;
				
				sourceSlotExists				= doesSlotExist(thisComp, inSourceSlotName);
				targetSlotExists				= doesSlotExist(targetComp, inTargetSlotName);
				
				if( sourceSlotExists && targetSlotExists )
				{
					Slot 	sourceSlot 				= thisComp.getSlot(inSourceSlotName);
					Slot 	targetSlot 				= targetComp.getSlot(inTargetSlotName);

					
					//Check link already exists, if so then don't continue...
					if( isAlreadyLinked(targetComp, inTargetSlotName, thisComp, inSourceSlotName) )
					{
						messageHandler(Level.FINEST, "createOutgoingLink(), ALREADY LINKED!, target: '" + targetComp.getName()+":"+targetSlot.getName() + "', source: '" + thisComp.getName()+":"+sourceSlot.getName());
						messageHandler(Level.FINEST, div);
						messageHandler(Level.FINEST, "");
						return;
					}
					
					boolean	sourceIsAction 			= false;
					boolean	sourceIsTopic 			= false;
					boolean	targetIsAction 			= false;
					boolean	targetIsTopic 			= false;
					
					try{sourceIsAction 				= sourceSlot.isAction();}catch(Exception e) {}
					try{sourceIsTopic 				= sourceSlot.isTopic();}catch(Exception e) {}
					try{targetIsAction 				= sourceSlot.isAction();}catch(Exception e) {}
					try{targetIsTopic 				= sourceSlot.isTopic();}catch(Exception e) {}
					
					boolean	sourceIsActionOrTopic	= sourceIsAction || sourceIsTopic;
					boolean	targetIsActionOrTopic	= targetIsAction || targetIsTopic;
					
					boolean	targetIsNormal			= !targetIsActionOrTopic;
					boolean	sourceIsNormal			= !sourceIsActionOrTopic;
					
					Type	srcType 				= determineSlotType(sourceSlot);
					Type	trgType 				= determineSlotType(targetSlot);
					
					if(getInDebug())
					{
						try{messageHandler(Level.FINEST, "createOutgoingLink(), targetComp: '" + targetComp.getName()+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR targetComp");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), targetSlot: '" + targetSlot.getName()+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR targetSlot");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), thisComp:   '" + thisComp.getName()+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR thisComp");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), sourceSlot: '" + sourceSlot.getName()+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR sourceSlot");}
						
						
						try{messageHandler(Level.FINEST, "createOutgoingLink(), srcType: '" + srcType+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR srcType");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), srcType: '" + srcType+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR srcType");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), trgType: '" + trgType+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR trgType");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), sourceIsAction: '" + sourceIsAction+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR sourceIsAction");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), sourceIsTopic: '" + sourceIsTopic+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR sourceIsTopic");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), targetIsAction: '" + targetIsAction+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR targetIsAction");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), targetIsTopic: '" + targetIsTopic+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR targetIsTopic");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), sourceIsActionOrTopic: '" + sourceIsActionOrTopic+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR sourceIsActionOrTopic");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), targetIsActionOrTopic: '" + targetIsActionOrTopic+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR targetIsActionOrTopic");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), targetIsNormal: '" + targetIsNormal+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR targetIsNormal");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), sourceIsNormal: '" + sourceIsNormal+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR sourceIsNormal");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), sourceSlotExists: '" + sourceSlotExists+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR sourceSlotExists");}
						try{messageHandler(Level.FINEST, "createOutgoingLink(), targetSlotExists: '" + targetSlotExists+"'");}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), ERROR targetSlotExists");}
					}
					
					/*
					NOTES ON WHAT TYPE OF LINKS ARE ALLOWED:
					action with null type		to	topic with event type	= NOT ALLOWED
					action with null type		to	action with param type	= NOT ALLOWED
					normal slot					to	topic with event type	= NOT ALLOWED
					topic with event type		to	normal slot				= NOT ALLOWED
					action with null type		to	normal slot				= NOT ALLOWED
					action with param type		to	normal slot				= NOT ALLOWED
					
					action with param type		to	topic with event type	= conversion link if diff, link if same
					topic with event type		to	action with param type	= conversion link if diff, link if same
					normal slot					to	action with param type	= conversion link if diff, link if same
					
					action with param type		to	action with null type	= link
					action with null type		to	action with null type	= link
					topic with event type		to	action with null type	= link
					normal slot					to	action with null type	= link

					*/
					
					if( (sourceIsNormal && targetIsTopic) || (sourceIsAction && srcType==null && targetIsTopic)
						|| (sourceIsAction && srcType==null && targetIsAction && trgType!=null)|| (sourceIsActionOrTopic && targetIsNormal))
					{
						try{messageHandler(Level.FINEST, "createOutgoingLink(), Link combination NOT ALLOWED!, target: '" + targetComp.getName()+":"+targetSlot.getName() + "', source: '" + thisComp.getName()+":"+sourceSlot.getName());}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), Link combination NOT ALLOWED!");}
						messageHandler(Level.FINEST, div);
						messageHandler(Level.FINEST, "");
						return;
					}
					else if( targetIsNormal && !hasLinks(targetComp, inTargetSlotName) )
					{
						try{messageHandler(Level.FINEST, "createOutgoingLink(), target slot already has a link, target: '" + targetComp.getName()+":"+targetSlot.getName() + "', source: '" + thisComp.getName()+":"+sourceSlot.getName());}catch(Exception e) {messageHandler(Level.FINEST, "createOutgoingLink(), target slot already has a link!");}
						messageHandler(Level.FINEST, div);
						messageHandler(Level.FINEST, "");
						return;
					}
					else if( targetIsAction && trgType==null)
					{
						BLink link = new BLink(sourceOrd, inSourceSlotName, inTargetSlotName, true);
						targetComp.add(null, link);
					}
					else
					{
						if(srcType==null || trgType==null)
						{
							BLink link = new BLink(sourceOrd, inSourceSlotName, inTargetSlotName, true);
							targetComp.add(null, link);
						}
						else
						{
							if(srcType.is(trgType))
							{
								BLink link = new BLink(sourceOrd, inSourceSlotName, inTargetSlotName, true);
								targetComp.add(null, link);
							}
							else
							{
								BConverter converter = findConverter(srcType,trgType);
								
								if( !converter.isNull() )
								{
									BConversionLink cLink = new BConversionLink(thisComp.getHandleOrd(),inSourceSlotName,inTargetSlotName,true,converter );
									targetComp.add(null, cLink);
								}
								else
								{
									messageHandler(Level.FINEST, "createOutgoingLink(), Could not determine the converter from type '" + srcType + "', to type '" + trgType + "', link was not created.");
								}
							}
						}
					}
				}
				else
				{
					if( !sourceSlotExists )
					{
						messageHandler(Level.FINEST, "createOutgoingLink(), source slot '"+inSourceSlotName+"' does not exist in component '"+thisComp.getSlotPath()+"'");
					}
					if( !targetSlotExists )
					{
						messageHandler(Level.FINEST, "createOutgoingLink(), target slot '"+inTargetSlotName+"' does not exist in component '"+targetComp.getSlotPath()+"'");
					}
				}
				
			}
			else
			{
				messageHandler(Level.FINEST, "createOutgoingLink(), targetOrd IS NOT VALID: '"+targetOrd+"'");
			}
		}
		catch (Exception e)
		{
			messageHandler(Level.FINEST, "createOutgoingLink()", e);
		}
		
		messageHandler(Level.FINEST, div);
		messageHandler(Level.FINEST, "");
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
	private String[] split(String inString, String delim)
	{									
		messageHandler( Level.FINEST, "\t" + "split() method called with inString: '" + inString + "'.");
		
		if (inString.indexOf(delim) == -1) 
		{
			if (inString.length() == 0)
			{
				return new String[0];
			}
			else
			{
				return new String[] { inString };
			}
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
				messageHandler( Level.FINE, "Parameter found in CSV string: " + inString.substring(firstChar, lastChar));
				lastChar = lastChar + delim.length();
				firstChar = lastChar;
			}
			else
			{
				lastChar++;
			}
		}
		
		
		list = resizeArray(list, index);
		list[index++] = inString.substring(firstChar, inString.length());

		if (index == list.length)
		{
			return list;
		}
		else
		{
			String[] trim = new String[index];
			System.arraycopy(list, 0, trim, 0, index);
			return trim;
		}
	}
	
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private String[][] split(String inString, String delim1, String delim2)
	{
		messageHandler( Level.FINEST, "\t" + "split(String inString, String delim1, String delim2) method called.");
		
		if (inString.indexOf(delim1) == -1 && inString.indexOf(delim2) == -1)
		{
			if (inString.length() == 0)
			{
				return new String[0][0];
			}
			else
			{
				String[][] outputArray;
				if (inString.indexOf(delim1) == -1 && inString.indexOf(delim2) > -1)
				{
					String[] tempArray = split(inString, delim2);
					outputArray = new String[1][tempArray.length];

					for (int i = 0; i < tempArray.length; i++)
					{
						outputArray[0][i] = tempArray[i];
					}
					return outputArray;
				}
				else
				{
					String[] tempArray = split(inString, delim1);
					outputArray = new String[tempArray.length][1];

					for (int i = 0; i < tempArray.length; i++)
					{
						outputArray[i][0] = tempArray[i];
					}
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

				for (int j = 0; j < arrDelim2.length; j++)
				{
					list[i][j] = arrDelim2[j];
				}
			}
			
			

			if (list[0].length == secondDimensionSize)
			{
				return list;
			}
			else
			{
				String[][] trim = new String[list.length][secondDimensionSize];
				
				for (int i = 0; i < trim.length; i++)
				{
					for (int j = 0; j < trim[i].length; j++)
					{
						trim[i][j] = list[i][j];
					}
				}

				return trim;
			}
		}
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private String[] resizeArray(String[] inArray, int len)
	{
		messageHandler( Level.FINEST, "\t" + "resizeArray(String[] inArray, int len) method called.");
		
		if (len < inArray.length)
		{
			return inArray;
		}
		// int newLength = Math.min(100, inArray.length*2);
		int newLength = 100;
		newLength = Math.max(newLength, inArray.length + 50);
		newLength = Math.min(newLength, inArray.length * 2);
		newLength = Math.max(newLength, len);

		String[] expand = new String[newLength];
		System.arraycopy(inArray, 0, expand, 0, inArray.length);
		return expand;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private String[][] resizeArray(String[][] inArray, int len1, int len2)
	{
		messageHandler( Level.FINEST, "\t" + "resizeArray(String[][] inArray, int len1, int len2) method called.");
		
		if (len1 <= inArray.length && len2 <= inArray[0].length)
			return inArray;

		int newLength1 = 100;

		if (len1 <= inArray.length)
			newLength1 = inArray.length;
		{
			newLength1 = Math.max(newLength1, inArray.length + 50);
			newLength1 = Math.min(newLength1, inArray.length * 2);
			newLength1 = Math.max(newLength1, len1);
		}

		int newLength2 = 100;

		if (len2 <= inArray[0].length)
			newLength2 = inArray[0].length;
		{
			newLength2 = Math.max(newLength2, inArray[0].length + 50);
			newLength2 = Math.min(newLength2, inArray[0].length * 2);
			newLength2 = Math.max(newLength2, len2);
		}

		String[][] expand = new String[newLength1][newLength2];

		for (int i = 0; i < inArray.length; i++)
			for (int j = 0; j < inArray[i].length; j++)
				expand[i][j] = inArray[i][j];

		return expand;
	}
	
	
	
	
	

	/*------------------------------------------------------------------------------------------------------------------------*/
	private String replaceString(String sourceStr, String oldStr, String newStr)
	{
		messageHandler( Level.FINEST, "\t" + "replaceString() method called with sourceStr: '" + sourceStr + "', oldStr: '" + oldStr + "', newStr: '" + newStr + "'.");
		
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
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * @param inComp
	 * @param inSlotName
	 * @return <code>true</code> if slot does exist.<br><code>false</code> if slot does not exist.
	 */
	private boolean doesSlotExist(BComponent inComp, String inSlotName)
	{
		//if(getDebug()){System.out.println("doesSlotExist() method called.");}
		
		boolean result = false;
		
		try
		{
			Slot slot = inComp.getSlot(inSlotName);
			if(slot.getDeclaringType().getDisplayName(null).length() > 0) { /* do nothing, this just here to prevent compile warning */}
			result = true;
		}
		catch(Exception e)
		{
			result = false;
		}
		
		//if(getDebug()){System.out.println("doesSlotExist() method done and returning: '" + result + "'");}
		return result;
	}
	
	
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	private static boolean isCompositeLink(BLink paramBLink)
	{
		if (paramBLink == null) 
		{
			return false;
		}
		
//		BCompositeEditor ed = new BCompositeEditor();
//		ed.createComposite().makeLink(source, sourceSlot, targetSlot, cx);
		BComplex localBComplex = paramBLink.getParent();
		int i = localBComplex.getFlags(paramBLink.getPropertyInParent());
		return (i & 0x1000) != 0;
	}
	
	
	
	/*----------------------------------------------------------------------------------------------------------*/
	/**
	 * Checks to see if a given BComponent has any links to the given slot.<br>
	 * Returns TRUE if links exist.
	 * @param inComp - BComponent that has a slot you want to check for links.
	 * @param inSlotName - String name of the slot you want to check
	 * @return boolean - TRUE if links exist.
	 * @throws Exception
	 */
	private boolean hasLinks(BComponent inComp, String inSlotName) throws Exception
	{
		boolean result = false;
		try
		{
			result = (inComp.getLinks(inComp.getSlot(inSlotName)).length > 0);
		}
		catch (Exception e)
		{
			throw e;
		}
		
		return result;
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private boolean isAlreadyLinked(BComponent inTrgComp, String inTrgSlotName, BComponent inSrcComp, String inSrcSlotName)
	{
		boolean result = false;
		try
		{
			BLink[] links = inTrgComp.getLinks(inTrgComp.getSlot(inTrgSlotName));
			
			for (int i = 0; i < links.length; i++)
			{
				BComponent srcComp = links[i].getSourceComponent();
				String srcSlotName = links[i].getSourceSlot().getName();

				BComponent trgComp = links[i].getTargetComponent();
				String trgSlotName = links[i].getTargetSlot().getName();
				
				
				messageHandler(Level.FINEST, "isAlreadyLinked(), srcComp: '" + srcComp.getName() + "', srcSlotName: '" + srcSlotName + "', trgComp: '" + trgComp + "', trgSlotName: '" + trgSlotName + "'");
				
				
				if(srcComp==inSrcComp && srcSlotName.equals(inSrcSlotName))
				{
					result = true;
					return result;
				}
			}
			
		}
		catch (Exception e)
		{
			messageHandler(Level.FINEST, "isAlreadyLinked()", e);
		}
		
		return result;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private Type determineSlotType(Slot inSlot)
	{
		Type type = null;
		
		try
		{
			boolean slotIsActionOrTopic = inSlot.isAction() || inSlot.isTopic();
			
			if( slotIsActionOrTopic)
			{
				try
				{
					if(inSlot.isAction())
					{
						type = inSlot.asAction().getParameterType();
						messageHandler(Level.FINEST, "determineSlotType(), inSlot: '" + inSlot.getName() + "' IS ACTION, ParameterType: " + type);
					}
					else if(inSlot.isTopic())
					{
						type = inSlot.asTopic().getEventType();
						messageHandler(Level.FINEST, "determineSlotType(), inSlot: '" + inSlot.getName() + "' IS TOPIC, EventType: " + type);
					}
				}
				catch(Exception e)
				{
					messageHandler(Level.FINEST, "determineSlotType(), slotIsActionOrTopic", e);
				}
			}
			else
			{
				//type = thisComp.get("").getType();
				type = inSlot.asProperty().getType();
				messageHandler(Level.FINEST, "determineSlotType(), inSlot: '" + inSlot.getName() + "', Type: " + type);
			}
		}
		catch(Exception e)
		{
		}
		
		return type;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public String seguinStationPath()
	{
		String station = seguinStationSlotPath().toString();
		if(station != null)
		{
			if(station.length() > 0)
			{
				station = "station:|" + station;
			}
		}
		
		return station;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public SlotPath seguinStationSlotPath()
	{
		String pointsString = "points";
		String thisSlotPath = thisComp.getSlotPath().getBody();
		
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
		{
			if(zone.length() > 0)
			{
				zone = "station:|" + zone;
			}
		}
		
		return zone;
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * This is a custom string that can be used in the source ord BFormat input.
	 */
	public SlotPath seguinZoneSlotPath()
	{
		String pointsString = "points";
		String thisSlotPath = thisComp.getSlotPath().getBody();
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
			ord = BOrd.make(BFormat.make(path).format(thisComp));
			com = (BComponent)ord.relativizeToHost().get();
		}
		catch (Exception e)
		{
			messageHandler(Level.FINEST, "Exception from method 'getComponentFromPath()'.", e);
		}
		
		return com;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public String wcRootPath()
	{
		String result = "";
		
		try
		{
			result = wcRoot().getSlotPath().toString();
		}
		catch (Exception e)
		{
		}
		
		return result;
	}
	
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	public BComponent wcRoot()
	{
		BComponent result = new BComponent();
		
		try
		{
			BComponent  comp   = (BComponent) this.getParent().getParentComponent();
			
			boolean found = false;
			while( !comp.getType().toString().equalsIgnoreCase("baja:Station") && !found )
			{
				if( comp.getType().toString().equalsIgnoreCase("korsComponentManager:WorkcenterFolder"))
				{
					found = true;
				}
				else
				{
					comp   = (BComponent) comp.getParent().getParentComponent();
				}
			}
			
			if(found)
			{
				result = comp;
			}
		}
		catch (Exception e)
		{
		}
		
		return result;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private void addSlotFlag(Property inPropertyName, int flag)
	{
		Slot updateSlot = thisComp.getSlot(inPropertyName.getName());
		thisComp.setFlags(updateSlot, (thisComp.getFlags(updateSlot) | flag));
	}
	
	/*------------------------------------------------------------------------------------------------------------------------*/
	private void removeSlotFlag(Property inPropertyName, int flag)
	{
		Slot updateSlot = thisComp.getSlot(inPropertyName.getName());
		thisComp.setFlags(updateSlot, (thisComp.getFlags(updateSlot) & ~flag));
	}
	
	
	/*---------------------------------------------------------------------------------------------------------*/
	private void debugStrOrds(String[][] strOrds)
	{
		try
		{
			if(getInDebug())
			{
				messageHandler(Level.FINEST, "\n\n" + div);
				messageHandler(Level.FINEST, div);
				for (int row = 0; row < strOrds.length; row++)
				{
					for (int col = 0; col < strOrds[row].length; col++)
					{
						messageHandler(Level.FINEST, "row:"+row+", col:"+col+", len: " +strOrds[row].length + "  =  " + strOrds[row][col] + "\t");
					}
					messageHandler(Level.FINEST, "");
				}
				messageHandler(Level.FINEST, div);
				messageHandler(Level.FINEST, div+"\n\n");
			}
		}
		catch (Exception e)
		{
			messageHandler(Level.FINEST, "debugStrOrds(), EXCEPTION!", e);
		}
	}
}

