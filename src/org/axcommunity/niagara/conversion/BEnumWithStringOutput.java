package org.axcommunity.niagara.conversion;

import javax.baja.control.BEnumWritable;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.BDynamicEnum;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public class BEnumWithStringOutput extends BEnumWritable {
	
	
	
    public void changed(Property property, Context context){
		if(!Sys.atSteadyState() || !isRunning()){
    		return;
    	}
		System.out.println(property.getName().toString());
		System.out.println(property.getType().toString());
        if (property == out) 
        {
            String tag =  ((BEnumRange)this.getFacets().get("range")).getDisplayTag(this.getOut().getValue().getOrdinal(), null);
            setStringOut(new BStatusString(tag));
         }  
        if(property==inNumeric)
        {
        	
        	calculate();
        }
    }
    
    void calculate()
    {
      BStatusEnum workingValue = new BStatusEnum();
      double inValue = getInNumeric().getValue();
      if(inValue <= (double)Integer.MAX_VALUE &&
         inValue >= (double)Integer.MIN_VALUE    )
      {
    	workingValue.setValue(BDynamicEnum.make((int)getInNumeric().getValue()) );
        workingValue.setStatusNull(false);
        workingValue.setStatusFault(false);
        setFallback( workingValue );
             }
      else    
      {
        workingValue.setStatusNull(true);
        workingValue.setStatusFault(true);
      }
      setFallback(workingValue);
    }

	
	 /**
     * String output after conversion
     */
    public static final Property stringOut = newProperty(Flags.SUMMARY, new BStatusString());
    public BStatusString getStringOut() { return (BStatusString)get(stringOut);}
    public void setStringOut(BStatusString v) {set(stringOut,v);}
    
    /**numeric input*/
    public static final Property inNumeric = newProperty(Flags.SUMMARY, new BStatusNumeric());
	public BStatusNumeric getInNumeric() {return (BStatusNumeric) get(inNumeric);	}
	public void setInNumeric(BStatusNumeric v) {set(inNumeric, v);}
 
    
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

    public static final Type TYPE = Sys.loadType(BEnumWithStringOutput.class);
    public Type getType() { return TYPE; }
}
