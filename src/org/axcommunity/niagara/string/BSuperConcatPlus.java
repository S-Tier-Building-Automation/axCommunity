package org.axcommunity.niagara.string;

import javax.baja.log.Log;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BObject;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;

/**
 * This was created using borrowed then modified code from the "BSuperOr" object
 * which was created by CMH (XENCOM Energy Management)
 *
 * Allows you to set the number of Status String input slots and the character you want to use as a delimeter. 
 * You can also decide whether to concat on any input change or only when triggered.
 * Five variations of the concatenated values are outputted...
 * 		- no delimiters
 * 		- delimit only non-blank or non-null values
 * 		- delimit all values
 * 		- and two more slots just like the previous two mentioned except a timestamp is added to the end.
 *
 * @author		Justin Koffler
 * @creation	5 Feb 12
 */



public class BSuperConcatPlus extends BComponent
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** ACTION, "concatenate", PERFORMS THE CONCATENATION OF THE INPUTS   */////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final Action concatenate = newAction(Flags.SUMMARY|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void concatenate(){invoke(concatenate,null,null);}
	public void doConcatenate()
	{
		calculate();
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** ACTION, "VariableCount", SETS THE NUMBER OF STRING INPUTS	*//////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final Action VariableCount = newAction(0, (BValue)BDouble.TYPE.getInstance(), null);
	public void VariableCount(BDouble v){ invoke(VariableCount, v, null); }
	public void doVariableCount(BDouble v) throws Exception
	{
		try { onVariableCount(v); }
		catch (Throwable t) { throw new Exception(t); }
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INPUTS		///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
			
		/** STATUS STRING INPUT, "inDelimiter", STRING USED TO DELIMIT THE OUTPUT STRING *//////////////////////
		public static final Property inDelimiter = newProperty(0, new BStatusString(","), null);
		public BStatusString getInDelimiter() { return (BStatusString)get(inDelimiter); }
		public void setInDelimiter(BStatusString v) { set(inDelimiter, v, null); }
		
		/** STATUS BOOLEAN INPUT, "inNullOnNoLink", SET UNLINKED INPUTS TO NULL UPON EXECUTION *///////////////
		public final static Property inNullOnNoLink = newProperty(0, new BStatusBoolean(false));
		public BStatusBoolean getInNullOnNoLink() { return (BStatusBoolean)get(inNullOnNoLink); }
		public void setInNullOnNoLink(BStatusBoolean v) { set(inNullOnNoLink, v); }
		
		/** STATUS BOOLEAN INPUT, "inConcatOnAnyInputChange", WHEN TRUE OUTPUT WILL BE CALCULATED ON ANY INPUT BEING CHANGED */
		public final static Property inConcatOnAnyInputChange = newProperty(0, new BStatusBoolean(false));
		public BStatusBoolean getInConcatOnAnyInputChange() { return (BStatusBoolean)get(inConcatOnAnyInputChange); }
		public void setInConcatOnAnyInputChange(BStatusBoolean v) { set(inConcatOnAnyInputChange, v); }
		
		/** STATUS NUMERIC INPUT, "numberOfSlots", NUMBER OF INPUT STRING SLOTS TO HAVE */////////////////////
		public static final Property numberOfSlots = newProperty(0, new BStatusNumeric(), null);
		public BStatusNumeric getNumberOfSlots() { return (BStatusNumeric)get(numberOfSlots); }
		public void setNumberOfSlots(BStatusNumeric v) { set(numberOfSlots, v, null); }
		
		/** STATUS BOOLEAN INPUT, "trigger", WILL CAUSE THE CONCATANATION TO OCCUR *///////////////
		public final static Property trigger = newProperty(0|Flags.SUMMARY, new BStatusBoolean(false));
		public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger); }
		public void setTrigger(BStatusBoolean v) { set(trigger, v); }
		
		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// OUTPUTS		///////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		/** STATUS NUMERIC OUTPUT, "numberOfValues", NUMBER OF INPUT STRING SLOTS *////////////////////////////
		public static final Property numberOfValues = newProperty(0|Flags.HIDDEN|Flags.READONLY, new BStatusNumeric(), null);
		public BStatusNumeric getNumberOfValues() { return (BStatusNumeric)get(numberOfValues); }
		public void setNumberOfValues(BStatusNumeric v) { set(numberOfValues, v, null); }
		
		/** STATUS STRING OUTPUT, "outNoDelimeters", THIS WILL CONCAT ALL VALUES WITH NO DELIMETERS */////////////////////
		public static final Property outNoDelimeters = newProperty(0|Flags.SUMMARY, new BStatusString(""), null);
		public BStatusString getOutNoDelimeters() { return (BStatusString)get(outNoDelimeters); }
		public void setOutNoDelimeters(BStatusString v) { set(outNoDelimeters, v, null); }
		
		/** STATUS STRING OUTPUT, "outDelimitValuesOnly" *, THIS WILL CONCAT ONLY NON-BLANK AND NON-NULL VALUES WITH DELIMETERS*/
		public static final Property outDelimitValuesOnly = newProperty(0|Flags.SUMMARY, new BStatusString(""), null);
		public BStatusString getOutDelimitValuesOnly() { return (BStatusString)get(outDelimitValuesOnly); }
		public void setOutDelimitValuesOnly(BStatusString v) { set(outDelimitValuesOnly, v, null); }
		
		/** STATUS STRING OUTPUT, "outDelimitAll", THIS WILL CONCAT ALL VALUES WITH DELIMETERS*/////////////////////////
		public static final Property outDelimitAll = newProperty(0|Flags.SUMMARY, new BStatusString(""), null);
		public BStatusString getOutDelimitAll() { return (BStatusString)get(outDelimitAll); }
		public void setOutDelimitAll(BStatusString v) { set(outDelimitAll, v, null); }
		
		/** STATUS STRING OUTPUT, "outDelimitValuesOnlyPlusTimestamp", SAME AS "outDelimitValuesOnly" EXCEPT THE A TIMESTAMP IS ADDED TO THE END *//////////
		public static final Property outDelimitValuesOnlyPlusTimestamp = newProperty(0|Flags.SUMMARY, new BStatusString(""), null);
		public BStatusString getOutDelimitValuesOnlyPlusTimestamp() { return (BStatusString)get(outDelimitValuesOnlyPlusTimestamp); }
		public void setOutDelimitValuesOnlyPlusTimestamp(BStatusString v) { set(outDelimitValuesOnlyPlusTimestamp, v, null); }
		
		/** STATUS STRING OUTPUT, "outDelimitAllPlusTimestamp", SAME AS "outDelimitAll" EXCEPT THE A TIMESTAMP IS ADDED TO THE END *//////////
		public static final Property outDelimitAllPlusTimestamp = newProperty(0|Flags.SUMMARY, new BStatusString(""), null);
		public BStatusString getOutDelimitAllPlusTimestamp() { return (BStatusString)get(outDelimitAllPlusTimestamp); }
		public void setOutDelimitAllPlusTimestamp(BStatusString v) { set(outDelimitAllPlusTimestamp, v, null); }
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//    TOPIC SLOTS   ///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
		
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
		
		
		
		
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ONSTART AND ONSTOP	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		/** started   *////////////////////////////////////////////////////////////////////////////////////////////
		public void started() throws Exception { try { onStart(); } catch(Throwable t) { throw new Exception(t); } }
		
		/** stopped   *////////////////////////////////////////////////////////////////////////////////////////////
		public void stopped() throws Exception { try { onStop(); } catch(Throwable t) { throw new Exception(t); } }
		
		/** onStart   *////////////////////////////////////////////////////////////////////////////////////////////
		public void onStart() throws Exception {}
		
		/** onStop   */////////////////////////////////////////////////////////////////////////////////////////////
		public void onStop() throws Exception {}

	
	//----CODE BELOW HERE--------------------------------------------------------------------------------------
 
	////////////////////////////////////////////////////////////////
	// Access
	////////////////////////////////////////////////////////////////
	public final BComponent getComponent() { return this; }
	public final BComponent getProgram() { return this; }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** DETERMINES HOW MANY NEW SLOTS TO CREATE.   *///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void onVariableCount(BDouble NV) throws Exception
	{                                              
		if(NV.getDouble() > 60.0) getNumberOfValues().setValue(60.0);
		else getNumberOfValues().setValue(NV.getDouble());      
		slots(getNumberOfValues().getValue());
	} 
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** ON CHANGE EVENT		*//////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean last = false;
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
					onVariableCount(BDouble.make(getNumberOfSlots().getValue()));
					
				}
				catch(Exception e)
				{
					logger.error("\r\n\t\t" + getSlotPath() + "\r\n\t\t" + e.getStackTrace(), e);
				}
			}
			
			// ONE OF THE STRING INPUTS HAS CHANGED ///////////////////////////////////////////////////////////
			if(calcOnChange==true && p!=numberOfSlots && p!=numberOfValues && p!=outNoDelimeters && p!=outDelimitValuesOnly && p!=outDelimitAll && p!=outDelimitValuesOnlyPlusTimestamp && p!=outDelimitAllPlusTimestamp)
			{
				calculate();
			}
			
			// TRIGER INPUT CHANGED ///////////////////////////////////////////////////////////////////////////
			if (p==trigger)
			{
				boolean input = getTrigger().getValue();
				if(input && !last)
				{
					last = input;
					calculate();
				}
				else
				{
					last = input;
				}
			}
		}
	}

  

  
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** CREATES THE REQUIRED SLOTS.   *////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void slots(double MD) throws Exception
	{
		for(int i=1; i<(MD+1 ); i++)
		{
			if(((BObject)get("In_"+i))==null) 
			{
				getProgram().add(("In_"+i), new BStatusString(""), Flags.SUMMARY);
			}
		}

		for(int i=(int)MD+1;((BObject)get("In_"+i))!=null;i++)
		{
			if(((BObject)get("In_"+i))!=null) 
			{
				getProgram().remove("In_"+i);
			}            
		}
	}

	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** CONCAT ALL THE INPUTS TOGETHER   */////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void calculate()
	{
		String	delim									= getInDelimiter().getValue();
		boolean	nullOnNoLink							= getInNullOnNoLink().getValue();
		int		slotCount								= (int) getNumberOfValues().getValue();
		String	strOutNoDelimeters						= ""; //1 ONLY GOOD VALUES NO DELIMETER
		String	strOutDelimitValuesOnly					= ""; //2 ONLY GOOD VALUES ALONG WITH DELIMETER
		String	strOutDelimitAll						= ""; //3 ALL VALUES ALONG WITH DELIMETER
		String	strOutDelimitValuesOnlyPlusTimestamp	= ""; //4 LIKE strOutDelimitValuesOnly BUT WITH TIMESTAMP
		String	strOutDelimitAllPlusTimestamp			= ""; //5 LIKE strOutDelimitAll BUT WITH TIMESTAMP
		String	temp			= "";
		
		for(int i=1;i<=slotCount;i++)
		{
			BStatusString inValue = ((BStatusString) ((BObject)get("In_"+i)));
			
			logger.trace("\r\n\t\t" + getSlotPath()	+ "\r\n\t\tBEGINING OF FOR LOOP..."
													+ "\r\n\t\tSLOT  = " + i 
													+ "\r\n\t\tVALUE = " + inValue
													+ "\r\n\t\tOUT1  = " + strOutNoDelimeters
													+ "\r\n\t\tOUT2  = " + strOutDelimitValuesOnly
													+ "\r\n\t\tOUT3  = " + strOutDelimitAll
													+ "\r\n\t\tOUT4  = " + strOutDelimitValuesOnlyPlusTimestamp
													+ "\r\n\t\tOUT5  = " + strOutDelimitAllPlusTimestamp);
				
			/** TESTS WHETHER THE SLOT IS LINKED. IF NOT, THE VALUE IS SET TO NULL **/
			if ( (getProgram().getLinks(getProperty("In_"+i)).length == 0) && (nullOnNoLink==true))
			{
				inValue.setStatusNull(true); 
			}

			// CREATE TEMP STRING TO HOLD THE VALID VALUE OF THE INPUT SLOT ///////////////////////////////////
			temp = "";
			if(inValue.getStatus().isValid())
			{
				temp=inValue.getValue();
			}
			else
			{
				temp="";
			}
			
			
			/** HANDLER FOR THE FIRST SLOT *///////////////////////////////////////////////////////////////////
			if( i==1 ) ////////////////////////////////////////////////////////////////////////////////////////
			{
					if (temp.length() > 0) //CONTAINS DATA
					{
						
						strOutNoDelimeters						= inValue.getValue();
						strOutDelimitValuesOnly					= inValue.getValue() + delim;
						strOutDelimitAll						= inValue.getValue() + delim;
						strOutDelimitValuesOnlyPlusTimestamp	= inValue.getValue() + delim;
						strOutDelimitAllPlusTimestamp			= inValue.getValue() + delim;
					}
					else
					{
						strOutNoDelimeters						= "";
						strOutDelimitValuesOnly					= "";
						strOutDelimitAll						= "" + delim;
						strOutDelimitValuesOnlyPlusTimestamp	= "";
						strOutDelimitAllPlusTimestamp			= "" + delim;
					}
			}
			
			/** HANDLER FOR SLOTS AFTER THE FIRST BUT NOT THE LAST *///////////////////////////////////////////
			if( i>1 && i<slotCount ) //////////////////////////////////////////////////////////////////////////
			{
					if (temp.length() > 0) //CONTAINS DATA
					{
						strOutNoDelimeters						= strOutNoDelimeters + inValue.getValue();
						strOutDelimitValuesOnly					= strOutDelimitValuesOnly + inValue.getValue() + delim;
						strOutDelimitAll						= strOutDelimitAll + inValue.getValue() + delim;
						strOutDelimitValuesOnlyPlusTimestamp	= strOutDelimitValuesOnlyPlusTimestamp + inValue.getValue() + delim;
						strOutDelimitAllPlusTimestamp			= strOutDelimitAllPlusTimestamp + inValue.getValue() + delim;
					}
					else
					{
						strOutDelimitAll				= strOutDelimitAll.trim() + delim;
						strOutDelimitAllPlusTimestamp	= strOutDelimitAllPlusTimestamp.trim() + delim;
					}
			}

			/** HANDLER FOR THE LAST SLOT *////////////////////////////////////////////////////////////////////
			if( i==slotCount ) ////////////////////////////////////////////////////////////////////////////////
			{
					if (temp.length() > 0) //CONTAINS DATA
					{
						strOutNoDelimeters						= strOutNoDelimeters + inValue.getValue();
						strOutDelimitValuesOnly					= strOutDelimitValuesOnly + inValue.getValue();
						strOutDelimitAll						= strOutDelimitAll + inValue.getValue();
						strOutDelimitValuesOnlyPlusTimestamp	= strOutDelimitValuesOnlyPlusTimestamp + inValue.getValue() + delim + currentTime();
						strOutDelimitAllPlusTimestamp			= strOutDelimitAllPlusTimestamp + inValue.getValue() + delim + currentTime();
					}
					else
					{
						strOutDelimitValuesOnly = strOutDelimitValuesOnly.substring(0,(strOutDelimitValuesOnly.length()-delim.length()));
						strOutDelimitValuesOnlyPlusTimestamp = strOutDelimitValuesOnlyPlusTimestamp + currentTime();
						strOutDelimitAllPlusTimestamp = strOutDelimitAllPlusTimestamp + delim + currentTime();
					}
			}
			logger.trace("\r\n\t\t" + getSlotPath()	+ "\r\n\t\tEND OF FOR LOOP..."
													+ "\r\n\t\tSLOT  = " + i 
													+ "\r\n\t\tVALUE = " + inValue
													+ "\r\n\t\tOUT1  = " + strOutNoDelimeters
													+ "\r\n\t\tOUT2  = " + strOutDelimitValuesOnly
													+ "\r\n\t\tOUT3  = " + strOutDelimitAll
													+ "\r\n\t\tOUT4  = " + strOutDelimitValuesOnlyPlusTimestamp
													+ "\r\n\t\tOUT5  = " + strOutDelimitAllPlusTimestamp);
		}
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
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**    ASSIGN CURRENT TIME TO STRING VALUE   */////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String currentTime()
	{
		BFacets facets1 = BFacets.make("timeFormat", BString.make("MM//DD//YYYY hh:mm:ss a"));
		BFacets facets2 = BFacets.make("showMilliseconds", true);
		BFacets facets3 = BFacets.make(facets1,facets2);

		String time = BAbsTime.now().toString(facets3);  
		return time;
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Type	*//////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final Log logger = Log.getLog("axCommunity.SuperConcatPlus");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BSuperConcatPlus.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");

}

