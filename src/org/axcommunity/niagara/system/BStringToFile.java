package org.axcommunity.niagara.system;
import javax.baja.sys.*;         /* baja Predefined*/
import javax.baja.status.*;      /* baja Predefined*/
import java.io.*;  

/*************************************************************************************************
 * This Code will save a string input to a file  
 *
 * Borrowed heavily from the BHistoryToCSV from CMH, thanks!
 * @author  MLA, Kors Engineering, 04/13/2010
 */
public class BStringToFile extends BComponent
{
  private static BFacets tBox = BFacets.make("multiLine",true);
  
  public static final Action execute = newAction(0);
  public void execute(){
    invoke(execute, null);
  }
  public void doExecute(){
    try{
      String filename = getFileName().getValue();
      String attachment = getInStringToSave().getValue();
      if(filename.length()>3&&attachment.length()>0)
      {
        
        FileWriter fstream = new FileWriter(getPath().getValue() + filename, getInAppendToFile().getValue());
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(attachment);
        //Close the output stream
        out.close();

      }
    }
    catch(Exception e){
      System.out.println(e.toString());
    }
  }
  
  
  /**
   * String input
   */
  public static final Property inStringToSave = newProperty(Flags.SUMMARY, new BStatusString(),tBox);
  public BStatusString getInStringToSave() { return (BStatusString)get(inStringToSave);}
  public void setInStringToSave(BStatusString v) {set(inStringToSave,v);}

  
  /**
   * If true, data will be written to the end of the file 
   */
  public static final Property inAppendToFile = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
  public BStatusBoolean getInAppendToFile() { return (BStatusBoolean)get(inAppendToFile);}
  public void setInAppendToFile(BStatusBoolean v) {set(inAppendToFile,v);}

  
  /**Enter the filename of the export*/
  public static final Property fileName = newProperty(Flags.SUMMARY, new BStatusString());
  public BStatusString getFileName() { return (BStatusString)get(fileName);}
  public void setFileName (BStatusString v) {set(fileName,v);}

  /**Enter the path for the file.  Default is the Daemon folder.  Must include a final "/" ,and the folder that you are saving to must already exist.*/
  public static final Property path = newProperty(Flags.SUMMARY, new BStatusString());
  public BStatusString getPath() { return (BStatusString)get(path);}
  public void setPath (BStatusString v) {set(path,v);}

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
    
  public static final Type TYPE = Sys.loadType(BStringToFile.class);
  public Type getType() { return TYPE; }   
}
