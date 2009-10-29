package org.axcommunity.niagara.math;


import javax.baja.sys.*;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;


/**
 * 
 *Determines the minimum, maximum and average based on 
 *variable amount of inputs. It can be greater than the 10 that
 *the original MinMaxAvg object provides.
 *@author , Cochrane Supply
 *
 * Edited by Tyler Long, McKenney's Inc.:
 * Added two inputs to exclude values above and below 
 * set points inMaximum and inMinimum. Enabled actions so 
 * that the inputs (ie. InMinimum, InMaximum, & inCount) may 
 * be configured outside of the property sheet.
 *
 */
public class BAvgMinMax_v1 extends BComponent
{ 
  
 
////////////////////////////////////////////////////////////////
//Property "inMinimum"
////////////////////////////////////////////////////////////////
 
 /**
  * Values below and including the minimum are excluded from the calculation
   */
  public static final Property inMinimum = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
  * Get the <code>inMinimum</code> property.
  */
 public BStatusNumeric getInMinimum() { return (BStatusNumeric)get(inMinimum); }
  
 /**
  * Set the <code>inMinimum</code> property.
  */
 public void setInMinimum(BStatusNumeric v) { set(inMinimum,v,null); } 
  
  
////////////////////////////////////////////////////////////////
//Property "inMaximum"
////////////////////////////////////////////////////////////////
 
 /**
  * Values above and including the maximum are excluded from the calculation
   */
 public static final Property inMaximum = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);
 
 /**
  * Get the <code>inMaximum</code> property.
  */
 public BStatusNumeric getInMaximum() { return (BStatusNumeric)get(inMaximum); }
 
 /**
  * Set the <code>inMaximum</code> property.
  */
 public void setInMaximum(BStatusNumeric v) { set(inMaximum,v,null); }  

////////////////////////////////////////////////////////////////
// Property "inCount"
////////////////////////////////////////////////////////////////
  
  /**
   * number of numeric inputs, resources determine the limits
    */
  public static final Property inCount = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>inCount</code> property.
   */
  public BStatusNumeric getInCount() { return (BStatusNumeric)get(inCount); }
  
  /**
   * Set the <code>inCount</code> property.
   */
  public void setInCount(BStatusNumeric v) { set(inCount,v,null); }

////////////////////////////////////////////////////////////////
// Property "min"
////////////////////////////////////////////////////////////////
  
  /**
   * minimum value of all numeric inputs
   */
  public static final Property min = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>min</code> property.
   */
  public BStatusNumeric getMin() { return (BStatusNumeric)get(min); }
  
  /**
   * Set the <code>min</code> property.
   */
  public void setMin(BStatusNumeric v) { set(min,v,null); }

////////////////////////////////////////////////////////////////
// Property "max"
////////////////////////////////////////////////////////////////
  
  /**
   * maximum value of all numeric inputs.
   */
  public static final Property max = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>max</code> property.
   */
  public BStatusNumeric getMax() { return (BStatusNumeric)get(max); }
  
  /**
   * Set the <code>max</code> property.
    */
  public void setMax(BStatusNumeric v) { set(max,v,null); }

////////////////////////////////////////////////////////////////
// Property "avg"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>avg</code> property.
   */
  public static final Property avg = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);

  /**
   * Get the <code>avg</code> property.
   */
  public BStatusNumeric getAvg() { return (BStatusNumeric)get(avg); }
  
  /**
   * Set the <code>avg</code> property.
   */
  public void setAvg(BStatusNumeric v) { set(avg,v,null); }
  
////////////////////////////////////////////////////////////////
//Action "SetMinimumInput"
////////////////////////////////////////////////////////////////
   
  /**
   * Sets the type of the returned variable
   */
  public static BDouble _MinSP = BDouble.make(20);
  
  /**
   * Creates the Action to set <code>inMinimum</code> Property.
   */
  public static final Action SetMinimumInput = newAction(0,_MinSP);
  
 /**
    * Invoke the <code>SetMinimumInput</code> action.
    */
 
 public BDouble SetMinimumInput(BDouble _MinSP) 
 {
   return (BDouble)invoke(SetMinimumInput,_MinSP,null); 
 }

 
////////////////////////////////////////////////////////////////
//Action "SetMaximumInput"
////////////////////////////////////////////////////////////////
  
 /**
  * Sets the type of the returned variable
  */
 public static BDouble _MaxSP = BDouble.make(95);
 
 /**
  * Creates the Action to set <code>inMaximum</code> Property.
  */
 public static final Action SetMaximumInput = newAction(0,_MaxSP);
 
/**
   * Invoke the <code>SetMaximumInput</code> action.
   */

public BDouble SetMaximumInput(BDouble _MaxSP) 
{
  return (BDouble)invoke(SetMaximumInput,_MaxSP,null); 
} 

////////////////////////////////////////////////////////////////
//Action "SetInCount"
////////////////////////////////////////////////////////////////

