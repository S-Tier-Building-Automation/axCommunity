package org.axcommunity.niagara.logic;


import javax.baja.log.Log;
import javax.baja.naming.BOrd;
import javax.baja.naming.InvalidOrdBaseException;
import javax.baja.naming.SlotPath;
import javax.baja.status.BStatusString;
import javax.baja.status.BStatusValue;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BLink;
import javax.baja.sys.BObject;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BValue;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Knob;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BCompositeAction;
import javax.baja.util.BCompositeTopic;
import javax.baja.util.BFormat;

/**
 * The primary function of this object is to be able to link from several objects anywhere on the station using a list of ords and/or
 * format ords, and slots.  In the CSV list, the ord or format ord is the first value, then the source slot, then the target slot.  The
 * target slot is automatically created using the name you specify, and the type is automatically determined by the source slot type.<br>
 * <br>
 * The secondary function of this object is to resolve formats to strings.  This is done by leaving the source slot empty in the CSV 
 * string.  These slots will not have a link shown on the object.<br>
 * <br>
 * <br>
 * Provide a CSV string structured as such:<br>
 * [format ord or string],[source slot name (leave blank if the ord is a string)],[Target slot name]<i>\n</i><br>
 * [format ord or string],[source slot name (leave blank if the ord is a string)],[Target slot name]<i>\n</i><br>
 * etc...<br>
 * <br>
 * <br>
 * If the format ord is a string output (such as %parent.name%, which would return the parent folder's name), 
 * leave the source slot name blank.  The target slot name, however, must always be provided.<br>
 * <br>
 * If the intention is to link the slot to another object, the proper formatting for the ord 
 * (or the value returned by the format ord) should be "station:|slot:/dir/object"<br>
 * <br>
 * <br>
 * Example:<br>
 * station:|%parent.sampleObject.slotPath%,out,someSlotFromAnObjectInMyParentFolder<br>
 * %parent.parent.name.substring(8,11)%,,PartOfANameInMyPath<br>
 * <br>
 * <br>
 * Eric's example from a production server:<br>
 * %parent.parent.parent.name%,,WorkcenterName<br>
 * station:|slot:/Global/ProprietaryData/CSVData,out,ProprietaryDataCsv<br>
 * station:|%parent.parent.slotPath%/Machine_Status/Status_Manual,out,Manual<br>
 * <br>
 * <br>
 * Notes:<br>
 *    - If the only the ord changes (source and target slot names remain the same), the link on the existing slot will be updated to the new ord<br>
 *    - If the only the target slot name changes (source ord and slot name remain the same), the target slot name will be renamed (not removed) and any links in or out of that slot will be updated to the new name.<br>
 *    - If the source slot name changes and the new source slot is the same type as the old source slot, the link is updated to the new source slot.<br>
 *    - If the source slot name changes and the new source slot is a different type, the old target slot is removed and a new one is created using the new type.<br>
 * <br>
 * @author Eric Bishop
 * @creation Aug 1, 2016
 */
public class BDynamicLinks extends BComponent
{
  static int colSourceOrd = 0;
  static int colSourceSlotName = 1;
  static int colTargetSlotName = 2;
  String [][] arrSlotInfo = new String[0][0];
  Clock.Ticket midnightTimer;
  Clock.Ticket refreshTimer;
  
  /**See the class description for more information*/
  public static final Property slotInfoCsv = newProperty(0, "%parent.name%,,MyParentName\nstation:|%slotPath%,,SampleSlotPath", BFacets.make(BFacets.MULTI_LINE, true));
  /**See the class description for more information*/
  public String getSlotInfoCsv() { return getString(slotInfoCsv);}
  /**See the class description for more information*/
  public void setSlotInfoCsv(String v) {setString(slotInfoCsv,v);}

