package org.axcommunity.niagara.bql;



import javax.baja.collection.BITable;
import javax.baja.collection.Column;
import javax.baja.collection.ColumnList;
import javax.baja.collection.TableCursor;
import javax.baja.log.Log;
import javax.baja.naming.BOrd;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BRelTime;
import javax.baja.sys.Clock;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;

/**
 * Object executes a BQL query based on the Execute Period for one or more NumericPoint values.  
 * Object calculates the Count, Average, Minimum, Maxium and Sum of the values returned by the BQL query.  
 * If no values are returned by the BQL query the object will set the Avg, Min, Max and Sum to the Fallback value.  
 * If any values return a ‘nan’ they are omitted from the calculation.
 * 
 * NOTE – A valid BQL ord string is required in the ‘In Bql Ord’ slot for the object to execute.  
 * If the slot is empty or the syntax of the query is incorrect, the object will not execute.
 * 
 * Sample Query 1:  station:|slot:/Drivers/BacnetNetwork|bql:select out from control:NumericPoint where displayName = 'ZoneTemp’
 * Sample Query 2:  station:|slot:/Drivers/LonNetwork|bql:select out from control:NumericPoint where displayName like '*nvoAirFlow’

 *
 * @author    Brian W. Collins, Electro Controls, Inc.
 */
public class BBqlNumericRecap extends BComponent {

