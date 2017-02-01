package org.axcommunity.niagara.string;

import java.util.logging.*;
import javax.baja.log.Log;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;



/**
 * Replaces string subset from input string with new value and outputs the results.
 *
 * @author		Justin Koffler, Texas Power Systems
 * @version		16.05.10
 */
 
public class BReplaceString extends BComponent
{
	private static String[] caseTags = {"CaseSensitive", "CaseInsensitive"};
	private static BEnumRange caseRange  = BEnumRange.make(caseTags);
	
	private boolean FoundNormalReplacement;
	private boolean FoundNonAlphaNumeric;
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	ACTION SLOTS   ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Action Execute = newAction(0|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void Execute(){invoke(Execute,null,null);}
	// public void doExecute(){} // Method actions are below on change method.
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	INPUTS   //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**ENUM INPUT, caseSensitivity, CASE SENSITIVITY TO USE FOR COMPARISON.*/
	public static final Property caseSensitivity = newProperty(0, BDynamicEnum.make(0, caseRange),null);
	public BDynamicEnum getCaseSensitivity() { return (BDynamicEnum)get(caseSensitivity); }
	public void setCaseSensitivity(BDynamicEnum v) { set(caseSensitivity,v,null); }
	
	/**Status String value in representing string to search for text to replace.*/
	public static final Property inString = newProperty(0|Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInString() { return (BStatusString)get(inString);}
	public void setInString(BStatusString v) {set(inString,v);}  

	//********************************************************************************************************************************
	
	/**Status String value in representing text to search for and replace.*/
	public static final Property inStringToReplace = newProperty(0|Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInStringToReplace() { return (BStatusString)get(inStringToReplace);}
	public void setInStringToReplace(BStatusString v) {set(inStringToReplace,v);}

	/** Comma separated values representing the string you wish to search for and replace.*/
	public static final Property inCsvStringsToReplace = newProperty(Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInCsvStringsToReplace() { return (BStatusString)get(inCsvStringsToReplace);}
	public void setInCsvStringsToReplace(BStatusString v) {set(inCsvStringsToReplace,v);}
	
	/** Regular Expression representing the string you wish to search for and replace.*/
	public static final Property inRegExpToReplace = newProperty(Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInRegExpToReplace() { return (BStatusString)get(inRegExpToReplace);}
	public void setInRegExpToReplace(BStatusString v) {set(inRegExpToReplace,v);}
	
	//********************************************************************************************************************************
	
	/**Status String value in representing text to use in place of searched text.*/
	public static final Property inReplacementString = newProperty(Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInReplacementString() { return (BStatusString)get(inReplacementString);}
	public void setInReplacementString(BStatusString v) {set(inReplacementString,v);}
	
	//********************************************************************************************************************************
	
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
	public static final Property outString = newProperty(Flags.SUMMARY, new BStatusString(""));
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
			if (prop == inString			|| prop == inStringToReplace		|| 
				prop == inReplacementString	|| prop == inCsvStringsToReplace	|| 
				prop == inRegExpToReplace	|| prop == caseSensitivity) 
			{
				doExecute();
			}
			

		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Finds the string to replace and replaces with replacement string then outputs results. *///////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void doExecute()
	{
		log.finest("\t" + getSlotPath()	+ "\t doExecute() called...");
		
		try
		{	
			String		strWorking		= getInString().getValue();
			String		output			= "";
			
			FoundNormalReplacement		= false;
			FoundNonAlphaNumeric		= false;
			
			// Process Order:
			// Non-Alpha
			// RegExp
			// Single
			// CSV
			// Trim
			
			
			//--------------------------------------------------------------------------------------------------
			//-- REMOVE NON-ALPHNUMERIC CHARACTERS (IF CONFIGURED) ---------------------------------------------
			//--------------------------------------------------------------------------------------------------
			if(getInRemoveNonAlphaNumericCharacters().getValue()==true)
			{
				strWorking = removeNonAlphaNumeric(strWorking);
			}
			else
			{
				log.finest("\t" + getSlotPath()	+ "\t skipping removeNonAlphaNumeric()");
			}
			
			
			//--------------------------------------------------------------------------------------------------
			//-- REGULAR EXPRESION REPLACEMENT -----------------------------------------------------------------
			//--------------------------------------------------------------------------------------------------
			if(getInRegExpToReplace().getValue().length()>0)
			{
				strWorking = regExpReplacement(strWorking);
			}
			else
			{
				log.finest("\t" + getSlotPath()	+ "\t skipping regExpReplacement()");
			}

			
			//--------------------------------------------------------------------------------------------------
			//-- SINGLE STRING REPLACEMENT ---------------------------------------------------------------------
			//--------------------------------------------------------------------------------------------------
			if(getInStringToReplace().getValue().length()>0)
			{
				strWorking = singleReplacement(strWorking, getInStringToReplace().getValue(), getInReplacementString().getValue(), true);
			}
			else
			{
				log.finest("\t" + getSlotPath()	+ "\t skipping singleReplacement()");
			}
			
			
			//--------------------------------------------------------------------------------------------------
			//-- CSV STRING REPLACEMENT ------------------------------------------------------------------------
			//--------------------------------------------------------------------------------------------------
			if(getInCsvStringsToReplace().getValue().length()>0)
			{
				strWorking = csvReplacement(strWorking);
			}
			else
			{
				log.finest("\t" + getSlotPath()	+ "\t skipping csvReplacement()");
			}

			
			//--------------------------------------------------------------------------------------------------
			//-- TRIM REPLACEMENTS (IF CONFIGURED) -------------------------------------------------------------
			//--------------------------------------------------------------------------------------------------
			if(getInTrimReplacementsFromFinalOutput().getValue()==true)
			{
				strWorking = trimReplacements(strWorking);
			}
			else
			{
				log.finest("\t" + getSlotPath()	+ "\t skipping trimReplacements()");
			}
	
			
			output = strWorking;
		
			
			// ALL DONE WITH REPLACEMENTS, OUTPUT THE RESULTS.
			getOutString().setValue(output);
			getOutStringFound().setValue(FoundNormalReplacement);
			getOutNonAlphaNumericFound().setValue(FoundNonAlphaNumeric);
			fireNewStringResults(BString.make(output));
		}
		catch (Exception e) 
		{
			log.severe( "\n" + getSlotPath()	
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
	/** FIND AND REPLACE USING REGULAR EXPRESSION (CAN'T APPLY THE CASE SENSITIVITY HERE). *////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String csvReplacement(String input)
	{
		log.finest("\t" + getSlotPath()	+ "\t csvReplacement() called with input = '" + input + "'");
		
		String		output					= input;
		String[]	arrStringsToReplace		= getInCsvStringsToReplace().getValue().split(",");
		String		replacementString		= getInReplacementString().getValue();
		boolean		caseSensitive			= true;
		
		switch(getCaseSensitivity().getOrdinal())
		{
			case 0:	caseSensitive	= true; 	break;
			case 1:	caseSensitive	= false;	break;
		}
		
		
		try
		{
			if(getInCsvStringsToReplace().getValue().length()>0)
			{
				for (int i = 0;i<arrStringsToReplace.length;i++)
				{
					String csvStringToReplace	= arrStringsToReplace[i].trim();
					
					if(caseSensitive==true)
					{
						int	idx = input.lastIndexOf( csvStringToReplace );
						if ( idx != -1 ) 
						{
							log.finest("\t" + getSlotPath()	+ "\tinput(" + input.length() + ") Replacing '" + csvStringToReplace + "' with '" + replacementString + "' in '" +  input + "'");
							
							StringBuffer results = new StringBuffer( input );
							results.replace( idx, idx+csvStringToReplace.length(), replacementString );
							while( (idx=input.lastIndexOf(csvStringToReplace, idx-1)) != -1 ) 
							{
								results.replace( idx, idx+csvStringToReplace.length(), replacementString );
							}
							input					= results.toString();
							FoundNormalReplacement	= true;
						}
						else
						{
							log.finest("\t" + getSlotPath()	+ "\tDidn't find '" + csvStringToReplace + "' in '" +  input + "'");
						}
					}
					else
					{
						int	idx = input.toUpperCase().lastIndexOf( csvStringToReplace.toUpperCase() );
						if ( idx != -1 ) 
						{
							log.finest("\t" + getSlotPath()	+ "\tinput(" + input.length() + ") Replacing '" + csvStringToReplace + "' with '" + replacementString + "' in '" +  input + "'");
							
							StringBuffer results = new StringBuffer( input );
							results.replace( idx, idx+csvStringToReplace.length(), replacementString );
							while( (idx=input.toUpperCase().lastIndexOf(csvStringToReplace.toUpperCase(), idx-1)) != -1 ) 
							{
								results.replace( idx, idx+csvStringToReplace.length(), replacementString );
							}
							input					= results.toString();
							FoundNormalReplacement	= true;
						}
						else
						{
							log.finest("\t" + getSlotPath()	+ "\tDidn't find '" + csvStringToReplace + "' in '" +  input + "'");
						}
					}
				}
				
				output = input;
			}
			else
			{
				log.finest("\t" + getSlotPath()	+ "\tinCsvStringsToReplace Not Defined, no replacements made.");
			}
		}
		catch (Exception e) 
		{
			log.severe("\n" + getSlotPath()	+ "\tregExpReplacement()\n" + e.getMessage() + "\n" + e.getStackTrace());
			FoundNormalReplacement	= false;
			FoundNonAlphaNumeric	= false;
			output 					= "";
		}
		
		log.finest("\t" + getSlotPath()	+ "\t csvReplacement() returning '" + output + "'");
		
			
		return output;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** FIND AND REPLACE USING REGULAR EXPRESSION (CAN'T APPLY THE CASE SENSITIVITY HERE). *////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String regExpReplacement(String input)
	{
		log.finest("\t" + getSlotPath()	+ "\t regExpReplacement() called with input = '" + input + "'");
		
		String		output					= input;
		String		regExpToReplace			= getInRegExpToReplace().getValue();
		String		replacementString		= getInReplacementString().getValue();
		
		try
		{
			if(getInRegExpToReplace().getValue().length()>0)
			{
				// FIND AND REPLACE SINGLE LOOKUP VALUE
				int idx = input.lastIndexOf( regExpToReplace );
				if ( idx != -1 ) 
				{
					log.finest("\t" + getSlotPath()	+ "\tinput(" + input.length() + ") Replacing '" + regExpToReplace + "' with '" + replacementString + "' in '" +  input + "'");
					
					output = input.replaceAll(regExpToReplace, replacementString);
					FoundNormalReplacement	= true;
				}
				else
				{
					log.finest("\t" + getSlotPath()	+ "\tDidn't find '" + regExpToReplace + "' in '" +  input + "'");
				}
			}
			else
			{
				log.finest("\t" + getSlotPath()	+ "\tinRegExpToReplace Not Defined, no replacements made.");
			}
			
			
		}
		catch (Exception e) 
		{
			log.severe("\n" + getSlotPath()	+ "\tregExpReplacement()\n" + e.getMessage() + "\n" + e.getStackTrace());
			FoundNormalReplacement	= false;
			FoundNonAlphaNumeric	= false;
			output 					= "";
		}
		
		log.finest("\t" + getSlotPath()	+ "\t regExpReplacement() returning '" + output + "'");
		
			
		return output;
		
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Finds the string to be replaced and replaces with new string. *////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String singleReplacement(String input, String stringToReplace, String replacementString, boolean NormalReplacement)
	{
		log.finest("\t" + getSlotPath()	+ "\t singleReplacement() called with input = '" + input + "'");
		
		String		output			= input;
		boolean		caseSensitive	= true;
			
		switch(getCaseSensitivity().getOrdinal())
		{
			case 0:	caseSensitive	= true; 	break;
			case 1:	caseSensitive	= false;	break;
		}
		
		try
		{
			if(caseSensitive==true)
			{
				int idx = input.lastIndexOf( stringToReplace );
				if ( idx != -1 ) 
				{
					log.finest("\t" + getSlotPath()	+ "\tinput(" + input.length() + ") Replacing '" + stringToReplace + "' with '" + replacementString + "' in '" +  input + "'");
					StringBuffer results = new StringBuffer( input );
					results.replace( idx, idx+stringToReplace.length(), replacementString );
					while( (idx=input.lastIndexOf(stringToReplace, idx-1)) != -1 ) 
					{
						results.replace( idx, idx+stringToReplace.length(), replacementString );
					}
					
					//*********************************
					if(NormalReplacement==true)
					{	FoundNormalReplacement	= true;	}
					else
					{	FoundNonAlphaNumeric	= true;	}
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
					log.finest("\t" + getSlotPath()	+ "\tDidn't find '" + stringToReplace + "' in '" +  input + "'");
					output = input;
				}
			}
			else
			{
				int idx = input.toUpperCase().lastIndexOf( stringToReplace.toUpperCase() );
				if ( idx != -1 ) 
				{
					log.finest("\t" + getSlotPath()	+ "\tinput(" + input.length() + ") Replacing '" + stringToReplace + "' with '" + replacementString + "' in '" +  input + "'");
					StringBuffer results = new StringBuffer( input );
					results.replace( idx, idx+stringToReplace.length(), replacementString );
					while( (idx=input.toUpperCase().lastIndexOf(stringToReplace.toUpperCase(), idx-1)) != -1 ) 
					{
						results.replace( idx, idx+stringToReplace.length(), replacementString );
					}
					
					//*********************************
					if(NormalReplacement==true)
					{	FoundNormalReplacement	= true;	}
					else
					{	FoundNonAlphaNumeric	= true;	}
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
					log.finest("\t" + getSlotPath()	+ "\tDidn't find '" + stringToReplace + "' in '" +  input + "'");
					output = input;
				}
			}
		}
		catch (Exception e) 
		{
			log.severe( "\n" + getSlotPath()	
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
		
		log.finest("\t" + getSlotPath()	+ "\t singleReplacement() returning '" + output + "'");
		
		
		return output;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Finds the non-alpha and non-numeric characters and replaces them with the replacement string*//////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String removeNonAlphaNumeric(String input)
	{
		log.finest("\t" + getSlotPath()	+ "\t removeNonAlphaNumeric() called with input = '" + input + "'");
		
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
					output = singleReplacement(output, String.valueOf(Character.toChars(i)), replacementString, false);
				}
			}
		}
		catch (Exception e) 
		{
			log.severe( "\n" + getSlotPath()	
						+ "\n" + "Method             = " + "removeNonAlphaNumeric()" 
						+ "\n" + "input              = '" + input + "', LENGTH = '" + input.length() + "'"
						+ "\n" + "getMessage         = " + e.getMessage() 
						+ "\n" + "getStackTrace      = " + e.getStackTrace() 
						+ "\n" + "toString           = " + e.toString());
			FoundNormalReplacement	= false;
			FoundNonAlphaNumeric	= false;
			output 					= "";
		}
		
		log.finest("\t" + getSlotPath()	+ "\t removeNonAlphaNumeric() returning '" + output + "'");
		
		return output;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Finds any double replacements and replaces with single replacement. *//////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String removeDuplicateReplacements(String input, String stringToReplace, String replacementString)
	{
		log.finest("\t" + getSlotPath()	+ "\t removeDuplicateReplacements() called with input = '" + input + "'");
		
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
			log.severe( "\n" + getSlotPath()	
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
		
		log.finest("\t" + getSlotPath()	+ "\t removeDuplicateReplacements() returning '" + output + "'");
		
		return output;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Removes any replacement strings from the beginning and end of output string. */////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String trimReplacements(String input)
	{
		log.finest("\t" + getSlotPath()	+ "\t trimReplacements() called with input = '" + input + "'");
		
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
				log.severe( "\n" + getSlotPath()	
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
		
		log.finest("\t" + getSlotPath()	+ "\t trimReplacements() returning '" + output + "'");
		
		return output;
	}
	
	public static final Logger log = Logger.getLogger("axCommunity.ReplaceString");

	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BReplaceString.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");
}
