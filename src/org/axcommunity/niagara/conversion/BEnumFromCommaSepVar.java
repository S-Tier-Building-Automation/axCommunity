package org.axcommunity.niagara.conversion;


import java.util.StringTokenizer;
/** Extension of the EnumWritable that accepts a csv string, one per line, as the range
 * @author Mike Arnott, Kors Engineering
*/

import javax.baja.control.BEnumWritable;
import javax.baja.log.Log;
import javax.baja.naming.SlotPath;
import javax.baja.status.BStatusString;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.*;

public class BEnumFromCommaSepVar extends BEnumWritable
{
  /** 
   * This the csv list to use for the enum string outputs
   * To use, put in a value something like:
   * 1,Hello World
   * 2,This is Line 2
   * 3,Get the idea?
   * */
  public static final Property inCommaSepVar = newProperty(Flags.SUMMARY,new BStatusString(""),BFacets.make("multiLine",true));
  public BStatusString getInCommaSepVar() {
    return (BStatusString)get(inCommaSepVar);
  }
  public void setInCommaSepVar(BStatusString v) {
    set(inCommaSepVar, v);
  }
  
  public void started(){
    //setFacets(BFacets.make("multiLine",true);
   
  }
  
  
  public void changed(Property property, Context context){
    if(!Sys.atSteadyState() || !isRunning()){
        return;
      }
        if (property == inCommaSepVar) {
          //parse carriage return delimited records
          StringTokenizer tokzer = new StringTokenizer(getInCommaSepVar().getValue(),"\r\n");
          int tokCount =tokzer.countTokens();
          if(tokCount>=0)
          {
            String[] tokVals = new String[tokzer.countTokens()];
            int[] tokKeys = new int[tokzer.countTokens()];
            for (int i = 0;i<tokCount;i++){
              String tmp = tokzer.nextToken();
              //parse each line for the commas
              String[] row = TextUtil.split(tmp, ',');
              tokKeys[i] = Integer.parseInt(row[0]);
              tokVals[i] = SlotPath.escape(row[1]);
            }
            //create enum range facets
            BEnumRange range = BEnumRange.make(tokKeys, tokVals);
            BFacets bf = BFacets.makeEnum(range);
            //add multiline back in to the facets
            bf = BFacets.make(bf,BFacets.make("multiLine",true));

            this.setFacets(bf);

          }
        }
  }
  

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
  public static final Log logger = Log.getLog("axCommunity.BEnumFromCommaSepVar");

  public static final Type TYPE = Sys.loadType(BEnumFromCommaSepVar.class);
  public Type getType() { return TYPE; }

  
}
