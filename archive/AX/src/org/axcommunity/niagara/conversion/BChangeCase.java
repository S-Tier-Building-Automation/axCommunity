package org.axcommunity.niagara.conversion;

import javax.baja.log.Log;
import javax.baja.status.BStatusString;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;


/**
* Changes case on Status String input to Upper, Lower and Tile case outputs.
* @author Justin Koffler
*/
public class BChangeCase
extends BComponent
{


	public void changed(Property property, Context context)
	{
		if(!Sys.atSteadyState() || !isRunning())
		{
			return;
		}

		if(isRunning())
		{
			if (property == inString) 
			{
				if(getInString().getValue().length()>0)
				{
					try
					{
						String temp = getInString().getValue(); 
						String upper = temp.toUpperCase();
						String lower = temp.toLowerCase();

						String firstLetter = "";
						firstLetter = lower.substring(0, 1).toUpperCase();

						StringBuffer sb = new StringBuffer(temp);

						// GO THROUGH THE STRING, EVERY TIME YOU COME ACROSS A NEW WORD SET THE FIRST LETTER TO UPPER CASE
						boolean haveSeenSpace = true; // SET IT INITIALLY TO TRUE SO THAT WE SET THE FIRST LETTER
						for(int i = 0; i < sb.length();i++)
						{
							if(sb.charAt(i) == ' ')
							{
								haveSeenSpace = true;
							}
							else
							{
								// MUST BE A LETTER SO CHECK TO SEE IF THE LAST ITEM WAS A SPACE TO SET TO UPPER CASE
								if(haveSeenSpace)
								{
									sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
									haveSeenSpace = false;
								}
								else
								{
									// MUST BE A LETTER SO PUSH TO LOWER
									sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
								}
							}
						}
						String title = (sb.toString());

						getOutUppercase().setValue(upper);   
						getOutLowercase().setValue(lower); 
						getOutTitlecase().setValue(title); 

					}
					catch (Exception e) 
					{
						logger.error("\r\n\r\n" + getSlotPath() + "\r\n" + e.getMessage() + "\r\n" + e.getStackTrace() + "\r\n");
					}
				}
				else
				{
					getOutUppercase().setValue("");   
					getOutLowercase().setValue(""); 
					getOutTitlecase().setValue("");
					logger.trace("\r\n\r\n" + getSlotPath() + "\r\n#########  String Input Length Not Greater Than Zero  #########\r\n");
				}
			}
		}
	}

	/**Status String value in representing string to convert*/
	public static final Property inString = newProperty(Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInString() { return (BStatusString)get(inString);}
	public void setInString(BStatusString v) {set(inString,v);}  

	/**Status String value out representing upper case of input*/
	public static final Property outUppercase = newProperty(Flags.SUMMARY, new BStatusString(""));
	public BStatusString getOutUppercase() { return (BStatusString)get(outUppercase);}
	public void setOutUppercase(BStatusString v) {set(outUppercase,v);}

	/**Status String value out representing lower case of input*/
	public static final Property outLowercase = newProperty(Flags.SUMMARY, new BStatusString(""));
	public BStatusString getOutLowercase() { return (BStatusString)get(outLowercase);}
	public void setOutLowercase(BStatusString v) {set(outLowercase,v);}

	/**Status String value out representing title case of input*/
	public static final Property outTitlecase = newProperty(Flags.SUMMARY, new BStatusString(""));
	public BStatusString getOutTitlecase() { return (BStatusString)get(outTitlecase);}
	public void setOutTitlecase(BStatusString v) {set(outTitlecase,v);}

	
	public static final Log logger = Log.getLog("axCommunity.ChangeCase");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BChangeCase.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");


}
