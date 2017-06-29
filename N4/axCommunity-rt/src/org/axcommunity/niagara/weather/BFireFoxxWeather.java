package org.axcommunity.niagara.weather;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.baja.status.BStatus;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;
import javax.baja.units.BUnit;
import javax.baja.xml.XElem;
import javax.baja.xml.XParser;
import java.net.URL;
import java.net.URLEncoder;

//	Update 6/29/2017 by James Johnson to move to current logger syntax

public class BFireFoxxWeather
extends BComponent
{
  
      protected static Logger log1 = Logger.getLogger("BFireFoxxWeather");
  
      
      public static int[] stateInt = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,3200}; 
      public static String[] stateTag = new String[] {"Tornado","Tropical_Storm","Hurricane","Severe_Thunderstorms","Thunderstorms","Mixed_Rain_And_Snow","Mixed_Rain_And_Sleet","Mixed_Snow_And_Sleet",
          "Freezing_Drizzle","Drizzle","Freezing_Rain","Showers","Showers_","Snow_Flurries","Light_Snow_Showers","Blowing_Snow","Snow","Hail","Sleet","Dust","Foggy","Haze","Smoky","Blustery","Windy",
          "Cold","Cloudy","Mostly_Cloudy_Night","Mostly_Cloudy_Day","Partly_Cloudy_Night","Partly_Cloudy_Day","Clear_Night","Sunny","Fair_Night","Fair_Day","Mixed_Rain_And_Hail","Hot",
          "Isolated_Thunderstorms","Scattered_Thunderstorms","Scattered_Thunderstorms_","Scattered_Showers","Heavy_Snow","Scattered_Snow_Showers","Heavy_Snow_","Partly_Cloudy","Thundershowers",
          "Snow_Showers","Isolated_Thundershowers","Not_Available"};
      public static BEnumRange statesEnum = BEnumRange.make(stateInt,stateTag); 
   
      
      
      
////////////////////////////////////////////////////////////////
//StartUp
////////////////////////////////////////////////////////////////
      public void started()
      throws Exception
      {
        super.started();
        doLocationField();
        updateTimeTimer();
        log1.log(Level.FINE, "Start Up "); 
      }
      
      
////////////////////////////////////////////////////////////////
//Program Variables
////////////////////////////////////////////////////////////////
          
        javax.baja.sys.Clock.Ticket updateTimeTicket;
        String units = "&u=c";
        
////////////////////////////////////////////////////////////////
//Property Change Running
////////////////////////////////////////////////////////////////
          
          public void changed(Property property, Context context)
          {
              if(!isRunning())
                  return;
              
              if(property == location ||property == locationId || property == selectSite)
              {
                  doLocationField();
              }
              
              if(property == refreshTime)
              {
                  updateTimeTimer();
              }
          }  
  
////////////////////////////////////////////////////////////////
//Do On Action
////////////////////////////////////////////////////////////////
  
          public void Refresh()
          {
             invoke(Refresh, null, null);
          }
          
          public void doRefresh() throws Exception
          {
            updateReport();
          }

          
////////////////////////////////////////////////////////////////
//Timers///////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
              
//////////////////////////////////////////UpDate Timer//////////////////////////////////////////////////
              
          void updateTimeTimer()
          {     
              if(updateTimeTicket != null)
              updateTimeTicket.cancel();
              BRelTime breltime = getRefreshTime();
              if(breltime.getMillis() != 0L)
              updateTimeTicket = Clock.schedulePeriodically(this, breltime , RefreshTimerExpired, null);
          }
          
          public void RefreshTimerExpired()
          {
            invoke(RefreshTimerExpired, null, null);
          }

          public void doRefreshTimerExpired()
          {
            updateReport();
            updateTimeTicket.cancel();
            updateTimeTimer();
          }
              
////////////////////////////////////////////////////////////////
//Main
////////////////////////////////////////////////////////////////
      
