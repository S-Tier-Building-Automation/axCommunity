package org.axcommunity.niagara.logic;
/**
 * The idea is to be able to dynamically change the input link of a StatusNumeric based on a 
 * string that is the Ord of the value you want to link from.  The source ord must have an "out" slot
 * that will be used as the source slot.  If the source ord is not valid, it will delete the link and the object
 * out value will go to 0.
 * 
 * Proper formatting for the ordIn is "station:|slot:/ind/Ouput01"
 * 
 * @author Mike Arnott, Kors Engineering
 */
import javax.baja.naming.BOrd;
import javax.baja.status.*;
import javax.baja.sys.*;

public class BDynamicLinkNumeric
    extends BComponent
{
  public void changed(Property property, Context context){
    super.changed(property, context);
    if(!Sys.atSteadyState()|| !isRunning()){
    return;
  }
    if(property==in)
    {
      getOut().setValue(getIn().getValue());
    }
    if(property == ordIn){
      //see if the object already has a link
      BLink[] links = this.getLinks(this.getSlot("in"));
      if(links.length>0)
      {
        //will only alter link 0, not meant as a many to 1!!!
        //try to make ord from input link string
        BOrd ord = BOrd.make(getOrdIn().getValue());
        if(isOrdValid(ord))
        {
          links[0].setSourceOrd(ord);
        }
        else
        {
          //invalid ord, remove link 0
          this.remove(links[0]);
        }
      }
      else
      {
        //no link, create one if possible
        BOrd ord = BOrd.make(getOrdIn().getValue());
        if(isOrdValid(ord))
        {
          BLink link = new BLink(ord,"out","in",true);
          this.add(null, link);  
        }
        else
        {
          //invalid ord with no links, do nothing         
        }
      }
    }
  }
  
  private boolean isOrdValid(BOrd ord)
  {
    try
    {
      //try to create the component - if it fails, false
      BComponent com = (BComponent)ord.relativizeToHost().get();
      return true;
    }
    catch(Exception e)
    {
      return false;
    }
    
  }
  
  /**Numeric Output*/
  public static final Property out = newProperty(Flags.SUMMARY + Flags.READONLY, new BStatusNumeric());
  public BStatusNumeric getOut() { return (BStatusNumeric)get(out);}
  public void setOut(BStatusNumeric v) {set(out,v);}
  
  /**Numeric Input for link*/
  public static final Property in = newProperty(Flags.SUMMARY, new BStatusNumeric());
  public BStatusNumeric getIn() { return (BStatusNumeric)get(in);}
  public void setIn(BStatusNumeric v) {set(in,v);}
  
  /**Link Ord String Input*/
  public static final Property ordIn = newProperty(Flags.SUMMARY, new BStatusString());
  public BStatusString getOrdIn() { return (BStatusString)get(ordIn);}
  public void setOrdIn(BStatusString v) {set(ordIn,v);}
  
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
    
  public static final Type TYPE = Sys.loadType(BDynamicLinkNumeric.class);
  public Type getType() { return TYPE; }

}

