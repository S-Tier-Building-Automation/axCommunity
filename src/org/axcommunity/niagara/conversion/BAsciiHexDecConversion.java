package org.axcommunity.niagara.conversion;

import javax.baja.log.Log;
import javax.baja.status.BStatusBoolean;
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
	//    CONFIG INPUTS   /////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**When "true" a delimiter as set in the "inDelimiter" slot will be place between individual hex or decimal pairs. */
	public final static Property inUseDelimiter = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getInUseDelimiter() { return (BStatusBoolean)get(inUseDelimiter); }
	public void setInUseDelimiter(BStatusBoolean v) { set(inUseDelimiter, v); }
	
	/**String to use as a delimiter between individual hex or decimal pairs. */
	public static final Property inDelimiter = newProperty(0, new BStatusString(","));
	public BStatusString getInDelimiter() { return (BStatusString)get(inDelimiter);}
	public void setInDelimiter(BStatusString v) {set(inDelimiter,v);}
	
	/**
	  * Number of Bytes the length of the Decimal output should be.
	  * If input shorter than byte length output will be padded with zeros.
	  * If input longer than byte length output will be truncated.
	*/
	public static final Property inBytesLong  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getInBytesLong() {return (BStatusNumeric) get(inBytesLong); }
	public void setInBytesLong(BStatusNumeric v) {set(inBytesLong, v);}
	
	/**When true the decimal byte length value is hornored.*/
	public final static Property inUseBytesLong = newProperty(0|Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getInUseBytesLong() { return (BStatusBoolean)get(inUseBytesLong); }
	public void setInUseBytesLong(BStatusBoolean v) { set(inUseBytesLong, v); }
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	INPUTS   //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** The ascii value in string format you want to convert to hex and decimal format. */
	public static final Property inAscii = newProperty(0|Flags.SUMMARY, new BStatusString(),BFacets.make("multiLine",true));
	public BStatusString getInAscii() { return (BStatusString)get(inAscii);}
	public void setInAscii(BStatusString v) {set(inAscii,v);}
	
	/**The hex value in string format you want to convert to ascii and decimal format. */
	public static final Property inHex = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getInHex() { return (BStatusString)get(inHex);}
	public void setInHex(BStatusString v) {set(inHex,v);}
	
	/**The decimal value in string format you want to convert to ascii and hex. */
	public static final Property inDecimal = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getInDecimal() { return (BStatusString)get(inDecimal);}
	public void setInDecimal(BStatusString v) {set(inDecimal,v);}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**The ascii value in string format as calculated from changed input value. */
	public static final Property outAscii = newProperty(0|Flags.SUMMARY, new BStatusString(),BFacets.make("multiLine",true));
	public BStatusString getOutAscii() { return (BStatusString)get(outAscii);}
	public void setOutAscii(BStatusString v) {set(outAscii,v);}
	
	/**The hex value in string format as calculated from changed input value. */
	public static final Property outHex = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getOutHex() { return (BStatusString)get(outHex);}
	public void setOutHex(BStatusString v) {set(outHex,v);}
	
	/**The decimal value in string format as calculated from changed input value. */
	public static final Property outDecimal = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getOutDecimal() { return (BStatusString)get(outDecimal);}
	public void setOutDecimal(BStatusString v) {set(outDecimal,v);}
	
	/**The number of characters in the string value. */
	public static final Property outLength  = newProperty(0, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLength() {return (BStatusNumeric) get(outLength); }
	public void setOutLength(BStatusNumeric v) {set(outLength, v);}
	
	/**If input exceeds the allowed number of bytes this value will be true.*/
	public final static Property outByteLengthExceeded = newProperty(0|Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getOutByteLengthExceeded() { return (BStatusBoolean)get(outByteLengthExceeded); }
	public void setOutByteLengthExceeded(BStatusBoolean v) { set(outByteLengthExceeded, v); }
	
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	TOPIC SLOTS   /////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**THE ASCII VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Topic SetAscii = newTopic(0);
	public void fireSetAscii(BString event){fire(SetAscii,event,null);}
	
	/**THE HEX VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Topic SetHex = newTopic(0);
	public void fireSetHex(BString event){fire(SetHex,event,null);}
	
	/**THE DECIMAL VALUE IN STRING FORMAT AS CALCULATED FROM CHANGED INPUT VALUE. */
	public static final Topic SetDecimal = newTopic(0);
	public void fireSetDecimal(BString event){fire(SetDecimal,event,null);}

	
	//---------------------------------------------------------------------------------------------------------
	//	SOURCE CODE BELOW HERE	-------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------
	
	private String strToHexD	= "";
	private String hexToDecD	= "";

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**    METHOD INVOKED WHEN ANY OF THE INPUTS CHANGES VALUES   *////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void changed(Property prop, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning())return;
		if(isRunning())
		{
			boolean useDelim	= getInUseDelimiter().getValue();
			String	strInAscii	= getInAscii().getValue();
			String	strInHex	= getInHex().getValue();
			String	strInDec	= getInDecimal().getValue();
			
			String	sAscii		= "";
			String	sHex		= "";
			String	sDec		= "";
			
			// CHECKS TO SEE IF INPUT "inAscii" HAS CHANGED ///////////////////////////////////////////////////
			if(prop==inAscii && strInAscii.length()>0)
			{
				getOutByteLengthExceeded().setValue(false);
				getInHex().setValue("");
				getInDecimal().setValue("");
				
				sAscii	= strInAscii;
				sHex	= convertStringToHex(strInAscii);
				sDec	= convertHexToDecimal(sHex);

				if(useDelim==true)
				{
					// SET OUTPUT VALUES
					getOutAscii().setValue(sAscii);
					getOutHex().setValue(strToHexD);
					getOutDecimal().setValue(hexToDecD);
					getOutLength().setValue(sAscii.length());
					
					// FIRE TOPIC SLOTS
					fireSetAscii(BString.make(sAscii));
					fireSetHex(BString.make(strToHexD));
					fireSetDecimal(BString.make(hexToDecD));
				}
				else
				{
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
			
			// CHECKS TO SEE IF INPUT "inHex" HAS CHANGED /////////////////////////////////////////////////////
			if(prop==inHex && strInHex.length()>0)
			{
				getOutByteLengthExceeded().setValue(false);
				getInAscii().setValue("");
				getInDecimal().setValue("");
				
				strInHex	= removeChar(strInHex," ");
				strInHex	= removeChar(strInHex,",");
				
				sAscii	= convertHexToString(strInHex);
				sHex	= strInHex;
				sDec	= convertHexToDecimal(sHex);

				if(useDelim==true)
				{
					// SET OUTPUT VALUES
					getOutAscii().setValue(sAscii);
					getOutHex().setValue(strToHexD);
					getOutDecimal().setValue(hexToDecD);
					getOutLength().setValue(sAscii.length());
					
					// FIRE TOPIC SLOTS
					fireSetAscii(BString.make(sAscii));
					fireSetHex(BString.make(strToHexD));
					fireSetDecimal(BString.make(hexToDecD));
				}
				else
				{
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
			
			// CHECKS TO SEE IF INPUT "inDecimal" HAS CHANGED /////////////////////////////////////////////////
			if(prop==inDecimal && strInDec.length()>0)
			{
				getOutByteLengthExceeded().setValue(false);
				getInAscii().setValue("");
				getInHex().setValue("");
				
				strInDec	= removeChar(strInDec," ");
				strInDec	= removeChar(strInDec,",");
				
				sAscii	= convertDecToString(strInDec);
				sHex	= convertStringToHex(sAscii);
				sDec	= strInDec;
				
				if(useDelim==true)
				{
					// SET OUTPUT VALUES
					getOutAscii().setValue(sAscii);
					getOutHex().setValue(strToHexD);
					getOutDecimal().setValue(hexToDecD);
					getOutLength().setValue(sAscii.length());
					
					// FIRE TOPIC SLOTS
					fireSetAscii(BString.make(sAscii));
					fireSetHex(BString.make(strToHexD));
					fireSetDecimal(BString.make(hexToDecD));
				}
				else
				{
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
		
			if(strInAscii.length()==0 && strInHex.length()==0 && strInDec.length()==0)
			{
				getOutByteLengthExceeded().setValue(false);
				int		bytes	= (int)getInBytesLong().getValue();
				String	delim	= getInDelimiter().getValue();
				String	zero	= "";
				
				if(getInUseBytesLong().getValue()==true)
				{
					for(int i=0; i<bytes; i++)
					{
						if(useDelim==true)
						{
							if(zero.length()==0)
							{
								zero	= "0";
							}
							else
							{
								zero = zero + delim + "0";
							}
						}
						else
						{
							zero = zero + "0";
						}
					}
				}
				
				getOutAscii().setValue("");
				getOutHex().setValue(zero);
				getOutDecimal().setValue(zero);
				getOutLength().setValue(sAscii.length());
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
			int		bytes	= (int)getInBytesLong().getValue();
			String	delim	= getInDelimiter().getValue();
			char[]	chars	= str.toCharArray();

			StringBuffer	hex		= new StringBuffer();
			StringBuffer	hexB	= new StringBuffer();
			for(int i = 0; i < chars.length; i++)
			{
				hex.append(Integer.toHexString((int)chars[i]));
				hexB.append(Integer.toHexString((int)chars[i])+getInDelimiter().getValue());
			}
			String	strToHex	= hex.toString().toUpperCase();
					strToHexD	= hexB.toString().toUpperCase().substring(0,hexB.toString().length()-1);
			if( strToHex.length()	== 1 ) strToHex  = "0" + strToHex;
			if( strToHexD.length()	== 1 ) strToHexD = "0" + strToHexD;
			
			if(getInUseBytesLong().getValue()==true)
			{
				if((strToHex.length()/2)<bytes)
				{
					for(int i=0; i<(bytes-(strToHex.length()/2)); i++)
					{
						strToHex	= strToHex + "0";
						strToHexD	= strToHexD + delim + "0";
					}
				}
				if((strToHex.length()/2)>bytes)
				{
								strToHex		= strToHex.substring(0,(bytes*2));
					String[]	spltHex			= strToHexD.split(delim);
					String		TEMPstrToHexD	= "";
					
					for(int x=0; x<bytes; x++)
					{
						TEMPstrToHexD = TEMPstrToHexD + delim + spltHex[x];
					}
					strToHexD	= TEMPstrToHexD.substring(delim.length());
					getOutByteLengthExceeded().setValue(true);
				}
			}
			
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

			//49204c6f7665204a617661 split into two characters 49, 20, 4c...
			for( int i=0; i<hex.length()-1; i+=2 )
			{
				//Grab the hex in pairs
				String output = hex.substring(i, (i + 2));
				//Convert hex to decimal
				int decimal = Integer.parseInt(output, 16);
				//Convert the decimal to character
				sb.append((char)decimal);
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

			//49204c6f7665204a617661 split into two characters 49, 20, 4c...
			for( int i=0; i<dec.length()-1; i+=2 )
			{
				//Grab the dec in pairs
				String output = dec.substring(i, (i + 2));
				//Convert string to integer
				int decimal = Integer.parseInt(output);
				//Convert the decimal to character
				sb.append((char)decimal);
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
			int 			bytes	= (int)getInBytesLong().getValue();
			String 			delim	= getInDelimiter().getValue();
			StringBuilder	temp	= new StringBuilder();
			StringBuilder	tempB	= new StringBuilder();

			//49204c6f7665204a617661 split into two characters 49, 20, 4c...
			for( int i=0; i<hex.length()-1; i+=2 )
			{
				//Grab the hex in pairs
				String output = hex.substring(i, (i + 2));
				//Convert hex to decimal
				int decimal = Integer.parseInt(output, 16);
				//Convert the decimal to character
				temp.append(decimal);
				tempB.append(decimal + delim);
			}
			String	hexToDec	= temp.toString();
					hexToDecD	= tempB.toString().substring(0,tempB.toString().length()-delim.length());
			
			
			if(getInUseBytesLong().getValue()==true)
			{
				if((hex.length()/2)<bytes)
				{
					for(int i=0; i<(bytes-(hex.length()/2)); i++)
					{
						hexToDec	= hexToDec + "0";
						hexToDecD	= hexToDecD + delim + "0";
					}
				}
				if((hex.length()/2)>bytes)
				{
								hexToDec		= hexToDec.substring(0,(bytes*2));
					String[]	spltDec			= hexToDecD.split(delim);
					String		TEMPhexToDecD	= "";
					
					for(int x=0; x<bytes; x++)
					{
						TEMPhexToDecD = TEMPhexToDecD + delim + spltDec[x];
					}
					hexToDecD	= TEMPhexToDecD.substring(delim.length());
					getOutByteLengthExceeded().setValue(true);
				}
			}
			
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
	/**	"removeChar" ACCEPTS A STRING INPUT AND RETURNS THE SAME STRING MINUS ANY SPACES *///////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String removeChar(String strIn, String stringToReplace)
	{
		try
		{
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
						+ "\r\n\t\t removeChar() results..." 
						+ "\r\n\t\t FROM:"
						+ "\r\n\t\t '" + strIn + "'"
						+ "\r\n\t\t TO:"
						+ "\r\n\t\t '" + newString + "'");
			
			return newString;
		}
		catch (Exception e) 
		{
			logger.error(		"\r\n\t\t" + getSlotPath()	
							+	"\r\n\t\t" + "ERROR IN removeChar() METHOD!"
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
