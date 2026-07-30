/* SPDX-License-Identifier: GPL-2.0-only */

package org.axcommunity.niagara.math;

import javax.baja.collection.*;
import javax.baja.control.*;
import javax.baja.naming.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.units.BUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Numeric values are stored in a stack of 33 values, on transition
 * of trigger from false to true the values 00 through 32 are shifted
 * by 1 and the value of the input written as the new value for 00
 * 
 * @author Mike Arnott, Kors Engineering
 */
@SuppressWarnings("rawtypes")
public class BStatusNumericFifo extends BComponent
{
	
	boolean					fire		= false;
	// Instance-scoped: two FIFO instances must not race on one shared array.
	final String[]			slotNames	= new String[MAXSLOTS];
	static final int		MAXSLOTS	= 33;
	BLink[]					linked;
	BUnit					degrees;
	BFacets					bfc;
	static final String		INPUT		= "Input", MAX = "Max$20Value", MIN = "Min$20Value", AVG = "Average";

	private static final Logger logger = Logger.getLogger("axCommunity.StatusNumericFifo");
	
	public void started()
	{
		try
		{
			super.started();
			setNames();
			if (!getSlot(INPUT).isProperty())
			{
				// Slot Does not Exist
				add(INPUT, new BStatusNumeric(0), Flags.SUMMARY);
				add(MAX, new BStatusNumeric(0), Flags.SUMMARY);
				add(MIN, new BStatusNumeric(0), Flags.SUMMARY);
				add(AVG, new BStatusNumeric(0), Flags.SUMMARY);
				addSlots();
			}
		}
		catch (NullPointerException en)
		{
			add(INPUT, new BStatusNumeric(0), Flags.SUMMARY);
			add(MAX, new BStatusNumeric(0), Flags.SUMMARY);
			add(MIN, new BStatusNumeric(0), Flags.SUMMARY);
			add(AVG, new BStatusNumeric(0), Flags.SUMMARY);
			addSlots();
			
		}
		catch (Exception ex)
		{
			logger.log(Level.SEVERE, "Kors Component Error at " + this.getSlotPath() + ": " + ex.getMessage(), ex);
		}
	}
	
	private void setNames()
	{
		for (int x = 0; x < slotNames.length; ++x)
		{
			if (x < 10)
			{
				slotNames[x] = "Output$200" + Integer.toString(x);
			}
			else
			{
				slotNames[x] = "Output$20" + Integer.toString(x);
			}
		}
	}
	