  /**Set to zero seconds to disable*/
  public static final Property refreshInterval = newProperty(0, BRelTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.FALSE, BFacets.MIN, BRelTime.make(0)));
  /**Set to zero seconds to disable*/
  public BRelTime getRefreshInterval() { return (BRelTime)get(refreshInterval);}
  /**Set to zero seconds to disable*/
  public void setRefreshInterval(BRelTime v) {set(refreshInterval,v);}
  
  public static final Property refreshLinksAtMidnight = newProperty(0, true);
  public boolean getRefreshLinksAtMidnight() { return getBoolean(refreshLinksAtMidnight);}
  public void setRefreshLinksAtMidnight(boolean v) {setBoolean(refreshLinksAtMidnight,v);}
  
  /**This will refresh any links and any string values*/
  public static final Action refreshLinks = newAction(Flags.OPERATOR,null);
  /**This will refresh any links and any string values*/
  public void refreshLinks() {invoke(refreshLinks,null,null);}
  
  public static final Action midnightTimerExpired = newAction(Flags.HIDDEN,null);
  public void midnightTimerExpired() {invoke(midnightTimerExpired,null,null);}
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BDynamicLinks.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://ROI/com/royaloakindustries/niagara/Graphics/EB.png");
  public static final Log logger = Log.getLog(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
  
  
  public void started() throws Exception
  {
    if(!Sys.atSteadyState()) return;
    //At this point, we know the object was just created (or copied).
    startupRoutine();
  }

  public void atSteadyState() throws Exception
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    startupRoutine();
  }

  public void stopped()
  {
    if (refreshTimer != null) refreshTimer.cancel();
    if(midnightTimer != null) midnightTimer.cancel();
  }
  
  public void changed(Property p, Context cx)
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    if(p.equals(slotInfoCsv)) doRefreshLinks();
    if(p.equals(refreshInterval)) updateTimer();
    if(p.equals(refreshLinksAtMidnight)) scheduleMidnightTimer();
  }
  
  void startupRoutine()
  {
    scheduleMidnightTimer();
    updateTimer();
    doRefreshLinks();
  }
  
  void updateTimer()
  {
    if (refreshTimer != null) refreshTimer.cancel();
    if(getRefreshInterval().getSeconds() > 0) refreshTimer = Clock.schedulePeriodically(this, getRefreshInterval(), refreshLinks, null);
  }
  
  private void scheduleMidnightTimer()
  {
    if(midnightTimer != null) midnightTimer.cancel();
    if(getRefreshLinksAtMidnight())
    {
      //Create a random number that is between 0-300 seconds
      int randomNumber = (int) ((Math.random()) * 300000);
      int offsetMillis;
      int offsetSeconds = 0;
      if(randomNumber >= 1000)
      {
        offsetSeconds = randomNumber / 1000;
        offsetMillis = randomNumber % 1000;
      }
      else offsetMillis = randomNumber;
      
      BAbsTime nextMidnight = BAbsTime.now().timeOfDay(0, 0, offsetSeconds, offsetMillis).nextDay();
      midnightTimer = Clock.schedule(this, nextMidnight, midnightTimerExpired, null);
    }
  }
  
  public void doMidnightTimerExpired()
  {
    doRefreshLinks();
    scheduleMidnightTimer();
  }
  
  public void doRefreshLinks()
  {
    if(!Sys.atSteadyState() || !isRunning()) return;
    String[][] strOrds;

    try {strOrds = split(getSlotInfoCsv(), "\n", ",");}
    catch (Exception e)
    {
      logger.error(getSlotPath().toString() + " - Could not parse CSV string!");
      logger.trace(getSlotPath().toString() + e.getMessage());
      if(logger.isTraceOn()) e.printStackTrace();
      return;
    }

    if(strOrds.length < 1) return;
    boolean validLinks = false;
    
    for (int i = 0; i < strOrds.length; i ++)
    {
      validLinks = false;
      String formatOrd = strOrds[i][colSourceOrd];
      String sourceSlotName = strOrds[i][colSourceSlotName];
      String targetSlotName = strOrds[i][colTargetSlotName];
      BOrd ord = null;
      BComponent com = null;
      BValue sourceBValue = null;
      Slot sourceSlot = null;
      boolean slotAdded = false;
      boolean sourceIsActionOrTopic = false;
      boolean renamedOldSlot = false;
      BLink[] links;
      
      if(formatOrd == null || targetSlotName == null) continue;
      
      targetSlotName = SlotPath.escape(targetSlotName);
      
      if(sourceSlotName.length() > 0)
      {
        try
        {
          ord = BOrd.make(BFormat.make(formatOrd).format(this));
          com = (BComponent)ord.relativizeToHost().get();
          
          sourceSlot = com.getSlot(sourceSlotName);
          
          sourceIsActionOrTopic = sourceSlot.isAction() || sourceSlot.isTopic();
          
          if(!sourceIsActionOrTopic) sourceBValue = com.get(sourceSlotName);
        }
        catch (InvalidOrdBaseException e)
        {
          try
          {
            ord = BOrd.make(BFormat.make("station:|" + formatOrd).format(this));
            com = (BComponent)ord.relativizeToHost().get();
            
            sourceSlot = com.getSlot(sourceSlotName);
            
            sourceIsActionOrTopic = sourceSlot.isAction() || sourceSlot.isTopic();
            
            if(!sourceIsActionOrTopic) sourceBValue = com.get(sourceSlotName);
          }
          catch (Exception f)
          {
            logger.error(getSlotPath().toString() + " - Could not retrieve ord/slot details!");
            logger.error("Format: " + formatOrd);
            logger.error("Ord: " + ord);
            logger.error("Source Slot Name: " + sourceSlotName);
            logger.trace(getSlotPath().toString() + f.getMessage());
            if(logger.isTraceOn()) f.printStackTrace();
            continue;
          }
        }
        catch (Exception e)
        {
          logger.error(getSlotPath().toString() + " - Could not retrieve ord/slot details!");
          logger.error("Format: " + formatOrd);
          logger.error("Ord: " + ord);
          logger.error("Source Slot Name: " + sourceSlotName);
          logger.trace(getSlotPath().toString() + e.getMessage());
          if(logger.isTraceOn()) e.printStackTrace();
          continue;
        }
      }
      else sourceBValue = new BStatusString();
      
      try
      {
        if(((BObject)get(targetSlotName))==null)
        {
          for (int j = 0; j < arrSlotInfo.length; j++)
          {
            String oldFormatOrd = arrSlotInfo[j][colSourceOrd];
            String oldTargetSlotName = arrSlotInfo[j][colTargetSlotName];
            
            if(oldTargetSlotName == null || oldFormatOrd == null) continue;
            if(oldTargetSlotName.length() == 0 || oldFormatOrd.length() == 0) continue;
            
            if(formatOrd.equals(oldFormatOrd))
            {
              oldTargetSlotName = SlotPath.escape(oldTargetSlotName);
              if(((BObject)get(oldTargetSlotName)) != null)
              {
                try
                {
                  BComponent destinationComp = this;
                  links = destinationComp.getLinks(destinationComp.getSlot(oldTargetSlotName));
                  Knob[] knobs = destinationComp.getKnobs(destinationComp.getSlot(oldTargetSlotName));
                  
                  logger.trace(getSlotPath().toString() + " - Renaming slot from " + oldTargetSlotName + " to " + targetSlotName);
                  destinationComp.rename(destinationComp.getProperty(oldTargetSlotName), targetSlotName);
                  renamedOldSlot = true;
                  
                  if(links.length>0)
                  {
                    for (int k = 0; k < links.length; k++)
                    {
                      //If the source slot is blank, this should be a BQL query and not a linked slot, so remove the link.
                      if(sourceSlotName.length() == 0) destinationComp.remove(links[k]);
                      
                      //If there is a source slot listed, then update any links to the new name.  There should only be 1
                      //link here, unless the slot is a topic or event, in which case someone might have manually linked
                      //something else to the slot.
                      else
                      {
                        if(links[k].getTargetSlotName().equalsIgnoreCase(oldTargetSlotName))
                        {
                          logger.trace(getSlotPath().toString() + " - Setting target on link#" + k + " from " + oldTargetSlotName + " to " + targetSlotName);
                          links[k].setTargetSlotName(targetSlotName);
                          validLinks = true;
                        }
                        else logger.trace(getSlotPath().toString() + " - Did not update target on link#" + k + ": " + links[k].getTargetSlotName() + " != " + oldTargetSlotName);
                      }
                    }
                  }
                  
                  
                  if(knobs.length>0)
                  {
                    for (int k = 0; k < knobs.length; k++)
                    {
                      if(knobs[k].getSourceSlotName().equalsIgnoreCase(oldTargetSlotName))
                      {
                        logger.trace(getSlotPath().toString() + " - Setting source on knob#" + k + " from " + oldTargetSlotName + " to " + targetSlotName);
                        knobs[k].getLink().setSourceSlotName(targetSlotName);
                      }
                      else logger.trace(getSlotPath().toString() + " - Did not update source on knob#" + k + ": " + knobs[k].getSourceSlotName() + " != " + oldTargetSlotName);
                    }
                  }
                }
                catch (Exception e)
                {
                  logger.error(getSlotPath().toString() + " - Could not rename old slot: " + oldTargetSlotName + " to: " + targetSlotName);
                  logger.trace(getSlotPath().toString() + e.getMessage());
                  if(logger.isTraceOn()) e.printStackTrace();
                }
              }
              break;
            }
          }
          
          if(!renamedOldSlot)
          {
            if(sourceIsActionOrTopic)
            {
              if(sourceSlot.isAction())
                this.add(targetSlotName, new BCompositeAction(), Flags.SUMMARY, null, null);
              else
                this.add(targetSlotName, new BCompositeTopic(), Flags.SUMMARY, null, null);
            }
            else
            {
              this.add(targetSlotName, sourceBValue.newCopy(), Flags.SUMMARY, null, null);
              slotAdded = true;
            }
          }
        }
      }
      catch (Exception e)
      {
        logger.error(getSlotPath().toString() + " - Could not create new slot: " + targetSlotName);
        logger.trace(getSlotPath().toString() + e.getMessage());
        if(logger.isTraceOn()) e.printStackTrace();
        continue;
      }
      
      
      if(!slotAdded && !sourceIsActionOrTopic)
      {
        Type sourceSlotType = null;
        Type targetSlotType = null;
        
        try {sourceSlotType = sourceBValue.getType();}
        catch (Exception e)
        {
          logger.error(getSlotPath().toString() + " - Could not read target slot type for: " + ord + ", slot name: " + sourceSlotName);
          logger.trace(getSlotPath().toString() + e.getMessage());
          if(logger.isTraceOn()) e.printStackTrace();
          continue;
        }
        
        try {targetSlotType = ((BObject)get(targetSlotName)).getType();}
        catch (Exception e)
        {
          logger.error(getSlotPath().toString() + " - Could not read source slot type for: " + targetSlotName);
          logger.trace(getSlotPath().toString() + e.getMessage());
          if(logger.isTraceOn()) e.printStackTrace();
          continue;
        }
        
        if(sourceSlotType != targetSlotType)
        {
          try {this.remove(targetSlotName);}
          catch (Exception e)
          {
            logger.error(getSlotPath().toString() + " - Could not remove slot: " + targetSlotName + " (this slot is the incorrect type)");
            logger.trace(getSlotPath().toString() + e.getMessage());
            if(logger.isTraceOn()) e.printStackTrace();
            continue;
          }
          
          try {this.add(targetSlotName, sourceBValue.newCopy(), Flags.SUMMARY, null, null);}
          catch (Exception e)
          {
            logger.error(getSlotPath().toString() + " - Could not create new slot: " + targetSlotName);
            logger.trace(getSlotPath().toString() + e.getMessage());
            if(logger.isTraceOn()) e.printStackTrace();
            continue;
          }
        }
      }
      
      if(!renamedOldSlot)
      {
        try
        {
          BComponent destinationComp = this;
          links = destinationComp.getLinks(destinationComp.getSlot(targetSlotName));
          if(sourceSlotName.length() == 0)
          {
            if(links.length>0) destinationComp.remove(links[0]);
            ((BStatusString) ((BObject)destinationComp.get(targetSlotName))).setValue(BFormat.make(formatOrd).format(destinationComp));
            continue;
          }
          
          if(links.length>0)
          {
            //will only alter link 0, not meant as a many to 1!!!
            //try to make ord from input link string
            if(isOrdValid(ord))
            {
              links[0].setSourceOrd(com.getHandleOrd());
              validLinks = true;
            }
            else
            {
              //invalid ord, remove link 0
              destinationComp.remove(links[0]);
            }
          }
          else
          {
            //no link, create one if possible
            if(isOrdValid(ord))
            {
              BLink link = new BLink(com.getHandleOrd(),sourceSlotName,targetSlotName,true);
              destinationComp.add(null, link);
              validLinks = true;
            }
            else
            {
              logger.error(getSlotPath().toString() + " - Invalid ord: " + ord);
              validLinks = false;
            }
          }
        }
        catch (Exception e)
        {
          logger.error(getSlotPath().toString() + " - Link create/modify error: " + ord + ", source slot: " + sourceSlotName  + ", Target slot:" + targetSlotName);
          logger.trace(getSlotPath().toString() + e.getMessage());
          if(logger.isTraceOn()) e.printStackTrace();
          validLinks = false;
        }
      }
      
      if(validLinks)
      {
        try {((BStatusValue) ((BObject) get(targetSlotName))).setStatusInAlarm(false);}
        catch (Exception e){}
      }
      else 
      {
        try {((BStatusValue) ((BObject) get(targetSlotName))).setStatusInAlarm(true);}
        catch (Exception e){}
      }
    }

    for (int i = 0; i < arrSlotInfo.length; i++)
    {
      String oldTargetSlotName = arrSlotInfo[i][colTargetSlotName];
      if(oldTargetSlotName == null) continue;
      
      oldTargetSlotName = SlotPath.escape(oldTargetSlotName);
      boolean foundSlot = false;
      
      if(strOrds.length > i)
      {
        String newTargetSlotName = strOrds[i][colTargetSlotName];
        if(newTargetSlotName != null) if(oldTargetSlotName.equals(SlotPath.escape(newTargetSlotName))) foundSlot = true;
      }

      if(!foundSlot)
      {
        for (int j = 0; j < strOrds.length; j++)
        {
          String newTargetSlotName = strOrds[j][colTargetSlotName];
          if(newTargetSlotName != null) if(oldTargetSlotName.equals(SlotPath.escape(newTargetSlotName)))
          {
            foundSlot = true;
            break;
          }
        }
      }
      
      if(!foundSlot && ((BObject)get(oldTargetSlotName))!=null)
      {
        try {this.remove(oldTargetSlotName);}
        catch (Exception e)
        {
          logger.error(getSlotPath().toString() + " - Could not remove unnecessary slot: " + oldTargetSlotName);
          logger.trace(getSlotPath().toString() + e.getMessage());
          if(logger.isTraceOn()) e.printStackTrace();
          continue;
        }
      }
    }
    arrSlotInfo = strOrds;
  }
  
  /**Checks to ensure the ord is valid before linking.*/
  private boolean isOrdValid(BOrd ord)
  {
    try
    {
      //try to create the component - if it fails, false
      BComponent com = (BComponent)ord.relativizeToHost().get();
      //This gets rid of the "unused variable" warning Eclipse gives me
      com = (BComponent)com;
      return true;
    }
    catch(Exception e)
    {
      return false;
    }
  }
  
  private static String[][] split(String inString, String delim1, String delim2)
  {                  
    if(inString.indexOf(delim1) == -1 && inString.indexOf(delim2) == -1) 
    {
      if (inString.length() == 0) return new String[0][0];
      else
      {
        String[][] outputArray;
        if(inString.indexOf(delim1) == -1 && inString.indexOf(delim2) > -1)
        {
          String[] tempArray = split(inString, delim2);
          outputArray = new String[1][tempArray.length];
          
          for(int i = 0; i < tempArray.length; i++) outputArray[0][i] = tempArray[i];
          return outputArray;
        }
        else
        {
          String[] tempArray = split(inString, delim1);
          outputArray = new String[tempArray.length][1];
          
          for(int i = 0; i < tempArray.length; i++) outputArray[i][0] = tempArray[i];
          return outputArray;
        }
      }
    }
    else
    {
      String[] arrDelim1 = split(inString, delim1);
      String[][] list = new String[arrDelim1.length][8];
      String[] arrDelim2;
      int secondDimensionSize = 0;
      
      for (int i = 0; i < arrDelim1.length; i++)
      {
        arrDelim2 = split(arrDelim1[i], delim2);
        secondDimensionSize = Math.max(secondDimensionSize, arrDelim2.length);
        list = resizeArray(list, arrDelim1.length, secondDimensionSize);
        
        for (int j = 0; j < arrDelim2.length; j++) list[i][j] = arrDelim2[j];
      }
      
      if(list[0].length == secondDimensionSize) return list;
      else
      {
        String[][] trim = new String[list.length][secondDimensionSize];
        for(int i = 0; i < trim.length; i++)
          for(int j = 0; j < trim[i].length; j++)
            trim[i][j] = list[i][j];
        
        return trim;
      }
    }
  }
  
  private static String[][] resizeArray(String[][] inArray, int len1, int len2)
  {
    if (len1 <= inArray.length && len2 <= inArray[0].length) return inArray;
    int tempMin;
    
    if(len1 <= inArray.length) tempMin = inArray.length;
    else tempMin = 100;
    
    int newLength1 = Math.min(tempMin, inArray.length*2);
    newLength1 = Math.max(tempMin, len1);
    
    if(len2 <= inArray[0].length) tempMin = inArray[0].length;
    else tempMin = 100;
    
    int newLength2 = Math.min(tempMin, inArray[0].length*2);
    newLength2 = Math.max(tempMin, len2);
    String[][] expand = new String[newLength1][newLength2];
    
    for(int i = 0; i < inArray.length; i++)
      for(int j = 0; j < inArray[i].length; j++)
        expand[i][j] = inArray[i][j];
    
    return expand;
  }
  
  
  private static String[] split(String inString, String delim)
  {                  
    if (inString.indexOf(delim) == -1) 
    {
      if (inString.length() == 0) return new String[0];
      else return new String[] { inString };
    }

    String[] list = new String[8];
    int firstChar = 0;
    int lastChar = 0;
    int index = 0;
    while (lastChar < inString.length())
    {
      if (inString.substring(lastChar).startsWith(delim))
      {
        list = resizeArray(list, index);
        list[index++] = inString.substring(firstChar, lastChar);
        lastChar = lastChar + delim.length();
        firstChar = lastChar;
      }
      else lastChar++;
    }
    list = resizeArray(list, index);
    list[index++] = inString.substring(firstChar, inString.length());

    if (index == list.length) return list;
    else
    {
      String[] trim = new String[index];
      System.arraycopy(list, 0, trim, 0, index);
      return trim;
    }
  }
  
  private static String[] resizeArray(String[] inArray, int len)
  {
    if (len < inArray.length) return inArray;
    int newLength = Math.min(100, inArray.length*2);
    String[] expand = new String[newLength];
    System.arraycopy(inArray, 0, expand, 0, inArray.length);
    return expand;
  }
}

