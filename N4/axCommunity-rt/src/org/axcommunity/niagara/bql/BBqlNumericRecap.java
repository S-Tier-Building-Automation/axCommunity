package org.axcommunity.niagara.bql;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.baja.collection.BITable;
import javax.baja.collection.Column;
import javax.baja.collection.ColumnList;
import javax.baja.collection.TableCursor;
import javax.baja.naming.BOrd;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;


/**
 * Object executes a BQL query based on the Execute Period for one or more NumericPoint values.
 * Object calculates the Count, Average, Minimum, Maximum and Sum of the values returned by the BQL query.
 * If no values are returned by the BQL query the object will set the Avg, Min, Max and Sum to the Fallback value.
 * If any values return a NaN they are omitted from the calculation.
 *
 * NOTE  A valid BQL ord string is required in the In Bql Ord slot for the object to execute.
 * If the slot is empty or the syntax of the query is incorrect, the object will not execute.
 *
 * Sample Query 1:  station:|slot:/Drivers/BacnetNetwork|bql:select out from control:NumericPoint where displayName = 'ZoneTemp
 * Sample Query 2:  station:|slot:/Drivers/LonNetwork|bql:select out from control:NumericPoint where displayName like '*nvoAirFlow

 *
 * @author    Brian W. Collins, Electro Controls, Inc.
 *
 * Update 6/29/2017 by James Johnson to move to current logger syntax
 */
public class BBqlNumericRecap extends BComponent
{
	private double startTime = 0;
	
	public static final Property facets = newProperty(0, BFacets.make(BFacets.PRECISION, BInteger.make(2), BFacets.FIELD_WIDTH, BInteger.make(50)),null);
	public BFacets getFacets() { return (BFacets) get(facets); }
	public void setFacets(BFacets v) { set(facets, v, null); }

	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * Applies the appropriate facets to the specified slots.
	 */
	public BFacets getSlotFacets(Slot slot)
	{
		try
		{
			Type type = null;
			
			if(slot.isAction())
			{
				try{ type = this.getAction( com.tridium.util.EscUtil.slot.escape(slot.getName()) ).getParameterDefault().getType(); }
				catch (Exception e){}
			}
			else
			{
				try{ type = this.get( com.tridium.util.EscUtil.slot.escape(slot.getName()) ).getType(); }
				catch (Exception e) {}
			}
			
			
			try
			{
				if( !(type==null) )
				{
					if (type == BDouble.TYPE || type == BFloat.TYPE || type == BInteger.TYPE || type == BStatusNumeric.TYPE)
					{
						if(getFacets().isEmpty())	{ return slot.getFacets();		}
						else						{ return getFacets();			}
					}
				}
			}
			catch (Exception e){}
		}
		catch (Exception e){}
		
		return super.getSlotFacets(slot);
	}
	
	public static final Property debug = newProperty(0, new BStatusBoolean(false, BStatus.ok), null);
	public BStatusBoolean getDebug() { return (BStatusBoolean)get(debug); }
	public void setDebug(BStatusBoolean v) { set(debug, v, null); }
	
	public static final Property updateOnBqlChange = newProperty(0, new BStatusBoolean(false, BStatus.ok), null);
	public BStatusBoolean getUpdateOnBqlChange() { return (BStatusBoolean)get(updateOnBqlChange); }
	public void setUpdateOnBqlChange(BStatusBoolean v) { set(updateOnBqlChange, v, null); }
	
