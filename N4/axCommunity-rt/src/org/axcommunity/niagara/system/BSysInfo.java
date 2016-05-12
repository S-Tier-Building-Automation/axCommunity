package org.axcommunity.niagara.system;

import com.tridium.platform.BSystemPlatformService;

import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;
import java.net.InetAddress;


/**
 * Executes every executePeriod and displays current system information.
 *
 * @author    Mike Arnott, Kors Engineering
 */

public class BSysInfo extends BComponent  
{

  private BSystemPlatformService sysObject = new BSystemPlatformService();

  public static final Action Update = newAction(Flags.SUMMARY|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
  public void Update(){invoke(Update,null,null);}
  public void doUpdate()
  {
    calculate();
  }


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
    calculate();
  }
  void updateTimer()
  {            
    if (ticket != null) ticket.cancel();
    ticket = Clock.schedulePeriodically(this, getExecutePeriod(), timerExpired, null);
  }    

  Clock.Ticket ticket;
  long lastOnExecuteTicks;

  public void calculate()
  {
    getCpuUsage().setValue((double)       sysObject.getCurrentCpuUsage());
    getBajaHome().setValue(           sysObject.getBajaHome());
    getFreePhysicalMemory().setValue((double) sysObject.getFreePhysicalMemory());
    // getStationHostId().setValue(       sysObject.getHostId());
    getJavaVmName().setValue(         sysObject.getJavaVmName());
    getJavaVmVersion().setValue(        sysObject.getJavaVmVersion());
    getLocale().setValue(           sysObject.getLocale());
    getNiagaraVersion().setValue(       sysObject.getNiagaraVersion());
    getOsArch().setValue(           sysObject.getOsArch());
    getOsName().setValue(           sysObject.getOsName());
    getOsVersion().setValue(          sysObject.getOsVersion());
    getOverallCpuUsage().setValue((double)    sysObject.getOverallCpuUsage());
    getPlatformServiceDescription().setValue( sysObject.getPlatformServiceDescription());
    getStationName().setValue(          sysObject.getStationName());
    getTotalPhysicalMemory().setValue((double)  sysObject.getTotalPhysicalMemory());




    long totalMem = Runtime.getRuntime().totalMemory()/1024;
    long freeMem = Runtime.getRuntime().freeMemory()/1024;
    long usedMem = totalMem - freeMem;

    getTotalHeap().setValue(totalMem);
    getUsedHeap().setValue(usedMem);
    getFreeHeap().setValue(freeMem);

    new InetInfo().start();
  }


