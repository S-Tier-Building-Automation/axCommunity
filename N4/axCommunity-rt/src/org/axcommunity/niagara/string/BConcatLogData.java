package org.axcommunity.niagara.string;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusString;
import javax.baja.status.BStatusValue;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BObject;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;


/**
 * 
 * 3/18/2013 - Added suffix and enable slots.
 *
 * @author ebishop
 * @creation Mar 18, 2013
 *
 *	Update 6/29/2017 by James Johnson to move to current logger syntax
 */
public class BConcatLogData extends BComponent
{
  public static final Property facets = newProperty(0, BFacets.DEFAULT);
  public BFacets getFacets() { return (BFacets)get(facets); }
  public void setFacets(BFacets v) { set(facets,v,null); }
  
  public static final Property outString = newProperty(Flags.SUMMARY, new BStatusString(),BFacets.make(BFacets.MULTI_LINE, true));
  public BStatusString getOutString() { return (BStatusString)get(outString); }
  public void setOutString(BStatusString v) { set(outString, v); }

  public static final Property numberOfInputs = newProperty(0, 0, BFacets.make(BFacets.MIN, BInteger.make(0), BFacets.MAX, BInteger.make(60)));
  public int getNumberOfInputs() { return getInt(numberOfInputs); }
  public void setNumberOfInputs(int v) {setInt(numberOfInputs,v);}
  
  public static final Property concatPrefixIfValueIsNullOrBlank = newProperty(Flags.SUMMARY, false);
  public boolean getConcatPrefixIfValueIsNullOrBlank() { return getBoolean(concatPrefixIfValueIsNullOrBlank); }
  public void setConcatPrefixIfValueIsNullOrBlank(boolean v) { setBoolean(concatPrefixIfValueIsNullOrBlank, v); }
  
  public static final Property lineUpResults = newProperty(Flags.SUMMARY, false);
  public boolean getLineUpResults() { return getBoolean(lineUpResults); }
  public void setLineUpResults(boolean v) { setBoolean(lineUpResults, v); }
  
