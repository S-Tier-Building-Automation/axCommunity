package org.axcommunity.niagara.logic;

import javax.baja.sys.*;
import javax.baja.status.*;
import javax.baja.log.Log;

/**
* Numeric Setpoint object that remembers the username/timestamp of the last change
*
* @author    Mike Arnott
* @creation  9 Dec 11
*/
public class BWhoWhenBooleanSetpoint
extends BComponent
{

	public static final Property facets = newProperty(0, BFacets.makeBoolean());
	public BFacets getFacets() {return (BFacets)get(facets); }
	public void setFacets(BFacets v) {set(facets, v); }
	
	
	public static final Property absTimeFacets			= newProperty(0, BFacets.make(BFacets.SHOW_DATE,			BBoolean.make(true), 
	                                          			                              BFacets.SHOW_TIME,			BBoolean.make(true), 
	                                          			                              BFacets.SHOW_MILLISECONDS,	BBoolean.make(true)),null);
	public BFacets getAbsTimeFacets(){ return (BFacets)get(absTimeFacets); }
	public void setAbsTimeFacets(BFacets v){ set(absTimeFacets,v,null); }
	
	public BFacets getSlotFacets(Slot slot)
	{
		if (slot == timeChanged)	return getAbsTimeFacets();
		if (slot == out)			return getFacets();
		return super.getSlotFacets(slot);
	}
	
	
	public static boolean valueCurrent;
	public static String  stringToLog;

	/**The output setpoint*/
	public static final Property out = newProperty(Flags.SUMMARY, new BStatusBoolean());
	public void setOut(BStatusBoolean v) {set(out, v);   }
	public BStatusBoolean getOut() {return (BStatusBoolean)get(out);   }


	/**String showing username of who changed the point*/
	public static final Property changedBy = newProperty(Flags.READONLY + Flags.SUMMARY + Flags.DEFAULT_ON_CLONE, new BStatusString());
	public void setChangedBy(BStatusString v) {set(changedBy, v); }
	public BStatusString getChangedBy() {return (BStatusString)get(changedBy); }

	/**Absolute Time of change*/
	public final static Property timeChanged = newProperty(Flags.SUMMARY + Flags.READONLY+ Flags.DEFAULT_ON_CLONE, BAbsTime.DEFAULT);
	public void setTimeChanged(BAbsTime v) {set(timeChanged, v); }
	public BAbsTime getTimeChanged() {return (BAbsTime)get(timeChanged); }
	
	/**
	* String container information related to when,who,what and where something was change.
	* The thought here was that several of these object could be linked to a single history.
	*/
	public static final Property outLogString = newProperty(0, new BStatusString());
	public BStatusString getOutLogString() { return (BStatusString)get(outLogString);}
	public void setOutLogString(BStatusString v) {set(outLogString,v);}
	
	
	/**invokable action to set the current value to true*/
	public static final Action SetTrue = newAction(Flags.OPERATOR,null);
	public void SetTrue(){invoke(SetTrue,null,null);}
	public void doSetTrue(Context cxin)
	{
		
		valueCurrent = getOut().getValue();
		String by = "logic";
		if(cxin==null)
		{
			//for wiresheet invokes, set username to "logic"
			getChangedBy().setValue(by);
		}
		else 
		{
			//for any user invokes, get the context username
			by = cxin.getUser().getUsername();
			getChangedBy().setValue(by);
		}

		logger.trace("\t\t" + getSlotPath()	+ "\t\tTRIGGERED TRUE BY: " + by);

		//set value and timestamp
		getOut().setValue(true);
		fireValue(BBoolean.make(true));
		fireIsTrue(BBoolean.make(true));

		//some day would love to add override status to this object also...
		getOut().setStatus(BStatus.ok);
		setTimeChanged(BAbsTime.make());
		
		stringToLog	= getTimeChanged().toString(getAbsTimeFacets()) + "," + by + ",FROM: " + valueCurrent + ",TO: " + getOut().getValue() + ",SLOTPATH: " + getSlotPath();
		getOutLogString().setValue(stringToLog);
		fireLogString(BString.make(stringToLog));
	}
	
	/**invokable action to set the current value to false*/
	public static final Action SetFalse = newAction(Flags.OPERATOR,null);
	public void SetFalse(){invoke(SetFalse,null,null);}
	public void doSetFalse(Context cxin)
	{
		valueCurrent = getOut().getValue();
		String by = "logic";
		
		if(cxin==null)
		{
			//for wiresheet invokes, set username to "logic"
			getChangedBy().setValue(by);
		}
		else 
		{
			//for any user invokes, get the context username
			by = cxin.getUser().getUsername();
			getChangedBy().setValue(by);
		}

		logger.trace("\t\t" + getSlotPath()	+ "\t\tTRIGGERED FALSE BY: " + by);	  
		//set value and timestamp
		getOut().setValue(false);
		fireValue(BBoolean.make(false));
		fireIsFalse(BBoolean.make(true));

		//some day would love to add override status to this object also...
		getOut().setStatus(BStatus.ok);
		setTimeChanged(BAbsTime.make());
		
		stringToLog	= getTimeChanged().toString(getAbsTimeFacets()) + "," + by + ",FROM: " + valueCurrent + ",TO: " + getOut().getValue() + ",SLOTPATH: " + getSlotPath();
		getOutLogString().setValue(stringToLog);
		fireLogString(BString.make(stringToLog));
	}
	
	
	/**invokable action to set the current value*/
	public static final Action SetValue = newAction(Flags.OPERATOR,BBoolean.DEFAULT);
	public void SetValue(BBoolean v){invoke(SetValue, null);}
	public void doSetValue(BBoolean v, Context cxin)
	{
		valueCurrent = getOut().getValue();
		String by = "logic";
		
		if(cxin==null)
		{
			//for wiresheet invokes, set username to "logic"
			getChangedBy().setValue(by);
		}
		else 
		{
			//for any user invokes, get the context username
			by = cxin.getUser().getUsername();
			getChangedBy().setValue(by);
		}

		logger.trace("\t\t" + getSlotPath()	+ "\t\tTRIGGERED " + v.getBoolean() + " BY: " + by);
		//set value and timestamp
		getOut().setValue(v.getBoolean());
		fireValue(BBoolean.make(v.getBoolean()));

		if(v.getBoolean()==true) {fireIsTrue(BBoolean.make(true));}
		else {fireIsFalse(BBoolean.make(true));}

		//some day would love to add override status to this object also...
		getOut().setStatus(BStatus.ok);
		setTimeChanged(BAbsTime.make());
		
		stringToLog	= getTimeChanged().toString(getAbsTimeFacets()) + "," + by + ",FROM: " + valueCurrent + ",TO: " + getOut().getValue() + ",SLOTPATH: " + getSlotPath();
		getOutLogString().setValue(stringToLog);
		fireLogString(BString.make(stringToLog));
	}

	
	public static final Topic Value = newTopic(0);
	public void fireValue(BBoolean event){fire(Value,event,null);}
	
		
	public static final Topic IsTrue = newTopic(0);
	public void fireIsTrue(BBoolean event){fire(IsTrue,event,null);}
		
	public static final Topic IsFalse = newTopic(0);
	public void fireIsFalse(BBoolean event){fire(IsFalse,event,null);}
	
	
	public static final Topic LogString = newTopic(0);
	public void fireLogString(BString event){fire(LogString,event,null);}
	

	public static final Log logger = Log.getLog("axCommunity.WhoWhenBooleanSetpoint");


	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");


	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BWhoWhenBooleanSetpoint.class);



	public BValue getActionParameterDefault(Action action)
	{
		if (action == SetValue) 
		return getOut().getValueValue();
		return super.getActionParameterDefault(action);
	}
}
