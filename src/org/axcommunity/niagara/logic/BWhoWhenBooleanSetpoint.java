package org.axcommunity.niagara.logic;

import javax.baja.sys.*;
import javax.baja.status.*;

/**
 * Numeric Setpoint object that remembers the username/timestamp of the last change
 *
 * @author    Mike Arnott
 * @creation  9 Dec 11
 */
public class BWhoWhenBooleanSetpoint
extends BComponent
{

  public static final Property facets = newProperty(0, BFacets.makeBoolean());
  public BFacets getFacets() { return (BFacets)get(facets); }
  public void setFacets(BFacets v) { set(facets, v); }

  /**The output setpoint*/
  public static final Property out = newProperty(Flags.SUMMARY, new BStatusBoolean());
  public void setOut(BStatusBoolean v) {     set(out, v);   }
  public BStatusBoolean getOut() {     return (BStatusBoolean)get(out);   }

  
  /**String showing username of who changed the point*/
  public static final Property changedBy = newProperty(Flags.READONLY + Flags.SUMMARY + Flags.DEFAULT_ON_CLONE, new BStatusString());
  public void setChangedBy(BStatusString v) { set(changedBy, v); }
  public BStatusString getChangedBy() { 
    return (BStatusString)get(changedBy); 
  }
  
  /**Absolute Time of change*/
  public final static Property timeChanged = newProperty(Flags.SUMMARY + Flags.READONLY+ Flags.DEFAULT_ON_CLONE, BAbsTime.DEFAULT);
  public void setTimeChanged(BAbsTime v) { set(timeChanged, v); }
  public BAbsTime getTimeChanged() { return (BAbsTime)get(timeChanged); }

  /**invokable action to set the current value*/
  public static final Action SetValue = newAction(Flags.OPERATOR,BBoolean.DEFAULT);
  public void SetValue(BBoolean v)
  {
    invoke(SetValue, null);
  }
  public void doSetValue(BBoolean v, Context cxin)
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
    //set value and timestamp
    getOut().setValue(v.getBoolean());
    
    //some day would love to add override status to this object also...
    getOut().setStatus(BStatus.ok);
    setTimeChanged(BAbsTime.make());
  }
  
  
    

  
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BWhoWhenBooleanSetpoint.class);


  
  public BValue getActionParameterDefault(Action action)
  {
    if (action == SetValue) 
      return getOut().getValueValue();
    return super.getActionParameterDefault(action);
  }
}
