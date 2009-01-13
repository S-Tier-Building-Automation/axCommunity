package org.axcommunity.niagara.conversion;

import javax.baja.sys.*;
import javax.baja.status.*;
import javax.baja.control.*;

/**
 * Executes every updateTime and displays the current time in seven different  
 *outputs out, hours, minutes, seconds, day, month and year.
 *
 * @author    Kevin Crabill, Cochrane Supply
 */
public class BTimeSplitter
  extends BComponent
{
  

////////////////////////////////////////////////////////////////
// Property "facets"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>facets</code> property.
   */
  public static final Property facets = newProperty(0, BFacets.make(BFacets.SHOW_DATE, BBoolean.make(true), BFacets.SHOW_TIME, BBoolean.make(true), BFacets.SHOW_SECONDS, BBoolean.make(true)),null);
  
  /**
   * Get the <code>facets</code> property.
   */
  public BFacets getFacets() { return (BFacets)get(facets); }
  
  /**
   * Set the <code>facets</code> property.
   */
  public void setFacets(BFacets v) { set(facets,v,null); }

////////////////////////////////////////////////////////////////
// Property "updateTime"
////////////////////////////////////////////////////////////////
  
  /**
   * time interval to execute object, default is 5 seconds.
   */
  public static final Property updateTime = newProperty(0, BRelTime.make(500l),BFacets.make(BFacets.SHOW_MILLISECONDS, true) );
  
  /**
   * Get the <code>updateTime</code> property.
   */
  public BRelTime getUpdateTime() { return (BRelTime)get(updateTime); }
  
  /**
   * Set the <code>updateTime</code> property.
   */
  public void setUpdateTime(BRelTime v) { set(updateTime,v,null); }

////////////////////////////////////////////////////////////////
// Property "out"
////////////////////////////////////////////////////////////////
  
  /**
   * current time at execution
   */
  public static final Property out = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, BAbsTime.make(),null);
  
  /**
   * Get the <code>out</code> property.
   */
  public BAbsTime getOut() { return (BAbsTime)get(out); }
  
  /**
   * Set the <code>out</code> property.
   */
  public void setOut(BAbsTime v) { set(out,v,null); }

////////////////////////////////////////////////////////////////
// Property "hours"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>hours</code> property.
   */
  public static final Property hours = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>hours</code> property.
   */
  public BStatusNumeric getHours() { return (BStatusNumeric)get(hours); }
  
  /**
   * Set the <code>hours</code> property.
   */
  public void setHours(BStatusNumeric v) { set(hours,v,null); }

////////////////////////////////////////////////////////////////
// Property "minutes"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>minutes</code> property.
   */
  public static final Property minutes = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>minutes</code> property.
   */
  public BStatusNumeric getMinutes() { return (BStatusNumeric)get(minutes); }
  
  /**
   * Set the <code>minutes</code> property.
   */
  public void setMinutes(BStatusNumeric v) { set(minutes,v,null); }

////////////////////////////////////////////////////////////////
// Property "seconds"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>seconds</code> property.
   */
  public static final Property seconds = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>seconds</code> property.
   */
  public BStatusNumeric getSeconds() { return (BStatusNumeric)get(seconds); }
  
  /**
   * Set the <code>seconds</code> property.
   */
  public void setSeconds(BStatusNumeric v) { set(seconds,v,null); }

////////////////////////////////////////////////////////////////
// Property "day"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>day</code> property.
   */
  public static final Property day = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>day</code> property.
   */
  public BStatusNumeric getDay() { return (BStatusNumeric)get(day); }
  
  /**
   * Set the <code>day</code> property.
   */
  public void setDay(BStatusNumeric v) { set(day,v,null); }

////////////////////////////////////////////////////////////////
// Property "month"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>month</code> property.
   */
  public static final Property month = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>month</code> property.
   */
  public BStatusNumeric getMonth() { return (BStatusNumeric)get(month); }
  
  /**
   * Set the <code>month</code> property.
   */
  public void setMonth(BStatusNumeric v) { set(month,v,null); }

////////////////////////////////////////////////////////////////
// Property "year"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>year</code> property.
   */
  public static final Property year = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>year</code> property.
   */
  public BStatusNumeric getYear() { return (BStatusNumeric)get(year); }
  
  /**
   * Set the <code>year</code> property.
   */
  public void setYear(BStatusNumeric v) { set(year,v,null); }

////////////////////////////////////////////////////////////////
// Action "timerExpired"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>timerExpired</code> action.
   */
  public static final Action timerExpired = newAction(Flags.HIDDEN,null);
  
  /**
   * Invoke the <code>timerExpired</code> action.
   */
  public void timerExpired() { invoke(timerExpired,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTimeSplitter.class);


  public BTimeSplitter()
  {
  }
  
  public void started()
  {
    initTimer();
  }

  protected void initTimer()
  {
    if (ticket != null) ticket.cancel();
    ticket = Clock.schedulePeriodically(this, getUpdateTime(), timerExpired, null);
  }

/**
   * setoutput on in change.
   */
  public void changed(Property p, Context cx)
  {
    if (!isRunning()) return;

    if (p.equals(updateTime))
    {
      initTimer();
    }
    else
    {
      super.changed(p, cx);
    }
  }

  public void doTimerExpired()
  { 
    BAbsTime now = BAbsTime.now();
    setOut( now );
    setHours( new BStatusNumeric( now.getHour() ) );
    setMinutes( new BStatusNumeric( now.getMinute() ) );
    setSeconds( new BStatusNumeric( now.getSecond() ) );
    setDay( new BStatusNumeric( now.getDay() ) );
    setMonth( new BStatusNumeric( now.getMonth().getMonthOfYear() ) );
    setYear( new BStatusNumeric( now.getYear() ) );
  }

  public String toString(Context cx)
  {
    return getOut().toString(cx);
  }

  /**
   * Apply the "facets" property to the "out" property.
  */
  public BFacets getSlotFacets(Slot slot)
  {
    if (slot == out) return getFacets();
    return super.getSlotFacets(slot);
  }
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/cochraneicon.png");

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

  boolean lastInput;
  Clock.Ticket ticket;      // Used to manage the current timer
  
}
