/**
 * 
 */
package org.axcommunity.niagara.logic;

import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.status.*;
import javax.baja.sys.*;

/**
 *  Use to determine failure by comparing the status to the command over the defined time period.
 *  If status does not match by the end of the elapsed time from, sets output to true (for alarm).
 *  Actually just a variation of BBooleanDelay by Andy Saunders @ Tridium 
 * @author Vance Hensley, pctechs4u
 * @creation Apr 13, 2009
 *
 */
public class BBooleanCompareCmdStatus extends BComponent
{ 
////////////////////////////////////////////////////////////////
//Property "inCmd"
////////////////////////////////////////////////////////////////
 
 /**
  * Slot for the <code>inCmd</code> property.
  */
  public static final Property inCmd = newProperty(Flags.SUMMARY|Flags.TRANSIENT, new BStatusBoolean(false, BStatus.nullStatus),null);
 
 /**
  * Get the <code>inCmd</code> property.
  */
 public BStatusBoolean getInCmd() { return (BStatusBoolean)get(inCmd); }
 
 /**
  * Set the <code>inCmd</code> property.
  */
 public void setInCmd(BStatusBoolean v) { set(inCmd,v,null); }
  
////////////////////////////////////////////////////////////////
//Property "inStatus"
////////////////////////////////////////////////////////////////

/**
 * Slot for the <code>inStatus</code> property.
 */
 public static final Property inStatus = newProperty(Flags.SUMMARY|Flags.TRANSIENT, new BStatusBoolean(false, BStatus.nullStatus),null);

/**
 * Get the <code>inStatus</code> property.
 */
public BStatusBoolean getInStatus() { return (BStatusBoolean)get(inStatus); }

/**
 * Set the <code>inCmd</code> property.
 */
public void setInStatus(BStatusBoolean v) { set(inStatus,v,null); }
 
////////////////////////////////////////////////////////////////
//Property "onDelay"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>onDelay</code> property.
*/
public static final Property onDelay = newProperty(0, BRelTime.make(1000l),null);

/**
* Get the <code>onDelay</code> property.
*/
public BRelTime getOnDelay() { return (BRelTime)get(onDelay); }

/**
* Set the <code>onDelay</code> property.
*/
public void setOnDelay(BRelTime v) { set(onDelay,v,null); }

////////////////////////////////////////////////////////////////
//Property "onDelayActive"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>onDelayActive</code> property.
*/
public static final Property onDelayActive = newProperty(Flags.TRANSIENT|Flags.READONLY, false,null);

/**
* Get the <code>onDelayActive</code> property.
*/
public boolean getOnDelayActive() { return getBoolean(onDelayActive); }

/**
* Set the <code>onDelayActive</code> property.
*/
public void setOnDelayActive(boolean v) { setBoolean(onDelayActive,v,null); }

////////////////////////////////////////////////////////////////
//Action "onTimerExpired"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>onTimerExpired</code> action.
*/
public static final Action onTimerExpired = newAction(Flags.HIDDEN,null);

/**
* Invoke the <code>onTimerExpired</code> action.
*/
public void onTimerExpired() { invoke(onTimerExpired,null,null); }
 
////////////////////////////////////////////////////////////////
//Property "out"
////////////////////////////////////////////////////////////////

/**
 * Slot for the <code>out</code> property.
 */
public static final Property out = newProperty(Flags.TRANSIENT|Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false),null);

/**
 * Get the <code>out</code> property.
 */
public BStatusBoolean getOut() { return (BStatusBoolean)get(out); }

/**
 * Set the <code>out</code> property.
 */
public void setOut(BStatusBoolean v) { set(out,v,null); }

////////////////////////////////////////////////////////////////
//Type
////////////////////////////////////////////////////////////////

 public Type getType() { return TYPE; }
 public static final Type TYPE = Sys.loadType(BBooleanCompareCmdStatus.class);

 public BIcon getIcon() { return icon; }
 private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/pctechs4u.png");
 
 public void started()
 {
   //getOut().setValue(getIn().getBoolean());
 }

 public void atSteadyState()
 {
     calculate();
 }
 public void changed(Property p, Context cx)
 {
   if (!isRunning()) return;
   if (p == inCmd || p ==inStatus)
   {
     setOnDelayActive(false);
     calculate();
   }
 }

 public void calculate()
 {
   if(!getInCmd().getStatus().isValid())
     return;
//                 ( command & status match [true or false] )       or     ( status is true [hand, etc] & command is false)
   boolean input = ((getInCmd().getValue() == getInStatus().getValue()) || (getInStatus().getValue() && !getInCmd().getValue()));
   if(input)
   {
     setOutput(false);
     input = lastInput; 
   }
   else if(getOnDelay().getMillis() == 0l)
     {
       if( offTicket != null) offTicket.cancel();
       setOutput(true);
     }
     else
       startOnTimer();
 }

 private void setOutput(boolean value)
 {
   getOut().setValue(value);
 }

 public void doOnTimerExpired()
 {
   setOutput(true);
   setOnDelayActive(false);
 }

 void startOnTimer()
 {
   if( offTicket != null) offTicket.cancel();
   if (onTicket != null) onTicket.cancel();
   onTicket = Clock.schedule(this, getOnDelay(), onTimerExpired, null);
   setOnDelayActive(true);
 }    
 
 boolean lastInput = false;
 Clock.Ticket onTicket;      // Used to manage the current timer
 Clock.Ticket offTicket;      // Used to manage the current timer
 
}
