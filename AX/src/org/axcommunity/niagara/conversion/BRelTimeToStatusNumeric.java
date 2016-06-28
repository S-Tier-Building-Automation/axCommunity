
package org.axcommunity.niagara.conversion;

import javax.baja.status.*;
import javax.baja.sys.*;

/**
 * The BRelTimeToStatusNumeric takes a RelTime and turns it into Hour or Minutes or Seconds 
 * @author		Dean Mynott			 - Ronin Control Systems Pty Ltd
 * @creation	16 June 2011
 */

public class BRelTimeToStatusNumeric extends BComponent implements Runnable
{

	private static BFacets	f1			= BFacets.make("showSeconds", true);
	private static BFacets	f2			= BFacets.make("showMilliseconds", false);
	private static BFacets	fRelTime	= BFacets.make(f1,f2);
	
	private static BFacets	fNum		= BFacets.make("precision", 0);
	
	//---------------------------------------------------------------------------------------------------------
	public static final Property facetsRelTime = newProperty(0, fRelTime,null);
	public BFacets getFacetsRelTime() { return (BFacets)get(facetsRelTime); }
	public void setFacetsRelTime(BFacets v) { set(facetsRelTime,v,null); }
	
	//---------------------------------------------------------------------------------------------------------
	public static final Property facetsNumeric = newProperty(0, fNum,null);
	public BFacets getFacetsNumeric() { return (BFacets)get(facetsNumeric); }
	public void setFacetsNumeric(BFacets v) { set(facetsNumeric,v,null); }
	
	//---------------------------------------------------------------------------------------------------------
	public BFacets getSlotFacets(Slot slot)
	{
		if(slot == relTime)
		{
			return getFacetsRelTime();
		}
		
		if(slot == seconds || slot == minutes || slot == hours)
		{
			return getFacetsNumeric();
		}
		
		return super.getSlotFacets(slot);
	}

	
	// Property "relTime"
	public static final Property relTime = newProperty(0|Flags.EXECUTE_ON_CHANGE|Flags.SUMMARY, (BValue)BRelTime.TYPE.getInstance(), fRelTime);
	public BRelTime getRelTime() { return (BRelTime)get(relTime); }
	public void setRelTime(BRelTime v) { set(relTime, v, null); }
	
	// Property "seconds"
	public static final Property seconds = newProperty(0|Flags.SUMMARY, new BStatusNumeric(), fNum);
	public BStatusNumeric getSeconds() { return (BStatusNumeric)get(seconds); }
	public void setSeconds(BStatusNumeric v) { set(seconds, v, null); }
	
	// Property "minutes"
	public static final Property minutes = newProperty(0|Flags.SUMMARY, new BStatusNumeric(), fNum);
	public BStatusNumeric getMinutes() { return (BStatusNumeric)get(minutes); }
	public void setMinutes(BStatusNumeric v) { set(minutes, v, null); }

	// Property "hours"
	public static final Property hours = newProperty(0|Flags.SUMMARY, new BStatusNumeric(), fNum);
	public BStatusNumeric getHours() { return (BStatusNumeric)get(hours); }
	public void setHours(BStatusNumeric v) { set(hours, v, null); }

	// Action "execute"
	public static final Action execute = newAction(0|Flags.ASYNC, null, null);
	public void execute(){ invoke(execute, null, null); }
	public void doExecute() throws Exception
	{
		try { onExecute(); }
		catch (Throwable t) { throw new Exception(t); }
	}


	// BComponent Overrides
	public void started() throws Exception { try { onStart(); } catch(Throwable t) { throw new Exception(t); } }
	public void stopped() throws Exception { try { onStop(); } catch(Throwable t) { throw new Exception(t); } }

	public void changed(Property prop, Context cx)
	{
		super.changed(prop, cx);
		if(!Sys.atSteadyState() || !isRunning()){ return; }
		if (Flags.isExecuteOnChange(this, prop)){ execute(); }
	}



	public void run() { System.out.println("Source BProgram did not override run(). Exiting thread."); }
	public final BComponent getComponent() { return this; }


	/** Print a string to standard out without a trailing new-line. */
	public void print(String s) { System.out.print(s); System.out.flush(); }

	/** Print a string to standard out with a trailing new-line. */
	public void println(String s) { System.out.println(s); }


	public void onStart() throws Exception
	{
		// start up code here
	}
	
	public void onExecute() throws Exception
	{
		getSeconds().setValue(	(double) getRelTime().getMillis()/1000		);
		getMinutes().setValue(	(double) getRelTime().getMillis()/1000/60	);
		getHours().setValue(	(double) getRelTime().getMillis()/1000/60/60	);
	}
	
	public void onStop() throws Exception
	{
		// shutdown code here
	}
	
	//icon for this component
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png"); 


	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BRelTimeToStatusNumeric.class);

}
