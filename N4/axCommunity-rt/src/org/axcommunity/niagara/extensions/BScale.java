package org.axcommunity.niagara.extensions;

import javax.baja.control.*;
import javax.baja.status.*;
import javax.baja.sys.*;

/**
 *  Scales the output of a numeric point.
 *
 * @author Eric Bishop, Texas Machining Technologies
 * @creation Mar 20, 2013
 */

public class BScale extends BPointExtension implements Runnable
{
  public void run() { System.out.println("Source BProgram did not override run(). Exiting thread."); }
  
  public static final Property addToInput = newProperty(Flags.SUMMARY, new BStatusNumeric());
  public BStatusNumeric getAddToInput() { return (BStatusNumeric)get(addToInput); }
  public void setAddToInput(BStatusNumeric v) { set(addToInput,v,null); }

  public static final Property multiplier = newProperty(Flags.SUMMARY, new BStatusNumeric(1),BFacets.make(BFacets.PRECISION, BInteger.make(8)));
  public BStatusNumeric getMultiplier() { return (BStatusNumeric)get(multiplier); }
  public void setMultiplier(BStatusNumeric v) { set(multiplier,v,null); }

  public static final Property divisor = newProperty(Flags.SUMMARY, new BStatusNumeric(1),BFacets.make(BFacets.PRECISION, BInteger.make(8)));
  public BStatusNumeric getDivisor() { return (BStatusNumeric)get(divisor); }
  public void setDivisor(BStatusNumeric v) { set(divisor,v,null); }

  public static final Property addToOutput = newProperty(Flags.SUMMARY, new BStatusNumeric());
  public BStatusNumeric getAddToOutput() { return (BStatusNumeric)get(addToOutput); }
  public void setAddToOutput(BStatusNumeric v) { set(addToOutput,v,null); }

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BScale.class);

  /**Forces the parent to be a numeric point.*/
  public boolean isParentLegal(BComponent parent)
  {
    if (parent instanceof BNumericPoint) return true;
    else return false;
  }

  public void changed(Property p, Context cx)
  {
    super.changed(p, cx);
    if(!isRunning()) return;
    if(p.equals(multiplier) || p.equals(addToInput) || p.equals(addToOutput) || p.equals(divisor))
    {
      BControlPoint parent = getParentPoint();
      if(parent != null) getParentPoint().execute();
    }
  }

  /**Called when either me or my parent control point is updated.*/
  public void onExecute(BStatusValue o, Context cx)
  {
    BStatusNumeric out = (BStatusNumeric)o;                   //parent output
    
    myTempDouble = out.getValue();
    
    myTempDouble = myTempDouble + getAddToInput().getValue();
    myTempDouble = myTempDouble * getMultiplier().getValue();
    myTempDouble = myTempDouble / getDivisor().getValue();
    myTempDouble = myTempDouble + getAddToOutput().getValue();
    
    o.setValueValue(BDouble.make(myTempDouble));
  }

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/TMT.png");

  double myTempDouble;
}