package org.axcommunity.niagara.logic;

import javax.baja.log.Log;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;

/**
 * Accepts a StatusString, StatusNumeric or StatusBoolean input, fires the appropriate event when:
 * String changes or
 * Number changes or
 * Boolean changes from False to True.
 * 
 * I really did this to figure out how Topics work, but found it pretty handy!
 * @author Mike Arnott, Kors Engineering
 */
public class BFireOnChange extends BComponent {
    //event to fire when new input value detected
	
	private double lastNumber = 0;
	private String lastString = "";
	private boolean lastBoolean = false;
	
 
    public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!Sys.atSteadyState()|| !isRunning()){
    		return;
    	}
		try
		{
			if(property == stringIn)
			{
				if(getStringIn().getValue().compareTo(lastString)!=0)
				{
					//new value, fire output
					lastString = getStringIn().getValue();
					this.fireNewStringInput(BString.make(lastString));
				}
			}
			if(property == numericIn)
			{
				if(getNumericIn().getValue()!=lastNumber)
				{
					//new value, fire output
					lastNumber = getNumericIn().getValue();
					this.fireNewNumericInput(BDouble.make(lastNumber));
				}
			}
			
			if(property == booleanIn)
			{
				if(getBooleanIn().getValue()==true&&lastBoolean==false)
				{
					//new value, fire output
					this.fireNewBooleanInput(BBoolean.make(true));
				}
				lastBoolean = getBooleanIn().getValue();
			}
		}
		catch (Exception e) 
		{
			logger.error("\n\n" + getSlotPath()	+ "\n\n" + e.getMessage() +	"\n\n" + e.getStackTrace());
		}

    }

    /**String Input*/
    public static final Property stringIn = newProperty(Flags.SUMMARY, new BStatusString());
    public BStatusString getStringIn() { return (BStatusString)get(stringIn);}
    public void setStringIn(BStatusString v) {set(stringIn,v);}
    
    /**String Input*/
    public static final Property numericIn = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public BStatusNumeric getNumericIn() { return (BStatusNumeric)get(numericIn);}
    public void setNumericIn(BStatusNumeric v) {set(numericIn,v);}
    
    /**Boolean Input*/
    public static final Property booleanIn = newProperty(Flags.SUMMARY, new BStatusBoolean());
    public BStatusBoolean getBooleanIn() { return (BStatusBoolean)get(booleanIn);}
    public void setBooleanIn(BStatusBoolean v) {set(booleanIn,v);}

    /**Event fired when a new string is detected*/
    public static final Topic newStringInput = newTopic(0);
    public void fireNewStringInput(BString event){
    	fire(newStringInput,event,null);
    }
    /**Event fired when a new Number is detected*/    
    public static final Topic newNumericInput = newTopic(0);
    public void fireNewNumericInput(BDouble event){
    	fire(newNumericInput,event,null);
    }
    /**Event fired when a boolean True is detected*/    
    public static final Topic newBooleanInput = newTopic(0);
    public void fireNewBooleanInput(BBoolean event){
    	fire(newBooleanInput,event,null);
    }
	
	public static final Log logger = Log.getLog("axCommunity.FireOnChange");
	
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	    
	public static final Type TYPE = Sys.loadType(BFireOnChange.class);
	public Type getType() { return TYPE; }
}
