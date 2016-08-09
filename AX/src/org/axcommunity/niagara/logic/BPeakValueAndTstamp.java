/**
 * Used to determine peaks and Date/Time stamps of the peaks, set up to "zero out" monthly
 * @author vance.hensley
 * @creation May 28, 2009 
 */
package org.axcommunity.niagara.logic;

import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.status.*;
import java.text.*;
import java.util.*;

public class BPeakValueAndTstamp extends BComponent
{
////////////////////////////////////////////////////////////////
//Property "facets"
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
//Property "in"
////////////////////////////////////////////////////////////////

/**
 * Input being looked at for peaks
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
//Property "hold"
////////////////////////////////////////////////////////////////

/**
* hold value for comparing new input value versus the last peak
*/
public static final Property hold = newProperty(Flags.HIDDEN, new BStatusNumeric(),null);
/**
* Get the <code>hold</code> property.
*/
public BStatusNumeric getHold() { return (BStatusNumeric)get(hold); }
/**
* Set the <code>hold</code> property.
*/
public void setHold(BStatusNumeric v) { set(hold,v,null); }

////////////////////////////////////////////////////////////////
//Property "out"
////////////////////////////////////////////////////////////////

/**
* output of the current peak.
*/
public static final Property out = newProperty(Flags.TRANSIENT|Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(),null);
/**
* Get the <code>out</code> property.
*/
public BStatusNumeric getOut() { return (BStatusNumeric)get(out); }
/**
* Set the <code>out</code> property.
 */
public void setOut(BStatusNumeric v) { set(out,v,null); }

////////////////////////////////////////////////////////////////
//Property "time"
////////////////////////////////////////////////////////////////

/**
* time as a string.
*/
public static final Property time = newProperty(Flags.HIDDEN, new BStatusString(),null);
/**
* Get the <code>time</code> property.
*/
public BStatusString getTime() { return (BStatusString)get(time); }
/**
* Set the <code>time</code> property.
*/
public void setTime(BStatusString v) { set(time,v,null); }

////////////////////////////////////////////////////////////////
//Property "date"
////////////////////////////////////////////////////////////////

/**
* time as a string.
*/
public static final Property date = newProperty(Flags.HIDDEN, new BStatusString(),null);
/**
* Get the <code>date</code> property.
*/
public BStatusString getDate() { return (BStatusString)get(date); }
/**
* Set the <code>date</code> property.
*/
public void setDate(BStatusString v) { set(date,v,null); }

////////////////////////////////////////////////////////////////
//Property "outTstamp"
////////////////////////////////////////////////////////////////

/**
* DateTimestamp of the peak as a string.
*/
public static final Property outTstamp = newProperty(Flags.SUMMARY, new BStatusString(),null);
/**
* Get the <code>outTstamp</code> property.
*/
public BStatusString getOutTstamp() { return (BStatusString)get(outTstamp); }
/**
* Set the <code>outTstamp</code> property.
*/
public void setOutTstamp(BStatusString v) { set(outTstamp,v,null); }

  
public void changed(Property p, Context cx)
{
  if (!isRunning()) return;
  if (p == in)
  {
    calculate();
  }
}
  
  public void calculate()
  {
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    if(!getIn().getStatus().isValid())
      return;
    
    if(getIn().getValue() >= getHold().getValue())
    {
      getOut().setValue(getIn().getValue());
      Date now = new Date();
      getTime().setValue(timeFormat.format(now));
      getDate().setValue(dateFormat.format(now)); 
      getOutTstamp().setValue(getTime().getValue() + " " + getDate().getValue());
      getHold().setValue(getIn().getValue());
    }
    Date now = new Date();
    if (now.getHours()==0 && now.getMinutes()==0 && now.getSeconds()<=30 && now.getDay()==01)
    {
      getHold().setValue(0.0);
    }
  }

  
  public void doClear()
  {
      getHold().setValue(0.0);
  }
public static final Action clear = newAction(0,null);
public void clear() { invoke(clear,null,null); }

public Type getType() { return TYPE; }
public static final Type TYPE = Sys.loadType(BPeakValueAndTstamp.class);

public BIcon getIcon() { return icon; }
private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/HBTechLogo.png");

}
