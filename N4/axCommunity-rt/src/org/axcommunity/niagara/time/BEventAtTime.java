package org.axcommunity.niagara.time;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BTime;
import javax.baja.sys.BValue;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;
import javax.baja.units.BUnit;

/**
* A simple event scheduler with a variable input for the time of day
* input either numbers for HH, MM, SS or a string in the format HH:MM:SS
* whichever input changes last wins.
* The topic will fire when the time of day is >= the input time
* 
* @author Mike Arnott, Kors Engineering
* 
* 	Update 6/29/2017 by James Johnson to move to current logger syntax
*/
public class BEventAtTime extends BComponent
{
	Clock.Ticket ticket;
	public static BFacets showsec = BFacets.make(BFacets.SHOW_SECONDS,true);   
	public static BUnit mySecs = BUnit.getUnit("second");
	public static BUnit myMins = BUnit.getUnit("minute");
	public static BUnit myHours = BUnit.getUnit("hour");

	public void started()
	{
		//see if event time expired
		updateTimer();
	}

	public void changed(Property property, Context context)
	{
		super.changed(property, context);
		if(!Sys.atSteadyState() || !isRunning())
		{
			return;
		}


		if((property==secondsIn||property==minutesIn||property==hoursIn))
		{
			//numeric time setting changed
			try
			{
				BTime tmp = BTime.make((int)getHoursIn().getValue(),(int)getMinutesIn().getValue(),(int)getSecondsIn().getValue(),0);
				setEventTimeOut(tmp);
				setAdjustedEventTimeOut(tmp.add(getTimeOffset()));
				updateTimer();
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, "\n" + getSlotPath() + "\n" + e.toString() + "\n" + e.getMessage() + "\n" + e.getStackTrace());
			}
		}
		else if (property==timeStringIn)
		{
			try
			{
				BTime tmp = BTime.DEFAULT;
				tmp = (BTime)tmp.decodeFromString(getTimeStringIn().getValue() + ".000");        
				setEventTimeOut(tmp);
				setAdjustedEventTimeOut(tmp.add(getTimeOffset()));
				updateTimer();
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, "\n" + getSlotPath() + "\n" + e.toString() + "\n" + e.getMessage() + "\n" + e.getStackTrace());
			}
		}
		else if(property == timeOffset)    
		{
			setAdjustedEventTimeOut(getEventTimeOut().add(getTimeOffset()));
			updateTimer();
		}
	}
	

	void updateTimer()
	{
		if (ticket != null) ticket.cancel();
		BAbsTime now = BAbsTime.make();
		//create abs time based on current day, event time
		BAbsTime nextEvent = BAbsTime.make(BAbsTime.make(now.getYear(),now.getMonth(),now.getDay()),getAdjustedEventTimeOut());

		if(nextEvent.isAfter(now))
		{
			//event not ready to fire, clear flag and schedule next for today
			// nextEvent = nextEvent.add(getTimeOffset());
			
			setNextEventAbsTimeOut(nextEvent);
			ticket = Clock.schedule(this, nextEvent, timerExpired, null); 
		}
		else
		{
			//if event already fired, clear flag and set event to tomorrow
			//going to set an arbitrary threshold of 5 seconds.  
			//If the times are within one minute of each other, fire the event.  
			//Otherwise, schedule it for tomorrow
			if(java.lang.Math.abs(now.delta(nextEvent).getSeconds())<5)
			{
				fireTimeOfDayEvent(BBoolean.TRUE);
			}
			nextEvent = nextEvent.nextDay();
			// nextEvent = nextEvent.add(getTimeOffset());
			setNextEventAbsTimeOut(nextEvent);
			ticket = Clock.schedule(this, nextEvent, timerExpired, null); 
		}
	} 


	/**Slot for the <code>timerExpired</code> action. */
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);
	public void timerExpired() { invoke(timerExpired,null,null); }
	public void doTimerExpired() throws Exception 
	{
		//this is not being built to handle changes to the time that are less than the current time
		//if the event time is change to less than now, we do not fire the event, just set the fired flag to true
		updateTimer();
	}    

	//if the event has already been fired, schedule the next event for tomorrow and clear the flag

	public static final Property eventTimeOut = newProperty(Flags.HIDDEN + Flags.READONLY, BTime.make(12,0,0),showsec);
	public BTime getEventTimeOut(){ return (BTime) get(eventTimeOut);  }
	public void setEventTimeOut(BTime v) {set(eventTimeOut, v);  }
	
	public static final Property adjustedEventTimeOut = newProperty(Flags.HIDDEN + Flags.READONLY, BTime.make(12,0,0),showsec);
	public BTime getAdjustedEventTimeOut(){ return (BTime) get(adjustedEventTimeOut);  }
	public void setAdjustedEventTimeOut(BTime v) {set(adjustedEventTimeOut, v);  }


	/**Absolute Time Output for next event*/
	public final static Property nextEventAbsTimeOut= newProperty(Flags.SUMMARY + Flags.READONLY, BAbsTime.DEFAULT,showsec);
	public void setNextEventAbsTimeOut(BAbsTime v) { set(nextEventAbsTimeOut, v); }
	public BAbsTime getNextEventAbsTimeOut() { return (BAbsTime)get(nextEventAbsTimeOut); }

	/**Event fired when new time has passed*/
	public static final Topic timeOfDayEvent = newTopic(Flags.SUMMARY);
	public void fireTimeOfDayEvent(BBoolean event)
	{
		//only fire if enabled
		if(getEnabled().getValue())
		{
			fire(timeOfDayEvent,event,null);      
		}
	}

	public static final Property enabled = newProperty(Flags.SUMMARY, new BStatusBoolean(true));
	public BStatusBoolean getEnabled(){ return (BStatusBoolean) get(enabled);  }
	public void setEnabled(BStatusBoolean v) {set(enabled, v);  }


	/**StatusNumeric value In representing time in seconds.  Use the individual time numerics OR the Time String In*/
	public final static Property secondsIn = newProperty(Flags.SUMMARY,new BStatusNumeric(0),BFacets.makeNumeric(mySecs,0));
	public BStatusNumeric getSecondsIn() { return (BStatusNumeric)get(secondsIn); }
	public void setSecondsIn(BStatusNumeric v) { set(secondsIn, v); }

	/**StatusNumeric value In representing time in minutes.  Use the individual time numerics OR the Time String In*/
	public final static Property minutesIn = newProperty(Flags.SUMMARY,new BStatusNumeric(0),BFacets.makeNumeric(myMins,0));
	public BStatusNumeric getMinutesIn() { return (BStatusNumeric)get(minutesIn); }
	public void setMinutesIn(BStatusNumeric v) { set(minutesIn, v); }

	/**StatusNumeric value In representing time in hours.  Use the individual time numerics OR the Time String In*/
	public final static Property hoursIn = newProperty(Flags.SUMMARY,new BStatusNumeric(12),BFacets.makeNumeric(myHours,0));
	public BStatusNumeric getHoursIn() { return (BStatusNumeric)get(hoursIn); }
	public void setHoursIn(BStatusNumeric v) { set(hoursIn, v); }

	/**StatusString value In representing the event time in the format HH:MM:SS.  Use this or the individual numbers*/
	public static final Property timeStringIn = newProperty(Flags.SUMMARY, new BStatusString("12:00:00"));
	public BStatusString getTimeStringIn()  {    return (BStatusString) get(timeStringIn);  }
	public void setTimeStringIn(BStatusString v)  {    set(timeStringIn, v);  }

	/**RelTime offset that can be used to correct or adjust the time of the event (for, say, timezone issues)*/
	public static final Property timeOffset = newProperty(Flags.SUMMARY, BRelTime.make(0), null);
	public BRelTime getTimeOffset() { return (BRelTime)get(timeOffset); }
	public void setTimeOffset(BRelTime v) { set(timeOffset, v, null); }

	public static final Logger logger = Logger.getLogger("axCommunity.EventAtTime");
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BEventAtTime.class);
	public Type getType() { return TYPE; }
}
