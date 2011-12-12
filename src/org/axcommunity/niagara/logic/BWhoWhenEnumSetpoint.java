package org.axcommunity.niagara.logic;

import javax.baja.sys.*;
import javax.baja.status.*;

/**
 * Enum Setpoint object that remembers the username/timestamp of the last change
 *
 * @author    Mike Arnott
 * @creation  9 Dec 11
 */
public class BWhoWhenEnumSetpoint
extends BComponent implements BIEnum
{

  public static final Property facets = newProperty(0, BFacets.makeEnum());
  public BFacets getFacets() { return (BFacets)get(facets); }
  public void setFacets(BFacets v) { set(facets, v); }

  /**The output setpoint*/
  public static final Property out = newProperty(Flags.SUMMARY, new BStatusEnum());
  public void setOut(BStatusEnum v) {     set(out, v);   }
  public BStatusEnum getOut() {     return (BStatusEnum)get(out);   }

  
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
  public static final Action SetValue = newAction(Flags.OPERATOR,BDynamicEnum.DEFAULT);
  public void SetValue(BDynamicEnum v)
  {
    invoke(SetValue,v, null);
  }
  public void doSetValue(BDynamicEnum v, Context cxin)
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
    getOut().setValue(v.getEnum());
    
    //some day would love to add override status to this object also...
    getOut().setStatus(BStatus.ok);
    setTimeChanged(BAbsTime.make());
  }
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BWhoWhenEnumSetpoint.class);


  public BFacets getSlotFacets(Slot slot)
  {
    if (slot.getName().equals("out") || slot == SetValue)
    {
      return getFacets();
    }
    else return super.getSlotFacets(slot);
  }
  public BValue getActionParameterDefault(Action action)
  {
    if (action == SetValue) 
      return getOut().getValueValue();
    return super.getActionParameterDefault(action);
  }
  /**
   * Return the vaule as a enum.
   */
  public final BEnum getEnum() { return getOut().getEnum(); }

  /**
   * Return getFacets().
   */
  public final BFacets getEnumFacets() { return getFacets(); }

}
