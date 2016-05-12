
package org.axcommunity.niagara.logic;

import javax.baja.status.*;
import javax.baja.sys.*;

/**
 * Same as Tridium's Latch object except the out property doesn't have
 * transient flag set so value is maintained through a reboot.
  *@author Kevin Crabill, Cochrane Supply
*/
public class BNumericLatchObject
  extends BComponent
{  


////////////////////////////////////////////////////////////////
// Property "facets"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>facets</code> property.
   * These facets are applied against all out properties.
   */
  public static final Property facets = newProperty(0, BFacets.DEFAULT,null);
  
  /**
   * Get the <code>facets</code> property.
   */
  public BFacets getFacets() { return (BFacets)get(facets); }
  
  /**
   * Set the <code>facets</code> property.
    */
  public void setFacets(BFacets v) { set(facets,v,null); }

////////////////////////////////////////////////////////////////
// Property "clock"
////////////////////////////////////////////////////////////////
  
  /**
   * doesn't do anything
   */
  public static final Property clock = newProperty(Flags.OPERATOR|Flags.TRANSIENT|Flags.SUMMARY, new BStatusBoolean(),null);
  
  /**
   * Get the <code>clock</code> property.
   */
  public BStatusBoolean getClock() { return (BStatusBoolean)get(clock); }
  
  /**
   * Set the <code>clock</code> property.
   */
  public void setClock(BStatusBoolean v) { set(clock,v,null); }

////////////////////////////////////////////////////////////////
// Property "out"
////////////////////////////////////////////////////////////////
  
  /**
   * last value of the input when the latch action was used. 
   */
  public static final Property out = newProperty(Flags.OPERATOR|Flags.READONLY|Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>out</code> property.
   */
  public BStatusNumeric getOut() { return (BStatusNumeric)get(out); }
  
  /**
   * Set the <code>out</code> property.
   */
  public void setOut(BStatusNumeric v) { set(out,v,null); }

////////////////////////////////////////////////////////////////
// Property "in"
////////////////////////////////////////////////////////////////
  
  /**
   * numeric input that is placed into out property on latch action
   */
  public static final Property in = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>in</code> property.
   */
  public BStatusNumeric getIn() { return (BStatusNumeric)get(in); }
  
  /**
   * Set the <code>in</code> property.
   */
  public void setIn(BStatusNumeric v) { set(in,v,null); }

////////////////////////////////////////////////////////////////
// Action "latch"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>latch</code> action.
   */
  public static final Action latch = newAction(0,null);
  
  /**
   * Invoke the <code>latch</code> action.
   */
  public void latch() { invoke(latch,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNumericLatchObject.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

////////////////////////////////////////////////////////////////
// Interfaces
////////////////////////////////////////////////////////////////

  /**
   * Get the control output value.
   */
  public final void setOutStatusValue(BStatusValue value) 
  { 
    setOut((BStatusNumeric)value);
  }

  public final BStatusValue getInStatusValue() 
  { 
    return (BStatusValue)(getIn().newCopy());
  }
  
  public void changed(Property property, Context context) 
  {
    if(isRunning())
    {
      if(property == clock)
      {
        currentClock = getClock().getValue();
        if(getClock().getStatus().isValid())
        {
          if(currentClock && !lastClock)
          {
            setOutStatusValue(getInStatusValue());
          }
          lastClock = currentClock;
        }
      }
    }
  }

  public void doLatch()
  {
    setOutStatusValue(getInStatusValue());
  }

  //public abstract void setOutStatusValue(BStatusValue value);
  //public abstract BStatusValue getInStatusValue();


  public BFacets getSlotFacets(Slot slot)
  {
    if (slot.getName().startsWith("out")) return getFacets();
    return super.getSlotFacets(slot);
  }

////////////////////////////////////////////////////////////////
// Presentation
////////////////////////////////////////////////////////////////  

  private boolean currentClock;
  private boolean lastClock;

  public String toString(Context cx) { return getOut().toString(cx); }

////////////////////////////////////////////////////////////////
// BIStatus interface
////////////////////////////////////////////////////////////////

  public BStatus getStatus() { return getOut().getStatus(); }

////////////////////////////////////////////////////////////////
// BINumeric interface
////////////////////////////////////////////////////////////////

  public double getNumeric() { return getOut().getValue(); }

  public final BFacets getNumericFacets() { return getOut().getStatus().getFacets(); }

  /**
   * Get the icon.
   */
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/cochraneicon.png");


}

