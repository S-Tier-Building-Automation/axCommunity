package org.axcommunity.niagara.weather;

import javax.baja.sys.BFrozenEnum;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public final class BWindDirection
extends BFrozenEnum
{
  
////////////////////////////////////////////////////////////////
//Program Variables
////////////////////////////////////////////////////////////////
  
      public static final int NORTH = 0;
      public static final int NORTHEAST = 1;
      public static final int EAST = 2;
      public static final int SOUTHEAST = 3;
      public static final int SOUTH = 4;
      public static final int SOUTHWEST = 5;
      public static final int WEST = 6;
      public static final int NORTHWEST = 7;
      public static final int VARIABLE = 8;
      
      public static final BWindDirection north = new BWindDirection(0);

      public static final BWindDirection northeast = new BWindDirection(1);

      public static final BWindDirection east = new BWindDirection(2);

      public static final BWindDirection southeast = new BWindDirection(3);

      public static final BWindDirection south = new BWindDirection(4);

      public static final BWindDirection southwest = new BWindDirection(5);

      public static final BWindDirection west = new BWindDirection(6);

      public static final BWindDirection northwest = new BWindDirection(7);

      public static final BWindDirection variable = new BWindDirection(8);
      
      //public static final BWindDirection DEFAULT = north;
      
      String windDirection = "";

////////////////////////////////////////////////////////////////
//Do On Action
////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////
//Main
////////////////////////////////////////////////////////////////
  
      public static final BWindDirection make(int paramInt)
      {
        return (BWindDirection)north.getRange().get(paramInt, false);
      }

      public static final BWindDirection make(String paramString)
      {
        return (BWindDirection)north.getRange().get(paramString);
      }

      public static final BWindDirection makeDegrees(int paramInt)
      {
        if ((paramInt >= 0) && (paramInt <= 11.25D))
          return north;
        if ((paramInt > 11.25D) && (paramInt <= 78.75D))
          return northeast;
        if ((paramInt > 78.75D) && (paramInt <= 101.25D))
          return east;
        if ((paramInt > 101.25D) && (paramInt <= 168.75D))
          return southeast;
        if ((paramInt > 168.75D) && (paramInt <= 191.25D))
          return south;
        if ((paramInt > 191.25D) && (paramInt <= 258.75D))
          return southwest;
        if ((paramInt > 258.75D) && (paramInt <= 281.25D))
          return west;
        if ((paramInt > 281.25D) && (paramInt <= 348.75D))
          return northwest;
        if ((paramInt > 348.75D) && (paramInt <= 360)) {
          return north;
        }
        return variable;
      }
      
      private BWindDirection(int paramInt)
      {
        super(paramInt);
      }
      
////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////

      
      public Type getType() { return TYPE; }
      public static final Type TYPE = Sys.loadType(BWindDirection.class);
}
