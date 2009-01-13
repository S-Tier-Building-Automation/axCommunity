
package org.axcommunity.niagara.logic;

import javax.baja.sys.*;
import javax.baja.status.*;
/**
 *The outputs for Analog and Boolean have to either the NormalOpen input or
 *the NormalClose input.  This is determined by booleanCmd input.
 */
public class BToggleSwitch extends BComponent
{     

////////////////////////////////////////////////////////////////
// Property "booleanCmd"
////////////////////////////////////////////////////////////////
  
  /**
   * input that determines if using NormalOpen or NormalClose
   * inputs for the outputs.  If true, use NormalOpen
   */
  public static final Property booleanCmd = newProperty(0, new BStatusBoolean(),null);
  
  /**
   * Get the <code>booleanCmd</code> property.
   */
  public BStatusBoolean getBooleanCmd() { return (BStatusBoolean)get(booleanCmd); }
  
  /**
   * Set the <code>booleanCmd</code> property.
   *@author Kevin Crabill, Cochrane Supply
 */
  public void setBooleanCmd(BStatusBoolean v) { set(booleanCmd,v,null); }

////////////////////////////////////////////////////////////////
// Property "boolNormalOpen"
////////////////////////////////////////////////////////////////
  
  /**
   * input for the BooleanNormalOpen
   */
  public static final Property boolNormalOpen = newProperty(0, new BStatusBoolean(),null);
  
  /**
   * Get the <code>boolNormalOpen</code> property.
   */
  public BStatusBoolean getBoolNormalOpen() { return (BStatusBoolean)get(boolNormalOpen); }
  
  /**
   * Set the <code>boolNormalOpen</code> property.
   */
  public void setBoolNormalOpen(BStatusBoolean v) { set(boolNormalOpen,v,null); }

////////////////////////////////////////////////////////////////
// Property "boolNormalClose"
////////////////////////////////////////////////////////////////
  
  /**
   * input for the BooleanNormalClose
   */
  public static final Property boolNormalClose = newProperty(0, new BStatusBoolean(),null);
  
  /**
   * Get the <code>boolNormalClose</code> property.
   */
  public BStatusBoolean getBoolNormalClose() { return (BStatusBoolean)get(boolNormalClose); }
  
  /**
   * Set the <code>boolNormalClose</code> property.
   */
  public void setBoolNormalClose(BStatusBoolean v) { set(boolNormalClose,v,null); }

////////////////////////////////////////////////////////////////
// Property "boolOutput"
////////////////////////////////////////////////////////////////
  
  /**
   * output could be either BooleanNormalOpen or BooleanNormalClose
   * depending on BooleanCmd input
   */
  public static final Property boolOutput = newProperty(0, new BStatusBoolean(),null);
  
  /**
   * Get the <code>boolOutput</code> property.
   */
  public BStatusBoolean getBoolOutput() { return (BStatusBoolean)get(boolOutput); }
  
  /**
   * Set the <code>boolOutput</code> property.
   */
  public void setBoolOutput(BStatusBoolean v) { set(boolOutput,v,null); }

////////////////////////////////////////////////////////////////
// Property "analogNormalOpen"
////////////////////////////////////////////////////////////////
  
  /**
   * input for the AnalogNormalOpen
   */
  public static final Property analogNormalOpen = newProperty(0, new BStatusNumeric(),null);
  
  /**
   * Get the <code>analogNormalOpen</code> property.
   */
  public BStatusNumeric getAnalogNormalOpen() { return (BStatusNumeric)get(analogNormalOpen); }
  
  /**
   * Set the <code>analogNormalOpen</code> property.
   */
  public void setAnalogNormalOpen(BStatusNumeric v) { set(analogNormalOpen,v,null); }

////////////////////////////////////////////////////////////////
// Property "analogNormalClose"
////////////////////////////////////////////////////////////////
  
  /**
   * input for the AnalogNormalClose
   */
  public static final Property analogNormalClose = newProperty(0, new BStatusNumeric(),null);
  
  /**
   * Get the <code>analogNormalClose</code> property.
   */
  public BStatusNumeric getAnalogNormalClose() { return (BStatusNumeric)get(analogNormalClose); }
  
  /**
   * Set the <code>analogNormalClose</code> property.
   */
  public void setAnalogNormalClose(BStatusNumeric v) { set(analogNormalClose,v,null); }

////////////////////////////////////////////////////////////////
// Property "analogOutput"
////////////////////////////////////////////////////////////////
  
  /**
   * output could be either AnalogNormalOpen or AnalogNormalClose
   * depending on BooleanCmd input
   */
  public static final Property analogOutput = newProperty(0, new BStatusNumeric(),null);
  
  /**
   * Get the <code>analogOutput</code> property.
   * @see com.cochrane.ian.BToggleSwitch#analogOutput
   */
  public BStatusNumeric getAnalogOutput() { return (BStatusNumeric)get(analogOutput); }
  
  /**
   * Set the <code>analogOutput</code> property.
   */
  public void setAnalogOutput(BStatusNumeric v) { set(analogOutput,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BToggleSwitch.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/cochraneicon.png");

  public void changed(Property prop, Context cx)
  { 
    if( prop != boolOutput && prop != analogOutput )
    {                     
       if( getBooleanCmd().getValue() )
       { 
         if( getBoolNormalOpen().getStatus().isNull() )
         {
            getBoolOutput().setStatusNull(true);   
         }
         else
         {
            setBoolOutput( new BStatusBoolean( getBoolNormalOpen().getValue(), BStatus.ok ) );
         }
         
         if( getAnalogNormalOpen().getStatus().isNull() )
         {
            getAnalogOutput().setStatusNull(true);
         }
         else
         {         
            setAnalogOutput( new BStatusNumeric( getAnalogNormalOpen().getValue(), BStatus.ok ) );
         }
       }
       else
       { 
         if( getBoolNormalClose().getStatus().isNull() )
         {
            getBoolOutput().setStatusNull(true);
         }
         else
         {
            setBoolOutput( new BStatusBoolean( getBoolNormalClose().getValue(), BStatus.ok ) );
         }
         
         if( getAnalogNormalClose().getStatus().isNull() )
         {
            getAnalogOutput().setStatusNull(true);
         }
         else
         {
            setAnalogOutput( new BStatusNumeric( getAnalogNormalClose().getValue(), BStatus.ok ) );
         }      
       }
    }
  }
}

