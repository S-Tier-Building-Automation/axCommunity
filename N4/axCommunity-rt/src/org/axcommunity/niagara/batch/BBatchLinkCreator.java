package org.axcommunity.niagara.batch;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

import javax.baja.naming.*;
import javax.baja.registry.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.axcommunity.niagara.helperClasses.LoggingComponent;

/**
 * This component will add links to components as specified in the link list csv.
 * It also has to the ability to remove existing links on the target component 
 * depending on the option chosen from slot 'inHowToHandleExistingLinksOnTarget'.
 * <p>
 * WARNING:
 * Due to the possible havoc this component could cause if improperly ran, 
 * this component is meant to be used manually via workbench. 
 * It is recommenced you execute the 'DryRunOnly' action and review the output 
 * prior to executing the 'Execute' action.
 * This can be used via logic on a wiresheet, but that scenario isn't recommenced.
 * 
 *
 * @author Justin Koffler
 * @creation Oct 30, 2019
 *
 */
public class BBatchLinkCreator extends BComponent implements LoggingComponent
{
	/** This is the default value for slot {@link #inLinkListCsv}.*/
	private static	String			DEFAULT_CSV			= "%ORD_BASE%/sourceOrd, sourceSlot, %ORD_BASE%/targetOrd, targetSlot";
	
	/** Represents this component and is used when processing a BFormat in the thread later in this logic.*/
	private	BComponent	thisComp = this;
	
	/** When true additional info will be send to the application director console.*/
	public static final Property inDebug = newProperty(0, false);
	public boolean getInDebug() { return getBoolean(inDebug); }
	public void setInDebug(boolean v) { setBoolean(inDebug, v, null); }
	
	public static final Property inSuppressSlotPath = newProperty(0, false);
	public boolean getInSuppressSlotPath() { return getBoolean(inSuppressSlotPath); }
	public void setInSuppressSlotPath(boolean v) { setBoolean(inSuppressSlotPath, v, null); }
	
	/**
	 * Action used to execute the process without actually creating the links. 
	 * This gives an opportunity to see if there are any possible issues before 
	 * attempting to create the links.
	 */
	public static final Action DryRunOnly = newAction(Flags.SUMMARY, null);
	public void DryRunOnly(){if(!Sys.atSteadyState()||!isRunning()){return;} invoke(DryRunOnly,null,null);}
	public void doDryRunOnly(Context cx)
	{
		onExecute(true);
	}
	
	/**
	 * Unlike {@link #DryRunOnly} when this action is 
	 * used it will actually create the links defined.
	 */
	public static final Action Execute = newAction(Flags.SUMMARY|Flags.CONFIRM_REQUIRED, null);
	public void Execute(){if(!Sys.atSteadyState()||!isRunning()){return;} invoke(Execute,null,null);}
	public void doExecute(Context cx)
	{
		onExecute(false);
	}
	
	/** Resets the {@link #outCountSuccess} and {@link #outCountFailed} slots to zero.*/
	public static final Action ResetCounts = newAction(0, null);
	public void ResetCounts(){if(!Sys.atSteadyState()||!isRunning()){return;} invoke(ResetCounts,null,null);}
	public void doResetCounts(Context cx)
	{
		setOutCountSuccess(0);
		setOutCountFailed(0);
	}
	
	
	/** Clears any output values and internal variables.*/
	public static final Action ClearOutputs = newAction(0, null);
	public void ClearOutputs(){if(!Sys.atSteadyState()||!isRunning()){return;} invoke(ClearOutputs,null,null);}
	public void doClearOutputs(Context cx)
	{
		if(getOutRunning()==false)
		{
			messages.clear();
			linksGood.clear();
			linksBad.clear();
//			goodLinks			= 0;
//			badLinks			= 0;
			startTime			= 0;
			hasError 			= false;
			useConversionLink	= false;
			converter			= null;
			
			setOutListLinksCreated(new BStatusString("", BStatus.ok));
			setOutListLinksFailed(new BStatusString("", BStatus.ok));
			
			setOutLinksCreated(0);
			setOutLinksFailed(0);
			setOutSuccess(false);
			setOutError(false);
			setOutLastExecuted(BAbsTime.make(0));
			setOutExecutionTime(BRelTime.make(0));
			getOutMessage().setValue("");
		}
	}
	
	private	static	String[]	defaultEnumRange	= 
			{
				escape("Take no action, leave as is.")
				,escape("Remove if exists or any other links on target if not an action slot or does not have 'FAN_IN' flag set.")
				,escape("Remove if exists or any other links on target even if is an action slot or has 'FAN_IN' flag set.")
				,escape("Don't remove if exists, but do remove any other links on target if not an action slot or does not have 'FAN_IN' flag set.")
				,escape("Don't remove if exists, but do remove any other links on target even if is an action slot or has 'FAN_IN' flag set.")
			};
	private	static	BEnumRange	enumRange			= BEnumRange.make(defaultEnumRange);
	
