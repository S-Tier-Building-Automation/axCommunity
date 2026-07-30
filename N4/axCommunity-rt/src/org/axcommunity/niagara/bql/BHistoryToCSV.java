/* SPDX-License-Identifier: GPL-2.0-only */

package org.axcommunity.niagara.bql;

import javax.baja.file.*;
import javax.baja.naming.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*************************************************************************************************
 * This Code will Export a History file and save it as a .csv.  
 *
 * The "history" will be the bql query call for the history file you wish to create.
 * The "historyName" will be the file name that it is saved as. 
 * The file is saved in the "Path" location. 
 * The "Path" must include a final "/" ,and the folder that you are saving to must already exist.
 * @author  CMH, Xex-com		07/31/09
 */

public class BHistoryToCSV extends BComponent{
	/**Enter the the bql query call for the history file you wish to create.*/
	public static final Property history = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getHistory() { return (BStatusString)get(history);}
	public void setHistory (BStatusString v) {set(history,v);}

	/**Enter the filename of the export*/
	public static final Property historyName = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getHistoryName() { return (BStatusString)get(historyName);}
	public void setHistoryName (BStatusString v) {set(historyName,v);}

	/**Enter the path for the .CSV file.  Default is the Daemon folder.  Must include a final "/" ,and the folder that you are saving to must already exist.*/
	public static final Property path = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getPath() { return (BStatusString)get(path);}
	public void setPath (BStatusString v) {set(path,v);}



	/**
	 * This action:
	 *   - executes bql query which returns a ITable
	 *   - formats the table as an in-memory CSV file
	 *   - saves the CSV file as an file
	 */
	public static final Action execute = newAction(0);
	public void execute(){
		invoke(execute, null);
	}
	public void doExecute(){
		try{
			OrdTarget table = query();
			writeCsv(table);
		}
		catch(Exception e){
			logger.log(Level.SEVERE, getSlotPath() + " export failed: " + e.getMessage(), e);
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
	 * Stream the table straight through the CSV exporter to disk. (The previous
	 * version wrote the CSV String via ObjectOutputStream, producing a Java
	 * serialization blob instead of a CSV file.)
	 *
	 * path and historyName are linkable properties, so treat them as operator
	 * input: refuse '..' traversal and path-in-filename tricks.
	 */
	private void writeCsv(OrdTarget table)
	throws Exception
	{
		String dir = getPath().getValue();
		String name = getHistoryName().getValue() + ".csv";
		if (dir.contains("..") || name.contains(".."))
			throw new IOException("refusing path containing '..': " + dir + name);
		if (name.contains("/") || name.contains("\\") || new File(name).isAbsolute())
			throw new IOException("historyName must be a plain file name, not a path: " + name);

		BExporter exporter = (BExporter)Sys.getType("file:ITableToCsv").getInstance();
		try (FileOutputStream fos = new FileOutputStream(dir + name, false))
		{
			exporter.export(ExportOp.make(table, fos));
		}
	}

	private static final Logger logger = Logger.getLogger("axCommunity.HistoryToCSV");
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/XENCOM_LogoMini.png");

	public static final Type TYPE = Sys.loadType(BHistoryToCSV.class);
	public Type getType() { return TYPE; }
}



