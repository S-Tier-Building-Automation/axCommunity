package org.axcommunity.niagara.conversion;

import javax.baja.log.Log;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BString;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;


/**
 * This object accepts Status String inputs representing a value in ASCII, Hex or Decimal format 
 * and will output its corresponding value in ASCII, Hex and Decimal format.
 * 
 * Outputs are calculated upon change of value of inputs.
 * When one input value changes the other input values are cleared
 * to minimize the confusion as to what the outputs values are representing.
 *
 * The conversion technique was borrowed and modified from the article
 * "How to convert Hex to ASCII in Java" written by mkyong.
 *
 * @author		Justin Koffler
 * @creation	5 Feb 12
 */



public class BAsciiHexDecConversion extends BComponent
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	INPUTS   //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**STATUS STRING MULTI-LINE INPUT, ASCII, THE ASCII VALUE IN STRING FORMAT YOU WANT TO CONVERT TO HEX AND DECIMAL FORMAT. */
	public static final Property inAscii = newProperty(Flags.SUMMARY, new BStatusString(),BFacets.make("multiLine",true));
	public BStatusString getInAscii() { return (BStatusString)get(inAscii);}
	public void setInAscii(BStatusString v) {set(inAscii,v);}
	
	/**STATUS STRING INPUT, Hex, THE HEX VALUE IN STRING FORMAT YOU WANT TO CONVERT TO ASCII AND DECIMAL FORMAT. */
	public static final Property inHex = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getInHex() { return (BStatusString)get(inHex);}
	public void setInHex(BStatusString v) {set(inHex,v);}
	
	/**STATUS STRING INPUT, Decimal, THE DECIMAL VALUE IN STRING FORMAT YOU WANT TO CONVERT TO ASCII AND HEX. */
	public static final Property inDecimal = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getInDecimal() { return (BStatusString)get(inDecimal);}
	public void setInDecimal(BStatusString v) {set(inDecimal,v);}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**STATUS STRING MULTI-LINE OUTPUT, Ascii, REPRESENTS THE ASCII VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Property outAscii = newProperty(Flags.SUMMARY, new BStatusString(),BFacets.make("multiLine",true));
	public BStatusString getOutAscii() { return (BStatusString)get(outAscii);}
	public void setOutAscii(BStatusString v) {set(outAscii,v);}
	
	/**STATUS STRING OUTPUT, Hex, REPRESENTS THE HEX VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Property outHex = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getOutHex() { return (BStatusString)get(outHex);}
	public void setOutHex(BStatusString v) {set(outHex,v);}
	
	/**STATUS STRING OUTPUT, Decimal, REPRESENTS THE DECIMAL VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Property outDecimal = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getOutDecimal() { return (BStatusString)get(outDecimal);}
	public void setOutDecimal(BStatusString v) {set(outDecimal,v);}
	
	/**STATUS NUMERIC OUTPUT, Length, REPRESENTS THE NUMBER OF CHARACTERS IN THE STRING VALUE. */
	public static final Property outLength  = newProperty(Flags.SUMMARY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLength() {return (BStatusNumeric) get(outLength); }
	public void setOutLength(BStatusNumeric v) {set(outLength, v);}
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	TOPIC SLOTS   /////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**TOPIC SLOT OF STATUS STRING VALUE REPRESENTING THE ASCII VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Topic SetAscii = newTopic(0);
	public void fireSetAscii(BString event){fire(SetAscii,event,null);}
	
	/**TOPIC SLOT OF STATUS STRING VALUE REPRESENTING THE HEX VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Topic SetHex = newTopic(0);
	public void fireSetHex(BString event){fire(SetHex,event,null);}
	
	/**TOPIC SLOT OF STATUS STRING VALUE REPRESENTING THE DECIMAL VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Topic SetDecimal = newTopic(0);
	public void fireSetDecimal(BString event){fire(SetDecimal,event,null);}

	
	//---------------------------------------------------------------------------------------------------------
	//	SOURCE CODE BELOW HERE	-------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**    METHOD INVOKED WHEN ANY OF THE INPUTS CHANGES VALUES   *////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void changed(Property prop, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning())return;
		if(isRunning())
		{
			String	strInAscii	= getInAscii().getValue();
			String	strInHex	= getInHex().getValue();
			String	strInDec	= getInDecimal().getValue();
			
			String	sAscii		= "";
			String	sHex		= "";
			String	sDec		= "";
				
			// CHECKS TO SEE IF INPUT "inAscii" HAS CHANGED ///////////////////////////////////////////////////
			if(prop==inAscii && strInAscii.length()>0)
			{
				// getInAscii().setValue("");
				getInHex().setValue("");
				getInDecimal().setValue("");
				
				sAscii	= strInAscii;
				sHex	= convertStringToHex(strInAscii);
				sDec	= convertHexToDecimal(sHex);
				
				// SET OUTPUT VALUES
				getOutAscii().setValue(sAscii);
				getOutHex().setValue(sHex);
				getOutDecimal().setValue(sDec);
				getOutLength().setValue(sAscii.length());
				
				// FIRE TOPIC SLOTS
				fireSetAscii(BString.make(sAscii));
				fireSetHex(BString.make(sHex));
				fireSetDecimal(BString.make(sDec));
			}
			
			// CHECKS TO SEE IF INPUT "inHex" HAS CHANGED /////////////////////////////////////////////////////
			if(prop==inHex && strInHex.length()>0)
			{
				getInAscii().setValue("");
				// getInHex().setValue("");
				getInDecimal().setValue("");
				
				strInHex	= removeSpaces(strInHex);
				// strInDec	= removeSpaces(strInDec);
				
				sAscii	= convertHexToString(strInHex);
				sHex	= strInHex;
				sDec	= convertHexToDecimal(sHex);
				
				// SET OUTPUT VALUES
				getOutAscii().setValue(sAscii);
				getOutHex().setValue(sHex);
				getOutDecimal().setValue(sDec);
				getOutLength().setValue(sAscii.length());
				
				// FIRE TOPIC SLOTS
				fireSetAscii(BString.make(sAscii));
				fireSetHex(BString.make(sHex));
				fireSetDecimal(BString.make(sDec));
			}
			
			// CHECKS TO SEE IF INPUT "inDecimal" HAS CHANGED /////////////////////////////////////////////////
			if(prop==inDecimal && strInDec.length()>0)
			{
				getInAscii().setValue("");
				getInHex().setValue("");
				// getInDecimal().setValue("");
				
				// strInHex	= removeSpaces(strInHex);
				strInDec	= removeSpaces(strInDec);
				
				sAscii	= convertDecToString(strInDec);
				sHex	= convertStringToHex(sAscii);
				sDec	= strInDec;
				
				// SET OUTPUT VALUES
				getOutAscii().setValue(sAscii);
				getOutHex().setValue(sHex);
				getOutDecimal().setValue(sDec);
				getOutLength().setValue(sAscii.length());
				
				// FIRE TOPIC SLOTS
				fireSetAscii(BString.make(sAscii));
				fireSetHex(BString.make(sHex));
				fireSetDecimal(BString.make(sDec));
			}
		
		
		
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**	"convertStringToHex" ACCEPTS A STRING INPUT REPRESENTED IN ASCII FORMAT AND RETURNS ITS HEX VALUE *////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String convertStringToHex(String str)
	{
		try
		{
			char[] chars = str.toCharArray();

			StringBuffer hex = new StringBuffer();
			for(int i = 0; i < chars.length; i++)
			{
				hex.append(Integer.toHexString((int)chars[i]));
			}
			String strToHex = hex.toString().toUpperCase();
			if( strToHex.length() == 1 ) strToHex = "0" + strToHex;
			
			logger.trace( "\r\n\t\t" + getSlotPath()	
						+ "\r\n\t\t convertStringToHex() results..." 
						+ "\r\n\t\t FROM:"
						+ "\r\n\t\t '" + str + "'"
						+ "\r\n\t\t TO:"
						+ "\r\n\t\t '" + strToHex + "'");
						
			return strToHex;
		}
		catch (Exception e) 
		{
			logger.error(		"\r\n\t\t" + getSlotPath()	
							+	"\r\n\t\t" + "ERROR IN convertStringToHex() METHOD!"
							+	"\r\n\t\t" + e.getMessage() 
							+	"\r\n\t\t" + e.getStackTrace());
			getOutAscii().setValue("");
			getOutHex().setValue("");
			getOutDecimal().setValue("");
			return "";
		}
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**	"convertHexToString" ACCEPTS A STRING INPUT REPRESENTED IN HEX FORMAT AND RETURNS ITS ASCII VALUE *////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String convertHexToString(String hex)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			StringBuilder temp = new StringBuilder();

			//49204c6f7665204a617661 split into two characters 49, 20, 4c...
			for( int i=0; i<hex.length()-1; i+=2 )
			{
				//GRAB THE HEX IN PAIRS
				String output = hex.substring(i, (i + 2));
				//CONVERT HEX TO DECIMAL
				int decimal = Integer.parseInt(output, 16);
				//convert the decimal to character
				sb.append((char)decimal);
				temp.append(decimal);
			}
			String hexToString = sb.toString();
			
			logger.trace( "\r\n\t\t" + getSlotPath()	
						+ "\r\n\t\t convertHexToString() results..." 
						+ "\r\n\t\t FROM:"
						+ "\r\n\t\t '" + hex + "'"
						+ "\r\n\t\t TO:"
						+ "\r\n\t\t '" + hexToString + "'");
						
			return hexToString;
		}
		catch (Exception e) 
		{
			logger.error(		"\r\n\t\t" + getSlotPath()	
							+	"\r\n\t\t" + "ERROR IN convertHexToString() METHOD!"
							+	"\r\n\t\t" + e.getMessage() 
							+	"\r\n\t\t" + e.getStackTrace());
			getOutAscii().setValue("");
			getOutHex().setValue("");
			getOutDecimal().setValue("");
			return "";
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**	"convertDecToString" ACCEPTS A STRING INPUT REPRESENTED IN DECIMAL FORMAT AND RETURNS ITS ASCII VALUE */
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String convertDecToString(String dec)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			StringBuilder temp = new StringBuilder();

			//49204c6f7665204a617661 split into two characters 49, 20, 4c...
			for( int i=0; i<dec.length()-1; i+=2 )
			{
				//grab the dec in pairs
				String output = dec.substring(i, (i + 2));
				//convert string to integer
				int decimal = Integer.parseInt(output);
				//convert the decimal to character
				sb.append((char)decimal);
				temp.append(decimal);
				
			}
			String decToString = sb.toString();
			
			logger.trace( "\r\n\t\t" + getSlotPath()	
						+ "\r\n\t\t convertDecToString() results..." 
						+ "\r\n\t\t FROM:"
						+ "\r\n\t\t '" + dec + "'"
						+ "\r\n\t\t TO:"
						+ "\r\n\t\t '" + decToString + "'");
						
			return decToString;
		}
		catch (Exception e) 
		{
			logger.error(		"\r\n\t\t" + getSlotPath()	
							+	"\r\n\t\t" + "ERROR IN convertDecToString() METHOD!"
							+	"\r\n\t\t" + e.getMessage() 
							+	"\r\n\t\t" + e.getStackTrace());
			getOutAscii().setValue("");
			getOutHex().setValue("");
			getOutDecimal().setValue("");
			return "";
		}
	}
		
		
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**	"convertHexToDecimal" ACCEPTS A STRING INPUT REPRESENTED IN HEX FORMAT AND RETURNS ITS DECIMAL VALUE *////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String convertHexToDecimal(String hex)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			StringBuilder temp = new StringBuilder();

			//49204c6f7665204a617661 split into two characters 49, 20, 4c...
			for( int i=0; i<hex.length()-1; i+=2 )
			{
				//grab the hex in pairs
				String output = hex.substring(i, (i + 2));
				//convert hex to decimal
				int decimal = Integer.parseInt(output, 16);
				//convert the decimal to character
				sb.append((char)decimal);
				temp.append(decimal);
			}
			String hexToDec = temp.toString();
			
			logger.trace( "\r\n\t\t" + getSlotPath()	
						+ "\r\n\t\t convertHexToDecimal() results..." 
						+ "\r\n\t\t FROM:"
						+ "\r\n\t\t '" + hex + "'"
						+ "\r\n\t\t TO:"
						+ "\r\n\t\t '" + hexToDec + "'");
						
			return hexToDec;
		}
		catch (Exception e) 
		{
			logger.error(		"\r\n\t\t" + getSlotPath()	
							+	"\r\n\t\t" + "ERROR IN convertHexToDecimal() METHOD!"
							+	"\r\n\t\t" + e.getMessage() 
							+	"\r\n\t\t" + e.getStackTrace());
			getOutAscii().setValue("");
			getOutHex().setValue("");
			getOutDecimal().setValue("");
			return "";
		}
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**	"removeSpaces" ACCEPTS A STRING INPUT AND RETURNS THE SAME STRING MINUS ANY SPACES *///////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String removeSpaces(String strIn)
	{
		try
		{
			String	stringToReplace		= " "; //SPACE
			String	replacementString	= ""; // NO SPACE
			String	newString			= ""; 

			
			int idx = strIn.lastIndexOf( stringToReplace );
			if ( idx != -1 ) 
			{
				StringBuffer results = new StringBuffer( strIn );
				results.replace( idx, idx+stringToReplace.length(), replacementString );
				while( (idx=strIn.lastIndexOf(stringToReplace, idx-1)) != -1 ) 
				{
					results.replace( idx, idx+stringToReplace.length(), replacementString );
				}
				newString = results.toString();
			}
			else
			{
				newString = strIn;
			}
			
			logger.trace( "\r\n\t\t" + getSlotPath()	
						+ "\r\n\t\t removeSpaces() results..." 
						+ "\r\n\t\t FROM:"
						+ "\r\n\t\t '" + strIn + "'"
						+ "\r\n\t\t TO:"
						+ "\r\n\t\t '" + newString + "'");
			
			return newString;
		}
		catch (Exception e) 
		{
			logger.error(		"\r\n\t\t" + getSlotPath()	
							+	"\r\n\t\t" + "ERROR IN removeSpaces() METHOD!"
							+	"\r\n\t\t" + e.getMessage() 
							+	"\r\n\t\t" + e.getStackTrace());
			getOutAscii().setValue("");
			getOutHex().setValue("");
			getOutDecimal().setValue("");
			return "";
		}
	}

	// ON START AND ON STOP RELATED STUFF. //////////////////////////////////////////////////////////////////
	public void started() throws Exception { try { onStart(); } catch(Throwable t) { throw new Exception(t); } }
	public void stopped() throws Exception { try { onStop(); } catch(Throwable t) { throw new Exception(t); } }
	
	public void onStart() throws Exception{	}
	public void onStop() throws Exception{ }
	

	public static final Log logger = Log.getLog("axCommunity.AsciiHexDecConversion");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BAsciiHexDecConversion.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");


}
