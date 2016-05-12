package org.axcommunity.niagara.system;

import javax.baja.log.Log;
import javax.baja.sys.*;         /* baja Predefined*/
import javax.baja.status.*;      /* baja Predefined*/
import java.io.*;  

/*************************************************************************************************
* This Code will save a string input to a file  
*
* Borrowed heavily from the BHistoryToCSV from CMH, thanks!
* @author  MLA, Kors Engineering, 04/13/2010
* 03/30/2012 added threading to avoid watchdog timeouts caused by network share path failures
*/
public class BStringToFile extends BComponent
{
	private static BFacets tBox = BFacets.make("multiLine",true);

	public static final Action execute = newAction(0);
	public void execute()
	{
		invoke(execute, null);
	}
	public void doExecute()
	{
		getOutSuccess().setValue(false);
		getOutFail().setValue(false);
		new FileThread().start();
	}

	class FileThread extends Thread
	{
		public void run()
		{
			try 
			{
				String filename = getFileName().getValue();
				String attachment = getInStringToSave().getValue();
				if(filename.length()>3&&attachment.length()>0)
				{
					FileWriter fstream = new FileWriter(getPath().getValue() + filename, getInAppendToFile().getValue());
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(attachment);
					//Close the output stream
					out.close();
					getOutSuccess().setValue(true);
					fireSuccesss(BBoolean.make(true));
				}
			}
			catch (Exception e) 
			{
				getOutFail().setValue(true);
				fireFail(BBoolean.make(true));
				logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
				throw new RuntimeException(e);
			}
			setLastTransaction(BAbsTime.make());
		}
	}

	/**String input.*/
	public static final Property inStringToSave = newProperty(Flags.SUMMARY, new BStatusString(),tBox);
	public BStatusString getInStringToSave() { return (BStatusString)get(inStringToSave);}
	public void setInStringToSave(BStatusString v) {set(inStringToSave,v);}

	/**If true, data will be written to the end of the file.*/
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
	
	public static final Property lastTransaction = newProperty(0|Flags.READONLY, BAbsTime.make(), BFacets.make("showSeconds",true));
	public BAbsTime getLastTransaction() { return (BAbsTime)get(lastTransaction); }
	public void setLastTransaction(BAbsTime v) { set(lastTransaction, v); }
	
	/**True when the file is created successfully.*/
	public final static Property outSuccess = newProperty(0|Flags.READONLY, new BStatusBoolean(false));
	public BStatusBoolean getOutSuccess() { return (BStatusBoolean)get(outSuccess); }
	public void setOutSuccess(BStatusBoolean v) { set(outSuccess, v); }
	
	/**True when the file creation fails.*/
	public final static Property outFail = newProperty(0|Flags.READONLY, new BStatusBoolean(false));
	public BStatusBoolean getOutFail() { return (BStatusBoolean)get(outFail); }
	public void setOutFail(BStatusBoolean v) { set(outFail, v); }
	
	/**Fires true when the file is created successfully.*/
	public static final Topic Successs = newTopic(0);
	public void fireSuccesss(BBoolean event){fire(Successs,event,null);}
	
	/**Fires true when the file creation fails.*/
	public static final Topic Fail = newTopic(0);
	public void fireFail(BBoolean event){fire(Fail,event,null);}
	
	
	public static final Log logger = Log.getLog("axCommunity.StringToFile");
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BStringToFile.class);
	public Type getType() { return TYPE; }   
}
