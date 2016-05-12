package org.axcommunity.niagara.math;

import javax.baja.status.*;
import javax.baja.sys.*;


/**
 * The BLimit takes a StatusNumeric and limits the output 
 * @author    Dean Mynott       - Ronin Control Systems Pty Ltd
 * @creation  28 May 2012
 */

public class BLimitOutput
  extends BComponent
{ 

////////////////////////////////////////////////////////////////
// Property "input"
////////////////////////////////////////////////////////////////
  public static final Property input = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getInput() { return (BStatusNumeric)get(input); }
  public void setInput(BStatusNumeric v) { set(input,v,null); }

////////////////////////////////////////////////////////////////
// Property "min"
////////////////////////////////////////////////////////////////
  public static final Property min = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getMin() { return (BStatusNumeric)get(min); }
  public void setMin(BStatusNumeric v) { set(min, v, null); }

////////////////////////////////////////////////////////////////
// Property "max"
////////////////////////////////////////////////////////////////
  public static final Property max = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getMax() { return (BStatusNumeric)get(max); }
  public void setMax(BStatusNumeric v) { set(max, v, null); }

////////////////////////////////////////////////////////////////
// Property "output"
////////////////////////////////////////////////////////////////
  public static final Property output = newProperty(0|Flags.SUMMARY, new BStatusNumeric(), null);
  public BStatusNumeric getOutput() { return (BStatusNumeric)get(output); }
  public void setOutput(BStatusNumeric v) { set(output,v,null); }


 public void changed(Property property, Context context)
     {
        super.changed(property, context);
        if(!isRunning())
        {
            return;
        }
        
        if (property == getProperty("input")||property == getProperty("min")||property == getProperty("max"))
        {
         
         double mn, mx, in ;
         
         in = getInput().getValue();
         mn = getMin().getValue();
         mx = getMax().getValue();
                    
         if ( getMin().getStatus() != BStatus.nullStatus )
             {
             if (in < mn)  in = mn ;
             }
             
         if ( getMax().getStatus() != BStatus.nullStatus )
             {
             if (in > mx)  in = mx ;
             }
 
         getOutput().setStatus (getInput().getStatus());           
         getOutput().setValue(in);  
         
         
        }
      }

////////////////////////////////////////////////////////////////
// Icon
////////////////////////////////////////////////////////////////
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png");


////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BLimitOutput.class);





}