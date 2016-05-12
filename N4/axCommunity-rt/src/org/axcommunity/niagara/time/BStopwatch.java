package org.axcommunity.niagara.time;

import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;

// import javax.baja.status.*;
// import java.util.Timer;
// import java.util.TimerTask;


/**
* Calculates the time from when a "start" input is receive until a "stop" input is received.
* The "start" and "stop" inputs can be configured to be triggered only on the rising edge
* or require them to remain true.
*
* Several output options are available. 
*
* Running totals of elapse time are given in 
* msec, sec, min and hr as StatusNumerics
*
* Segmented time values of elapse time are also given in
* msec, sec, min and hr as StatusNumerics
*
* Values representing the stopped time are also outputted and
* are only reset to zero by an action input. This makes this output
* good for recording histories on to be used on graphs or charts as needed.
*
* @author   Justin Koffler
* @creation   6 May 11
* @since    axCommunity 11.05.06
*/


public class BStopwatch extends BComponent
{
  private long startTime = -1;
  private long stopTime = -1;
  private boolean running, reset, gotStart, risingOnly;
  private int hours, minutes, seconds, milliseconds, hr, min, sec, ms;
  private long time;
  private String strHr = "0", strMin = "0", strSec = "0", strMsec = "0";
  
  

  //*******************************************************************************************
  //    REFRESHES VALUES AT RATE DEFINED BY inExecutePeriod   *****************************
  //*******************************************************************************************
  Clock.Ticket ticket;
  void updateTimer()
  {            
    if (ticket != null) ticket.cancel();
    ticket = Clock.schedulePeriodically(this, getInExecutePeriod(), timerExpired, null);
  }    
  
  public static final Action timerExpired = newAction(Flags.HIDDEN,null);
  
  public void timerExpired() 
  { 
    invoke(timerExpired,null,null); 
  }

  public void doTimerExpired() throws Exception 
  {
    setTime();
  }
  
  public void started()
  {
    if (getInTriggerOnRisingEdgeOnly().getValue())
    {
      risingOnly = true;
    }

    if (!getInTriggerOnRisingEdgeOnly().getValue())
    {
      risingOnly = false;
    }
    updateTimer();
  }
  

  
  //*******************************************************************************************
  //    ACTION SLOTS  *********************************************************************
  //*******************************************************************************************
  
  /**Action IN TO RESET THE FINAL VALUE OUTPUTS*/
  public static final Action resetFinalValues = newAction(0);
  public void resetFinalValues() 
  { 
    invoke(resetFinalValues, null);
  }

  public void doResetFinalValues()
  {
    getOutFinalTotalMilliseconds().setValue(0);
    getOutFinalTotalSeconds().setValue(0);
    getOutFinalTotalMinutes().setValue(0);
    getOutFinalTotalHours().setValue(0);
  }
  
  //*******************************************************************************************
  //    CODE EXECUTED UPON VALUE CHANGE OF A SLOT *****************************************
  //*******************************************************************************************
  public void changed(Property property, Context context)
  {
    if(!Sys.atSteadyState() || !isRunning())
    {
      return;
    }
    
    //##########################################
    if (property == inTriggerOnRisingEdgeOnly) 
    {
      if (getInTriggerOnRisingEdgeOnly().getValue())
      {
        risingOnly = true;
      }

      if (!getInTriggerOnRisingEdgeOnly().getValue())
      {
        risingOnly = false;
      }

      getInStart().setValue(false);
      getInStop().setValue(false);
      doReset();
    }

    //##########################################
    if (property == inStart) 
    {
      doStart();
    }
    
    //##########################################
    if (property == inStop) 
    {
      doStop();
    }   
    
    //##########################################
    if (property == inReset) 
    {
      doReset();
    }
  }
  
  

  
  //*******************************************************************************************
  //    DO START ACTIONS  *****************************************************************
  //*******************************************************************************************
  public void doStart()
  {
    if (risingOnly && running || getInStop().getValue() && !running) return;
    
    if (running && !risingOnly && !getInStart().getValue())
    {
      doStop();
    }
    
    if (getInStart().getValue() && !running)
    {
      if (gotStart)
      {
        startTime = (startTime - stopTime) + System.currentTimeMillis();
      }else{
        gotStart = true;
        startTime = System.currentTimeMillis();
      }
      running = true;
      getOutRunning().setValue(running);
      setOutTimeStarted(BAbsTime.now());
      setTime();
    }
    
    
  }
  
  
  //*******************************************************************************************
  //    DO STOP ACTIONS   *****************************************************************
  //*******************************************************************************************
  public void doStop()
  {
    
    if (!running) return;
    
    if (!getInStop().getValue() || getInStop().getValue())
    {
      running = false;
      getOutRunning().setValue(running);
      setOutTimeStopped(BAbsTime.now());
      stopTime = System.currentTimeMillis();
      getOutStopTime().setValue(stopTime);
      if (!running)
      {
        setFinalValues();
      }
      setTime();
    }
  }
  
