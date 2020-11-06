/**
 * Used to determine peaks and Date/Time stamps of the peaks, set up to "zero out" monthly
 * @author vance.hensley
 * @creation May 28, 2009 
 */
package org.axcommunity.niagara.logic;

import javax.baja.status.*;
import javax.baja.sys.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BPeakValueAndTstamp extends BComponent
{
	/**
	* Slot for the <code>facets</code> property.
	* These facets are applied against all out properties.
	*/
	public static final Property facets = newProperty(0, BFacets.DEFAULT,null);
	public BFacets getFacets() { return (BFacets)get(facets); }
	public void setFacets(BFacets v) { set(facets,v,null); }
	
	/**
	 * Input being looked at for peaks
	 */
	public static final Property in = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);
	public BStatusNumeric getIn() { return (BStatusNumeric)get(in); }
	public void setIn(BStatusNumeric v) { set(in,v,null); }
	
	/**
	* hold value for comparing new input value versus the last peak
	*/
	public static final Property hold = newProperty(Flags.HIDDEN, new BStatusNumeric(),null);
	public BStatusNumeric getHold() { return (BStatusNumeric)get(hold); }
	public void setHold(BStatusNumeric v) { set(hold,v,null); }
	
	/**
	* output of the current peak.
	*/
	public static final Property out = newProperty(Flags.TRANSIENT|Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(),null);
	public BStatusNumeric getOut() { return (BStatusNumeric)get(out); }
	public void setOut(BStatusNumeric v) { set(out,v,null); }
	
	/**
	* time as a string.
	*/
	public static final Property time = newProperty(Flags.HIDDEN, new BStatusString(),null);
	public BStatusString getTime() { return (BStatusString)get(time); }
	public void setTime(BStatusString v) { set(time,v,null); }
	
	/**
	* time as a string.
	*/
	public static final Property date = newProperty(Flags.HIDDEN, new BStatusString(),null);
	public BStatusString getDate() { return (BStatusString)get(date); }
	public void setDate(BStatusString v) { set(date,v,null); }
	
	/**
	* DateTimestamp of the peak as a string.
	*/
	public static final Property outTstamp = newProperty(Flags.SUMMARY, new BStatusString(),null);
	public BStatusString getOutTstamp() { return (BStatusString)get(outTstamp); }
	public void setOutTstamp(BStatusString v) { set(outTstamp,v,null); }
	
	
	public static final Action clear = newAction(0,null);
	public void clear() { invoke(clear,null,null); }
	public void doClear()
	{
		getHold().setValue(0.0);
	}
	
	
	public void changed(Property p, Context cx)
	{
		if (!isRunning()) return;
		if (p == in)
		{
			calculate();
		}
	}
	
	public void calculate()
	{
		//SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		//SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		
		BAbsTime timeNow = BAbsTime.now();
		
		
		
		if (!getIn().getStatus().isValid()) return;
		
		if (getIn().getValue() >= getHold().getValue())
		{
			String formatTime = "hh:mm a";
			String formatDate = "dd MMM yyyy";
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(timeNow.getMillis());
			
			DateFormat timeFormat = new SimpleDateFormat( formatTime );
			DateFormat dateFormat = new SimpleDateFormat( formatDate );
			String timeString = timeFormat.format(cal.getTime());
			String dateString = dateFormat.format(cal.getTime());
			
			getOut().setValue(getIn().getValue());
			getTime().setValue(timeString);
			getDate().setValue(dateString);
			getOutTstamp().setValue(getTime().getValue() + " " + getDate().getValue());
			getHold().setValue(getIn().getValue());
		}
		
		
		//Date now = new Date();
		
		//if (now.getHours() == 0 && now.getMinutes() == 0 && now.getSeconds() <= 30 && now.getDay() == 01)
		if (timeNow.getHour() == 0 && timeNow.getMinute() == 0 && timeNow.getSecond() <= 30 && timeNow.getDay() == 01)
		{
			getHold().setValue(0.0);
		}
	}
	
	
	
	
	
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BPeakValueAndTstamp.class);
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/HBTechLogo.png");

}
