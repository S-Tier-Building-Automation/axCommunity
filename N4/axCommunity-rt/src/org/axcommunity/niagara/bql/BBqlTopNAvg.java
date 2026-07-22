package org.axcommunity.niagara.bql;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
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
 * Executes a BQL query on an editable Execute Period and averages the
 * <b>N highest</b> values returned by the query (the "top-N average").
 *
 * <p>This is an enriched MinMaxAvg: in addition to {@code topAvg} it also
 * reports the count, full-set average, minimum, maximum and sum of the values
 * returned by the query. Values that are NaN or of invalid status are omitted
 * from every calculation. If fewer than {@code topN} valid values are returned,
 * the average is taken over however many were found. If no valid values are
 * returned, the outputs are set to the {@code fallback} value.</p>
 *
 * <p>The numeric value is read from the column named by {@code valueColumn}
 * (default {@code "value"}); if no column of that name exists the first column
 * is used, matching the {@code select out from ...} convention.</p>
 *
 * <p>A valid BQL ord is required in {@code inBqlOrd} for the object to execute.
 * If the slot is empty or the query syntax is invalid the object will not run.</p>
 *
 * Sample query: {@code station:|slot:/|bql:select out from control:NumericPoint where displayName like 'ZN-T*'}
 *
 * <p>Built on the same module idiom as {@code BBqlNumericRecap} (BQL query +
 * {@code Clock.schedulePeriodically} interval + off-engine-thread calculation).</p>
 *
 * @author  STC Worldwide
 */
public class BBqlTopNAvg extends BComponent
{
	private double startTime = 0;

	public static final Property facets = newProperty(0, BFacets.make(BFacets.PRECISION, BInteger.make(2), BFacets.FIELD_WIDTH, BInteger.make(50)),null);
	public BFacets getFacets() { return (BFacets) get(facets); }
	public void setFacets(BFacets v) { set(facets, v, null); }

	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * Applies the configured numeric facets to numeric slots.
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

	/** Manually trigger the BQL query and calculations.  */
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

