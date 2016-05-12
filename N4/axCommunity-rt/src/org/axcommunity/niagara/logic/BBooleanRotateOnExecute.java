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

	public static final Action execute = newAction(Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void execute() { invoke(execute,null,null); }

	////////////////////////////////////////////////////////////////
	//Property "out"
	////////////////////////////////////////////////////////////////

	public static final Property out = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false),null);
	public BStatusBoolean getOut() { return (BStatusBoolean)get(out); }
	public void setOut(BStatusBoolean v) { set(out,v,null); }

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
		}
		else
		{
			getOut().setValue(true);
		}
	}
	/////////////////////////////////////////////////////////////////
	//End main code
	/////////////////////////////////////////////////////////////////
}
