package org.axcommunity.niagara.time;

import javax.baja.sys.*; 
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.BRelTime;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.log.Log;

/**
* Calculates the time from when a "start" input is receive until a "stop" input is received.
* The "start" and "stop" inputs can be configured to be triggered only on the rising edge
* or require them to remain true.
*
* Several output options are available. 
*
* Running totals of elapse time and lap time are given in 
* msec, sec, min and hr as StatusNumerics
*
* Segmented time values of elapse time and lap time are also given in
* msec, sec, min and hr as StatusNumerics
*
* @author   Justin Koffler
* @creation	July 15, 2012
*/


public class BStopwatch_v2 extends BComponent
{
	private String	strElapseHr		= "0", strElapseMin	= "0", strElapseSec	= "0", strElapseMs	= "0";
	private String	strLapHr		= "0", strLapMin	= "0", strLapSec	= "0", strLapMs		= "0";
	private long	startTime		= -1;
	private long	stopTime		= -1;
	private long	lapStartTime	= -1;
	private int		lap				= 0;
	private boolean	running, allowOneShot;
	private int		elapseHours, elapseMinutes, elapseSeconds, elapseMilliseconds;
	private int		lapHours, lapMinutes, lapSeconds, lapMilliseconds;
	private int		elapseHr, elapseMin, elapseSec, elapseMs;
	private int		lapHr, lapMin, lapSec, lapMs;
	private long	lapTime, elapseTime, tempElapse, tempLap, lastLap;
	
	

	//*********************************************************************************************************
	// USER CONTROLS ******************************************************************************************
	//*********************************************************************************************************

	/** IF VALUE EQUALS FALSE THE TIMER WILL NOT RUN.*/
	public final static Property inEnable = newProperty(0, new BStatusBoolean(true));
	public BStatusBoolean getInEnable() { return (BStatusBoolean)get(inEnable); }
	public void setInEnable(BStatusBoolean v) { set(inEnable, v); }
	
	/** FREQUENCY THE CLOCK VALUES WILL BE UPDATED. */
	public static final Property inUpdateRate = newProperty(0, BRelTime.make(500l),BFacets.make(BFacets.SHOW_MILLISECONDS, true));
	public BRelTime getInUpdateRate() { return (BRelTime)get(inUpdateRate); }
	public void setInUpdateRate(BRelTime v) { set(inUpdateRate, v); }

	/** DEFINES IF START AND STOP CAN BE TRIGGERED FROM A ONESHOT AND NOT HAVE TO REMAIN TRUE (DEFAULT TRUE).*/
	public final static Property inTriggerOnOneshot = newProperty(0, new BStatusBoolean(true));
	public BStatusBoolean getInTriggerOnOneshot() { return (BStatusBoolean)get(inTriggerOnOneshot); }
	public void setInTriggerOnOneshot(BStatusBoolean v) { set(inTriggerOnOneshot, v); }

	/** STARTS THE TIMER (IF 'inTriggerOnOneshot' IS FALSE THIS INPUT MUST REMAIN TRUE TO KEEP THE TIMER GOING).*/
	public final static Property inStart = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getInStart() { return (BStatusBoolean)get(inStart); }
	public void setInStart(BStatusBoolean v) { set(inStart, v); }

	/** STOPS THE TIMER (IF 'inTriggerOnOneshot' IS FALSE THEN THIS INPUT MUST BE FALSE BEFORE ANOTHER START CAN BE TRIGGERED).*/
	public final static Property inStop = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getInStop() { return (BStatusBoolean)get(inStop); }
	public void setInStop(BStatusBoolean v) { set(inStop, v); }

	/** WILL RESET ALL THE TIMER OUTPUTS.*/
	public final static Property inReset = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getInReset() { return (BStatusBoolean)get(inReset); }
	public void setInReset(BStatusBoolean v) { set(inReset, v); }

	/** INDICATES IF THE TIMER IS ACTIVELY RUNNING.*/
	public final static Property outRunning = newProperty(0|Flags.SUMMARY|Flags.READONLY, new BStatusBoolean(false));
	public BStatusBoolean getOutRunning() { return (BStatusBoolean)get(outRunning); }
	public void setOutRunning(BStatusBoolean v) { set(outRunning, v); }
	
	
	//*********************************************************************************************************
	// ACTION SLOTS *******************************************************************************************
	//*********************************************************************************************************
	