  //*******************************************************************************************
  //    DO RESET ACTIONS  *****************************************************************
  //*******************************************************************************************
  public void doReset()
  {
    if (getInReset().getValue())
      {
        reset = true;
        running = false;
        getOutRunning().setValue(running);
        
        if (!running)
        {
          setFinalValues();
        }
        
        hours = 0;
        minutes = 0;
        seconds = 0;
        milliseconds = 0;
        
        hr = 0;
        min = 0;
        sec = 0;
        ms = 0;
        
        strHr = "0";
        strMin = "0";
        strSec = "0";
        strMsec = "0";
        
        startTime = -1;
        stopTime = -1;
        gotStart = false;
        getOutElapseTimeInMilliseconds().setValue(0);
        getOutElapseTimeInSeconds().setValue(0);
        getOutElapseTimeInMinutes().setValue(0);
        getOutElapseTimeInHours().setValue(0);
        
        getOutMilliseconds().setValue(0);
        getOutSeconds().setValue(0);
        getOutMinutes().setValue(0);
        getOutHours().setValue(0);
        
        
        getOutCurrentMillisec().setValue(System.currentTimeMillis());
        reset = false;
      }
  
  }
  
  
  //*******************************************************************************************
  //    CALCULATE TIME VALUES   *********************************************************
  //*******************************************************************************************
  public void setTime() 
  {
    getOutCurrentMillisec().setValue(System.currentTimeMillis());
    setOutCurrentTime(BAbsTime.now());
    
    if (running)
    {
      time = System.currentTimeMillis() - startTime;
    }else {
      time = stopTime - startTime;
    }
    
    if (getInReset().getValue())
    {
      time = 0;
    }
    
    BRelTime CurrentMSec = BRelTime.make(time);
    setOutElapseTimeRel(CurrentMSec.abs());
    
    if (running)
    {
      hours = (int) (time / (60 * 60 * 1000));
      minutes = (int) (time / (60 * 1000));
      seconds = (int) (time / 1000);
      milliseconds = (int) time;
      
      hr = (int) (time / (60 * 60 * 1000));
      time = time - (hr * (60 * 60 * 1000));
      min = (int) (time / (60 * 1000));
      time = time - (min * (60 * 1000));
      sec = (int) (time / 1000);
      time = time - (sec * 1000);
      ms = (int) time;
      
      strHr = "" + hr;
      strMin = "" + min;
      strSec = "" + sec;
      strMsec = "" + ms;
      
    } 
    
    getOutElapseTimeInMilliseconds().setValue(milliseconds);
    getOutElapseTimeInSeconds().setValue(seconds);
    getOutElapseTimeInMinutes().setValue(minutes);
    getOutElapseTimeInHours().setValue(hours);
    
    getOutMilliseconds().setValue(ms);
    getOutSeconds().setValue(sec);
    getOutMinutes().setValue(min);
    getOutHours().setValue(hr);
    
    getOutStartTime().setValue(startTime);
    getOutStopTime().setValue(stopTime);
    
    getOutElapseTimeString().setValue(strHr + "hrs " + strMin + "mins " + strSec + "." + strMsec + "sec");
    
  }
  
  
  
