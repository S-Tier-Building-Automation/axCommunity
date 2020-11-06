package org.axcommunity.niagara.conversion;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import javax.baja.control.BEnumWritable;
import javax.baja.naming.SlotPath;
import javax.baja.nre.util.TextUtil;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

/** Extension of the EnumWritable that accepts a csv string, one per line, as the range
* @author Mike Arnott, Kors Engineering
* 
* 	Update 6/29/2017 by James Johnson to move to current logger syntax
*/
@SuppressWarnings("rawtypes")
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

	/**
	* This the csv list to use for the enum string outputs
	* To use no key value is required, put in a value something like the 
	* someValue,anotherValue,moreValue,etcValue
	* */
	public static final Property inCsvWithNoIndex = newProperty(0,new BStatusString(""),BFacets.make("multiLine",true));
	public BStatusString getInCsvWithNoIndex() {return (BStatusString)get(inCsvWithNoIndex);}
	public void setInCsvWithNoIndex(BStatusString v) {set(inCsvWithNoIndex, v);}
	
	public static final Property outString = newProperty(Flags.SUMMARY, new BStatusString());
    public BStatusString getOutString() { return (BStatusString)get(outString);}
    public void setOutString(BStatusString v) {set(outString,v);}
	
	public static final Property outNumeric  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutNumeric() {return (BStatusNumeric) get(outNumeric); }
	public void setOutNumeric(BStatusNumeric v) {set(outNumeric, v);}
	
	
	public void started()
	{
		//setFacets(BFacets.make("multiLine",true);
	}

	//----------------------------------------------------------------------------------------------------------
	public void changed(Property property, Context context)
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		
		
		//******************************************************************************************************
		if (property == out) 
		{
			/*
			 * Setting the slot status is commented out because if you have something linked to the slot that is triggered on change it will get fired twice,
			 * once for the value change and once for the status change. I was thinking of adding a boolean option input slot to allow someone to allow the status change or not.
			 * However, if everything works correctly (no exception errors) then you should never need to set the status of the slot. 
			 */
			
			try					{setOutString(new BStatusString(((BEnumRange)this.getFacets().get("range")).getDisplayTag(this.getOut().getValue().getOrdinal(), null)/*, BStatus.ok*/));	}
			catch (Exception e)	{setOutString(new BStatusString(""/*, BStatus.nullStatus*/));																								}
			
			try					{setOutNumeric(new BStatusNumeric((double)this.getOut().getValue().getOrdinal()/*, BStatus.ok*/));	}
			catch (Exception e)	{setOutNumeric(new BStatusNumeric(0/*, BStatus.ok*/));}
			
		}  
		
		
		//******************************************************************************************************
		if (property == inCommaSepVar)
		{
			try
			{
				//PARSE CARRIAGE RETURN DELIMITED RECORDS
				StringTokenizer	tokzer			= new StringTokenizer(getInCommaSepVar().getValue(),"\r\n");
				int				tokCount		= tokzer.countTokens();
				
				if(tokCount>=0)
				{
					String[]	tokVals			= new String[tokzer.countTokens()];
					int[]		tokKeys			= new int[tokzer.countTokens()];
					
					//THIS WILL BE USED TO DETERMINE THE FINAL SIZE OF OUR ARRAY IN CASE IT NEEDS TO BE RESIZED DUE TO DUPLICATES.
					int			keyCount		= 0; 
					
					//THIS WILL BE USED TO DETERMINE IF WE'VE SET THE VALUE FOR THE ZERO INDEX YET
					boolean		zeroIndexSet	= false;
					
					
					
					
					//================================================================================
					//== INTERATE THRU EACH TOKEN AND ADD VALUES TO ARRAY IF NOT DUPLICATES ==========
					//================================================================================
					for (int i = 0; i < tokCount; i++)
					{
						String		tmp		= tokzer.nextToken();
						
						//PARSE EACH LINE FOR THE COMMAS
						String[]	row		= TextUtil.split(tmp, ',');
						
						boolean		dupKey	= false;
						boolean		dupVal	= false;
						
						
						//ONLY IF LOG LEVEL IS TRACE AND THIS IS THE FIRST TOKEN OUTPUT TO CONSOLE THE ARRAY VALUES.
						if(logger.isLoggable(Level.FINE) && i==0)
						{
							try
							{
								for (int k = 0; k < tokCount; k++)
								{
									logger.log(Level.FINE, "\t" + getSlotPath() + "\t ("+k+" of "+tokCount+") " + "\t tokKeys[" + k + "] = " + tokKeys[k] + "\t\t tokVals[" + k + "] = " + tokVals[k] );
								}
							}
							catch (Exception e) 
							{
								String msg	= "Error while showing array values. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
								logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
							}
						}
						
						
						
						
						// CHECK FOR DUPLICATE KEYS, THIS ONE IS TRICKY BECAUSE THE ENTIRE ARRAY HAS VALUE OF ZERO AND 
						// WE COULD HAVE A KEY BEING SET TO ZERO. WE ONLY WANT TO ALLOW ZERO TO BE SET TO A VALUE ONCE.
						for (int k = 0; k < tokKeys.length; k++)
						{
							if((Integer.parseInt(row[0])==0) && zeroIndexSet==false)
							{
								logger.log(Level.FINE, "\t" + getSlotPath() + "\t **ZERO KEY BUT NO YET SET**  Integer.parseInt(row[0]) = " + (Integer.parseInt(row[0])) + " and zeroIndexSet = " + zeroIndexSet); 
								zeroIndexSet	= true;
								dupKey			= false;
								break;
							}
							else if((Integer.parseInt(row[0])==0) && zeroIndexSet==true)
							{
								logger.log(Level.FINE, "\t" + getSlotPath() + "\t **DUP ZERO KEY FOUND** Integer.parseInt(row[0]) = " + (Integer.parseInt(row[0])) + " and zeroIndexSet = " + zeroIndexSet);
								dupKey = true;
								break;
							}
							else if(tokKeys[k]==Integer.parseInt(row[0]))
							{
								logger.log(Level.FINE, "\t" + getSlotPath() + "\t **DUP NON-ZERO KEY FOUND** Integer.parseInt(row[0]) = " + (Integer.parseInt(row[0])) + " and zeroIndexSet = " + zeroIndexSet);
								dupKey = true;
								break;
							}
							else
							{
								dupKey = false;
							}
						}
						
						
						//CHECK FOR DUPLICATE VALUES
						for (int v = 0; v < tokVals.length; v++)
						{
							if(tokVals[v] != null)
							{
								if(tokVals[v].compareTo(SlotPath.escape(row[1]))==0)
								{
									dupVal = true;
									break;
								}
							}
						}
						
						
						//===========================================================================
						//== IF NOT A DUPLICATE ADD IT TO OUR ARRAY. ================================
						//===========================================================================
						if(dupKey==false && dupVal==false)
						{
							tokKeys[keyCount]		= Integer.parseInt(row[0]);
							tokVals[keyCount]		= SlotPath.escape(row[1]);
							
							if((Integer.parseInt(row[0])==0) && zeroIndexSet==false)
							{
								zeroIndexSet = true;
							}
							
							logger.log(Level.FINE, "\t" + getSlotPath() + "\t (" + keyCount + ") Added tag '" + SlotPath.escape(row[1]) + "' with ordinal '"+ (Integer.parseInt(row[0])) + "' to index '" + keyCount + "' zeroIndexSet = '" + zeroIndexSet + "'");
							keyCount++;
						}
						else
						{
							logger.log(Level.FINE, "\t" + getSlotPath() + "\t DUPLICATE FOUND '" + SlotPath.escape(row[1]) + "' with index '"+ (Integer.parseInt(row[0])) + "'");
						}
						
						
						
						//ONLY IF LOG LEVEL IS TRACE OUTPUT TO CONSOLE THE ARRAY VALUES.
						if(logger.isLoggable(Level.FINE))
						{
							try
							{
								for (int k2 = 0; k2 < tokCount; k2++)
								{
									logger.log(Level.FINE, "\t" + getSlotPath() + "\t ("+k2+" of "+tokCount+") " + "\t tokKeys[" + k2 + "] = " + tokKeys[k2] + "\t\t tokVals[" + k2 + "] = " + tokVals[k2] );
								}
							}
							catch (Exception e) 
							{
								String msg	= "Error while showing array values. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
								logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
							}
						}
						
					}
					
					
					
					
					//----------------------------------------------------------------------------
					//-- DONE WITH THE TOKENS, NOW LETS CLEAN UP THE ARRAYS AND SET THE FACETS. --
					//----------------------------------------------------------------------------
					
					
					//ONLY IF LOG LEVEL IS TRACE OUTPUT TO CONSOLE THE ARRAY VALUES.
					if(logger.isLoggable(Level.FINE))
					{
						try
						{
							logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t BEFORE RESIZING ARRAY:");
							for (int v = 0; v < tokVals.length; v++)
							{
								logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t (" + v + ") KEY: " + tokKeys[v] + "\t VAL: " + tokVals[v]);
							}
						}
						catch (Exception e) 
						{
							String msg	= "Error while showing array values before resizing array. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
							logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
						}
					}
					
					
					//------------------------------------------------------------------------------------------------------------------
					//-- RESIZE ARRAY IN CASE WE HAD DUPLICATES, IF ARRAY HAS NULLS MAKING FACET FROM THE ARRAY WILL CAUSE AN ERROR.  --
					//------------------------------------------------------------------------------------------------------------------
					tokVals = (String[])resizeArray(tokVals, keyCount);
					tokKeys = (int[])resizeArray(tokKeys, keyCount);
					
					
					
					
					
					//ONLY IF LOG LEVEL IS TRACE OUTPUT TO CONSOLE THE ARRAY VALUES.
					if(logger.isLoggable(Level.FINE))
					{
						try
						{
							logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t AFTER RESIZING ARRAY:");
							for (int v = 0; v < tokVals.length; v++)
							{
								logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t (" + v + ") KEY: " + tokKeys[v] + "\t VAL: " + tokVals[v]);
							}
						}
						catch (Exception e) 
						{
							String msg	= "Error while showing array values after resizing array. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
							logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
						}
					}
					
					
					//FINALLY IF WE MADE IT THIS FAR WE CAN TRY TO CREATE THE ENUMRANGE FACET AND APPLY IT
					try
					{
						//CREATE ENUM RANGE FACETS
						BEnumRange	range	= BEnumRange.make(tokKeys, tokVals);
						BFacets		bf		= BFacets.makeEnum(range);
						
						//ADD MULTILINE BACK IN TO THE FACETS
						bf = BFacets.make(bf,BFacets.make("multiLine",true));

						this.setFacets(bf);
					}
					catch (Exception e) 
					{
						String msg	= "Error setting facets. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
						logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
					}
				}
			}
			catch (Exception e) 
			{
				String msg	= "inCommaSepVar Exception \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
				logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
			}
		}
		
		
		
		
		
		
		
		//******************************************************************************************************
		if (property == inCsvWithNoIndex)
		{
			try
			{
				//PARSE COMMA DELIMITED RECORDS
				StringTokenizer	tokzer		= new StringTokenizer(getInCsvWithNoIndex().getValue(),",");
				int				tokCount	= tokzer.countTokens();
				
				
				//ONLY PROCESS IF WE HAVE TOKENS.
				if(tokCount>=0)
				{
					String[]	tokVals		= new String[tokzer.countTokens()];
					int[]		tokKeys		= new int[tokzer.countTokens()];
					
					//THIS WILL BE USED TO DETERMINE THE FINAL SIZE OF OUR ARRAY IN CASE IT NEEDS TO BE RESIZED DUE TO DUPLICATES.
					int			keyCount	= 0; 
					
					
					
					//================================================================================
					//== INTERATE THRU EACH TOKEN AND ADD VALUES TO ARRAY IF NOT DUPLICATES ==========
					//================================================================================
					for (int i = 0; i < tokCount; i++)
					{
						String		tmp		= tokzer.nextToken();
						boolean		dupVal	= false;
						
						try
						{
							//DETERMINE IF CURRENT TOKEN IS A DUPLICATE OR NOT.
							for (int v1 = 0; v1 < tokVals.length; v1++)
							{
								logger.log(Level.FINE,"\t" + getSlotPath() + "\t ("+v1+" of "+tokVals.length+") " + "\t tokVals[" + v1 + "] = " + tokVals[v1] + " keyCount = " + keyCount + " tmp = " + tmp);
								
								if(tokVals[v1] != null)
								{
									if(tmp.compareTo(tokVals[v1])==0)
									{
										dupVal = true;
										break;
									}
									else
									{
										dupVal = false;
									}
								}
							}
							
							
							//===========================================================================
							//== IF NOT A DUPLICATE ADD IT TO OUR ARRAY. ================================
							//===========================================================================
							if(dupVal==false)
							{
								tokKeys[keyCount]		= keyCount;
								tokVals[keyCount]		= SlotPath.escape(tmp);
								
								logger.log(Level.FINE, "\t" + getSlotPath() + "\t (" + keyCount + ") Added tag '" + SlotPath.escape(tmp) + "' with ordinal '"+ (keyCount) + "' to index '" + keyCount + "'");
								keyCount++;
							}
							else
							{
								logger.log(Level.FINE, "\t" + getSlotPath() + "\t DUPLICATE FOUND '" + SlotPath.escape(tmp) + "'");
							}
						}
						catch (Exception e) 
						{
							String msg	= "inCsvWithNoIndex error looking for dups and adding to array. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
							logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
						}
					}
					
			
					//----------------------------------------------------------------------------
					//-- DONE WITH THE TOKENS, NOW LETS CLEAN UP THE ARRAYS AND SET THE FACETS. --
					//----------------------------------------------------------------------------
					
					//ONLY IF LOG LEVEL IS TRACE OUTPUT TO CONSOLE THE ARRAY VALUES.
					if(logger.isLoggable(Level.FINE))
					{
						try
						{
							logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t BEFORE RESIZING ARRAY:");
							for (int v = 0; v < tokVals.length; v++)
							{
								logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t (" + v + ") KEY: " + tokKeys[v] + "\t VAL: " + tokVals[v]);
							}
						}
						catch (Exception e) 
						{
							String msg	= "Error while showing array values before resizing array. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
							logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
						}
					}
					
					
					//------------------------------------------------------------------------------------------------------------------
					//-- RESIZE ARRAY IN CASE WE HAD DUPLICATES, IF ARRAY HAS NULLS MAKING FACET FROM THE ARRAY WILL CAUSE AN ERROR.  --
					//------------------------------------------------------------------------------------------------------------------
					tokVals = (String[])resizeArray(tokVals, keyCount);
					tokKeys = (int[])resizeArray(tokKeys, keyCount);
					
					
					
					
					//ONLY IF LOG LEVEL IS TRACE OUTPUT TO CONSOLE THE ARRAY VALUES.
					if(logger.isLoggable(Level.FINE))
					{
						try
						{
							logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t AFTER RESIZING ARRAY:");
							for (int v = 0; v < tokVals.length; v++)
							{
								logger.log(Level.FINE, "\t" + getSlotPath()	+ "\t (" + v + ") KEY: " + tokKeys[v] + "\t VAL: " + tokVals[v]);
							}
						}
						catch (Exception e) 
						{
							String msg	= "Error while showing array values after resizing array. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
							logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
						}
					}
					
					
					
					//FINALLY IF WE MADE IT THIS FAR WE CAN TRY TO CREATE THE ENUMRANGE FACET AND APPLY IT
					try
					{
						//CREATE ENUM RANGE FACETS
						BEnumRange	range	= BEnumRange.make(tokKeys, tokVals);
						BFacets		bf		= BFacets.makeEnum(range);
						
						//ADD MULTILINE BACK IN TO THE FACETS
						bf = BFacets.make(bf,BFacets.make("multiLine",true));
	
						this.setFacets(bf);
					}
					catch (Exception e) 
					{
						String msg	= "Error setting facets. \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
						logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
					}
					
				}
			}
			catch (Exception e) 
			{
				String msg = "\n" + "inCsvWithNoIndex Exception \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
				logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
			}
		}
	}
	
	
	
	
	//----------------------------------------------------------------------------------------------------------
	
	private Object resizeArray (Object oldArray, int newSize) 
	{
		try
		{
			int		oldSize			= java.lang.reflect.Array.getLength(oldArray);
			Class	elementType		= oldArray.getClass().getComponentType();
			Object	newArray		= java.lang.reflect.Array.newInstance(elementType, newSize);
			int		preserveLength	= Math.min(oldSize, newSize);
			
			logger.log(Level.FINE, "\t" + getSlotPath() + "\t oldSize = " + oldSize + "\t newSize = " + newSize + " elementType = " + elementType.toString());
			
			if (preserveLength > 0)
			{
				System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
			}
			
			return newArray; 
		}
		catch (Exception e) 
		{
			String msg	= "resizeArray() Exception \n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			msg = msg + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
			logger.log(Level.SEVERE, "\n" + getSlotPath()	+ "\n" + msg);
			
			return null;
		}
	}


	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	public static final Logger logger = Logger.getLogger("axCommunity.BEnumFromCommaSepVar");

	public static final Type TYPE = Sys.loadType(BEnumFromCommaSepVar.class);
	public Type getType() { return TYPE; }


}
