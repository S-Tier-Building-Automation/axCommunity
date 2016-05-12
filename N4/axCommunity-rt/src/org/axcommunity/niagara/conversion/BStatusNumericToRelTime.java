package org.axcommunity.niagara.conversion;

import javax.baja.status.*;
import javax.baja.sys.*;

/**Converts a StatusNumeric input of time in seconds to a relative time output
 * @author Mike Arnott, Kors Engineering
*/

public class BStatusNumericToRelTime extends BComponent{

    public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!Sys.atSteadyState() || !isRunning()){
    		return;
    	}
       
        if (property == secondsIn){
            long msecs = (long)(getSecondsIn().getValue()*1000);
            BRelTime inTime = BRelTime.make(msecs);
            setTimeOut(inTime.abs());
        }
    }    
    
    /**StatusNumeric value in representing total time in seconds*/
    public final static Property secondsIn = newProperty(Flags.SUMMARY,new BStatusNumeric());
    /**Relative Time Out*/
    public final static Property timeOut = newProperty(Flags.SUMMARY, BRelTime.DEFAULT);
    
    public BStatusNumeric getSecondsIn() { return (BStatusNumeric)get(secondsIn); }
    public BRelTime getTimeOut() { return (BRelTime)get(timeOut); }
    
    public void setSecondsIn(BStatusNumeric v) { set(secondsIn, v); }
    public void setTimeOut(BRelTime v) { set(timeOut, v); }
    
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
    
    public static final Type TYPE = Sys.loadType(BStatusNumericToRelTime.class);
    public Type getType() { return TYPE; }
    
}
