package org.axcommunity.niagara.extensions;

import java.util.*;

import java.io.*;
import javax.baja.sys.*;
import javax.baja.status.*;
import javax.baja.control.*;
import javax.baja.control.enums.*;
import javax.baja.units.*;
import javax.baja.util.*;
import java.util.*;

/**
 * The BFilter is a standard point extension
 * that takes the value of a numeric point and applies
 * a filter function
 *
 * @author    Dean Mynott       - Ronin Control Systems Pty Ltd
 * @creation  16 June 2011
 */
public class BFilterExt extends BPointExtension implements Runnable
{

////////////////////////////////////////////////////////////////
// Runnable
////////////////////////////////////////////////////////////////

  public void run() { System.out.println("Source BProgram did not override run(). Exiting thread."); }

  
////////////////////////////////////////////////////////////////
// Property "Filter"
////////////////////////////////////////////////////////////////
//  public static final Property filter = newProperty(Flags.SUMMARY, new BStatusNumeric(95, BStatus.nullStatus),BFacets.make(BFacets.MIN, BInteger.make(0),BFacets.MAX, BInteger.make(100)));
  public static final Property filter = newProperty(Flags.SUMMARY, new BStatusNumeric(5),BFacets.make(BFacets.MIN, BInteger.make(0),BFacets.MAX, BInteger.make(100)));
  public BStatusNumeric getFilter() { return (BStatusNumeric)get(filter); }
  public void setFilter(BStatusNumeric v) { set(filter,v,null); }


////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BFilterExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////

  public BFilterExt()
  {
  }

////////////////////////////////////////////////////////////////
//  Parent checking
////////////////////////////////////////////////////////////////

  /**
   * Forces the parent to be a numeric point.
   */

  public boolean isParentLegal(BComponent parent)
  {
    if (parent instanceof BNumericPoint)   
      return true;
    else
      return false;
  }

////////////////////////////////////////////////////////////////
// Update Methods
////////////////////////////////////////////////////////////////

  public void changed(Property p, Context cx)
  {
   super.changed(p, cx);
   if(!isRunning())
    return;
   if( p.equals(filter) )
   {
    BControlPoint parent = getParentPoint();
    if(parent != null) getParentPoint().execute();
   }
  }

  /**
   * Called when either me or my parent control point is updated.
   */

  public void onExecute(BStatusValue o, Context cx)
  {

   //System.out.println("EXECUTE WAS CALLED" );
   
   BStatusNumeric out = (BStatusNumeric)o;                   //parent output
  
//   for( i=2; i>0; i--){
//          y[i] = y[i-1]   ;                                  //shift output values
//          x[i] = x[i-1]   ;                                  //shift input values
//          }
   x[2] = x[1] = x[0] ;    
   y[2] = y[1] = y[0] ;  
   
   f    = getFilter().getValue()/100;                        //get the filter const
      
   x[0] = out.getValue();                                    //current input value from parent
   
   double gain = (1-f)/ (f+1);
   double a    =    f / (f+1);                         
 
   //out = gain * previous_out + a * (input + previous_in);  //
   y[0] =   gain * y[1]        + a * (x[0]  + x[1]);         // calculate new value
      
   o.setValueValue(BDouble.make(y[0]));                      //write to parent.valuevalue
       
    //getParentPoint().execute();

   }

 
  public void onStart() throws Exception
  {
  }
  
  public void onStop() throws Exception
  {
  }


    //icon for this component
    public BIcon getIcon() { return icon; }
        private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png"); 

                
////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////
  double [] y = new double[3];
  double [] x = new double[3];
  int       i = 0;
  double f;
}  


/***************************************************    

One-Liner IIR Filters (1st order)

Type : IIR 1-pole
References : Posted by chris at ariescode dot com

Notes : 
Here is a collection of one liner IIR filters.
Each filter has been transformed into a single C++ expression.

The filter parameter is f or g, and the state variable that needs to be kept around between interations is s.

- Christian

Code : 
    101 Leaky Integrator

        a0 = 1
        b1 = 1 - f

        out = s += in - f * s;


    102 Basic Lowpass (all-pole)

        A first order lowpass filter, by finite difference appoximation (differentials --> differences).

        a0 = f
    b1 = 1 - f

        out = s += f * ( in - s );


    103 Lowpass with inverted control

    Same as above, except for different filter parameter is now inverted.
        In this case, g equals the location of the pole.

        a0 = g - 1
    b1 = g

        out = s = in + g * ( s - in );


    104 Lowpass with zero at Nyquist

        A first order lowpass filter, by via the conformal map of the z-plane (0..infinity --> 0..Nyquist).

        a0 = f
        a1 = f
        b1 = 1 - 2 * f

    s = temp + ( out = s + ( temp = f * ( in - s ) ) );


    105 Basic Highpass (DC-blocker)

        Input complement to basic lowpass, yields a finite difference highpass filter.

        a0 = 1 - f
        a1 = f - 1
        b1 = 1 - f

        out = in - ( s += f * ( in - s ) );


    106 Highpass with forced unity gain at Nyquist

        Input complement to filter 104, yields a conformal map highpass filter.

        a0 = 1 - f
        a1 = f - 1
        b1 = 1 - 2 * f

        out = in + temp - ( s += 2 * ( temp = f * ( in - s ) ) );


    107 Basic Allpass

        This corresponds to a first order allpass filter,
        where g is the location of the pole in the range -1..1.

        a0 = -g
        a1 = 1
        b1 = g

    s = in + g * ( out = s - g * in );


 ********************************************************/




