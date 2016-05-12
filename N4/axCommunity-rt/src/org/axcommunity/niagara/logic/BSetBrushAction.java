package org.axcommunity.niagara.logic;

import javax.baja.gx.BBrush;
import javax.baja.status.BStatusBoolean;
import javax.baja.sys.*;

/**
 * Fires a brush event.
 *
 * @author Eric Bishop, Texas Machining Technologies
 * @creation Mar 20, 2013
 */

public class BSetBrushAction extends BComponent
{ 
  public static final Property inBrush = newProperty(Flags.SUMMARY, BBrush.DEFAULT);
  public BBrush getInBrush() { return (BBrush)get(inBrush);}
  public void setInBrush(BBrush v) {set(inBrush,v);}

  public static final Property trigger = newProperty(Flags.SUMMARY, new BStatusBoolean());
  public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger);}
  public void setTrigger(BStatusBoolean v) {set(trigger,v);}

  public static final Action SetValue = newAction(0);
  public void SetValue(){invoke(SetValue, null);}
  public void doSetValue(){this.fireNewBrushInput(getInBrush());}
  
  public static final Topic newBrushInput = newTopic(0);
  public void fireNewBrushInput(BBrush event){fire(newBrushInput,event,null);}

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSetBrushAction.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/TMT.png");

  private boolean lastBoolean = false;


  public void changed(Property property, Context context)
  {
    super.changed(property, context);
    if(!Sys.atSteadyState()|| !isRunning()) return;
    
    if(property == trigger)
    {
      if(getTrigger().getValue()==true&&lastBoolean==false) this.fireNewBrushInput(getInBrush());
      lastBoolean = getTrigger().getValue();
    }
  }
}