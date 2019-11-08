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
public class BBatchLinkCreator extends BComponent
{
	/** This is the default value for slot {@link #inLinkListCsv}.*/
	private static	String			DEFAULT_CSV			= "%ORD_BASE%/sourceOrd, sourceSlot, %ORD_BASE%/targetOrd, targetSlot";
	
	/** Represents this component and is used when processing a BFormat in the thread later in this logic.*/
	private	BComponent	thisComp = this;
	
	/** When true additional info will be send to the application director console.*/
	public static final Property inDebug = newProperty(0, false);
	public boolean getInDebug() { return getBoolean(inDebug); }
	public void setInDebug(boolean v) { setBoolean(inDebug, v, null); }
	
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
	public static final Action ClearOutpus = newAction(0, null);
	public void ClearOutpus(){if(!Sys.atSteadyState()||!isRunning()){return;} invoke(ClearOutpus,null,null);}
	public void doClearOutpus(Context cx)
	{
		if(getOutRunning()==false)
		{
			msg					= "";
			goodLinks			= 0;
			badLinks			= 0;
			startTime			= 0;
			hasError 			= false;
			useConversionLink	= false;
			converter			= null;
			
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
				,escape("Remove if exists or any other links on target if not an action slot or does not have fanin facet.")
				,escape("Remove if exists or any other links on target even if is an action slot or has fanin facet.")
				,escape("Don't remove if exists, but do remove any other links on target if not an action slot or does not have fanin facet.")
				,escape("Son't remove if exists, but do remove any other links on target even if is an action slot or has fanin facet.")
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
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	public void changed(Property p, Context c)
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		
		if ( p == inLinkListCsv ) 
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
	private 		String			msg					= "";
	
	/** Represents the number of links successfully created.*/
	private			int				goodLinks			= 0;
	
	/** Represents the number of links that failed.*/
	private			int				badLinks			= 0;
	
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
				startTime	= System.currentTimeMillis();
				setOutRunning(true);
				
				//Reset status bits...
				setOutSuccess(false);
				setOutError(false);
				getOutMessage().setValue("");
				setOutLinksCreated(0);
				setOutLinksFailed(0);
				goodLinks	= 0;
				badLinks	= 0;
				msg = "";
				thisComp = this;
				
				if(getInLinkListCsv().trim().length() > 0 && getInLinkListCsv().trim().compareTo(DEFAULT_CSV)!=0 )
				{
					Thread t = new Thread(new ThreadedProcessor(inCheckOnly));
					t.start();
				}
				else
				{
					msg = "Invalid link list csv.";
					if(inCheckOnly) msg = "DRY RUN ONLY.\n" + msg;
					getOutMessage().setValue(msg);
					setOutCountFailed(getOutCountFailed()+1);
					setOutError(true);
					setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
					setOutLastExecuted(BAbsTime.make());
					setOutRunning(false);
					if(!inCheckOnly) fireLinksCreated(BDouble.make(goodLinks));
					if(!inCheckOnly) fireLinksFailed(BDouble.make(badLinks));
					if(!inCheckOnly) fireMessage(BString.make(msg));
					if(!inCheckOnly) fireError(BBoolean.make(true));
					return;
				}
			}
			catch(Exception e)
			{
				msg	= errorHandler("doExecute(), Exception", e);
				if(inCheckOnly) msg = "DRY RUN ONLY, NO LINKS CREATED.\n" + msg;
				
				getOutMessage().setValue(msg);
				setOutCountFailed(getOutCountFailed()+1);
				setOutError(true);
				setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
				setOutLastExecuted(BAbsTime.make());
				setOutRunning(false);
				if(!inCheckOnly) fireLinksCreated(BDouble.make(goodLinks));
				if(!inCheckOnly) fireMessage(BString.make(msg));
				if(!inCheckOnly) fireError(BBoolean.make(true));
			}
		}
		else
		{
			if(getInDebug())System.out.println("onExecute() method was called but 'outRunning' is already active.");
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
			if(getInDebug())System.out.println("Executer.Run() method called.");
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
				goodLinks	= 0;
				badLinks	= 0;
				msg = "";

				String			ordBase		= getInBaseOrd().toString();
				
				// Change the case of any occurrences of 'BASE_ORD' to upper case, then split on new lines and add to array...
				String[]		arrLinkList	= getInLinkListCsv().trim().replaceAll("(?i)%BASE_ORD%", "%BASE_ORD%").split("\n");
				List			linkList	= Arrays.asList(arrLinkList);

				// Process each line of the link list csv...
				for (int l = 0; l < linkList.size(); l++) 
				{
					// if 'outRunning' somehow got set to false then don't process any more records...
					if(getOutRunning()==false) {break;}
					
					try
					{
						// If the line item matches the default value, then skip...
						if( ((String)linkList.get(l)).trim().compareTo(DEFAULT_CSV)==0){continue;}
						
						// Remove any spaces after the commas...
						String		fixedComma		= ((String)linkList.get(l)).trim().replaceAll(",\\s*", ",");
						
						// Replace any occurrences of '%ORD_BASE%' with the value from 'inBaseOrd' if defined, else use the value from 'fixedComma'...
						String		fixedOrdBase	= ordBase.length()>0? fixedComma.replaceAll(Pattern.quote("%ORD_BASE%"), ordBase) : fixedComma;
						
						String[]	linkParts		= fixedOrdBase.split(",");
						
						if(linkParts.length == 4)
						{
							String		strSourceOrd	= linkParts[0].trim();
							String		strSourceSlot	= linkParts[1].trim();
							String		strTargetOrd	= linkParts[2].trim();
							String		strTargetSlot	= linkParts[3].trim();
							
							BOrd		sourceOrd		= BOrd.make( BFormat.make(strSourceOrd).format(thisComp).replaceAll("//", "/") );
							BOrd		targetOrd		= BOrd.make( BFormat.make(strTargetOrd).format(thisComp).replaceAll("//", "/") );
							
							if(getInDebug())System.out.println((l+1) + " of " + linkList.size() + ", fixedOrdBase: '" + fixedOrdBase + "'");
							if(getInDebug())System.out.println(".......sourceOrd:    '" + sourceOrd.toString() + "'");
							if(getInDebug())System.out.println(".......targetOrd:    '" + targetOrd.toString() + "'");
							
							
							boolean		sourceOrdExists	= isOrdValid(sourceOrd);
							boolean		targetOrdExists	= isOrdValid(targetOrd);
							
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
											if(getInDebug())System.out.println((l+1) + " of " + linkList.size() + ", All link checks passed!");
											if(!checkOnly){ targetComp.add(null, link); }
											goodLinks++;
										}
										else if( useConversionLink && converter!=null )
										{
											BConversionLink cLink = new BConversionLink(sourceComp.getHandleOrd(),strSourceSlot,strTargetSlot,true,findConverter(sourceType,targetType) );
											if(getInDebug())System.out.println((l+1) + " of " + linkList.size() + ", All link checks passed!");
											if(!checkOnly){ targetComp.add(null, cLink); }
											goodLinks++;
										}
										else
										{
											tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Could not determine the converter from type '" + sourceType + "', to type '" + targetType + "', link was not created.";
											if(getInDebug())System.out.println(tmpMsg);
											msg	= msg.trim() + "\n" + tmpMsg;
											badLinks++;
										}
									}
									else
									{
										//Not ok to link, the message as to why should have already been displayed.
										hasError = true;
										badLinks++;
									}
								}
								else
								{
									if( !sourceSlotExists )
									{
										tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Invalid source slot: '" + strSourceOrd + "." + strSourceSlot + "'";
										if(getInDebug())System.out.println(tmpMsg);
										msg	= msg.trim() + "\n" + tmpMsg;
									}
									
									if( !targetSlotExists )
									{
										tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Invalid target slot: '" + strTargetOrd + "." + strTargetSlot + "'" ;
										if(getInDebug())System.out.println(tmpMsg);
										msg	= msg.trim() + "\n" + tmpMsg;
									}
									badLinks++;
								}
							}
							else
							{
								if( !sourceOrdExists )
								{
									tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Invalid source ord: '" + sourceOrd.toString() + "'";
									if(getInDebug())System.out.println(tmpMsg);
									msg	= msg.trim() + "\n" + tmpMsg;
								}
								
								if( !targetOrdExists )
								{
									tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: Invalid target ord: '" + strTargetOrd + "'";
									if(getInDebug())System.out.println(tmpMsg);
									msg	= msg.trim() + "\n" + tmpMsg;
								}
								
								hasError = true;
								badLinks++;
							}
							
						}
						else
						{
							tmpMsg = (l+1) + " of " + linkList.size() + ", ERROR: INVALID LINK CSV: fixedOrdBase: '" + fixedOrdBase + "'";
							if(getInDebug())System.out.println(tmpMsg);
							msg	= msg.trim() + "\n" + tmpMsg;
							hasError = true;
							badLinks++;
						}
					}
					catch (Exception e)
					{
						msg	= msg.trim() + "\n\n" + errorHandler( (l+1) + "  of " + linkList.size()+", ERROR in ThreadedProcessor.run() for loop.", e ) + "\n";
						hasError = true;
						badLinks++;
					}
				}
				// END OF FOR LOOP
				
				
				//  All done, set success if no errors occurred..................................................
				if(hasError==false)
				{
					msg = "SUCCESS FOR ALL LINKS!" + "\n" + msg;
					if(checkOnly) msg = "DRY RUN ONLY, NO LINKS CREATED.\n" + msg;
					
					setOutLinksCreated(goodLinks);
					setOutLinksFailed(badLinks);
					getOutMessage().setValue(msg.trim());
					setOutCountSuccess(getOutCountSuccess()+1);
					setOutSuccess(true);
					setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
					setOutLastExecuted(BAbsTime.make());
					setOutRunning(false);
					if(!checkOnly) fireLinksCreated(BDouble.make(goodLinks));
					if(!checkOnly) fireLinksFailed(BDouble.make(badLinks));
					if(!checkOnly) fireMessage(BString.make(msg.trim()));
					if(!checkOnly) fireSuccess(BBoolean.make(true));
				}
				else
				{
					if(checkOnly) msg = "DRY RUN ONLY, NO LINKS CREATED.\n" + msg;
					
					setOutLinksCreated(goodLinks);
					setOutLinksFailed(badLinks);
					getOutMessage().setValue(msg.trim());
					setOutCountFailed(getOutCountFailed()+1);
					setOutError(true);
					setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
					setOutLastExecuted(BAbsTime.make());
					setOutRunning(false);
					if(!checkOnly) fireLinksCreated(BDouble.make(goodLinks));
					if(!checkOnly) fireLinksFailed(BDouble.make(badLinks));
					if(!checkOnly) if(msg.trim().length()>0){fireMessage(BString.make(msg.trim()));}else{fireMessage(BString.make("ERROR! " + badLinks + " components failed."));}
					if(!checkOnly) fireError(BBoolean.make(true));
				}
				
			}
			catch (Exception e)
			{
				tmpMsg = "\n\n" + errorHandler( Level.FINE, "ERROR before getting to the 'while' loop in method changeFacetsForOrd().", e ) + "\n";
				msg	= msg.trim() + "\n" + tmpMsg;
				if(checkOnly) msg = "DRY RUN ONLY, NO LINKS CREATED.\n" + msg;
				
				setOutLinksCreated(0);
				setOutLinksFailed(0);
				getOutMessage().setValue(msg.trim());
				setOutCountFailed(getOutCountFailed()+1);
				setOutError(true);
				setOutExecutionTime(BRelTime.make((int)(System.currentTimeMillis()-startTime)).abs());
				setOutLastExecuted(BAbsTime.make());
				setOutRunning(false);
				if(!checkOnly) fireLinksCreated(BDouble.make(goodLinks));
				if(!checkOnly) fireLinksFailed(BDouble.make(badLinks));
				if(!checkOnly) if(msg.trim().length()>0){fireMessage(BString.make(msg.trim()));}else{fireMessage(BString.make("ERROR!"));}
				if(!checkOnly) fireError(BBoolean.make(true));
				return;
			}
			finally
			{
				setOutRunning(false);
			}
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
			boolean	result	= true;
			String	tmpMsg	= "";

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
					tmpMsg = "ERROR: Link already exists, source: '" + sourceComp.getSlotPath()+"."+sourceSlot.getName() + "', target: '" + targetComp.getSlotPath()+"."+targetSlot.getName() + "'";
					if(getInDebug())System.out.println(tmpMsg);
					msg	= msg.trim() + "\n" + tmpMsg;
					result = false;
				}
				else if( targetIsNormal && targetHasLinks && !targetIsFanIn )
				{
					tmpMsg = "ERROR: Target already contains a link, source: '" + sourceComp.getSlotPath()+"."+sourceSlot.getName() + "', target: '" + targetComp.getSlotPath()+"."+targetSlot.getName() + "'";
					if(getInDebug())System.out.println(tmpMsg);
					msg	= msg.trim() + "\n" + tmpMsg;
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
					tmpMsg = "ERROR: Link combination NOT ALLOWED!, source: '" + sourceComp.getSlotPath()+"."+sourceSlot.getName() + "', target: '" + targetComp.getSlotPath()+"."+targetSlot.getName() + "'";
					if(getInDebug())System.out.println(tmpMsg);
					msg	= msg.trim() + "\n" + tmpMsg;
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
								tmpMsg = "ERROR: Could not determine the link converter, source: '" + sourceComp.getSlotPath()+"."+sourceSlot.getName() + ":" + sourceType + "', target: '" + targetComp.getSlotPath()+"."+targetSlot.getName() + ":" + sourceType + "'";
								if(getInDebug())System.out.println(tmpMsg);
								msg	= msg.trim() + "\n" + tmpMsg;
								result = false;
							}
						}
					}
				}
			}
			catch(Exception e)
			{
				msg	= msg.trim() + "\n\n" + errorHandler( Level.FINE, "ERROR in okToLink().", e ) + "\n";
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
									try{ if(!checkOnly) {targetComp.remove(targetLinks[i].getName()); if(getInDebug())System.out.println("removed link: '"+targetLinks[i].toString()+"'");}}
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
										try{ if(!checkOnly) {targetComp.remove(targetLinks[i].getName()); if(getInDebug())System.out.println("removed link: '"+targetLinks[i].toString()+"'");}}
										catch(Exception e) {errorHandler( Level.FINE, "ERROR in linkAlreadyExists() trying to remove link.", e );}
									}
								}
								else
								{
									if(handleExisting==1 || handleExisting==3   ||   handleExisting==2 || handleExisting==4)
									{
										try{ if(!checkOnly) {targetComp.remove(targetLinks[i].getName()); if(getInDebug())System.out.println("removed link: '"+targetLinks[i].toString()+"'");}}
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
				msg	= msg.trim() + "\n\n" + errorHandler( Level.FINE, "ERROR in linkAlreadyExists().", e ) + "\n";
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
	private String errorHandler(String msg, Exception e)
	{
		return errorHandler(Level.SEVERE, msg, e);
	}
		
	/*----------------------------------------------------------------------------------------------------------------*/
	private String errorHandler(Level level, String msg, Exception e)
	{
		try
		{
			int		MAXLOGLENGTH	= 3583;
			String	MESSAGE			= "";
			String	STACKTRACE		= "";
			String	PRINTSTACKTRACE	= "";
			
			try{MESSAGE		= e.getMessage().trim();}catch(Exception ex) {}
			try{STACKTRACE	= e.getStackTrace().toString().trim();}catch(Exception ex) {}
			try{StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				PRINTSTACKTRACE = errors.toString().trim();
			}catch(Exception ex) {}
			
			msg	= "\n\n" + msg + "\n" + "MESSAGE: \n" + MESSAGE + "\n" + "STACKTRACE: \n" + STACKTRACE + "\n" + "PRINTSTACKTRACE: \n" + PRINTSTACKTRACE;
			msg	= msg.length()>MAXLOGLENGTH? msg.substring(0, MAXLOGLENGTH) : msg;
			
			if(getInDebug()) {System.out.println("\n" + this.getSlotPath() + "\n" + msg);}
			else{logger.log(level, "\n" + this.getSlotPath() + "\n" + msg);}
		}
		catch (Exception e1)
		{
			if(getInDebug()) {System.out.println("\n" + "EXCEPTION ERROR WITH '" + TYPE.getModule().getModuleName() + "." + TYPE.getTypeName() + "'");}
			else{logger.log(level, "\n" + "EXCEPTION ERROR WITH '" + TYPE.getModule().getModuleName() + "." + TYPE.getTypeName() + "'");}
		}
		
		return msg.trim();
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