///////////////////Location Setter////////////////////////////////////////////////
      
      public void doLocationField()
      {
        
        BSiteLocation mode = getSelectSite();
        
        switch(mode.getOrdinal())
        {    
          case BSiteLocation.SASKATOON:
          setString(locationId, "CAXX0442");
          break;
          
          case BSiteLocation.CALGARY:
          setString(locationId, "CAXX0054");
          break;
          
          case BSiteLocation.EDMONTON:
          setString(locationId, "CAXX0126");
          break;
            
          case BSiteLocation.NORTHVANCOUVER:
          setString(locationId, "CAXX0328");
          break;
          
          case BSiteLocation.VANCOUVER:
          setString(locationId, "CAXX0518");
          break;
          
          case BSiteLocation.WINNIPEG:
          setString(locationId, "CAXX0547");
          break;
          
          case BSiteLocation.WHISTLER:
          setString(locationId, "CAXX0538");
          break;
          
          case BSiteLocation.VICTORIA:
          setString(locationId, "CAXX0523");
          break;
        }
          updateReport();
        }

////////////////////////////////// Get Connection////////////////////////////////////////////////////////////////

      private XElem getFeed() 
      throws Exception
    {
      String http = "http://weather.yahooapis.com/forecastrss?p=" + URLEncoder.encode(getLocationId(), "UTF-8");//+ units;
      String address = (http + units);
      String url = BRequest.get(new URL(address));
      XElem root = XParser.make(url).parse();
      log1.log(Level.FINE, "URL " + url);
      log1.log(Level.FINE, "HTTP " + http);
      log1.log(Level.FINE, "Address " + address);
      return root;
    }

////////////////////////////////////////Update Weather////////////////////////////////////////////////
      
      public void updateReport()
      {
        try 
        {     
          log1.log(Level.FINE, "Update Report "); 
          
          // Parse XML
          XElem root = getFeed();
          root = root.elem("channel");
          
          // Geographic location
          XElem location = root.elem("location");
          String city = location.get("city");
          String region = location.get("region");
          String country = location.get("country");
          setLocation(city + ", " + region + " " + country);
          
          // General Conditions
          XElem condition = root.elem("item").elem("condition");
          if (condition != null)
          {
            setTemp(new BStatusNumeric(Integer.parseInt(condition.get("temp"))));
            setWeatherSummary(new BStatusString(condition.get("text")));
            setDateTime(new BStatusString(condition.get("date")));
            BFoxxWeatherState weatherState = BFoxxWeatherState.make(Integer.parseInt(condition.get("code")));
            setState (new BStatusEnum (weatherState));
          }
            
            //Atmospheric Conditions
            XElem atmosphere = root.elem("atmosphere");
            if (atmosphere != null)
            {
              setHumidity(new BStatusNumeric(Integer.parseInt(atmosphere.get("humidity"))));
              setBarometricPressure(new BStatusNumeric(Float.parseFloat(atmosphere.get("pressure"))));
              BPressureState.variable.pressureState = atmosphere.get("rising");
              BPressureState pressureState = BPressureState.makeDegrees(Integer.parseInt(atmosphere.get("rising")));
              setBarometricPressureState (new BStatusEnum (pressureState));
            }
            
            //Astronomy
            XElem astronomy = root.elem("astronomy");
            if (astronomy != null)
            {
              setSunrise(astronomy.get("sunrise"));
              setSunset(astronomy.get("sunset"));
            }
            
            // Wind conditions
            XElem wind = root.elem("wind");
            if (wind != null)
            {
              setWindChill(new BStatusNumeric(Integer.parseInt(wind.get("chill"))));
              setWindSpeed (new BStatusNumeric (Float.parseFloat(wind.get("speed"))));
              BWindDirection.variable.windDirection = wind.get("direction");
              BWindDirection windDirection = BWindDirection.makeDegrees(Integer.parseInt(wind.get("direction")));
              setWindDirection (new BStatusEnum (windDirection));
            }  

        }
          catch (Exception localException) 
          {
            throw new RuntimeException(localException);
          }
          updateForecast();
      }
            
