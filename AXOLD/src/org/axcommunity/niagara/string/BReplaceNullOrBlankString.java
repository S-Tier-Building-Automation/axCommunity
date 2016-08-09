package org.axcommunity.niagara.string;

import javax.baja.status.BStatus;
import javax.baja.status.BStatusString;
import javax.baja.status.BStatusValue;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

public class BReplaceNullOrBlankString extends BComponent
{
  public static final Property facets = newProperty(0, BFacets.DEFAULT);
  public BFacets getFacets() { return (BFacets)get(facets); }
  public void setFacets(BFacets v) { set(facets,v,null); }
  
  public static final Property in = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.nullStatus),BFacets.make(BFacets.MULTI_LINE, true));
  public BStatusString getIn() { return (BStatusString)get(in); }
  public void setIn(BStatusString v) { set(in, v); }
  
  public static final Property replacementString = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.nullStatus),BFacets.make(BFacets.MULTI_LINE, true));
  public BStatusString getReplacementString() { return (BStatusString)get(replacementString); }
  public void setReplacementString(BStatusString v) { set(replacementString, v); }
  
  public static final Property out = newProperty(Flags.SUMMARY, new BStatusString("", BStatus.nullStatus),BFacets.make(BFacets.MULTI_LINE, true));
  public BStatusString getOut() { return (BStatusString)get(out); }
  public void setOut(BStatusString v) { set(out, v); }

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BReplaceNullOrBlankString.class);

  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/EB.png");
  
  
  public void started() throws Exception
  {
    myCode();
  }
  
  public void changed(Property p, Context cx)
  {
    if(p == in || p == replacementString) myCode();
  }
  
  public void myCode()
  {
    if(getIn().getStatus().isValid() && getIn().getValue().length() > 0)
    {
      getOut().setStatus(0);
      getOut().setValue(getIn().getValue());
    }
    else
    {
      getOut().setStatus(getReplacementString().getStatus());
      getOut().setValue(getReplacementString().getValue());
    }
  }
  public BFacets getSlotFacets(Slot slot) { return super.getSlotFacets(slot); }
  public BStatus getStatus() { return getOut().getStatus(); }
  public BStatusValue getStatusValue() { return getOut().getStatusValue(); }
  public BFacets getStatusValueFacets() { return getFacets(); }
  public BValue getActionParameterDefault(Action action) { return super.getActionParameterDefault(action); }
}
