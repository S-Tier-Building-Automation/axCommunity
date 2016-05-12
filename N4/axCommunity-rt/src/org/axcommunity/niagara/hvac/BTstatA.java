
package org.axcommunity.niagara.hvac;

import com.tridium.kitControl.logic.*;

import javax.baja.status.*;
import javax.baja.sys.*;

/**
 * BTstatA is a two position thermostat with setpoint and differential. 
 * - if the Differential is (+)ve then it is Direct Acting
 * - if the Differential is (-)ve then it is Reverse Acting
 * - built on the framework that Andy Saunders authored

 * @author    Dean Mynott       - Ronin Control Systems Pty Ltd
 * @creation  13 Nov 2011 
 */  
 
public class BTstatA extends BLogic
{ 


////////////////////////////////////////////////////////////////
// Property "cv"
////////////////////////////////////////////////////////////////
  
  public static final Property cv = newProperty(Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getCv() { return (BStatusNumeric)get(cv); }
  public void setCv(BStatusNumeric v) { set(cv,v,null); }

////////////////////////////////////////////////////////////////
// Property "sp"
////////////////////////////////////////////////////////////////
  
  public static final Property sp = newProperty(Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getSp() { return (BStatusNumeric)get(sp); }
  public void setSp(BStatusNumeric v) { set(sp,v,null); }

////////////////////////////////////////////////////////////////
// Property "diff"
////////////////////////////////////////////////////////////////
  public static final Property diff = newProperty(Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getDiff() { return (BStatusNumeric)get(diff); }
  public void setDiff(BStatusNumeric v) { set(diff,v,null); }

////////////////////////////////////////////////////////////////
// Property "nullOnInControl"
////////////////////////////////////////////////////////////////
  public static final Property nullOnInControl = newProperty(0, false,null);
  public boolean getNullOnInControl() { return getBoolean(nullOnInControl); }
  public void setNullOnInControl(boolean v) { setBoolean(nullOnInControl,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTstatA.class);

////////////////////////////////////////////////////////////////
//icon for this component  
////////////////////////////////////////////////////////////////

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png"); 


  public void onExecute(BStatusValue o, Context cx)
 // public void onExecute()
  {
    BStatusBoolean out   = (BStatusBoolean)o;

    BStatus sa = getCv().getStatus();
    BStatus sb = getSp().getStatus();

    //  If either input is null, force the output
    //  to null
    if (sa.isNull() || sb.isNull())
    { 
      out.setValue(false);
      out.setStatus(BStatus.nullStatus);            
    }
    else
    {
      out.setStatus(propagate(BStatus.make(sa.getBits() | sb.getBits())));
  
      //  If either input is invalid, force the output
      //  to false
      if (!sa.isValid() || !sb.isValid())        
        out.setValue(false);
      else
        out.setValue(calculate());         
      if(getNullOnInactive() && !(out.getValue()) )
        out.setStatusNull(true);
      else if(getNullOnInControl() && inControl)
      out.setStatusNull(true);
    }    
  }

  protected boolean calculate()
  {
    
   double v   = getCv().getValue();
   double s   = getSp().getValue();
   double d   = getDiff().getValue();    
    
    
    boolean     over         ;
    boolean     under        ;    
    boolean     returnValue  = getOut().getValue();
    boolean     direct       = (d >= 0) ;     
   
    if (direct)
    {
      over  = ( v >= s );
      under = ( v < s - d ) ;    

      if(over)
        returnValue = true;
      else if (under) 
        returnValue = false; 
         
    }
    else
    {
      over  = ( v <= s + d);      // changed 2012-06-17
      under = ( v > s ) ;         // changed 2012-06-17   

      if(over)
        returnValue = true;
      else if (under) 
        returnValue = false; 
         
    }
    
    inControl = !over && !under; 
    return returnValue;
  }

  boolean inControl = false;
}


 


  
