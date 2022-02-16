package org.axcommunity.niagara.helperClasses;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.baja.naming.*;
import javax.baja.registry.*;
import javax.baja.sys.*;
import javax.baja.util.*;

/**
 * Contains various methods that can be used to accomplish common tasks.
 *
 * @author Justin Koffler
 * @creation Oct 29, 2019
 */
public class Utilities
{

	public Utilities() {}
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * The purpose of this logic is to clear a slot's value 
	 * when a link is deleted so it doesn't leave behind its old data. 
	 * Should be placed in the {@link javax.baja.sys.BComponent#removed(Property, BValue, Context) removed} method of your component.
	 * @param inComp - BComponent
	 * @param inProp - Property
	 * @param inOldValue - BValue
	 */
	public static void linkRemovedClearValue(BComponent inComp, Property inProp, BValue inOldValue) throws Exception
	{
		try
		{
			if(inProp.getType()==BLink.TYPE || inProp.getType()==BConversionLink.TYPE)
			{
				String slotName = "";
				
				if(inProp.getType()==BConversionLink.TYPE)
				{
					slotName = ((BConversionLink) inOldValue).getTargetSlotName();
				}
				else
				{
					slotName = ((BLink) inOldValue).getTargetSlotName();
				}
				
				Slot slot = inComp.getSlot(slotName);
								
				if( !slot.isAction() && !slot.isTopic() )
				{
					Property	targetProp	= inComp.getProperty(slot.getName());
					inComp.set(targetProp, (BValue) targetProp.getType().getInstance());
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	/*----------------------------------------------------------------------------------------------------------*/
	/**
	 * Checks to see if a given BComponent has any links to the given slot.<br>
	 * Returns TRUE if links exist.
	 * 
	 * @param inComp - BComponent that has a slot you want to check for links.
	 * @param inSlotName - String name of the slot you want to check
	 * @return boolean - TRUE if links exist.
	 * @throws Exception
	 */
	public static boolean hasLinks(BComponent inComp, String inSlotName) throws Exception
	{
		boolean result = false;
		try
		{
			result = (inComp.getLinks(inComp.getSlot(inSlotName)).length > 0);
		}
		catch (Exception e)
		{
			throw e;
		}
		
		return result;
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * Indicates whether or not it is ok to add a link between two slots on the provided component(s).<br>
	 * You may want to also call {@link #findConverter(Type, Type) findConverter} to determine if a conversion link is required.
	 * <p>
	 * <b>NOTES ON WHAT TYPE OF LINKS ARE ALLOWED:</b>
	 * <p>
	 * <table border='1' cellpadding='5' style='border-collapse:collapse; width: 100%; white-space:nowrap;'>
	 * <tr>	<th align="left">SOURCE</th>										<th></th>					<th align="left">TARGET</th>										<th></th>					<th align="left">RESULT</th></tr>
	 * <tr>	<td>{@link javax.baja.sys.Action Action} with null param type</td>	<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Action Action} with param type</td>		<td>&#11; = &#11;</td>		<td>NOT ALLOWED</td></tr>
	 * <tr>	<td>{@link javax.baja.sys.Action Action} with null param type</td>	<td>&#11; to &#11;</td>		<td>normal {@link javax.baja.sys.Slot Slot}</td>					<td>&#11; = &#11;</td>		<td>NOT ALLOWED</td></tr>
	 * <tr>	<td>{@link javax.baja.sys.Action Action} with null param type</td>	<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Topic Topic} with event type</td>			<td>&#11; = &#11;</td>		<td>NOT ALLOWED</td></tr>
	 * <tr>	<td>{@link javax.baja.sys.Action Action} with param type</td>		<td>&#11; to &#11;</td>		<td>normal {@link javax.baja.sys.Slot Slot}</td>					<td>&#11; = &#11;</td>		<td>NOT ALLOWED</td></tr>
	 * <tr>	<td>normal {@link javax.baja.sys.Slot Slot}</td>					<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Topic Topic} with event type</td>			<td>&#11; = &#11;</td>		<td>NOT ALLOWED</td></tr>
	 * <tr>	<td>{@link javax.baja.sys.Topic Topic} with event type</td>			<td>&#11; to &#11;</td>		<td>normal {@link javax.baja.sys.Slot Slot}</td>					<td>&#11; = &#11;</td>		<td>NOT ALLOWED</td></tr>
	 * 
	 * <tr>	<td>{@link javax.baja.sys.Action Action} with param type</td>		<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Topic Topic} with event type</td>			<td>&#11; = &#11;</td>		<td>ALLOWED, use {@link javax.baja.sys.BConversionLink BConversionLink} if diff types.</td></tr>
	 * <tr>	<td>normal {@link javax.baja.sys.Slot Slot}</td>					<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Action Action} with param type</td>		<td>&#11; = &#11;</td>		<td>ALLOWED, use {@link javax.baja.sys.BConversionLink BConversionLink} if diff types.</td></tr>
	 * <tr>	<td>{@link javax.baja.sys.Topic Topic} with event type</td>			<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Action Action} with param type</td>		<td>&#11; = &#11;</td>		<td>ALLOWED, use {@link javax.baja.sys.BConversionLink BConversionLink} if diff types.</td></tr>
	 * 
	 * <tr>	<td>{@link javax.baja.sys.Action Action} with null param type</td>	<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Action Action} with null type</td>		<td>&#11; = &#11;</td>		<td>ALLOWED</td></tr>
	 * <tr>	<td>{@link javax.baja.sys.Action Action} with param type</td>		<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Action Action} with null type</td>		<td>&#11; = &#11;</td>		<td>ALLOWED</td></tr>
	 * <tr>	<td>normal {@link javax.baja.sys.Slot Slot}</td>					<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Action Action} with null type</td>		<td>&#11; = &#11;</td>		<td>ALLOWED</td></tr>
	 * <tr>	<td>{@link javax.baja.sys.Topic Topic} with event type</td>			<td>&#11; to &#11;</td>		<td>{@link javax.baja.sys.Action Action} with null type</td>		<td>&#11; = &#11;</td>		<td>ALLOWED</td></tr>
	 * </table>
	 * 
	 * @param sourceComp
	 * @param sourceSlotName
	 * @param targetComp
	 * @param targetSlotName
	 * @return boolean
	 */
	public boolean okToLink(BComponent sourceComp, String sourceSlotName, BComponent targetComp, String targetSlotName)
	{
		boolean result = true;
		
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
			
			boolean	targetHasLinks			= (targetComp.getLinks(targetSlot).length > 0);
			
			boolean	targetIsFanIn			= ((targetComp.getFlags(targetSlot) & Flags.FAN_IN ) == Flags.FAN_IN  );
			boolean	targetIsReadOnly		= ((targetComp.getFlags(targetSlot) & Flags.READONLY ) == Flags.READONLY  );
			
			if( linkAlreadyExists(sourceComp, sourceSlotName, targetComp, targetSlotName) )
			{
				result = false;
			}
			else if( targetIsNormal && targetHasLinks && !targetIsFanIn )
			{
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
				result = false;
			}
			else if( targetIsAction && targetType==null)
			{
				//Ok to add normal link, no conversion link needed...
				result 				= true;
			}
			else
			{
				if(sourceType==null || targetType==null)
				{
					//Ok to add normal link, no conversion link needed...
					result 				= true;
				}
				else
				{
					if(sourceType.is(targetType))
					{
						//Ok to add normal link, no conversion link needed...
						result 				= true;
					}
					else
					{
						BConverter converter = findConverter(sourceType,targetType);
						
						if( !converter.isNull() )
						{
							//Ok to add normal link, but a conversion link is required...
							result 				= true;
						}
						else
						{
							result = false;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
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
	 * @return boolean
	 * @throws Exception
	 */
	public boolean linkAlreadyExists(BComponent sourceComp, String sourceSlotName, BComponent targetComp, String targetSlotName) throws Exception
	{
		boolean exists = false;
		
		try
		{
			Slot	targetSlot	= targetComp.getSlot(targetSlotName);
			BLink[]	targetLinks	= targetComp.getLinks(targetSlot);
			
			if ( targetLinks.length > 0 )
			{
				for (int i = 0; i < targetLinks.length; i++)
				{
					if(targetLinks[i].getSourceSlotName().equals(sourceSlotName) && targetLinks[i].getSourceComponent().equals(sourceComp)  )
					{
						exists = true;
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			throw e;
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
				catch(Exception e){}
			}
			else
			{
				type = slot.asProperty().getType();
			}
		}
		catch(Exception e){}
		
		return type;
	}
	
	
	
	
	/*----------------------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * Returns a {@link javax.baja.util.BConverter BConverter} to be used when adding a conversion link between two slots.
	 * 
	 * @param typeFrom
	 * @param typeTo
	 * @return {@link javax.baja.util.BConverter BConverter}
	 * @throws Exception
	 */
	public BConverter findConverter(Type typeFrom, Type typeTo) throws Exception
	{
		BConverter converter = null;
		try
		{
			Registry	registry	= Sys.getRegistry();
			TypeInfo[]	adapters	= registry.getAdapters(typeFrom.getTypeInfo(), typeTo.getTypeInfo());
			
			for (int i = adapters.length - 1; i >= 0; i--)
			{
				if ( registry.isAgent( adapters[i], BConversionLink.TYPE.getTypeInfo() ) )
				{
					converter = (BConverter) adapters[i].getInstance();
					return converter;
				}
			}
		}
		catch(Exception e){throw e;}
		
		return converter;
	}
	
	
	
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * Determines if a slot exists on a given component.
	 * 
	 * @param inComp - BComponent to look for given slot.
	 * @param inSlotName - String name of slot to find.
	 * @return boolean.
	 */
	public static boolean doesSlotExist(BComponent inComp, String inSlotName)
	{
		boolean result = false;
		
		try
		{
			Slot slot = inComp.getSlot(inSlotName);
			if(slot.getDeclaringType().getDisplayName(null).length() > 0) { /* do nothing, this just here to prevent compile warning */}
			result = true;
		}
		catch(Exception e)
		{
			result = false;
		}
		
		return result;
	}
	
	
	/*----------------------------------------------------------------------------------------------------------------------------------------*/
	/**
	 * Convenience method for {@link #isOrdValid(BOrd)}.
	 * 
	 * @param ord
	 * @return Returns 'true' if ord is found to be valid.
	 */
	public boolean isOrdValid(String ord)
	{
		try
		{
			return isOrdValid(BOrd.make(ord));
		}
		catch(Exception e)
		{
			return false;
		}
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
	public boolean isOrdValid(BOrd ord)
	{
		try
		{
			BComponent com = (BComponent)ord.relativizeToHost().get();
			//This gets rid of the "unused variable" warning Eclipse gives.
			com = (BComponent)com;
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * Will reorder the dynamic slots alphabetically for the provided BComponent.
	 * 
	 * @param inComp
	 * @throws Exception
	 */
	public void reorderSlots(BComponent inComp) throws Exception
	{
		try
		{
			List<Property> lstProps = new ArrayList<Property>();
			Collections.addAll(lstProps, inComp.getDynamicPropertiesArray());

			Collections.sort(lstProps, new Comparator<Property>()
			{
				@Override
				public int compare(Property prop1, Property prop2)
				{
					return prop1.getName().compareTo(prop2.getName());
				}
			});

			Property[] sortedProps = lstProps.toArray(new Property[lstProps.size()]);
			inComp.reorder(sortedProps);
		}
		catch (Exception e) 
		{
			throw e;
		}
	}
	
	
	/*----------------------------------------------------------------------------------------------------------*/
	/**
	 * Adds the given slot flag(s) to the BComponented provided.
	 * 
	 * @param inComp - BComponent to add the flag(s) to.
	 * @param inSlot - Slot on the BComponent to add the flag(s).
	 * @param inFlag - int value that represents the flag(s) to add.
	 * @throws Exception
	 */
	public void addSlotFlag(BComponent inComp, Slot inSlot, int inFlag) throws Exception
	{
		try
		{
			if( inComp.getLinks(inSlot).length <= 0 && inComp.getKnobs(inSlot).length <= 0)	{inComp.setFlags(inSlot, (inComp.getFlags(inSlot) |  inFlag ));}
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	
	/*----------------------------------------------------------------------------------------------------------*/
	/**
	 * Removes the given slot flag(s) from the BComponented provided.
	 * 
	 * @param inComp - BComponent to remove the flag(s) from.
	 * @param inSlot - Slot on the BComponent to remove the flag(s).
	 * @param inFlag - int value that represents the flag(s) to remove.
	 * @throws Exception
	 */
	public void removeSlotFlag(BComponent inComp, Slot inSlot, int inFlag) throws Exception
	{
		try
		{
			inComp.setFlags(inSlot, (inComp.getFlags(inSlot) & ~inFlag ));
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	
	
	
	
		
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * @param inMsg - String, exception message prefix, usually the calling method name.
	 * @param inEx - Exception, the exception that was thrown and you want to generate a string message for.
	 * @return String
	 */
	public static String buildErrorMsg(String msg, Exception e)
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
		}
		catch (Exception e1){}
		
		return msg.trim();
	}
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * Convenience method for {@link org.axcommunity.niagara.helperClasses.Utilities#errorHandler(Level, BComponent, String, Exception) errorHandler(Level.SEVERE, BComponent, "", Exception)}
	 */
	public static String errorHandler(BComponent comp, Exception e)
	{
		return errorHandler(Level.SEVERE, comp, "", e);
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * Convenience method for {@link org.axcommunity.niagara.helperClasses.Utilities#errorHandler(Level, BComponent, String, Exception) errorHandler(Level.SEVERE, BComponent, "", Exception)}
	 */
	public static String errorHandler(BComponent comp, String msg, Exception e)
	{
		return errorHandler(Level.SEVERE, comp, msg, e);
	}
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * Generates a string containing all details about the exception.
	 * 
	 * @param level
	 * @param comp - the BComponent calling this method.
	 * @param msg - an optional string to prefix the error message with. <br>
	 * Can be useful in debugging if you specify the method that generated the exception.
	 * @param e
	 * @return - String
	 */
	public static String errorHandler(Level level, BComponent comp, String msg, Exception e)
	{
		try
		{
			msg	= buildErrorMsg(msg, e);
			logger.log(level, comp.getSlotPath() + "\t" + msg);
		}
		catch (Exception e1)
		{
			logger.log( level, ("EXCEPTION ERROR WITH 'axCommunity:errorHandler'"+"\n"+msg).trim() );
		}
		
		return msg.trim();
	}
	
	
	
	//public Type getType() { return TYPE; }
	//public static final Type TYPE = Sys.loadType(Utilities.class);
	
	public static final Logger logger = Logger.getLogger("axCommunity");
}





















