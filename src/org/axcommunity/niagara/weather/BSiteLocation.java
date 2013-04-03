package org.axcommunity.niagara.weather;


import javax.baja.sys.BFrozenEnum;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;


public final class BSiteLocation
extends BFrozenEnum
{
  
////////////////////////////////////////////////////////////////
//Program Variables
////////////////////////////////////////////////////////////////
 
      public static final int WHISTLER = 0;
      public static final int CALGARY = 1;
      public static final int EDMONTON = 2;
      public static final int NORTHVANCOUVER = 3;
      public static final int VANCOUVER = 4;
      public static final int VICTORIA = 5;
      public static final int WINNIPEG = 6;
      public static final int SASKATOON = 7;

      
      public static final BSiteLocation Whistler = new BSiteLocation(WHISTLER);
      public static final BSiteLocation Calgary = new BSiteLocation(CALGARY);
      public static final BSiteLocation Edmonton = new BSiteLocation(EDMONTON);
      public static final BSiteLocation NorthVancouver = new BSiteLocation(NORTHVANCOUVER);
      public static final BSiteLocation Vancouver = new BSiteLocation(VANCOUVER);
      public static final BSiteLocation Victoria = new BSiteLocation(VICTORIA);
      public static final BSiteLocation Winnipeg = new BSiteLocation(WINNIPEG);
      public static final BSiteLocation Saskatoon = new BSiteLocation(SASKATOON);
      
////////////////////////////////////////////////////////////////
//Main/////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
      
      public static BSiteLocation make(int ordinal)
      {
        return (BSiteLocation)NorthVancouver.getRange().get(ordinal, false);
      }
  
      public static BSiteLocation make(String tag)
      {
        return (BSiteLocation)NorthVancouver.getRange().get(tag);
      }
  
      private BSiteLocation(int ordinal)
      {
        super(ordinal);
      }
  
      public static final BSiteLocation DEFAULT = BSiteLocation.NorthVancouver;
  
////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////
      
      public Type getType() { return TYPE; }
      public static final Type TYPE = Sys.loadType(BSiteLocation.class);
      
      
}
