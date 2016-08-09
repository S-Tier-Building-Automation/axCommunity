package org.axcommunity.niagara.logic;

import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;
/**
 * This object takes a numeric input or constant and when "triggered" will fire a topic with the numeric input variable as an argument.
 * We use it to send a single value to a bunch of numeric writables at the same time.  
 * To use, link the New Numeric Input output to the "set" action on any numeric writable.
 * @author Mike Arnott, Kors Engineering
*/
public class BSetNumericAction extends BComponent{
	private boolean lastBoolean = false;

	
    public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!Sys.atSteadyState()|| !isRunning()){
    		return;
    	}
      	if(property == trigger)
       	{
       		if(getTrigger().getValue()==true&&lastBoolean==false)
       		{
       			//new value, fire output
       			this.fireNewNumericInput(BDouble.make(getInNumber().getValue()));
       		}
   			lastBoolean = getTrigger().getValue();

       	}

    }

    
	/**invokable action to send the current value*/
    public static final Action SetValue = newAction(0);
	public void SetValue()
	{
		invoke(SetValue, null);
	}
	public void doSetValue()
	{
		this.fireNewNumericInput(BDouble.make(getInNumber().getValue()));	
	}

    /**Numeric value to be sent*/
    public static final Property inNumber = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public BStatusNumeric getInNumber() { return (BStatusNumeric)get(inNumber);}
    public void setInNumber(BStatusNumeric v) {set(inNumber,v);}

	
    /**Boolean Input, false to true transition invokes the send*/
    public static final Property trigger = newProperty(Flags.SUMMARY, new BStatusBoolean());
    public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger);}
    public void setTrigger(BStatusBoolean v) {set(trigger,v);}

    /**Event fired to send new Number*/    
    public static final Topic newNumericInput = newTopic(0);
    public void fireNewNumericInput(BDouble event){
    	fire(newNumericInput,event,null);
    }
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	    
	public static final Type TYPE = Sys.loadType(BSetNumericAction.class);
	public Type getType() { return TYPE; }

}