////////////////////////////////////////Update Forecast//////////////////////////////////////////////// 
     
      // Forecasts
      public void updateForecast()
      {
        try 
        {
          // Parse XML
          XElem root = getFeed();
          root = root.elem("channel");
        
            XElem[] forecasts = root.elem("item").elems("forecast");
            for (int i = 0; i < forecasts.length; i++)
            {
              String str1;
              String str2;
              setTomorrowsHigh(new BStatusNumeric(Integer.parseInt(forecasts[i].get("high"))));
              setTomorrowsLow(new BStatusNumeric(Integer.parseInt(forecasts[i].get("low"))));
              setTomorrowsWeatherSummary(new BStatusString(forecasts[i].get("text")));
              str1 = (forecasts[i].get("day"));
              str2 = (forecasts[i].get("date"));
              setTomorrow(str1 + ", " + str2 + " ");
            }
          }
              catch (Exception localException) 
              {
                throw new RuntimeException(localException);
              }
        }
      
////////////////////////////////////////////////////////////////
//Properties
////////////////////////////////////////////////////////////////
           
////////////////////////////////////////////////////////////////
//Facets
////////////////////////////////////////////////////////////////
  
////////////////////////////////////////////////////////////////
//Actions
////////////////////////////////////////////////////////////////
  
      public static final Action Refresh = newAction(0, null);
      public static final Action RefreshTimerExpired = newAction(4, null);
  
