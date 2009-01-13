
package org.axcommunity.niagara.logic;

import javax.baja.sys.*;
import javax.baja.status.*;

/**
 * This is a modification to the existing latch object.  Currently
 * you can only store one value for latching.  This object can store
 * the previous X values.  A good example is monthly metering that is
 * triggered at the end of the month.
 *@author Kevin Crabill, Cochrane Supply
 */
public class BNumericLatchCustom
  extends BComponent
{  

 
////////////////////////////////////////////////////////////////
// Property "facets"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>facets</code> property.
   * These facets are applied against all out properties.
   */
  public static final Property facets = newProperty(0, BFacets.DEFAULT,null);
  
  /**
   * Get the <code>facets</code> property.
   */
  public BFacets getFacets() { return (BFacets)get(facets); }
  
  /**
   * Set the <code>facets</code> property.
   */
  public void setFacets(BFacets v) { set(facets,v,null); }

////////////////////////////////////////////////////////////////
// Property "clock"
////////////////////////////////////////////////////////////////
  
  /**
   * doesn't do anything
    */
  public static final Property clock = newProperty(Flags.OPERATOR|Flags.TRANSIENT|Flags.SUMMARY, new BStatusBoolean(),null);
  
  /**
   * Get the <code>clock</code> property.
   */
  public BStatusBoolean getClock() { return (BStatusBoolean)get(clock); }
  
  /**
   * Set the <code>clock</code> property.
   */
  public void setClock(BStatusBoolean v) { set(clock,v,null); }

////////////////////////////////////////////////////////////////
// Property "inCount"
////////////////////////////////////////////////////////////////
  
  /**
   * determines number of outputs, creates two new slots, one is
   * for latch value and the other is for the time the latch took
   * place
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
// Property "in"
////////////////////////////////////////////////////////////////
  
  /**
   * this value will be used when the latch action takes place.
   * It gets pushed down to the first output slot and timestamped.
   * The other output slots are pushed down.
   */
  public static final Property in = newProperty(Flags.SUMMARY, new BStatusNumeric(),null);
  
  /**
   * Get the <code>in</code> property.
   */
  public BStatusNumeric getIn() { return (BStatusNumeric)get(in); }
  
  /**
   * Set the <code>in</code> property.
   */
  public void setIn(BStatusNumeric v) { set(in,v,null); }

////////////////////////////////////////////////////////////////
// Action "latch"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>latch</code> action.
   */
  public static final Action latch = newAction(0,null);
  
  /**
   * Invoke the <code>latch</code> action.
   */
  public void latch() { invoke(latch,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNumericLatchCustom.class);


////////////////////////////////////////////////////////////////
// Interfaces
////////////////////////////////////////////////////////////////

  /**
   * Get the control output value.
   */
  public final void setOutStatusValue(BStatusValue value) 
  { 
    //setOut((BStatusNumeric)value);
    if( getInCount().getValue() == 0 )
    {
       return;
    }
    
    for( int loop = (int) getInCount().getValue() - 1; loop > 0; loop-- )
    {  
       int preSpot = loop - 1;                                                 
       
       BStatusNumeric preValue = (BStatusNumeric) get( inPrefix + preSpot );
       set( inPrefix + loop, (BStatusNumeric) preValue.newCopy() );
       
       BAbsTime preTime = (BAbsTime) get( "time" + preSpot );
       set( "time" + loop, (BAbsTime) preTime.newCopy() );
    }
    
    set( inPrefix + "0", value );
    set( "time0", BAbsTime.now() );
  }

  public final BStatusValue getInStatusValue() 
  { 
    return (BStatusValue)(getIn().newCopy());
  }
  
  private static final String inPrefix = "out";
  
  public void changed(Property property, Context context) 
  {
    if(isRunning())
    {
      if(property == clock)
      {
        currentClock = getClock().getValue();
        if(getClock().getStatus().isValid())
        {
          if(currentClock && !lastClock)
          {
            //setOutStatusValue(getInStatusValue());
          }
          lastClock = currentClock;
        }
      }
      else if ( property == inCount )
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
                 remove( "time" + i );
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
                    add( "time" + i, BAbsTime.now() );
                    setFlags( getSlot( "time" + i ), 8 );
                 }
              }
           }
        }
        catch( DuplicateSlotException e )
        {
           System.out.println( "Messed Up" );
        }
      }
    }
  }

  public void doLatch()
  {
    setOutStatusValue(getInStatusValue());
  }

  public BFacets getSlotFacets(Slot slot)
  {
    if (slot.getName().startsWith("out")) return getFacets();
    return super.getSlotFacets(slot);
  }

////////////////////////////////////////////////////////////////
// Presentation
////////////////////////////////////////////////////////////////  

  private boolean currentClock;
  private boolean lastClock;



  /**
   * Get the icon.
   */
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/cochraneicon.png");


}

