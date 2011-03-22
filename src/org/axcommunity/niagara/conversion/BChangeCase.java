package org.axcommunity.niagara.conversion;

import javax.baja.sys.*; 
import javax.baja.status.*;


/**
 * Changes case on Status String input to Upper, Lower and Tile case outputs.
 * @author Justin Koffler
*/
public class BChangeCase
  extends BComponent
{
  
  
  public void changed(Property property, Context context){
    if(!Sys.atSteadyState() || !isRunning()){
        return;
      }
  
  if (property == inString) {
  
  // execute code (set executeOnChange flag on inputs)     
      String temp = getInString().getValue(); 
      String upper = temp.toUpperCase();
      String lower = temp.toLowerCase();
     
      String firstLetter = "";
      String titleCase = "";
      firstLetter = lower.substring(0, 1).toUpperCase();
      titleCase = firstLetter + lower.substring(1,lower.length());
     
     
      StringBuffer sb = new StringBuffer(temp);
     
      // Go through the string, every time you come across
      // a new word set the first letter to upper case
     
      boolean haveSeenSpace = true; // Set it initially to true so that we set
      // the first letter
      for(int i = 0; i < sb.length();i++)
      {
       if(sb.charAt(i) == ' ')
        {
         haveSeenSpace = true;
        }
       else
        {
         // Must be a letter so check to see if the last item was
         // a space to set to upper case
         if(haveSeenSpace)
       {
        sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
        haveSeenSpace = false;
        }
         else
       {
        // Must be a letter so push to lower
        sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
       }
        }
      }
      String title = (sb.toString());
     
      getOutUppercase().setValue(upper);   
      getOutLowercase().setValue(lower); 
      getOutTitlecase().setValue(title); 
  }

  }

  /**Status String value in representing string to convert*/
  public static final Property inString = newProperty(Flags.SUMMARY, new BStatusString(""));
  public BStatusString getInString() { return (BStatusString)get(inString);}
  public void setInString(BStatusString v) {set(inString,v);}  

  /**Status String value out representing upper case of input*/
  public static final Property outUppercase = newProperty(Flags.SUMMARY, new BStatusString(""));
  public BStatusString getOutUppercase() { return (BStatusString)get(outUppercase);}
  public void setOutUppercase(BStatusString v) {set(outUppercase,v);}
  
  /**Status String value out representing lower case of input*/
  public static final Property outLowercase = newProperty(Flags.SUMMARY, new BStatusString(""));
  public BStatusString getOutLowercase() { return (BStatusString)get(outLowercase);}
  public void setOutLowercase(BStatusString v) {set(outLowercase,v);}
  
  /**Status String value out representing title case of input*/
  public static final Property outTitlecase = newProperty(Flags.SUMMARY, new BStatusString(""));
  public BStatusString getOutTitlecase() { return (BStatusString)get(outTitlecase);}
  public void setOutTitlecase(BStatusString v) {set(outTitlecase,v);}

 public Type getType() { return TYPE; }
 public static final Type TYPE = Sys.loadType(BChangeCase.class);
 
  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");

  
}
