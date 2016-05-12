package org.axcommunity.niagara.weather;

import javax.baja.sys.BFrozenEnum;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public  final class BFoxxWeatherState
extends BFrozenEnum
{

////////////////////////////////////////////////////////////////
//Program Variables
////////////////////////////////////////////////////////////////
  
      public static final int TORNADO = 0;
      public static final int TROPICAL_STORM = 1;
      public static final int HURRICANE = 2;
      public static final int SEVERE_THUNDERSTORMS = 3;
      public static final int THUNDERSTORMS = 4;
      public static final int MIXED_RAIN_AND_SNOW = 5;
      public static final int MIXED_RAIN_AND_SLEET = 6;
      public static final int MIXED_SNOW_AND_SLEET = 7;
      public static final int FREEZING_DRIZZLE = 8;
      public static final int DRIZZLE = 9;
      public static final int FREEEZING_RAIN = 10;
      public static final int SHOWERS1 = 11;
      public static final int SHOWERS2 = 12;
      public static final int SNOW_FLURRIES = 13;
      public static final int LIGHT_SNOW_SHOWERS = 14;
      public static final int BLOWING_SNOW = 15;
      public static final int SNOW = 16;
      public static final int HAIL = 17;
      public static final int SLEET = 18;
      public static final int DUST = 19;
      public static final int FOGGY = 20;
      public static final int HAZE = 21;
      public static final int SMOKY = 22;
      public static final int BLUSTERY = 23;
      public static final int WINDY = 24;
      public static final int COLD = 25;
      public static final int CLOUDY = 26;
      public static final int MOSTLY_CLOUDY_NIGHT = 27;
      public static final int MOSTLY_CLOUDY_DAY = 28;
      public static final int PARTLY_CLOUDDY_NIGHT = 29;
      public static final int PARTLY_CLOUDDY_DAY = 30;
      public static final int CLEAR_NIGHT = 31;
      public static final int SUNNY = 32;
      public static final int FAIR_NIGHT = 33;
      public static final int FAIR_DAY = 34;
      public static final int MIXED_RAIN_AND_HAIL = 35;
      public static final int HOT = 36;
      public static final int ISOLATED_THUNDERSTORMS = 37;
      public static final int SCATTERED_THUNDERSTORMS1 = 38;
      public static final int SCATTERED_THUNDERSTORMS2 = 39;
      public static final int SCATTERED_SHOWERS = 40;
      public static final int HEAVY_SNOW1 = 41;
      public static final int SCATTERED_SNOW_SHOWERS = 42;
      public static final int HEAVY_SNOW2 = 43;
      public static final int PARTLY_CLOUDY = 44;
      public static final int THUNDERSHOWERS = 45;
      public static final int SNOW_SHOWERS = 46;
      public static final int ISOLATED_THUNDERSHOWERS = 47;
      public static final int NOT_AVAILABLE = 3200;

      
      //Code  Description For Yahoo Weather State
      //0 tornado
      //1 tropical storm
      //2 hurricane
      //3 severe thunderstorms
      //4 thunderstorms
      //5 mixed rain and snow
      //6 mixed rain and sleet
      //7 mixed snow and sleet
      //8 freezing drizzle
      //9 drizzle
      //10  freezing rain
      //11  showers
      //12  showers
      //13  snow flurries
      //14  light snow showers
      //15  blowing snow
      //16  snow
      //17  hail
      //18  sleet
      //19  dust
      //20  foggy
      //21  haze
      //22  smoky
      //23  blustery
      //24  windy
      //25  cold
      //26  cloudy
      //27  mostly cloudy (night)
      //28  mostly cloudy (day)
      //29  partly cloudy (night)
      //30  partly cloudy (day)
      //31  clear (night)
      //32  sunny
      //33  fair (night)
      //34  fair (day)
      //35  mixed rain and hail
      //36  hot
      //37  isolated thunderstorms
      //38  scattered thunderstorms
      //39  scattered thunderstorms
      //40  scattered showers
      //41  heavy snow
      //42  scattered snow showers
      //43  heavy snow
      //44  partly cloudy
      //45  thundershowers
      //46  snow showers
      //47  isolated thundershowers
      //3200  not available
      
      
      
      public static final BFoxxWeatherState tornado = new BFoxxWeatherState(0);

      public static final BFoxxWeatherState tropical_storm = new BFoxxWeatherState(1);

      public static final BFoxxWeatherState hurricane = new BFoxxWeatherState(2);

      public static final BFoxxWeatherState severe_thunderstorms = new BFoxxWeatherState(3);

      public static final BFoxxWeatherState thunderstorms = new BFoxxWeatherState(4);

      public static final BFoxxWeatherState mixed_rain_and_snow = new BFoxxWeatherState(5);

      public static final BFoxxWeatherState mixed_rain_and_sleet = new BFoxxWeatherState(6);

      public static final BFoxxWeatherState mixed_snow_and_sleet = new BFoxxWeatherState(7);

      public static final BFoxxWeatherState freezing_drizzle = new BFoxxWeatherState(8);

      public static final BFoxxWeatherState drizzle = new BFoxxWeatherState(9);

      public static final BFoxxWeatherState freezing_rain = new BFoxxWeatherState(10);

      public static final BFoxxWeatherState showers1 = new BFoxxWeatherState(11);

      public static final BFoxxWeatherState showers2 = new BFoxxWeatherState(12);

      public static final BFoxxWeatherState snow_flurries = new BFoxxWeatherState(13);

      public static final BFoxxWeatherState light_snow_showers = new BFoxxWeatherState(14);

      public static final BFoxxWeatherState blowing_snow = new BFoxxWeatherState(15);

      public static final BFoxxWeatherState snow = new BFoxxWeatherState(16);

      public static final BFoxxWeatherState hail = new BFoxxWeatherState(17);

      public static final BFoxxWeatherState sleet = new BFoxxWeatherState(18);

      public static final BFoxxWeatherState dust = new BFoxxWeatherState(19);

      public static final BFoxxWeatherState foggy = new BFoxxWeatherState(20);

      public static final BFoxxWeatherState haze = new BFoxxWeatherState(21);

      public static final BFoxxWeatherState smoky = new BFoxxWeatherState(22);

      public static final BFoxxWeatherState blustery = new BFoxxWeatherState(23);

      public static final BFoxxWeatherState windy = new BFoxxWeatherState(24);

      public static final BFoxxWeatherState cold = new BFoxxWeatherState(25);

      public static final BFoxxWeatherState cloudy = new BFoxxWeatherState(26);

      public static final BFoxxWeatherState mostly_cloudy_night = new BFoxxWeatherState(27);

      public static final BFoxxWeatherState mostly_cloudy_day = new BFoxxWeatherState(28);
      
      public static final BFoxxWeatherState Partly_Cloudy_Night = new BFoxxWeatherState(29);
      
      public static final BFoxxWeatherState Partly_Cloudy_Day = new BFoxxWeatherState(30);
      
      public static final BFoxxWeatherState clear_night = new BFoxxWeatherState(31);
      
      public static final BFoxxWeatherState sunny = new BFoxxWeatherState(32);
      
      public static final BFoxxWeatherState fair_night = new BFoxxWeatherState(33);
      
      public static final BFoxxWeatherState fair_day = new BFoxxWeatherState(34);
      
      public static final BFoxxWeatherState mixed_rain_and_hail = new BFoxxWeatherState(35);
      
      public static final BFoxxWeatherState hot = new BFoxxWeatherState(36);
      
      public static final BFoxxWeatherState isolated_thunderstorms = new BFoxxWeatherState(37);
      
      public static final BFoxxWeatherState scattered_thunderstorms1 = new BFoxxWeatherState(38);
      
      public static final BFoxxWeatherState scattered_thunderstorms2 = new BFoxxWeatherState(39);
      
      public static final BFoxxWeatherState scattered_showers = new BFoxxWeatherState(40);
      
      public static final BFoxxWeatherState heavy_snow1 = new BFoxxWeatherState(41);
      
      public static final BFoxxWeatherState scattered_snow_showers = new BFoxxWeatherState(42);
      
      public static final BFoxxWeatherState heavy_snow2 = new BFoxxWeatherState(43);
      
      public static final BFoxxWeatherState partly_cloudy = new BFoxxWeatherState(44);
      
      public static final BFoxxWeatherState thundershowers = new BFoxxWeatherState(45);
      
      public static final BFoxxWeatherState snow_showers = new BFoxxWeatherState(46);
      
      public static final BFoxxWeatherState isolated_thundershowers = new BFoxxWeatherState(47);
      
      public static final BFoxxWeatherState not_available = new BFoxxWeatherState(3200);

      public static final BFoxxWeatherState DEFAULT = not_available;
      
      String weatherState = "";
      
////////////////////////////////////////////////////////////////
//Do On Action
////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////
//Main
////////////////////////////////////////////////////////////////
      
      public static final BFoxxWeatherState make(int paramInt)
      {
        return (BFoxxWeatherState)not_available.getRange().get(paramInt, false);
      }

      public static final BFoxxWeatherState make(String paramString)
      {
        return (BFoxxWeatherState)not_available.getRange().get(paramString);
      }

      public static final BFoxxWeatherState makeDegrees(int paramInt)
      {
        if ((paramInt == 0))
          return tornado;
        if ((paramInt == 1))
          return tropical_storm;
        if ((paramInt == 2))
          return hurricane;
        if ((paramInt == 3))
          return severe_thunderstorms;
        if ((paramInt == 4))
          return thunderstorms;
        if ((paramInt == 5))
          return mixed_rain_and_snow;
        if ((paramInt == 6))
          return mixed_rain_and_sleet;
        if ((paramInt == 7))
          return mixed_snow_and_sleet;
        if ((paramInt == 8))
          return freezing_drizzle;
        if ((paramInt == 9))
          return drizzle;
        if ((paramInt == 10))
          return freezing_rain;
        if ((paramInt == 11))
          return showers1;
        if ((paramInt == 12))
          return showers2;
        if ((paramInt == 13))
          return snow_flurries;
        if ((paramInt == 14))
          return light_snow_showers;
        if ((paramInt == 15))
          return blowing_snow;
        if ((paramInt == 16))
          return snow;
        if ((paramInt == 17))
          return hail;
        if ((paramInt == 18))
          return sleet;
        if ((paramInt == 19))
          return dust;
        if ((paramInt == 20))
          return foggy;
        if ((paramInt == 21))
          return haze;
        if ((paramInt == 22))
          return smoky;
        if ((paramInt == 23))
          return blustery;
        if ((paramInt == 24))
          return windy;
        if ((paramInt == 25))
          return cold;
        if ((paramInt == 26))
          return cloudy;
        if ((paramInt == 27))
          return mostly_cloudy_night;
        if ((paramInt == 28))
          return mostly_cloudy_day;
        if ((paramInt == 29))
          return Partly_Cloudy_Night;
        if ((paramInt == 30))
          return Partly_Cloudy_Day;
        if ((paramInt == 31))
          return clear_night;
        if ((paramInt == 32))
          return sunny;
        if ((paramInt == 33))
          return fair_night;
        if ((paramInt == 34))
          return fair_day;
        if ((paramInt == 35))
          return mixed_rain_and_hail;
        if ((paramInt == 36))
          return hot;
        if ((paramInt == 37))
          return isolated_thunderstorms;
        if ((paramInt == 38))
          return scattered_thunderstorms1;
        if ((paramInt == 39))
          return scattered_thunderstorms2;
        if ((paramInt == 40))
          return scattered_showers;
        if ((paramInt == 41))
          return heavy_snow1;
        if ((paramInt == 42))
          return scattered_snow_showers;
        if ((paramInt == 43))
          return heavy_snow2;
        if ((paramInt == 44))
          return partly_cloudy;
        if ((paramInt == 45))
          return thundershowers;
        if ((paramInt == 46))
          return snow_showers;
        if ((paramInt == 47))
          return isolated_thundershowers;
        if ((paramInt == 3200))
          return not_available;
        
          return not_available;
      }

      private BFoxxWeatherState(int paramInt)
      {
        super(paramInt);
      }
      
////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////

      
      public Type getType() { return TYPE; }
      public static final Type TYPE = Sys.loadType(BFoxxWeatherState.class);
}