  //*******************************************************************************************
  //    WRITE FINAL TIME VALUES   *********************************************************
  //*******************************************************************************************
  public void setFinalValues()
  {
    if (!running)
      {
        getOutFinalTotalMilliseconds().setValue(milliseconds);
        getOutFinalTotalSeconds().setValue(seconds);
        getOutFinalTotalMinutes().setValue(minutes);
        getOutFinalTotalHours().setValue(hours);    
      }
  }

  

  
  //*******************************************************************************************************************
  //    USER CONTROLS *********************************************************************************************
  //*******************************************************************************************************************
  
  /**RelTime VALUE IN REPRESENTING FREQUENCY THE CLOCK VALUES WILL BE UPDATED. SET THIS VALUE IN THE PALETTE BEFORE PUTTING OBJECT ON WIRESHEET (I WAS TOO LAZY TO PROGRAM OTHERWISE)*/
  public static final Property inExecutePeriod = newProperty(0, BRelTime.make(500));
    public BRelTime getInExecutePeriod() { return (BRelTime)get(inExecutePeriod); }
    public void setInExecutePeriod(BRelTime v) { set(inExecutePeriod, v); }

  /**StatusBoolean VALUE IN REPRESENTING IF START AND STOP ARE TRIGGERED ON RISING EDGE ONLY (DEFAULT TRUE, RISING EDGE ONLY).*/
  public final static Property inTriggerOnRisingEdgeOnly = newProperty(0, new BStatusBoolean(true));
  public BStatusBoolean getInTriggerOnRisingEdgeOnly() { return (BStatusBoolean)get(inTriggerOnRisingEdgeOnly); }
  public void setInTriggerOnRisingEdgeOnly(BStatusBoolean v) { set(inTriggerOnRisingEdgeOnly, v); }
  
  /**StatusBoolean VALUE IN REPRESENTING TIMER START.*/
  public final static Property inStart = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
  public BStatusBoolean getInStart() { return (BStatusBoolean)get(inStart); }
  public void setInStart(BStatusBoolean v) { set(inStart, v); }
  
  /**StatusBoolean VALUE IN REPRESENTING TIMER STOP.*/
  public final static Property inStop = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
  public BStatusBoolean getInStop() { return (BStatusBoolean)get(inStop); }
  public void setInStop(BStatusBoolean v) { set(inStop, v); }
  
  /**StatusBoolean VALUE IN REPRESENTING TIMER RESET.*/
  public final static Property inReset = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
  public BStatusBoolean getInReset() { return (BStatusBoolean)get(inReset); }
  public void setInReset(BStatusBoolean v) { set(inReset, v); }
  
  /**StatusBoolean VALUE OUT REPRESENTING TIMER IS RUNNING.*/
  public final static Property outRunning = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
  public BStatusBoolean getOutRunning() { return (BStatusBoolean)get(outRunning); }
  public void setOutRunning(BStatusBoolean v) { set(outRunning, v); }
  
  
  //*******************************************************************************************************************
  //    TIME FORMATS  *********************************************************************************************
  //*******************************************************************************************************************
  
  /**AbsTime VALUE OUT REPRESENTING CURRENT TIME.*/
  public static final Property outCurrentTime = newProperty(Flags.READONLY, BAbsTime.make(), BFacets.make("showSeconds",true));
  public BAbsTime getOutCurrentTime() { return (BAbsTime)get(outCurrentTime); }
  public void setOutCurrentTime(BAbsTime v) { set(outCurrentTime, v); }
  
