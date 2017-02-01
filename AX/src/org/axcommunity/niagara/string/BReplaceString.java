package org.axcommunity.niagara.string;

import javax.baja.log.Log;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;


/**
 * Replaces string subset from input string with new value and outputs the results.
 *
 * @author    Justin Koffler
 * @creation  02/18/2012
 * @company   Kors Engineering
 */
 
public class BReplaceString extends BComponent
{
	public boolean FoundNormalReplacement;
	public boolean FoundNonAlphaNumeric;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	ACTION SLOTS   ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Action Execute = newAction(0|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void Execute(){invoke(Execute,null,null);}
	// public void doExecute(){} // Method actions are below on change method.
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	INPUTS   //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**Status String value in representing string to search for text to replace.*/
	public static final Property inString = newProperty(0|Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInString() { return (BStatusString)get(inString);}
	public void setInString(BStatusString v) {set(inString,v);}  

	/**Status String value in representing text to search for and replace.*/
	public static final Property inStringToReplace = newProperty(0|Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInStringToReplace() { return (BStatusString)get(inStringToReplace);}
	public void setInStringToReplace(BStatusString v) {set(inStringToReplace,v);}

	/**Status String value in representing text to use in place of searched text.*/
	public static final Property inReplacementString = newProperty(0|Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInReplacementString() { return (BStatusString)get(inReplacementString);}
	public void setInReplacementString(BStatusString v) {set(inReplacementString,v);}
	
	/**When TRUE all non-alphanumeric characters will be replaced with the value from 'inNonAlphaNumericReplacementString'*/
	public final static Property inRemoveNonAlphaNumericCharacters = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getInRemoveNonAlphaNumericCharacters() { return (BStatusBoolean)get(inRemoveNonAlphaNumericCharacters); }
	public void setInRemoveNonAlphaNumericCharacters(BStatusBoolean v) { set(inRemoveNonAlphaNumericCharacters, v); }
	
	/**Character to use as replacement for non-alphanumeric characters. This must only be a SINGLE character*/
	public static final Property inNonAlphaNumericReplacementString = newProperty(0, new BStatusString());
	public BStatusString getInNonAlphaNumericReplacementString() { return (BStatusString)get(inNonAlphaNumericReplacementString);}
	public void setInNonAlphaNumericReplacementString(BStatusString v) {set(inNonAlphaNumericReplacementString,v);}

	/**When TRUE if the first or last charactes are one of a replacement character it will be removed.*/
	public final static Property inTrimReplacementsFromFinalOutput = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getInTrimReplacementsFromFinalOutput() { return (BStatusBoolean)get(inTrimReplacementsFromFinalOutput); }
	public void setInTrimReplacementsFromFinalOutput(BStatusBoolean v) { set(inTrimReplacementsFromFinalOutput, v); }
	
	/**STATUS BOOLEAN INPUT, PreventConsecutiveReplacements*/
	public final static Property inPreventConsecutiveReplacements = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getInPreventConsecutiveReplacements() { return (BStatusBoolean)get(inPreventConsecutiveReplacements); }
	public void setInPreventConsecutiveReplacements(BStatusBoolean v) { set(inPreventConsecutiveReplacements, v); }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**Status String value out representing the new text with replaced values.*/
	public static final Property outString = newProperty(0|Flags.SUMMARY, new BStatusString(""));
	public BStatusString getOutString() { return (BStatusString)get(outString);}
	public void setOutString(BStatusString v) {set(outString,v);}

	/**Status Boolean Out representing whether or not the searched string was found and replaced.*/
	public static final Property outStringFound = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getOutStringFound() { return (BStatusBoolean)get(outStringFound);}
	public void setOutStringFound(BStatusBoolean v) {set(outStringFound,v);}
	
	/**True is a non-alphanumeric character is found and the option to replace non-alphanumeric is true.*/
	public final static Property outNonAlphaNumericFound = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getOutNonAlphaNumericFound() { return (BStatusBoolean)get(outNonAlphaNumericFound); }
	public void setOutNonAlphaNumericFound(BStatusBoolean v) { set(outNonAlphaNumericFound, v); }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	TOPIC SLOTS   /////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final Topic NewStringResults = newTopic(0);
	public void fireNewStringResults(BString event){fire(NewStringResults,event,null);}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Method invoked when any of the inputs changes values */////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void changed(Property prop, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning())return;
		if(isRunning())
		{
			// if(prop == inString || prop == inStringToReplace || prop == inReplacementString) 
			if(prop.getName().startsWith("in"))
			{
				logger.trace("\t" + getSlotPath()	+ "\tCHANGED: " + prop.getName() + "\t" +  getInString().getValue());
				doExecute();
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Finds the string to replace and replaces with replacement string then outputs results. *///////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void doExecute()
	{
		try
		{
			String	strIn				= getInString().getValue();
			String	strWorking			= "";
			String	output				= "";

			FoundNormalReplacement		= false;
			FoundNonAlphaNumeric		= false;
			strWorking					= strIn;
			
			logger.trace("\t" + getSlotPath()	+ "\t" + "doExecute(), [1] strWorking: '" + strWorking + "', initial value");
			
			//--------------------------------------------------------------------------------------------------
			//-- REMOVE NON-ALPHNUMERIC CHARACTERS BEFORE PROCESSING OTHER REPLACEMENTS ------------------------
			//--------------------------------------------------------------------------------------------------
			if(getInRemoveNonAlphaNumericCharacters().getValue()==true)
			{
				strWorking = removeNonAlphaNumeric(strWorking);
			}
			
			logger.trace("\t" + getSlotPath()	+ "\t" + "doExecute(), [2] strWorking: '" + strWorking + "', after removeNonAlphaNumeric()");
			
			//--------------------------------------------------------------------------------------------------
			//-- NOW PROCESS NORMAL STRING REPLACEMENT ---------------------------------------------------------
			//--------------------------------------------------------------------------------------------------
			strWorking = processReplacement(strWorking, getInStringToReplace().getValue(), getInReplacementString().getValue(), true);
			
			logger.trace("\t" + getSlotPath()	+ "\t" + "doExecute(), [3] strWorking: '" + strWorking + "', after processReplacement()");
			//--------------------------------------------------------------------------------------------------
			//-- DO THIS LAST ----------------------------------------------------------------------------------
			//--------------------------------------------------------------------------------------------------
			if(getInTrimReplacementsFromFinalOutput().getValue()==true)
			{
				output = trimReplacements(strWorking);
			}
			else
			{
				output = strWorking;
			}
			
			logger.trace("\t" + getSlotPath()	+ "\t" + "doExecute(), [4] strWorking: '" + strWorking + "', after trimReplacements()");
			
			getOutString().setValue(output);
			getOutStringFound().setValue(FoundNormalReplacement);
			getOutNonAlphaNumericFound().setValue(FoundNonAlphaNumeric);
			fireNewStringResults(BString.make(output));
		}
		catch (Exception e) 
		{
			logger.error( "\n" + getSlotPath()	
						+ "\n" + "Method             = " + "doExecute()" 
						+ "\n" + "getMessage         = " + e.getMessage() 
						+ "\n" + "getStackTrace      = " + e.getStackTrace() 
						+ "\n" + "toString           = " + e.toString());
						
			getOutString().setValue("");
			getOutStringFound().setValue(false);
			getOutNonAlphaNumericFound().setValue(false);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Finds the string to be replaced and replaces with new string. *////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String processReplacement(String input, String stringToReplace, String replacementString, boolean NormalReplacement)
	{
		String	output	= input;
		int		idx		= input.lastIndexOf( stringToReplace );
		
		try
		{
			if ( idx != -1 ) 
			{
				StringBuffer results = new StringBuffer( input );
				results.replace( idx, idx+stringToReplace.length(), replacementString );
				
				while( (idx=input.lastIndexOf(stringToReplace, idx-1)) != -1 ) 
				{
					results.replace( idx, idx+stringToReplace.length(), replacementString );
				}
				
				
				//*********************************
				if(NormalReplacement==true)
				{
					FoundNormalReplacement=true;
				}
				else
				{
					FoundNonAlphaNumeric=true;
				}
				//*********************************
				
				if(getInPreventConsecutiveReplacements().getValue()==true && replacementString.length()>0)
				{
					output = removeDuplicateReplacements(results.toString(), replacementString+replacementString, replacementString);
				}
				else
				{
					output	= results.toString();
				}
			}
			else
			{
				output = input;
			}
		}
		catch (Exception e) 
		{
			logger.error( "\n" + getSlotPath()	
						+ "\n" + "Method             = " + "processReplacement()" 
						+ "\n" + "input              = '" + input + "', LENGTH = '" + input.length() + "'"
						+ "\n" + "stringToReplace    = '" + stringToReplace + "', LENGTH = '" + stringToReplace.length() + "'"
						+ "\n" + "replacementString  = '" + replacementString + "', LENGTH = '" + replacementString.length() + "'"
						+ "\n" + "getMessage         = " + e.getMessage() 
						+ "\n" + "getStackTrace      = " + e.getStackTrace() 
						+ "\n" + "toString           = " + e.toString());
						
			FoundNormalReplacement	= false;
			FoundNonAlphaNumeric	= false;
			output 					= "";
		}
		
		return output;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Finds the non-alpha and non-numeric characters and replaces them with the replacement string*//////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String removeNonAlphaNumeric(String input)
	{
		String			replacementString	= getInNonAlphaNumericReplacementString().getValue();
		StringBuffer	inputChars			= new StringBuffer( input );
		String			output				= input;

		try
		{
			for(int i = 0; i <=255;i++)
			{
				// Ascii 0-9 = 48-57 decimal, Ascii A-Z = 65-90 decimal, Ascii a-z = 97-122 decimal
				if( !(i>=48 && i<=57) && !(i>=65 && i<=90) && !(i>=97 && i<=122) )
				{
					output = processReplacement(output, String.valueOf(Character.toChars(i)), replacementString, false);
				}
			}
		}
		catch (Exception e) 
		{
			logger.error( "\n" + getSlotPath()	
						+ "\n" + "Method             = " + "removeNonAlphaNumeric()" 
						+ "\n" + "input              = '" + input + "', LENGTH = '" + input.length() + "'"
						+ "\n" + "getMessage         = " + e.getMessage() 
						+ "\n" + "getStackTrace      = " + e.getStackTrace() 
						+ "\n" + "toString           = " + e.toString());
						
			FoundNormalReplacement	= false;
			FoundNonAlphaNumeric	= false;
			output 					= "";
		}
		return output;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Finds any double replacements and replaces with single replacement. *//////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String removeDuplicateReplacements(String input, String stringToReplace, String replacementString)
	{
		String	output	= input;
		int		idx		= input.lastIndexOf( stringToReplace );
		
		try
		{
			while(output.lastIndexOf(stringToReplace)>-1)
			{
				output = output.replace(stringToReplace, replacementString);
			}   
		}
		catch (Exception e) 
		{
			logger.error( "\n" + getSlotPath()	
						+ "\n" + "Method             = "	+ "removeDuplicateReplacements()" 
						+ "\n" + "input              = '" + input + "', LENGTH = '" + input.length() + "'"
						+ "\n" + "stringToReplace    = '" + stringToReplace + "', LENGTH = '" + stringToReplace.length() + "'"
						+ "\n" + "replacementString  = '" + replacementString + "', LENGTH = '" + replacementString.length() + "'"
						+ "\n" + "getMessage         = " + e.getMessage() 
						+ "\n" + "getStackTrace      = " + e.getStackTrace() 
						+ "\n" + "toString           = " + e.toString());
						
			FoundNormalReplacement	= false;
			FoundNonAlphaNumeric	= false;
			output 					= "";
		}
		
		return output;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Removes any replacement strings from the beginning and end of output string. */////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String trimReplacements(String input)
	{
		String	output				= input;
		
		if(input.length() > 0)
		{
			String	replacementString1	= getInReplacementString().getValue();
			String	replacementString2	= getInNonAlphaNumericReplacementString().getValue();
			
			try
			{
				if(replacementString1.length()>0)
				{
					while( output.substring(0, replacementString1.length()).equals(replacementString1) &&  output.length()>replacementString1.length() ) 
					{
						output = output.substring(replacementString1.length());
					}
					
					while( (output.substring(output.length()-replacementString1.length()).compareTo(replacementString1)==0) &&  output.length()>replacementString1.length() )
					{
						output = output.substring(0,output.length()-replacementString1.length());
					}
				}
				
				if(replacementString2.length()>0)
				{
					while( output.substring(0, replacementString2.length()).equals(replacementString2) &&  output.length()>replacementString2.length() ) 
					{
						output = output.substring(replacementString2.length());
					}
					
					while( (output.substring(output.length()-replacementString2.length()).compareTo(replacementString2)==0) &&  output.length()>replacementString2.length() )
					{
						output = output.substring(0,output.length()-replacementString2.length());
					}
				}
			}
			catch (Exception e) 
			{
				logger.error( "\n" + getSlotPath()	
							+ "\n" + "Method             = " + "trimReplacements()" 
							+ "\n" + "input              = '" + input + "', LENGTH = '" + input.length() + "'"
							+ "\n" + "getMessage         = " + e.getMessage() 
							+ "\n" + "getStackTrace      = " + e.getStackTrace() 
							+ "\n" + "toString           = " + e.toString());
							
				FoundNormalReplacement	= false;
				FoundNonAlphaNumeric	= false;
				output 					= "";
			}
		}
		
		logger.trace("\t" + getSlotPath()	+ "\t trimReplacements() returning '" + output + "'");
		
		return output;
	}
	
	public static final Log logger = Log.getLog("axCommunity.ReplaceString");

	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BReplaceString.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");
}
