package org.axcommunity.niagara.string;

import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BString;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;
import javax.baja.util.BFormat;

public class BParentFolderInfo extends BComponent
{
	Clock.Ticket ticket;

	//----------------------------------------------------------------------------------------------------------
	public static final Action refresh = newAction(0);
	public void refresh(){invoke(refresh, null);}
	public void doRefresh()
	{
		updateID();
	}
	
	//----------------------------------------------------------------------------------------------------------
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);
	public void timerExpired() { invoke(timerExpired,null,null); }
	public void doTimerExpired()
	{
		updateID();
	}
	
	
	/**BRelTime input, time to wait after startup before calculating. */
	public static final Property startupDelay = newProperty(0, BRelTime.make(10000l),BFacets.make(BFacets.SHOW_MILLISECONDS, true));
	public BRelTime getStartupDelay() { return (BRelTime)get(startupDelay); }
	public void setStartupDelay(BRelTime v) { set(startupDelay,v,null); }
	
	/**BFormat string input that represents the path or name you wish to display.*/
	public static final Property inFormat = newProperty(0, BFormat.make("%parent.displayName%"));
    public BFormat getInFormat() {return (BFormat) get(inFormat);}
    public void setInFormat(BFormat v) {set(inFormat, v);}

	/**StatusString output representing the value from the BFormat*/
	public static final Property parentName = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getParentName() {return (BStatusString)get(parentName);}
	public void setParentName(BStatusString v) { set(parentName, v); }
	
	/**Boolean Topic is fired when the 'parentName' slot value changes.*/
	public static final Topic changed = newTopic(0);
	public void fireChanged(BBoolean event){fire(changed,event,null);}
	
	/**String Topic is fired after each refresh.*/
	public static final Topic ParentName = newTopic(0);
	public void fireParentName(BString event){fire(ParentName,event,null);}
	
	
	
	//---------------------------------------------------------------------------------------------------------
	public void started()	throws Exception { try { onStart();	} catch(Throwable t) { throw new Exception(t); } }
	public void stopped()	throws Exception { try { onStop();	} catch(Throwable t) { throw new Exception(t); } }
	
	public void onStart()	throws Exception
	{
		updateTimer();
	}
	
	public void onStop()	throws Exception{	}

	//----------------------------------------------------------------------------------------------------------
	void updateTimer()
	{            
		if (ticket != null) ticket.cancel();
		ticket = Clock.schedule(this, getStartupDelay(), timerExpired, null);
	}
	
	
	//----------------------------------------------------------------------------------------------------------
	public void changed(Property prop, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		if(isRunning())
		{
			if (prop == inFormat) 
			{
				updateID();
			}
		}
	}
	
	
	//----------------------------------------------------------------------------------------------------------
	public void updateID()
	{
		String	outNew		= getInFormat().format(this.asComponent());
		String	outCurrent	= getParentName().getValue();
		
		getParentName().setValue(outNew);
		
		fireParentName(BString.make(outNew)); 
		
		if(!outCurrent.equals(outNew)) 
		{ 
			fireChanged(BBoolean.make(true));
		}
	}
	
	
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BParentFolderInfo.class);
	public Type getType() { return TYPE; }

}