  /**AbsTime VALUE OUT REPRESENTING TIME STARTED.*/
  public static final Property outTimeStarted = newProperty(Flags.READONLY, BAbsTime.make(), BFacets.make("showSeconds",true));
  public BAbsTime getOutTimeStarted() { return (BAbsTime)get(outTimeStarted); }
  public void setOutTimeStarted(BAbsTime v) { set(outTimeStarted, v); }
  
  /**AbsTime VALUE OUT REPRESENTING TIME STOPPED.*/
  public static final Property outTimeStopped = newProperty(Flags.READONLY, BAbsTime.make(), BFacets.make("showSeconds",true));
  public BAbsTime getOutTimeStopped() { return (BAbsTime)get(outTimeStopped); }
  public void setOutTimeStopped(BAbsTime v) { set(outTimeStopped, v); }
  
  /**RelTime VALUE OUT REPRESENTING ELAPSE TIME.*/
  public final static Property outElapseTimeRel = newProperty(Flags.SUMMARY + Flags.READONLY, BRelTime.DEFAULT);
  public BRelTime getOutElapseTimeRel() { return (BRelTime)get(outElapseTimeRel); }
  public void setOutElapseTimeRel(BRelTime v) { set(outElapseTimeRel, v); }

  //*******************************************************************************************************************
  //    SOME TIMING AND DEBUG VALUES    *************************************************************************
  //*******************************************************************************************************************

  /**StatusNumeric VALUE OUT REPRESENTING MSEC STARTED FROM MIDNIGHT JANUARY 1, 1970 UTC.*/
  public static final Property outStartTime = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutStartTime() { return (BStatusNumeric)get(outStartTime);}
  public void setOutStartTime(BStatusNumeric v) {set(outStartTime,v);}

