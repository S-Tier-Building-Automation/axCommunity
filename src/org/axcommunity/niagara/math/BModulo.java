package org.axcommunity.niagara.math;

import javax.baja.sys.*;
import javax.baja.status.*;
/** Returns the remainder from a divide operation
 * @author Mike Arnott, Kors Engineering
*/

public class BModulo extends BComponent{
	
	public void started(){
	}
	
	public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!isRunning()){
    		return;
    	}
       
        if ((property == getProperty("dividend"))||property == getProperty("divisor")){
        	if(getDivisor().getValue()>0){
        		getRemainder().setValue(getDividend().getValue()%getDivisor().getValue());
        	}
        	else{
        		getRemainder().setValue(0);
        	}
        	getRemainder().setStatus(getDividend().getStatus());
       }
	}	
	
   /**The dividend*/
    public static final Property dividend = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public void setDividend(BStatusNumeric v) { 
    	set(dividend, v); 
    }
	public BStatusNumeric getDividend() { 
		return (BStatusNumeric)get(dividend); 
	}
    /**The divisor*/
    public static final Property divisor = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public void setDivisor(BStatusNumeric v) { set(divisor, v); }
	public BStatusNumeric getDivisor() { 
		return (BStatusNumeric)get(divisor); 
	}
  /**The remainder*/
    public static final Property remainder = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public void setRemainder(BStatusNumeric v) { set(remainder, v); }
	public BStatusNumeric getRemainder() { 
		return (BStatusNumeric)get(remainder); 
	}

	
	public BIcon getIcon() { return icon; }
	   private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	       
    public static final Type TYPE = Sys.loadType(BModulo.class);
    public Type getType() { return TYPE; }   
	
}