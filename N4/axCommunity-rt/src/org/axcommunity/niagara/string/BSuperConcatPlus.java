package org.axcommunity.niagara.string;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BConversionLink;
import javax.baja.sys.BDouble;
import javax.baja.sys.BDynamicEnum;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BLink;
import javax.baja.sys.BObject;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;

import org.axcommunity.niagara.helperClasses.LoggingComponent;

/**
 * This was created using borrowed then modified code from the "BSuperOr" object
 * which was created by CMH (XENCOM Energy Management)
 *
 * Allows you to set the number of Status String input slots and the character you want to use as a delimiter. 
 * You can also decide whether to concat on any input change or only when triggered.
 * Five variations of the concatenated values are returned...
 * 		- no delimiters
 * 		- delimit only non-blank or non-null values
 * 		- delimit all values
 * 		- and two more slots just like the previous two mentioned except a timestamp is added to the end.
 *
 * @author		Justin Koffler
 * @creation	5 Feb 12
 */



public class BSuperConcatPlus extends BComponent implements LoggingComponent
{
	public static final Property inDebug = newProperty(Flags.HIDDEN, false);
	public boolean getInDebug() { return getBoolean(inDebug); }
	public void setInDebug(boolean v) { setBoolean(inDebug, v, null); }

	public static final Property inSuppressSlotPath = newProperty(Flags.HIDDEN, false);
	public boolean getInSuppressSlotPath() { return getBoolean(inSuppressSlotPath); }
	public void setInSuppressSlotPath(boolean v) { setBoolean(inSuppressSlotPath, v, null); }
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** ACTION, "concatenate", PERFORMS THE CONCATENATION OF THE INPUTS   */
	public static final Action concatenate = newAction(Flags.SUMMARY|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void concatenate(){invoke(concatenate,null,null);}
	public void doConcatenate()
	{
		Thread t = new Thread(new calculate());
		t.start();		
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** ACTION, "VariableCount", SETS THE NUMBER OF STRING INPUTS	*/
	public static BDouble _VariableCount = BDouble.make(5);
	public static final Action VariableCount = newAction(0, (BValue)BDouble.TYPE.getInstance(), BFacets.make(BFacets.PRECISION, BInteger.make(0)) );
	public BDouble VariableCount(BDouble _VariableCount){return (BDouble)invoke(VariableCount,_VariableCount,null);}
	public BDouble doVariableCount(BDouble v)
	{
		try 
		{ 
			updatingSlotCount = true;
			if(v.getDouble() > 256.0)
			{
				_VariableCount = BDouble.make(256);
				if( getNumberOfSlots().getValue()  != _VariableCount.getDouble() ){ getNumberOfSlots().setValue(256.0);}
				if( getNumberOfValues().getValue() != _VariableCount.getDouble() ){ getNumberOfValues().setValue(256.0);}
			}
			else
			{
				_VariableCount = v;
				if( getNumberOfSlots().getValue()  != _VariableCount.getDouble() ){ getNumberOfSlots().setValue(v.getDouble());}
				if( getNumberOfValues().getValue() != _VariableCount.getDouble() ){ getNumberOfValues().setValue(v.getDouble());}
			}
			
			slots(getNumberOfSlots().getValue());
			updatingSlotCount = false;
		}
		catch (Exception e) {}
		
		return _VariableCount;
	}
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Gets Action parameter defaults */
	public BValue getActionParameterDefault(Action paramAction)
	{
		if (paramAction == VariableCount)
		{
			Double		inValue		= new Double(getNumberOfValues().getValue());
			BValue		outValue	= (BValue)BDouble.make(java.lang.String.valueOf(inValue));
			
			return outValue;
		}
		
		return super.getActionParameterDefault(paramAction);
	}


	/*----------------------------------------------------------------------------------------------------------------*/
	/* INPUTS  -------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/

	/** STATUS STRING INPUT, "inDelimiter", STRING USED TO DELIMIT THE OUTPUT STRING */
	public static final Property inDelimiter = newProperty(0, new BStatusString(","), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getInDelimiter() { return (BStatusString)get(inDelimiter); }
	public void setInDelimiter(BStatusString v) { set(inDelimiter, v, null); }

	/**Represents the location where the timestamp should be place.*/
	public static final Property inTimestampLocation = newProperty(0, (BValue)BDynamicEnum.TYPE.getInstance(), BFacets.tryMake("range=E:{Put$20timestamp$20at$20end$20of$20string=0,Put$20timestamp$20at$20begining$20of$20string=1}"));
	public BDynamicEnum getInTimestampLocation() { return (BDynamicEnum)get(inTimestampLocation); }
	public void setInTimestampLocation(BDynamicEnum v) { set(inTimestampLocation, v, null); }
	
	/** This is the format the time string output should be written in. <br>
	 * <p>Available Fields Include: <br>
	 * 
	 * <blockquote><table border='1' bordercolor='#505050' cellspacing='0'>
	 * <tr><th bgcolor='#666699' align=left><font color='#ffffff'>Letter</font></th><th bgcolor='#666699' align=left><font color='#ffffff'>Component</font></th><th bgcolor='#666699' align=left><font color='#ffffff'>Type</font></th><th bgcolor='#666699' align=left><font color='#ffffff'>Examples</font></th></tr>
	 * <tr><td><code>G</code></td><td>Era designator</td><td>Text</td><td><code>AD</code></td></tr>
	 * <tr><td><code>y</code></td><td>Year</td><td>Year</td><td><code>yyyy=1996</code>, <code>yy=96</code></td></tr>
	 * <tr><td><code>Y</code></td><td>Week year</td><td>Year</td><td><code>YYYY=2009</code>, <code>YY=09</code></td></tr>
	 * <tr><td><code>M</code></td><td>Month in year</td><td>Month</td><td><code>MMMM=July</code>, <code>MMM=Jul</code>, <code>MM=07</code></td></tr>
	 * <tr><td><code>w</code></td><td>Week in year</td><td>Number</td><td><code>27</code></td></tr>
	 * <tr><td><code>W</code></td><td>Week in month</td><td>Number</td><td><code>2</code></td></tr>
	 * <tr><td><code>D</code></td><td>Day in year</td><td>Number</td><td><code>189</code></td></tr>
	 * <tr><td><code>d</code></td><td>Day in month</td><td>Number</td><td><code>10</code></td></tr>
	 * <tr><td><code>F</code></td><td>Day of week in month</td><td>Number</td><td><code>2</code></td></tr>
	 * <tr><td><code>E</code></td><td>Day name in week</td><td>Text</td><td><code>EEEE=Tuesday</code>, <code>E=Tue</code></td></tr>
	 * <tr><td><code>u</code></td><td>Day number of week</td><td>Number</td><td><code>1</code></td></tr>
	 * <tr><td><code>a</code></td><td>AM/PM marker</td><td>Text</td><td><code>PM</code></td></tr>
	 * <tr><td><code>H</code></td><td>Hour in day (0-23)</td><td>Number</td><td><code>0</code></td></tr>
	 * <tr><td><code>k</code></td><td>Hour in day (1-24)</td><td>Number</td><td><code>24</code></td></tr>
	 * <tr><td><code>K</code></td><td>Hour in am/pm (0-11)</td><td>Number</td><td><code>0</code></td></tr>
	 * <tr><td><code>h</code></td><td>Hour in am/pm (1-12)</td><td>Number</td><td><code>12</code></td></tr>
	 * <tr><td><code>m</code></td><td>Minute in hour</td><td>Number</td><td><code>30</code></td></tr>
	 * <tr><td><code>s</code></td><td>Second in minute</td><td>Number</td><td><code>55</code></td></tr>
	 * <tr><td><code>S</code></td><td>Millisecond</td><td>Number</td><td><code>978</code></td></tr>
	 * <tr><td><code>z</code></td><td>Time zone</td><td>General time zone</td><td><code>zzzz=Pacific Standard Time</code>, <code>z=PST</code></td></tr>
	 * <tr><td><code>Z</code></td><td>Time zone</td><td>RFC 822 time zone</td><td><code>-0800</code></td></tr>
	 * <tr><td><code>X</code></td><td>Time zone</td><td>ISO 8601 time zone</td><td><code>X=-08</code>, <code>XX=-0800</code>,  <code>XXX=-08:00</code></td></tr>
	 * </table></blockquote>
	 * <br>
	 * <p>EXAMPLE: MM/dd/yyyy hh:mm:ss.S a <br>
	 * 
	 */
	public static final Property inTimestampFormat = newProperty(0, BString.make("MM/dd/yyyy hh:mm:ss.S a"), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public String getInTimestampFormat() { return getString(inTimestampFormat); }
	public void setInTimestampFormat(String v) { setString(inTimestampFormat,v,null); }
	
	/** STATUS BOOLEAN INPUT, "inNullOnNoLink", SET UNLINKED INPUTS TO NULL UPON EXECUTION */
	public final static Property inNullOnNoLink = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getInNullOnNoLink() { return (BStatusBoolean)get(inNullOnNoLink); }
	public void setInNullOnNoLink(BStatusBoolean v) { set(inNullOnNoLink, v); }

	
	public static final Property inConcatOnStartup = newProperty(0, new BStatusBoolean(true, BStatus.ok), null);
	public BStatusBoolean getInConcatOnStartup() { return (BStatusBoolean)get(inConcatOnStartup); }
	public void setInConcatOnStartup(BStatusBoolean v) { set(inConcatOnStartup, v, null); }
	
	/** STATUS BOOLEAN INPUT, "inConcatOnAnyInputChange", WHEN TRUE OUTPUT WILL BE CALCULATED ON ANY INPUT BEING CHANGED */
	public final static Property inConcatOnAnyInputChange = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getInConcatOnAnyInputChange() { return (BStatusBoolean)get(inConcatOnAnyInputChange); }
	public void setInConcatOnAnyInputChange(BStatusBoolean v) { set(inConcatOnAnyInputChange, v); }

	/** STATUS NUMERIC INPUT, "numberOfSlots", NUMBER OF INPUT STRING SLOTS TO HAVE */
	public static final Property numberOfSlots = newProperty(0, new BStatusNumeric(), null);
	public BStatusNumeric getNumberOfSlots() { return (BStatusNumeric)get(numberOfSlots); }
	public void setNumberOfSlots(BStatusNumeric v) { set(numberOfSlots, v, null); }

	/** STATUS BOOLEAN INPUT, "trigger", WILL CAUSE THE CONCATANATION TO OCCUR */
	public final static Property trigger = newProperty(0|Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger); }
	public void setTrigger(BStatusBoolean v) { set(trigger, v); }


	/*----------------------------------------------------------------------------------------------------------------*/
	/* OUTPUTS  ------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/

	/** STATUS NUMERIC OUTPUT, "numberOfValues", NUMBER OF INPUT STRING SLOTS */
	public static final Property numberOfValues = newProperty(Flags.HIDDEN|Flags.READONLY, new BStatusNumeric(), null);
	public BStatusNumeric getNumberOfValues() { return (BStatusNumeric)get(numberOfValues); }
	public void setNumberOfValues(BStatusNumeric v) { set(numberOfValues, v, null); }

	/** STATUS STRING OUTPUT, "outNoDelimeters", THIS WILL CONCAT ALL VALUES WITH NO DELIMETERS */
	public static final Property outNoDelimeters = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(""), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutNoDelimeters() { return (BStatusString)get(outNoDelimeters); }
	public void setOutNoDelimeters(BStatusString v) { set(outNoDelimeters, v, null); }

	/** STATUS STRING OUTPUT, "outDelimitValuesOnly" *, THIS WILL CONCAT ONLY NON-BLANK AND NON-NULL VALUES WITH DELIMETERS*/
	public static final Property outDelimitValuesOnly = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(""), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutDelimitValuesOnly() { return (BStatusString)get(outDelimitValuesOnly); }
	public void setOutDelimitValuesOnly(BStatusString v) { set(outDelimitValuesOnly, v, null); }

	/** STATUS STRING OUTPUT, "outDelimitAll", THIS WILL CONCAT ALL VALUES WITH DELIMETERS*/
	public static final Property outDelimitAll = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(""), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutDelimitAll() { return (BStatusString)get(outDelimitAll); }
	public void setOutDelimitAll(BStatusString v) { set(outDelimitAll, v, null); }

	/** STATUS STRING OUTPUT, "outDelimitValuesOnlyPlusTimestamp", SAME AS "outDelimitValuesOnly" EXCEPT THE A TIMESTAMP IS ADDED TO THE END */
	public static final Property outDelimitValuesOnlyPlusTimestamp = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(""), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutDelimitValuesOnlyPlusTimestamp() { return (BStatusString)get(outDelimitValuesOnlyPlusTimestamp); }
	public void setOutDelimitValuesOnlyPlusTimestamp(BStatusString v) { set(outDelimitValuesOnlyPlusTimestamp, v, null); }

	/** STATUS STRING OUTPUT, "outDelimitAllPlusTimestamp", SAME AS "outDelimitAll" EXCEPT THE A TIMESTAMP IS ADDED TO THE END */
	public static final Property outDelimitAllPlusTimestamp = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(""), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutDelimitAllPlusTimestamp() { return (BStatusString)get(outDelimitAllPlusTimestamp); }
	public void setOutDelimitAllPlusTimestamp(BStatusString v) { set(outDelimitAllPlusTimestamp, v, null); }

	/*----------------------------------------------------------------------------------------------------------------*/
	/* TOPICS  -------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
	
	/** OUTPUT HAS CHANGED VALUE */
	public static final Topic changed = newTopic(0);
	public void fireChanged(BBoolean event){fire(changed,event,null);}

	/** SET STRING VALUE */ 
	public static final Topic SetNoDelimeters = newTopic(0);
	public void fireSetNoDelimeters(BString event){fire(SetNoDelimeters,event,null);}

	/** SET STRING VALUE */ 
	public static final Topic SetDelimitValuesOnly = newTopic(0);
	public void fireSetDelimitValuesOnly(BString event){fire(SetDelimitValuesOnly,event,null);}

	/** SET STRING VALUE */ 
	public static final Topic SetDelimitAll = newTopic(0);
	public void fireSetDelimitAll(BString event){fire(SetDelimitAll,event,null);}

	/** SET STRING VALUE */ 
	public static final Topic SetDelimitValuesOnlyPlusTimestamp = newTopic(0);
	public void fireSetDelimitValuesOnlyPlusTimestamp(BString event){fire(SetDelimitValuesOnlyPlusTimestamp,event,null);}

	/** SET STRING VALUE */ 
	public static final Topic SetDelimitAllPlusTimestamp = newTopic(0);
	public void fireSetDelimitAllPlusTimestamp(BString event){fire(SetDelimitAllPlusTimestamp,event,null);}


	public	final	BComponent	getComponent() { return this; }
	public	final	BComponent	getProgram() { return this; }
	
	private boolean				lastTrigger			= false;
	private boolean				updatingSlotCount	= false;
	private static final String	SLOT_NAME_PREFIX	= "In_";
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void started() throws Exception
	{
		super.started();
		if(!Sys.atSteadyState() || !isRunning()){return;}
		startAndSteadyState();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void stationStarted() throws Exception
	{
		super.stationStarted();
		if(!Sys.atSteadyState() || !isRunning()){return;}
		startAndSteadyState();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void atSteadyState() throws Exception
	{
		super.atSteadyState();
		if(!Sys.atSteadyState() || !isRunning()){return;}
		startAndSteadyState();
	}
	
	/*------------------------------------------------------------------------------------------------------------------*/
	/**
	 * The logic in this method should only be executed if the station is running and at steadyState.</br>
	 * This means this will only get ran when this object is initially added to a running station or duplicated/copied from an existing object.
	 */
	private void startAndSteadyState()
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		
		if(getInConcatOnStartup().getStatus().isValid() && getInConcatOnStartup().getValue())
		{
			Thread t = new Thread(new calculate());
			t.start();
		}
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------*/
	public void changed(Property p, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning())return;

		if(isRunning())
		{
			boolean calcOnChange = getInConcatOnAnyInputChange().getValue();

			// NUMBEROFSLOTS HAS CHANGED //////////////////////////////////////////////////////////////////////
			if(p == numberOfSlots)
			{
				try
				{
					if(!updatingSlotCount)
					{
						doVariableCount(BDouble.make(getNumberOfSlots().getValue()));
					}
				}
				catch(Exception e)
				{
					logger.log(Level.SEVERE, "\n" + getSlotPath() + "\n" + e.getStackTrace(), e);
				}
			}

			// ONE OF THE STRING INPUTS HAS CHANGED ///////////////////////////////////////////////////////////
			else if(calcOnChange==true && p!=numberOfSlots && p!=numberOfValues && p!=outNoDelimeters && p!=outDelimitValuesOnly && p!=outDelimitAll && p!=outDelimitValuesOnlyPlusTimestamp && p!=outDelimitAllPlusTimestamp)
			{
				logger.log(Level.FINE, "\n" + getSlotPath()	+ "\nCalculating because " + p.getName() + " changed");

				Thread t = new Thread(new calculate());
				t.start();
			}

			// TRIGER INPUT CHANGED ///////////////////////////////////////////////////////////////////////////
			else if (p==trigger)
			{
				boolean input = getTrigger().getValue();
				if(input && !lastTrigger)
				{
					lastTrigger = input;
					Thread t = new Thread(new calculate());
					t.start();
				}
				else
				{
					lastTrigger = input;
				}
			}
		}
	}
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void removed(Property prop, BValue oldValue, Context context)
	{
		/** 
		 * The purpose of this logic is to clear a slot's value 
		 * when a link is deleted so it doesn't leave behind its old data. 
		 */
		try
		{
			if(prop.getType()==BLink.TYPE || prop.getType()==BConversionLink.TYPE)
			{
				String slotName = "";
				
				if(prop.getType()==BConversionLink.TYPE)
				{
					slotName = ((BConversionLink) oldValue).getTargetSlotName();
				}
				else
				{
					slotName = ((BLink) oldValue).getTargetSlotName();
				}
				
				Slot 		slot 		= this.getSlot(slotName);
				Property	targetProp	= this.getProperty(slot.getName());
				
				if( !slot.isAction() && !slot.isTopic() )
				{
					if(targetProp.isDynamic())
					{
						this.set(targetProp, (BValue) targetProp.getType().getInstance());
					}
					else
					{
						this.set(targetProp, (BValue) targetProp.getDefaultValue());
					}
				}
			}
		}
		catch (Exception e)
		{
		}
	}




	/*------------------------------------------------------------------------------------------------------------------*/
	/*-- CREATES THE REQUIRED SLOTS ------------------------------------------------------------------------------------*/
	private void slots(double MD)
	{
		logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t" + "slots() methods called with value: '" + MD + "'");
		
		try
		{
			for (int i = 1; i < (MD + 1); i++)
			{
				if (((BObject) get(SLOT_NAME_PREFIX + i)) == null)
				{
					getProgram().add((SLOT_NAME_PREFIX + i), new BStatusString(""), Flags.SUMMARY, BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)), null);
				}
			}
			for (int i = (int) MD + 1; ((BObject) get(SLOT_NAME_PREFIX + i)) != null; i++)
			{
				if (((BObject) get(SLOT_NAME_PREFIX + i)) != null)
				{
					getProgram().remove(SLOT_NAME_PREFIX + i);
				}
			}
		}
		catch (Exception e)
		{
			errorHandler(Level.FINE, e);
		}
	}


	/*------------------------------------------------------------------------------------------------------------------*/
	/*-- CONCAT ALL THE INPUTS TOGETHER --------------------------------------------------------------------------------*/
	class calculate implements Runnable
	{
		public void run()
		{
			try
			{
				BAbsTime	dtNow									= BAbsTime.now();
				String		delim									= getInDelimiter().getValue();
				boolean		nullOnNoLink							= getInNullOnNoLink().getValue();
				int			slotCount								= (int) getNumberOfValues().getValue();
				String		strTimeNow								= timestampAsString(dtNow);
				String		strOutNoDelimeters						= ""; //1 ONLY GOOD VALUES NO DELIMETER
				String		strOutDelimitValuesOnly					= ""; //2 ONLY GOOD VALUES ALONG WITH DELIMETER
				String		strOutDelimitAll						= ""; //3 ALL VALUES ALONG WITH DELIMETER
				String		strOutDelimitValuesOnlyPlusTimestamp	= ""; //4 LIKE strOutDelimitValuesOnly BUT WITH TIMESTAMP
				String		strOutDelimitAllPlusTimestamp			= ""; //5 LIKE strOutDelimitAll BUT WITH TIMESTAMP
				String		temp									= "";
				int			timeLocation							= getInTimestampLocation().getOrdinal();  // 0=Suffix, 1=Prefix
	
				
				List<String>	inputsAllValues		= new ArrayList<String>();
				List<String>	inputsHaveValues	= new ArrayList<String>();
				
				for(int i=1;i<=slotCount;i++)
				{
					try
					{
						BStatusString inValue = ((BStatusString) ((BObject)get(SLOT_NAME_PREFIX+i)));
		
						/** TESTS WHETHER THE SLOT IS LINKED. IF NOT, THE VALUE IS SET TO NULL **/
						if ( (getProgram().getLinks(getProperty(SLOT_NAME_PREFIX+i)).length == 0) && (nullOnNoLink==true))
						{
							inValue.setStatusNull(true); 
						}
		
						// CREATE TEMP STRING TO HOLD THE VALID VALUE OF THE INPUT SLOT ///////////////////////////////////
						temp = inValue.getStatus().isValid() ? inValue.getValue() : "";
						
						inputsAllValues.add(temp);
						
						if(temp.length()>0) {inputsHaveValues.add(temp);}
						
					}
					catch(Exception e)
					{
						errorHandler(Level.FINE, e);
					}
				}
				
				
				strOutNoDelimeters						= String.join("", inputsAllValues);
				strOutDelimitValuesOnly					= String.join(delim, inputsHaveValues);
				strOutDelimitAll						= String.join(delim, inputsAllValues);
				strOutDelimitValuesOnlyPlusTimestamp	= (timeLocation==1?strTimeNow + delim:"") + strOutDelimitValuesOnly	+ (timeLocation==0?delim + strTimeNow:"");
				strOutDelimitAllPlusTimestamp			= (timeLocation==1?strTimeNow + delim:"") + strOutDelimitAll		+ (timeLocation==0?delim + strTimeNow:"");
				
				getOutNoDelimeters().setValue(strOutNoDelimeters);
				getOutDelimitValuesOnly().setValue(strOutDelimitValuesOnly);
				getOutDelimitAll().setValue(strOutDelimitAll);
				getOutDelimitValuesOnlyPlusTimestamp().setValue(strOutDelimitValuesOnlyPlusTimestamp);
				getOutDelimitAllPlusTimestamp().setValue(strOutDelimitAllPlusTimestamp);
	
				fireSetNoDelimeters(BString.make(strOutNoDelimeters));
				fireSetDelimitValuesOnly(BString.make(strOutDelimitValuesOnly));
				fireSetDelimitAll(BString.make(strOutDelimitAll));
				fireSetDelimitValuesOnlyPlusTimestamp(BString.make(strOutDelimitValuesOnlyPlusTimestamp));
				fireSetDelimitAllPlusTimestamp(BString.make(strOutDelimitAllPlusTimestamp));
	
				fireChanged(BBoolean.make(true));
			}
			catch(Exception e)
			{
				errorHandler(Level.FINE, e);
			}
		}
	}

	
	/*------------------------------------------------------------------------------------------------------------------*/
	/*-- ASSIGN CURRENT TIME TO STRING VALUE ---------------------------------------------------------------------------*/
	public String timestampAsString(BAbsTime input)
	{
		String time = input.encodeToString();

		try
		{
    		Calendar cal = Calendar.getInstance();
    		cal.setTimeInMillis(input.getMillis());
    		
    		String formatDate = getInTimestampFormat();
    		
    		if( formatDate.length() <= 0 )
    		{
    			formatDate = "MM/dd/yyyy hh:mm:ss.S a";
    		}
    		
    		DateFormat df = new SimpleDateFormat( formatDate );
    		time = df.format(cal.getTime());
    	}
    	catch (Exception e) 
    	{
    		errorHandler(Level.FINE, e);
    	}
		
		return time;
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------*/
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BSuperConcatPlus.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");

	public static final Logger logger = Logger.getLogger(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
}