	/** Editable time interval at which the object executes the BQL query and calculations. Default 15 minutes.  */
	public static final Property executePeriod = newProperty(Flags.SUMMARY,  BRelTime.make(900000), BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BRelTime getExecutePeriod() { return (BRelTime)get(executePeriod); }
	public void setExecutePeriod(javax.baja.sys.BRelTime v) { set(executePeriod, v); }

	/** BQL query string (baja:StatusString)  */
	public static final Property inBqlOrd = newProperty(Flags.SUMMARY,  new BStatusString(), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getInBqlOrd() { return (BStatusString)get(inBqlOrd); }
	public void setInBqlOrd(BStatusString v) { set(inBqlOrd, v); }

	public static final Property inBql = newProperty(0, BOrd.DEFAULT, null);
	public BOrd getInBql() { return (BOrd) get(inBql); }
	public void setInBql(BOrd v) { set(inBql, v, null); }

	/** Name of the table column holding the numeric value. Defaults to "value"; falls back to the first column if not found.  */
	public static final Property valueColumn = newProperty(Flags.SUMMARY, "value", null);
	public String getValueColumn() { return getString(valueColumn); }
	public void setValueColumn(String v) { setString(valueColumn, v, null); }

	/** Number of highest values (N) to average for the top-N average.  */
	public static final Property topN = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(5, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getTopN() { return (BStatusNumeric)get(topN); }
	public void setTopN(BStatusNumeric v) { set(topN, v); }

	/** Value is true when the object is actively running the BQL query and calculations.  */
	public final static Property calculating = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false));
	public BStatusBoolean getCalculating() { return (BStatusBoolean)get(calculating); }
	public void setCalculating(BStatusBoolean v) { set(calculating, v); }

	/** The last time the BQL query was calculated.  */
	public static final Property lastQuery = newProperty(Flags.DEFAULT_ON_CLONE, BAbsTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BAbsTime getLastQuery() { return (BAbsTime)get(lastQuery); }
	public void setLastQuery(BAbsTime v) { set(lastQuery, v); }

	public final static Property executionTime = newProperty(Flags.DEFAULT_ON_CLONE, BRelTime.make(0),BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BRelTime getExecutionTime() { return (BRelTime)get(executionTime); }
	public void setExecutionTime(BRelTime v) { set(executionTime, v); }

	/** Average of the N highest values returned by the BQL query (baja:StatusNumeric)  */
	public static final Property topAvg = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getTopAvg() { return (BStatusNumeric)get(topAvg); }
	public void setTopAvg(BStatusNumeric v) { set(topAvg, v); }

	/** Number/Count of valid values returned by the BQL query (baja:StatusNumeric)  */
	public static final Property num = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getNum() { return (BStatusNumeric)get(num); }
	public void setNum(BStatusNumeric v) { set(num, v); }

	/** Average of all values returned by the BQL query (baja:StatusNumeric)  */
	public static final Property avg = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getAvg() { return (BStatusNumeric)get(avg); }
	public void setAvg(BStatusNumeric v) { set(avg, v); }

	/** Minimum value returned by the BQL query (baja:StatusNumeric)  */
	public static final Property min = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getMin() { return (BStatusNumeric)get(min); }
	public void setMin(BStatusNumeric v) { set(min, v); }

	/** Maximum value returned by the BQL query (baja:StatusNumeric)  */
	public static final Property max = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getMax() { return (BStatusNumeric)get(max); }
	public void setMax(BStatusNumeric v) { set(max, v); }

	/** Sum of all values returned by the BQL query (baja:StatusNumeric)  */
	public static final Property sum = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getSum() { return (BStatusNumeric)get(sum); }
	public void setSum(BStatusNumeric v) { set(sum, v); }

	/** Value returned if no valid values are returned by the BQL query (baja:StatusNumeric)  */
	public static final Property fallback = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE,  new BStatusNumeric(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getFallback() { return (BStatusNumeric)get(fallback); }
	public void setFallback(BStatusNumeric v) { set(fallback, v); }

	/** Value is true if an error occurs in the query or calculations. */
	public final static Property error = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false));
	public BStatusBoolean getError() { return (BStatusBoolean)get(error); }
	public void setError(BStatusBoolean v) { set(error, v); }

	public static final Property message = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.ok), BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getMessage() { return (BStatusString) get(message); }
	public void setMessage(BStatusString v) { set(message, v); }

	/** Fired true when a calculation completes. */
	public static final Topic calculated = newTopic(0);
	public void fireCalculated(BBoolean event){fire(calculated,event,null);}

	/** Fired true when an error occurs in a calculation. */
	public static final Topic errorOccured = newTopic(0);
	public void fireErrorOccured(BBoolean event){fire(errorOccured,event,null);}

	public static final Topic TopAvg = newTopic(0);
	public void fireTopAvg(BDouble event){fire(TopAvg,event,null);}

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
	public void doTimerExpired() throws Exception
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

				String	ord		= getInBqlOrd().getValue();
				BStatusNumeric	Value	= null;

				double	sum		= 0;
				double	min		= Double.MAX_VALUE;
				double	max		= Double.MIN_VALUE;
				ArrayList<Double> values = new ArrayList<Double>();

				BITable 	result 	= (BITable)BOrd.make(ord).resolve(Sys.getStation()).get(); 	// execute bql into table
				ColumnList	columns	= result.getColumns();										// get table cols data

				// resolve value column by name, falling back to the first column
				Column valueCol = null;
				if(getValueColumn()!=null && getValueColumn().length()>0) { valueCol = columns.get(getValueColumn()); }
				if(valueCol==null) { valueCol = columns.get(0); }

				try(TableCursor	cursor	= result.cursor())										// setup table cursor
				{
					// walk bql rows
					while (cursor.next())
					{
						// Provide a way to exit thread cleanly...
						if( getCalculating().getValue()==false ) {return;}

						try
						{
							// is BSimple? (aggregate columns etc.)
							if (((BObject) cursor.cell(valueCol)).isSimple())
							{
								Value = new BStatusNumeric((BDouble.make(cursor.cell(valueCol).toString()).getDouble()));
							}
							else
							{
								Value = (BStatusNumeric) cursor.cell(valueCol);
							}

							// omit NaN values
							if (!Double.isNaN(Value.getValue()))
							{
								// only count valid status
								if (Value.getStatus().isValid())
								{
									double v = Value.getValue();
									values.add(v);
									min	= Math.min(min, v);
									max	= Math.max(max, v);
									sum	+= v;
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

				double num		= values.size();
				double avg		= 0;
				double topAvg	= 0;

				if (num > 0)
				{
					avg = sum / num;

					// average of the N highest values
					Collections.sort(values, Collections.reverseOrder());
					int n = (int)getTopN().getValue();
					if (n < 0) n = 0;
					if (n > values.size()) n = values.size();
					if (n > 0)
					{
						double topSum = 0;
						for (int i = 0; i < n; i++) { topSum += values.get(i); }
						topAvg = topSum / n;
					}
					else
					{
						topAvg = getFallback().getValue();
					}
				}
				else
				{
					// No valid values returned, use the fallback...
					avg		= getFallback().getValue();
					min		= getFallback().getValue();
					max		= getFallback().getValue();
					sum		= getFallback().getValue();
					topAvg	= getFallback().getValue();
				}

				if( !getNum().getStatus().isValid() )		{ setNum(new BStatusNumeric(num, BStatus.ok)); }
				else 										{ getNum().setValue(num); }

				if( !getTopAvg().getStatus().isValid() )	{ setTopAvg(new BStatusNumeric(topAvg, BStatus.ok)); }
				else 										{ getTopAvg().setValue(topAvg); }

				if( !getAvg().getStatus().isValid() )		{ setAvg(new BStatusNumeric(avg, BStatus.ok)); }
				else 										{ getAvg().setValue(avg); }

				if( !getMin().getStatus().isValid() )		{ setMin(new BStatusNumeric(min, BStatus.ok)); }
				else 										{ getMin().setValue(min); }

				if( !getMax().getStatus().isValid() )		{ setMax(new BStatusNumeric(max, BStatus.ok)); }
				else 										{ getMax().setValue(max); }

				if( !getSum().getStatus().isValid() )		{ setSum(new BStatusNumeric(sum, BStatus.ok)); }
				else 										{ getSum().setValue(sum); }

				fireTopAvg(BDouble.make( getTopAvg().getValue() ));
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
				getTopAvg().setStatus(BStatus.fault);
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

	public static final Logger logger = Logger.getLogger("axCommunity.BqlTopNAvg");

	//TYPE declaration
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BBqlTopNAvg.class);

}
