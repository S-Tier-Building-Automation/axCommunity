package org.axcommunity.niagara.bql;
import javax.baja.sys.*;         /* baja Predefined*/
import javax.baja.status.*;      /* baja Predefined*/
import javax.baja.file.*;        /* baja User Defined*/
import javax.baja.naming.*;      /* baja User Defined*/
import java.io.*;                /* java User Defined*/

/**
	* This Code will Export a History file and save it as a .csv. 
	* The file is stored in the deamon folder under your primary WEBs folder.
	* @author  CMH, Xex-com		07/31/09
 */

public class BHistoryToCSV extends BComponent{
	/**Enter the history to be exported*/
	public static final Property history = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getHistory() { return (BStatusString)get(history);}
	public void setHistory (BStatusString v) {set(history,v);}

	/**Enter the filename of the export*/
	public static final Property historyName = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getHistoryName() { return (BStatusString)get(historyName);}
	public void setHistoryName (BStatusString v) {set(historyName,v);}

	/**Enter the path for the .CSV file.  Default is the Daemon folder*/
	public static final Property path = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getPath() { return (BStatusString)get(path);}
	public void setPath (BStatusString v) {set(path,v);}


	
	/** Executes bql query which returns a ITable, formats the table as an in-memory CSV file, saves the CSV file as an file*/
	public static final Action execute = newAction(0);
	public void execute(){
		invoke(execute, null);
	}
	public void doExecute(){
		try{
			OrdTarget table = query();
			String csv = exportToCsv(table);  
			hist(getHistoryName() + ".csv",csv);              
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}

	/**
	 * Perform a bql query which returns an OrdTarget for a BITable
	 */
	private OrdTarget query()
	throws Exception
	{ 
		return BOrd.make(getHistory().getValue()).resolve();
	}

	/**
	 * Run the CSV exporter against the specified table to build an
	 * in memory representation of the table as a CSV file.
	 */
	private String exportToCsv(OrdTarget table) 
	throws Exception
	{ 
		// create instance of ITableToCsv exporter                        
		BExporter exporter = (BExporter)Sys.getType("file:ITableToCsv").getInstance(); 

		// run the CSV exporter to export to memory based byte array
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ExportOp op = ExportOp.make(table, out);  
		exporter.export(op);

		// return as string (this String works because we  will use the default 
		// encoding, which should match encoding ITableToCsv exporter used to 
		// create a PrintWriter from a raw OutputStream)    
		return new String(out.toByteArray());
	}

	/**
	 * Save CSV as File 
	 */
	private void hist(String fileName, String attachment) 
	throws Exception
	{ 
		try
		{ 
			//FileOutputStream fos = new FileOutputStream( getPath() + getHistoryName() + ".csv", false);
			FileOutputStream fos = new FileOutputStream( getPath().getValue() + getHistoryName().getValue() + ".csv", false);
			ObjectOutputStream oos = new ObjectOutputStream (fos);
			oos.writeObject(attachment);
			oos.close();
		}
		catch (FileNotFoundException fnfe)
		{  
			System.out.println( " Unable to find " + getHistoryName() + ".csv" );
		}
	}

    public static final Type TYPE = Sys.loadType(BHistoryToCSV.class);
    public Type getType() { return TYPE; }
}



