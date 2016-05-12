
package org.axcommunity.niagara.conversion;

import javax.baja.driver.ping.*;
import javax.baja.status.*;
import javax.baja.sys.*;

/**
 *Takes a BPingHealth input from a driver device and splits it into
 *three different outputs.
 *@author Kevin Crabill, Cochrane Supply
*/
public class BPingHealthConverter extends BComponent
{     

////////////////////////////////////////////////////////////////
// Property "inHealth"
////////////////////////////////////////////////////////////////
  
  /**
   * BPingHealth input typically on a driver device object
   */
  public static final Property inHealth = newProperty(Flags.SUMMARY, new BPingHealth(),null);
  
  /**
   * Get the <code>inHealth</code> property.
   */
  public BPingHealth getInHealth() { return (BPingHealth)get(inHealth); }
  
  /**
   * Set the <code>inHealth</code> property.
   */
  public void setInHealth(BPingHealth v) { set(inHealth,v,null); }

////////////////////////////////////////////////////////////////
// Property "lastOkTime"
////////////////////////////////////////////////////////////////
  
  /**
   * output displaying the lastOkTime from inHealth
   */
  public static final Property lastOkTime = newProperty(Flags.SUMMARY, BAbsTime.now(),null);
  
  /**
   * Get the <code>lastOkTime</code> property.
   */
  public BAbsTime getLastOkTime() { return (BAbsTime)get(lastOkTime); }
  
  /**
   * Set the <code>lastOkTime</code> property.
   */
  public void setLastOkTime(BAbsTime v) { set(lastOkTime,v,null); }

////////////////////////////////////////////////////////////////
// Property "lastFailTime"
////////////////////////////////////////////////////////////////
  
  /**
   *  output displaying the lastFailTime from inHealth.
   */
  public static final Property lastFailTime = newProperty(Flags.SUMMARY, BAbsTime.now(),null);
  
  /**
   * Get the <code>lastFailTime</code> property.
   */
  public BAbsTime getLastFailTime() { return (BAbsTime)get(lastFailTime); }
  
  /**
   * Set the <code>lastFailTime</code> property.
   */
  public void setLastFailTime(BAbsTime v) { set(lastFailTime,v,null); }

////////////////////////////////////////////////////////////////
// Property "lastFaultCause"
////////////////////////////////////////////////////////////////
  
  /**
   * output displaying the lastFaultCause from inHealth.
   */
  public static final Property lastFaultCause = newProperty(0, new BStatusString(),null);
  
  /**
   * Get the <code>lastFaultCause</code> property.
   */
  public BStatusString getLastFaultCause() { return (BStatusString)get(lastFaultCause); }
  
  /**
   * Set the <code>lastFaultCause</code> property.
   */
  public void setLastFaultCause(BStatusString v) { set(lastFaultCause,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BPingHealthConverter.class);
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/cochraneicon.png");

  
  
/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public void changed(Property prop, Context cx)
  { 
    if( prop == inHealth )
    {               
       setLastOkTime( getInHealth().getLastOkTime() );
       setLastFailTime( getInHealth().getLastFailTime() );
       setLastFaultCause( new BStatusString( getInHealth().getLastFailCause() ) );        
    }
  }
}

