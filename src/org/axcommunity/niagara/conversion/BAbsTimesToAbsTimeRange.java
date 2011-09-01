package org.axcommunity.niagara.conversion;

import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BAbsTimeRange;

/**
 * takes two abs time inputs, creates an abs time range output
 * @author Mike Arnott, Kors Engineering
 */
public class BAbsTimesToAbsTimeRange extends BComponent {
	public void changed(Property property, Context context){
		super.changed(property, context);
		if(!Sys.atSteadyState() || !isRunning()){
			return;
		}

		if ((property == timeInStart)||(property == timeInStop)){
			getTimeRangeOut().setStartTime(getTimeInStart());
			getTimeRangeOut().setEndTime(getTimeInStop());
		}
	}
	/**Absolute Time Range Output*/
	public final static Property timeRangeOut = newProperty(Flags.SUMMARY, new BAbsTimeRange());
	public void setTimeRangeOut(BAbsTimeRange v) { set(timeRangeOut, v); }
	public BAbsTimeRange getTimeRangeOut() { return (BAbsTimeRange)get(timeRangeOut); }
	
	/**Absolute Time Start Input*/
	public final static Property timeInStart = newProperty(Flags.SUMMARY, BAbsTime.DEFAULT);
	public void setTimeInStart(BAbsTime v) { set(timeInStart, v); }
	public BAbsTime getTimeInStart() { return (BAbsTime)get(timeInStart); }

	/**Absolute Time Stop Input*/
	public final static Property timeInStop = newProperty(Flags.SUMMARY, BAbsTime.DEFAULT);
	public void setTimeInStop(BAbsTime v) { set(timeInStop, v); }
	public BAbsTime getTimeInStop() { return (BAbsTime)get(timeInStop); }

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BAbsTimesToAbsTimeRange.class);
	public Type getType() { return TYPE; }


}
