/**
 * 
 */
package org.axcommunity.niagara.conversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.baja.log.Log;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BMonth;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.BWeekday;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.timezone.BTimeZone;
import javax.baja.units.BUnit;
import javax.baja.util.Lexicon;

/**
 * Converts an AbsTime input into individual outputs for date parts
 * @author Mike Arnott, Kors Engineering
 * 
 * Update 6/29/2017 by James Johnson to move to current logger syntax
 * Update 2/17/2011 by Justin Koffler, added lexicon lookup for days of the week.
 * 
 */

 
public class BAbsTimeToDateParts extends BComponent 
{
	public static final int		JGREG		= 15 + 31 * (10 + 12 * 1582);
	public static final double	HALFSECOND	= 0.5;
	
	private Context				context		= null;
	private Lexicon				bajaLex		= null; // Lexicon.make("baja", Sys.getLanguage());
	
	
	/*------------------------------------------------------------------------------------------------------------------*/
	public static final Action Update = newAction(0, null, null);
	public void Update(){invoke(Update, null, null);}
	public void doUpdate(Context cx)
	{
		onUpdate(cx);
	}
	
	/*------------------------------------------------------------------------------------------------------------------*/
	public static final Action SetTime = newAction(0, BAbsTime.DEFAULT, BFacets.make(BFacets.SHOW_TIME, BBoolean.TRUE, BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public void SetTime(BAbsTime v){invoke(SetTime, null, null);}
	public void doSetTime(BAbsTime v, Context cx)
	{
		if(v.getMillis()==getTimeIn().getMillis())
		{
			onUpdate(cx);
		}
		else
		{
			try {this.context = cx;}catch(Exception e) {}
			setTimeIn(v);
		}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public BValue getActionParameterDefault(Action paramAction)
	{
		if (paramAction == SetTime)
		{
			return (BValue) getTimeIn();
		}
		
		return super.getActionParameterDefault(paramAction);
	}
	
	
	/*------------------------------------------------------------------------------------------------------------------*/
	/**<p>Executed and output values updated upon value change of the following slots:</br>- timeIn</br>- inSimpleDateFormat</br>*/
	public void changed(Property property, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		
		if (property == timeIn || property == inSimpleDateFormat)
		{
			onUpdate(this.context!=null?this.context:cx);
		}
	}   
	
	/*------------------------------------------------------------------------------------------------------------------*/
	private void onUpdate(Context cx)
	{
		try
		{
			this.context = cx;
			if (this.bajaLex == null) { this.bajaLex = Lexicon.make("baja", lang(this.context)); }
			
			BAbsTime	time			= getTimeIn();
			BAbsTime	dtGmt			= BAbsTime.make(time, BTimeZone.GMT);
			BMonth		myMonth			= time.getMonth();
			BWeekday	myDay			= time.getWeekday();
			
			String		stMonthLong		= myMonth.getDisplayTag(this.context);
			String		stMonthShort	= myMonth.getShortDisplayTag(this.context);
			
			String		stDayLong		= myDay.getDisplayTag(this.context);
			String		stDayShort		= myDay.getShortDisplayTag(this.context);
			
			double		dblDayOfWeek	= toDayOfWeek(stDayLong);
			
			getSecondsOut().setValue((time.getSecond()));
			getMinutesOut().setValue((time.getMinute()));
			
			setHoursOut(new BStatusNumeric(time.getHour()));
			setDayOut(new BStatusNumeric(time.getDay()));
			setMonthOut(new BStatusNumeric(time.getMonth().getMonthOfYear()));
			setYearOut(new BStatusNumeric(time.getYear()));
			
			setLongMonthOut(new BStatusString(stMonthLong, stMonthLong.isEmpty()?BStatus.fault:BStatus.ok));
			setShortMonthOut(new BStatusString(stMonthShort, stMonthShort.isEmpty()?BStatus.fault:BStatus.ok));
			setLongDayOut(new BStatusString(stDayLong, stDayLong.isEmpty()?BStatus.fault:BStatus.ok));
			setShortDayOut(new BStatusString(stDayShort, stDayShort.isEmpty()?BStatus.fault:BStatus.ok));
			
			setDayOfTheWeek(new BStatusNumeric(dblDayOfWeek, dblDayOfWeek==0?BStatus.fault:BStatus.ok));
			
			setDaysInTheMonth(new BStatusNumeric(BAbsTime.getDaysInMonth(time.getYear(), time.getMonth())));
			getJulianOut().setValue(toJulian(new int[]{(int)getYearOut().getValue(),(int)getMonthOut().getValue(),(int)getDayOut().getValue()}));
			
			Calendar calNow = Calendar.getInstance();
			Calendar calGmt = Calendar.getInstance();
			calNow.set(time.getYear(),time.getMonth().getMonthOfYear() - 1,time.getDay(),time.getHour(),time.getMinute(),time.getSecond());
			calGmt.set(dtGmt.getYear(),dtGmt.getMonth().getMonthOfYear() - 1,dtGmt.getDay(),dtGmt.getHour(),dtGmt.getMinute(),dtGmt.getSecond());
			
			getOutSerialTime().setValue(calNow.getTimeInMillis());

			if(getInSimpleDateFormat().length()>0)
			{
				DateFormat df = new SimpleDateFormat(getInSimpleDateFormat());
				getStringDateOut().setValue(df.format(calNow.getTime()));
				getOutGMTTime().setValue(df.format(calGmt.getTime()));
			}
			else
			{
				getStringDateOut().setValue(time.encodeToString());
				getOutGMTTime().setValue(dtGmt.encodeToString());
			}
			
			this.context = null;
		}
		catch (Exception e) 
		{
			logger.error("\nERROR in: 'public void changed'" + "\ngetMessage =\n" + e.getMessage() + "\ngetStackTrace =\n" + e.getStackTrace() +	"\ntoString =\n" + e.toString());
		}
	}
	
	/*---------------------------------------------------------------------------------------------------------*/
	private double toDayOfWeek(String dow)
	{
		try
		{
			double dayNumber = 0;
			if		(dow.equalsIgnoreCase( this.bajaLex.getText("sunday.short") ) 		|| dow.equalsIgnoreCase( this.bajaLex.getText("sunday") ))		dayNumber = 1;
			else if	(dow.equalsIgnoreCase( this.bajaLex.getText("monday.short") )		|| dow.equalsIgnoreCase( this.bajaLex.getText("monday") ))		dayNumber = 2;
			else if	(dow.equalsIgnoreCase( this.bajaLex.getText("tuesday.short") )		|| dow.equalsIgnoreCase( this.bajaLex.getText("tuesday") ))		dayNumber = 3;
			else if	(dow.equalsIgnoreCase( this.bajaLex.getText("wednesday.short") )	|| dow.equalsIgnoreCase( this.bajaLex.getText("wednesday") ))	dayNumber = 4;
			else if	(dow.equalsIgnoreCase( this.bajaLex.getText("thursday.short") )		|| dow.equalsIgnoreCase( this.bajaLex.getText("thursday") ))	dayNumber = 5;
			else if	(dow.equalsIgnoreCase( this.bajaLex.getText("friday.short") )		|| dow.equalsIgnoreCase( this.bajaLex.getText("friday") ))		dayNumber = 6;
			else if	(dow.equalsIgnoreCase( this.bajaLex.getText("saturday.short") )		|| dow.equalsIgnoreCase( this.bajaLex.getText("saturday") ))	dayNumber = 7;
			else																																		dayNumber = 0;
			return dayNumber;
		}
		catch (Exception e) 
		{
			logger.error("\nERROR in: 'private double toDayOfWeek'" + "\ngetMessage =\n" + e.getMessage() + "\ngetStackTrace =\n" + e.getStackTrace() +	"\ntoString =\n" + e.toString());
			return 0;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------------------------------*/
	static String lang(Context cx)
	{
		if (cx != null) return cx.getLanguage();
		return Sys.getLanguage();
	}
	
	
	/**Absolute Time Input*/
	public static final Property timeIn = newProperty(Flags.SUMMARY, BAbsTime.DEFAULT, BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BAbsTime getTimeIn() { return (BAbsTime) get(timeIn); }
	public void setTimeIn(BAbsTime v) { set(timeIn, v, null); }

	/**StatusNumeric out value representing Julian Date*/
	public final static Property julianOut = newProperty(Flags.SUMMARY,new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getJulianOut() { return (BStatusNumeric)get(julianOut); }
	public void setJulianOut(BStatusNumeric v) { set(julianOut, v); }

	/**StatusNumeric out value representing time in seconds*/
	public final static Property secondsOut = newProperty(Flags.SUMMARY,new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.UNITS, BUnit.getUnit("second"), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getSecondsOut() { return (BStatusNumeric)get(secondsOut); }
	public void setSecondsOut(BStatusNumeric v) { set(secondsOut, v); }

	/**StatusNumeric out value representing time in minutes*/
	public final static Property minutesOut = newProperty(Flags.SUMMARY,new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.UNITS, BUnit.getUnit("minute"), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getMinutesOut() { return (BStatusNumeric)get(minutesOut); }
	public void setMinutesOut(BStatusNumeric v) { set(minutesOut, v); }
	
	/**StatusNumeric out value representing time in hours*/
	public final static Property hoursOut = newProperty(Flags.SUMMARY,new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.UNITS, BUnit.getUnit("hour"), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getHoursOut() { return (BStatusNumeric)get(hoursOut); }
	public void setHoursOut(BStatusNumeric v) { set(hoursOut, v); }

	/**StatusNumeric out value representing day of month*/
	public final static Property dayOut = newProperty(Flags.SUMMARY,new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getDayOut() { return (BStatusNumeric)get(dayOut); }
	public void setDayOut(BStatusNumeric v) { set(dayOut, v); }

	/**StatusNumeric out value representing month*/
	public final static Property monthOut = newProperty(Flags.SUMMARY,new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getMonthOut() { return (BStatusNumeric)get(monthOut); }
	public void setMonthOut(BStatusNumeric v) { set(monthOut, v); }

	/**StatusNumeric out value representing years*/
	public final static Property yearOut = newProperty(Flags.SUMMARY,new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getYearOut() { return (BStatusNumeric)get(yearOut); }
	public void setYearOut(BStatusNumeric v) { set(yearOut, v); }

	/**StatusString out value representing short month*/
	public final static Property shortMonthOut = newProperty(Flags.SUMMARY,new BStatusString("", BStatus.ok), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getShortMonthOut() { return (BStatusString)get(shortMonthOut); }
	public void setShortMonthOut(BStatusString v) { set(shortMonthOut, v); }

	/**StatusString out value representing long month*/
	public final static Property longMonthOut = newProperty(Flags.SUMMARY,new BStatusString("", BStatus.ok), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getLongMonthOut() { return (BStatusString)get(longMonthOut); }
	public void setLongMonthOut(BStatusString v) { set(longMonthOut, v); }

	/**StatusString out value representing short day*/
	public final static Property shortDayOut = newProperty(Flags.SUMMARY,new BStatusString("", BStatus.ok), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getShortDayOut() { return (BStatusString)get(shortDayOut); }
	public void setShortDayOut(BStatusString v) { set(shortDayOut, v); }

	/**StatusString out value representing long Day*/
	public final static Property longDayOut = newProperty(Flags.SUMMARY,new BStatusString("", BStatus.ok), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getLongDayOut() { return (BStatusString)get(longDayOut); }
	public void setLongDayOut(BStatusString v) { set(longDayOut, v); }
	
	/**StatusNumeric out value representing the day of the week that was inputted. */
	public static final Property dayOfTheWeek = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getDayOfTheWeek() { return (BStatusNumeric)get(dayOfTheWeek); }
	public void setDayOfTheWeek(BStatusNumeric v) { set(dayOfTheWeek, v, null); }

	/**StatusNumeric out value representing the number of days in the month. */
	public static final Property daysInTheMonth = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getDaysInTheMonth() { return (BStatusNumeric)get(daysInTheMonth); }
	public void setDaysInTheMonth(BStatusNumeric v) { set(daysInTheMonth, v, null); }
	
	/**StatusNumeric out value representing milliseconds since epoch (January 1, 1970, 00:00:00 GMT)*/
	public final static Property outSerialTime = newProperty(Flags.SUMMARY, new BStatusNumeric(0, BStatus.ok), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.FIELD_WIDTH, BInteger.make(50)));
	public BStatusNumeric getOutSerialTime() { return (BStatusNumeric)get(outSerialTime); }
	public void setOutSerialTime(BStatusNumeric v) { set(outSerialTime, v); }
	
	
	
	/** This is the format the time string output should be written in. <br>
	 * <p>Available Fields Include: <br>
	 * 
	 * <blockquote><table border='1' bordercolor='#505050' cellspacing='0'>
	 * <tr><th bgcolor='#666699' align=left><font color='#ffffff'>Letter</font></th><th bgcolor='#666699' align=left><font color='#ffffff'>Component</font></th><th bgcolor='#666699' align=left><font color='#ffffff'>Type</font></th><th bgcolor='#666699' align=left><font color='#ffffff'>Examples</font></th></tr>
	 * <tr><td><code>G</code></td><td>Era designator</td><td>Text</td><td><code>AD</code></td></tr>
	 * <tr><td><code>y</code></td><td>Year</td><td>Year</td><td><code>yyyy=1996</code>, <code>yy=96</code></td></tr>
	 * <tr><td><code>Y</code></td><td>Week year</td><td>Year</td><td><code>YYYY=2009</code>, <code>YY=09</code></td></tr>
	 * <tr><td><code>M</code></td><td>Month in year</td><td>Month</td><td><code>MMMM=July</code>, <code>MMM=Jul</code>, <code>MM=07</code></td></tr>
	 * <tr><td><code>w</code></td><td>Week in year</td><td>Number</td><td><code>27</code></td></tr>
	 * <tr><td><code>W</code></td><td>Week in month</td><td>Number</td><td><code>2</code></td></tr>
	 * <tr><td><code>D</code></td><td>Day in year</td><td>Number</td><td><code>189</code></td></tr>
	 * <tr><td><code>d</code></td><td>Day in month</td><td>Number</td><td><code>10</code></td></tr>
	 * <tr><td><code>F</code></td><td>Day of week in month</td><td>Number</td><td><code>2</code></td></tr>
	 * <tr><td><code>E</code></td><td>Day name in week</td><td>Text</td><td><code>EEEE=Tuesday</code>, <code>E=Tue</code></td></tr>
	 * <tr><td><code>u</code></td><td>Day number of week</td><td>Number</td><td><code>1</code></td></tr>
	 * <tr><td><code>a</code></td><td>AM/PM marker</td><td>Text</td><td><code>PM</code></td></tr>
	 * <tr><td><code>H</code></td><td>Hour in day (0-23)</td><td>Number</td><td><code>0</code></td></tr>
	 * <tr><td><code>k</code></td><td>Hour in day (1-24)</td><td>Number</td><td><code>24</code></td></tr>
	 * <tr><td><code>K</code></td><td>Hour in am/pm (0-11)</td><td>Number</td><td><code>0</code></td></tr>
	 * <tr><td><code>h</code></td><td>Hour in am/pm (1-12)</td><td>Number</td><td><code>12</code></td></tr>
	 * <tr><td><code>m</code></td><td>Minute in hour</td><td>Number</td><td><code>30</code></td></tr>
	 * <tr><td><code>s</code></td><td>Second in minute</td><td>Number</td><td><code>55</code></td></tr>
	 * <tr><td><code>S</code></td><td>Millisecond</td><td>Number</td><td><code>978</code></td></tr>
	 * <tr><td><code>z</code></td><td>Time zone</td><td>General time zone</td><td><code>zzzz=Pacific Standard Time</code>, <code>z=PST</code></td></tr>
	 * <tr><td><code>Z</code></td><td>Time zone</td><td>RFC 822 time zone</td><td><code>-0800</code></td></tr>
	 * <tr><td><code>X</code></td><td>Time zone</td><td>ISO 8601 time zone</td><td><code>X=-08</code>, <code>XX=-0800</code>,  <code>XXX=-08:00</code></td></tr>
	 * </table></blockquote>
	 * <br>
	 * <p>EXAMPLE: MM/dd/yyyy hh:mm:ss.S a <br>
	 * 
	 */
	public static final Property inSimpleDateFormat = newProperty(0, BString.make("MM/dd/yyyy hh:mm:ss.S a"), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public String getInSimpleDateFormat() { return getString(inSimpleDateFormat); }
	public void setInSimpleDateFormat(String v) { setString(inSimpleDateFormat,v,null); }
	
	/**<p>StatusString out value representing date and time</br><p>Format can be modified using the 'inSimpleDateFormat' slot.</br>*/
	public final static Property stringDateOut = newProperty(Flags.SUMMARY,new BStatusString("", BStatus.ok), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getStringDateOut() { return (BStatusString)get(stringDateOut); }
	public void setStringDateOut(BStatusString v) { set(stringDateOut, v); }

	/**<p>StatusString out value representing date and time as GMT timezone.</br><p>Format can be modified using the 'inSimpleDateFormat' slot.</br>*/
	public final static Property outGMTTime = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.ok), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutGMTTime() { return (BStatusString)get(outGMTTime); }
	public void setOutGMTTime(BStatusString v) { set(outGMTTime, v); }

	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BAbsTimeToDateParts.class);
	public Type getType() { return TYPE; }

	public static final Log logger = Log.getLog(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());


	

	//copied from internet
	private double toJulian(int[] ymd) 
	{
		try
		{
			int	year		=ymd[0];
			int	month		=ymd[1]; // jan=1, feb=2,...
			int	day			=ymd[2];    
			int	julianYear	= year;
			
			if (year < 0) julianYear++;
			
			int	julianMonth	= month;
			
			if (month > 2) 
			{
				julianMonth++;
			}
			else 
			{
				julianYear--;
				julianMonth += 13;
			}

			double julian = (java.lang.Math.floor(365.25 * julianYear) + java.lang.Math.floor(30.6001*julianMonth) + day + 1720995.0);
			
			if (day + 31 * (month + 12 * year) >= JGREG) 
			{
				// change over to Gregorian calendar
				int	ja	= (int)(0.01 * julianYear);
				julian	+= 2 - ja + (0.25 * ja);
			}
			return java.lang.Math.floor(julian);
		}
		catch (Exception e) 
		{
			logger.error("\nERROR in: 'private double toJulian'" + "\ngetMessage =\n" + e.getMessage() + "\ngetStackTrace =\n" + e.getStackTrace() +	"\ntoString =\n" + e.toString());
			return 0;
		}
	}

	/**
	 * Converts a Julian day to a calendar date
	 * ref :
	 * Numerical Recipes in C, 2nd ed., Cambridge University Press 1992
	 */
	public int[] fromJulian(double injulian) 
	{
		try
		{
			int	jalpha,ja,jb,jc,jd,je,year,month,day;
			ja	= (int) injulian;
			
			if (ja>= JGREG) 
			{    
				jalpha	= (int) (((ja - 1867216) - 0.25) / 36524.25);
				ja		= ja + 1 + jalpha - jalpha / 4;
			}

			jb		= ja + 1524;
			jc		= (int) (6680.0 + ((jb - 2439870) - 122.1) / 365.25);
			jd		= 365 * jc + jc / 4;
			je		= (int) ((jb - jd) / 30.6001);
			day		= jb - jd - (int) (30.6001 * je);
			month	= je - 1;
			
			if (month > 12) { month = month - 12;}
			
			year	= jc - 4715;
			
			if (month > 2) {year--;}
			if (year <= 0) {year--;}

			return new int[] {year, month, day};
		}
		catch (Exception e) 
		{
			logger.error("\nERROR in: 'public int[] fromJulian'" + "\ngetMessage =\n" + e.getMessage() + "\ngetStackTrace =\n" + e.getStackTrace() +	"\ntoString =\n" + e.toString());
			return new int[] {0, 0, 0};
		}
	}
}
