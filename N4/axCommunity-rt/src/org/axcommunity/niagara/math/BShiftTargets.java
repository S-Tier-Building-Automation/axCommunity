package org.axcommunity.niagara.math;

import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.baja.status.BIStatusValue;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusValue;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

//	Update 6/29/2017 by James Johnson to move to current logger syntax

/**
 * BNumericPoint defines a read only numeric.
 */
public class BShiftTargets extends BComponent implements BIStatusValue
{ 
  public static final Property facets = newProperty(0, BFacets.makeNumeric());
  public BFacets getFacets() { return (BFacets)get(facets); }
  public void setFacets(BFacets v) { set(facets,v,null); }
  
  public static final Property inNumberOfShifts = newProperty(0, 0, BFacets.make(BFacets.MIN, BInteger.make(1), BFacets.MAX, BInteger.make(60)));
  public int getInNumberOfShifts() { return getInt(inNumberOfShifts); }
  public void setInNumberOfShifts(int v) {setInt(inNumberOfShifts,v);}
  
  public static final Property data = newProperty(Flags.HIDDEN, "", BFacets.make(BFacets.MULTI_LINE, true));
  public String getData() { return getString(data);}
  public void setData(String v) {setString(data,v);}
  
  public static final Property dailyTarget = newProperty(Flags.OPERATOR|Flags.READONLY|Flags.TRANSIENT|Flags.SUMMARY, new BStatusNumeric(),null);
  public BStatusNumeric getDailyTarget() { return (BStatusNumeric)get(dailyTarget); }
  public void setDailyTarget(BStatusNumeric v) { set(dailyTarget,v,null); }

  public final double getNumeric() { return getDailyTarget().getValue(); }
  public final BFacets getNumericFacets() { return getFacets(); }
  public final BStatusValue getOutStatusValue() { return getDailyTarget(); }
  public final BStatus getStatus() { return getOutStatusValue().getStatus(); }
  public final BFacets getStatusValueFacets() { return getFacets(); }
  public boolean isWritablePoint() { return true; }
  public String toString(Context context) { return propertyValueToString(getOutProperty(), context); }
  public final Property getOutProperty() { return getOutStatusValue().getPropertyInParent(); }
  public BStatusValue getStatusValue() { return getDailyTarget().getStatusValue(); }
  
  public BFacets getSlotFacets(Slot slot)
  {
    if (slot == getOutProperty()) return getFacets();
    return super.getSlotFacets(slot);
  }
  
  public void changed(Property p, Context cx)
  {
    super.changed(p, cx);
    if(!Sys.atSteadyState()|| !isRunning()) return;
    if(p.equals(inNumberOfShifts)) shiftSlots(getInNumberOfShifts(), cx);
    
    if(p.getName().startsWith("shift_") || p.equals(inNumberOfShifts)) writeDataSlot(cx);
  }
  
  void writeDataSlot(Context cx)
  {
    String stringValue = "";
    double dailyTarget = 0;
    double tempNum;
    NumberFormat format = NumberFormat.getNumberInstance();
    
      format.setMaximumFractionDigits(127);
      format.setMinimumFractionDigits(0);
      format.setMaximumIntegerDigits(127);
      format.setMinimumIntegerDigits(1);
      format.setGroupingUsed(false);
    
    try
    {
      for(int i=1; i<=getInNumberOfShifts(); i++)
      {
        tempNum = ((BStatusNumeric) ((BObject)get("shift_"+i))).getValue();
        
        stringValue = stringValue + format.format(tempNum);
        if(i<getInNumberOfShifts()) stringValue = stringValue + "\n";
        
        dailyTarget = dailyTarget + tempNum;
      }
    }
    catch (Exception e)
    {
      logger.log(Level.SEVERE, getSlotPath().toString() + e.getMessage());
      e.printStackTrace();
    }
    setData(stringValue);
    getDailyTarget().setValue(dailyTarget);
  }
  
  /**Creates all of the shift_*_* slots used to track part counts and times*/
  public void shiftSlots(int SlotCount, Context cx)
  {
    try
    {
      for(int i=1; i<SlotCount+1; i++)
      {
        if(((BObject)get("shift_"+i))==null)
        {
          this.add(("shift_"+i), new BStatusNumeric(), Flags.OPERATOR|Flags.SUMMARY, BFacets.make(BFacets.PRECISION, BInteger.make(1)), cx);
        }
      }
      
      for(int i=SlotCount+1; (BObject)get("shift_"+i)!=null; i++)
      {
        if(((BObject)get("shift_"+i))!=null) {this.remove("shift_"+i);}
      }
    }
    catch (Exception e)
    {
      logger.log(Level.SEVERE, getSlotPath().toString() + e.getMessage());
      e.printStackTrace();
    }
  }
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BShiftTargets.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://ROI/com/royaloakindustries/niagara/Graphics/EB.png");
  
  public static final Logger logger = Logger.getLogger(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
}