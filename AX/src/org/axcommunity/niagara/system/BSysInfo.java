package org.axcommunity.niagara.system;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.logging.Level;

import javax.baja.log.Log;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BRelTime;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Topic;
import javax.baja.sys.Type;

import com.tridium.platform.BSystemPlatformService;
import com.tridium.sys.station.Station;


/**
 * Executes every executePeriod and displays current system information.
 *
 * @author		Mike Arnott, Kors Engineering
 */

public class BSysInfo extends BComponent	
{

	//private	BSystemPlatformService	sysObject			= new BSystemPlatformService();
	//private	BStation				station				= new BStation();
	Clock.Ticket					ticket;
	//long							lastOnExecuteTicks;
	
	
	/*----------------------------------------------------------------------------------------------------------*/
	public static final Property facetsNumerics = newProperty(0, BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)), null);
	public BFacets getFacetsNumerics() { return (BFacets)get(facetsNumerics); }
	public void setFacetsNumerics(BFacets v) { set(facetsNumerics,v,null); }
	
	public static final Property facetsStrings = newProperty(0, BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)), null);
	public BFacets getFacetsStrings() { return (BFacets)get(facetsStrings); }
	public void setFacetsStrings(BFacets v) { set(facetsStrings,v,null); }
	
	
	public static final Property populateNetworkInfo = newProperty(0, true);
	public boolean getPopulateNetworkInfo() { return getBoolean(populateNetworkInfo); }
	public void setPopulateNetworkInfo(boolean v) { setBoolean(populateNetworkInfo, v, null); }

	public static final Property populateHostId = newProperty(0, false);
	public boolean getPopulateHostId() { return getBoolean(populateHostId); }
	public void setPopulateHostId(boolean v) { setBoolean(populateHostId, v, null); }
	
	/*----------------------------------------------------------------------------------------------------------*/
	public BFacets getSlotFacets(Slot slot)
	{
		if(	slot.getName().equals("cpuUsage")				|
			slot.getName().equals("totalPhysicalMemory")	|
			slot.getName().equals("freePhysicalMemory")		|
			slot.getName().equals("overallCpuUsage")		|
			slot.getName().equals("usedHeap")				|
			slot.getName().equals("totalHeap")				|
			slot.getName().equals("freeHeap")				)
		{
			return getFacetsNumerics();
		}
		
		if(	slot.getName().equals("bajaHome")					|
			slot.getName().equals("javaVmName")					|
			slot.getName().equals("javaVmVersion")				|
			slot.getName().equals("locale")						|
			slot.getName().equals("niagaraVersion")				|
			slot.getName().equals("osArch")						|
			slot.getName().equals("osName")						|
			slot.getName().equals("osVersion")					|
			slot.getName().equals("platformServiceDescription")	|
			slot.getName().equals("stationName")				|
			slot.getName().equals("ipAddress")					|
			slot.getName().equals("hostName")					|
			slot.getName().equals("domain")						|
			slot.getName().equals("fqdn")						)
		{
			return getFacetsStrings();
		}
		
		return super.getSlotFacets(slot);
	}
	
	public static final Action Update = newAction(Flags.SUMMARY|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void Update(){invoke(Update,null,null);}
	public void doUpdate()
	{
		calculate();
	}

	public static final Property executePeriod = newProperty(Flags.SUMMARY,	BRelTime.make(60000), BFacets.make(BFacets.MIN, BRelTime.makeSeconds(0)));
	public BRelTime getExecutePeriod() { return (BRelTime)get("executePeriod"); }
	public void setExecutePeriod(BRelTime v) { set("executePeriod", v); }
	
	//events for the timer
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);
	public void timerExpired() { invoke(timerExpired,null,null); }
	public void doTimerExpired() throws Exception 
	{
		calculate();
	}
	
	public void atSteadyState()
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		calculate();
		updateTimer();
	}
	
	public void started()
	{
		if(!Sys.atSteadyState() || !isRunning()){return;}
		calculate();
		updateTimer();
	}
	
	public void changed(Property p, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning()) return;
		if(p.equals(executePeriod))
		{
			updateTimer();
			calculate();
		}

		if(p.equals(populateNetworkInfo))
		{
			if(getPopulateNetworkInfo()==false)
			{
				getHostName().setValue("");
				getFqdn().setValue("");
				getDomain().setValue("");
				getIpAddress().setValue("");
			}
			else
			{
				calculate();
			}
		}
		
		if(p.equals(populateHostId))
		{
			if(getPopulateHostId()==false)
			{
				getStationHostId().setValue("");
			}
			else
			{
				calculate();
			}
		}
	}
	
	
	
	void updateTimer()
	{						
		if (ticket != null) ticket.cancel();
		if(getExecutePeriod().getMillis() > 0) ticket = Clock.schedulePeriodically(this, getExecutePeriod(), timerExpired, null);
	}		

	public void calculate()
	{
		try
		{
			BSystemPlatformService sysObject = (BSystemPlatformService) Sys.getService(BSystemPlatformService.TYPE);
			getCpuUsage().setValue((double) sysObject.getCurrentCpuUsage());
			getBajaHome().setValue(sysObject.getBajaHome());
			getFreePhysicalMemory().setValue((double) sysObject.getFreePhysicalMemory());
			
			getJavaVmName().setValue(sysObject.getJavaVmName());
			getJavaVmVersion().setValue(sysObject.getJavaVmVersion());
			getLocale().setValue(sysObject.getLocale());
			getNiagaraVersion().setValue(sysObject.getNiagaraVersion());
			getOsArch().setValue(sysObject.getOsArch());
			getOsName().setValue(sysObject.getOsName());
			getOsVersion().setValue(sysObject.getOsVersion());
			getOverallCpuUsage().setValue((double) sysObject.getOverallCpuUsage());
			getPlatformServiceDescription().setValue(sysObject.getPlatformServiceDescription());
			getStationName().setValue(sysObject.getStationName());
			getTotalPhysicalMemory().setValue((double) sysObject.getTotalPhysicalMemory()); //INT VALUE
			long totalMem = Runtime.getRuntime().totalMemory() / 1024;
			long freeMem = Runtime.getRuntime().freeMemory() / 1024;
			long maxMem = Runtime.getRuntime().maxMemory() / 1024;
			long usedMem = totalMem - freeMem;
			getTotalHeap().setValue(totalMem);
			getUsedHeap().setValue(usedMem);
			getFreeHeap().setValue(freeMem);
			getMaxHeap().setValue(maxMem);
			
			/*
			try{getNiagaraHome().setValue(Sys.getNiagaraHome().toString());}
			catch (Exception e){getNiagaraHome().setValue("");}
			
			try{getNiagaraUserHome().setValue(Sys.getNiagaraUserHome().toString());}
			catch (Exception e){getNiagaraUserHome().setValue("");}
			
			try{getProtectedStationHome().setValue(Sys.getProtectedStationHome().toString());}
			catch (Exception e){getProtectedStationHome().setValue("");}
			*/
			try{getStationHome().setValue(Sys.getStationHome().toString());}
			catch (Exception e){getStationHome().setValue("");}
			
			if(getPopulateHostId()==true)
			{
				getStationHostId().setValue( sysObject.getHostId() );
			}
			else
			{
				getStationHostId().setValue("");
			}
			
			if(getPopulateNetworkInfo()==true)
			{
				Thread t = new Thread(new InetInfo());
				t.start();
			}
			else
			{
				getHostName().setValue("");
				getFqdn().setValue("");
				getDomain().setValue("");
				getIpAddress().setValue("");
				
				fireUpdated(BBoolean.make(true));
			}
		}
		catch (Exception e)
		{
			errorHandler("Exception in calculate() method!", e);
		}
	}


	
	private final Station.SaveListener saveListener = new Station.SaveListener()
	{
		//@Override
		public void stationSave()
		{
			try
			{
				log.trace("\t" + getSlotPath() + "\t" + "stationSave() method called.");
				getExecutingSave().setValue(true);
			}
			catch (Exception e)
			{
			}
		}
		
		
		//@Override
		public void stationSaveOk()
		{
			log.trace("\t" + getSlotPath() + "\t" + "stationSaveOk() method called.");
			getExecutingSave().setValue(false);
			fireStationSaveSuccess(BBoolean.make(true));
			Station.removeSaveListener(saveListener);
		}
		
		
		//@Override
		public void stationSaveFail(String cause)
		{
			log.trace("\t" + getSlotPath() + "\t" + "stationSaveFail() method called.");
			getExecutingSave().setValue(false);
			fireStationSaveFailed(BBoolean.make(true));
			Station.removeSaveListener(saveListener);
		}
	};
	
	
	private void errorHandler(String msgPrefix, Exception e)
	{
		String errMsg = msgPrefix.trim() + "\n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		errMsg = errMsg.trim() + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
		log.error("\n" + getSlotPath() + "\n" + errMsg);
	}
	
	private void errorHandlerTrace(String msgPrefix, Exception e)
	{
		String errMsg = msgPrefix.trim() + "\n" + "MESSAGE: \n" + e.getMessage() + "\n" + "STACKTRACE: \n" + e.getStackTrace();
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		errMsg = errMsg.trim() + "\n" + "PRINTSTACKTRACE: \n" + errors.toString();
		log.trace("\n" + getSlotPath() + "\n" + errMsg);
	}
	
	
	class InetInfo implements Runnable
	{
		public void run()
		{
			try
			{
				try
				{
					getHostName().setValue(InetAddress.getLocalHost().getHostName());
				}
				catch (Exception e)
				{
					errorHandlerTrace("Exception in InetInfo.run().getHostName() method!", e);
					getHostName().setValue("ERROR");
				}

				try
				{
					getFqdn().setValue(InetAddress.getLocalHost().getCanonicalHostName());
				}
				catch (Exception e)
				{
					errorHandlerTrace("Exception in InetInfo.run().getFqdn() method!", e);
					getFqdn().setValue("ERROR");
				}

				try
				{
					String canonicalHostName = InetAddress.getLocalHost().getCanonicalHostName();
					String hostAddress = InetAddress.getLocalHost().getHostAddress();
					String hostName = InetAddress.getLocalHost().getHostName();
					
					if(canonicalHostName.length()>0 && hostName.length()>0)
					{
						if(!canonicalHostName.equalsIgnoreCase(hostAddress)	&& !canonicalHostName.equalsIgnoreCase(hostName) )
						{
							if(canonicalHostName.indexOf(hostName)>=0 && canonicalHostName.length() > hostName.length()+1)
							{
								getDomain().setValue(canonicalHostName.substring(hostName.length() + 1));
							}
							else
							{
								getDomain().setValue("");
							}
						}
						else
						{
							getDomain().setValue("");
						}
					}
					else
					{
						getDomain().setValue("");
					}
				}
				catch (Exception e)
				{
					errorHandlerTrace("Exception in InetInfo.run().getDomain() method!", e);
					getDomain().setValue("ERROR");
				}

				try
				{
					getIpAddress().setValue(InetAddress.getLocalHost().getHostAddress());
				}
				catch (Exception e)
				{
					errorHandlerTrace("Exception in InetInfo.run().getIpAddress() method!", e);
					
					getIpAddress().setValue("ERROR");
				}
			}
			catch (Exception e)
			{
				errorHandlerTrace("Exception in InetInfo.run() method!", e);
			}
			
			fireUpdated(BBoolean.make(true));
		}
	}

	/** 
	 * This will initiate a station save.
	 * The "CONFIRM_REQUIRED" flag is set by default but when used from logic this confirmation isn't required (that I can tell at least).
	 */
	public static final Action SaveStation = newAction(Flags.ASYNC|Flags.CONFIRM_REQUIRED, null, null);
	public void SaveStation(){invoke(SaveStation,null,null);}
	public void doSaveStation()
	{
		try
		{
			Station.addSaveListener(saveListener);
			getExecutingSave().setValue(true);
			System.out.println("\nA Station Save Has Been Invoked From:\t\t" + getSlotPath());
			Sys.getStation().save();
			
			
		}
		catch (Exception e)
		{
			errorHandler("Exception in doSaveStation() method!", e);
			getExecutingSave().setValue(false);
		}
	}
	
	/** 
	 * This will cause the station to restart just as if you did it from the application director.
	 * The "CONFIRM_REQUIRED" flag is set by default but when used from logic this confirmation isn't required (that I can tell at least).
	 */
	public static final Action RestartStation = newAction(0|Flags.ASYNC|Flags.CONFIRM_REQUIRED, null, null);
	public void RestartStation(){invoke(RestartStation,null,null);}
	public void doRestartStation()
	{
		try
		{
			System.out.println("\r\nA Station Restart Has Been Invoked From:\t\t" + getSlotPath());
			BSystemPlatformService sysObject = (BSystemPlatformService) Sys.getService(BSystemPlatformService.TYPE);
			sysObject.lease(1);
			sysObject.invoke(BSystemPlatformService.restartStation, null);
		}
		catch (Exception e)
		{
			errorHandler("Exception in doRestartStation() method!", e);
		}
	}
	
	/** 
	 * This will cause the station to reboot (entire computer\platform) just as if you did it from the application director
	 * The "CONFIRM_REQUIRED" flag is set by default but when used from logic this confirmation isn't required (that I can tell at least).
	 */
	public static final Action RebootStation = newAction(0|Flags.ASYNC|Flags.CONFIRM_REQUIRED, null, null);
	public void RebootStation(){invoke(RebootStation,null,null);}
	public void doRebootStation()
	{
		try
		{
			System.out.println("\r\nA Station Reboot Has Been Invoked From:\t\t" + getSlotPath());
			BSystemPlatformService sysObject = (BSystemPlatformService) Sys.getService(BSystemPlatformService.TYPE);
			sysObject.lease(1);
			sysObject.invoke(BSystemPlatformService.reboot, null);
		}
		catch (Exception e)
		{
			errorHandler("Exception in doRebootStation() method!", e);
		}
	}

	/***/
	public static final Property bajaHome = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setBajaHome(BStatusString v) { set(bajaHome, v); }
	public BStatusString getBajaHome() {return (BStatusString)get(bajaHome);}

	/***/
	public static final Property stationHostId = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setStationHostId(BStatusString v) { set(stationHostId, v); }
	public BStatusString getStationHostId() {return (BStatusString)get(stationHostId);}

	/***/
	public static final Property javaVmName = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setJavaVmName(BStatusString v) { set(javaVmName, v); }
	public BStatusString getJavaVmName() {return (BStatusString)get(javaVmName);}

	/***/
	public static final Property javaVmVersion = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setJavaVmVersion(BStatusString v) { set(javaVmVersion, v); }
	public BStatusString getJavaVmVersion() {return (BStatusString)get(javaVmVersion);}

	/***/
	public static final Property locale = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setLocale(BStatusString v) { set(locale, v); }
	public BStatusString getLocale() {return (BStatusString)get(locale);}

	/***/
	public static final Property niagaraVersion = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setNiagaraVersion(BStatusString v) { set(niagaraVersion, v); }
	public BStatusString getNiagaraVersion() {return (BStatusString)get(niagaraVersion);}

	/***/
	public static final Property osArch = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setOsArch(BStatusString v) { set(osArch, v); }
	public BStatusString getOsArch() {return (BStatusString)get(osArch);}

	/***/
	public static final Property osName = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setOsName(BStatusString v) { set(osName, v); }
	public BStatusString getOsName() {return (BStatusString)get(osName);}

	/***/
	public static final Property osVersion = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setOsVersion(BStatusString v) { set(osVersion, v); }
	public BStatusString getOsVersion() {return (BStatusString)get(osVersion);}

	/***/
	public static final Property platformServiceDescription = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setPlatformServiceDescription(BStatusString v) { set(platformServiceDescription, v); }
	public BStatusString getPlatformServiceDescription() {return (BStatusString)get(platformServiceDescription);}

	/***/
	public static final Property stationName = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setStationName(BStatusString v) { set(stationName, v); }
	public BStatusString getStationName() {return (BStatusString)get(stationName);}

	/***/
	public static final Property ipAddress = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setIpAddress(BStatusString v) { set(ipAddress, v); }
	public BStatusString getIpAddress() {return (BStatusString)get(ipAddress);}

	/***/
	public static final Property hostName = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setHostName(BStatusString v) { set(hostName, v); }
	public BStatusString getHostName() {return (BStatusString)get(hostName);}

	/***/
	public static final Property domain = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setDomain(BStatusString v) { set(domain, v); }
	public BStatusString getDomain() {return (BStatusString)get(domain);}

	/***/
	public static final Property fqdn = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setFqdn(BStatusString v) { set(fqdn, v); }
	public BStatusString getFqdn() {return (BStatusString)get(fqdn);}

	
	public static final Property niagaraHome = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.DEFAULT), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getNiagaraHome() { return (BStatusString)get(niagaraHome);}
	public void setNiagaraHome(BStatusString v) {set(niagaraHome,v);}
	
	
	public static final Property niagaraUserHome = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.DEFAULT), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getNiagaraUserHome() { return (BStatusString)get(niagaraUserHome);}
	public void setNiagaraUserHome(BStatusString v) {set(niagaraUserHome,v);}
	
	public static final Property protectedStationHome = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.DEFAULT), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getProtectedStationHome() { return (BStatusString)get(protectedStationHome);}
	public void setProtectedStationHome(BStatusString v) {set(protectedStationHome,v);}
	
	public static final Property stationHome = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusString("", BStatus.DEFAULT), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getStationHome() { return (BStatusString)get(stationHome);}
	public void setStationHome(BStatusString v) {set(stationHome,v);}
	
	/***/
	public static final Property cpuUsage = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setCpuUsage(BStatusNumeric v) { set(cpuUsage, v); }
	public BStatusNumeric getCpuUsage() {return (BStatusNumeric)get(cpuUsage);}

	/***/
	public static final Property totalPhysicalMemory = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setTotalPhysicalMemory(BStatusNumeric v) { set(totalPhysicalMemory, v); }
	public BStatusNumeric getTotalPhysicalMemory() {return (BStatusNumeric)get(totalPhysicalMemory);}

	/***/
	public static final Property freePhysicalMemory = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setFreePhysicalMemory(BStatusNumeric v) { set(freePhysicalMemory, v); }
	public BStatusNumeric getFreePhysicalMemory() {return (BStatusNumeric)get(freePhysicalMemory);}

	/***/
	public static final Property overallCpuUsage = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setOverallCpuUsage(BStatusNumeric v) { set(overallCpuUsage, v); }
	public BStatusNumeric getOverallCpuUsage() {return (BStatusNumeric)get(overallCpuUsage);}

	/***/
	public static final Property usedHeap = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusNumeric getUsedHeap() { return (BStatusNumeric)get(usedHeap); }
	public void setUsedHeap(javax.baja.status.BStatusNumeric v) { set(usedHeap, v); }

	/***/
	public static final Property maxHeap = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusNumeric getMaxHeap() { return (BStatusNumeric)get(maxHeap); }
	public void setMaxHeap(javax.baja.status.BStatusNumeric v) { set(maxHeap, v); }
	
	/***/
	public static final Property totalHeap = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusNumeric getTotalHeap() { return (BStatusNumeric)get(totalHeap); }
	public void setTotalHeap(javax.baja.status.BStatusNumeric v) { set(totalHeap, v); }

	/***/
	public static final Property freeHeap = newProperty(Flags.SUMMARY|Flags.DEFAULT_ON_CLONE, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusNumeric getFreeHeap() { return (BStatusNumeric)get(freeHeap); }
	public void setFreeHeap(javax.baja.status.BStatusNumeric v) { set(freeHeap, v); }
		
	
	public static final Property executingSave = newProperty(Flags.DEFAULT_ON_CLONE, new BStatusBoolean(false),null);
	public BStatusBoolean getExecutingSave() { return (BStatusBoolean) get(executingSave); }
	public void setExecutingSave(BStatusBoolean v) { set(executingSave, v); }
	
	
	public static final Topic Updated = newTopic(0);
	public void fireUpdated(BBoolean event){fire(Updated,event,null);}

	
		
	public static final Topic StationSaveSuccess = newTopic(0);
	public void fireStationSaveSuccess(BBoolean event){fire(StationSaveSuccess,event,null);}
	
		
	public static final Topic StationSaveFailed = newTopic(0);
	public void fireStationSaveFailed(BBoolean event){fire(StationSaveFailed,event,null);}
	
	
	
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BSysInfo.class);
	public Type getType() { return TYPE; }	 
	
	public static final Log log = Log.getLog(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());
}
