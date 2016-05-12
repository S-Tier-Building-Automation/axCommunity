package org.axcommunity.niagara.math;


import com.tridium.kitControl.constants.BNumericConst;

import javax.baja.sys.*;


/** Numeric Writable with actions to perform simple math functions (add, sub, mult, div)
 * @author Mike Arnott, Kors Engineering
*/

public class BNumericConstantAdjustable
    extends BNumericConst
{
  /**add triggered value to the current output value*/
  public static final Action addToOutput = newAction(Flags.SUMMARY,BDouble.make(0));
  public void addToOutput(BDouble add) { 
      invoke(addToOutput,  add);
  }
  public void doAddToOutput (BDouble add)
  {
    getOut().setValue(getOut().getValue() + add.getDouble());
  }
  
  /**subtract triggered value from the current output value*/
  public static final Action subtractFromOutput = newAction(Flags.SUMMARY,BDouble.make(0));
  public void subtractFromOutput(BDouble sub) { 
      invoke(subtractFromOutput,  sub);
  }
  public void doSubtractFromOutput (BDouble sub)
  {
    getOut().setValue(getOut().getValue() - sub.getDouble());
  }

  
  /**multiply current output by triggered value*/
  public static final Action multiplyOutput = newAction(Flags.SUMMARY,BDouble.make(0));
  public void multiplyOutput(BDouble mult) { 
      invoke(multiplyOutput,  mult);
  }
  public void doMultiplyOutput (BDouble mult)
  {
    getOut().setValue(getOut().getValue() * mult.getDouble());
  }  
  
  /**divide current output by triggered value*/
  public static final Action divideOutput = newAction(Flags.SUMMARY,BDouble.make(0));
  public void divideOutput(BDouble div) { 
      invoke(divideOutput,  div);
  }
  public void doDivideOutput (BDouble div)
  {
    if(!(div.getDouble()==0)) getOut().setValue(getOut().getValue() / div.getDouble());
  }  

  
  /**reset the current output value to 0*/
  public static final Action resetOutput = newAction(Flags.SUMMARY,null);
  public void resetOutput() { 
      invoke(resetOutput,null);
  }
  public void doResetOutput ()
  {
    getOut().setValue(0);
  }
  
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
      
 public static final Type TYPE = Sys.loadType(BNumericConstantAdjustable.class);
 public Type getType() { return TYPE; }   

  
}
