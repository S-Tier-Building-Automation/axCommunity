package org.axcommunity.niagara.bql;

import javax.baja.collection.BITable;
import javax.baja.collection.ColumnList;
import javax.baja.collection.TableCursor;
import javax.baja.naming.BOrd;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.BMonth;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BAbsTimeRange;

/**
 * BTrendAnalyzer uses a BQL query to grab the count, min, max, average
 * and sum for a user selected trend.
 * 
 * @author Tucker Watson
 * @creation 02 Feb 07
 * @company Activelogix (http://www.activelogix.com)
 * @blog http://www.tuckwat.com
 */

public class BTrendAnalyzer extends BComponent
{
	/*-
	
	class BTrendAnalyzer
	{
		actions
		{
			execute()
		}
		properties
		{	
			history:BOrd
			  default {[ BOrd.NULL ]}
			dateRange:BAbsTimeRange
			  default {[ new BAbsTimeRange(BAbsTime.make(2000,BMonth.make(0),0,0,0),BAbsTime.make()) ]}
			count:BStatusNumeric
			  default {[ new BStatusNumeric(0.0) ]}
			  flags { summary }
			min:BStatusNumeric
			  default {[ new BStatusNumeric(0.0) ]}
			  flags { summary }
			max:BStatusNumeric
			  default {[ new BStatusNumeric(0.0) ]}
			  flags { summary }
			average:BStatusNumeric
			  default {[ new BStatusNumeric(0.0) ]}
			  flags { summary }
			sum:BStatusNumeric
			  default {[ new BStatusNumeric(0.0) ]}
			  flags { summary }
			
			
		}	  
	}
	
	-*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $org.axcommunity.niagara.bql.BTrendAnalyzer(2106091625)1.0$ @*/
/* Generated Sun Feb 22 14:52:04 EST 2009 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "history"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>history</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#getHistory
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#setHistory
   */
  public static final Property history = newProperty(0, BOrd.NULL,null);
  
  /**
   * Get the <code>history</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#history
   */
  public BOrd getHistory() { return (BOrd)get(history); }
  
  /**
   * Set the <code>history</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#history
   */
  public void setHistory(BOrd v) { set(history,v,null); }

////////////////////////////////////////////////////////////////
// Property "dateRange"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>dateRange</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#getDateRange
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#setDateRange
   */
  public static final Property dateRange = newProperty(0, new BAbsTimeRange(BAbsTime.make(2000,BMonth.make(0),0,0,0),BAbsTime.make()),null);
  
  /**
   * Get the <code>dateRange</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#dateRange
   */
  public BAbsTimeRange getDateRange() { return (BAbsTimeRange)get(dateRange); }
  
  /**
   * Set the <code>dateRange</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#dateRange
   */
  public void setDateRange(BAbsTimeRange v) { set(dateRange,v,null); }

////////////////////////////////////////////////////////////////
// Property "count"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>count</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#getCount
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#setCount
   */
  public static final Property count = newProperty(Flags.SUMMARY, new BStatusNumeric(0.0),null);
  
  /**
   * Get the <code>count</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#count
   */
  public BStatusNumeric getCount() { return (BStatusNumeric)get(count); }
  
  /**
   * Set the <code>count</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#count
   */
  public void setCount(BStatusNumeric v) { set(count,v,null); }

////////////////////////////////////////////////////////////////
// Property "min"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>min</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#getMin
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#setMin
   */
  public static final Property min = newProperty(Flags.SUMMARY, new BStatusNumeric(0.0),null);
  
  /**
   * Get the <code>min</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#min
   */
  public BStatusNumeric getMin() { return (BStatusNumeric)get(min); }
  
  /**
   * Set the <code>min</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#min
   */
  public void setMin(BStatusNumeric v) { set(min,v,null); }

////////////////////////////////////////////////////////////////
// Property "max"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>max</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#getMax
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#setMax
   */
  public static final Property max = newProperty(Flags.SUMMARY, new BStatusNumeric(0.0),null);
  
  /**
   * Get the <code>max</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#max
   */
  public BStatusNumeric getMax() { return (BStatusNumeric)get(max); }
  
  /**
   * Set the <code>max</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#max
   */
  public void setMax(BStatusNumeric v) { set(max,v,null); }

////////////////////////////////////////////////////////////////
// Property "average"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>average</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#getAverage
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#setAverage
   */
  public static final Property average = newProperty(Flags.SUMMARY, new BStatusNumeric(0.0),null);
  
  /**
   * Get the <code>average</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#average
   */
  public BStatusNumeric getAverage() { return (BStatusNumeric)get(average); }
  
  /**
   * Set the <code>average</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#average
   */
  public void setAverage(BStatusNumeric v) { set(average,v,null); }

////////////////////////////////////////////////////////////////
// Property "sum"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>sum</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#getSum
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#setSum
   */
  public static final Property sum = newProperty(Flags.SUMMARY, new BStatusNumeric(0.0),null);
  
  /**
   * Get the <code>sum</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#sum
   */
  public BStatusNumeric getSum() { return (BStatusNumeric)get(sum); }
  
  /**
   * Set the <code>sum</code> property.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#sum
   */
  public void setSum(BStatusNumeric v) { set(sum,v,null); }

////////////////////////////////////////////////////////////////
// Action "execute"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>execute</code> action.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#execute()
   */
  public static final Action execute = newAction(0,null);
  
  /**
   * Invoke the <code>execute</code> action.
   * @see org.axcommunity.niagara.bql.BTrendAnalyzer#execute
   */
  public void execute() { invoke(execute,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTrendAnalyzer.class);
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/ActiveLogixLogo.png");

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/


  public void doExecute()
	{
		String fiftyYearsInMS = "1577846298735";

		// Grab the ORD for the selected history
		String hist = getHistory().toString();

		// Grab the start and end dates, note 'encodeToString()' makes it
		// ORD friendly
		String startDate = getDateRange().getStartTime().encodeToString();
		String endDate = getDateRange().getEndTime().encodeToString();

		BOrd bqlOrd = BOrd.make(hist + "?period=timeRange;start=" + startDate + ";end=" + endDate
				+ "|bql:historyFunc:HistoryRollup.rollup(select *, baja:RelTime '" + fiftyYearsInMS + "')");

		// Run the BQL query
		BITable result = (BITable) bqlOrd.resolve(Sys.getStation()).get();
		ColumnList columns = result.getColumns();
		TableCursor c = (TableCursor) result.cursor();

		// We should only have one entry in the history since we're querying 50 years
		c.next(); // Move to 1st (and only) record

		// Grab the info from the corresponding rows
		double count = Double.parseDouble(c.get(columns.get(2)).toString().replace(',','.'));
		double min = Double.parseDouble(c.get(columns.get(3)).toString().replace(',','.'));
		double max = Double.parseDouble(c.get(columns.get(4)).toString().replace(',','.'));
		double avg = Double.parseDouble(c.get(columns.get(5)).toString().replace(',','.'));
		double sum = Double.parseDouble(c.get(columns.get(6)).toString().replace(',','.'));

		// Set each value
		getCount().setValue(count);
		getMin().setValue(min);
		getMax().setValue(max);
		getAverage().setValue(avg);
		getSum().setValue(sum);
	}
}