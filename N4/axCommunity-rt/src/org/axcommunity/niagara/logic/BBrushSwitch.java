package org.axcommunity.niagara.logic;

import javax.baja.gx.BBrush;
import javax.baja.status.BStatusBoolean;
import javax.baja.sys.*;

/**
 * Switches between two brushes using a boolean input.
 *
 * @author Eric Bishop, Texas Machining Technologies
 * @creation Mar 20, 2013
 *
 */
public class BBrushSwitch extends BComponent
{ 
  public static final Property out = newProperty(Flags.SUMMARY, BBrush.DEFAULT);
  public BBrush getOut() { return (BBrush)get(out); }
  public void setOut(BBrush v) { set(out,v,null); }

  public static final Property inSwitch = newProperty(Flags.SUMMARY, new BStatusBoolean());
  public BStatusBoolean getInSwitch() { return (BStatusBoolean)get(inSwitch);}
  public void setInSwitch(BStatusBoolean v) {set(inSwitch,v);}
  
  public static final Property inTrue = newProperty(Flags.SUMMARY, BBrush.DEFAULT);
  public BBrush getInTrue() { return (BBrush)get(inTrue);}
  public void setInTrue(BBrush v) {set(inTrue,v);}
  
  public static final Property inFalse = newProperty(Flags.SUMMARY, BBrush.DEFAULT);
  public BBrush getInFalse() { return (BBrush)get(inFalse);}
  public void setInFalse(BBrush v) {set(inFalse,v);}
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BBrushSwitch.class);
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/TMT.png");
  
  public void started() throws Exception
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    myCode();
  }
  
  public void atSteadyState() throws Exception
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    myCode();
  }

  public void changed(Property p, Context cx)
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    if(p.equals(inSwitch) || p.equals(inTrue) || p.equals(inFalse)) myCode();
  }
  
  private void myCode()
  {
    if(getInSwitch().getValue()) setOut(getInTrue());
    else setOut(getInFalse());
  }
  
  public String toString(Context cx)
  {
    return propertyValueToString(out, cx);
  }
}