/**
* Sets the type of the returned variable
*/
public static BInteger _InCountSP = BInteger.make(0);

/**
* Creates the Action to set <code>inCount</code> Property.
*/
public static final Action SetInCount = newAction(Flags.CONFIRM_REQUIRED,_InCountSP);

/**
 * Invoke the <code>SetInCount</code> action.
 */
public BInteger SetInCount(BInteger _InCountSP) 
{
  //_InCountSP = getInCount().getValue();
  
  return (BInteger)invoke(SetInCount,_InCountSP,null); 
}

////////////////////////////////////////////////////////////////
//Gets Action parameter defaults
////////////////////////////////////////////////////////////////
public static BInteger _InCount = BInteger.make(0);

public BValue getActionParameterDefault(Action paramAction)
{
  
  /*
   * Format: insert values where "{}" is shown
   * if (paramAction == {Action}) return {value to return};
   */
  if (paramAction == SetMinimumInput) return getInMinimum().getValueValue();
  if (paramAction == SetMaximumInput) return getInMaximum().getValueValue();
  
  if (paramAction == SetInCount)
  {
    Double inValue;
    int outValue = 0;

    inValue = new Double(getInCount().getValue());
    outValue = inValue.intValue();
    BInteger b = BInteger.make(outValue);

    return b;
  }
  
  //if (paramAction == SetInCount) return getInteger(BInteger);
  //if (paramAction == override) return new BNumericOverride(getOut().getValue());
  //if (paramAction == emergencyOverride) return getOut().getValueValue();
  return super.getActionParameterDefault(paramAction);
}

////////////////////////////////////////////////////////////////
// Type  
////////////////////////////////////////////////////////////////

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BAvgMinMax_v1.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/MK_Logo_16-16_transparent.png");

  private static final String inPrefix = "in";
  
  public void changed( Property prop, Context cx )
  {
    if ( !isRunning() ) return;

    if ( prop == inCount )
    {
      Property[] properties = getDynamicPropertiesArray();
      int currentInCount = 0;
                        
      for( int i = 0; i < properties.length; i++ )
      {
         if( properties[ i ].getName().startsWith( inPrefix ) )
            currentInCount++;
      }
      
      int newInCount = ( int ) getInCount().getValue();
      
      try 
      {
         if( currentInCount > newInCount )
         {                                
            for( int i = currentInCount - 1; i >= newInCount; i-- )
            {                                                     
               remove( inPrefix + i );
            }
         } 
         else if( currentInCount < newInCount )
         {                                   
            for( int i = currentInCount; i < newInCount; i++ )
            {                                               
               if( getProperty( inPrefix + i ) == null )
               {                                      
                  add( inPrefix + i, new BStatusNumeric() );
                  setFlags( getSlot( inPrefix + i ), 8 );
                  
               }
            }
         }
         
         createOutput();
      }
      catch( DuplicateSlotException e )
      {
         System.out.println( "Messed Up" );
      }
    }
    else
    {
      super.changed( prop, cx );
      createOutput();
    }
  }
  
  public void createOutput()
  {
     double min = Double.POSITIVE_INFINITY;
     double max = Double.NEGATIVE_INFINITY;
     double avg = (double)0.0;
     double avgCount = 0;
     double inMin = getInMinimum().getValue();
     double inMax = getInMaximum().getValue();
     
     for(int i = 0; i < getInCount().getValue(); i++)
     {
       BStatusNumeric value = (BStatusNumeric) get( inPrefix + i );
       if( value.getStatus().isValid() && value.getValue() > inMin && value.getValue() < inMax)
       {
         if(value.getValue() < min) min = value.getValue();
         if(value.getValue() > max) max = value.getValue();
         avg = avg + value.getValue();
         avgCount++;
       }
     }
     if(min == Double.POSITIVE_INFINITY) min = Double.NaN;
     if(max == Double.NEGATIVE_INFINITY) max = Double.NaN;
     avg = avg / avgCount;
     getMin().setValue(min);
     getMax().setValue(max);
     getAvg().setValue(avg);
  } 
  
 
  public BDouble doSetMinimumInput(BDouble v)  
  {
    //Sets _MinSP = v (double arg0)
    _MinSP = v;
    
    //Sets inMinimum = _MinSP
    getInMinimum().setValue(_MinSP.getDouble());
    
    return _MinSP;
  }

  public BDouble doSetMaximumInput(BDouble v)  
  {
    //Sets _MaxSP = v (double arg0)
    _MaxSP = v;
    
    //Sets inMaximum = _MaxSP
    getInMaximum().setValue(_MaxSP.getDouble());
    
    return _MaxSP;
  }  
  
  public BInteger doSetInCount(BInteger v)  
  {
    //Sets _InCountSP = v (double arg0)
    _InCountSP = v;
    
    //Sets inCount = _InCountSP
    getInCount().setValue(_InCountSP.getDouble());
    
    return _InCountSP;
  } 

}

