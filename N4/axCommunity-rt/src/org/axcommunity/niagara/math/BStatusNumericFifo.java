package org.axcommunity.niagara.math;

import javax.baja.collection.*;
import javax.baja.control.*;
import javax.baja.naming.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.units.BUnit;
/**
 * Numeric values are stored in a stack of 33 values, on transition
 * of trigger from false to true the values 00 through 32 are shifted
 * by 1 and the value of the input written as the new value for 00
 * @author Mike Arnott, Kors Engineering
 */
@SuppressWarnings("rawtypes")
public class BStatusNumericFifo extends BComponent 
{

	boolean fire = false;
    static String [] slotNames;
    final int MAXSLOTS = 33;
    BLink [] linked;
    BUnit degrees;
    BFacets bfc;
    static final String INPUT = "Input", MAX = "Max$20Value",
    	MIN = "Min$20Value", AVG = "Average";
    static BStatusNumeric bsn;

	public void started(){
		try{
			slotNames = new String[MAXSLOTS];
			setNames();
			if (!getSlot(INPUT).isProperty()){
			//Slot Does not Exist
				add(INPUT, new BStatusNumeric(0), Flags.SUMMARY);
				add(MAX, new BStatusNumeric(0), Flags.SUMMARY);
				add(MIN, new BStatusNumeric(0), Flags.SUMMARY);
				add(AVG, new BStatusNumeric(0), Flags.SUMMARY);
				addSlots();
			}
	}
		catch(NullPointerException en){
			add(INPUT, new BStatusNumeric(0), Flags.SUMMARY);
			add(MAX, new BStatusNumeric(0), Flags.SUMMARY);
			add(MIN, new BStatusNumeric(0), Flags.SUMMARY);
			add(AVG, new BStatusNumeric(0), Flags.SUMMARY);
			addSlots();

		}
		catch(Exception ex){
			System.out.println(BAbsTime.now().toString() +   ": Kors Component Error at " + this.getSlotPath().toString() + ":" + ex.toString());
		}
    }
	private void setNames(){
        for (int x = 0; x < slotNames.length; ++x) {
            if ( x < 10) {
                slotNames[x] = "Output$200" + Integer.toString(x);
            } else {
                slotNames[x] = "Output$20" + Integer.toString(x);
            }
        }
	}

