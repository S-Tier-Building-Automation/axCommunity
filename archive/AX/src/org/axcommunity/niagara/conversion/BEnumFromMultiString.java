package org.axcommunity.niagara.conversion;

import javax.baja.control.BEnumWritable;
import javax.baja.log.Log;
import javax.baja.naming.SlotPath;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;

/**
 * An extension of an enumwritable that allows you to specify a number if
 * statusstring input slots that can be used to build the enum dynamically.
 * Be careful with the string - we still have to follow enum validation rules -
 * no duplicates, no nulls.  If the object detects either of these, it truncates
 * to that point in the enum range facets.
 *
 * @author Mike Arnott, Kors Engineering
 */
public class BEnumFromMultiString
extends BEnumWritable
{
	//----------------------------------------------------------------------------------------------------------
	/** ACTION, "VariableCount", SETS THE NUMBER OF STRING INPUTS */ /////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final Action VariableCount = newAction(0, (BValue) BDouble.TYPE.getInstance(), null);
	public void VariableCount(BDouble v){invoke(VariableCount, v, null);}
	public void doVariableCount(BDouble v) throws Exception
	{
		try
		{
			onVariableCount(v);
		}
		catch (Throwable t)
		{
			throw new Exception(t);
		}
	}


	/** STATUS NUMERIC INPUT, "numberOfSlots", NUMBER OF INPUT STRING SLOTS TO HAVE */ ////////////////////
	public static final Property numberOfSlots = newProperty(0, new BStatusNumeric(), null);
	public BStatusNumeric getNumberOfSlots(){return (BStatusNumeric) get(numberOfSlots);}
	public void setNumberOfSlots(BStatusNumeric v){set(numberOfSlots, v, null);}

	/** STATUS NUMERIC OUTPUT, "numberOfValues", NUMBER OF INPUT STRING SLOTS */ ///////////////////////////
	public static final Property numberOfValues = newProperty(0 | Flags.HIDDEN | Flags.READONLY, new BStatusNumeric(), null);
	public BStatusNumeric getNumberOfValues(){return (BStatusNumeric) get(numberOfValues);}
	public void setNumberOfValues(BStatusNumeric v){set(numberOfValues, v, null);}

	public static final Property outString = newProperty(Flags.SUMMARY, new BStatusString());
    public BStatusString getOutString() { return (BStatusString)get(outString);}
    public void setOutString(BStatusString v) {set(outString,v);}
	
	public static final Property outNumeric  = newProperty(Flags.SUMMARY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutNumeric() {return (BStatusNumeric) get(outNumeric); }
	public void setOutNumeric(BStatusNumeric v) {set(outNumeric, v);}
	
	
	//----------------------------------------------------------------------------------------------------------
	/** DETERMINES HOW MANY NEW SLOTS TO CREATE.   */
	public void onVariableCount(BDouble NV) throws Exception
	{
		if (NV.getDouble() > 60.0) getNumberOfValues().setValue(60.0);
		else getNumberOfValues().setValue(NV.getDouble());
		slots(getNumberOfValues().getValue());
	}

	//----------------------------------------------------------------------------------------------------------
	public void changed(Property p, Context cx)
	{
		if (!Sys.atSteadyState() || !isRunning()) return;

		if (isRunning())
		{
			logger.trace("\t" + getSlotPath() + "\t" + "changed:" + p.getName());
			
			
			//**************************************************************************************************
			if (p == numberOfSlots)
			{
				try
				{
					onVariableCount(BDouble.make(getNumberOfSlots().getValue()));
				}
				catch (Exception e)
				{
					logger.error("\n" + getSlotPath() + "\n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace()); 
				}
			}
			
			//**************************************************************************************************
			if (p.getName().startsWith("In_"))
			{
				logger.trace("\t" + getSlotPath() + "\t" + "in slot changed:" + p.getName());
				calculate();
			}
			
			
			//**************************************************************************************************
			if (p == out) 
			{
				if(getOut().getStatus().isValid()==true)
				{
					try
					{
						getOutString().setValue(((BEnumRange)this.getFacets().get("range")).getDisplayTag(this.getOut().getValue().getOrdinal(), null));
						
						if(getOutString().getStatus().isValid()==false)
						{
							getOutString().setStatus(BStatus.ok);
						}
					}
					catch (Exception e)
					{
						setOutString(new BStatusString("", BStatus.nullStatus));
					}
					
					
					try					
					{
						getOutNumeric().setValue((double)this.getOut().getValue().getOrdinal());
						
						if(getOutNumeric().getStatus().isValid()==false)
						{
							getOutNumeric().setStatus(BStatus.ok);
						}
					}
					catch (Exception e)	
					{
						setOutNumeric(new BStatusNumeric(0, BStatus.nullStatus));
					}
				}
				else
				{
					setOutString(new BStatusString("", BStatus.nullStatus));
					setOutNumeric(new BStatusNumeric(0, BStatus.nullStatus));
				}
				
			}
		}
	}
		
	//----------------------------------------------------------------------------------------------------------
	/** CREATES THE REQUIRED SLOTS.   */
	public void slots(double MD) throws Exception
	{
		for (int i = 1; i < (MD + 1); i++)
		{
			if (((BObject) get("In_" + i)) == null)
			{
				this.add(("In_" + i), new BStatusString(""), Flags.SUMMARY);
			}
		}

		for (int i = (int) MD + 1;
			((BObject) get("In_" + i)) != null; i++)
		{
			if (((BObject) get("In_" + i)) != null)
			{
				this.remove("In_" + i);
			}
		}
	}

	//----------------------------------------------------------------------------------------------------------
	private void calculate()
	{
		String[]	tokVals	= new String[(int) getNumberOfValues().getValue() + 1];
		int[]		tokKeys	= new int[(int) getNumberOfValues().getValue() + 1];
		BComponent	comp	= this;
		tokVals[0]			= "No$20Selection";
		tokKeys[0]			= 0;
		
		for (int i = 1; i < tokVals.length; i++)
		{
			tokKeys[i]			= i;
			BStatusString	inx	= (BStatusString) comp.get("In_" + (i));
			
			if (inx.getStatus().isNull())
			{
				tokVals[i] = "";
			}
			else
			{
				tokVals[i] = SlotPath.escape(inx.getValue());
			}
		}

		//check for duplicates, nulls, end prematurely and truncate if so.
		boolean		badkey		= false;
		String[]	newEnumVals	= new String[0];
		int[]		newEnumKeys	= new int[0];
		
		for (int i = 0; i < tokVals.length; i++)
		{
			if (tokVals[i] != "")
			{
				if (badkey) break;
				for (int j = 0; j < i; j++)
				{
					if (i != j)
					{
						if (tokVals[i].compareTo(tokVals[j]) == 0)
						{
							//strings match!  end at i-1, terminate and truncate
							newEnumVals	= copyPartialStringArray(tokVals, i - 1);
							newEnumKeys	= copyPartialIntArray(tokKeys, i - 1);
							badkey		= true;
							break;
						}
					}
				}

			}
			else
			{
				//string is null, terminate and truncate
				newEnumVals	= copyPartialStringArray(tokVals, i);
				newEnumKeys	= copyPartialIntArray(tokKeys, i);
				badkey		= true;
				break;
			}
		}

		if (!badkey)
		{
			newEnumVals	= copyPartialStringArray(tokVals, tokVals.length);
			newEnumKeys	= copyPartialIntArray(tokKeys, tokKeys.length);
		}

		//create enum range facets
		BEnumRange	range	= BEnumRange.make(newEnumKeys, newEnumVals);
		BFacets		bf		= BFacets.makeEnum(range);
		//add multiline back in to the facets
		bf					= BFacets.make(bf, BFacets.make("multiLine", true));
		this.setFacets(bf);

	}

	//----------------------------------------------------------------------------------------------------------
	private String[] copyPartialStringArray(String[] inArray, int count)
	{
		logger.trace("\t" + getSlotPath() + "\t" + "calling copy partial strings, size " + count);
		
		String[] ret = new String[count];
		
		for (int i = 0; i < count; i++)
		{
			ret[i] = inArray[i];
		}
		
		return ret;
	}
	
	//----------------------------------------------------------------------------------------------------------
	private int[] copyPartialIntArray(int[] inArray, int count)
	{
		logger.trace("\t" + getSlotPath() + "\t" + "calling copy partial ints, size " + count);
		
		int[] ret = new int[count];
		
		for (int i = 0; i < count; i++)
		{
			ret[i] = inArray[i];
		}
		
		return ret;
	}
	
	//----------------------------------------------------------------------------------------------------------
	public BIcon getIcon(){return icon;}
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BEnumFromMultiString.class);
	public Type getType(){return TYPE;}

	public static final Log logger = Log.getLog("axCommunity.BEnumFromMultiString");

}