	/** Manually trigger the BQL expression and calculations.  */
	public static final Action queryAndCalculate = newAction(Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void queryAndCalculate(){invoke(queryAndCalculate,null,null);}
	public void doQueryAndCalculate()
	{
		if( getCalculating().getValue()==false )
		{
			getCalculating().setValue(true);
			Thread tCalc = new Thread(new threadedCalculate());
			tCalc.start();
		}
		else
		{
			updateTimer();
		}
	}
	
	/** Time interval at which object executes BQL query and calculates values.  */
	public static final Property executePeriod = newProperty(Flags.SUMMARY,  BRelTime.make(60000), BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BRelTime getExecutePeriod() { return (BRelTime)get(executePeriod); }
	public void setExecutePeriod(javax.baja.sys.BRelTime v) { set(executePeriod, v); }
	
	/** BQL query string (baja:StatusString)  */
	public static final Property inBqlOrd = newProperty(Flags.SUMMARY,  new BStatusString(), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getInBqlOrd() { return (BStatusString)get(inBqlOrd); }
	public void setInBqlOrd(BStatusString v) { set(inBqlOrd, v); }
	
	public static final Property inBql = newProperty(0, BOrd.DEFAULT, null);
	public BOrd getInBql() { return (BOrd) get(inBql); }
	public void setInBql(BOrd v) { set(inBql, v, null); }
	
	/** Value is true when object is actively running BQL expression and calculations.  */
	public final static Property calculating = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false));
	public BStatusBoolean getCalculating() { return (BStatusBoolean)get(calculating); }
	public void setCalculating(BStatusBoolean v) { set(calculating, v); }
	
	/** The last time the BQL expression was calculated.  */
	public static final Property lastQuery = newProperty(Flags.DEFAULT_ON_CLONE, BAbsTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BAbsTime getLastQuery() { return (BAbsTime)get(lastQuery); }
	public void setLastQuery(BAbsTime v) { set(lastQuery, v); }
	
	public final static Property executionTime = newProperty(Flags.DEFAULT_ON_CLONE, BRelTime.make(0),BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BRelTime getExecutionTime() { return (BRelTime)get(executionTime); }
	public void setExecutionTime(BRelTime v) { set(executionTime, v); }
	
	/** Number/Count of values returned by BQL query (baja:StatusNumeric)  */
	public static final Property num = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getNum() { return (BStatusNumeric)get(num); }
	public void setNum(BStatusNumeric v) { set(num, v); }
	
	/** Average of values returned by BQL query (baja:StatusNumeric)  */
	public static final Property avg = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getAvg() { return (BStatusNumeric)get(avg); }
	public void setAvg(BStatusNumeric v) { set(avg, v); }
	
	/** Minimum value returned by BQL query (baja:StatusNumeric)  */
	public static final Property min = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getMin() { return (BStatusNumeric)get(min); }
	public void setMin(BStatusNumeric v) { set(min, v); }
	
	/** Maximum value returned by BQL query (baja:StatusNumeric)  */
	public static final Property max = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getMax() { return (BStatusNumeric)get(max); }
	public void setMax(BStatusNumeric v) { set(max, v); }
	
	/** Sum of all values returned by BQL query (baja:StatusNumeric)  */
	public static final Property sum = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getSum() { return (BStatusNumeric)get(sum); }
	public void setSum(BStatusNumeric v) { set(sum, v); }
	
	/** Value is returned is no values are returned by BQL query (baja:StatusNumeric)  */
	public static final Property fallback = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getFallback() { return (BStatusNumeric)get(fallback); }
	public void setFallback(BStatusNumeric v) { set(fallback, v); }
	
	/** Value is true if an error occurs in query or calculations. */
	public final static Property error = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false));
	public BStatusBoolean getError() { return (BStatusBoolean)get(error); }
	public void setError(BStatusBoolean v) { set(error, v); }

	public static final Property message = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.ok), BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getMessage() { return (BStatusString) get(message); }
	public void setMessage(BStatusString v) { set(message, v); }
	
	/** Value is fired true when calculation is complete. */
	public static final Topic calculated = newTopic(0);
	public void fireCalculated(BBoolean event){fire(calculated,event,null);}
	
	/** Value is fired true when there is an error in the calculation. */
	public static final Topic errorOccured = newTopic(0);
	public void fireErrorOccured(BBoolean event){fire(errorOccured,event,null);}
		
	public static final Topic Num = newTopic(0);
	public void fireNum(BDouble event){fire(Num,event,null);}
		
	public static final Topic Avg = newTopic(0);
	public void fireAvg(BDouble event){fire(Avg,event,null);}
	
	public static final Topic Min = newTopic(0);
	public void fireMin(BDouble event){fire(Min,event,null);}
		
	public static final Topic Max = newTopic(0);
	public void fireMax(BDouble event){fire(Max,event,null);}
	
	public static final Topic Sum = newTopic(0);
	public void fireSum(BDouble event){fire(Sum,event,null);}

	public static final Action timerExpired = newAction(Flags.HIDDEN,null);
	public void timerExpired() { invoke(timerExpired,null,null); }
	public void doTimerExpired() throws Exception  //renamed from onExecute
	{
		if( getCalculating().getValue()==false )
		{
			getCalculating().setValue(true);
			Thread tCalc = new Thread(new threadedCalculate());
			tCalc.start();
		}
		else
		{
			updateTimer();
		}
	}

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
		
