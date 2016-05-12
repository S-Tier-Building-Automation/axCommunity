package org.axcommunity.niagara.math;

import javax.baja.status.BStatusNumeric;
import javax.baja.sys.*;
/**
 * takes one abs time and one number as seconds, adds seconds 
 * (positive or negative) to time input to create time output
 * @author Mike Arnott, Kors Engineering
 */

public class BAbsTimeOffset extends BComponent{

 public void changed(Property property, Context context){
  super.changed(property, context);
  if(!Sys.atSteadyState() || !isRunning()){
   return;
  }

  if ((property == timeIn)||(property == secondsIn)){
   setTimeOut(getTimeIn().add(BRelTime.makeSeconds((int)getSecondsIn().getValue())));
   
  }
 }
 
 
 /**Absolute Time Input*/
 public final static Property timeIn = newProperty(Flags.SUMMARY, BAbsTime.DEFAULT);
 public void setTimeIn(BAbsTime v) { set(timeIn, v); }
 public BAbsTime getTimeIn() { return (BAbsTime)get(timeIn); }
 
 /**StatusNumeric value in representing time in seconds to offset*/
    public final static Property secondsIn = newProperty(Flags.SUMMARY,new BStatusNumeric());
    public BStatusNumeric getSecondsIn() { return (BStatusNumeric)get(secondsIn); }
    public void setSecondsIn(BStatusNumeric v) { set(secondsIn, v); }

    /**Absolute Time Out*/
    public final static Property timeOut = newProperty(Flags.SUMMARY, BAbsTime.DEFAULT);
    public BAbsTime getTimeOut() { return (BAbsTime)get(timeOut); }
    public void setTimeOut(BAbsTime v) { set(timeOut, v); }
    
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
    
    public static final Type TYPE = Sys.loadType(BAbsTimeOffset.class);
    public Type getType() { return TYPE; }


}
