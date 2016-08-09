package org.axcommunity.niagara.conversion;

import javax.baja.status.BStatusNumeric;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.units.BUnit;
import javax.baja.sys.*;
import javax.baja.timezone.*;
/**
 * Converts numeric inputs into abs time.
 * @author Mike Arnott, Kors Engineering
*/
public class BDatePartsToAbsTime extends BComponent {

	   public void changed(Property property, Context context){
	        super.changed(property, context);
	       	if(!Sys.atSteadyState() || !isRunning()){
	    		return;
	    	}
	       	
	       	
	       	if((property.getName().indexOf("In")>0)){
		        int year = (int)getYearIn().getValue();
		        int month = 0;
		        if(getMonthIn().getValue()>0){
		        	month = (int)getMonthIn().getValue() - 1;
		        }
		        BMonth bmonth = BMonth.make(month);
		        int day = (int)getDayIn().getValue();
		        int hour = (int)getHoursIn().getValue();
		        int min = (int)getMinutesIn().getValue();
		        int sec = (int)getSecondsIn().getValue();
		        int millis = 0;
		        BTimeZone timeZone = BTimeZone.getLocal();
		        
		        
		        
		        BAbsTime x = BAbsTime.make( year,  bmonth,  day,  hour,  min,  sec,  millis,  timeZone);
		        
		        setTimeOut(x);
	       	}
	   }
	
    /**Absolute Time Output*/
    public final static Property timeOut= newProperty(Flags.SUMMARY, BAbsTime.DEFAULT);
    public void setTimeOut(BAbsTime v) { set(timeOut, v); }
    public BAbsTime getTimeOut() { return (BAbsTime)get(timeOut); }

	
    public static BUnit mySecs = BUnit.getUnit("second");
    public static BUnit myMins = BUnit.getUnit("minute");
    public static BUnit myHours = BUnit.getUnit("hour");
    
    /**StatusNumeric value In representing time in seconds*/
    public final static Property secondsIn = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(mySecs,0));
    public BStatusNumeric getSecondsIn() { return (BStatusNumeric)get(secondsIn); }
    public void setSecondsIn(BStatusNumeric v) { set(secondsIn, v); }

    /**StatusNumeric value In representing time in minutes*/
    public final static Property minutesIn = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(myMins,0));
    public BStatusNumeric getMinutesIn() { return (BStatusNumeric)get(minutesIn); }
    public void setMinutesIn(BStatusNumeric v) { set(minutesIn, v); }
    
    /**StatusNumeric value In representing time in hours*/
    public final static Property hoursIn = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(myHours,0));
    public BStatusNumeric getHoursIn() { return (BStatusNumeric)get(hoursIn); }
    public void setHoursIn(BStatusNumeric v) { set(hoursIn, v); }
    
    /**StatusNumeric value In representing day of month*/
    public final static Property dayIn = newProperty(Flags.SUMMARY,new BStatusNumeric(1),BFacets.makeNumeric(0));
    public BStatusNumeric getDayIn() { return (BStatusNumeric)get(dayIn); }
    public void setDayIn(BStatusNumeric v) { set(dayIn, v); }

    /**StatusNumeric value In representing month*/
    public final static Property monthIn = newProperty(Flags.SUMMARY,new BStatusNumeric(1),BFacets.makeNumeric(0));
    public BStatusNumeric getMonthIn() { return (BStatusNumeric)get(monthIn); }
    public void setMonthIn(BStatusNumeric v) { set(monthIn, v); }

    /**StatusNumeric value In representing year*/
    public final static Property yearIn = newProperty(Flags.SUMMARY,new BStatusNumeric(2000),BFacets.makeNumeric(0));
    public BStatusNumeric getYearIn() { return (BStatusNumeric)get(yearIn); }
    public void setYearIn(BStatusNumeric v) { set(yearIn, v); }

    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
    
    public static final Type TYPE = Sys.loadType(BDatePartsToAbsTime.class);
    public Type getType() { return TYPE; }
 
}