	/** WILL RESET ALL THE TIMER OUTPUTS.*/////////////////////////////////////////////////////////////////////
	public static final Action Reset = newAction(0);
	public void Reset() {invoke(Reset, null);}
	public void doReset()
	{
		try
		{
			logger.trace("\t\t" + getSlotPath()	+ "\t\t******** doReset() Method Called ********");
			
			if (updateTicket != null) updateTicket.cancel();
			
			running = false;
			getOutRunning().setValue(false);

			// ELAPSE -----------------------
			elapseHours			= 0;
			elapseMinutes		= 0;
			elapseSeconds		= 0;
			elapseMilliseconds	= 0;

			elapseHr			= 0;
			elapseMin			= 0;
			elapseSec			= 0;
			elapseMs			= 0;

			strElapseHr			= "0";
			strElapseMin		= "0";
			strElapseSec		= "0";
			strElapseMs			= "0";
			
			// LAP --------------------------
			lapHours			= 0;
			lapMinutes			= 0;
			lapSeconds			= 0;
			lapMilliseconds		= 0;

			lapHr				= 0;
			lapMin				= 0;
			lapSec				= 0;
			lapMs				= 0;

			strLapHr			= "0";
			strLapMin			= "0";
			strLapSec			= "0";
			strLapMs			= "0";

			// MISC --------------------------
			startTime			= -1;
			stopTime			= -1;
			lapStartTime		= -1;
			lap					= 0;
			
			elapseTime			= 0;
			lapTime				= 0;
			tempElapse			= 0;
			tempLap				= 0;
			lastLap				= 0;
			
			// ELAPSE _____________________________________________________________________________________________
			getOutElapseTimeInMilliseconds().setValue(0);
			getOutElapseTimeInSeconds().setValue(0);
			getOutElapseTimeInMinutes().setValue(0);
			getOutElapseTimeInHours().setValue(0);

			getOutElapseMilliseconds().setValue(0);
			getOutElapseSeconds().setValue(0);
			getOutElapseMinutes().setValue(0);
			getOutElapseHours().setValue(0);
			
			// getOutElapseTimeString().setValue("0hrs 0mins 0sec");
			getOutElapseTimeString().setValue(strElapseHr	+ "hrs " + strElapseMin	+ "mins " + strElapseSec	+ "." + strElapseMs	+ "sec");
			
			
			// LAP ________________________________________________________________________________________________
			getOutLap().setValue(0);
			
			getOutLapTimeInMilliseconds().setValue(0);
			getOutLapTimeInSeconds().setValue(0);
			getOutLapTimeInMinutes().setValue(0);
			getOutLapTimeInHours().setValue(0);
			
			getOutLapMilliseconds().setValue(0);
			getOutLapSeconds().setValue(0);
			getOutLapMinutes().setValue(0);
			getOutLapHours().setValue(0);

			getOutLapTimeString().setValue(strLapHr			+ "hrs " + strLapMin	+ "mins " + strLapSec		+ "." + strLapMs	+ "sec");
			
			
			// MISC _______________________________________________________________________________________________
			getOutRunning().setValue(false);

			setOutTimeStarted(BAbsTime.make(0));
			setOutTimeStopped(BAbsTime.make(0));

			setOutElapseTime(BRelTime.make(0));
			setOutLapTime(BRelTime.make(0));
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
		}
	}
	
