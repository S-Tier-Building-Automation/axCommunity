package org.axcommunity.niagara.math;

import javax.baja.sys.*;
import javax.baja.status.*;

/**
 * 
 *Determines the minimum, maximum and average based on 
 *variable amount of inputs. It can be greater than the 10 that
 *the original MinMaxAvg object provides.
 *@author , Cochrane Supply
 */
public class BAvgMinMax extends BComponent
{ 

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
// Property "sum"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>sum</code> property.
   */
  public static final Property sum = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);

  /**
   * Get the <code>sum</code> property.
   */
  public BStatusNumeric getSum() { return (BStatusNumeric)get(sum); }
  
  /**
   * Set the <code>sum</code> property.
   */
  public void setSum(BStatusNumeric v) { set(sum,v,null); }
  

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BAvgMinMax.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/cochraneicon.png");

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
	 double sum = (double)0.0;
     double avgCount = 0;
     for(int i = 0; i < getInCount().getValue(); i++)
     {
       BStatusNumeric value = (BStatusNumeric) get( inPrefix + i );
       if( value.getStatus().isValid() )
       {
         if(value.getValue() < min) min = value.getValue();
         if(value.getValue() > max) max = value.getValue();
         avg = avg + value.getValue();
         avgCount++;
       }
     }
     if(min == Double.POSITIVE_INFINITY) min = Double.NaN;
     if(max == Double.NEGATIVE_INFINITY) max = Double.NaN;
	 sum = avg;
     avg = avg / avgCount;
     getMin().setValue(min);
     getMax().setValue(max);
     getAvg().setValue(avg);
	 getSum().setValue(sum);
  } 
}

