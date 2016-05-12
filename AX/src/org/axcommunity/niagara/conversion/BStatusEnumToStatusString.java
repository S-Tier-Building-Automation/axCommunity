
package org.axcommunity.niagara.conversion;

import javax.baja.log.Log;
import javax.baja.control.BEnumWritable;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;
/**
 * Converts a StatusEnum input to a Status String output.
 * @author Mike Arnott, Kors Engineering
 */
public class BStatusEnumToStatusString extends BComponent
{

    public void changed(Property property, Context context)
	{
		if(!Sys.atSteadyState() || !isRunning())
		{
			return;
		}
		if (property == enumIn) 
		{
			try
			{
				BLink[] links = getLinks(enumIn);
				for (int i = 0; i < links.length; i++) 
				{
					if ((links[i].getSourceComponent().getType() != BEnumWritable.TYPE) && (getInputMustBeEnumWritable().getValue()==true)) 
					{
						continue;
					}
					BEnumWritable	sc		= ((BEnumWritable) links[i].getSourceComponent());
					BEnumRange		range	= ((BEnumRange) sc.getFacets().get("range"));
					String			tag		= range.getDisplayTag(getEnumIn().getValue().getOrdinal(), null);
					setStringOut(new BStatusString(tag));
				}
			}
			catch (Exception e) 
			{
				logger.error("\n slotPath = " + getSlotPath() + "\n getMessage = " + e.getMessage() + "\n getStackTrace = " + e.getStackTrace() + "\n toString = " + e.toString());
			}
		}
	}


	/**
	* Boolean input config option for input links
	*/
	public final static Property inputMustBeEnumWritable = newProperty(0|Flags.HIDDEN, new BStatusBoolean(false));
	public BStatusBoolean getInputMustBeEnumWritable() { return (BStatusBoolean)get(inputMustBeEnumWritable); }
	public void setInputMustBeEnumWritable(BStatusBoolean v) { set(inputMustBeEnumWritable, v); }
	
	
	/**
	* Enum input to be converted
	*/
	public static final Property enumIn = newProperty(Flags.SUMMARY, new BStatusEnum());
	public BStatusEnum getEnumIn() { return (BStatusEnum)get(enumIn); }
	public void setEnumIn(BStatusEnum v) { set(enumIn, v); }
	/**
	 * String output after conversion
	 */
	public static final Property stringOut = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getStringOut() { return (BStatusString)get(stringOut);}
	public void setStringOut(BStatusString v) {set(stringOut,v);}
	
	public static final Log logger = Log.getLog("axCommunity.StatusEnumToStatusString");
	
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BStatusEnumToStatusString.class);
	public Type getType() { return TYPE; }
}