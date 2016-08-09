/**
 * 
 */
package org.axcommunity.niagara.logic;

import javax.baja.sys.BComponent;
import javax.baja.sys.BEnum;
import javax.baja.sys.BFacets;
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
 * @creation Apr 13, 2009 / Modified July 13, 2009
 * Update: Went in and simply copied Andy's code in and created internal booleans to sort out a command is true and status is false.
 * Used the outcome of this logic to mimic the input value that Andy wrote the code for to begin with.
 * @modified July, 17, 2009 
 *
 */
public class BBooleanCompareCmdStatus extends BComponent
{
////////////////////////////////////////////////////////////////
//Property "facets"
////////////////////////////////////////////////////////////////
 /**
  * Slot for the <code>facets</code> property.
  * These facets are applied against the out property.
  */
 public static final Property facets = newProperty(0, BFacets.makeBoolean(),null);
 /**
  * Get the <code>facets</code> property.
  */
 public BFacets getFacets() { return (BFacets)get(facets); }
 /**
  * Set the <code>facets</code> property.
  */
 public void setFacets(BFacets v) { set(facets,v,null); }
  
////////////////////////////////////////////////////////////////
//Property "inCmd"
////////////////////////////////////////////////////////////////
/**
 * Slot for the <code>inCmd</code> property.
 */
public static final Property inCmd = newProperty(Flags.SUMMARY, new BStatusBoolean(),null);
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
public static final Property inStatus = newProperty(Flags.SUMMARY, new BStatusBoolean(),null);
/**
* Get the <code>inStatus</code> property.
*/
public BStatusBoolean getInStatus() { return (BStatusBoolean)get(inStatus); }
/**
* Set the <code>inStatus</code> property.
*/
public void setInStatus(BStatusBoolean v) { set(inStatus,v,null); }

////////////////////////////////////////////////////////////////
//Property "onDelay"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>onDelay</code> property.
*/
public static final Property onDelay = newProperty(0, BRelTime.make(15000l),null);

/**
* Get the <code>onDelay</code> property.
*/
public BRelTime getOnDelay() { return (BRelTime)get(onDelay); }

/**
* Set the <code>onDelay</code> property.
*/
public void setOnDelay(BRelTime v) { set(onDelay,v,null); }

////////////////////////////////////////////////////////////////
//Property "offDelay"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>offDelay</code> property.
*/
public static final Property offDelay = newProperty(0, BRelTime.make(15000l),null);

/**
* Get the <code>offDelay</code> property.
*/
public BRelTime getOffDelay() { return (BRelTime)get(offDelay); }

/**
* Set the <code>offDelay</code> property.
*/
public void setOffDelay(BRelTime v) { set(offDelay,v,null); }

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
//Property "offDelayActive"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>offDelayActive</code> property.
*/
public static final Property offDelayActive = newProperty(Flags.TRANSIENT|Flags.READONLY, false,null);

/**
* Get the <code>offDelayActive</code> property.
*/
public boolean getOffDelayActive() { return getBoolean(offDelayActive); }

/**
* Set the <code>offDelayActive</code> property.
*/
public void setOffDelayActive(boolean v) { setBoolean(offDelayActive,v,null); }

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
//Action "offTimerExpired"
////////////////////////////////////////////////////////////////

/**
* Slot for the <code>offTimerExpired</code> action.
*/
public static final Action offTimerExpired = newAction(Flags.HIDDEN,null);

/**
* Invoke the <code>offTimerExpired</code> action.
*/
public void offTimerExpired() { invoke(offTimerExpired,null,null); }

////////////////////////////////////////////////////////////////
//Type
////////////////////////////////////////////////////////////////
 public Type getType() { return TYPE; }
 public static final Type TYPE = Sys.loadType(BBooleanCompareCmdStatus.class);
 
 public BBooleanCompareCmdStatus()
 {
 }
 /**
  * Init if started after steady state has been reached.
  */
 public void started()
 {
   //getOut().setValue(getIn().getBoolean());
 }

 public void atSteadyState()
 {
     calculate();
 }

/**
  * set output on in change.
  */
 public void changed(Property p, Context cx)
 {
   if (!isRunning()) return;
   if (p == inCmd || p == inStatus)
     calculate();
 }

 public void calculate()
 {
   if(!(getInCmd().getStatus().isValid() || getInStatus().getStatus().isValid()))
     return;
   boolean inCall = getInCmd().getValue();
   boolean inRun = getInStatus().getValue();
   boolean noMatch = (inCall && !inRun); // This takes the place of getIn().getValue() from original code
   if(noMatch && !lastInput)
   {
     lastInput = noMatch;
     if(getOnDelay().getMillis() == 0l)
     {
       if( offTicket != null) offTicket.cancel();
       setOutput(true);
     }
     else
       startOnTimer();
   }
   else if(!noMatch && lastInput)
   {
     lastInput = noMatch;
     if(getOffDelay().getMillis() == 0l)
     {
       if (onTicket != null) onTicket.cancel();
       setOutput(false);
     }
     else
     startOffTimer();
   }
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

 public void doOffTimerExpired()
 {
   setOutput(false);
   setOffDelayActive(false);
 }

 void startOnTimer()
 {
   if( offTicket != null) offTicket.cancel();
   if (onTicket != null) onTicket.cancel();
   onTicket = Clock.schedule(this, getOnDelay(), onTimerExpired, null);
   setOnDelayActive(true);
   setOffDelayActive(false);
 }    
 
 void startOffTimer()
 {            
   if (onTicket != null) onTicket.cancel();
   if (offTicket != null) offTicket.cancel();
   offTicket = Clock.schedule(this, getOffDelay(), offTimerExpired, null);
   setOffDelayActive(true);
   setOnDelayActive(false);
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

////////////////////////////////////////////////////////////////
//BIStatus interface
////////////////////////////////////////////////////////////////
 public BStatus getStatus() { return getOut().getStatus(); }

////////////////////////////////////////////////////////////////
//BIBoolean interface
////////////////////////////////////////////////////////////////
 public boolean getBoolean() { return getOut().getValue(); }
 public final BFacets getBooleanFacets() { return getFacets(); }
 /**
  * Return the value as a enum.
  */
 public final BEnum getEnum() { return getOut().getEnum(); }
 /**
  * Return getFacets().
  */
 public final BFacets getEnumFacets() { return getFacets(); }
 public BIcon getIcon() { return icon; }
 private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/pctechs4u.png");
 
////////////////////////////////////////////////////////////////
//Attributes
////////////////////////////////////////////////////////////////

boolean lastInput = false;
Clock.Ticket onTicket;      // Used to manage the current timer
Clock.Ticket offTicket;      // Used to manage the current timer
 
}
