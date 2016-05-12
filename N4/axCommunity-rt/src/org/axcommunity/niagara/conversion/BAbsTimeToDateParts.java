/**
 * 
 */
package org.axcommunity.niagara.conversion;

import javax.baja.log.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.units.*;
import javax.baja.sys.BComponent;





/**
 * Converts an AbsTime input into individual outputs for date parts
 * @author Mike Arnott, Kors Engineering
 */
public class BAbsTimeToDateParts extends BComponent 
{

	public static int JGREG= 15 + 31*(10+12*1582);
	public static double HALFSECOND = 0.5;

	public void changed(Property property, Context context)
	{
		super.changed(property, context);
		if(!Sys.atSteadyState() || !isRunning())
		{
			return;
		}

		if (property == timeIn)
		{
			try
			{
				BAbsTime dtNow = getTimeIn();
				
				getSecondsOut().setValue((dtNow.getSecond()));
				getMinutesOut().setValue((dtNow.getMinute()));
				
				setHoursOut(new BStatusNumeric(dtNow.getHour()));
				setDayOut(new BStatusNumeric(dtNow.getDay()));
				setMonthOut(new BStatusNumeric(getTimeIn().getMonth().getMonthOfYear()));
				setYearOut(new BStatusNumeric(getTimeIn().getYear()));
				
				BMonth myMonth = getTimeIn().getMonth();
				String stMonth = new String(myMonth.toString());
				
				setLongMonthOut(new BStatusString(stMonth));
				setShortMonthOut(new BStatusString(stMonth.substring(0,3)));
				
				BWeekday myDay = getTimeIn().getWeekday();
				
				setLongDayOut(new BStatusString(myDay.toString()));
				setDayOfTheWeek(new BStatusNumeric(toDayOfWeek(getLongDayOut().getValue())));
				
				String stDay = myDay.toString().substring(0,3);
				
				setShortDayOut(new BStatusString(stDay));
				getJulianOut().setValue(toJulian(new int[]{(int)getYearOut().getValue(),(int)getMonthOut().getValue(),(int)getDayOut().getValue()}));
				getStringDateOut().setValue(dtNow.encodeToString());
				
				Calendar cal = Calendar.getInstance();
				
				cal.set(dtNow.getYear(),dtNow.getMonth().getMonthOfYear() - 1,dtNow.getDay(),dtNow.getHour(),dtNow.getMinute(),dtNow.getSecond());
				getOutSerialTime().setValue(cal.getTimeInMillis());
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
				getOutGMTTime().setValue(df.format(cal.getTime()));
			}
			catch (Exception e) 
			{
				logger.error("\nslotPath = " + getSlotPath() + "\nERROR in: 'public void changed'" + "\ngetMessage =\n" + e.getMessage() + "\ngetStackTrace =\n" + e.getStackTrace() +	"\ntoString =\n" + e.toString());
			}
		
		}
	}    
	/**Absolute Time Input*/
	public final static Property timeIn = newProperty(Flags.SUMMARY, BAbsTime.DEFAULT);
	public void setTimeIn(BAbsTime v) { set(timeIn, v); }
	public BAbsTime getTimeIn() { return (BAbsTime)get(timeIn); }

	public static BUnit mySecs = BUnit.getUnit("second");
	public static BUnit myMins = BUnit.getUnit("minute");
	public static BUnit myHours = BUnit.getUnit("hour");