  public static final Property inOutputPrefix = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.nullStatus),BFacets.make(BFacets.MULTI_LINE, true));
  public BStatusString getInOutputPrefix() { return (BStatusString)get(inOutputPrefix); }
  public void setInOutputPrefix(BStatusString v) { set(inOutputPrefix, v); }
  
  public static final Property inDelimiter = newProperty(Flags.SUMMARY, new BStatusString("; "),BFacets.make(BFacets.MULTI_LINE, true));
  public BStatusString getInDelimiter() { return (BStatusString)get(inDelimiter);}
  public void setInDelimiter(BStatusString v) { set(inDelimiter, v);}
  
  public static final Property inOutputSuffix = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.nullStatus),BFacets.make(BFacets.MULTI_LINE, true));
  public BStatusString getInOutputSuffix() { return (BStatusString)get(inOutputSuffix); }
  public void setInOutputSuffix(BStatusString v) { set(inOutputSuffix, v); }
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BConcatLogData.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
  
  public void changed(Property p, Context cx)
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    
    if(p.equals(numberOfInputs)) recreateInputs(getNumberOfInputs());
    else
    {
      int longestPrefix = 0;
      String currentPrefix = "";
      String currentSuffix = "";
      String currentValue = "";
      String currentPrefixSpacing = "";
      String finalString = "";
      if(!getInOutputPrefix().getStatus().isNull() & getInOutputPrefix().getValue().length() > 0) finalString = getInOutputPrefix().getValue() + getInDelimiter().getValue();
      
      //if the "line up results" input is true, find the longest prefix
      if(getLineUpResults())
      {
        for(int i=1; i<getNumberOfInputs()+1; i++)
        {
          try
          {
            if(((BObject)get("in"+i+"_Enable"))==null)  {this.add(("in"+i+"_Enable"), new BStatusBoolean(true, BStatus.nullStatus), Flags.SUMMARY);}
            if(((BObject)get("in"+i+"_Prefix"))==null)  {this.add(("in"+i+"_Prefix"), new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
            if(((BObject)get("in"+i))          ==null)  {this.add(("in"+i),           new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
            if(((BObject)get("in"+i+"_Suffix"))==null)  {this.add(("in"+i+"_Suffix"), new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}

            if(((BStatusString) ((BObject)get("in"+i+"_Prefix"))).getStatus().isValid() & ((BStatusString) ((BObject)get("in"+i+"_Prefix"))).getValue().length() > 0)
              if(getConcatPrefixIfValueIsNullOrBlank() || !((BStatusString) ((BObject)get("in"+i))).getStatus().isNull() & ((BStatusString) ((BObject)get("in"+i))).getValue().length() > 0)
                if(((BStatusBoolean) ((BObject)get("in"+i+"_Enable"))).getValue() & ((BStatusBoolean) ((BObject)get("in"+i+"_Enable"))).getStatus().isValid())
                  if(longestPrefix < ((BStatusString) ((BObject)get("in"+i+"_Prefix"))).getValue().length())
                    longestPrefix = ((BStatusString) ((BObject)get("in"+i+"_Prefix"))).getValue().length();
          }
          catch (Exception e)
          {
            logger.log(Level.SEVERE, getSlotPath().toString() + e.getMessage());
            e.printStackTrace();
          }
        }
      }
      
      //Retrieve all prefixes and inputs
      for(int i=1; i<getNumberOfInputs()+1; i++)
      {
        currentPrefix = "";
        currentSuffix = "";
        currentValue = "";
        currentPrefixSpacing = "";
        
        //Find the prefix at the current index
        try
        {
          if(((BObject)get("in"+i+"_Enable"))==null)  {this.add(("in"+i+"_Enable"), new BStatusBoolean(true), Flags.SUMMARY);}
          if(((BObject)get("in"+i+"_Prefix"))==null)  {this.add(("in"+i+"_Prefix"), new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
          if(((BObject)get("in"+i))          ==null)  {this.add(("in"+i),           new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
          if(((BObject)get("in"+i+"_Suffix"))==null)  {this.add(("in"+i+"_Suffix"), new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
          
          if(((BStatusString) ((BObject)get("in"+i+"_Prefix"))).getStatus().isValid() & ((BStatusString) ((BObject)get("in"+i+"_Prefix"))).getValue().length() > 0)
            if(getConcatPrefixIfValueIsNullOrBlank() || !((BStatusString) ((BObject)get("in"+i))).getStatus().isNull() & ((BStatusString) ((BObject)get("in"+i))).getValue().length() > 0)
            {
              currentPrefix = ((BStatusString) ((BObject)get("in"+i+"_Prefix"))).getValue();
            }
          if(((BStatusString) ((BObject)get("in"+i+"_Suffix"))).getStatus().isValid() & ((BStatusString) ((BObject)get("in"+i+"_Suffix"))).getValue().length() > 0)
            if(getConcatPrefixIfValueIsNullOrBlank() || !((BStatusString) ((BObject)get("in"+i))).getStatus().isNull() & ((BStatusString) ((BObject)get("in"+i))).getValue().length() > 0)
            {
              currentSuffix = ((BStatusString) ((BObject)get("in"+i+"_Suffix"))).getValue();
            }
        }
        catch (Exception e)
        {
          logger.log(Level.SEVERE, getSlotPath().toString() + e.getMessage());
          e.printStackTrace();
        }
        
        //if lineUpResults is true, fix the spacing between the prefix and the value
        if(getLineUpResults()) while(currentPrefix.length() + currentPrefixSpacing.length() < longestPrefix) currentPrefixSpacing = currentPrefixSpacing + " ";
        
        //Find the value at the current index
        try
        {
          if(!((BStatusString) (BObject)get("in"+i)).getStatus().isNull() & ((BStatusString) ((BObject)get("in"+i))).getValue().length() > 0)
          {
            currentValue = ((BStatusString) ((BObject)get("in"+i))).getValue();
          }
        }
        catch (Exception e)
        {
          logger.log(Level.SEVERE, getSlotPath().toString() + e.getMessage());
          e.printStackTrace();
        }
        
        //if the length of the prefix + value > 0, concat the whole string to the end result
        if(currentPrefix.length() + currentValue.length() > 0 && ((BStatusBoolean) ((BObject)get("in"+i+"_Enable"))).getValue() && ((BStatusBoolean) ((BObject)get("in"+i+"_Enable"))).getStatus().isValid())
        {
          finalString += currentPrefix + currentPrefixSpacing + currentValue + currentSuffix + getInDelimiter().getValue();
        }
      }
      
      //if the end result length is greater than 0, set the output, otherwise set the output to null
      if(finalString.length() > 0)
      {
        //trim the last delimiter off and add suffix
        finalString = finalString.substring(0, finalString.length() - getInDelimiter().getValue().length()) + getInOutputSuffix().getValue();
        
        getOutString().setStatus(0);
        getOutString().setValue(finalString);
      }
      else
      {
        getOutString().setStatus(BStatus.nullStatus);
        getOutString().setValue("");
      }
    }
  }
  
  private void recreateInputs(int SlotCount)
  {
    try
    {
      for(int i=1;i<(SlotCount+1);i++)
      {
        if(((BObject)get("in"+i+"_Enable"))==null)  {this.add(("in"+i+"_Enable"), new BStatusBoolean(true), Flags.SUMMARY);}
        if(((BObject)get("in"+i+"_Prefix"))==null)  {this.add(("in"+i+"_Prefix"), new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
        if(((BObject)get("in"+i))          ==null)  {this.add(("in"+i),           new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
        if(((BObject)get("in"+i+"_Suffix"))==null)  {this.add(("in"+i+"_Suffix"), new BStatusString("", BStatus.nullStatus), Flags.SUMMARY);}
      }
      
      for(int i=SlotCount+1;
          (BObject)get("in"+i+"_Enable")!=null | 
          (BObject)get("in"+i+"_Prefix")!=null | 
          (BObject)get("in"+i)!=null | 
          (BObject)get("in"+i+"_Suffix")!=null;
          i++)
      {
        if(((BObject)get("in"+i+"_Prefix"))!=null) {this.remove("in"+i+"_Enable");}             
        if(((BObject)get("in"+i+"_Prefix"))!=null) {this.remove("in"+i+"_Prefix");}             
        if(((BObject)get("in"+i))!=null) {this.remove("in"+i);}             
        if(((BObject)get("in"+i+"_Suffix"))!=null) {this.remove("in"+i+"_Suffix");}             
      }
    }
    catch (Exception e)
    {
      logger.log(Level.SEVERE, getSlotPath().toString() + e.getMessage());
      e.printStackTrace();
    }
  }
  public static final Logger logger = Logger.getLogger(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
  public BFacets getSlotFacets(Slot slot) { return super.getSlotFacets(slot); }
  public BStatus getStatus() { return getOutString().getStatus(); }
  public BStatusValue getStatusValue() { return getOutString().getStatusValue(); }
  public BFacets getStatusValueFacets() { return getFacets(); }
  public BValue getActionParameterDefault(Action action) { return super.getActionParameterDefault(action); }
}
