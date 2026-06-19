package org.axcommunity.niagara.logic;

import javax.baja.gx.BFont;
import javax.baja.status.BStatusBoolean;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;

/**
 * Fires a font event.
 *
 * @author Eric Bishop
 * @creation Mar 20, 2013
 *
 */

public class BSetFontAction extends BComponent
{ 
  public static final Property inFont = newProperty(Flags.SUMMARY, BFont.DEFAULT);
  public BFont getInFont() { return (BFont)get(inFont);}
  public void setInFont(BFont v) {set(inFont,v);}

  public static final Property trigger = newProperty(Flags.SUMMARY, new BStatusBoolean());
  public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger);}
  public void setTrigger(BStatusBoolean v) {set(trigger,v);}

  public static final Action SetValue = newAction(0);
  public void SetValue(){invoke(SetValue, null);}
  public void doSetValue(){this.fireNewFontInput(getInFont());}
  
  public static final Topic newFontInput = newTopic(0);
  public void fireNewFontInput(BFont event){fire(newFontInput,event,null);}

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSetFontAction.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");

  private boolean lastBoolean = false;


  public void changed(Property property, Context context)
  {
    super.changed(property, context);
    if(!Sys.atSteadyState()|| !isRunning()) return;
    
    if(property == trigger)
    {
      if(getTrigger().getValue()==true&&lastBoolean==false) this.fireNewFontInput(getInFont());
      lastBoolean = getTrigger().getValue();
    }
  }
}