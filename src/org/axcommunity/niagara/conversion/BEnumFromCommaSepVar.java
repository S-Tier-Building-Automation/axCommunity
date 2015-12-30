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
	public BStatusString getInCommaSepVar() {return (BStatusString)get(inCommaSepVar);}
	public void setInCommaSepVar(BStatusString v) {set(inCommaSepVar, v);}

	public static final Property inCsvWithNoIndex = newProperty(0,new BStatusString(""),BFacets.make("multiLine",true));
	public BStatusString getInCsvWithNoIndex() {return (BStatusString)get(inCsvWithNoIndex);}
	public void setInCsvWithNoIndex(BStatusString v) {set(inCsvWithNoIndex, v);}
	
	
	public void started()
	{
		//setFacets(BFacets.make("multiLine",true);
	}


	public void changed(Property property, Context context)
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		
		if (property == inCommaSepVar)
		{
			try
			{
				//PARSE CARRIAGE RETURN DELIMITED RECORDS
				StringTokenizer	tokzer		= new StringTokenizer(getInCommaSepVar().getValue(),"\r\n");
				int				tokCount	= tokzer.countTokens();
				
				if(tokCount>=0)
				{
					String[]	tokVals	= new String[tokzer.countTokens()];
					int[]		tokKeys	= new int[tokzer.countTokens()];
					
					for (int i = 0;i<tokCount;i++)
					{
						String		tmp	= tokzer.nextToken();
						
						//PARSE EACH LINE FOR THE COMMAS
						String[]	row	= TextUtil.split(tmp, ',');
						tokKeys[i]		= Integer.parseInt(row[0]);
						tokVals[i]		= SlotPath.escape(row[1]);
						
						logger.trace("\t" + getSlotPath()	+ "\t KEY: " + tokKeys[i] + "\t VAL: " + tokVals[i]);
					}
					
					//CREATE ENUM RANGE FACETS
					BEnumRange	range	= BEnumRange.make(tokKeys, tokVals);
					BFacets		bf		= BFacets.makeEnum(range);
					
					//ADD MULTILINE BACK IN TO THE FACETS
					bf = BFacets.make(bf,BFacets.make("multiLine",true));

					this.setFacets(bf);
				}
			}
			catch (Exception e) 
			{
				logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
			}
		}
		
		
		if (property == inCsvWithNoIndex)
		{
			try
			{
				//PARSE COMMA DELIMITED RECORDS
				StringTokenizer	tokzer		= new StringTokenizer(getInCsvWithNoIndex().getValue(),",");
				int				tokCount	= tokzer.countTokens();
				
				if(tokCount>=0)
				{
					String[]	tokVals	= new String[tokzer.countTokens()];
					int[]		tokKeys	= new int[tokzer.countTokens()];
					
					for (int i = 0;i<tokCount;i++)
					{
						String		tmp	= tokzer.nextToken();
						
						tokKeys[i]		= i;
						// tokVals[i]		= tmp;
						tokVals[i]		= SlotPath.escape(tmp);
						
						logger.trace("\t" + getSlotPath()	+ "\t KEY: " + tokKeys[i] + "\t VAL: " + tokVals[i]);
					}
					
					//CREATE ENUM RANGE FACETS
					BEnumRange	range	= BEnumRange.make(tokKeys, tokVals);
					BFacets		bf		= BFacets.makeEnum(range);
					
					//ADD MULTILINE BACK IN TO THE FACETS
					bf = BFacets.make(bf,BFacets.make("multiLine",true));

					this.setFacets(bf);
				}
			}
			catch (Exception e) 
			{
				logger.error("\n" + getSlotPath()	+ "\n" + e.getMessage() + "\n" + e.getStackTrace());
			}
		}
	}


	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	public static final Log logger = Log.getLog("axCommunity.BEnumFromCommaSepVar");

	public static final Type TYPE = Sys.loadType(BEnumFromCommaSepVar.class);
	public Type getType() { return TYPE; }


}
