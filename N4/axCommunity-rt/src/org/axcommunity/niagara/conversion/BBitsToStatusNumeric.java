package org.axcommunity.niagara.conversion;

import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.*;

/**
 * Creates a numeric output from 8 binary bits in
 * @author Mike Arnott, Kors Engineering
 */


public class BBitsToStatusNumeric  extends BComponent {
	public void changed(Property property, Context context){
		super.changed(property, context);
		if(!Sys.atSteadyState() || !isRunning()){
			return;
		}

		if (property.getName().startsWith("in")){
			double temp = 0;
			if(getIn00().getValue())
			{
				temp += 1;
			}
			if(getIn01().getValue())
			{
				temp += 2;
			}
			if(getIn02().getValue())
			{
				temp += 4;
			}
			if(getIn03().getValue())
			{
				temp += 8;
			}
			if(getIn04().getValue())
			{
				temp += 16;
			}
			if(getIn05().getValue())
			{
				temp += 32;
			}
			if(getIn06().getValue())
			{
				temp += 64;
			}
			if(getIn07().getValue())
			{
				temp += 128;
			}
			getOut().setValue(temp);

		}
	}
	/**Bit 00*/
    public static final Property in00 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getIn00() { return (BStatusBoolean)get(in00); }
    public void setIn00(BStatusBoolean v) { set(in00, v); }

	/**Bit 01*/
    public static final Property in01 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getIn01() { return (BStatusBoolean)get(in01); }
    public void setIn01(BStatusBoolean v) { set(in01, v); }

	/**Bit 02*/
    public static final Property in02 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getIn02() { return (BStatusBoolean)get(in02); }
    public void setIn02(BStatusBoolean v) { set(in02, v); }

	/**Bit 03*/
    public static final Property in03 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getIn03() { return (BStatusBoolean)get(in03); }
    public void setIn03(BStatusBoolean v) { set(in03, v); }

	/**Bit 04*/
    public static final Property in04 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getIn04() { return (BStatusBoolean)get(in04); }
    public void setIn04(BStatusBoolean v) { set(in04, v); }

	/**Bit 05*/
    public static final Property in05 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getIn05() { return (BStatusBoolean)get(in05); }
    public void setIn05(BStatusBoolean v) { set(in05, v); }

    /**Bit 06*/
    public static final Property in06 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getIn06() { return (BStatusBoolean)get(in06); }
    public void setIn06(BStatusBoolean v) { set(in06, v); }
	/**Bit 07*/
    public static final Property in07 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public BStatusBoolean getIn07() { return (BStatusBoolean)get(in07); }
    public void setIn07(BStatusBoolean v) { set(in07, v); }

    /**Status Numeric Output*/
    public static final Property out = newProperty(Flags.SUMMARY, new BStatusNumeric(0));
    public BStatusNumeric getOut() { return (BStatusNumeric)get(out); }
    public void setOut(BStatusNumeric v) { set(out, v); }

    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
    
    public static final Type TYPE = Sys.loadType(BBitsToStatusNumeric.class);
    public Type getType() { return TYPE; }

    
}
