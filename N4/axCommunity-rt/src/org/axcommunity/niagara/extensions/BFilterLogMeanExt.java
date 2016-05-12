package org.axcommunity.niagara.extensions;

//import java.util.*;

//import java.io.*;

import javax.baja.control.*;
import javax.baja.status.*;
import javax.baja.sys.*;
//import javax.baja.control.enums.*;
//import javax.baja.units.*;
//import javax.baja.util.*;
//import java.util.*;
//import com.tridium.kitControl.math.*;

/**
 * The BFilter is a standard point extension
 * that takes the value of a numeric point and applies
 * a Log Mean filter function
 *
 * @author    Dean Mynott       - Ronin Control Systems Pty Ltd
 * @creation  Sept 2012
 */

public class BFilterLogMeanExt extends BPointExtension implements Runnable
{

double output;
double array_avg;
double calc;
double prev;

int    count      = 1;

double []log      = new double[getFilter()+1];

////////////////////////////////////////////////////////////////
// Runnable
////////////////////////////////////////////////////////////////
  public void run() { System.out.println("Source BProgram did not override run(). Exiting thread."); }

////////////////////////////////////////////////////////////////
// Property "Filter"
////////////////////////////////////////////////////////////////
//  public static final Property filter = newProperty(Flags.SUMMARY, new BStatusNumeric(95, BStatus.nullStatus),BFacets.make(BFacets.MIN, BInteger.make(0),BFacets.MAX, BInteger.make(100)));
//  public static final Property filter = newProperty(Flags.SUMMARY, new BStatusNumeric(3),BFacets.make(BFacets.MIN, BInteger.make(0),BFacets.MAX, BInteger.make(100)));
//  public BStatusNumeric getFilter() { return (BStatusNumeric)get(filter); }
//  public void setFilter(BStatusNumeric v) { set(filter,v,null); }

////////////////////////////////////////////////////////////////
// Property "filteredOut"
////////////////////////////////////////////////////////////////
  public static final Property filteredOut = newProperty(0|Flags.SUMMARY, new BStatusNumeric(), null);
  public BStatusNumeric getFilteredOut() { return (BStatusNumeric)get(filteredOut); }
  public void setFilteredOut(BStatusNumeric v) { set(filteredOut, v, null); }

  public static final Property filter = newProperty(0, 8,BFacets.makeInt(1, 64) );
  public int getFilter() { return getInt(filter); }
  public void setFilter(int v) { setInt(filter,v,null); }



////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BFilterLogMeanExt.class);


////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////                 
  public BFilterLogMeanExt()
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
// BComponent Overrides
////////////////////////////////////////////////////////////////

  public void started() throws Exception { try { onStart(); } catch(Throwable t) { throw new Exception(t); } }

  public void stopped() throws Exception { try { onStop(); } catch(Throwable t) { throw new Exception(t); } }

  public void changed(Property p, Context cx)
  {        
    if (p == filter) 
      {
        double[] temp = new double[getFilter() + 1];
        
        if (log != null)
            {
            System.arraycopy(log , 0, temp , 0, Math.min(log.length, temp.length) );
            log = temp;
            //System.out.println("Array CHANGED - length =  " + log.length );
            }
      }
   
      super.changed(p, cx);
      if(!isRunning())  return;
   
      BControlPoint parent = getParentPoint();
      if(parent != null) getParentPoint().execute();
  } 
  public void onStart() throws Exception
  {
    log = new double[getFilter()+1];
  } 
  public void onStop() throws Exception
  { 
  }   
  // Called when either me or my parent control point is updated.
  public void onExecute(BStatusValue o, Context cx)
  {
     double total  = 0d;
     BStatusNumeric out = (BStatusNumeric)o;      //parent point
             if (count <= getFilter() - 1 )
     {count++ ;}                                  //count values as the come in
     else
     {count = getFilter() ;}
          
     for(int i= count; i > 0 ; i--) 
     {
       log[i] = log[i-1] ;                       // shift the items in the array
       total  = total + log[count-i]; 
     }     
             
     
     log[1] = Math.log( out.getValue());         //current input value from parent
     
     total = total + log[1];                     //add the value we just got to the total     
     
     array_avg = (total/count);
     output    = Math.exp(array_avg);
     
     o.setValueValue(BDouble.make( output ));           //write to parent.valuevalue  but write the currently calculated value       
  
  //  System.out.println("TOTAL     : " + total );       
  //  System.out.println("count     : " + count );
  //  System.out.println("array_avg : " + array_avg );
  //  System.out.println("calc      : " + calc );
  //  System.out.println("prev      : " + prev ); 
  //  System.out.println("output    : " + Math.exp(total/(count)));
  }                                                
 
  //icon for this component
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png"); 

                                  
  
}  


