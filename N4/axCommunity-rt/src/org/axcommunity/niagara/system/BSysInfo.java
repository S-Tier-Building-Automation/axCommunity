package org.axcommunity.niagara.system;

import java.net.InetAddress;

import javax.baja.status.BStatus;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BInteger;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BStation;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

import com.tridium.platform.BSystemPlatformService;


/**
 * Executes every executePeriod and displays current system information.
 *
 * @author		Mike Arnott, Kors Engineering
 */

public class BSysInfo extends BComponent	
{

	private	BSystemPlatformService	sysObject			= new BSystemPlatformService();
	private	BStation				station				= new BStation();
	Clock.Ticket					ticket;
	long							lastOnExecuteTicks;
	
	
	/*----------------------------------------------------------------------------------------------------------*/
	public static final Property facetsNumerics = newProperty(0, BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)), null);
	public BFacets getFacetsNumerics() { return (BFacets)get(facetsNumerics); }
	public void setFacetsNumerics(BFacets v) { set(facetsNumerics,v,null); }
	
	public static final Property facetsStrings = newProperty(0, BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)), null);
	public BFacets getFacetsStrings() { return (BFacets)get(facetsStrings); }
	public void setFacetsStrings(BFacets v) { set(facetsStrings,v,null); }
	
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

	public static final Property executePeriod = newProperty(Flags.SUMMARY,	BRelTime.make(60000));
	public BRelTime getExecutePeriod() { return (BRelTime)get("executePeriod"); }
	public void setExecutePeriod(javax.baja.sys.BRelTime v) { set("executePeriod", v); }
	
	//events for the timer
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);
	public void timerExpired() { invoke(timerExpired,null,null); }

	public void started()
	{
		updateTimer();
		sysObject =(BSystemPlatformService)Sys.getService(BSystemPlatformService.TYPE);
		station=Sys.getStation();
	}

	public void changed(Property p, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning()) return;
		if(p.equals(executePeriod))
		{
			updateTimer();
			calculate();
		}
	}
	
	public void doTimerExpired() throws Exception 
	{
		calculate();
	}
	
	void updateTimer()
	{						
		if (ticket != null) ticket.cancel();
		ticket = Clock.schedulePeriodically(this, getExecutePeriod(), timerExpired, null);
	}		

	public void calculate()
	{
		getCpuUsage().setValue((double)				sysObject.getCurrentCpuUsage());
		getBajaHome().setValue(						sysObject.getBajaHome());
		getFreePhysicalMemory().setValue((double)	sysObject.getFreePhysicalMemory());
		// getStationHostId().setValue(				sysObject.getHostId());
		getJavaVmName().setValue(					sysObject.getJavaVmName());
		getJavaVmVersion().setValue(				sysObject.getJavaVmVersion());
		getLocale().setValue(						sysObject.getLocale());
		getNiagaraVersion().setValue(				sysObject.getNiagaraVersion());
		getOsArch().setValue(						sysObject.getOsArch());
		getOsName().setValue(						sysObject.getOsName());
		getOsVersion().setValue(					sysObject.getOsVersion());
		getOverallCpuUsage().setValue((double)		sysObject.getOverallCpuUsage());
		getPlatformServiceDescription().setValue(	sysObject.getPlatformServiceDescription());
		getStationName().setValue(					sysObject.getStationName());
		getTotalPhysicalMemory().setValue((double)	sysObject.getTotalPhysicalMemory()); //INT VALUE

		long	totalMem	= Runtime.getRuntime().totalMemory()/1024;
		long	freeMem		= Runtime.getRuntime().freeMemory()/1024;
		long	usedMem		= totalMem - freeMem;

		getTotalHeap().setValue(totalMem);
		getUsedHeap().setValue(usedMem);
		getFreeHeap().setValue(freeMem);

		try{getNiagaraHome().setValue(Sys.getNiagaraHome().toString());}
		catch (Exception e){getNiagaraHome().setValue("");}
		
		try{getNiagaraUserHome().setValue(Sys.getNiagaraUserHome().toString());}
		catch (Exception e){getNiagaraUserHome().setValue("");}
		
		try{getProtectedStationHome().setValue(Sys.getProtectedStationHome().toString());}
		catch (Exception e){getProtectedStationHome().setValue("");}
		
		try{getStationHome().setValue(Sys.getStationHome().toString());}
		catch (Exception e){getStationHome().setValue("");}
		
		new InetInfo().start();
	}


	class InetInfo extends Thread
	{
		public void run()
		{
			try 
			{
				getHostName().setValue(InetAddress.getLocalHost().getHostName());
			} 
			catch (Exception e)
			{
				getHostName().setValue("");
			} 

			try
			{
				getFqdn().setValue(InetAddress.getLocalHost().getCanonicalHostName());
			} 
			catch (Exception e)
			{
				getFqdn().setValue("");
			} 

			try 
			{
				getDomain().setValue(InetAddress.getLocalHost().getCanonicalHostName().substring(InetAddress.getLocalHost().getHostName().length() + 1));
			} 
			catch (Exception e)
			{
				getDomain().setValue("");
			} 

			try 
			{
				getIpAddress().setValue(InetAddress.getLocalHost().getHostAddress());
			} 
			catch (Exception e)
			{
				getIpAddress().setValue("");
			} 
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
		System.out.println("\nA Station Save Has Been Invoked From:\t\t" + getSlotPath());
		station.save();
	}
	
	/** 
	 * This will cause the station to restart just as if you did it from the application director.
	 * The "CONFIRM_REQUIRED" flag is set by default but when used from logic this confirmation isn't required (that I can tell at least).
	 */
	public static final Action RestartStation = newAction(0|Flags.ASYNC|Flags.CONFIRM_REQUIRED, null, null);
	public void RestartStation(){invoke(RestartStation,null,null);}
	public void doRestartStation()
	{
		System.out.println("\r\nA Station Restart Has Been Invoked From:\t\t" + getSlotPath());
		sysObject.lease(1);
		sysObject.invoke(BSystemPlatformService.restartStation, null);
	}

	/** 
	 * This will cause the station to reboot (entire computer\platform) just as if you did it from the application director
	 * The "CONFIRM_REQUIRED" flag is set by default but when used from logic this confirmation isn't required (that I can tell at least).
	 */
	public static final Action RebootStation = newAction(0|Flags.ASYNC|Flags.CONFIRM_REQUIRED, null, null);
	public void RebootStation(){invoke(RebootStation,null,null);}
	public void doRebootStation()
	{
		System.out.println("\r\nA Station Reboot Has Been Invoked From:\t\t" + getSlotPath());
		sysObject.lease(1);
		sysObject.invoke(BSystemPlatformService.reboot, null);
	}

	/***/
	public static final Property bajaHome = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setBajaHome(BStatusString v) { set(bajaHome, v); }
	public BStatusString getBajaHome() {return (BStatusString)get(bajaHome);}

	/***/
	//		public static final Property stationHostID = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	//		public void setStationHostId(BStatusString v) { set(stationHostID, v); }
	//	public BStatusString getStationHostId() { 
	//		return (BStatusString)get(stationHostID); 
	//	}

	/***/
	public static final Property javaVmName = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setJavaVmName(BStatusString v) { set(javaVmName, v); }
	public BStatusString getJavaVmName() {return (BStatusString)get(javaVmName);}

	/***/
	public static final Property javaVmVersion = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setJavaVmVersion(BStatusString v) { set(javaVmVersion, v); }
	public BStatusString getJavaVmVersion() {return (BStatusString)get(javaVmVersion);}

	/***/
	public static final Property locale = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setLocale(BStatusString v) { set(locale, v); }
	public BStatusString getLocale() {return (BStatusString)get(locale);}

	/***/
	public static final Property niagaraVersion = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setNiagaraVersion(BStatusString v) { set(niagaraVersion, v); }
	public BStatusString getNiagaraVersion() {return (BStatusString)get(niagaraVersion);}

	/***/
	public static final Property osArch = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setOsArch(BStatusString v) { set(osArch, v); }
	public BStatusString getOsArch() {return (BStatusString)get(osArch);}

	/***/
	public static final Property osName = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setOsName(BStatusString v) { set(osName, v); }
	public BStatusString getOsName() {return (BStatusString)get(osName);}

	/***/
	public static final Property osVersion = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setOsVersion(BStatusString v) { set(osVersion, v); }
	public BStatusString getOsVersion() {return (BStatusString)get(osVersion);}

	/***/
	public static final Property platformServiceDescription = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setPlatformServiceDescription(BStatusString v) { set(platformServiceDescription, v); }
	public BStatusString getPlatformServiceDescription() {return (BStatusString)get(platformServiceDescription);}

	/***/
	public static final Property stationName = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setStationName(BStatusString v) { set(stationName, v); }
	public BStatusString getStationName() {return (BStatusString)get(stationName);}

	/***/
	public static final Property ipAddress = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setIpAddress(BStatusString v) { set(ipAddress, v); }
	public BStatusString getIpAddress() {return (BStatusString)get(ipAddress);}

	/***/
	public static final Property hostName = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setHostName(BStatusString v) { set(hostName, v); }
	public BStatusString getHostName() {return (BStatusString)get(hostName);}

	/***/
	public static final Property domain = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setDomain(BStatusString v) { set(domain, v); }
	public BStatusString getDomain() {return (BStatusString)get(domain);}

	/***/
	public static final Property fqdn = newProperty(Flags.SUMMARY, new BStatusString(), BFacets.make(BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setFqdn(BStatusString v) { set(fqdn, v); }
	public BStatusString getFqdn() {return (BStatusString)get(fqdn);}

	
	public static final Property niagaraHome = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.DEFAULT), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getNiagaraHome() { return (BStatusString)get(niagaraHome);}
	public void setNiagaraHome(BStatusString v) {set(niagaraHome,v);}
	
	
	public static final Property niagaraUserHome = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.DEFAULT), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getNiagaraUserHome() { return (BStatusString)get(niagaraUserHome);}
	public void setNiagaraUserHome(BStatusString v) {set(niagaraUserHome,v);}
	
	public static final Property protectedStationHome = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.DEFAULT), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getProtectedStationHome() { return (BStatusString)get(protectedStationHome);}
	public void setProtectedStationHome(BStatusString v) {set(protectedStationHome,v);}
	
	public static final Property stationHome = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.DEFAULT), BFacets.make(BFacets.MULTI_LINE, BBoolean.FALSE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusString getStationHome() { return (BStatusString)get(stationHome);}
	public void setStationHome(BStatusString v) {set(stationHome,v);}
	
	/***/
	public static final Property cpuUsage = newProperty(Flags.SUMMARY, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setCpuUsage(BStatusNumeric v) { set(cpuUsage, v); }
	public BStatusNumeric getCpuUsage() {return (BStatusNumeric)get(cpuUsage);}

	/***/
	public static final Property totalPhysicalMemory = newProperty(Flags.SUMMARY, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setTotalPhysicalMemory(BStatusNumeric v) { set(totalPhysicalMemory, v); }
	public BStatusNumeric getTotalPhysicalMemory() {return (BStatusNumeric)get(totalPhysicalMemory);}

	/***/
	public static final Property freePhysicalMemory = newProperty(Flags.SUMMARY, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setFreePhysicalMemory(BStatusNumeric v) { set(freePhysicalMemory, v); }
	public BStatusNumeric getFreePhysicalMemory() {return (BStatusNumeric)get(freePhysicalMemory);}

	/***/
	public static final Property overallCpuUsage = newProperty(Flags.SUMMARY, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public void setOverallCpuUsage(BStatusNumeric v) { set(overallCpuUsage, v); }
	public BStatusNumeric getOverallCpuUsage() {return (BStatusNumeric)get(overallCpuUsage);}

	/***/
	public static final Property usedHeap = newProperty(Flags.SUMMARY, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusNumeric getUsedHeap() { return (BStatusNumeric)get(usedHeap); }
	public void setUsedHeap(javax.baja.status.BStatusNumeric v) { set(usedHeap, v); }

	/***/
	public static final Property totalHeap = newProperty(Flags.SUMMARY, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusNumeric getTotalHeap() { return (BStatusNumeric)get(totalHeap); }
	public void setTotalHeap(javax.baja.status.BStatusNumeric v) { set(totalHeap, v); }

	/***/
	public static final Property freeHeap = newProperty(Flags.SUMMARY, new BStatusNumeric(), BFacets.make(BFacets.PRECISION, BInteger.make(0), BFacets.SHOW_SEPARATORS, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
	public BStatusNumeric getFreeHeap() { return (BStatusNumeric)get(freeHeap); }
	public void setFreeHeap(javax.baja.status.BStatusNumeric v) { set(freeHeap, v); }

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BSysInfo.class);
	public Type getType() { return TYPE; }	 
}
