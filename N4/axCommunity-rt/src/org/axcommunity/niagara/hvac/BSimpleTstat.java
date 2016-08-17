
package org.axcommunity.niagara.hvac;

import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.units.UnitDatabase;
/**
 *
 */
public class BSimpleTstat extends BComponent
{ 
    public static final Property InFacets = newProperty(0, BFacets.makeNumeric(UnitDatabase.getUnit("celsius"), 1), null);
    public BFacets getInFacets() { return (BFacets)get(InFacets); }
    public void setInFacets(BFacets v) { set(InFacets, v, null); }
    
    public static final Property OutFacets = newProperty(0, BFacets.makeBoolean(), null);
    public BFacets getOutFacets() { return (BFacets)get(OutFacets); }
    public void setOutFacets(BFacets v) { set(OutFacets, v, null); }

    public static final Property out = newProperty(Flags.TRANSIENT | Flags.SUMMARY | Flags.READONLY, new BStatusBoolean(), null);
    public BStatusBoolean getOut() { return (BStatusBoolean)get(out); }
    public void setOut(BStatusBoolean v) { set(out, v, null); }

    public static final Property cv = newProperty(Flags.SUMMARY, new BStatusNumeric(), null);
    public BStatusNumeric getCv() { return (BStatusNumeric)get(cv); }
    public void setCv(BStatusNumeric v) { set(cv, v, null); }
    
    public static final Property sp = newProperty(Flags.SUMMARY, new BStatusNumeric(), null);
    public BStatusNumeric getSp() { return (BStatusNumeric)get(sp); }
    public void setSp(BStatusNumeric v) { set(sp, v, null); }

    public static final Property diff = newProperty(0, new BStatusNumeric(), null);
    public BStatusNumeric getDiff() { return (BStatusNumeric)get(diff); }
    public void setDiff(BStatusNumeric v) { set(diff, v, null); }

    public static final Property action = newProperty(0, true, BFacets.makeBoolean("Direct", "Reverse"));
    public boolean getAction() { return getBoolean(action); } 
    public void setAction(boolean v) { setBoolean(action, v); }

    public static final Type TYPE = Sys.loadType(BSimpleTstat.class);
    public Type getType() { return TYPE; }
    
    public void started()
    {
        if(!isRunning())
            return;
        else
          getOut().setValue( doCalculate() );
    }

    public void changed(Property property, Context context)
    {  
        if(!isRunning())
            return;
        if(property.equals(cv) || property.equals(sp) || property.equals(diff) || property.equals(action))
          getOut().setValue( doCalculate() );
    }

    public boolean doCalculate()
    {
        double threshhold = getSp().getValue() + ((getOut().getValue()?0:getDiff().getValue()) * (getAction()?1:-1));
        boolean outValue = (getCv().getValue() > threshhold);
        return getAction()?outValue:!outValue;
    }

    public BFacets getSlotFacets(Slot slot)
    {   
        if(slot == out)
             return getOutFacets();
        else if(slot == cv || slot == sp || slot == diff)
             return getInFacets();     
        else
            return super.getSlotFacets(slot);
    }
    
    public BSimpleTstat()
    { 
    }
    
    public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/Ronin16.png");

}

