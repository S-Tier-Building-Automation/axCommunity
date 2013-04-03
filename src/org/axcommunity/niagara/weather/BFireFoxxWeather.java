package org.axcommunity.niagara.weather;
import java.net.URL;
import java.net.URLEncoder;
import javax.baja.log.Log;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BRelTime;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.units.BUnit;
import javax.baja.xml.XElem;
import javax.baja.xml.XParser;


public class BFireFoxxWeather
extends BComponent
{
  
      protected static Log log1 = Log.getLog("BFireFoxxWeather");
  
////////////////////////////////////////////////////////////////
//StartUp
////////////////////////////////////////////////////////////////
      public void started()
      throws Exception
      {
        super.started();
        doLocationField();
        updateTimeTimer();
        log1.trace("Start Up "); 
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
      log1.trace("URL " + url);
      log1.trace("HTTP " + http);
      log1.trace("Address " + address);
      return root;
    }

////////////////////////////////////////Update Weather////////////////////////////////////////////////
      
      public void updateReport()
      {
        try 
        {     
          log1.trace("Update Report "); 
          
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

      public static final Property state = newProperty(8|Flags.READONLY, new BStatusEnum(), BFacets.tryMake("range=E:{Tornado=0,Tropical_Storm=1,Hurricane=2,Severe_Thunderstorms=3,Thunderstorms=4,Mixed_Rain_And_Snow=5,Mixed_Rain_And_Sleet=6,Mixed_Snow_And_Sleet=7,Freezing_Drizzle=8,Drizzle=9,Freezing_Rain=10," +
      "Showers=11,Showers=12,Snow_Flurries=13,Light_Snow_Showers=14,Blowing_Snow=15,Snow=16,Hail=17,Sleet=18,Dust=19,Foggy=20," +
      "Haze=21,Smoky=22,Blustery=23,Windy=24,Cold=25,Cloudy=26,Mostly_Cloudy_Night=27,Mostly_Cloudy_Day=28,Partly_Cloudy_Night=29,Partly_Cloudy_Day=30," +
      "Clear_Night=31,Sunny=32,Fair_Night=33,Fair_Day=34,Mixed_Rain_And_Hail=35,Hot=36,Isolated_Thunderstorms=37,Scattered_Thunderstorms=38,Scattered_Thunderstorms=39,Scattered_Showers=40," +
      "Heavy_Snow=41,Scattered_Snow_Showers=42,Heavy_Snow=43,Partly_Cloudy=44,Thundershowers=45,Snow_Showers=46,Isolated_Thundershowers=47,Not_Available=3200}"));
      
      public BStatusEnum getState(){return (BStatusEnum)get(state);}
      public void setState(BStatusEnum paramBStatusEnum){set(state, paramBStatusEnum, null);}

      public static final Property humidity = newProperty(3, new BStatusNumeric(0.0D, BStatus.nullStatus), BFacets.make("units", BUnit.getUnit("percent relative humidity"), "precision", BInteger.make(1)));
      public BStatusNumeric getHumidity(){return (BStatusNumeric)get(humidity);}
      public void setHumidity(BStatusNumeric paramBStatusNumeric){set(humidity, paramBStatusNumeric, null);}
      
      public static final Property windDirection = newProperty(8|Flags.READONLY, new BStatusEnum(), BFacets.tryMake("range=E:{North=0,NorthEast=1,East=2,SouthEast=3,South=4,SouthWest=5,West=6,NorthWest=7,Variable=8}")); 
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
      
      public static final Property barometricPressureState = newProperty(8|Flags.READONLY, new BStatusEnum(), BFacets.tryMake("range=E:{Steady=0,Rising=1,Falling=2,NotValid=3}")); 
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

      private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/firefoxx.png");
}
