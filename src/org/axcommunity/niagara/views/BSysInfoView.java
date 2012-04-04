package org.axcommunity.niagara.views;

import javax.baja.converters.BINumericToNumber;
import javax.baja.converters.BObjectToString;
import javax.baja.gx.BSize;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BLabel;
import javax.baja.ui.BLayout;
import javax.baja.ui.BValueBinding;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.enums.BScaleMode;
import javax.baja.ui.enums.BValign;
import javax.baja.ui.pane.BCanvasPane;
import javax.baja.ui.pane.BGridPane;
import javax.baja.util.BFormat;
import javax.baja.workbench.view.*;

import org.axcommunity.niagara.system.BSysInfo;

import com.tridium.kitpx.BAnalogMeter;
import com.tridium.kitpx.BBargraph;
import com.tridium.kitpx.BBoundLabel;

/**
 * Did this as a learning exercise.  Wanted to learn how to create custom
 * BWbComponentView for an object.  This is meant as a rough template for
 * different ways you can do it.  What I have learned is how to properly overload
 * the doLoadValue and create widgets with bindings in it.  Also, learned how to create
 * agents in the module-inlcude.xml file.  This is a pretty rough view - I stink at 
 * graphics.  But, I'm hoping this inspires someone creative to build better object views. 
 *
 * @author    Mike Arnott, Kors Engineering
 */
