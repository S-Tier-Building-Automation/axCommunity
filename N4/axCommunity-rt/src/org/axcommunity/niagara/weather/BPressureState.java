/* SPDX-License-Identifier: GPL-2.0-only */

package org.axcommunity.niagara.weather;

import javax.baja.sys.BFrozenEnum;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

/**
 * Frozen enum of barometric-pressure trend states ({@code steady},
 * {@code rising}, {@code falling}, {@code variable}) backing
 * {@link BFireFoxxWeather}'s {@code barometricPressureState} slot.
 *
 * <p>{@code makeDegrees} maps the feed's numeric rising code: 0 steady,
 * 1 rising, 2 falling, anything else variable.</p>
 *
 * @author Ron Lea, FireFoxx Controls
 */
public final class BPressureState
extends BFrozenEnum
{
    
////////////////////////////////////////////////////////////////
//Program Variables
////////////////////////////////////////////////////////////////
    
        public static final int STEADY = 0;
        public static final int RISING  = 1;
        public static final int FALLING = 2;
        public static final int VARIABLE = 3;

        
        public static final BPressureState steady = new BPressureState(0);

        public static final BPressureState rising  = new BPressureState(1);

        public static final BPressureState falling = new BPressureState(2);
        
        public static final BPressureState variable = new BPressureState(3);
        
        //public static final BPressureState DEFAULT = steady;
        
        String pressureState = "";

  ////////////////////////////////////////////////////////////////
  //Do On Action
  ////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////
//Main
////////////////////////////////////////////////////////////////
    
        public static final BPressureState make(int paramInt)
        {
          return (BPressureState)steady.getRange().get(paramInt, false);
        }

        public static final BPressureState make(String paramString)
        {
          return (BPressureState)steady.getRange().get(paramString);
        }

        public static final BPressureState makeDegrees(int paramInt)
        {
          if ((paramInt == 0))
            return steady;
          if ((paramInt == 1))
            return rising;
          if ((paramInt == 2))
            return falling;
          
            return variable;
        }
        
        private BPressureState(int paramInt)
        {
          super(paramInt);
        }
        
////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////

        
        public Type getType() { return TYPE; }
        public static final Type TYPE = Sys.loadType(BPressureState.class);
  
}
