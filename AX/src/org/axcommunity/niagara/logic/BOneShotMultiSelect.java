package org.axcommunity.niagara.logic;

import javax.baja.log.Log;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BString;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;


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
 */
 
 
public class BOneShotMultiSelect extends BComponent
{
	private boolean last;
	private boolean triggered;
	Clock.Ticket ticket;	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	ACTION SLOTS   ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Action fire = newAction(0,null);
	public void fire() { invoke(fire,null,null); }
	public void doFire()
	{
		getOutTimerActive().setValue(true);
		
		getOutBool().setValue(		getInBoolTrue().getValue());
		getOutNum().setValue(		getInNumTrue().getValue());
		getOutString().setValue(	getInStringTrue().getValue());

		
		fireNewBoolValue(	BBoolean.make(	getOutBool().getValue()		));
		fireNewNumValue(	BDouble.make(	getOutNum().getValue()		));
		fireNewStringValue(	BString.make(	getOutString().getValue()	));
		
		fireTimerActiveBoolValue(	BBoolean.make(	getInBoolTrue().getValue()		));
		fireTimerActiveNumValue(	BDouble.make(	getInNumTrue().getValue()		));
		fireTimerActiveStringValue(	BString.make(	getInStringTrue().getValue()	));
		
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
		
		
		fireNewBoolValue(	BBoolean.make(	getOutBool().getValue()		));
		fireNewNumValue(	BDouble.make(	getOutNum().getValue()		));
		fireNewStringValue(	BString.make(	getOutString().getValue()	));
		
		fireTimerInactiveBoolValue(		BBoolean.make(	getInBoolFalse().getValue()		));
		fireTimerInactiveNumValue(		BDouble.make(	getInNumFalse().getValue()		));
		fireTimerInactiveStringValue(	BString.make(	getInStringFalse().getValue()	));
		
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

	

	/**When true the out values and topics will be updated whenever any of the in.. values change.*/
	public static final Property updateOutputsWithInputChanges = newProperty(0, new BStatusBoolean(false),null);
	public BStatusBoolean getUpdateOutputsWithInputChanges() { return (BStatusBoolean)get(updateOutputsWithInputChanges); }
	public void setUpdateOutputsWithInputChanges(BStatusBoolean v) { set(updateOutputsWithInputChanges,v,null); }
	
	
	/** Input boolean trigger.*/
	public static final Property trigger = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false),null);
	public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger); }
	public void setTrigger(BStatusBoolean v) { set(trigger,v,null); }

	/** Time output should hold its triggered value*/
	public static final Property time = newProperty(0, BRelTime.make(500l),BFacets.make(BFacets.SHOW_MILLISECONDS, true));
	public BRelTime getTime() { return (BRelTime)get(time); }
	public void setTime(BRelTime v) { set(time,v,null); }
	
	public final static Property inBoolTrue = newProperty(Flags.SUMMARY, new BStatusBoolean(true));
	public BStatusBoolean getInBoolTrue() { return (BStatusBoolean)get(inBoolTrue); }
	public void setInBoolTrue(BStatusBoolean v) { set(inBoolTrue, v); }
	
	public final static Property inBoolFalse = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getInBoolFalse() { return (BStatusBoolean)get(inBoolFalse); }
	public void setInBoolFalse(BStatusBoolean v) { set(inBoolFalse, v); }
	
	public static final Property inNumTrue  = newProperty(Flags.SUMMARY, new BStatusNumeric(0), BFacets.make(BFacets.PRECISION, BInteger.make(2), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getInNumTrue() {return (BStatusNumeric) get(inNumTrue); }
	public void setInNumTrue(BStatusNumeric v) {set(inNumTrue, v);}
	
	public static final Property inNumFalse  = newProperty(Flags.SUMMARY, new BStatusNumeric(0), BFacets.make(BFacets.PRECISION, BInteger.make(2), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getInNumFalse() {return (BStatusNumeric) get(inNumFalse); }
	public void setInNumFalse(BStatusNumeric v) {set(inNumFalse, v);}
	
	public static final Property inStringTrue = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getInStringTrue() { return (BStatusString)get(inStringTrue);}
	public void setInStringTrue(BStatusString v) {set(inStringTrue,v);}

	public static final Property inStringFalse = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getInStringFalse() { return (BStatusString)get(inStringFalse);}
	public void setInStringFalse(BStatusString v) {set(inStringFalse,v);}

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public final static Property outBool = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false));
	public BStatusBoolean getOutBool() { return (BStatusBoolean)get(outBool); }
	public void setOutBool(BStatusBoolean v) { set(outBool, v); }
	
	public static final Property outNum  = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(0), BFacets.make(BFacets.PRECISION, BInteger.make(2), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getOutNum() {return (BStatusNumeric) get(outNum); }
	public void setOutNum(BStatusNumeric v) {set(outNum, v);}
	
	public static final Property outString = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutString() { return (BStatusString)get(outString);}
	public void setOutString(BStatusString v) {set(outString,v);}
	
	/** Time of last trigger input*/
	public static final Property lastTrigger = newProperty(Flags.READONLY|Flags.DEFAULT_ON_CLONE, BAbsTime.make(), BFacets.make("showMilliseconds",true));
	public BAbsTime getLastTrigger() { return (BAbsTime)get(lastTrigger); }
	public void setLastTrigger(BAbsTime v) { set(lastTrigger, v); }
	
	/**STATUS BOOLEAN OUTPUT, TimerActive*/
	public final static Property outTimerActive = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false));
	public BStatusBoolean getOutTimerActive() { return (BStatusBoolean)get(outTimerActive); }
	public void setOutTimerActive(BStatusBoolean v) { set(outTimerActive, v); }
	
	
	
	/**Fired with the value from outBool*/
	public static final Topic NewBoolValue = newTopic(0);
	public void fireNewBoolValue(BBoolean event){fire(NewBoolValue,event,null);}
	
	/**Fired with the value from outNum.*/	
	public static final Topic NewNumValue = newTopic(0);
	public void fireNewNumValue(BDouble event){fire(NewNumValue,event,null);}
	
	/**Fired with the value from outString.*/	
	public static final Topic NewStringValue = newTopic(0);
	public void fireNewStringValue(BString event){fire(NewStringValue,event,null);}
	
	
	
	
	/**Fired with the value from inBoolTrue when the timer is active.*/
	public static final Topic TimerActiveBoolValue = newTopic(0);
	public void fireTimerActiveBoolValue(BBoolean event){fire(TimerActiveBoolValue,event,null);}
		
	/**Fired with the value from inBoolFalse when the timer is inactive.*/
	public static final Topic TimerInactiveBoolValue = newTopic(0);
	public void fireTimerInactiveBoolValue(BBoolean event){fire(TimerInactiveBoolValue,event,null);}
	
	
	
	/**Fired with the value from inNumTrue when the timer is active.*/
	public static final Topic TimerActiveNumValue = newTopic(0);
	public void fireTimerActiveNumValue(BDouble event){fire(TimerActiveNumValue,event,null);}
	
	/**Fired with the value from inNumFalse when the timer is inactive.*/
	public static final Topic TimerInactiveNumValue = newTopic(0);
	public void fireTimerInactiveNumValue(BDouble event){fire(TimerInactiveNumValue,event,null);}
	
	
	
	/**Fired with the value from inStringTrue when the timer is active.*/
	public static final Topic TimerActiveStringValue = newTopic(0);
	public void fireTimerActiveStringValue(BString event){fire(TimerActiveStringValue,event,null);}
	
	/**Fired with the value from inStringFalse when the timer is inactive.*/
	public static final Topic TimerInactiveStringValue = newTopic(0);
	public void fireTimerInactiveStringValue(BString event){fire(TimerInactiveStringValue,event,null);}
	
	
	
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
				if (getUpdateOutputsWithInputChanges().getValue()==true)
				{
					if (prop == inBoolTrue)
					{
						if( getOutTimerActive().getValue()==true && getOutBool().getValue()!=getInBoolTrue().getValue())
						{
							getOutBool().setValue( getInBoolTrue().getValue() );
							fireNewBoolValue( BBoolean.make( getOutBool().getValue() ));
							fireTimerActiveBoolValue( BBoolean.make( getInBoolTrue().getValue() ));
						}
					}
					
					if (prop == inBoolFalse)
					{
						if( getOutTimerActive().getValue()==false && getOutBool().getValue()!=getInBoolFalse().getValue())
						{
							getOutBool().setValue( getInBoolFalse().getValue() );
							fireNewBoolValue( BBoolean.make( getOutBool().getValue() ));
							fireTimerInactiveBoolValue( BBoolean.make( getInBoolFalse().getValue() ));
						}
					}
					
					
					if (prop == inNumTrue)
					{
						if( getOutTimerActive().getValue()==true && Double.compare(getOutNum().getValue(), getInNumTrue().getValue())!=0 )
						{
							getOutNum().setValue( getInNumTrue().getValue() );
							fireNewNumValue( BDouble.make( getOutNum().getValue() ));
							fireTimerActiveNumValue( BDouble.make( getInNumTrue().getValue() ));
							
						}
					}
					
					if (prop == inNumFalse)
					{
						if( getOutTimerActive().getValue()==false && getOutNum().getValue()!=getInNumFalse().getValue())
						{
							getOutNum().setValue( getInNumFalse().getValue() );
							fireNewNumValue( BDouble.make( getOutNum().getValue() ));
							fireTimerInactiveNumValue( BDouble.make( getInNumFalse().getValue() ));
						}
					}
					
					
					
					if (prop == inStringTrue)
					{
						if( getOutTimerActive().getValue()==true && !getOutString().getValue().equals(getInStringTrue().getValue()))
						{
							getOutString().setValue( getInStringTrue().getValue() );
							fireNewStringValue( BString.make( getOutString().getValue() ));
							fireTimerActiveStringValue( BString.make( getInStringTrue().getValue() ));
						}
					}
					
					if (prop == inStringFalse)
					{
						if( getOutTimerActive().getValue()==false && !getOutString().getValue().equals(getInStringFalse().getValue()))
						{
							getOutString().setValue( getInStringFalse().getValue() );
							fireNewStringValue( BString.make( getOutString().getValue() ));
							fireTimerInactiveStringValue( BString.make( getInStringFalse().getValue() ));
						}
					}
				}
				
				
				
				if (prop == trigger)
				{
					triggered = getTrigger().getValue();
					if(triggered && !last)
					{
						last = triggered;
						getOutTimerActive().setValue(true);
						
						getOutBool().setValue(		getInBoolTrue().getValue()		);
						getOutNum().setValue(		getInNumTrue().getValue()		);
						getOutString().setValue(	getInStringTrue().getValue()	);

						fireNewBoolValue(	BBoolean.make(	getOutBool().getValue()		));
						fireNewNumValue(	BDouble.make(	getOutNum().getValue()		));
						fireNewStringValue(	BString.make(	getOutString().getValue()	));
						
						fireTimerActiveBoolValue(	BBoolean.make(	getInBoolTrue().getValue()		));
						fireTimerActiveNumValue(	BDouble.make(	getInNumTrue().getValue()		));
						fireTimerActiveStringValue(	BString.make(	getInStringTrue().getValue()	));
						
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
				logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final Log logger = Log.getLog("axCommunity.OneShotBooleanSelect");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BOneShotMultiSelect.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");
	}