	private void addSlots()
	{
		
		for (int x = 0; x < slotNames.length; ++x)
		{
			if (slotNames[x] == null)
			{
				setNames();
			}
			if (x < 10)
			{
				add(slotNames[x], new BStatusNumeric(0));
			}
			else
			{
				add(slotNames[x], new BStatusNumeric(0));
			}
		}
		
	}
	
	
	public void changed(Property property, Context context)
	{
		if (!isRunning() || !Sys.atSteadyState())
		{ return; }
		super.changed(property, context);
		
		for (int x = 0; x < slotNames.length; ++x)
		{
			if (slotNames[x] == null)
			{
				setNames();
			}
		}
		
		if (property == trigger && getTrigger().getValue() && fire == false)
		{
			try
			{
				linked	= getLinks(getSlot(INPUT));
				for (int x = 0; x < linked.length; ++x)
				{
					// Read the linked component directly. The previous BQL query
					// (displayName string-concatenated into the where clause and
					// resolved synchronously on the engine thread) was fragile,
					// injectable via crafted component names, and unnecessary.
					BComponent bCom = linked[x].getSourceComponent();
					if (!(bCom instanceof BControlPoint))
					{
						((BStatusNumeric) get(getProperty(INPUT))).setStatus(BStatus.fault);
						continue;
					}
					BControlPoint	point	= (BControlPoint) bCom;
					BStatusValue	out		= point.getOutStatusValue();
					if (out instanceof BStatusNumeric)
					{
						double	value	= ((BStatusNumeric) out).getValue();
						BFacets	facets	= point.getFacets();
						if (facets == null)
						{
							shiftData(value);
							minData();
							maxData();
							avgData();
						}
						else
						{
							shiftData(value, facets);
							minData(facets);
							maxData(facets);
							avgData(facets);
						}
					}
					else
					{
						// Non-numeric source cannot be buffered.
						((BStatusNumeric) get(getProperty(INPUT))).setStatus(BStatus.fault);
					}
				}
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "Kors Component Error at " + this.getSlotPath() + ": " + e.getMessage(), e);
			}
			finally
			{
				fire = true;
			}
		}
		else
		{
			fire = false;
			
		}
	}
	
	private void shiftData(double value, BFacets bfc)
	{
		double tempVal;
		for (int x = slotNames.length - 2; x >= 0; --x)
		{
			tempVal = ((BStatusNumeric) get(getProperty(slotNames[x]))).getValue();
			set(getProperty(slotNames[x + 1]), new BStatusNumeric(tempVal));
			setFacets(getSlot(slotNames[x + 1]), bfc);
		}
		set(getProperty(slotNames[0]), new BStatusNumeric(value));
		setFacets(getSlot(INPUT), bfc);
		setFacets(getSlot(slotNames[0]), bfc);
	}
	
	private void shiftData(double value)
	{
		double tempVal;
		for (int x = slotNames.length - 2; x >= 0; --x)
		{
			tempVal = ((BStatusNumeric) get(getProperty(slotNames[x]))).getValue();
			set(getProperty(slotNames[x + 1]), new BStatusNumeric(tempVal));
		}
		set(getProperty(slotNames[0]), new BStatusNumeric(value));
	}
	
	private void minData(BFacets bfc)
	{
		set(getProperty(MIN), new BStatusNumeric(minValue()));
		setFacets(getSlot(MIN), bfc);
	}

	private void minData()
	{
		set(getProperty(MIN), new BStatusNumeric(minValue()));
	}

	private void maxData(BFacets bfc)
	{
		set(getProperty(MAX), new BStatusNumeric(maxValue()));
		setFacets(getSlot(MAX), bfc);
	}

	private void maxData()
	{
		set(getProperty(MAX), new BStatusNumeric(maxValue()));
	}

	private void avgData(BFacets bfc)
	{
		set(getProperty(AVG), new BStatusNumeric(avgValue()));
		setFacets(getSlot(AVG), bfc);
	}

	private void avgData()
	{
		set(getProperty(AVG), new BStatusNumeric(avgValue()));
	}

	// Min/max/avg are computed over ALL buffered values. The previous versions
	// used 0 as a sentinel and counted only positive values, so any buffer
	// containing negatives (or an all-negative buffer) reported wrong stats.
	private double minValue()
	{
		double min = ((BStatusNumeric) get(getProperty(slotNames[0]))).getValue();
		for (int x = 1; x < slotNames.length; ++x)
		{
			double v = ((BStatusNumeric) get(getProperty(slotNames[x]))).getValue();
			if (v < min) min = v;
		}
		return min;
	}

	private double maxValue()
	{
		double max = ((BStatusNumeric) get(getProperty(slotNames[0]))).getValue();
		for (int x = 1; x < slotNames.length; ++x)
		{
			double v = ((BStatusNumeric) get(getProperty(slotNames[x]))).getValue();
			if (v > max) max = v;
		}
		return max;
	}

	private double avgValue()
	{
		double sum = 0;
		for (int x = 0; x < slotNames.length; ++x)
		{
			sum += ((BStatusNumeric) get(getProperty(slotNames[x]))).getValue();
		}
		return sum / slotNames.length;
	}
	
	/*@formatter:off*/
    public static final Property trigger = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger); }
    public void setTrigger(BStatusBoolean v) { set(trigger, v); }

    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

    public static final Type TYPE = Sys.loadType(BStatusNumericFifo.class);
    public Type getType() { return TYPE; }
    /*@formatter:on*/
	
}