	/** Determines how to handle existing links on the target component/slot.*/
	public static final Property inHowToHandleExistingLinksOnTarget = newProperty(Flags.HIDDEN, (BValue)BDynamicEnum.TYPE.getInstance(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100), BFacets.RANGE, enumRange));
	public BDynamicEnum getInHowToHandleExistingLinksOnTarget() { return (BDynamicEnum)get(inHowToHandleExistingLinksOnTarget); }
	public void setInHowToHandleExistingLinksOnTarget(BDynamicEnum v) { set(inHowToHandleExistingLinksOnTarget, v, null); }
	
	/**
	 * If your {@link javax.baja.naming.BOrd BOrd}s share a common path, you can specify that in this slot, then in slot {@link #inLinkListCsv} 
	 * you can use the variable %ORD_BASE% which will resolve to this slot's value.
	 */
	public static final Property inBaseOrd = newProperty(0, BOrd.DEFAULT, null);
	public BOrd getInBaseOrd() { return (BOrd) get(inBaseOrd); }
	public void setInBaseOrd(BOrd v) { set(inBaseOrd, v, null); }
	
	
	public static final Property inBaseOrdString = newProperty(0, BString.DEFAULT, BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public String getInBaseOrdString() { return getString(inBaseOrdString); }
	public void setInBaseOrdString(String v) { setString(inBaseOrdString, v, null); }
	
	
	
	/** Represents the list of links to be created. <br>
	 * Each link definition should be in the following csv format:<br>
	 * &#11;&#11;<i><code>sourceOrd, sourceSlot, targetOrd, targetSlot</code></i><br>
	 * Each csv should be separated by a new line. 
	 */
	public static final Property inLinkListCsv = newProperty(Flags.SUMMARY, BString.make(DEFAULT_CSV), BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(125)));
	public String getInLinkListCsv() { return getString(inLinkListCsv); }
	public void setInLinkListCsv(String v) { setString(inLinkListCsv, v, null); }
	
	
	
	
	/**When 'true' it indicates the process is actively running and prevents additional actions from being triggered.<br>
	 * Also by manually setting this value to false it will terminate the execution early if it is taking too long to complete.*/
	public static final Property outRunning = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE|Flags.TRANSIENT, false);
	public boolean getOutRunning() { return getBoolean(outRunning); }
	public void setOutRunning(boolean v) { setBoolean(outRunning, v); }
	
	/** Represents the number of links that were successfully created.*/
	public static final Property outLinksCreated = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, (BValue)BInteger.TYPE.getInstance(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public int getOutLinksCreated() { return getInt(outLinksCreated); }
	public void setOutLinksCreated(int v) { setInt(outLinksCreated, v, null); }
	
	/** Represents the number of links that failed to be created.*/
	public static final Property outLinksFailed = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, (BValue)BInteger.TYPE.getInstance(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public int getOutLinksFailed() { return getInt(outLinksFailed); }
	public void setOutLinksFailed(int v) { setInt(outLinksFailed, v, null); }
	
	
	public static final Property outListLinksCreated = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.ok), BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutListLinksCreated() { return (BStatusString) get(outListLinksCreated); }
	public void setOutListLinksCreated(BStatusString v) { set(outListLinksCreated, v); }
	
	public static final Property outListLinksFailed = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.ok), BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutListLinksFailed() { return (BStatusString) get(outListLinksFailed); }
	public void setOutListLinksFailed(BStatusString v) { set(outListLinksFailed, v); }
	
	/** Represented as true if no errors occurred in the process.*/
	public static final Property outSuccess = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, false);
	public boolean getOutSuccess() { return getBoolean(outSuccess); }
	public void setOutSuccess(boolean v) { setBoolean(outSuccess, v); }
	
	/** Represented as true if any errors occurred in the process.*/
	public static final Property outError = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, false);
	public boolean getOutError() { return getBoolean(outError); }
	public void setOutError(boolean v) { setBoolean(outError, v); }
	
	/** Represents the number of times this process has been executed successfully.*/
	public static final Property outCountSuccess = newProperty(Flags.DEFAULT_ON_CLONE, (BValue)BInteger.TYPE.getInstance(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public int getOutCountSuccess() { return getInt(outCountSuccess); }
	public void setOutCountSuccess(int v) { setInt(outCountSuccess,v,null); }

	/** Represents the number of times this process has been executed unsuccessfully.*/
	public static final Property outCountFailed = newProperty(Flags.DEFAULT_ON_CLONE, (BValue)BInteger.TYPE.getInstance(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(50)));
	public int getOutCountFailed() { return getInt(outCountFailed); }
	public void setOutCountFailed(int v) { setInt(outCountFailed,v,null); }
	
	/** Represents the last time this process has been executed.*/
	public static final Property outLastExecuted = newProperty(Flags.DEFAULT_ON_CLONE, BAbsTime.make(0), BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BAbsTime getOutLastExecuted() { return (BAbsTime)get(outLastExecuted); }
	public void setOutLastExecuted(BAbsTime v) { set(outLastExecuted, v); }
	
	/**Use for analytical purposes to determine how long the process takes to run. The process is threaded so it shouldn't bog down the station (in theory).*/
	public final static Property outExecutionTime = newProperty(Flags.DEFAULT_ON_CLONE, BRelTime.make(0),BFacets.make(BFacets.SHOW_MILLISECONDS, BBoolean.TRUE));
	public BRelTime getOutExecutionTime() { return (BRelTime)get(outExecutionTime); }
	public void setOutExecutionTime(BRelTime v) { set(outExecutionTime, v); }
	
	/** Represents any feedback messages for the process.*/
	public static final Property outMessage = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.ok), BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getOutMessage() { return (BStatusString) get(outMessage); }
	public void setOutMessage(BStatusString v) { set(outMessage, v); }
	
	/*-----------------------------------------------------------------------------------------------------*/
	public static final Topic Success = newTopic(0);
	public void fireSuccess(BBoolean event){fire(Success,event);}
	
	public static final Topic Error = newTopic(0);
	public void fireError(BBoolean event){fire(Error,event);}
	
	public static final Topic Message = newTopic(0);
	public void fireMessage(BString event){fire(Message,event);}
		
	public static final Topic LinksCreated = newTopic(0);
	public void fireLinksCreated(BDouble event){fire(LinksCreated,event,null);}
	
	public static final Topic LinksFailed = newTopic(0);
	public void fireLinksFailed(BDouble event){fire(LinksFailed,event,null);}

	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void stopped() throws Exception{	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void started() throws Exception
	{
		super.started();
		if(!Sys.atSteadyState() || !isRunning()){return;}
		startAndSteadyState();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void atSteadyState() throws Exception
	{
		super.atSteadyState();
		if(!Sys.atSteadyState() || !isRunning()){return;}
		startAndSteadyState();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void startAndSteadyState()
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		thisComp = this;
		setOutRunning(false);
		this.setDisplayName(inBaseOrd, BFormat.make(inBaseOrd.getName() + " (optional)"), null);
		
		if ( getInBaseOrdString().trim().length()>0 ) setInBaseOrd( BOrd.make( BFormat.make(getInBaseOrdString().trim()).format(thisComp).replaceAll("//", "/") ).relativizeToHost() );
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void changed(Property p, Context c)
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		
		if ( p == inBaseOrdString && getInBaseOrdString().trim().length()>0 ) 
		{
			setInBaseOrd( BOrd.make( BFormat.make(getInBaseOrdString().trim()).format(thisComp).replaceAll("//", "/") ).relativizeToHost() );
		}
		else if ( p == inLinkListCsv ) 
		{
			if(getInLinkListCsv().length()<=0)
			{
				// set the link link csv to the default value...
				setInLinkListCsv(DEFAULT_CSV);
			}
			else if(getInLinkListCsv().trim().length() > DEFAULT_CSV.length() && getInLinkListCsv().trim().indexOf(DEFAULT_CSV)>=0 )
			{
				// remove the default value from the link list csv...
				setInLinkListCsv( getInLinkListCsv().trim().replaceAll(Pattern.quote(DEFAULT_CSV), "").trim() );
			}
		}
	}
	
	/** The string used to hold any messages related to the logic processing.*/
	private 		List<String> 	messages 		= new ArrayList<String>();
	
	/** Represents the successfully created links.*/
	private 		List<String> 	linksGood 		= new ArrayList<String>();
	
	/** Represents the failed links.*/
	private 		List<String> 	linksBad 		= new ArrayList<String>();
	
	/** Represents time the process started, then used to calculate the total execution time.*/
	private			double			startTime			= 0;
	
	/** Indicates whether or not an error has occurred somewhere in the process. */
	private			boolean 		hasError 			= false;
	
	/** Indicates whether or not a conversion link is required.<br>
	 * Value is set in the {@link #okToLink(BComponent, String, BComponent, String) okToLink} 
	 * method and used in {@link ThreadedProcessor#run() ThreadedProcessor.run} method. */
	private			boolean			useConversionLink	= false;
	
	/** The BConverter to be used is a conversion link is required.<br>
	 * Value is set in the {@link #okToLink(BComponent, String, BComponent, String) okToLink} 
	 * method and used in {@link ThreadedProcessor#run() ThreadedProcessor.run} method. */
	private			BConverter		converter			= null;
	
	/*-----------------------------------------------------------------------------------------------------*/
	/**
	 * Called from {@link #doDryRunOnly()} and {@link #doExecute()} to kick off the linking process.
	 * @param inCheckOnly when truthy no links are created, only the validation process is performed.
	 */
	private void onExecute(boolean inCheckOnly)
	{
		if(getOutRunning()==false)
		{
			try
			{
				hasError = false;
				startTime	= System.currentTimeMillis();
				setOutRunning(true);
				
				messages.clear();
				linksGood.clear();
				linksBad.clear();
				
				//Reset status bits...
				setOutSuccess(false);
				setOutError(false);
				getOutMessage().setValue("");
				setOutLinksCreated(0);
				setOutLinksFailed(0);
				setOutListLinksCreated(new BStatusString("", BStatus.ok));
				setOutListLinksFailed(new BStatusString("", BStatus.ok));
//				goodLinks	= 0;
//				badLinks	= 0;
				thisComp = this;
				
				if(getInLinkListCsv().trim().length() > 0 && getInLinkListCsv().trim().compareTo(DEFAULT_CSV)!=0 )
				{
					Thread t = new Thread(new ThreadedProcessor(inCheckOnly));
					t.start();
				}
				else
				{
					messages.add("Invalid link list csv.");
					if(inCheckOnly) messages.add(0, "DRY RUN ONLY.");
					getOutMessage().setValue(String.join("\n", messages));
					setOutCountFailed(getOutCountFailed()+1);
					setOutError(true);
					setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
					setOutLastExecuted(BAbsTime.make());
					setOutRunning(false);
					if(!inCheckOnly) fireLinksCreated(BDouble.make(linksGood.size()));
					if(!inCheckOnly) fireLinksFailed(BDouble.make(linksBad.size()));
					if(!inCheckOnly) fireMessage(BString.make(getOutMessage().getValue()));
					if(!inCheckOnly) fireError(BBoolean.make(true));
					return;
				}
			}
			catch(Exception e)
			{
				messages.add(errorHandler(Level.FINEST, e));
				if(inCheckOnly) messages.add(0, "DRY RUN ONLY, NO LINKS CREATED");
				
				getOutMessage().setValue(String.join("\n", messages));
				setOutCountFailed(getOutCountFailed()+1);
				setOutError(true);
				setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
				setOutLastExecuted(BAbsTime.make());
				setOutRunning(false);
				if(!inCheckOnly) fireLinksCreated(BDouble.make(linksGood.size()));
				if(!inCheckOnly) fireMessage(BString.make(getOutMessage().getValue()));
				if(!inCheckOnly) fireError(BBoolean.make(true));
			}
		}
		else
		{
			if(getInDebug())messageHandler("onExecute() method was called but 'outRunning' is already active.");
		}
	}
	
	
	
	/*-----------------------------------------------------------------------------------------------------*/
	/** This is the thread where the actual linking work is being done. */
	class ThreadedProcessor implements Runnable
	{
		private	boolean	checkOnly	= false;
		
		/*-----------------------------------------------------------------------------------------------------*/
		/**
		 * @param inCheckOnly when truthy no links are created, only the validation process is performed.
		 */
		public ThreadedProcessor(boolean inCheckOnly)
		{
			this.checkOnly = inCheckOnly;
		}
		
		/*-----------------------------------------------------------------------------------------------------*/
		/** This is where the actual linking work is being done. */
		public void run()
		{
			if(getInDebug())messageHandler("Executer.Run() method called.");
			String	tmpMsg	= "";
			
			try
			{
				startTime	= System.currentTimeMillis();
				setOutRunning(true);
				
				//Reset status bits...
				setOutSuccess(false);
				setOutError(false);
				getOutMessage().setValue("");
				setOutLinksCreated(0);
				setOutLinksFailed(0);
				setOutListLinksCreated(new BStatusString("", BStatus.ok));
				setOutListLinksFailed(new BStatusString("", BStatus.ok));
//				goodLinks	= 0;
//				badLinks	= 0;

				if ( getInBaseOrdString().trim().length()>0 ) setInBaseOrd( BOrd.make( BFormat.make(getInBaseOrdString().trim()).format(thisComp).replaceAll("//", "/") ).relativizeToHost() );
				
				String			ordBase		= BOrd.make( BFormat.make(getInBaseOrd().toString()).format(thisComp).replaceAll("//", "/") ).relativizeToHost().toString();
				String 			linkListCsv = BFormat.make(getInLinkListCsv().trim().replaceAll(DEFAULT_CSV, "").trim()).format(thisComp).replaceAll("//", "/");

				if(getInDebug()) messageHandler("ordBase:     '"+ordBase+"'");
				if(getInDebug()) messageHandler("linkListCsv: '"+linkListCsv+"'");
				
				
				// Change the case of any occurrences of 'BASE_ORD' to upper case, then split on new lines and add to array...
				String[]		arrLinkList	= linkListCsv.split("\n");
				List<String>	linkList	= Arrays.asList(arrLinkList);

				String 			logString 	= "";
				
				// Process each line of the link list csv...
				for (int l = 0; l < linkList.size(); l++) 
				{
					// if 'outRunning' somehow got set to false then don't process any more records...
					if(getOutRunning()==false) {break;}
					
					if(getInDebug()) messageHandler("link " + (l+1) + " of " + linkList.size() + ": '" + linkList.get(l).trim() + "'");
					
					try
					{
						// If the line item matches the default value, then skip...
						if(linkList.get(l).trim().compareTo(DEFAULT_CSV)==0){continue;}
						
						// Remove any spaces after the commas...
						String		fixedComma		= linkList.get(l).trim().replaceAll(",\\s*", ",");
						
						// Replace any occurrences of '%ORD_BASE%' with the value from 'inBaseOrd' if defined, else use the value from 'fixedComma'...
						String		fixedOrdBase	= ordBase.length()>0? fixedComma.replaceAll(Pattern.quote("%ORD_BASE%"), ordBase) : fixedComma;
						
						String[]	linkParts		= fixedOrdBase.split(",");
						
						if(linkParts.length == 4)
						{
							String		strSourceOrd	= linkParts[0].trim();
							String		strSourceSlot	= linkParts[1].trim();
							String		strTargetOrd	= linkParts[2].trim();
							String		strTargetSlot	= linkParts[3].trim();
							
							BOrd		sourceOrd		= BOrd.make( BFormat.make(strSourceOrd).format(thisComp).replaceAll("//", "/") ).relativizeToHost();
							BOrd		targetOrd		= BOrd.make( BFormat.make(strTargetOrd).format(thisComp).replaceAll("//", "/") ).relativizeToHost();
							
//							if(getInDebug())messageHandler((l+1) + " of " + linkList.size() + ", fixedOrdBase: '" + fixedOrdBase + "'");
							if(getInDebug())messageHandler(".......sourceOrd:    '" + sourceOrd.toString() + "'");
							if(getInDebug())messageHandler(".......sourceSlot:   '" + strSourceSlot + "'");
							if(getInDebug())messageHandler(".......targetOrd:    '" + targetOrd.toString() + "'");
							if(getInDebug())messageHandler(".......targetSlot:   '" + strTargetSlot + "'");
							
							
							boolean		sourceOrdExists	= isOrdValid(sourceOrd);
							boolean		targetOrdExists	= isOrdValid(targetOrd);
							
							logString 					= linkLogString(sourceOrd.toString(), strSourceSlot, targetOrd.toString(), strTargetSlot);
							
							if( sourceOrdExists && targetOrdExists )
							{
								BComponent	sourceComp	= (BComponent)sourceOrd.relativizeToHost().get();
								BComponent	targetComp	= (BComponent)targetOrd.relativizeToHost().get();
								
								boolean		sourceSlotExists	= doesSlotExist(sourceComp, strSourceSlot);
								boolean		targetSlotExists	= doesSlotExist(targetComp, strTargetSlot);
								
								
								if( sourceSlotExists && targetSlotExists )
								{
									Slot 	sourceSlot 	= sourceComp.getSlot(strSourceSlot);
									Slot 	targetSlot 	= targetComp.getSlot(strTargetSlot);
									
									Type	sourceType 	= determineSlotType(sourceSlot);
									Type	targetType 	= determineSlotType(targetSlot);
									
									if( okToLink(sourceComp, strSourceSlot, targetComp, strTargetSlot) )
									{
										if( useConversionLink==false )
										{
											BLink link = new BLink(sourceComp.getHandleOrd(),strSourceSlot,strTargetSlot,true);
											if(getInDebug())messageHandler((l+1) + " of " + linkList.size() + ", All link checks passed!");
											if(!checkOnly){ targetComp.add(null, link); }
											linksGood.add(logString);
										}
										else if( useConversionLink && converter!=null )
										{
											BConversionLink cLink = new BConversionLink(sourceComp.getHandleOrd(),strSourceSlot,strTargetSlot,true,findConverter(sourceType,targetType) );
											if(getInDebug())messageHandler((l+1) + " of " + linkList.size() + ", All link checks passed!");
											if(!checkOnly){ targetComp.add(null, cLink); }
											linksGood.add(logString);
										}
										else
										{
											tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Could not determine the converter from type '" + sourceType + "', to type '" + targetType + "', link was not created.";
											if(getInDebug())messageHandler(tmpMsg);
											messages.add(tmpMsg);
											linksBad.add(logString);
										}
									}
									else
									{
										//Not ok to link, the message as to why should have already been displayed.
										hasError = true;
										messageHandler("SET 'hasError' = TRUE");
										linksBad.add(logString);
									}
								}
								else
								{
									if( !sourceSlotExists )
									{
										tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Invalid source slot: '" + strSourceOrd + "." + strSourceSlot + "'";
										if(getInDebug())messageHandler(tmpMsg);
										messages.add(tmpMsg);
									}
									
									if( !targetSlotExists )
									{
										tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Invalid target slot: '" + strTargetOrd + "." + strTargetSlot + "'" ;
										if(getInDebug())messageHandler(tmpMsg);
										messages.add(tmpMsg);
									}
									linksBad.add(logString);
								}
							}
							else
							{
								if( !sourceOrdExists )
								{
									tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Invalid source ord: '" + sourceOrd.toString() + "'";
									if(getInDebug())messageHandler(tmpMsg);
									messages.add(tmpMsg);
								}
								
								if( !targetOrdExists )
								{
									tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Invalid target ord: '" + strTargetOrd + "'";
									if(getInDebug())messageHandler(tmpMsg);
									messages.add(tmpMsg);
								}
								
								hasError = true;
								messageHandler("SET 'hasError' = TRUE");
								linksBad.add(logString);
							}
							
						}
						else
						{
							tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: INVALID LINK CSV: fixedOrdBase: '" + fixedOrdBase + "'";
							if(getInDebug())messageHandler(tmpMsg);
							messages.add(tmpMsg);
							hasError = true;
							messageHandler("SET 'hasError' = TRUE");
							linksBad.add(logString);
						}
					}
					catch (Exception e)
					{
						messages.add(errorHandler(Level.FINEST, (l+1) + " of " + linkList.size(), e ));
						hasError = true;
						messageHandler("SET 'hasError' = TRUE");
						linksBad.add(logString);
					}
				}
				// END OF FOR LOOP
				
				
				//  All done, set success if no errors occurred..................................................
				if(hasError==false)
				{
					
					messages.add(0, "SUCCESS FOR ALL LINKS!");
					if(checkOnly) messages.add(0, "DRY RUN ONLY, NO LINKS CREATED");
					
					getOutMessage().setValue(String.join("\n", messages));
					
					setOutLinksCreated(linksGood.size());
					setOutLinksFailed(linksBad.size());
					setOutListLinksCreated(new BStatusString(String.join("\n", linksGood), BStatus.ok));
					setOutListLinksFailed(new BStatusString(String.join("\n", linksBad), BStatus.ok));
					setOutCountSuccess(getOutCountSuccess()+1);
					setOutSuccess(true);
					setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
					setOutLastExecuted(BAbsTime.make());
					setOutRunning(false);
					if(!checkOnly) fireLinksCreated(BDouble.make(linksGood.size()));
					if(!checkOnly) fireLinksFailed(BDouble.make(linksBad.size()));
					if(!checkOnly) fireMessage(BString.make(getOutMessage().getValue()));
					if(!checkOnly) fireSuccess(BBoolean.make(true));
				}
				else
				{
					if(checkOnly) messages.add(0, "DRY RUN ONLY, NO LINKS CREATED");
					
					getOutMessage().setValue(String.join("\n", messages));
					
					setOutLinksCreated(linksGood.size());
					setOutLinksFailed(linksBad.size());
					setOutListLinksCreated(new BStatusString(String.join("\n", linksGood), BStatus.ok));
					setOutListLinksFailed(new BStatusString(String.join("\n", linksBad), BStatus.ok));
					setOutCountFailed(getOutCountFailed()+1);
					setOutError(true);
					setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
					setOutLastExecuted(BAbsTime.make());
					setOutRunning(false);
					if(!checkOnly) fireLinksCreated(BDouble.make(linksGood.size()));
					if(!checkOnly) fireLinksFailed(BDouble.make(linksBad.size()));
					if(!checkOnly) if(getOutMessage().getValue().trim().length()>0){fireMessage(BString.make(getOutMessage().getValue().trim()));}else{fireMessage(BString.make("ERROR! " + linksBad.size() + " components failed."));}
					if(!checkOnly) fireError(BBoolean.make(true));
				}
				
			}
			catch (Exception e)
			{
				messages.add(errorHandler(Level.FINEST, e));
				if(checkOnly) messages.add(0, "DRY RUN ONLY, NO LINKS CREATED");
				
				getOutMessage().setValue(String.join("\n", messages));
				
				setOutLinksCreated(0);
				setOutLinksFailed(0);
				setOutListLinksCreated(new BStatusString(String.join("\n", linksGood), BStatus.ok));
				setOutListLinksFailed(new BStatusString(String.join("\n", linksBad), BStatus.ok));
				setOutCountFailed(getOutCountFailed()+1);
				setOutError(true);
				setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
				setOutLastExecuted(BAbsTime.make());
				setOutRunning(false);
				if(!checkOnly) fireLinksCreated(BDouble.make(linksGood.size()));
				if(!checkOnly) fireLinksFailed(BDouble.make(linksBad.size()));
				if(!checkOnly) if(getOutMessage().getValue().trim().length()>0){fireMessage(BString.make(getOutMessage().getValue().trim()));}else{fireMessage(BString.make("ERROR!"));}
				if(!checkOnly) fireError(BBoolean.make(true));
				return;
			}
			finally
			{
				setOutRunning(false);
			}
		}
		
		/*----------------------------------------------------------------------------------------------------------------*/
		private String linkLogString(String sourceOrdString, String sourceSlotName, String targetOrdString, String targetSlotName)
		{
			String	logStr		= "";
			
			try
			{
				String	base		= BOrd.make(BFormat.make(getInBaseOrd().toString()).format(thisComp).replaceAll("//", "/")).relativizeToHost().toString();
				String	sourcePath	= sourceOrdString.replaceAll(Pattern.quote(base), ".");
				String	targetPath	= targetOrdString.replaceAll(Pattern.quote(base), ".");
				logStr = "SOURCE: '" + sourcePath + "/" + sourceSlotName + "', TARGET: '" + targetPath + "/" + targetSlotName + "'";
			}
			catch (Exception e)
			{
				errorHandler(Level.FINEST, e);
				logStr = UUID.randomUUID().toString();
			}
			
			messageHandler("method returning '"+logStr+"'");
			return logStr;
		}
		
		
		/*----------------------------------------------------------------------------------------------------------------*/
		/**
		 * Indicates whether or not it is ok to add a link between two slots on the provided component(s).<br>
		 * 
		 * @param sourceComp
		 * @param sourceSlotName
		 * @param targetComp
		 * @param targetSlotName
		 * @return
		 */
		private boolean okToLink(BComponent sourceComp, String sourceSlotName, BComponent targetComp, String targetSlotName)
		{
			boolean	result		= true;
			String	tmpMsg		= "";
			String	logString	= linkLogString(sourceComp.getNavOrd().relativizeToHost().toString(), sourceSlotName, targetComp.getNavOrd().relativizeToHost().toString(), targetSlotName);

			/*
			NOTES ON WHAT TYPE OF LINKS ARE ALLOWED:
			action with null type		to	action with param type	= NOT ALLOWED
			action with null type		to	normal slot				= NOT ALLOWED
			action with null type		to	topic with event type	= NOT ALLOWED
			action with param type		to	normal slot				= NOT ALLOWED
			normal slot					to	topic with event type	= NOT ALLOWED
			topic with event type		to	normal slot				= NOT ALLOWED
			
			action with param type		to	topic with event type	= conversion link if diff, link if same
			normal slot					to	action with param type	= conversion link if diff, link if same
			topic with event type		to	action with param type	= conversion link if diff, link if same
			
			action with null type		to	action with null type	= link
			action with param type		to	action with null type	= link
			normal slot					to	action with null type	= link
			topic with event type		to	action with null type	= link
			*/
			
			try
			{
				Slot	sourceSlot				= sourceComp.getSlot(sourceSlotName);
				Slot	targetSlot				= targetComp.getSlot(targetSlotName);
				
				boolean	sourceIsAction 			= false;
				boolean	sourceIsTopic 			= false;
				boolean	targetIsAction 			= false;
				boolean	targetIsTopic 			= false;
				
				try{sourceIsAction 				= sourceSlot.isAction();}catch(Exception e) {}
				try{sourceIsTopic 				= sourceSlot.isTopic();}catch(Exception e) {}
				try{targetIsAction 				= targetSlot.isAction();}catch(Exception e) {}
				try{targetIsTopic 				= targetSlot.isTopic();}catch(Exception e) {}
				
				boolean	sourceIsActionOrTopic	= sourceIsAction || sourceIsTopic;
				boolean	targetIsActionOrTopic	= targetIsAction || targetIsTopic;
				
				boolean	sourceIsNormal			= !sourceIsActionOrTopic;
				boolean	targetIsNormal			= !targetIsActionOrTopic;
				
				Type	sourceType 				= determineSlotType(sourceSlot);
				Type	targetType 				= determineSlotType(targetSlot);
				
				boolean	linkAlreadyExists		= linkAlreadyExists(sourceComp, sourceSlotName, targetComp, targetSlotName);
				boolean	targetHasLinks			= (targetComp.getLinks(targetSlot).length > 0);
				
				boolean	targetIsFanIn			= ((targetComp.getFlags(targetSlot) & Flags.FAN_IN ) == Flags.FAN_IN  );
				boolean	targetIsReadOnly		= ((targetComp.getFlags(targetSlot) & Flags.READONLY ) == Flags.READONLY  );
				
				if( linkAlreadyExists )
				{
					tmpMsg = "ERROR: Link already exists, " + logString + "";
					if(getInDebug())messageHandler(tmpMsg);
					messages.add(tmpMsg);
					result = false;
				}
				else if( targetIsNormal && targetHasLinks && !targetIsFanIn )
				{
					tmpMsg = "ERROR: Target already contains a link, " + logString + "";
					if(getInDebug())messageHandler(tmpMsg);
					messages.add(tmpMsg);
					result = false;
					
				}
				else if( 
						(sourceIsNormal && targetIsTopic) 
						|| (sourceIsAction && sourceType==null && targetIsTopic) 
						|| (sourceIsAction && sourceType==null && targetIsAction && targetType!=null) 
						|| (sourceIsActionOrTopic && targetIsNormal) 
						|| (targetIsReadOnly)
						)
				{
					tmpMsg = "ERROR: Link combination NOT ALLOWED!, " + logString + "";
					if(getInDebug())messageHandler(tmpMsg);
					messages.add(tmpMsg);
					result = false;
				}
				else if( targetIsAction && targetType==null)
				{
					//Ok to add normal link...
					useConversionLink	= false;
					result 				= true;
				}
				else
				{
					if(sourceType==null || targetType==null)
					{
						//Ok to add normal link...
						useConversionLink	= false;
						result 				= true;
					}
					else
					{
						if(sourceType.is(targetType))
						{
							//Ok to add normal link...
							useConversionLink	= false;
							result 				= true;
						}
						else
						{
							converter = findConverter(sourceType,targetType);
							
							if( !converter.isNull() )
							{
								//Ok to add CONVERSION link...
								useConversionLink	= true;
								result 				= true;
							}
							else
							{
								tmpMsg = "ERROR: Could not determine the link converter, source type: '" + sourceType + "', target type: '" + sourceType + "', " + logString + "";
								if(getInDebug())messageHandler(tmpMsg);
								messages.add(tmpMsg);
								result = false;
							}
						}
					}
				}
			}
			catch(Exception e)
			{
				messages.add(errorHandler(Level.FINEST, e));
				result = false;
			}
			
			return result;
		}
		
		/*----------------------------------------------------------------------------------------------------------------------------------------*/
		/**
		 * Determines if a link already exists between the supplied components and slots.
		 * 
		 * @param sourceComp
		 * @param sourceSlotName
		 * @param targetComp
		 * @param targetSlotName
		 * @return
		 */
		private boolean linkAlreadyExists(BComponent sourceComp, String sourceSlotName, BComponent targetComp, String targetSlotName)
		{
			boolean exists = false;
			
			try
			{
				Slot	targetSlot	= targetComp.getSlot(targetSlotName);
				BLink[]	targetLinks	= targetComp.getLinks(targetSlot);
				
				if ( targetLinks.length > 0 )
				{
					int		handleExisting			= getInHowToHandleExistingLinksOnTarget().getOrdinal();
					boolean	targetIsFanIn			= ((targetComp.getFlags(targetSlot) & Flags.FAN_IN ) == Flags.FAN_IN  );
					boolean	targetIsAction 			= false;
					try{targetIsAction 				= targetSlot.isAction();}catch(Exception e) {}
					
					for (int i = 0; i < targetLinks.length; i++)
					{
						try
						{
							// if 'outRunning' somehow got set to false then don't process any more records...
							if(getOutRunning()==false) {return true;}
							
							if(targetLinks[i].getSourceSlotName().equals(sourceSlotName) && targetLinks[i].getSourceComponent().equals(sourceComp)  )
							{
								if(handleExisting==1 || handleExisting==2)
								{
									try{ if(!checkOnly) {targetComp.remove(targetLinks[i].getName()); if(getInDebug())messageHandler("removed link: '"+targetLinks[i].toString()+"'");}}
									catch(Exception e) {errorHandler( Level.FINE, "ERROR in linkAlreadyExists() trying to remove link.", e ); exists = true;}
								}
								else
								{
									exists = true;
								}
							}
							else
							{
								if(targetIsAction || targetIsFanIn)
								{
									if(handleExisting==2 || handleExisting==4)
									{
										try{ if(!checkOnly) {targetComp.remove(targetLinks[i].getName()); if(getInDebug())messageHandler("removed link: '"+targetLinks[i].toString()+"'");}}
										catch(Exception e) {errorHandler( Level.FINE, "ERROR in linkAlreadyExists() trying to remove link.", e );}
									}
								}
								else
								{
									if(handleExisting==1 || handleExisting==3   ||   handleExisting==2 || handleExisting==4)
									{
										try{ if(!checkOnly) {targetComp.remove(targetLinks[i].getName()); if(getInDebug())messageHandler("removed link: '"+targetLinks[i].toString()+"'");}}
										catch(Exception e) {errorHandler( Level.FINE, "ERROR in linkAlreadyExists() trying to remove link.", e );}
									}
								}
							}
						}
						catch (Exception e) {}
					}
				}
			}
			catch (Exception e)
			{
				messages.add(errorHandler(Level.FINEST, e));
			}
			
			return exists;
		}
		
		/*----------------------------------------------------------------------------------------------------------------------------------------*/
		/**
		 * Determines what {@link javax.baja.sys.Type Type} a given {@link javax.baja.sys.Slot Slot} is.
		 * @param slot
		 * @return {@link javax.baja.sys.Type Type}
		 */
		private Type determineSlotType(Slot slot)
		{
			Type type = null;
			
			try
			{
				boolean slotIsActionOrTopic = slot.isAction() || slot.isTopic();
				
				if( slotIsActionOrTopic)
				{
					try
					{
						if(slot.isAction())		{ type = slot.asAction().getParameterType();	}
						else if(slot.isTopic())	{ type = slot.asTopic().getEventType();		}
					}
					catch(Exception e)
					{
						errorHandler( Level.FINE, "ERROR, coult not determine slot type in method determineSlotType().", e );
					}
				}
				else
				{
					type = slot.asProperty().getType();
				}
			}
			catch(Exception e)
			{
				errorHandler( Level.FINE, "ERROR, coult not determine slot type in method determineSlotType().", e );
			}
			
			return type;
		}
		
		/*----------------------------------------------------------------------------------------------------------------------------------------*/
		/**
		 * Returns a {@link javax.baja.util.BConverter BConverter} to be used when adding a conversion link between two slots.
		 * 
		 * @param typeFrom
		 * @param typeTo
		 * @return {@link javax.baja.util.BConverter BConverter}
		 */
		private BConverter findConverter(Type typeFrom, Type typeTo)
		{
			BConverter converter = null;
			try
			{
				Registry	registry	= Sys.getRegistry();
				TypeInfo[]	adapters	= registry.getAdapters(typeFrom.getTypeInfo(), typeTo.getTypeInfo());
				
				for (int i = adapters.length - 1; i >= 0; i--)
				{
					try
					{
						// if 'outRunning' somehow got set to false then don't process any more records...
						if(getOutRunning()==false) {break;}
						
						if ( registry.isAgent( adapters[i], BConversionLink.TYPE.getTypeInfo() ) )
						{
							converter = (BConverter) adapters[i].getInstance();
							return converter;
						}
					}
					catch (Exception e){}
				}
			}
			catch (Exception e){}
			
			return converter;
		}
		
		/*----------------------------------------------------------------------------------------------------------------------------------------*/
		/**
		 * Determines if a supplied ord is valid by <br>
		 * attempting to cast the ord into a BComponent.<br> 
		 * If the cast throws an exception then <br>
		 * it is determined to be invalid.
		 * 
		 * @param ord
		 * @return Returns 'true' if ord is found to be valid.
		 */
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
				try
				{
					//try to create the component - if it fails, false
					BComponent com = (BComponent)ord.get().asComponent();
					//This gets rid of the "unused variable" warning Eclipse gives me
					com = (BComponent)com;
					return true;
				}
				catch(Exception e2)
				{
					return false;
				}
			}
		}
		
		/*----------------------------------------------------------------------------------------------------------------------------------------*/
		/**
		 * Determines if a slot exists on a given component.
		 * 
		 * @param comp - BComponent to look for given slot.
		 * @param slotName - String name of slot to find.
		 * @return boolean.
		 */
		private boolean doesSlotExist(BComponent comp, String slotName)
		{
			boolean result = false;
			
			try
			{
				Slot slot = comp.getSlot(slotName);
				if(slot.getDeclaringType().getDisplayName(null).length() > 0) { /* do nothing, this just here to prevent compile warning */}
				result = true;
			}
			catch(Exception e)
			{
				result = false;
			}
			
			return result;
		}
	}
	// END OF THREAD CLASS
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public String ORD_BASE()
	{
		return  BaseOrd();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public String OrdBase()
	{
		return  BaseOrd();
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public String BaseOrd()
	{
		try
		{
			return  BOrd.make( BFormat.make(getInBaseOrd().toString()).format(thisComp).replaceAll("//", "/") ).relativizeToHost().toString();
		}
		catch (Exception e){return "";}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	public String wcRootPath()
	{
		try{return wcRoot().getSlotPath().toString();}
		catch (Exception e){return "";}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public String wcRootNavOrd()
	{
		try{return wcRoot().getNavOrd().toString();}
		catch (Exception e){return "";}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public BComponent wcRoot()
	{
		BComponent result = null;
		
		try
		{
			BComponent  comp   = (BComponent) this.getParent().getParentComponent();
			
			boolean found = false;
			while( !comp.getType().toString().equalsIgnoreCase("baja:Station") && !found )
			{
				if( comp.getType().toString().equalsIgnoreCase("korsComponentManager:WorkcenterFolder"))
				{
					found = true;
				}
				else
				{
					comp   = (BComponent) comp.getParent().getParentComponent();
				}
			}
			
			if(found){result = comp;}
		}
		catch (Exception e)
		{
		}
		
		return result;
	}
	
	static String escape(String s){return com.tridium.util.EscUtil.slot.escape(s);}
	static String unescape(String s){return com.tridium.util.EscUtil.slot.unescape(s);}
	
	
	/*------------------------------------------------------------------------------------------------------------------*/
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BBatchLinkCreator.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");

	public static final Logger logger = Logger.getLogger(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
}



