package org.axcommunity.niagara.logic;

import javax.baja.gx.BFont;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

/**
 * Font setpoint that monitors when the last change was made and who made it.
 *
 * @author Eric Bishop, Texas Machining Technologies
 * @creation Mar 20, 2013
 */

public class BWhoWhenFontSetpoint extends BComponent
{ 
  public static final Property out = newProperty(Flags.SUMMARY, BFont.DEFAULT,null);
  public BFont getOut() { return (BFont)get(out); }
  public void setOut(BFont v) { set(out,v,null); }

  /**String showing username of who changed the point*/
  public static final Property changedBy = newProperty(Flags.READONLY + Flags.SUMMARY + Flags.DEFAULT_ON_CLONE, new BStatusString());
  public void setChangedBy(BStatusString v) { set(changedBy, v); }
  public BStatusString getChangedBy() {return (BStatusString)get(changedBy);}

  /**Absolute Time of change*/
  public final static Property timeChanged = newProperty(Flags.SUMMARY + Flags.READONLY+ Flags.DEFAULT_ON_CLONE, BAbsTime.DEFAULT);
  public void setTimeChanged(BAbsTime v) { set(timeChanged, v); }
  public BAbsTime getTimeChanged() { return (BAbsTime)get(timeChanged); }

  public static final Action set = newAction(Flags.OPERATOR, BFont.DEFAULT,null);
  public void set(BFont arg) { invoke(set,arg,null); }

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BWhoWhenFontSetpoint.class);
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/TMT.png");
  
  public BValue getActionParameterDefault(Action action)
  {
    if (action == set) return getOut();
    return super.getActionParameterDefault(action);
  }

  public void doSet(BFont value, Context cxin)
  {
    if(cxin==null)
    {
      //for wiresheet invokes, set username to "logic"
      getChangedBy().setValue("logic");
    }

    else 
    {
      //for any user invokes, get the context username
      getChangedBy().setValue(cxin.getUser().getUsername());
    }
    
    setOut(value);
    setTimeChanged(BAbsTime.make());
  }
  
  public String toString(Context cx)
  {
    return propertyValueToString(out, cx);
  }
}