	//
	//Declare static properties
	//
	/** Manually trigger the BQL expression and calculations.  */
	public static final Action queryAndCalculate = newAction(0|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	/** Time interval at which object executes BQL query and calculates values.  */
	public static final Property executePeriod = newProperty(Flags.SUMMARY,  BRelTime.make(60000));
	/** BQL query string (baja:StatusString)  */
	public static final Property inBqlOrd = newProperty(Flags.SUMMARY,  new BStatusString());
	/** Value is true when object is actively running BQL expression and calculations.  */
	public final static Property calculating = newProperty(0|Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false));
	/** The last time the BQL expression was calculated.  */
	public static final Property lastQuery = newProperty(0|Flags.DEFAULT_ON_CLONE, BAbsTime.make(0), BFacets.make("showMilliseconds",true));
	/** Number/Count of values returned by BQL query (baja:StatusNumeric)  */
	public static final Property num = newProperty(Flags.SUMMARY,  new BStatusNumeric());
	/** Average of values returned by BQL query (baja:StatusNumeric)  */
	public static final Property avg = newProperty(Flags.SUMMARY,  new BStatusNumeric());
	/** Minimum value returned by BQL query (baja:StatusNumeric)  */
	public static final Property min = newProperty(Flags.SUMMARY,  new BStatusNumeric());
	/** Maximum value returned by BQL query (baja:StatusNumeric)  */
	public static final Property max = newProperty(Flags.SUMMARY,  new BStatusNumeric());
	/** Sum of all values returned by BQL query (baja:StatusNumeric)  */
	public static final Property sum = newProperty(Flags.SUMMARY,  new BStatusNumeric());
	/** Value is returned is no values are returned by BQL query (baja:StatusNumeric)  */
	public static final Property fallback = newProperty(Flags.SUMMARY,  new BStatusNumeric());
	/** Value is true if an error occurs in query or calculations. */
	public final static Property error = newProperty(0|Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false));
	/** Value is fired true when calculation is complete. */
	public static final Topic calculated = newTopic(0);
	public void fireCalculated(BBoolean event){fire(calculated,event,null);}
	/** Value is fired true when there is an error in the calculation. */
	public static final Topic errorOccured = newTopic(0);
	public void fireErrorOccured(BBoolean event){fire(errorOccured,event,null);}
	

	
	


	
	
	
	
	////////////////////////////////////////////////////////////////
	// Getters
	////////////////////////////////////////////////////////////////

	public BRelTime			getExecutePeriod()	{ return (BRelTime)get("executePeriod"); }
	public BStatusString	getInBqlOrd()		{ return (BStatusString)get("inBqlOrd"); }
	public BStatusBoolean	getCalculating()	{ return (BStatusBoolean)get(calculating); }
	public BAbsTime			getLastQuery()		{ return (BAbsTime)get(lastQuery); }
	public BStatusNumeric	getNum()			{ return (BStatusNumeric)get("num"); }
	public BStatusNumeric	getAvg()			{ return (BStatusNumeric)get("avg"); }
	public BStatusNumeric	getMin()			{ return (BStatusNumeric)get("min"); }
	public BStatusNumeric	getMax()			{ return (BStatusNumeric)get("max"); }
	public BStatusNumeric	getSum()			{ return (BStatusNumeric)get("sum"); }
	public BStatusNumeric	getFallback()		{ return (BStatusNumeric)get("fallback"); }
	public BStatusBoolean	getError()			{ return (BStatusBoolean)get(error); }

	////////////////////////////////////////////////////////////////
	// Setters
	////////////////////////////////////////////////////////////////

	public void setExecutePeriod(javax.baja.sys.BRelTime v) { set("executePeriod", v); }
	public void setInBqlOrd(javax.baja.status.BStatusString v) { set("inBqlOrd", v); }
	public void setCalculating(BStatusBoolean v) { set(calculating, v); }
	public void setLastQuery(BAbsTime v) { set(lastQuery, v); }
	public void setNum(javax.baja.status.BStatusNumeric v) { set("num", v); }
	public void setAvg(javax.baja.status.BStatusNumeric v) { set("avg", v); }
	public void setMin(javax.baja.status.BStatusNumeric v) { set("min", v); }
	public void setMax(javax.baja.status.BStatusNumeric v) { set("max", v); }
	public void setSum(javax.baja.status.BStatusNumeric v) { set("sum", v); }
	public void setFallback(javax.baja.status.BStatusNumeric v) { set("fallback", v); }
	public void setError(BStatusBoolean v) { set(error, v); }

	//events for the timer
	/**
	 * Slot for the <code>timerExpired</code> action.
	 */
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);

	/**
	 * Invoke the <code>timerExpired</code> action.
	 */
	public void timerExpired() { invoke(timerExpired,null,null); }

	

	public void queryAndCalculate(){invoke(queryAndCalculate,null,null);}
	public void doQueryAndCalculate()
	{
		calculate();
	}



	//////////////////////////////////////////////////////////////
	// Program Source
	////////////////////////////////////////////////////////////////


	// MinMaxAvgSum - Points Selected by BQL
	// Daniel Drury / 2007
	// Brian Collins / 2008 - added code to account for invalid NaN values on LonNetwork and calculate the sum of the columns
	//
	// Return BQL result with 1st column is a BStatusNumeric
	// Edit property sheet with BQL, get BQL by using query builder
	// Example: Avg all Numeric Points in station
	//  station:|slot:/|bql:select out from control:NumericPoint
	// Example: Avg all Numeric Points starting with ZN-T
	//  station:|slot:/|bql:select out from control:NumericPoint where displayName like 'ZN-T*'

	public void started() throws Exception
	{
		updateTimer();
	}


	public void doTimerExpired() throws Exception  //renamed from onExecute
	{
		calculate();
	}
	
	public void calculate()
	{
		try
		{
			getError().setValue(false);
			getCalculating().setValue(true);
			BStatusString bqlord = getInBqlOrd();
			String ord = bqlord.getValue();
			BStatusNumeric Value;

			double sum=0;
			double num=0;
			double avg=0;
			double min=0;
			double max=0;
			double first=1;

			updateTimer(); 

			BITable result = (BITable)BOrd.make(ord).resolve(Sys.getStation()).get();   // execute bql into table

			ColumnList columns = result.getColumns();				// get table cols data
			TableCursor c = (TableCursor)result.cursor();			// setup table cursor
			first = 1;												// init stuff on 1st valid found 
			num = 0;												// init number points found

			while (c.next())										// walk bql rows
			{
				Column valueColumn = columns.get(0);				// data is in 1st col
				Value = (BStatusNumeric) c.get(valueColumn);		// get BStatusNumeric from table
				if ( BDouble.make(Value.getValue()) != BDouble.NaN)	// check to see if value=NaN, if yes then omit from calculation
					if (Value.getStatus().isValid()) {				// get Status of point, check valid
						if (first != 0) {							// 1st valid point, use it for everything
							min = Value.getValue();
							max = Value.getValue();
							avg = Value.getValue();
							sum = Value.getValue();
							num = 1;								// Init number points found
							first = 0;
							//data.setValue(good);
						} else {									// 2nd+ valid point, check min/max, and avg it
							if (Value.getValue() <= min) min = Value.getValue();
							if (Value.getValue() >= max) max = Value.getValue();
							avg += Value.getValue();
							sum+= Value.getValue();
							num++;
						}
					}          
			}

			getNum().setValue(num);       // set number points found
			if (num > 0) {                // only use avg/min/max if something valid found
				if (num != 0) getAvg().setValue(avg/num);
				getMin().setValue(min);
				getMax().setValue(max);
				getSum().setValue(sum);
			} else {                      // No Valid Return, Return Default
				getAvg().setValue(getFallback().getValue());
				getMin().setValue(getFallback().getValue());
				getMax().setValue(getFallback().getValue());
				getSum().setValue(getFallback().getValue());
			}
			getCalculating().setValue(false);
			setLastQuery(BAbsTime.make());
			fireCalculated(BBoolean.make(true));
		}
		catch (Exception e) 
		{
			getError().setValue(true);
			fireErrorOccured(BBoolean.make(true));
			logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
		}		
	}


	public void stopped() throws Exception
	{
		if (ticket != null) ticket.cancel();
	}

	void updateTimer()
	{            
		if (ticket != null) ticket.cancel();
		//ticket = Clock.schedule(getProgram(), getExecutePeriod(), BProgram.execute, null);
		ticket = Clock.schedulePeriodically(this, getExecutePeriod(), timerExpired, null);
	}    

	Clock.Ticket ticket;
	long lastOnExecuteTicks;

	 
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/electro_diamond_header.png");

	public static final Log logger = Log.getLog("axCommunity.BqlNumericRecap");

	//TYPE declaration
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BBqlNumericRecap.class);

}