	/*----------------------------------------------------------------------------------------------------------------*/
	public void changed(Property p, Context c)
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		
		if ( p == inBqlOrd ) 
		{
			if(getInBqlOrd().getValue()!=getInBql().toString())
			{
				if(getInBqlOrd().getValue().length()>0)
				{
					try
					{
						setInBql( BOrd.make(getInBqlOrd().getValue()) ); 
						if(getUpdateOnBqlChange().getValue()==true && getInBqlOrd().getValue()==getInBql().toString()) {doQueryAndCalculate();}
					}
					catch(Exception e) {errorHandler("ERROR in changed(inBqlOrd) method!", e);}
				}
				else
				{
					try{setInBql( BOrd.DEFAULT); }catch(Exception e) {errorHandler("ERROR in changed(inBqlOrd) method!", e);}
				}
			}
		}
		else if ( p == inBql ) 
		{
			if(getInBqlOrd().getValue()!=getInBql().toString())
			{
				if(getInBql()!=null && getInBql()!=BOrd.DEFAULT && getInBql().toString().length()>0)
				{
					try
					{
						getInBqlOrd().setValue( getInBql().toString() ); 
						if(getUpdateOnBqlChange().getValue()==true && getInBqlOrd().getValue()==getInBql().toString()) {doQueryAndCalculate();}
					}
					catch(Exception e) {errorHandler("ERROR in changed(inBql) method!", e);}
				}
				else
				{
					try{getInBqlOrd().setValue( "" ); }catch(Exception e) {errorHandler("ERROR in changed(inBql) method!", e);}
				}
			}
		}
		else if ( p == executePeriod ) 
		{
			if(getExecutePeriod().getMillis()>0 && getInBqlOrd().getValue().length()>0)
			{
				updateTimer();
			}
		}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void stopped() throws Exception
	{
		getCalculating().setValue(false);
		if (ticket != null) ticket.cancel();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void started() throws Exception
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		startAndSteadyState();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void atSteadyState() throws Exception
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		startAndSteadyState();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	private void startAndSteadyState()
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		
		if ( getInBqlOrd().getValue().length()>0 && (getInBql().toString().length()<=0 || getInBqlOrd().getValue()!=getInBql().toString())  ) 
		{
			try{setInBql( BOrd.make(getInBqlOrd().getValue()) ); }catch(Exception e) {errorHandler("ERROR in startAndSteadyState() method!", e);}
		}
		else if ( getInBql()!=null && getInBql()!=BOrd.DEFAULT && getInBql().toString().length()>0 && getInBqlOrd().getValue().length()<=0 ) 
		{
			try{getInBqlOrd().setValue( getInBql().toString() ); }catch(Exception e) {errorHandler("ERROR in startAndSteadyState() method!", e);}
		}
		getCalculating().setValue(false);
		
		if(getInBqlOrd().getValue().length()>0)
		{
			updateTimer();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	private class threadedCalculate implements Runnable
	{
		/*----------------------------------------------------------------------------------------------------------------*/
		@SuppressWarnings("rawtypes")
		public void run()
		{
			startTime	= System.currentTimeMillis();
			
			try
			{
				if( getInBqlOrd().getValue().length()<=0 || getInBqlOrd().getValue().equalsIgnoreCase("null") || !getInBqlOrd().getStatus().isValid() )
				{
					getCalculating().setValue(false);
					return;
				}
				
				getError().setValue(false);
				getMessage().setValue("");
				getCalculating().setValue(true);
				
				BStatusString	bqlord	= getInBqlOrd();
				String			ord		= bqlord.getValue();
				BStatusNumeric	Value	= null;
				
				double			avg		= 0;
				double			sum		= 0;
				double			num		= 0;
				double			min		= Double.MAX_VALUE;
				double			max		= Double.MIN_VALUE;
				
				BITable 	result 	= (BITable)BOrd.make(ord).resolve(Sys.getStation()).get(); 	// execute bql into table
				ColumnList	columns	= result.getColumns();										// get table cols data
				try(TableCursor	cursor	= result.cursor())											// setup table cursor
				{
					// walk bql rows
					while (cursor.next()) 
					{
						// Provide a way to exit thread cleanly...
						if( getCalculating().getValue()==false ) {return;}
						
						try
						{
							// data is in 1st col
							Column valueColumn = columns.get(0); 
							
							// is BSimple?
							if (((BObject) cursor.cell(valueColumn)).isSimple())  
							{
								// handle result columns that are not complex type (aggregates for example)
								Value = new BStatusNumeric((BDouble.make(cursor.cell(valueColumn).toString()).getDouble()));
							}
							else
							{
								// get BStatusNumeric from table
								Value = (BStatusNumeric) cursor.cell(valueColumn);		
							}
							
							// check to see if value=NaN, if yes then omit from calculation
							if (BDouble.make(Value.getValue()) != BDouble.NaN)	
							{
								// get Status of point, check valid
								if (Value.getStatus().isValid())
								{				
									min		= Math.min(min, Value.getValue());
									max		= Math.max(max, Value.getValue());
									sum		+= Value.getValue();
									num++;
								}
							}
						}
						catch (Exception e)
						{
							errorHandler( "ERROR in threadedCalculate.run() method while loop!", e );
						}
					}
					// End of while loop.
				}
				
				
				// Provide a way to exit thread cleanly...
				if( getCalculating().getValue()==false ) {return;}
				
				
				if (num > 0)
				{               
					// only use avg/min/max if something valid found
					if (num != 0) 
					{
						avg = sum / num;
					}
				}
				else
				{                      
					// No values returned, return default...
					avg = getFallback().getValue();
					min = getFallback().getValue();
					max = getFallback().getValue();
					sum = getFallback().getValue();
				}
				
				if( !getNum().getStatus().isValid() )	{ setNum(new BStatusNumeric(num, BStatus.ok)); }
				else 									{ getNum().setValue(num); }
				
				if( !getAvg().getStatus().isValid() )	{ setAvg(new BStatusNumeric(avg, BStatus.ok)); }
				else 									{ getAvg().setValue(avg); }
				
				if( !getMin().getStatus().isValid() )	{ setMin(new BStatusNumeric(min, BStatus.ok)); }
				else 									{ getMin().setValue(min); }
				
				if( !getMax().getStatus().isValid() )	{ setMax(new BStatusNumeric(max, BStatus.ok)); }
				else 									{ getMax().setValue(max); }
				
				if( !getSum().getStatus().isValid() )	{ setSum(new BStatusNumeric(sum, BStatus.ok)); }
				else 									{ getSum().setValue(sum); }
				
				fireNum(BDouble.make( getNum().getValue() ));
				fireAvg(BDouble.make( getAvg().getValue() ));
				fireMin(BDouble.make( getMin().getValue() ));
				fireMax(BDouble.make( getMax().getValue() ));
				fireSum(BDouble.make( getSum().getValue() ));
				
				getCalculating().setValue(false);
				setLastQuery(BAbsTime.make());
				fireCalculated(BBoolean.make(true));
			}
			catch (Exception e)
			{
				getNum().setStatus(BStatus.fault);
				getAvg().setStatus(BStatus.fault);
				getMin().setStatus(BStatus.fault);
				getMax().setStatus(BStatus.fault);
				getSum().setStatus(BStatus.fault);
				
				getMessage().setValue( errorHandler("ERROR in threadedCalculate.run() method!", e) );
				
				getCalculating().setValue(false);
				getError().setValue(true);
				fireErrorOccured(BBoolean.make(true));
			}
			finally
			{
				getCalculating().setValue(false);
				setExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
				updateTimer();
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	private void updateTimer()
	{
		try
		{
			if (ticket != null) ticket.cancel();
			
			if(getExecutePeriod().getMillis()>0 && getInBqlOrd().getValue().length()>0)
			{
				ticket = Clock.schedulePeriodically(this, getExecutePeriod(), timerExpired, null);
			}
		}
		catch (Exception e)
		{
			errorHandler( "updateTimer()", e );
		}
	}

	private	Clock.Ticket	ticket;
		
	/*----------------------------------------------------------------------------------------------------------------*/
	private String errorHandler(String msg, Exception e)
	{
		return errorHandler(Level.SEVERE, msg, e);
	}
		
	/*----------------------------------------------------------------------------------------------------------------*/
	private String errorHandler(Level level, String msg, Exception e)
	{
		try
		{
			int		MAXLOGLENGTH	= 3583;
			String	MESSAGE			= "";
			String	STACKTRACE		= "";
			String	PRINTSTACKTRACE	= "";
			
			try{MESSAGE		= e.getMessage().trim();}catch(Exception ex) {}
			try{STACKTRACE	= e.getStackTrace().toString().trim();}catch(Exception ex) {}
			try{StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				PRINTSTACKTRACE = errors.toString().trim();
			}catch(Exception ex) {}
			
			msg	= "\n\n" + msg + "\n" + "MESSAGE: \n" + MESSAGE + "\n" + "STACKTRACE: \n" + STACKTRACE + "\n" + "PRINTSTACKTRACE: \n" + PRINTSTACKTRACE;
			msg	= msg.length()>MAXLOGLENGTH? msg.substring(0, MAXLOGLENGTH) : msg;
			
			if(getDebug().getValue()) {System.out.println("\n" + this.getSlotPath() + "\n" + msg);}
			else{logger.log(level, "\n" + this.getSlotPath() + "\n" + msg);}
			
		}
		catch (Exception e1)
		{
			if(getDebug().getValue()) {System.out.println("\n" + "EXCEPTION ERROR WITH '" + TYPE.getModule().getModuleName() + "." + TYPE.getTypeName() + "'");}
			else{logger.log(level, "\n" + "EXCEPTION ERROR WITH '" + TYPE.getModule().getModuleName() + "." + TYPE.getTypeName() + "'");}
		}
		
		return msg.trim();
	}
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/electro_diamond_header.png");

	public static final Logger logger = Logger.getLogger("axCommunity.BqlNumericRecap");

	//TYPE declaration
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BBqlNumericRecap.class);

}


