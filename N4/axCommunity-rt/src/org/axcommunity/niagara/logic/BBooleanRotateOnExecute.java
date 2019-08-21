package org.axcommunity.niagara.logic;

import javax.baja.status.*;
import javax.baja.sys.*;

/**
 *  Use to rotate boolean output from true to false to true to false... when executed, use trigger to fire
 * @author Vance Hensley, pctechs4u
 * @creation May 3, 2009
 *
 */
public class BBooleanRotateOnExecute extends BComponent
{ 
	////////////////////////////////////////////////////////////////
	//Action "execute"
	////////////////////////////////////////////////////////////////

	public static final Action execute = newAction(0,null);
	public void execute() { invoke(execute,null,null); }

	////////////////////////////////////////////////////////////////
	//Property "out"
	////////////////////////////////////////////////////////////////

	public static final Property out = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false, BStatus.ok), null);
	public BStatusBoolean getOut() { return (BStatusBoolean)get(out); }
	public void setOut(BStatusBoolean v) { set(out,v,null); }

	public static final Property outNot = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusBoolean(true, BStatus.ok), null);
	public BStatusBoolean getOutNot() { return (BStatusBoolean)get(outNot); }
	public void setOutNot(BStatusBoolean v) { set(outNot,v,null); }
	
		
	public static final Topic currentValue = newTopic(0);
	public void fireCurrentValue(BBoolean event){fire(currentValue,event,null);}
	
	public static final Topic isTrue = newTopic(0);
	public void fireIsTrue(BBoolean event){fire(isTrue,event,null);}
		
	public static final Topic isFalse = newTopic(0);
	public void fireIsFalse(BBoolean event){fire(isFalse,event,null);}
	
	
	
	////////////////////////////////////////////////////////////////
	//Type
	////////////////////////////////////////////////////////////////

	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BBooleanRotateOnExecute.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/pctechs4u.png");

	/////////////////////////////////////////////////////////////////
	//Begin main code
	/////////////////////////////////////////////////////////////////
	public void doExecute() throws Exception
	{
		if(!Sys.atSteadyState()|| !isRunning())
		{
			return;
		}
		if (getOut().getValue()== true)
		{
			getOut().setValue(false);
			getOutNot().setValue(true);
			fireIsFalse(BBoolean.make(true));
		}
		else
		{
			getOut().setValue(true);
			getOutNot().setValue(false);
			fireIsTrue(BBoolean.make(true));
		}
		
		fireCurrentValue(BBoolean.make(getOut().getValue()));
	}
	/////////////////////////////////////////////////////////////////
	//End main code
	/////////////////////////////////////////////////////////////////
}
