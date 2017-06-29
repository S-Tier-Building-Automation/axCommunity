package org.axcommunity.niagara.logic;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.baja.status.*;
import javax.baja.sys.*;


/**
 * When 'trigger' input or 'fire' action slot are true the value
 * set in the 'inXTrue' slot will be represented in the 'out' slot
 * for the amount of time configured in the 'time' slot.
 * When the timer expires the value in the 'inXFalse' slot 
 * will be represented in the 'out' slot.
 *
 *
 * @author		Justin Koffler, Texas Power Systems
 * @version		12.02.28
 * 
 * 	Update 6/29/2017 by James Johnson to move to current logger syntax
 */
 
 
public class BOneShotMultiSelect extends BComponent
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
		getOutBool().setValue(		getInBoolTrue().getValue());
		getOutNum().setValue(		getInNumTrue().getValue());
		getOutString().setValue(	getInStringTrue().getValue());

		getOutTimerActive().setValue(true);
		setLastTrigger(BAbsTime.now());
		updateTimer();
	}
	
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);
	public void timerExpired() { invoke(timerExpired,null,null); }
	public void doTimerExpired()
	{
		getOutBool().setValue(		getInBoolFalse().getValue());
		getOutNum().setValue(		getInNumFalse().getValue());
		getOutString().setValue(	getInStringFalse().getValue());
		
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
	
	public final static Property inBoolTrue = newProperty(0|Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getInBoolTrue() { return (BStatusBoolean)get(inBoolTrue); }
	public void setInBoolTrue(BStatusBoolean v) { set(inBoolTrue, v); }
	
	public final static Property inBoolFalse = newProperty(0|Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getInBoolFalse() { return (BStatusBoolean)get(inBoolFalse); }
	public void setInBoolFalse(BStatusBoolean v) { set(inBoolFalse, v); }
	
	public static final Property inNumTrue  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getInNumTrue() {return (BStatusNumeric) get(inNumTrue); }
	public void setInNumTrue(BStatusNumeric v) {set(inNumTrue, v);}
	
	public static final Property inNumFalse  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getInNumFalse() {return (BStatusNumeric) get(inNumFalse); }
	public void setInNumFalse(BStatusNumeric v) {set(inNumFalse, v);}
	
	public static final Property inStringTrue = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getInStringTrue() { return (BStatusString)get(inStringTrue);}
	public void setInStringTrue(BStatusString v) {set(inStringTrue,v);}

	public static final Property inStringFalse = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getInStringFalse() { return (BStatusString)get(inStringFalse);}
	public void setInStringFalse(BStatusString v) {set(inStringFalse,v);}

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public final static Property outBool = newProperty(0|Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getOutBool() { return (BStatusBoolean)get(outBool); }
	public void setOutBool(BStatusBoolean v) { set(outBool, v); }
	
	public static final Property outNum  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutNum() {return (BStatusNumeric) get(outNum); }
	public void setOutNum(BStatusNumeric v) {set(outNum, v);}
	
	public static final Property outString = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getOutString() { return (BStatusString)get(outString);}
	public void setOutString(BStatusString v) {set(outString,v);}
	
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
						getOutBool().setValue(		getInBoolTrue().getValue());
						getOutNum().setValue(		getInNumTrue().getValue());
						getOutString().setValue(	getInStringTrue().getValue());

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
				logger.log(Level.SEVERE, "\r\n\t\t" + getSlotPath()	+ "\r\n\t\t" + e.getMessage() + "\r\n\t\t" + e.getStackTrace());
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final Logger logger = Logger.getLogger("axCommunity.OneShotBooleanSelect");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BOneShotMultiSelect.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");
}


