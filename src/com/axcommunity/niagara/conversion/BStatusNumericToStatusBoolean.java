package com.axcommunity.niagara.conversion;

import javax.baja.status.*;
import javax.baja.sys.*;

/**Converts a StatusNumeric input to a 16 bit word represented
* by 16 StatusBoolean outputs
* @author Mike Arnott, Kors Engineering
*/
public class BStatusNumericToStatusBoolean extends BComponent{

    public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!Sys.atSteadyState() || !isRunning()){
    		return;
    	}

        String binStr = "";
        double getNum;
        int castNum;

        if (property == numberIn) {
            //Convert decimal to binary
            getNum = getNumberIn().getValue();
            if (getNum <= 65535 && getNum >= 0) {
                castNum = (int)getNum;
                binStr = toBinaryString(castNum);
            }

            setStringBinary(new BStatusString(binStr));
            
            setBinaryOut00(new BStatusBoolean((binStr.charAt(15) == '1')));
            setBinaryOut01(new BStatusBoolean((binStr.charAt(14) == '1')));
            setBinaryOut02(new BStatusBoolean((binStr.charAt(13) == '1')));
            setBinaryOut03(new BStatusBoolean((binStr.charAt(12) == '1')));
            setBinaryOut04(new BStatusBoolean((binStr.charAt(11) == '1')));
            setBinaryOut05(new BStatusBoolean((binStr.charAt(10) == '1')));
            setBinaryOut06(new BStatusBoolean((binStr.charAt(9) == '1')));
            setBinaryOut07(new BStatusBoolean((binStr.charAt(8) == '1')));
            setBinaryOut08(new BStatusBoolean((binStr.charAt(7) == '1')));
            setBinaryOut09(new BStatusBoolean((binStr.charAt(6) == '1')));
            setBinaryOut10(new BStatusBoolean((binStr.charAt(5) == '1')));
            setBinaryOut11(new BStatusBoolean((binStr.charAt(4) == '1')));
            setBinaryOut12(new BStatusBoolean((binStr.charAt(3) == '1')));
            setBinaryOut13(new BStatusBoolean((binStr.charAt(2) == '1')));
            setBinaryOut14(new BStatusBoolean((binStr.charAt(1) == '1')));
            setBinaryOut15(new BStatusBoolean((binStr.charAt(0) == '1')));
        }
    }

    //Property Declarations, Gets, and Sets
    //Integer Input
    public static final Property numberIn = newProperty(Flags.SUMMARY, new BStatusNumeric());
    
    /**Binary Outputs*/
    public static final Property binaryOut00 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut01 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut02 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut03 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut04 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut05 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut06 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut07 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut08 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut09 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut10 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut11 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut12 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut13 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut14 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    public static final Property binaryOut15 = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
    
    /**String Output*/
    public static final Property stringBinary = newProperty(Flags.SUMMARY, new BStatusString());
    
    //Get methods
    public BStatusNumeric getNumberIn() { return (BStatusNumeric)get(numberIn); }
    public BStatusBoolean getBinaryOut00() { return (BStatusBoolean)get(binaryOut00); }
    public BStatusBoolean getBinaryOut01() { return (BStatusBoolean)get(binaryOut01); }
    public BStatusBoolean getBinaryOut02() { return (BStatusBoolean)get(binaryOut02); }
    public BStatusBoolean getBinaryOut03() { return (BStatusBoolean)get(binaryOut03); }
    public BStatusBoolean getBinaryOut04() { return (BStatusBoolean)get(binaryOut04); }
    public BStatusBoolean getBinaryOut05() { return (BStatusBoolean)get(binaryOut05); }
    public BStatusBoolean getBinaryOut06() { return (BStatusBoolean)get(binaryOut06); }
    public BStatusBoolean getBinaryOut07() { return (BStatusBoolean)get(binaryOut07); }
    public BStatusBoolean getBinaryOut08() { return (BStatusBoolean)get(binaryOut08); }
    public BStatusBoolean getBinaryOut09() { return (BStatusBoolean)get(binaryOut09); }
    public BStatusBoolean getBinaryOut10() { return (BStatusBoolean)get(binaryOut10); }
    public BStatusBoolean getBinaryOut11() { return (BStatusBoolean)get(binaryOut11); }
    public BStatusBoolean getBinaryOut12() { return (BStatusBoolean)get(binaryOut12); }
    public BStatusBoolean getBinaryOut13() { return (BStatusBoolean)get(binaryOut13); }
    public BStatusBoolean getBinaryOut14() { return (BStatusBoolean)get(binaryOut14); }
    public BStatusBoolean getBinaryOut15() { return (BStatusBoolean)get(binaryOut15); }
    public BStatusString getStringBinary() { return (BStatusString)get(stringBinary); }

    //Set Methods
    public void setNumberIn(BStatusNumeric v) { set(numberIn, v); }
    public void setBinaryOut00(BStatusBoolean v) { set(binaryOut00, v); }
    public void setBinaryOut01(BStatusBoolean v) { set(binaryOut01, v); }
    public void setBinaryOut02(BStatusBoolean v) { set(binaryOut02, v); }
    public void setBinaryOut03(BStatusBoolean v) { set(binaryOut03, v); }
    public void setBinaryOut04(BStatusBoolean v) { set(binaryOut04, v); }
    public void setBinaryOut05(BStatusBoolean v) { set(binaryOut05, v); }
    public void setBinaryOut06(BStatusBoolean v) { set(binaryOut06, v); }
    public void setBinaryOut07(BStatusBoolean v) { set(binaryOut07, v); }
    public void setBinaryOut08(BStatusBoolean v) { set(binaryOut08, v); }
    public void setBinaryOut09(BStatusBoolean v) { set(binaryOut09, v); }
    public void setBinaryOut10(BStatusBoolean v) { set(binaryOut10, v); }
    public void setBinaryOut11(BStatusBoolean v) { set(binaryOut11, v); }
    public void setBinaryOut12(BStatusBoolean v) { set(binaryOut12, v); }
    public void setBinaryOut13(BStatusBoolean v) { set(binaryOut13, v); }
    public void setBinaryOut14(BStatusBoolean v) { set(binaryOut14, v); }
    public void setBinaryOut15(BStatusBoolean v) { set(binaryOut15, v); }
    public void setStringBinary(BStatusString v) { set(stringBinary, v); }
    
    /**Converts binary value to a string representation of the bit word*/
    public String toBinaryString(int numberIn) {
        String binStr = Integer.toBinaryString(numberIn);
        if (binStr.length() < 16) { //pad to 16 characters
            int charsToPad = 16 - binStr.length();
            char [] buf = new char[charsToPad];
            for (int i = 0; i < charsToPad; i++) {
                buf[i] = '0';
            }
            binStr = new String(buf) + binStr;
        }
        return binStr;
    }

    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("local:|module://axCommunity/com/axcommunity/niagara/graphics/korsLogo.png");

    public static final Type TYPE = Sys.loadType(BStatusNumericToStatusBoolean.class);
    public Type getType() { return TYPE; }
}