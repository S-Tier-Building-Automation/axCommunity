
package org.axcommunity.niagara.logic;

import javax.baja.status.*;
import javax.baja.sys.*;

/**
 * This object has two different actions on it.  The latch action sets the out
 * property to true while the unlatch action sets it to false.
  *@author Kevin Crabill, Cochrane Supply
*/
public class BToggleLatch extends BComponent
{                                           

////////////////////////////////////////////////////////////////
// Property "out"
////////////////////////////////////////////////////////////////
  
  /**
   * boolean output
   */
  public static final Property out = newProperty(Flags.SUMMARY, new BStatusBoolean(),null);
  
  /**
   * Get the <code>out</code> property.
   */
  public BStatusBoolean getOut() { return (BStatusBoolean)get(out); }
  
  /**
   * Set the <code>out</code> property.
   */
  public void setOut(BStatusBoolean v) { set(out,v,null); }

////////////////////////////////////////////////////////////////
// Action "latch"
////////////////////////////////////////////////////////////////
  
  /**
   * Sets out to True 
   */
  public static final Action latch = newAction(Flags.SUMMARY,null);
  
  /**
   * Invoke the <code>latch</code> action.
    */
  public void latch() { invoke(latch,null,null); }

////////////////////////////////////////////////////////////////
// Action "unlatch"
////////////////////////////////////////////////////////////////
  
  /**
   * Sets out to false 
   */
  public static final Action unlatch = newAction(Flags.SUMMARY,null);
  
  /**
   * Invoke the <code>unlatch</code> action.
    */
  public void unlatch() { invoke(unlatch,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BToggleLatch.class);
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/cochraneicon.png");

  public void doLatch()
  {
    setOut( new BStatusBoolean( true ) );
  }
                                      
  public void doUnlatch()
  {
    setOut( new BStatusBoolean( false ) );
  }
}