  class InetInfo extends Thread{
    public void run(){
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
  public static final Property bajaHome = newProperty(Flags.SUMMARY, new BStatusString());
  public void setBajaHome(BStatusString v) { set(bajaHome, v); }
  public BStatusString getBajaHome() {return (BStatusString)get(bajaHome);}

  /***/
  //    public static final Property stationHostID = newProperty(Flags.SUMMARY, new BStatusString());
  //    public void setStationHostId(BStatusString v) { set(stationHostID, v); }
  //  public BStatusString getStationHostId() { 
  //    return (BStatusString)get(stationHostID); 
  //  }

  /***/
  public static final Property javaVmName = newProperty(Flags.SUMMARY, new BStatusString());
  public void setJavaVmName(BStatusString v) { set(javaVmName, v); }
  public BStatusString getJavaVmName() {return (BStatusString)get(javaVmName);}

  /***/
  public static final Property javaVmVersion = newProperty(Flags.SUMMARY, new BStatusString());
  public void setJavaVmVersion(BStatusString v) { set(javaVmVersion, v); }
  public BStatusString getJavaVmVersion() {return (BStatusString)get(javaVmVersion);}


  /***/
  public static final Property locale = newProperty(Flags.SUMMARY, new BStatusString());
  public void setLocale(BStatusString v) { set(locale, v); }
  public BStatusString getLocale() {return (BStatusString)get(locale);}

  /***/
  public static final Property niagaraVersion = newProperty(Flags.SUMMARY, new BStatusString());
  public void setNiagaraVersion(BStatusString v) { set(niagaraVersion, v); }
  public BStatusString getNiagaraVersion() {return (BStatusString)get(niagaraVersion);}

  /***/
  public static final Property osArch = newProperty(Flags.SUMMARY, new BStatusString());
  public void setOsArch(BStatusString v) { set(osArch, v); }
  public BStatusString getOsArch() {return (BStatusString)get(osArch);}

  /***/
  public static final Property osName = newProperty(Flags.SUMMARY, new BStatusString());
  public void setOsName(BStatusString v) { set(osName, v); }
  public BStatusString getOsName() {return (BStatusString)get(osName);}

  /***/
  public static final Property osVersion = newProperty(Flags.SUMMARY, new BStatusString());
  public void setOsVersion(BStatusString v) { set(osVersion, v); }
  public BStatusString getOsVersion() {return (BStatusString)get(osVersion);}

  /***/
  public static final Property platformServiceDescription = newProperty(Flags.SUMMARY, new BStatusString());
  public void setPlatformServiceDescription(BStatusString v) { set(platformServiceDescription, v); }
  public BStatusString getPlatformServiceDescription() {return (BStatusString)get(platformServiceDescription);}

  /***/
  public static final Property stationName = newProperty(Flags.SUMMARY, new BStatusString());
  public void setStationName(BStatusString v) { set(stationName, v); }
  public BStatusString getStationName() {return (BStatusString)get(stationName);}

  /***/
  public static final Property ipAddress = newProperty(Flags.SUMMARY, new BStatusString());
  public void setIpAddress(BStatusString v) { set(ipAddress, v); }
  public BStatusString getIpAddress() {return (BStatusString)get(ipAddress);}

  /***/
  public static final Property hostName = newProperty(Flags.SUMMARY, new BStatusString());
  public void setHostName(BStatusString v) { set(hostName, v); }
  public BStatusString getHostName() {return (BStatusString)get(hostName);}

  /***/
  public static final Property domain = newProperty(Flags.SUMMARY, new BStatusString());
  public void setDomain(BStatusString v) { set(domain, v); }
  public BStatusString getDomain() {return (BStatusString)get(domain);}

  /***/
  public static final Property fqdn = newProperty(Flags.SUMMARY, new BStatusString());
  public void setFqdn(BStatusString v) { set(fqdn, v); }
  public BStatusString getFqdn() {return (BStatusString)get(fqdn);}

  /***/
  public static final Property cpuUsage = newProperty(Flags.SUMMARY, new BStatusNumeric());
  public void setCpuUsage(BStatusNumeric v) { set(cpuUsage, v); }
  public BStatusNumeric getCpuUsage() {return (BStatusNumeric)get(cpuUsage);}

  /***/
  public static final Property totalPhysicalMemory = newProperty(Flags.SUMMARY, new BStatusNumeric());
  public void setTotalPhysicalMemory(BStatusNumeric v) { set(totalPhysicalMemory, v); }
  public BStatusNumeric getTotalPhysicalMemory() {return (BStatusNumeric)get(totalPhysicalMemory);}

  /***/
  public static final Property freePhysicalMemory = newProperty(Flags.SUMMARY, new BStatusNumeric());
  public void setFreePhysicalMemory(BStatusNumeric v) { set(freePhysicalMemory, v); }
  public BStatusNumeric getFreePhysicalMemory() {return (BStatusNumeric)get(freePhysicalMemory);}

  /***/
  public static final Property overallCpuUsage = newProperty(Flags.SUMMARY, new BStatusNumeric());
  public void setOverallCpuUsage(BStatusNumeric v) { set(overallCpuUsage, v); }
  public BStatusNumeric getOverallCpuUsage() {return (BStatusNumeric)get(overallCpuUsage);}

  public static final Property usedHeap = newProperty(Flags.SUMMARY, new BStatusNumeric()); 
  public BStatusNumeric getUsedHeap() { return (BStatusNumeric)get(usedHeap); }
  public void setUsedHeap(javax.baja.status.BStatusNumeric v) { set(usedHeap, v); }

  public static final Property totalHeap = newProperty(Flags.SUMMARY, new BStatusNumeric()); 
  public BStatusNumeric getTotalHeap() { return (BStatusNumeric)get(totalHeap); }
  public void setTotalHeap(javax.baja.status.BStatusNumeric v) { set(totalHeap, v); }

  public static final Property freeHeap = newProperty(Flags.SUMMARY, new BStatusNumeric()); 
  public BStatusNumeric getFreeHeap() { return (BStatusNumeric)get(freeHeap); }
  public void setFreeHeap(javax.baja.status.BStatusNumeric v) { set(freeHeap, v); }


  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

  public static final Type TYPE = Sys.loadType(BSysInfo.class);
  public Type getType() { return TYPE; }   


}
