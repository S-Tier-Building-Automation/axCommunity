package org.axcommunity.niagara.conversion;

import javax.baja.agent.BPxView;
import javax.baja.naming.BOrd;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

//*****************************************************
/**
* @author Eric Bishop
* @version 1.0
* @date 2014-03-21
* 
* Sets a PXView slot using an ord input.  Allows the
* user to specify a custom view for each folder or
* object.
* */
//*****************************************************
public class BOrdToPxView extends BComponent
{
  public static final Property inPxFile = newProperty(Flags.SUMMARY, BOrd.NULL,BFacets.make(BFacets.TARGET_TYPE, "file:PxFile"));
  public BOrd getInPxFile() {return (BOrd)get(inPxFile);}
  public void setInPxFile(BOrd v) {set(inPxFile,v,null);}
  
  public static final Property outPxView = newProperty(Flags.OPERATOR|Flags.SUMMARY, new BPxView());
  public BPxView getOutPxView() { return (BPxView)get(outPxView); }
  public void setOutPxView(BPxView v) {set(outPxView,v);}
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BOrdToPxView.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
  
  public void changed(Property p, Context cx)
  {
    if(!Sys.atSteadyState() || !isRunning() || !p.equals(inPxFile)) return;
    getOutPxView().setPxFile(getInPxFile());
  }
}
