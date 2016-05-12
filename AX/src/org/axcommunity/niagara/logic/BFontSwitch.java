package org.axcommunity.niagara.logic;

/**
 * Switches between two fonts using a boolean input.
 *
 * @author Eric Bishop
 * @creation Mar 20, 2013
 */

import javax.baja.gx.BFont;
import javax.baja.status.BStatusBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public class BFontSwitch extends BComponent
{ 
  public static final Property out = newProperty(Flags.SUMMARY, BFont.DEFAULT);
  public BFont getOut() { return (BFont)get(out); }
  public void setOut(BFont v) { set(out,v,null); }

  public static final Property inSwitch = newProperty(Flags.SUMMARY, new BStatusBoolean());
  public BStatusBoolean getInSwitch() { return (BStatusBoolean)get(inSwitch);}
  public void setInSwitch(BStatusBoolean v) {set(inSwitch,v);}
  
  public static final Property inTrue = newProperty(Flags.SUMMARY, BFont.DEFAULT);
  public BFont getInTrue() { return (BFont)get(inTrue);}
  public void setInTrue(BFont v) {set(inTrue,v);}
  
  public static final Property inFalse = newProperty(Flags.SUMMARY, BFont.DEFAULT);
  public BFont getInFalse() { return (BFont)get(inFalse);}
  public void setInFalse(BFont v) {set(inFalse,v);}
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BFontSwitch.class);
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
  
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