	/**StatusNumeric value out representing Julian Date*/
	public final static Property julianOut = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(0));
	public BStatusNumeric getJulianOut() { return (BStatusNumeric)get(julianOut); }
	public void setJulianOut(BStatusNumeric v) { set(julianOut, v); }



	/**StatusNumeric value out representing time in seconds*/
	public final static Property secondsOut = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(mySecs,0));
	public BStatusNumeric getSecondsOut() { return (BStatusNumeric)get(secondsOut); }
	public void setSecondsOut(BStatusNumeric v) { set(secondsOut, v); }

	/**StatusNumeric value out representing time in minutes*/
	public final static Property minutesOut = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(myMins,0));
	public BStatusNumeric getMinutesOut() { return (BStatusNumeric)get(minutesOut); }
	public void setMinutesOut(BStatusNumeric v) { set(minutesOut, v); }

	/**StatusNumeric value out representing time in hours*/
	public final static Property hoursOut = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(myHours,0));
	public BStatusNumeric getHoursOut() { return (BStatusNumeric)get(hoursOut); }
	public void setHoursOut(BStatusNumeric v) { set(hoursOut, v); }

	/**StatusNumeric value out representing day of month*/
	public final static Property dayOut = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(0));
	public BStatusNumeric getDayOut() { return (BStatusNumeric)get(dayOut); }
	public void setDayOut(BStatusNumeric v) { set(dayOut, v); }

	/**StatusNumeric value out representing month*/
	public final static Property monthOut = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(0));
	public BStatusNumeric getMonthOut() { return (BStatusNumeric)get(monthOut); }
	public void setMonthOut(BStatusNumeric v) { set(monthOut, v); }

	/**StatusNumeric value out representing years*/
	public final static Property yearOut = newProperty(Flags.SUMMARY,new BStatusNumeric(),BFacets.makeNumeric(0));
	public BStatusNumeric getYearOut() { return (BStatusNumeric)get(yearOut); }
	public void setYearOut(BStatusNumeric v) { set(yearOut, v); }

	/**StatusString value out representing short month*/
	public final static Property shortMonthOut = newProperty(Flags.SUMMARY,new BStatusString());
	public BStatusString getShortMonthOut() { return (BStatusString)get(shortMonthOut); }
	public void setShortMonthOut(BStatusString v) { set(shortMonthOut, v); }

	/**StatusString value out representing long month*/
	public final static Property longMonthOut = newProperty(Flags.SUMMARY,new BStatusString());
	public BStatusString getLongMonthOut() { return (BStatusString)get(longMonthOut); }
	public void setLongMonthOut(BStatusString v) { set(longMonthOut, v); }

	/**StatusString value out representing short day*/
	public final static Property shortDayOut = newProperty(Flags.SUMMARY,new BStatusString());
	public BStatusString getShortDayOut() { return (BStatusString)get(shortDayOut); }
	public void setShortDayOut(BStatusString v) { set(shortDayOut, v); }

	/**StatusString value out representing long Day*/
	public final static Property longDayOut = newProperty(Flags.SUMMARY,new BStatusString());
	public BStatusString getLongDayOut() { return (BStatusString)get(longDayOut); }
	public void setLongDayOut(BStatusString v) { set(longDayOut, v); }
	
	/** Status Number Output representing the day of the week that was inputted. */
	public static final Property dayOfTheWeek = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getDayOfTheWeek() { return (BStatusNumeric)get(dayOfTheWeek); }
	public void setDayOfTheWeek(BStatusNumeric v) { set(dayOfTheWeek, v, null); }

	/**StatusString value out representing datetime*/
	public final static Property stringDateOut = newProperty(Flags.SUMMARY,new BStatusString());
	public BStatusString getStringDateOut() { return (BStatusString)get(stringDateOut); }
	public void setStringDateOut(BStatusString v) { set(stringDateOut, v); }


	/**StatusNumeric value out representing milliseconds since epoch (January 1, 1970, 00:00:00 GMT)*/
	public final static Property outSerialTime = newProperty(Flags.SUMMARY, new BStatusNumeric(),BFacets.makeNumeric(0));
	public BStatusNumeric getOutSerialTime() { return (BStatusNumeric)get(outSerialTime); }
	public void setOutSerialTime(BStatusNumeric v) { set(outSerialTime, v); }

	/**StatusString value out representing GMT time string*/
	public final static Property outGMTTime = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getOutGMTTime() { return (BStatusString)get(outGMTTime); }
	public void setOutGMTTime(BStatusString v) { set(outGMTTime, v); }

	
	public static final Log logger = Log.getLog("axCommunity.AbsTimeToDateParts");

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BAbsTimeToDateParts.class);
	public Type getType() { return TYPE; }


	private double toDayOfWeek(String dow)
	{
		try
		{
			double dayNumber = 0;
			if		(dow.equalsIgnoreCase("sun") || dow.equalsIgnoreCase("sunday"))		dayNumber = 1;
			else if	(dow.equalsIgnoreCase("mon") || dow.equalsIgnoreCase("monday"))		dayNumber = 2;
			else if	(dow.equalsIgnoreCase("tue") || dow.equalsIgnoreCase("tuesday"))	dayNumber = 3;
			else if	(dow.equalsIgnoreCase("wed") || dow.equalsIgnoreCase("wednesday"))	dayNumber = 4;
			else if	(dow.equalsIgnoreCase("thu") || dow.equalsIgnoreCase("thursday"))	dayNumber = 5;
			else if	(dow.equalsIgnoreCase("fri") || dow.equalsIgnoreCase("friday"))		dayNumber = 6;
			else if	(dow.equalsIgnoreCase("sat") || dow.equalsIgnoreCase("saturday"))	dayNumber = 7;
			else																		dayNumber = 0;
			return dayNumber;
		}
		catch (Exception e) 
		{
			logger.error("\nslotPath = " + getSlotPath() + "\nERROR in: 'private double toDayOfWeek'" + "\ngetMessage =\n" + e.getMessage() + "\ngetStackTrace =\n" + e.getStackTrace() +	"\ntoString =\n" + e.toString());
			return 0;
		}
	}


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

			double julian = (java.lang.Math.floor(365.25 * julianYear)
					+ java.lang.Math.floor(30.6001*julianMonth) + day + 1720995.0);
			
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
			logger.error("\nslotPath = " + getSlotPath() + "\nERROR in: 'private double toJulian'" + "\ngetMessage =\n" + e.getMessage() + "\ngetStackTrace =\n" + e.getStackTrace() +	"\ntoString =\n" + e.toString());
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
			
			if (month > 12) month = month - 12;
			
			year	= jc - 4715;
			
			if (month > 2) year--;
			if (year <= 0) year--;

			return new int[] {year, month, day};
		}
		catch (Exception e) 
		{
			logger.error("\nslotPath = " + getSlotPath() + "\nERROR in: 'public int[] fromJulian'" + "\ngetMessage =\n" + e.getMessage() + "\ngetStackTrace =\n" + e.getStackTrace() +	"\ntoString =\n" + e.toString());
			return new int[] {0, 0, 0};
		}
	}
}
