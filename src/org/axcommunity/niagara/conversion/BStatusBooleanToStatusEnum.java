package org.axcommunity.niagara.conversion;

import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.control.*;
import javax.baja.control.util.*;
import javax.baja.control.enums.*;

/**Converts a StatusBoolean input to a 2-state StatusEnum
* Just tacking on a Boolean Input to the BStatusNumericToStatusEnum by Andy Saunders @ tridium
* to make 1 object instead of stringing 3 together.
* @author Vance Hensley, pctechs4u
* @creation Apr 25, 2009
*/
public class BStatusBooleanToStatusEnum
  extends BComponent
 {
////////////////////////////////////////////////////////////////
//Property "facets"
////////////////////////////////////////////////////////////////
 
 /**
  * Slot for the <code>facets</code> property.
  * These facets are applied against the out property.
  */
 public static final Property facets = newProperty(0, BFacets.makeEnum(),null);
 
 /**
  * Get the <code>facets</code> property.
  */
 public BFacets getFacets() { return (BFacets)get(facets); }
 
 /**
  * Set the <code>facets</code> property.
  */
 public void setFacets(BFacets v) { set(facets,v,null); }
  
////////////////////////////////////////////////////////////////
//Property "in"
////////////////////////////////////////////////////////////////
 
 /**
  * Slot for the <code>in</code> property.
  */
 public static final Property in = newProperty(Flags.TRANSIENT|Flags.SUMMARY, new BStatusBoolean(),null);
 
 /**
  * Get the <code>in</code> property.
  */
 public BStatusBoolean getIn() { return (BStatusBoolean)get(in); }
 
 /**
  * Set the <code>in</code> property.
  */
 public void setIn(BStatusBoolean v) { set(in,v,null); }
 
////////////////////////////////////////////////////////////////
//Property "trueValue"
////////////////////////////////////////////////////////////////

/**
 * Slot for the <code>trueValue</code> property.
 */
public static final Property trueValue = newProperty(Flags.TRANSIENT|Flags.SUMMARY, new BStatusNumeric(),null);

/**
 * Get the <code>trueValue</code> property.
 */
public BStatusNumeric getTrueValue() { return (BStatusNumeric)get(trueValue); }

/**
 * Set the <code>trueValue</code> property.
 */
public void setTrueValue(BStatusNumeric v) { set(trueValue,v,null); }

////////////////////////////////////////////////////////////////
//Property "falseValue"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>falseValue</code> property.
*/
public static final Property falseValue = newProperty(Flags.TRANSIENT|Flags.SUMMARY, new BStatusNumeric(),null);

/**
* Get the <code>falseValue</code> property.
*/
public BStatusNumeric getFalseValue() { return (BStatusNumeric)get(falseValue); }

/**
* Set the <code>falseValue</code> property.
*/
public void setFalseValue(BStatusNumeric v) { set(falseValue,v,null); }

////////////////////////////////////////////////////////////////
//Property "out"
////////////////////////////////////////////////////////////////

/**
 * Slot for the <code>out</code> property.
 */
public static final Property out = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusEnum(),null);

/**
 * Get the <code>out</code> property.
 */
public BStatusEnum getOut() { return (BStatusEnum)get(out); }

/**
 * Set the <code>out</code> property.
 */
public void setOut(BStatusEnum v) { set(out,v,null); }

/////////////////////////////////////////////////////////////////
// Begin main code
/////////////////////////////////////////////////////////////////
/**
 * Init if started after steady state has been reached.
 */
public void started()
{
  calculate();
}

/**
 * set output on in change.
 */
public void changed(Property p, Context cx)
{
  if (!isRunning()) return;

  if (p.equals(in) || p.equals(trueValue) || p.equals(falseValue))
  {
    calculate();
  }
}

void calculate()
{
  BStatusEnum workingValue = getOut();
  boolean inValue = getIn().getValue();
  if(inValue)
    {
    workingValue.setValue(BDynamicEnum.make((int)getTrueValue().getValue()) );
    workingValue.setStatusNull(false);
    workingValue.setStatusFault(false);
    }
  else if(!inValue)
    {
    workingValue.setValue(BDynamicEnum.make((int)getFalseValue().getValue()) );
    workingValue.setStatusNull(false);
    workingValue.setStatusFault(false);
    }
  else    
  {
    workingValue.setStatusNull(true);
    workingValue.setStatusFault(true);
  }
  setOut(workingValue);
}

public String toString(Context cx)
{
  return getOut().toString(cx);
}

/**
 * Apply the "facets" property to the "out" property.
 */
public BFacets getSlotFacets(Slot slot)
{
  if (slot == out)return getFacets();
  return super.getSlotFacets(slot);
}

/////////////////////////////////////////////////////////////////
//End main code
/////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
//Type
////////////////////////////////////////////////////////////////
public BIcon getIcon() { return icon; }
private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/pctechs4u.png");

public static final Type TYPE = Sys.loadType(BStatusBooleanToStatusEnum.class);
public Type getType() { return TYPE; }
}