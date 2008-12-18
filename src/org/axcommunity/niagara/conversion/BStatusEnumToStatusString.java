
package org.axcommunity.niagara.conversion;

import javax.baja.control.BEnumWritable;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;
/**
 * Converts a StatusEnum input to a Status String output.
 * @author Mike Arnott, Kors Engineering
 */
public class BStatusEnumToStatusString extends BComponent{

    public void changed(Property property, Context context){
		if(!Sys.atSteadyState() || !isRunning()){
    		return;
    	}
        if (property == enumIn) {
            BLink[] links = getLinks(enumIn);
            for (int i = 0; i < links.length; i++) {
                if (links[i].getSourceComponent().getType() != BEnumWritable.TYPE) {
                    continue;
                }
                BEnumWritable sc =((BEnumWritable) links[i].getSourceComponent());
                BEnumRange range = ((BEnumRange) sc.getFacets().get("range"));
                String tag = 	range.getDisplayTag(getEnumIn().getValue().getOrdinal(), null);
                setStringOut(new BStatusString(tag));
            }
        }
    }

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
    
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

    public static final Type TYPE = Sys.loadType(BStatusEnumToStatusString.class);
    public Type getType() { return TYPE; }
}