  /**StatusNumeric VALUE OUT REPRESENTING MSEC STOPPED FROM MIDNIGHT JANUARY 1, 1970 UTC.*/
  public static final Property outStopTime = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutStopTime() { return (BStatusNumeric)get(outStopTime);}
  public void setOutStopTime(BStatusNumeric v) {set(outStopTime,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING CURRENT MSEC FROM MIDNIGHT JANUARY 1, 1970 UTC.*/
  public static final Property outCurrentMillisec = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutCurrentMillisec() { return (BStatusNumeric)get(outCurrentMillisec);}
  public void setOutCurrentMillisec(BStatusNumeric v) {set(outCurrentMillisec,v);}
  
  
  //*******************************************************************************************************************
  //    ELAPSE TIME IN VARIOUS FORMATS    *************************************************************************
  //*******************************************************************************************************************
  
  /**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN MILLISECONDS.*/
  public static final Property outElapseTimeInMilliseconds = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutElapseTimeInMilliseconds() { return (BStatusNumeric)get(outElapseTimeInMilliseconds);}
  public void setOutElapseTimeInMilliseconds(BStatusNumeric v) {set(outElapseTimeInMilliseconds,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN SECONDS.*/
  public static final Property outElapseTimeInSeconds = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutElapseTimeInSeconds() { return (BStatusNumeric)get(outElapseTimeInSeconds);}
  public void setOutElapseTimeInSeconds(BStatusNumeric v) {set(outElapseTimeInSeconds,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN MINUTES.*/
  public static final Property outElapseTimeInMinutes = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutElapseTimeInMinutes() { return (BStatusNumeric)get(outElapseTimeInMinutes);}
  public void setOutElapseTimeInMinutes(BStatusNumeric v) {set(outElapseTimeInMinutes,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING ELAPSE TIME IN HOURS.*/
  public static final Property outElapseTimeInHours = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutElapseTimeInHours() { return (BStatusNumeric)get(outElapseTimeInHours);}
  public void setOutElapseTimeInHours(BStatusNumeric v) {set(outElapseTimeInHours,v);}
  
  /**StatusString VALUE OUT REPRESENTING ELAPSE TIME IN HOURS, MINUTES AND SECONDS.*/
  public static final Property outElapseTimeString = newProperty(Flags.SUMMARY, new BStatusString());
    public BStatusString getOutElapseTimeString() { return (BStatusString)get(outElapseTimeString);}
    public void setOutElapseTimeString(BStatusString v) {set(outElapseTimeString,v);}

  
  //*******************************************************************************************************************
  //    INDIVIDUAL NUMERIC TIME VALUES    *************************************************************************
  //*******************************************************************************************************************
  
  /**StatusNumeric VALUE OUT REPRESENTING MILLISOCONDS PORTION OF ELASPSE TIME.*/
  public static final Property outMilliseconds = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutMilliseconds() { return (BStatusNumeric)get(outMilliseconds);}
  public void setOutMilliseconds(BStatusNumeric v) {set(outMilliseconds,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING SECONDS PORTION OF ELASPSE TIME.*/
  public static final Property outSeconds = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutSeconds() { return (BStatusNumeric)get(outSeconds);}
  public void setOutSeconds(BStatusNumeric v) {set(outSeconds,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING MINUTES PORTION OF ELASPSE TIME.*/
  public static final Property outMinutes = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutMinutes() { return (BStatusNumeric)get(outMinutes);}
  public void setOutMinutes(BStatusNumeric v) {set(outMinutes,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING HOURS PORTION OF ELASPSE TIME.*/
  public static final Property outHours = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutHours() { return (BStatusNumeric)get(outHours);}
  public void setOutHours(BStatusNumeric v) {set(outHours,v);}

  
  //*******************************************************************************************************************
  //    FINAL VALUES UPON TIMER STOP, NO LIVE UPDATES   *********************************************************
  //*******************************************************************************************************************
  
  /**StatusNumeric VALUE OUT REPRESENTING FINAL ELAPSE TIME IN MILLISECONDS, GOOD TO USE WITH HISTORIES AND GRAPHING. USE THE "resetFinalValues" ACTION TO RESET.*/
  public static final Property outFinalTotalMilliseconds = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutFinalTotalMilliseconds() { return (BStatusNumeric)get(outFinalTotalMilliseconds);}
  public void setOutFinalTotalMilliseconds(BStatusNumeric v) {set(outFinalTotalMilliseconds,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING FINAL ELAPSE TIME IN SECONDS, GOOD TO USE WITH HISTORIES AND GRAPHING. USE THE "resetFinalValues" ACTION TO RESET.*/
  public static final Property outFinalTotalSeconds = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutFinalTotalSeconds() { return (BStatusNumeric)get(outFinalTotalSeconds);}
  public void setOutFinalTotalSeconds(BStatusNumeric v) {set(outFinalTotalSeconds,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING FINAL ELAPSE TIME IN MINUTES, GOOD TO USE WITH HISTORIES AND GRAPHING. USE THE "resetFinalValues" ACTION TO RESET.*/
  public static final Property outFinalTotalMinutes = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutFinalTotalMinutes() { return (BStatusNumeric)get(outFinalTotalMinutes);}
  public void setOutFinalTotalMinutes(BStatusNumeric v) {set(outFinalTotalMinutes,v);}
  
  /**StatusNumeric VALUE OUT REPRESENTING FINAL ELAPSE TIME IN HOURS, GOOD TO USE WITH HISTORIES AND GRAPHING. USE THE "resetFinalValues" ACTION TO RESET.*/
  public static final Property outFinalTotalHours = newProperty(Flags.READONLY, new BStatusNumeric(0), BFacets.makeNumeric(0));
  public BStatusNumeric getOutFinalTotalHours() { return (BStatusNumeric)get(outFinalTotalHours);}
  public void setOutFinalTotalHours(BStatusNumeric v) {set(outFinalTotalHours,v);}

  
  //*******************************************************************************************************************
  //    END OF THE ROAD   *****************************************************************************************
  //*******************************************************************************************************************
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BStopwatch.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");
}