public class BSysInfoView
    extends BWbComponentView
{
  public BSysInfoView()
  {
  }
  
  
  public void doLoadValue(BObject obj, Context cx)
  {
    //cast incoming obj to a sysinfo object    
    BSysInfo sysInfo = (BSysInfo)obj;

    SlotPath basePath = sysInfo.getSlotPath();
    

    //create the background canvas the objects will show up on
    BCanvasPane canvas = new BCanvasPane();
    canvas.setViewSize(BSize.make(235,195));
    canvas.setScale(BScaleMode.fitRatio);
    

    /*
    make label for station name
    */
    BLabel stationName = new BLabel();
    stationName.setLayout(BLayout.makeAbs(0,0,235,20));
    //make binding
    BValueBinding snameBind = new BValueBinding();
    snameBind.setPopupEnabled(false);
    //make ord from path from path to object, append slot
    SlotPath snamePath =  basePath.merge(new SlotPath("stationName/value"));
    BOrd snameOrd = BOrd.make(sysInfo.getHandleOrd(), snamePath);
    //set binding to ord
    snameBind.setOrd(snameOrd);
    //create BFromat converter
    BObjectToString snameCvt = new BObjectToString();
    snameCvt.setFormat(BFormat.make("Station Name: %.%"));
    //add binding to bound label
    snameBind.add("text",snameCvt);
    stationName.add(null,snameBind);
    //add label to canvas
    canvas.add(null,stationName);
    
    
    /*
    make label for Niagara Version
    */
    BLabel niagaraVer = new BLabel();
    niagaraVer.setLayout(BLayout.makeAbs(0,20,235,20));
    //make binding
    BValueBinding nverBind = new BValueBinding();
    nverBind.setPopupEnabled(false);
    //make ord from path from path to object, append slot
    SlotPath nverPath =  basePath.merge(new SlotPath("niagaraVersion/value"));
    BOrd nverOrd = BOrd.make(sysInfo.getHandleOrd(), nverPath);
    //set binding to ord
    nverBind.setOrd(nverOrd);
    //create BFromat converter
    BObjectToString nverCvt = new BObjectToString();
    nverCvt.setFormat(BFormat.make("Niagra Version: %.%"));
    //add binding to bound label
    nverBind.add("text",nverCvt);
    niagaraVer.add(null,nverBind);
    //add label to canvas
    canvas.add(null,niagaraVer);
    
    
    
    /*
    make label for OS Name
    */
    BLabel osName = new BLabel();
    osName.setLayout(BLayout.makeAbs(0,40,175,20));
    osName.setHalign(BHalign.left);
    //make binding
    BValueBinding osNameBind = new BValueBinding();
    osNameBind.setPopupEnabled(false);
    //make ord from path from path to object, append slot
    SlotPath osNamePath =  basePath.merge(new SlotPath("osName/value"));
    BOrd osNameOrd = BOrd.make(sysInfo.getHandleOrd(), osNamePath);
    //set binding to ord
    osNameBind.setOrd(osNameOrd);
    //create BFromat converter
    BObjectToString osNameCvt = new BObjectToString();
    osNameCvt.setFormat(BFormat.make("OS Name: %.%"));
    //add binding to bound label
    osNameBind.add("text",osNameCvt);
    osName.add(null,osNameBind);
    //add label to canvas
    canvas.add(null,osName);
    
    
    /*
    make label for OS Version
    */
    BLabel osVersion = new BLabel();
    osVersion.setHalign(BHalign.right);
    osVersion.setLayout(BLayout.makeAbs(175,40,60,20));
    //make binding
    BValueBinding osVersionBind = new BValueBinding();
    osVersionBind.setPopupEnabled(false);
    //make ord from path from path to object, append slot
    SlotPath osVersionPath =  basePath.merge(new SlotPath("osVersion/value"));
    BOrd osVersionOrd = BOrd.make(sysInfo.getHandleOrd(), osVersionPath);
    //set binding to ord
    osVersionBind.setOrd(osVersionOrd);
    //create BFromat converter
    BObjectToString osVersionCvt = new BObjectToString();
    osVersionCvt.setFormat(BFormat.make("Ver: %.%"));
    //add binding to bound label
    osVersionBind.add("text",osVersionCvt);
    osVersion.add(null,osVersionBind);
    //add label to canvas
    canvas.add(null,osVersion);
    
    
    /*
     * make analog meter for CPU usage
     */
    BAnalogMeter cpuMeter = new BAnalogMeter();
    cpuMeter.setLayout(BLayout.makeAbs(0,80,95,115));
    cpuMeter.setNumDivisions(10);
    cpuMeter.setMax(100);
    cpuMeter.setText("Percent %");
    BValueBinding cpuBind = new BValueBinding();
    cpuBind.setPopupEnabled(false);
    SlotPath cpuBindPath = basePath.merge(new SlotPath("cpuUsage/value"));
    BOrd cpuBindOrd = BOrd.make(sysInfo.getHandleOrd(),cpuBindPath);
    cpuBind.setOrd(cpuBindOrd);
    BINumericToNumber cpuCvt = new BINumericToNumber();
    cpuBind.add("value",cpuCvt);
    cpuMeter.add(null, cpuBind);
    canvas.add(null,cpuMeter);
    
    
    /*
     * make bargraph for free memory
     */
    BBargraph freeMem = new BBargraph();
    freeMem.setLayout(BLayout.makeAbs(100,80,60,115));
    freeMem.setMax(sysInfo.getTotalPhysicalMemory().getValue());
    freeMem.setText("");
    freeMem.setScale(freeMem.getMax()/8);
    BValueBinding freeMemBind = new BValueBinding();
    freeMemBind.setPopupEnabled(false);
    SlotPath freeMemBindPath = basePath.merge(new SlotPath("freePhysicalMemory/value"));
    BOrd freeMemBindOrd = BOrd.make(sysInfo.getHandleOrd(),freeMemBindPath);
    freeMemBind.setOrd(freeMemBindOrd);
    BINumericToNumber freeMemCvt = new BINumericToNumber();
    freeMemBind.add("value",freeMemCvt);
    freeMem.add(null, freeMemBind);
    canvas.add(null,freeMem);
    
    
    
    /*
     * make bargraph for heap - this one needs two bindings because heap total is a variable!
     */
    BBargraph freeHeap = new BBargraph();
    freeHeap.setLayout(BLayout.makeAbs(165,80,60,115));
    freeHeap.setMax(sysInfo.getTotalHeap().getValue());
    freeHeap.setText("");
    freeHeap.setScale(freeHeap.getMax()/8);
    //free heap binding value
    BValueBinding freeHeapBind = new BValueBinding();
    freeHeapBind.setPopupEnabled(false);
    SlotPath freeHeapBindPath = basePath.merge(new SlotPath("freeHeap/value"));
    BOrd freeHeapBindOrd = BOrd.make(sysInfo.getHandleOrd(),freeHeapBindPath);
    freeHeapBind.setOrd(freeHeapBindOrd);
    BINumericToNumber freeHeapCvt = new BINumericToNumber();
    freeHeapBind.add("value",freeHeapCvt);
    freeHeap.add(null, freeHeapBind);
    //max value binding
    BValueBinding totalHeapBind = new BValueBinding();
    totalHeapBind.setPopupEnabled(false);
    SlotPath totalHeapBindPath = basePath.merge(new SlotPath("totalHeap/value"));
    BOrd totalHeapBindOrd = BOrd.make(sysInfo.getHandleOrd(),totalHeapBindPath);
    totalHeapBind.setOrd(totalHeapBindOrd);
    BINumericToNumber totalHeapCvt = new BINumericToNumber();
    totalHeapBind.add("max",totalHeapCvt);
    freeHeap.add(null, totalHeapBind);
    canvas.add(null,freeHeap);
    
    //add some basic labels
    BLabel cpuLabel = new BLabel();
    cpuLabel.setLayout(BLayout.makeAbs(0,65,95,15));
    cpuLabel.setText("CPU Usage");
    canvas.add(null, cpuLabel);
    
    BLabel ramLabel = new BLabel();
    ramLabel.setLayout(BLayout.makeAbs(100,65,60,15));
    ramLabel.setText("Free RAM");
    canvas.add(null, ramLabel);
    
    BLabel heapLabel = new BLabel();
    heapLabel.setLayout(BLayout.makeAbs(165,65,60,15));
    heapLabel.setText("Free Heap");
    canvas.add(null, heapLabel);  
    
    //finally, set the content for the view
    setContent(canvas);   
  }
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSysInfoView.class);
  
}