	private void addSlots(){

        for (int x = 0; x < slotNames.length; ++x) {
           	if(slotNames[x]==null){
        		setNames();
        	}
           if ( x < 10) {
                 add(slotNames[x], new BStatusNumeric(0));
            } else {
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
			// linked = new BLink[getLinks(getSlot("input")).length];
			// linked = getLinks(getSlot("input"));
			try
			{
				linked = new BLink[getLinks(getSlot(INPUT)).length];
				linked = getLinks(getSlot(INPUT));
				for (int x = 0; x < linked.length; ++x)
				{
					BComponent bCom = new BComponent();
					bCom = linked[x].getSourceComponent();
					int ordEnd = bCom.getNavOrd().toString().indexOf(bCom.getName());
					String ordStr = bCom.getNavOrd().toString().substring(7, ordEnd - 1);
					BOrd ord = BOrd.make(ordStr + "|bql:select from control:ControlPoint where displayName = '" + bCom.getName() + "'");
					BITable result = (BITable) ord.resolve().get();
					try(Cursor c = result.cursor())
					{
						while (c.next())
						{
							BControlPoint point = (BControlPoint) c.get();
							BStatusValue out = point.getOutStatusValue();
							if (out instanceof BStatusString)
							{
								((BStatusNumeric) get(getProperty(INPUT))).setStatus(BStatus.fault);
							}
							if (out instanceof BStatusBoolean)
							{
								((BStatusNumeric) get(getProperty(INPUT))).setStatus(BStatus.fault);
							}
							if (out instanceof BStatusEnum)
							{
								((BStatusNumeric) get(getProperty(INPUT))).setStatus(BStatus.fault);
							}
							if (out instanceof BStatusNumeric)
							{
								double value = ((BStatusNumeric) out).getValue();
								BFacets bfc = point.getFacets();
								// System.out.println(bfc == null);
								if (bfc == null)
								{
									shiftData(value);
									minData();
									maxData();
									avgData();
								}
								else
								{
									shiftData(value, bfc);
									minData(bfc);
									maxData(bfc);
									avgData(bfc);
								}
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				System.out.println(BAbsTime.now().toString() + ": Kors Component Error at " + this.getSlotPath().toString() + ":" + e.toString());
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
	
    private void shiftData(double value, BFacets bfc){
    	double tempVal;
		for (int x = slotNames.length - 2; x >= 0; --x){
			tempVal = ((BStatusNumeric)get(getProperty(slotNames[x]))).getValue();
			set(getProperty(slotNames[x + 1]), new BStatusNumeric(tempVal));
			setFacets(getSlot(slotNames[x + 1]), bfc);
		}
		set(getProperty(slotNames[0]), new BStatusNumeric(value));
		setFacets(getSlot(INPUT), bfc);
		setFacets(getSlot(slotNames[0]), bfc);
    }
    private void shiftData(double value){
    	double tempVal;
		for (int x = slotNames.length - 2; x >= 0; --x){
			tempVal = ((BStatusNumeric)get(getProperty(slotNames[x]))).getValue();
			set(getProperty(slotNames[x + 1]), new BStatusNumeric(tempVal));
		}
		set(getProperty(slotNames[0]), new BStatusNumeric(value));
    }

    private void minData(BFacets bfc){
    	double tempVal, minVal;
    	tempVal = minVal = 0;
    	for (int x = 0; x < slotNames.length; ++x){
    		tempVal = ((BStatusNumeric)get(getProperty(slotNames[x]))).getValue();
    		if (minVal == 0){
    			minVal = tempVal;
    		} else {
    			if (tempVal > 0 && tempVal < minVal){
    			minVal = tempVal;
    			}
    		}
    	}
    	set(getProperty(MIN), new BStatusNumeric(minVal));
    	setFacets(getSlot(MIN), bfc);
    }
    private void minData(){
    	double tempVal, minVal;
    	tempVal = minVal = 0;
    	for (int x = 0; x < slotNames.length; ++x){
    		tempVal = ((BStatusNumeric)get(getProperty(slotNames[x]))).getValue();
    		if (minVal == 0){
    			minVal = tempVal;
    		} else {
    			if (tempVal > 0 && tempVal < minVal){
    			minVal = tempVal;
    			}
    		}
    	}
    	set(getProperty(MIN), new BStatusNumeric(minVal));
   }
    private void maxData(BFacets bfc){
    	double tempVal, maxVal;
    	tempVal = maxVal = 0;
    	for (int x = 0; x < slotNames.length; ++x){
    		tempVal = ((BStatusNumeric)get(getProperty(slotNames[x]))).getValue();
    		if (maxVal == 0){
    			maxVal = tempVal;
    		} else {
    			if (tempVal > 0 && tempVal > maxVal){
    				maxVal = tempVal;
    			}
    		}
    	}
    	set(getProperty(MAX), new BStatusNumeric(maxVal));
    	setFacets(getSlot(MAX), bfc);
    }
    private void maxData(){
    	double tempVal, maxVal;
    	tempVal = maxVal = 0;
    	for (int x = 0; x < slotNames.length; ++x){
    		tempVal = ((BStatusNumeric)get(getProperty(slotNames[x]))).getValue();
    		if (maxVal == 0){
    			maxVal = tempVal;
    		} else {
    			if (tempVal > 0 && tempVal > maxVal){
    				maxVal = tempVal;
    			}
    		}
    	}
    	set(getProperty(MAX), new BStatusNumeric(maxVal));
    }
    private void avgData(BFacets bfc){
    	double tempVal, avgVal, checkVal, goodVals;
    	tempVal = avgVal = checkVal = goodVals = 0;
    	for (int x = 0; x < slotNames.length; ++x){
    		checkVal = ((BStatusNumeric)get(getProperty(slotNames[x]))).getValue();
    		tempVal += checkVal;
    		if (checkVal > 0){
    			goodVals += 1;
    		}
    	}
    	if(goodVals>0){
    	  avgVal = tempVal / goodVals;
    	}
    	else
    	{
    	  avgVal = 0;
    	}
    	set(getProperty(AVG), new BStatusNumeric(avgVal));
    	setFacets(getSlot(AVG), bfc);
    }
    private void avgData(){
    	double tempVal, avgVal, checkVal, goodVals;
    	tempVal = avgVal = checkVal = goodVals = 0;
    	for (int x = 0; x < slotNames.length; ++x){
    		checkVal = ((BStatusNumeric)get(getProperty(slotNames[x]))).getValue();
    		tempVal += checkVal;
    		if (checkVal > 0){
    			goodVals += 1;
    		}
    	}
      if(goodVals>0){
        avgVal = tempVal / goodVals;
      }
      else
      {
        avgVal = 0;
      }
      set(getProperty(AVG), new BStatusNumeric(avgVal));
    }

    public static final Property trigger = newProperty(Flags.SUMMARY, new BStatusBoolean(false));

    public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger); }

    public void setTrigger(BStatusBoolean v) { set(trigger, v); }

    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

    public static final Type TYPE = Sys.loadType(BStatusNumericFifo.class);
    public Type getType() { return TYPE; }
}
