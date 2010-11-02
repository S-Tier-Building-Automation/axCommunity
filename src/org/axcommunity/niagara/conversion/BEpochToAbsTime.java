package org.axcommunity.niagara.conversion;

import java.util.Calendar;

import javax.baja.status.BStatusNumeric;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public class BEpochToAbsTime extends BComponent 
{
  public void changed(Property property, Context context){
    super.changed(property, context);
    if(!Sys.atSteadyState() || !isRunning()){
    return;
    }
    if(property==epochIn){
      if(getEpochIn().getValue()>0){
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis((long)getEpochIn().getValue());
        setTimeOut(BAbsTime.make((long)getEpochIn().getValue()));
      }
    }
    
  }
  /**Absolute Time Output*/
  public final static Property timeOut= newProperty(Flags.SUMMARY, BAbsTime.DEFAULT);
  public void setTimeOut(BAbsTime v) { set(timeOut, v); }
  public BAbsTime getTimeOut() { return (BAbsTime)get(timeOut); }
  
  /**StatusNumeric value In representing time in milliseconds since Epoch*/
  public final static Property epochIn = newProperty(Flags.SUMMARY,new BStatusNumeric());
  public BStatusNumeric getEpochIn() { return (BStatusNumeric)get(epochIn); }
  public void setEpochIn(BStatusNumeric v) { set(epochIn, v); }

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
  
  public static final Type TYPE = Sys.loadType(BEpochToAbsTime.class);
  public Type getType() { return TYPE; }

}
