package org.axcommunity.niagara.weather;

import javax.baja.sys.BFrozenEnum;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

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
