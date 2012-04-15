package org.axcommunity.niagara.string;

import javax.baja.log.Log;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;


/**
 * Replaces string subset from input string with new value and outputs the results.
 *
 * @author		Justin Koffler, Texas Power Systems
 * @version		12.02.18
 */
 
public class BReplaceString extends BComponent
{
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
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**Status String value out representing the new text with replaced values.*/
	public static final Property outString = newProperty(0|Flags.SUMMARY|Flags.READONLY, new BStatusString(""));
	public BStatusString getOutString() { return (BStatusString)get(outString);}
	public void setOutString(BStatusString v) {set(outString,v);}

	/**Status Boolean Out representing whether or not the searched string was found and replaced.*/
	public static final Property outStringFound = newProperty(0|Flags.READONLY, new BStatusBoolean(false));
	public BStatusBoolean getOutStringFound() { return (BStatusBoolean)get(outStringFound);}
	public void setOutStringFound(BStatusBoolean v) {set(outStringFound,v);}

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
			if (prop == inString || prop == inStringToReplace || prop == inReplacementString) 
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
		String strIn				= getInString().getValue();
		String stringToReplace		= getInStringToReplace().getValue();
		String replacementString	= getInReplacementString().getValue();

		int idx = strIn.lastIndexOf( stringToReplace );
		if ( idx != -1 ) 
		{
			StringBuffer results = new StringBuffer( strIn );
			results.replace( idx, idx+stringToReplace.length(), replacementString );
			while( (idx=strIn.lastIndexOf(stringToReplace, idx-1)) != -1 ) 
			{
				results.replace( idx, idx+stringToReplace.length(), replacementString );
			}
			getOutString().setValue(results.toString());
			getOutStringFound().setValue(true);
			fireNewStringResults(BString.make(results.toString()));
		}
		else
		{
			getOutString().setValue(strIn);
			getOutStringFound().setValue(false);
			fireNewStringResults(BString.make(strIn));
		}
	}
	
	public static final Log logger = Log.getLog("axCommunity.ReplaceString");

	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BReplaceString.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");


}
