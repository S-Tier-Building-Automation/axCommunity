/* SPDX-License-Identifier: GPL-2.0-only */

package org.axcommunity.niagara.logic;

import javax.baja.status.BStatusBoolean;
import javax.baja.sys.*;


/**
 * Actions to set a boolean true/false: fires boolean events that can be
 * linked to the set/override actions of BooleanWritables.
 *
 * <p>Invoking the {@code SetTrue} or {@code SetFalse} action fires the
 * {@code newOutputValue} topic with true or false; the {@code triggerTrue}
 * and {@code triggerFalse} inputs do the same on a false-to-true
 * transition. Use it wherever wiresheet logic needs to push a discrete
 * command into a writable point.</p>
 *
 * @author Mike Arnott, Kors Engineering
 */
public class BSetBooleanAction extends BComponent{

	private boolean lastTriggerTrue = false;
	private boolean lastTriggerFalse = false;

	
    public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!Sys.atSteadyState()|| !isRunning()){
    		return;
    	}
     	if(property == triggerTrue)
       	{
       		if(getTriggerTrue().getValue()==true&&lastTriggerTrue==false)
       		{
       			//new value, fire output
       			this.fireNewOutputValue(BBoolean.make(true));
       		}
       		lastTriggerTrue = getTriggerTrue().getValue();

       	}
    	if(property == triggerFalse)
       	{
       		if(getTriggerFalse().getValue()==true&&lastTriggerFalse==false)
       		{
       			//new value, fire output
       			this.fireNewOutputValue(BBoolean.make(false));
       		}
       		lastTriggerFalse = getTriggerFalse().getValue();

       	}
    	

    }
	/**invokable action to send true*/
    public static final Action SetTrue = newAction(0);
	public void SetTrue()
	{
		invoke(SetTrue, null);
	}
	public void doSetTrue()
	{
		this.fireNewOutputValue(BBoolean.make(true));	
	}
	/**invokable action to send false*/
    public static final Action SetFalse = newAction(0);
	public void SetFalse()
	{
		invoke(SetFalse, null);
	}
	public void doSetFalse()
	{
		this.fireNewOutputValue(BBoolean.make(false));	
	}
	
	
    /**Boolean Input, false to true transition invokes the send True*/
    public static final Property triggerTrue = newProperty(Flags.SUMMARY, new BStatusBoolean());
    public BStatusBoolean getTriggerTrue() { return (BStatusBoolean)get(triggerTrue);}
    public void setTriggerTrue(BStatusBoolean v) {set(triggerTrue,v);}

    /**Boolean Input, false to true transition invokes the send False*/
    public static final Property triggerFalse = newProperty(Flags.SUMMARY, new BStatusBoolean());
    public BStatusBoolean getTriggerFalse() { return (BStatusBoolean)get(triggerFalse);}
    public void setTriggerFalse(BStatusBoolean v) {set(triggerFalse,v);}

	
	
    /**Event fired to send new value*/    
    public static final Topic newOutputValue = newTopic(0);
    public void fireNewOutputValue(BBoolean event){
    	fire(newOutputValue,event,null);
    }
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	    
	public static final Type TYPE = Sys.loadType(BSetBooleanAction.class);
	public Type getType() { return TYPE; }
}
