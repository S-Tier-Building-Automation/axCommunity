package org.axcommunity.niagara.logic;

import javax.baja.log.Log;
import javax.baja.status.*;
import javax.baja.sys.*;


/**
 * When 'trigger' input or 'fire' action slot are true the value
 * set in the 'inTrue' slot will be represented in the 'out' slot
 * for the amount of time configured in the 'time' slot.
 * When the timer expires the value in the 'inFalse' slot 
 * will be represented in the 'out' slot.
 *
 *
 * @author		Justin Koffler, Texas Power Systems
 * @version		12.02.28
 */
 
 
public class BOneShotBooleanSelect extends BComponent
{
	private boolean last;
	private boolean fired;
	private boolean triggered;
	Clock.Ticket ticket;	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	ACTION SLOTS   ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Action fire = newAction(0,null);
	public void fire() { invoke(fire,null,null); }
	public void doFire()
	{
		fired = true;
		getOut().setValue(getInTrue().getValue());
		
		getOutTimerActive().setValue(true);
		setLastTrigger(BAbsTime.now());
		updateTimer();
	}
	
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);
	public void timerExpired() { invoke(timerExpired,null,null); }
	public void doTimerExpired()
	{
		getOut().setValue(getInFalse().getValue());
		getOutTimerActive().setValue(false);
		fired = false;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**	TRACKS THE TIMER	*//////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	void updateTimer()
	{            
		if (ticket != null) ticket.cancel();
		ticket = Clock.schedule(this, getTime(), timerExpired, null);
	}  
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	INPUTS   //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Input boolean trigger.*/
	public static final Property trigger = newProperty(0|Flags.SUMMARY, new BStatusBoolean(),null);
	public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger); }
	public void setTrigger(BStatusBoolean v) { set(trigger,v,null); }

	/** Time output should hold its triggered value*/
	public static final Property time = newProperty(0, BRelTime.make(500l),BFacets.make(BFacets.SHOW_MILLISECONDS, true));
	public BRelTime getTime() { return (BRelTime)get(time); }
	public void setTime(BRelTime v) { set(time,v,null); }
	
	/** When trigger input is true this is the boolean value that will set on the output.*/
	public final static Property inTrue = newProperty(0|Flags.SUMMARY, new BStatusBoolean(),null);
	public BStatusBoolean getInTrue() { return (BStatusBoolean)get(inTrue); }
	public void setInTrue(BStatusBoolean v) { set(inTrue, v); }
	
	/** When trigger input is false this is the boolean value that will set on the output.*/
	public final static Property inFalse = newProperty(0|Flags.SUMMARY, new BStatusBoolean(),null);
	public BStatusBoolean getInFalse() { return (BStatusBoolean)get(inFalse); }
	public void setInFalse(BStatusBoolean v) { set(inFalse, v); }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** When trigger input is true the value from slot 'in' will be represented in this slot. */
	public final static Property out = newProperty(0|Flags.SUMMARY|Flags.READONLY, new BStatusBoolean(),null);
	public BStatusBoolean getOut() { return (BStatusBoolean)get(out); }
	public void setOut(BStatusBoolean v) { set(out, v); }

	
	/** Time of last trigger input*/
	public static final Property lastTrigger = newProperty(0|Flags.READONLY, BAbsTime.make(), BFacets.make("showMilliseconds",true));
	public BAbsTime getLastTrigger() { return (BAbsTime)get(lastTrigger); }
	public void setLastTrigger(BAbsTime v) { set(lastTrigger, v); }
	
	/**STATUS BOOLEAN OUTPUT, TimerActive*/
	public final static Property outTimerActive = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getOutTimerActive() { return (BStatusBoolean)get(outTimerActive); }
	public void setOutTimerActive(BStatusBoolean v) { set(outTimerActive, v); }

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**    METHOD INVOKED WHEN ANY OF THE INPUTS CHANGES VALUES   *////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void changed(Property prop, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning())return;
		if(isRunning())
		{
			try
			{
				if (prop == trigger)
				{
					triggered = getTrigger().getValue();
					if(triggered && !last)
					{
						last = triggered;
						getOut().setValue(getInTrue().getValue());
						
						getOutTimerActive().setValue(true);
						setLastTrigger(BAbsTime.now());
						updateTimer();
					}
					else
					{
						last = triggered;
					}
				}
			}
			catch (Exception e) 
			{
				logger.error("\r\n\t\t" + getSlotPath()	+ "\r\n\t\t" + e.getMessage() + "\r\n\t\t" + e.getStackTrace());
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final Log logger = Log.getLog("axCommunity.OneShotBooleanSelect");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BOneShotBooleanSelect.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");
}