////////////////////////////////////////////////////////////////
//Main Properties
////////////////////////////////////////////////////////////////
      
      public static final Property dateTime = newProperty(3, new BStatusString("", BStatus.nullStatus), null);
      public BStatusString getDateTime(){return (BStatusString)get(dateTime);}
      public void setDateTime(BStatusString paramBStatusString){set(dateTime, paramBStatusString, null);}
      
      public static final Property location = newProperty(Flags.READONLY, "", null);
      public String getLocation(){return getString(location);}
      public void setLocation(String v){setString(location, v, null);}
      
      public static final Property locationId = newProperty(0, "CAXX0328", null);
      public String getLocationId(){return getString(locationId);}
      public void setLocationId(String v){setString(locationId, v, null);}
      
      public static final Property selectSite = newProperty(Flags.SUMMARY, BSiteLocation.NorthVancouver,null);
      public BSiteLocation getSelectSite() { return (BSiteLocation)get(selectSite); }
      public void setSelectSite(BSiteLocation v) { set(selectSite,v,null); }
      
      public static final Property sunrise = newProperty(Flags.READONLY, "", null);
      public String getSunrise(){return getString(sunrise);}
      public void setSunrise(String v){setString(sunrise, v, null);}
      
      public static final Property sunset = newProperty(Flags.READONLY, "", null);
      public String getSunset(){return getString(sunset);}
      public void setSunset(String v){setString(sunset, v, null);}

      public static final Property temp = newProperty(3, new BStatusNumeric(0.0D, BStatus.nullStatus), BFacets.make("units", BUnit.getUnit("celsius"), "precision", BInteger.make(1)));
      public BStatusNumeric getTemp(){return (BStatusNumeric)get(temp);}
      public void setTemp(BStatusNumeric paramBStatusNumeric){set(temp, paramBStatusNumeric, null);}

      public static final Property weatherSummary = newProperty(3, new BStatusString("", BStatus.nullStatus), null);
      public BStatusString getWeatherSummary(){return (BStatusString)get(weatherSummary);}
      public void setWeatherSummary(BStatusString paramBStatusString){set(weatherSummary, paramBStatusString, null);}

      public static final Property state = newProperty(8|Flags.READONLY, new BStatusEnum(), BFacets.makeEnum(statesEnum) );
      
      public BStatusEnum getState(){return (BStatusEnum)get(state);}
      public void setState(BStatusEnum paramBStatusEnum){set(state, paramBStatusEnum, null);}

      public static final Property humidity = newProperty(3, new BStatusNumeric(0.0D, BStatus.nullStatus), BFacets.make("units", BUnit.getUnit("percent relative humidity"), "precision", BInteger.make(1)));
      public BStatusNumeric getHumidity(){return (BStatusNumeric)get(humidity);}
      public void setHumidity(BStatusNumeric paramBStatusNumeric){set(humidity, paramBStatusNumeric, null);}
      

      
      

      public static final Property windDirection = newProperty(8|Flags.READONLY, new BStatusEnum(), BFacets.makeEnum(BEnumRange.make(BWindDirection.TYPE))); 
      public BStatusEnum getWindDirection() { return (BStatusEnum)get(windDirection); } 
      public void setWindDirection(BStatusEnum v) { set(windDirection,v,null); }

      public static final Property windSpeed = newProperty(3, new BStatusNumeric(0.00D, BStatus.nullStatus), BFacets.make("units", BUnit.getUnit("kilometers per hour"), "precision", BInteger.make(2)));
      public BStatusNumeric getWindSpeed(){return (BStatusNumeric)get(windSpeed);}
      public void setWindSpeed(BStatusNumeric paramBStatusNumeric){set(windSpeed, paramBStatusNumeric, null);}

      public static final Property windChill = newProperty(3, new BStatusNumeric(0.0D, BStatus.nullStatus), BFacets.make("units", BUnit.getUnit("celsius"), "precision", BInteger.make(1)));
      public BStatusNumeric getWindChill(){return (BStatusNumeric)get(windChill);}
      public void setWindChill(BStatusNumeric paramBStatusNumeric){set(windChill, paramBStatusNumeric, null);}
      
      public static final Property barometricPressure = newProperty(3, new BStatusNumeric(0.0D, BStatus.nullStatus), BFacets.make("units", BUnit.getUnit("inches of mercury")));
      public BStatusNumeric getBarometricPressure(){return (BStatusNumeric)get(barometricPressure);}
      public void setBarometricPressure(BStatusNumeric paramBStatusNumeric){set(barometricPressure, paramBStatusNumeric, null);}
      
      public static final Property barometricPressureState = newProperty(8|Flags.READONLY, new BStatusEnum(), BFacets.makeEnum(BEnumRange.make(BPressureState.TYPE))); 
      public BStatusEnum getBarometricPressureState() { return (BStatusEnum)get(barometricPressureState); } 
      public void setBarometricPressureState(BStatusEnum v) { set(barometricPressureState,v,null); }
      
      public static final Property tomorrow = newProperty(Flags.READONLY, "", null);
      public String getTomorrow(){return getString(tomorrow);}
      public void setTomorrow(String v){setString(tomorrow, v, null);}
      
      public static final Property tomorrowsHigh = newProperty(3, new BStatusNumeric(0.0D, BStatus.nullStatus), BFacets.make("units", BUnit.getUnit("celsius"), "precision", BInteger.make(1)));
      public BStatusNumeric getTomorrowsHigh(){return (BStatusNumeric)get(tomorrowsHigh);}
      public void setTomorrowsHigh(BStatusNumeric paramBStatusNumeric){set(tomorrowsHigh, paramBStatusNumeric, null);}
      
      public static final Property tomorrowsLow = newProperty(3, new BStatusNumeric(0.0D, BStatus.nullStatus), BFacets.make("units", BUnit.getUnit("celsius"), "precision", BInteger.make(1)));
      public BStatusNumeric getTomorrowsLow(){return (BStatusNumeric)get(tomorrowsLow);}
      public void setTomorrowsLow(BStatusNumeric paramBStatusNumeric){set(tomorrowsLow, paramBStatusNumeric, null);}
      
      public static final Property tomorrowsWeatherSummary = newProperty(3, new BStatusString("", BStatus.nullStatus), null);
      public BStatusString getTomorrowsWeatherSummary(){return (BStatusString)get(tomorrowsWeatherSummary);}
      public void setTomorrowsWeatherSummary(BStatusString paramBStatusString){set(tomorrowsWeatherSummary, paramBStatusString, null);}
      
      public static final Property refreshTime = newProperty(0, BRelTime.make(3600000L), null);
      public BRelTime getRefreshTime(){return (BRelTime)get(refreshTime);}
      public void setRefreshTime(BRelTime breltime){set(refreshTime, breltime, null);}
      
////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////

      
      public Type getType() { return TYPE; }
      public static final Type TYPE = Sys.loadType(BFireFoxxWeather.class);
      
      public BIcon getIcon()
      {
        return icon;
      }

      private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/firefoxx.png");
}
