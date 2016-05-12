package org.axcommunity.niagara.math;

import javax.baja.sys.*;
import javax.baja.status.*;
/**
 * Rounds any fractional value to next whole value, e.g. input value of 0.00001 has output value of 1.00
 * @author Mike Arnott, Kors Engineering
 */
public class BRoundUp extends BComponent{
	
	public void started(){
		try{
			if (getSlot("Input").isProperty()){
				//Check for slot
			}
		}
		catch(Exception ex){
			add("Input", new BStatusNumeric(0), Flags.SUMMARY);
			add("Output", new BStatusNumeric(0), Flags.SUMMARY);
		}
	}
	
	public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!isRunning()){
    		return;
    	}
       
        if (property == getProperty("Input")){
        	double a, b;
        	a = ((BStatusNumeric)get(getProperty("Input"))).getValue();
        	b = Math.ceil(a);
        	set(getProperty("Output"), new BStatusNumeric(b));
        }
	}	
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	       
    public static final Type TYPE = Sys.loadType(BRoundUp.class);
    public Type getType() { return TYPE; }   
	
}