	/** STARTS THE TIMER.*/////////////////////////////////////////////////////////////////////////////////////
	public static final Action Start = newAction(Flags.SUMMARY|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void Start(){invoke(Start,null,null);}
	public void doStart()
	{
		try
		{
			logger.trace("\t\t" + getSlotPath()	+ "\t\t******** doStart() [start of method] ********");

			if(getInTriggerOnOneshot().getValue()==false && getInStop().getValue()==true)
			{
				return;
			}
			
			//Start the timer when start input is true and timer not already running.
			if (!running)
			{
				logger.trace("\r\n\t\t" + getSlotPath()	+ "\t\t******** doStart() Method and is NOT running. ********"
										+ "\r\n\t\t running = " + running
										+ "\r\n\t\t lap =     " + lap
										+ "\r\n\t\t outLap =  " + getOutLap().getValue()
										+ "\r\n\t\t" );
				
				lap = (int) getOutLap().getValue()+1;
				getOutLap().setValue(lap);
				//If just continuing a previously started timer session...
				if (lap > 1)
				{
					// startTime = (startTime - stopTime) + System.currentTimeMillis();
					lapStartTime	= System.currentTimeMillis();
				}
				else //...otherwise start a new timer session.
				{
					// doReset();
					// lap = (int) getOutLap().getValue()+1;
					getOutLap().setValue(lap);
					startTime		= System.currentTimeMillis();
					lapStartTime	= startTime;
				}
				
				
				
				running = true;
				getOutRunning().setValue(true);
				setOutTimeStarted(BAbsTime.now());
				
				logger.trace("\r\n\t\t" + getSlotPath()	+ "\t\t******** doStart() [end of method] ********"
										+ "\r\n\t\t running = " + running
										+ "\r\n\t\t lap =     " + lap
										+ "\r\n\t\t outLap =  " + getOutLap().getValue()
										+ "\r\n\t\t" );
				
				
				
				
				setTime();
			}
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
		}
	}
	
	
	/** STOPS THE TIMER.*//////////////////////////////////////////////////////////////////////////////////////
	public static final Action Stop = newAction(Flags.SUMMARY|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void Stop(){invoke(Stop,null,null);}
	public void doStop()
	{
		try
		{
			logger.trace("\t\t" + getSlotPath()	+ "\t\t******** doStop() Method Called ********");
			if (running) 
			{
				running		= false;
				stopTime	= System.currentTimeMillis();
				
				getOutRunning().setValue(false);
				setOutTimeStopped(BAbsTime.now());
				
				setTime();
				// setLapTime();
			}
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
		}
	}
	
	
	
	//*********************************************************************************************************
	// TIME FORMATS *******************************************************************************************
	//*********************************************************************************************************

	/**AbsTime VALUE OUT REPRESENTING TIME STARTED.*/
	public static final Property outTimeStarted = newProperty(0|Flags.READONLY, BAbsTime.make(), BFacets.make("showSeconds",true));
	public BAbsTime getOutTimeStarted() { return (BAbsTime)get(outTimeStarted); }
	public void setOutTimeStarted(BAbsTime v) { set(outTimeStarted, v); }

	/**AbsTime VALUE OUT REPRESENTING TIME STOPPED.*/
	public static final Property outTimeStopped = newProperty(0|Flags.READONLY, BAbsTime.make(), BFacets.make("showSeconds",true));
	public BAbsTime getOutTimeStopped() { return (BAbsTime)get(outTimeStopped); }
	public void setOutTimeStopped(BAbsTime v) { set(outTimeStopped, v); }

	/**RelTime VALUE OUT REPRESENTING ELAPSE TIME.*/
	public final static Property outElapseTime = newProperty(Flags.SUMMARY + Flags.READONLY, BRelTime.DEFAULT);
	public BRelTime getOutElapseTime() { return (BRelTime)get(outElapseTime); }
	public void setOutElapseTime(BRelTime v) { set(outElapseTime, v); }
	
	/**RelTime VALUE OUT REPRESENTING LAP ELAPSE TIME.*/
	public final static Property outLapTime = newProperty(Flags.SUMMARY + Flags.READONLY, BRelTime.DEFAULT);
	public BRelTime getOutLapTime() { return (BRelTime)get(outLapTime); }
	public void setOutLapTime(BRelTime v) { set(outLapTime, v); }





	//*********************************************************************************************************
	// ELAPSE TIME IN VARIOUS FORMATS *************************************************************************
	//*********************************************************************************************************

	/**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN MILLISECONDS.*/
	public static final Property outElapseTimeInMilliseconds = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutElapseTimeInMilliseconds() { return (BStatusNumeric)get(outElapseTimeInMilliseconds);}
	public void setOutElapseTimeInMilliseconds(BStatusNumeric v) {set(outElapseTimeInMilliseconds,v);}

	/**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN SECONDS.*/
	public static final Property outElapseTimeInSeconds = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutElapseTimeInSeconds() { return (BStatusNumeric)get(outElapseTimeInSeconds);}
	public void setOutElapseTimeInSeconds(BStatusNumeric v) {set(outElapseTimeInSeconds,v);}

	/**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN MINUTES.*/
	public static final Property outElapseTimeInMinutes = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutElapseTimeInMinutes() { return (BStatusNumeric)get(outElapseTimeInMinutes);}
	public void setOutElapseTimeInMinutes(BStatusNumeric v) {set(outElapseTimeInMinutes,v);}

	/**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN HOURS.*/
	public static final Property outElapseTimeInHours = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutElapseTimeInHours() { return (BStatusNumeric)get(outElapseTimeInHours);}
	public void setOutElapseTimeInHours(BStatusNumeric v) {set(outElapseTimeInHours,v);}

	/**StatusString VALUE OUT REPRESENTING ELAPSE TIME IN HOURS, MINUTES AND SECONDS.*/
	public static final Property outElapseTimeString = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusString());
	public BStatusString getOutElapseTimeString() { return (BStatusString)get(outElapseTimeString);}
	public void setOutElapseTimeString(BStatusString v) {set(outElapseTimeString,v);}
	
	
	
	//*********************************************************************************************************
	// INDIVIDUAL NUMERIC ELAPSE TIME VALUES *************************************************************************
	//*********************************************************************************************************

	/**StatusNumeric VALUE OUT REPRESENTING MILLISOCONDS PORTION OF ELASPSE TIME.*/
	public static final Property outElapseMilliseconds = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutElapseMilliseconds() { return (BStatusNumeric)get(outElapseMilliseconds);}
	public void setOutElapseMilliseconds(BStatusNumeric v) {set(outElapseMilliseconds,v);}

	/**StatusNumeric VALUE OUT REPRESENTING SECONDS PORTION OF ELASPSE TIME.*/
	public static final Property outElapseSeconds = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutElapseSeconds() { return (BStatusNumeric)get(outElapseSeconds);}
	public void setOutElapseSeconds(BStatusNumeric v) {set(outElapseSeconds,v);}

	/**StatusNumeric VALUE OUT REPRESENTING MINUTES PORTION OF ELASPSE TIME.*/
	public static final Property outElapseMinutes = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutElapseMinutes() { return (BStatusNumeric)get(outElapseMinutes);}
	public void setOutElapseMinutes(BStatusNumeric v) {set(outElapseMinutes,v);}

	/**StatusNumeric VALUE OUT REPRESENTING HOURS PORTION OF ELASPSE TIME.*/
	public static final Property outElapseHours = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutElapseHours() { return (BStatusNumeric)get(outElapseHours);}
	public void setOutElapseHours(BStatusNumeric v) {set(outElapseHours,v);}
	
	
	
	
	//*********************************************************************************************************
	// LAP TIME IN VARIOUS FORMATS *************************************************************************
	//*********************************************************************************************************

	/**StatusNumeric VALUE OUT REPRESENTING THE CURRENT LAP.*/
	public static final Property outLap  = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLap() {return (BStatusNumeric) get(outLap); }
	public void setOutLap(BStatusNumeric v) {set(outLap, v);}
	
	/**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN MILLISECONDS.*/
	public static final Property outLapTimeInMilliseconds = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLapTimeInMilliseconds() { return (BStatusNumeric)get(outLapTimeInMilliseconds);}
	public void setOutLapTimeInMilliseconds(BStatusNumeric v) {set(outLapTimeInMilliseconds,v);}

	/**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN SECONDS.*/
	public static final Property outLapTimeInSeconds = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLapTimeInSeconds() { return (BStatusNumeric)get(outLapTimeInSeconds);}
	public void setOutLapTimeInSeconds(BStatusNumeric v) {set(outLapTimeInSeconds,v);}

	/**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN MINUTES.*/
	public static final Property outLapTimeInMinutes = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLapTimeInMinutes() { return (BStatusNumeric)get(outLapTimeInMinutes);}
	public void setOutLapTimeInMinutes(BStatusNumeric v) {set(outLapTimeInMinutes,v);}

	/**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN HOURS.*/
	public static final Property outLapTimeInHours = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLapTimeInHours() { return (BStatusNumeric)get(outLapTimeInHours);}
	public void setOutLapTimeInHours(BStatusNumeric v) {set(outLapTimeInHours,v);}

	/**StatusString VALUE OUT REPRESENTING ELAPSE TIME IN HOURS, MINUTES AND SECONDS.*/
	public static final Property outLapTimeString = newProperty(Flags.SUMMARY|Flags.READONLY, new BStatusString());
	public BStatusString getOutLapTimeString() { return (BStatusString)get(outLapTimeString);}
	public void setOutLapTimeString(BStatusString v) {set(outLapTimeString,v);}


	
	
	
	
	//*********************************************************************************************************
	// INDIVIDUAL NUMERIC LAP TIME VALUES *********************************************************************
	//*********************************************************************************************************

	/**StatusNumeric VALUE OUT REPRESENTING MILLISOCONDS PORTION OF ELASPSE TIME.*/
	public static final Property outLapMilliseconds = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLapMilliseconds() { return (BStatusNumeric)get(outLapMilliseconds);}
	public void setOutLapMilliseconds(BStatusNumeric v) {set(outLapMilliseconds,v);}

	/**StatusNumeric VALUE OUT REPRESENTING SECONDS PORTION OF ELASPSE TIME.*/
	public static final Property outLapSeconds = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLapSeconds() { return (BStatusNumeric)get(outLapSeconds);}
	public void setOutLapSeconds(BStatusNumeric v) {set(outLapSeconds,v);}

	/**StatusNumeric VALUE OUT REPRESENTING MINUTES PORTION OF ELASPSE TIME.*/
	public static final Property outLapMinutes = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLapMinutes() { return (BStatusNumeric)get(outLapMinutes);}
	public void setOutLapMinutes(BStatusNumeric v) {set(outLapMinutes,v);}

	/**StatusNumeric VALUE OUT REPRESENTING HOURS PORTION OF ELASPSE TIME.*/
	public static final Property outLapHours = newProperty(0|Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutLapHours() { return (BStatusNumeric)get(outLapHours);}
	public void setOutLapHours(BStatusNumeric v) {set(outLapHours,v);}

	

	//*********************************************************************************************************
	// REFRESHES VALUES AT RATE DEFINED BY inUpdateRate *******************************************************
	//*********************************************************************************************************
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);
	public void timerExpired() {invoke(timerExpired,null,null);}
	public void doTimerExpired() throws Exception 
	{
		if(getInEnable().getValue()==true)
		{
			setTime();
		}
		else
		{
			doStop();
		}
	}
	
	Clock.Ticket updateTicket;
	void updateTimer()
	{            
		try
		{
			if(running)
			{
				
				if (updateTicket != null) updateTicket.cancel();
				updateTicket = Clock.schedulePeriodically(this, getInUpdateRate(), timerExpired, null);
			}
			else
			{
				updateTicket.cancel();
			}
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
		}
	} 



	//*********************************************************************************************************
	// CODE EXECUTED UPON VALUE CHANGE OF A SLOT **************************************************************
	//*********************************************************************************************************
	public void changed(Property p, Context context)
	{
		try
		{
			if(!Sys.atSteadyState() || !isRunning())
			{
				return;
			}
			
			allowOneShot	= getInTriggerOnOneshot().getValue();
			// running			= getOutRunning().getValue();
				
				

			//*****************************************************************************************************
			if (p == inEnable) 
			{
				if(getInEnable().getValue()==false)
				{
					doStop();
				}
				return;
			}
			
			//*****************************************************************************************************
			if (p == inTriggerOnOneshot) 
			{
				return;
			}

			//*****************************************************************************************************
			if (p == inStart) 
			{
				if(getInStart().getValue()==false) 
				{
					if(!allowOneShot)
					{
						//Stop timer when the start input goes false and the allow oneshot trigger bool is false.
						if (running)
						{
							doStop();
						}
					}
				}
				else if(getInStart().getValue()==true && getInEnable().getValue()==true)
				{
					//Do nothing if already running or the stop input is true when the allow oneshot trigger bool is false
					if(running || (!allowOneShot && getInStop().getValue()==true))
					{
						return;
					}
					else
					{
						//Ok to start the timer.
						doStart();
					}
				}
			}

			//*****************************************************************************************************
			if (p == inStop) 
			{
				if(getInStop().getValue()==false)
				{
					if(allowOneShot)
					{
						//Do Nothing since the timer could have already been started again.
						return;
					}
				}
				else if(getInStop().getValue()==true)
				{
					//If timer is running then stop it, otherwiese who cares.
					if (running)
					{
						doStop();
					}
					else
					{
						return;
					}
				}
			}   

			//*****************************************************************************************************
			if (p == inReset) 
			{
				if(getInReset().getValue()==true)
				{
					doReset();
				}
			}
		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
		}
	}




	//*********************************************************************************************************
	// CALCULATE TIME VALUES **********************************************************************************
	//*********************************************************************************************************
	public void setTime() 
	{
		try
		{
			if (running)
			{
				lapTime			= System.currentTimeMillis() - lapStartTime;
				elapseTime		= lastLap + lapTime;
			}
			else
			{
				lapTime			= stopTime - lapStartTime;
				elapseTime		= lastLap + lapTime;
				lastLap			= lapTime;
			}
			
			
			///////////////////////////////////////////////////////////////////////////////////////////////////
			// ELAPSE TIMES ///////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////
			elapseHours			= (int) (elapseTime / (60 * 60 * 1000));
			elapseMinutes		= (int) (elapseTime / (60 * 1000));
			elapseSeconds		= (int) (elapseTime / 1000);
			elapseMilliseconds	= (int) (elapseTime);
			
			//CREATE TEMP PLACEHOLDER FOR ELAPSE TIME.
			tempElapse			= elapseTime;

			//SEPERATE TIME INTO INDIVIDUAL TIME VALUES.
			elapseHr			= (int)  (tempElapse / (60 * 60 * 1000));
			tempElapse			= tempElapse - (elapseHr * (60 * 60 * 1000));
			elapseMin			= (int)  (tempElapse / (60 * 1000));
			tempElapse			= tempElapse - (elapseMin * (60 * 1000));
			elapseSec			= (int)  (tempElapse / 1000);
			tempElapse			= tempElapse - (elapseSec * 1000);
			elapseMs			= (int)  tempElapse;

			strElapseHr			= "" + elapseHr;
			strElapseMin		= "" + elapseMin;
			strElapseSec		= "" + elapseSec;
			strElapseMs			= "" + elapseMs;
			
			
			
			///////////////////////////////////////////////////////////////////////////////////////////////////
			// LAP TIMES //////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////
			lapHours			= (int) (lapTime / (60 * 60 * 1000));
			lapMinutes			= (int) (lapTime / (60 * 1000));
			lapSeconds			= (int) (lapTime / 1000);
			lapMilliseconds		= (int) (lapTime);
			
			//CREATE TEMP PLACEHOLDER FOR LAP TIME
			tempLap				= lapTime;
			
			//SEPERATE TIME INTO INDIVIDUAL TIME VALUES.
			lapHr				= (int)  (tempLap / (60 * 60 * 1000));
			tempLap				= tempLap - (lapHr * (60 * 60 * 1000));
			lapMin				= (int)  (tempLap / (60 * 1000));
			tempLap				= tempLap - (lapMin * (60 * 1000));
			lapSec				= (int)  (tempLap / 1000);
			tempLap				= tempLap - (lapSec * 1000);
			lapMs				= (int)  tempLap;
			
			strLapHr			= "" + lapHr;
			strLapMin			= "" + lapMin;
			strLapSec			= "" + lapSec;
			strLapMs			= "" + lapMs;


			
			

			BRelTime CurrentMSec	= BRelTime.make(elapseTime);
			BRelTime CurrentLapMSec	= BRelTime.make(lapTime);
			
			
			setOutElapseTime(CurrentMSec.abs());
			setOutLapTime(CurrentLapMSec.abs());

			
			//ELAPSE TIMES
			getOutElapseTimeInMilliseconds().setValue(elapseMilliseconds);
			getOutElapseTimeInSeconds().setValue(elapseSeconds);
			getOutElapseTimeInMinutes().setValue(elapseMinutes);
			getOutElapseTimeInHours().setValue(elapseHours);
			
			getOutElapseMilliseconds().setValue(elapseMs);
			getOutElapseSeconds().setValue(elapseSec);
			getOutElapseMinutes().setValue(elapseMin);
			getOutElapseHours().setValue(elapseHr);
			
			getOutElapseTimeString().setValue(strElapseHr + "hrs " + strElapseMin + "mins " + strElapseSec + "." + strElapseMs + "sec");
			
			
			//LAP TIMES
			getOutLapTimeInMilliseconds().setValue(lapMilliseconds);
			getOutLapTimeInSeconds().setValue(lapSeconds);
			getOutLapTimeInMinutes().setValue(lapMinutes);
			getOutLapTimeInHours().setValue(lapHours); 
			
			getOutLapMilliseconds().setValue(lapMs);
			getOutLapSeconds().setValue(lapSec);
			getOutLapMinutes().setValue(lapMin);
			getOutLapHours().setValue(lapHr);
			
			getOutLapTimeString().setValue(strLapHr + "hrs " + strLapMin + "mins " + strLapSec + "." + strLapMs + "sec");

			

			updateTimer();

		}
		catch (Exception e) 
		{
			logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
		}
	}


	//*********************************************************************************************************
	// END OF THE ROAD ****************************************************************************************
	//*********************************************************************************************************
	public static final Log logger = Log.getLog("axCommunity.Stopwatch_v2");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BStopwatch_v2.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");
}
