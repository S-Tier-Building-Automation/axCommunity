package org.axcommunity.niagara.conversion;

import javax.baja.status.*;
import javax.baja.sys.*;

/**Converts a StatusBoolean input to a 2-state StatusEnum
* Just tacking on a Boolean Input to the BStatusNumericToStatusEnum by Andy Saunders @ tridium
* to make 1 object instead of stringing 3 together.
* @author Vance Hensley, pctechs4u
* @creation Apr 25, 2009
* modified BStatusEnum "out" slot to "outEnum" to accommodate add below
* added BStatusNumeric "outNumeric" slot to convert boolean to numeric to make a multi-purpose object.
* @modified July, 17, 2009
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
public static final Property trueValue = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);

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
public static final Property falseValue = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);

/**
* Get the <code>falseValue</code> property.
*/
public BStatusNumeric getFalseValue() { return (BStatusNumeric)get(falseValue); }

/**
* Set the <code>falseValue</code> property.
*/
public void setFalseValue(BStatusNumeric v) { set(falseValue,v,null); }

////////////////////////////////////////////////////////////////
//Property "outEnum"
////////////////////////////////////////////////////////////////

/**
 * Slot for the <code>outEnum</code> property.
 */
public static final Property outEnum = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusEnum(),null);

/**
 * Get the <code>outEnum</code> property.
 */
public BStatusEnum getOutEnum() { return (BStatusEnum)get(outEnum); }

/**
 * Set the <code>outEnum</code> property.
 */
public void setOutEnum(BStatusEnum v) { set(outEnum,v,null); }

////////////////////////////////////////////////////////////////
//Property "outNumeric"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>outNumeric</code> property.
*/
public static final Property outNumeric = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusNumeric(),null);

/**
* Get the <code>outNumeric</code> property.
*/
public BStatusNumeric getOutNumeric() { return (BStatusNumeric)get(outNumeric); }

/**
* Set the <code>outNumeric</code> property.
*/
public void setOutNumeric(BStatusNumeric v) { set(outNumeric,v,null); }

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
       BStatusEnum bstatusenum = getOutEnum();
        boolean flag = getIn().getValue();
        if(flag)
    {
            bstatusenum.setValue(BDynamicEnum.make((int)getTrueValue().getValue()));
            bstatusenum.setStatusNull(false);
            bstatusenum.setStatusFault(false);
    } else
        if(!flag)
    {
            bstatusenum.setValue(BDynamicEnum.make((int)getFalseValue().getValue()));
            bstatusenum.setStatusNull(false);
            bstatusenum.setStatusFault(false);
    } else
    {
            bstatusenum.setStatusNull(true);
           bstatusenum.setStatusFault(true);
    }
        BStatusNumeric bstatusnumeric = getOutNumeric();
        if(flag)
    {
            bstatusnumeric.setValue(getTrueValue().getValue());
            bstatusnumeric.setStatusNull(false);
            bstatusnumeric.setStatusFault(false);
    } else
        if(!flag)
    {
            bstatusnumeric.setValue(getFalseValue().getValue());
            bstatusnumeric.setStatusNull(false);
            bstatusnumeric.setStatusFault(false);
    } else
    {
            bstatusnumeric.setStatusNull(true);
            bstatusnumeric.setStatusFault(true);
    }
        setOutEnum(bstatusenum);
        setOutNumeric(bstatusnumeric);
}


public String toString(Context cx)
{
  return getOutEnum().toString(cx);
}

/**
 * Apply the "facets" property to the "out" property.
 */
public BFacets getSlotFacets(Slot slot)
{
  if (slot == outEnum)return getFacets();
  return super.getSlotFacets(slot);
}

/////////////////////////////////////////////////////////////////
//End main code
/////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
//Type
////////////////////////////////////////////////////////////////
public BIcon getIcon() { return icon; }
private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/pctechs4u.png");

public static final Type TYPE = Sys.loadType(BStatusBooleanToStatusEnum.class);
public Type getType() { return TYPE; }
}