package org.axcommunity.niagara.helperClasses;


import javax.baja.naming.SlotPath;
import javax.baja.sys.Type;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 *
 * @author Justin Koffler
 * @creation 11/01/2021 11:36:18 AM
 *
 */
public interface LoggingComponent 
{
	default boolean isDebug() {return getInDebug()||getDebug();}
	
	/** Override this with slot's getter. */
	default boolean getDebug()
	{ return false; }
	
	/** Override this with slot's getter. */
	default boolean getInDebug()
	{ return false; }
	
	/** Override this with slot's getter. */
	default boolean getInSuppressSlotPath()
	{ return false; }
	
	/* Logger.getLogger caches loggers by name */
	default Logger getLogger()
	{ return Logger.getLogger(getType().getModule().getModuleName() + "." + getType().getTypeName()); }
	
	SlotPath getSlotPath();
	Type getType();
	
	
    
	/** The max length a returned message should be as not to exceed the limit a string history allows */
	static final int	MAXLOGLENGTH			= 3583;
	static final int	ERROR_FRAME_INDEX		= 0;
	static final Level	DEFAULT_MESSAGE_LEVEL	= Level.FINEST;
	static final Level	DEFAULT_ERROR_LEVEL		= Level.FINE;
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for<br>{@link #errorHandler(Level, String, String, Exception) errorHandler(Level Level.FINE, String getMethodName(), String "", Exception e)} */
	default String errorHandler(Exception e)
	{
		try
		{
			String methodName = "";
			
			if(isLoggable(DEFAULT_ERROR_LEVEL, DEFAULT_ERROR_LEVEL))
			{
				StackTraceElement[]	stackTrace	= e.getStackTrace();
				String				lineNumber	= "";
				String				className	= "";
				//if( getInSuppressSlotPath()==true ) try{className = cleanClassName(stackTrace[ERROR_FRAME_INDEX].getClassName());}catch(Exception ee) {}
				try{className = cleanClassName(stackTrace[ERROR_FRAME_INDEX].getClassName());}catch(Exception ee) {}
				try{lineNumber = Integer.toString( stackTrace[ERROR_FRAME_INDEX].getLineNumber() );}catch(Exception ee) {}
				try{methodName = (className.isEmpty()?"":className+", ")+(lineNumber.isEmpty()?"":"["+lineNumber+"] ")+ ((methodName.isEmpty() ? stackTrace[ERROR_FRAME_INDEX].getMethodName()+"()": methodName));}catch(Exception ee) {}
			}
		
			return errorHandler(DEFAULT_ERROR_LEVEL, methodName, "", e);
		}catch(Exception ex) {return "ERROR";}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for<br>{@link #errorHandler(Level, String, String, Exception) errorHandler(Level Level.FINE, String getMethodName(), String msg, Exception e)} */
	default String errorHandler(String msg, Exception e)
	{
		try
		{
			String methodName = "";
			
			if(isLoggable(DEFAULT_ERROR_LEVEL, DEFAULT_ERROR_LEVEL))
			{
				StackTraceElement[]	stackTrace	= e.getStackTrace();
				String				lineNumber	= "";
				String				className	= "";
				//if( getInSuppressSlotPath()==true ) try{className = cleanClassName(stackTrace[ERROR_FRAME_INDEX].getClassName());}catch(Exception ee) {}
				try{className = cleanClassName(stackTrace[ERROR_FRAME_INDEX].getClassName());}catch(Exception ee) {}
				try{lineNumber = Integer.toString( stackTrace[ERROR_FRAME_INDEX].getLineNumber() );}catch(Exception ee) {}
				try{methodName = (className.isEmpty()?"":className+", ")+(lineNumber.isEmpty()?"":"["+lineNumber+"] ")+ ((methodName.isEmpty() ? stackTrace[ERROR_FRAME_INDEX].getMethodName()+"()": methodName));}catch(Exception ee) {}
			}
		
			return errorHandler(DEFAULT_ERROR_LEVEL, methodName, msg, e);
		}catch(Exception ex) {return ("ERROR" + "\n" + msg).trim();}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for<br>{@link #errorHandler(Level, String, String, Exception) errorHandler(Level level, String getMethodName(), String "", Exception e)} */
	default String errorHandler(Level level, Exception e)
	{
		try
		{
			String methodName = "";
			
			if(isLoggable(level, DEFAULT_ERROR_LEVEL))
			{
				StackTraceElement[]	stackTrace	= e.getStackTrace();
				String				lineNumber	= "";
				String				className	= "";
				//if( getInSuppressSlotPath()==true ) try{className = cleanClassName(stackTrace[ERROR_FRAME_INDEX].getClassName());}catch(Exception ee) {}
				try{className = cleanClassName(stackTrace[ERROR_FRAME_INDEX].getClassName());}catch(Exception ee) {}
				try{lineNumber = Integer.toString( stackTrace[ERROR_FRAME_INDEX].getLineNumber() );}catch(Exception ee) {}
				try{methodName = (className.isEmpty()?"":className+", ")+(lineNumber.isEmpty()?"":"["+lineNumber+"] ")+ ((methodName.isEmpty() ? stackTrace[ERROR_FRAME_INDEX].getMethodName()+"()": methodName));}catch(Exception ee) {}
			}
		
			return errorHandler(level!=null?level:DEFAULT_ERROR_LEVEL, methodName, "", e);
		}catch(Exception ex) {return "ERROR";}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for<br>{@link #errorHandler(Level, String, String, Exception) errorHandler(Level level, String getMethodName(), String msg, Exception e)} */
	default String errorHandler(Level level, String msg, Exception e)
	{
		try
		{
			String methodName = "";
			
			if(isLoggable(level, DEFAULT_ERROR_LEVEL))
			{
				StackTraceElement[]	stackTrace	= e.getStackTrace();
				String				lineNumber	= "";
				String				className	= "";
				//if( getInSuppressSlotPath()==true ) try{className = cleanClassName(stackTrace[ERROR_FRAME_INDEX].getClassName());}catch(Exception ee) {}
				try{className = cleanClassName(stackTrace[ERROR_FRAME_INDEX].getClassName());}catch(Exception ee) {}
				try{lineNumber = Integer.toString( stackTrace[ERROR_FRAME_INDEX].getLineNumber() );}catch(Exception ee) {}
				try{methodName = (className.isEmpty()?"":className+", ")+(lineNumber.isEmpty()?"":"["+lineNumber+"] ")+ ((methodName.isEmpty() ? stackTrace[ERROR_FRAME_INDEX].getMethodName()+"()": methodName));}catch(Exception ee) {}
			}
		
			return errorHandler(level!=null?level:DEFAULT_ERROR_LEVEL, methodName, msg, e);
		}catch(Exception ex) {return ("ERROR" + "\n" + msg).trim();}
	}
		
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * 
	 * @param level - Level, the level you want the message to be logged at. <br>This is overridden by debug.
	 * @param methodName - String, name of calling method.
	 * @param msg - String, message you want logged.
	 * @param e - Exception
	 * @return String representing the value of the provided msg param and the message portion of the 
	 * exception if available otherwise a shortened version of the stacktrace. The method name
	 * is not included in the returned string.
	 */
	default String errorHandler(Level level, String methodName, String msg, Exception e)
	{
		String			MSG_DIV			= "-------------------------------------------------------------------------------------------------------------------------------------------";
		StringWriter	ERRORS			= new StringWriter();
		String			MESSAGE			= "";
		String			CAUSE			= "";
		String			STACKTRACE		= "";
		String			PRINTSTACKTRACE	= "";
		String			TMPMSG_FULL		= "";
		String			TMPMSG_SHORT	= "";
		String			METHOD_NAME		= "";
		
		level = level != null ? level : DEFAULT_ERROR_LEVEL;
		
		try
		{
			StackTraceElement[] stackTrace = new Throwable().getStackTrace();
			try{METHOD_NAME	= (methodName!=null && methodName.length()>0 ? methodName : (stackTrace[ERROR_FRAME_INDEX].getMethodName()+"()"));}catch(Exception ex) {};
			try{MESSAGE		= e.getMessage().trim();}catch(Exception ex) {}
			try{CAUSE		= e.getCause().toString().trim();}catch(Exception ex) {}
			try{STACKTRACE	= e.getStackTrace()[0].toString().trim();}catch(Exception ex) {}
			try{e.printStackTrace(new PrintWriter(ERRORS));
				PRINTSTACKTRACE = ERRORS.toString().trim();
			}catch(Exception ex) {}
			
			TMPMSG_FULL	= (METHOD_NAME.length()>0 ? METHOD_NAME + ", " + msg : msg) 
					+ (MESSAGE.trim().length()>0 ?			"\n" + "MESSAGE:		\n" + MESSAGE.trim()	: "")
					+ (CAUSE.trim().length()>0 ?			"\n" + "CAUSE:			\n" + CAUSE.trim()		: "")
					+ (STACKTRACE.trim().length()>0 ?		"\n" + "STACKTRACE:		\n" + STACKTRACE.trim()	: "")
					+ (PRINTSTACKTRACE.trim().length()>0&&(level.intValue()>Level.FINE.intValue()||CAUSE.trim().length()<=0) ?	"\n" + "PRINTSTACKTRACE:\n" + PRINTSTACKTRACE	: "");
			TMPMSG_FULL	= TMPMSG_FULL.length()>MAXLOGLENGTH? TMPMSG_FULL.substring(0, MAXLOGLENGTH) : TMPMSG_FULL;
			
			if(MESSAGE.length() > 0)
			{
				TMPMSG_SHORT	= (msg + "\n" + MESSAGE).trim();
				TMPMSG_SHORT	= TMPMSG_SHORT.length()>MAXLOGLENGTH? TMPMSG_SHORT.substring(0, MAXLOGLENGTH) : TMPMSG_SHORT;
			}
			else
			{
				TMPMSG_SHORT	= (msg + "\n" + (STACKTRACE.length()>0? "STACKTRACE: \n" + STACKTRACE:  "PRINTSTACKTRACE: \n" + PRINTSTACKTRACE)).trim();
				TMPMSG_SHORT	= TMPMSG_SHORT.length()>MAXLOGLENGTH? TMPMSG_SHORT.substring(0, MAXLOGLENGTH) : TMPMSG_SHORT;
			}
			
			if(isDebug()) {System.out.println("\n\n"+MSG_DIV+"\n" + getTimestamp() + "\n" + (getInSuppressSlotPath()?"":this.getSlotPath()+"\n") + TMPMSG_FULL.trim() + "\n"+MSG_DIV+"\n\n");}
			else{getLogger().log(level, "\n\n"+MSG_DIV+"\n" + (getInSuppressSlotPath()?"":this.getSlotPath()+"\n") + TMPMSG_FULL.trim() + "\n"+MSG_DIV+"\n\n");}
		}
		catch (Exception e1)
		{
			if(isDebug()) 
			{
				if(isDebug()) {System.out.println(getTimestamp() + ", " + "EXCEPTION ERROR WITH '" + getType().getModule().getModuleName() + "." + getType().getTypeName() + "'"); e.printStackTrace();}
				else{getLogger().log(level, "EXCEPTION ERROR WITH '" + getType().getModule().getModuleName() + "." + getType().getTypeName() + "'"); e.printStackTrace();}
			}
		}
		
		return TMPMSG_SHORT.trim();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for {@link #messageHandler(Level, String, String, boolean) messageHandler(Level Level.FINEST, String getMethodName(), String <b>msg</b>, boolean false)} */
	default String messageHandler(String msg)
	{
		try
		{
			String className, lineNumber, methodName = lineNumber = className = "";
			
			if(isLoggable(DEFAULT_MESSAGE_LEVEL, DEFAULT_MESSAGE_LEVEL))
			{
				StackTraceElement[]	stackTrace	= new Throwable().getStackTrace();
				int					frame		= (stackTrace[1].getMethodName()).startsWith("access$") ? 2 : 1;
				//if( getInSuppressSlotPath()==true ) try{className = cleanClassName(stackTrace[frame].getClassName());}catch(Exception ee) {}
				try{className = cleanClassName(stackTrace[frame].getClassName());}catch(Exception ee) {}
				try{lineNumber = Integer.toString( stackTrace[frame].getLineNumber() );}catch(Exception ee) {}
				try{methodName = (methodName.isEmpty() ? stackTrace[frame].getMethodName()+"()": methodName);}catch(Exception ee) {}
			}
			
			return messageHandler(DEFAULT_MESSAGE_LEVEL,className,lineNumber,methodName,msg, false);
		}catch(Exception e) {return msg;}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for {@link #messageHandler(Level, String, String, boolean) messageHandler(Level <b>level</b>, String getMethodName(), String <b>msg</b>, boolean false)} */
	default String messageHandler(Level level, String msg)
	{
		try
		{
			String className, lineNumber, methodName = lineNumber = className = "";
			
			if(isLoggable(level, DEFAULT_MESSAGE_LEVEL))
			{
				StackTraceElement[]	stackTrace	= new Throwable().getStackTrace();
				int					frame		= (stackTrace[1].getMethodName()).startsWith("access$") ? 2 : 1;
				//if( getInSuppressSlotPath()==true ) try{className = cleanClassName(stackTrace[frame].getClassName());}catch(Exception ee) {}
				try{className = cleanClassName(stackTrace[frame].getClassName());}catch(Exception ee) {}
				try{lineNumber = Integer.toString( stackTrace[frame].getLineNumber() );}catch(Exception ee) {}
				try{methodName = (methodName.isEmpty() ? stackTrace[frame].getMethodName()+"()": methodName);}catch(Exception ee) {}
			}
			
			return messageHandler(level!=null?level:DEFAULT_MESSAGE_LEVEL,className,lineNumber,methodName,msg, false);
		}catch(Exception e) {return msg;}
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for {@link #messageHandler(Level, String, String, boolean) messageHandler(Level Level.FINEST, String <b>methodName</b>, String <b>msg</b>, boolean true)} */
	default String messageHandler(String methodName, String msg)
	{
		return messageHandler(DEFAULT_MESSAGE_LEVEL,"","",methodName,msg, true);
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for {@link #messageHandler(Level, String, String, boolean) messageHandler(Level <b>level</b>, String <b>methodName</b>, String <b>msg</b>, boolean false)} */
	default String messageHandler(Level level, String methodName, String msg)
	{
		return messageHandler(level!=null?level:DEFAULT_MESSAGE_LEVEL,"","",methodName,msg, false);
	}
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/** Convenience method for {@link #messageHandler(Level, String, String, boolean) messageHandler(Level <b>level</b>, String <b>methodName</b>, String <b>msg</b>, boolean <b>returnMethodName</b>)} */
	default String messageHandler(Level level, String methodName, String msg, boolean returnMethodName)
	{
		return messageHandler(level!=null?level:DEFAULT_MESSAGE_LEVEL, "","",methodName, msg, returnMethodName);
	}
	
	
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
	/**
	 * The final boss of the messageHandler methods.</br>
	 * Logs a message to application director and returns formated string.</p>
	 * This method is not intended to be called outside this class,
	 * but there is nothing stopping you from doing so.</p>
	 * 
	 * @param level - Level, the level you want the message to be logged at. This is overridden by debug.
	 * @param methodName - String, name of calling method.
	 * @param msg - String, message you want logged.
	 * @param returnMethodName - boolean, if true the method name will be included in the returned String.
	 * @param cx - only used to differentiate from other similar method. Just pass a null value.
	 * @return String containing same string provided and trimmed to {@link #MAXLOGLENGTH}
	 * 
	 */
	default String messageHandler(Level level, String className, String lineNumber, String methodName, String msg, Boolean returnMethodName)
	{
		try
		{
			/* set default values if null to avoid exceptions later...
			 * sure would be nice if java had an easy way to 
			 * set the defaults like C# does. */
			if ( level == null )			level				= DEFAULT_MESSAGE_LEVEL;
			if ( className == null )		className			= "";
			if ( lineNumber == null )		lineNumber			= "";
			if ( methodName == null )		methodName			= "";
			if ( msg == null )				msg					= "";
			if ( returnMethodName == null )	returnMethodName	= false;
			
			
			/* check to see if we actually need these values, 
			 * if not just skip this extra logic... */
			if(isLoggable(level, DEFAULT_MESSAGE_LEVEL) || returnMethodName)
			{
				StackTraceElement[]	stackTrace	= new Throwable().getStackTrace();
				int					frame		= (stackTrace[1].getMethodName()).startsWith("access$") ? 2 : 1;
				
				/* only include class name if not including slot path in message. */
//				if( getInSuppressSlotPath()==true )
//				{
					try{if(className.isEmpty()) className = stackTrace[frame].getClassName();}catch(Exception ee) {}
					try{if(!className.isEmpty()) className = className.substring(className.lastIndexOf(".")+1);}catch(Exception ee) {}
					try{if(!className.isEmpty() && className.indexOf("$")>0) className = className.substring(0, className.lastIndexOf("$"));}catch(Exception ee) {}
//				} else { className = ""; }
				
				try{if(lineNumber.isEmpty()) lineNumber = Integer.toString( stackTrace[frame].getLineNumber() );}catch(Exception ee) {}
				try{methodName = (className.isEmpty()?"":className+", ")+(lineNumber.isEmpty()?"":"["+lineNumber+"] ")+ ((methodName.isEmpty() ? stackTrace[frame].getMethodName()+"()": methodName));}catch(Exception ee) {}
			}
			
			String returnMsg = trimToMaxLength(returnMethodName && methodName.length() > 0 ? methodName + ", " + msg : msg);
			String logMsg = methodName.length() > 0 ? methodName + ", " + msg : msg;
			
			if(isDebug()) {System.out.println(getTimestamp() + ", " + (getInSuppressSlotPath()?"":this.getSlotPath()+", ") + logMsg );}
			else{getLogger().log(level, (getInSuppressSlotPath()?"":this.getSlotPath()+", ") + logMsg );}
			return returnMsg;
		}
		catch (Exception e1)
		{
			if(isDebug()) {System.out.println(getTimestamp() + ", " + "EXCEPTION ERROR WITH '" + getType().getModule().getModuleName() + "." + getType().getTypeName() + "'");}
			else{getLogger().log(level, "EXCEPTION ERROR WITH '" + getType().getModule().getModuleName() + "." + getType().getTypeName() + "'");}
			return msg;
		}
	}
	/*---------------------------------------------------------------------------------------------------------*/
	/*---------------------------------------------------------------------------------------------------------*/
	/*---------------------------------------------------------------------------------------------------------*/
	
	
	
	
	
	
	
	
	/*---------------------------------------------------------------------------------------------------------*/
	default String getMethodName()
	{
		try
		{
			StackTraceElement[] stackTrace = new Throwable().getStackTrace();
			return (stackTrace[1].getMethodName()).startsWith("access$") ? stackTrace[2].getMethodName()+"()" : stackTrace[1].getMethodName()+"()";
		}
		catch (Exception e){return "?";}
	}
	
	/*---------------------------------------------------------------------------------------------------------*/
	/**
	 * @param params - String representing the param values you want to shown between the parentheses.
	 * @return String with param values list between the parentheses</br>
	 * Example: <i>execute('theParam', 'stringYou', 'provided')</i>
	 */
	default String getMethodName(String params)
	{
		try
		{
			StackTraceElement[] stackTrace = new Throwable().getStackTrace();
			params = (params!=null && params.length()>0) ? "("+params+")" : "()";
			return (stackTrace[1].getMethodName()).startsWith("access$") ? stackTrace[2].getMethodName()+params : stackTrace[1].getMethodName()+params;
		}
		catch (Exception e){return "?";}
	}
	
	/*---------------------------------------------------------------------------------------------------------*/
	default String getLineNumber()
	{
		try
		{
			StackTraceElement[] stackTrace = new Throwable().getStackTrace();
			return  Integer.toString( (stackTrace[1].getMethodName()).startsWith("access$") ? stackTrace[2].getLineNumber() : stackTrace[1].getLineNumber() );
		}
		catch (Exception e){return "?";}
	}
	
	/*---------------------------------------------------------------------------------------------------------*/
	/** @return String representing current timestamp in the format<br> <code>yyyy-MM-dd HH:mm:ss.SSS</code> */
	default String getTimestamp()
	{
		try
		{
			return   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		}
		catch (Exception e){return "?";}
	}
	
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	static String cleanClassName(String className)
	{
		try
		{
			try{if(!className.isEmpty()) className = className.substring(className.lastIndexOf(".")+1);}catch(Exception ee) {}
			try{if(!className.isEmpty() && className.indexOf("$")>0) className = className.substring(0, className.lastIndexOf("$"));}catch(Exception ee) {}
			return className;
		}
		catch(Exception e) 
		{
			return className;
		}
	}
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	static String trimToMaxLength(String str)
	{
		try
		{
			return str.length() > MAXLOGLENGTH ? str.substring(0, MAXLOGLENGTH) : str;
		}
		catch(Exception e) 
		{
			return str;
		}
	}
	
	
	/*----------------------------------------------------------------------------------------------------------------*/
	default boolean isLoggable(Level level, Level levelDefault)
	{
		try
		{
			if(isDebug()) return true;
			if ( level == null ) level = levelDefault;
			Logger				defaultLogger		= null;
			boolean				isLoggable			= false;
			boolean				isLoggableThis		= false;
			boolean				isLoggableDefault	= false;
		
			if(!isDebug()) try{defaultLogger		= LogManager.getLogManager().getLogger("");}catch(Exception e) {}		
			if(!isDebug()) try{isLoggableThis		= getLogger().isLoggable(level);}catch(Exception e) {}	
			if(!isDebug()) try{isLoggableDefault	= defaultLogger.isLoggable(level);}catch(Exception e) {}	
			try{isLoggable = (isDebug() || isLoggableThis || isLoggableDefault);}catch(Exception e) {}
		
			return isLoggable;
		}
		catch(Exception e) 
		{
			return false;
		}
	}
	
	
}
