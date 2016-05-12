
package org.axcommunity.niagara.hvac;

import javax.baja.sys.*;
import javax.baja.status.*;

import com.tridium.kitControl.logic.*;
import com.tridium.kitControl.enums.*;

/**
 * BTstatB is a two position thermostat with 2 setpoints  

 
 *  Direct Acting ( onSP > offSP) : 
 *  -  on if CV goes higher than onSP 
 *  - off if CV goes lower than offSP
   
 *  Reverse Acting ( onSP < offSP) :  
 *  -  on if CV goes lower than onSP 
 *  - off if CV goes higher than offSP  
 
 * @author    Dean Mynott       - Ronin Control Systems Pty Ltd
 * @creation  13 Nov 2011 
 */ 
 
public class BTstatB extends BLogic
{ 


////////////////////////////////////////////////////////////////
// Property "cv"
////////////////////////////////////////////////////////////////
  
  public static final Property cv = newProperty(Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getCv() { return (BStatusNumeric)get(cv); }
  public void setCv(BStatusNumeric v) { set(cv,v,null); }

////////////////////////////////////////////////////////////////
// Property "onSP"
//////////////////////////////////////////////////////////////// 
  public static final Property onSP = newProperty(Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getOnSP() { return (BStatusNumeric)get(onSP); }
  public void setOnSP(BStatusNumeric v)  { set(onSP,v,null); } 
  
  
  
////////////////////////////////////////////////////////////////
// Property "offSP"
//////////////////////////////////////////////////////////////// 
  public static final Property offSP = newProperty(Flags.SUMMARY, new BStatusNumeric(0, BStatus.nullStatus),null);
  public BStatusNumeric getOffSP() { return (BStatusNumeric)get(offSP); }
  public void setOffSP(BStatusNumeric v)
  { 
    set(offSP,v,null); 
  } 


////////////////////////////////////////////////////////////////
// Property "nullOnInControl"
////////////////////////////////////////////////////////////////
  public static final Property nullOnInControl = newProperty(0, false,null);
  public boolean getNullOnInControl() { return getBoolean(nullOnInControl); }
  public void setNullOnInControl(boolean v) { setBoolean(nullOnInControl,v,null); }
  
////////////////////////////////////////////////////////////////
// Property "effectiveAction"
////////////////////////////////////////////////////////////////
  public static final Property effectiveAction = newProperty(Flags.SUMMARY|Flags.OPERATOR|Flags.READONLY|Flags.TRANSIENT, BLoopAction.direct ,null);
  public BLoopAction getEffectiveAction() { return (BLoopAction)get(effectiveAction); }
  public void setEffectiveAction(BLoopAction v) { set(effectiveAction,v,null); }


////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTstatB.class);

////////////////////////////////////////////////////////////////
//icon for this component  
////////////////////////////////////////////////////////////////

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png"); 


  public void onExecute(BStatusValue o, Context cx)
 // public void onExecute()
  {
    //double v     = getCv().getValue();
    //double s1     = getOnSP().getValue();
    //double s2     = getOffSP().getValue();
    BStatusBoolean out   = (BStatusBoolean)o;

    BStatus sa = getCv().getStatus();
    BStatus sb = getOnSP().getStatus();
    BStatus sc = getOffSP().getStatus();
    
    //  If either input is null, force the output
    //  to null
    if (sa.isNull() || sb.isNull() || sc.isNull() )
    { 
      out.setValue(false);
      out.setStatus(BStatus.nullStatus);            
    }
    else
    {
      out.setStatus(propagate(BStatus.make(sa.getBits() | sb.getBits() | sc.getBits())));
      
      //  If either input is invalid, force the output
      //  to false
      if (!sa.isValid() || !sb.isValid() || !sc.isValid())        
        out.setValue(false);
      else
        
        if ( getOnSP().getValue() >= getOffSP().getValue() )
         { setEffectiveAction(BLoopAction.direct); }
        else
         { setEffectiveAction(BLoopAction.reverse); }
        
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
   double on  = getOnSP().getValue();   
   double off = getOffSP().getValue();
   
   boolean     over         ;
   boolean     under        ;    
   boolean     returnValue  = getOut().getValue();
   boolean     action = ( on >= off); 
   
    
    if (action )            //direct
      {
      over  = ( v >= on );
      under = ( v <= off ) ;    

      if( over )
        returnValue = true;
      else if ( under ) 
        returnValue = false; 
      }
    else     
      {
      over  = ( v <= on );
      under = ( v >= off ) ;    

      if( over )
        returnValue = true;
      else if ( under ) 
        returnValue = false; 
      }    
   
    inControl = !over && !under; 
    return returnValue;
  }

  boolean inControl = false;
}


 


  
