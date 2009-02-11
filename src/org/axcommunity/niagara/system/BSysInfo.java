package org.axcommunity.niagara.system;

import javax.baja.file.BFileSystem;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.BRelTime;
import javax.baja.sys.Clock;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

import com.tridium.platform.BSystemPlatformService;

/**
 * Executes every executePeriod and displays current system information.
 *
 * @author    Mike Arnott, Kors Engineering
 */

public class BSysInfo extends BComponent  
{

	private BSystemPlatformService sysObject = new BSystemPlatformService();

	private BFileSystem fs = BFileSystem.INSTANCE;

	
	public static final Property executePeriod = newProperty(Flags.SUMMARY,  BRelTime.make(60000));
	public BRelTime getExecutePeriod() { return (BRelTime)get("executePeriod"); }
	public void setExecutePeriod(javax.baja.sys.BRelTime v) { set("executePeriod", v); }
	//events for the timer
	/**
	 * Slot for the <code>timerExpired</code> action.
	 */
	public static final Action timerExpired = newAction(Flags.HIDDEN,null);

	/**
	 * Invoke the <code>timerExpired</code> action.
	 */
	public void timerExpired() { invoke(timerExpired,null,null); }

	public void started(){
		updateTimer();
	    sysObject =(BSystemPlatformService)Sys.getService(BSystemPlatformService.TYPE);
	}
	


	public void doTimerExpired() throws Exception 
	{
		
	
		getCpuUsage().setValue((double)sysObject.getCurrentCpuUsage());
		getBajaHome().setValue( sysObject.getBajaHome());
		getFreePhysicalMemory().setValue((double)sysObject.getFreePhysicalMemory());
		getStationHostId().setValue(sysObject.getHostId());
		getJavaVmName().setValue(sysObject.getJavaVmName());
		getJavaVmVersion().setValue(sysObject.getJavaVmVersion());
		getLocale().setValue(sysObject.getLocale());
		getNiagaraVersion().setValue(sysObject.getNiagaraVersion());
		getOsArch().setValue(sysObject.getOsArch());
		getOsVersion().setValue(sysObject.getOsVersion());
		getOverallCpuUsage().setValue((double)sysObject.getOverallCpuUsage());
		getPlatformServiceDescription().setValue(sysObject.getPlatformServiceDescription());
		getStationName().setValue(sysObject.getStationName());
		getTotalPhysicalMemory().setValue((double)sysObject.getTotalPhysicalMemory());
		
	}
	void updateTimer()
	{            
		if (ticket != null) ticket.cancel();
		ticket = Clock.schedulePeriodically(this, getExecutePeriod(), timerExpired, null);
	}    

	Clock.Ticket ticket;
	long lastOnExecuteTicks;


	/***/
    public static final Property bajaHome = newProperty(Flags.SUMMARY, new BStatusString());
    public void setBajaHome(BStatusString v) { set(bajaHome, v); }
	public BStatusString getBajaHome() { 
		return (BStatusString)get(bajaHome); 
	}
	
	

	
	/***/
    public static final Property stationHostID = newProperty(Flags.SUMMARY, new BStatusString());
    public void setStationHostId(BStatusString v) { set(stationHostID, v); }
	public BStatusString getStationHostId() { 
		return (BStatusString)get(stationHostID); 
	}

	/***/
    public static final Property javaVmName = newProperty(Flags.SUMMARY, new BStatusString());
    public void setJavaVmName(BStatusString v) { set(javaVmName, v); }
	public BStatusString getJavaVmName() { 
		return (BStatusString)get(javaVmName); 
	}
	
	/***/
    public static final Property javaVmVersion = newProperty(Flags.SUMMARY, new BStatusString());
    public void setJavaVmVersion(BStatusString v) { set(javaVmVersion, v); }
	public BStatusString getJavaVmVersion() { 
		return (BStatusString)get(javaVmVersion); 
	}

	
	/***/
    public static final Property locale = newProperty(Flags.SUMMARY, new BStatusString());
    public void setLocale(BStatusString v) { set(locale, v); }
	public BStatusString getLocale() { 
		return (BStatusString)get(locale); 
	}

	/***/
    public static final Property niagaraVersion = newProperty(Flags.SUMMARY, new BStatusString());
    public void setNiagaraVersion(BStatusString v) { set(niagaraVersion, v); }
	public BStatusString getNiagaraVersion() { 
		return (BStatusString)get(niagaraVersion); 
	}

	/***/
    public static final Property osArch = newProperty(Flags.SUMMARY, new BStatusString());
    public void setOsArch(BStatusString v) { set(osArch, v); }
	public BStatusString getOsArch() { 
		return (BStatusString)get(osArch); 
	}

	/***/
    public static final Property osVersion = newProperty(Flags.SUMMARY, new BStatusString());
    public void setOsVersion(BStatusString v) { set(osVersion, v); }
	public BStatusString getOsVersion() { 
		return (BStatusString)get(osVersion); 
	}

	/***/
    public static final Property platformServiceDescription = newProperty(Flags.SUMMARY, new BStatusString());
    public void setPlatformServiceDescription(BStatusString v) { set(platformServiceDescription, v); }
	public BStatusString getPlatformServiceDescription() { 
		return (BStatusString)get(platformServiceDescription); 
	}
	
	/***/
    public static final Property stationName = newProperty(Flags.SUMMARY, new BStatusString());
    public void setStationName(BStatusString v) { set(stationName, v); }
	public BStatusString getStationName() { 
		return (BStatusString)get(stationName); 
	}

	/***/
    public static final Property cpuUsage = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public void setCpuUsage(BStatusNumeric v) { set(cpuUsage, v); }
	public BStatusNumeric getCpuUsage() { 
		return (BStatusNumeric)get(cpuUsage); 
	}

	/***/
    public static final Property totalPhysicalMemory = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public void setTotalPhysicalMemory(BStatusNumeric v) { set(totalPhysicalMemory, v); }
	public BStatusNumeric getTotalPhysicalMemory() { 
		return (BStatusNumeric)get(totalPhysicalMemory); 
	}

	/***/
    public static final Property freePhysicalMemory = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public void setFreePhysicalMemory(BStatusNumeric v) { set(freePhysicalMemory, v); }
	public BStatusNumeric getFreePhysicalMemory() { 
		return (BStatusNumeric)get(freePhysicalMemory); 
	}

	
	/***/
    public static final Property overallCpuUsage = newProperty(Flags.SUMMARY, new BStatusNumeric());
    public void setOverallCpuUsage(BStatusNumeric v) { set(overallCpuUsage, v); }
	public BStatusNumeric getOverallCpuUsage() { 
		return (BStatusNumeric)get(overallCpuUsage); 
	}


	
	
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	    
    public static final Type TYPE = Sys.loadType(BSysInfo.class);
    public Type getType() { return TYPE; }   
	
	
}
