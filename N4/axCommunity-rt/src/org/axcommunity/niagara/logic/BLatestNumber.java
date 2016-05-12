package org.axcommunity.niagara.logic;

import javax.baja.log.Log;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.*;


/**
 * This object accepts up to 24 Status Numeric inputs and will output the latest 
 * value if it meets the minimum required value as configured in the "inMinValue" slot.
 * 
 * I got the idea for this from an the object "LatestString" in the KorsMaster Module
 * that was created by Mike Arnott with Kors Engineering. Thank for the idea.
 * Thank for the idea, I'm sure your code probably looks better than mine though.
 * 
 *
 * @author		Justin Koffler, Texas Power Systems
 * @version		12.02.18
 */



public class BLatestNumber extends BComponent
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	ACTION SLOTS   ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Action ClearOutput = newAction(0|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void ClearOutput(){invoke(ClearOutput,null,null);}
	public void doClearOutput()
	{
		getOutLatestNumber().setValue(0);
		fireLatestNumber(BDouble.make(0));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Latest number received from input slots greater or equal to the minimum allowed value. */
	public static final Property outLatestNumber  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getOutLatestNumber() {return (BStatusNumeric) get(outLatestNumber); }
	public void setOutLatestNumber(BStatusNumeric v) {set(outLatestNumber, v);}
	
	/** BONUS SLOT!! The sum of all input values. */
	public static final Property outSum  = newProperty(0, new BStatusNumeric(0));
	public BStatusNumeric getOutSum() {return (BStatusNumeric) get(outSum); }
	public void setOutSum(BStatusNumeric v) {set(outSum, v);}
	
	/** Timestamp for when 'outLatestString' was last updated. */
	public static final Property outLastOutputChange = newProperty(0, BAbsTime.make(), BFacets.make("showSeconds",true));
	public BAbsTime getOutLastOutputChange() { return (BAbsTime)get(outLastOutputChange); }
	public void setOutLastOutputChange(BAbsTime v) { set(outLastOutputChange, v); }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	TOPIC SLOTS   /////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**NUMERIC TOPIC FIRED*/
	public static final Topic LatestNumber = newTopic(0);
	public void fireLatestNumber(BDouble event){fire(LatestNumber,event,null);}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	INPUTS   //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Minimum input value to be considered for output. */
	public static final Property inMinValue  = newProperty(0, new BStatusNumeric(0));
	public BStatusNumeric getInMinValue() {return (BStatusNumeric) get(inMinValue); }
	public void setInMinValue(BStatusNumeric v) {set(inMinValue, v);}
	
	public static final Property in1  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn1() {return (BStatusNumeric) get(in1); }
	public void setIn1(BStatusNumeric v) {set(in1, v);}
	
	public static final Property in2  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn2() {return (BStatusNumeric) get(in2); }
	public void setIn2(BStatusNumeric v) {set(in2, v);}
	
	public static final Property in3  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn3() {return (BStatusNumeric) get(in3); }
	public void setIn3(BStatusNumeric v) {set(in3, v);}
	
	public static final Property in4  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn4() {return (BStatusNumeric) get(in4); }
	public void setIn4(BStatusNumeric v) {set(in4, v);}
	
	public static final Property in5  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn5() {return (BStatusNumeric) get(in5); }
	public void setIn5(BStatusNumeric v) {set(in5, v);}
	
	public static final Property in6  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn6() {return (BStatusNumeric) get(in6); }
	public void setIn6(BStatusNumeric v) {set(in6, v);}
	
	public static final Property in7  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn7() {return (BStatusNumeric) get(in7); }
	public void setIn7(BStatusNumeric v) {set(in7, v);}
	
	public static final Property in8  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn8() {return (BStatusNumeric) get(in8); }
	public void setIn8(BStatusNumeric v) {set(in8, v);}
	
	public static final Property in9  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn9() {return (BStatusNumeric) get(in9); }
	public void setIn9(BStatusNumeric v) {set(in9, v);}
	
	public static final Property in10  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn10() {return (BStatusNumeric) get(in10); }
	public void setIn10(BStatusNumeric v) {set(in10, v);}
	
	public static final Property in11  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn11() {return (BStatusNumeric) get(in11); }
	public void setIn11(BStatusNumeric v) {set(in11, v);}
	
	public static final Property in12  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn12() {return (BStatusNumeric) get(in12); }
	public void setIn12(BStatusNumeric v) {set(in12, v);}
	
	public static final Property in13  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn13() {return (BStatusNumeric) get(in13); }
	public void setIn13(BStatusNumeric v) {set(in13, v);}
	
	public static final Property in14  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn14() {return (BStatusNumeric) get(in14); }
	public void setIn14(BStatusNumeric v) {set(in14, v);}
	
	public static final Property in15  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn15() {return (BStatusNumeric) get(in15); }
	public void setIn15(BStatusNumeric v) {set(in15, v);}
	
	public static final Property in16  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn16() {return (BStatusNumeric) get(in16); }
	public void setIn16(BStatusNumeric v) {set(in16, v);}
	
	public static final Property in17  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn17() {return (BStatusNumeric) get(in17); }
	public void setIn17(BStatusNumeric v) {set(in17, v);}
	
	public static final Property in18  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn18() {return (BStatusNumeric) get(in18); }
	public void setIn18(BStatusNumeric v) {set(in18, v);}
	
	public static final Property in19  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn19() {return (BStatusNumeric) get(in19); }
	public void setIn19(BStatusNumeric v) {set(in19, v);}
	
	public static final Property in20  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn20() {return (BStatusNumeric) get(in20); }
	public void setIn20(BStatusNumeric v) {set(in20, v);}
	
	public static final Property in21  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn21() {return (BStatusNumeric) get(in21); }
	public void setIn21(BStatusNumeric v) {set(in21, v);}
	
	public static final Property in22  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn22() {return (BStatusNumeric) get(in22); }
	public void setIn22(BStatusNumeric v) {set(in22, v);}
	
	public static final Property in23  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn23() {return (BStatusNumeric) get(in23); }
	public void setIn23(BStatusNumeric v) {set(in23, v);}
	
	public static final Property in24  = newProperty(0|Flags.SUMMARY, new BStatusNumeric(0));
	public BStatusNumeric getIn24() {return (BStatusNumeric) get(in24); }
	public void setIn24(BStatusNumeric v) {set(in24, v);}
	
	
	
	
	//---------------------------------------------------------------------------------------------------------
	//	SOURCE CODE BELOW HERE	-------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**    METHOD INVOKED WHEN ANY OF THE INPUTS CHANGES VALUES   *////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void changed(Property p, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning())return;
		if(isRunning())
		{
			double min	= getInMinValue().getValue();
			
			double n1	= getIn1().getValue();
			double n2	= getIn2().getValue();
			double n3	= getIn3().getValue();
			double n4	= getIn4().getValue();
			double n5	= getIn5().getValue();
			double n6	= getIn6().getValue();
			double n7	= getIn7().getValue();
			double n8	= getIn8().getValue();
			double n9	= getIn9().getValue();
			double n10	= getIn10().getValue();
			double n11	= getIn11().getValue();
			double n12	= getIn12().getValue();
			double n13	= getIn13().getValue();
			double n14	= getIn14().getValue();
			double n15	= getIn15().getValue();
			double n16	= getIn16().getValue();
			double n17	= getIn17().getValue();
			double n18	= getIn18().getValue();
			double n19	= getIn19().getValue();
			double n20	= getIn20().getValue();
			double n21	= getIn21().getValue();
			double n22	= getIn22().getValue();
			double n23	= getIn23().getValue();
			double n24	= getIn24().getValue();
			
			
			if(p == in1 && (n1>=min)){getOutLatestNumber().setValue(n1);fireLatestNumber(BDouble.make(n1));sumAll();}
			if(p == in2 && (n2>=min)){getOutLatestNumber().setValue(n2);fireLatestNumber(BDouble.make(n2));sumAll();}
			if(p == in3 && (n3>=min)){getOutLatestNumber().setValue(n3);fireLatestNumber(BDouble.make(n3));sumAll();}
			if(p == in4 && (n4>=min)){getOutLatestNumber().setValue(n4);fireLatestNumber(BDouble.make(n4));sumAll();}
			if(p == in5 && (n5>=min)){getOutLatestNumber().setValue(n5);fireLatestNumber(BDouble.make(n5));sumAll();}
			if(p == in6 && (n6>=min)){getOutLatestNumber().setValue(n6);fireLatestNumber(BDouble.make(n6));sumAll();}
			if(p == in7 && (n7>=min)){getOutLatestNumber().setValue(n7);fireLatestNumber(BDouble.make(n7));sumAll();}
			if(p == in8 && (n8>=min)){getOutLatestNumber().setValue(n8);fireLatestNumber(BDouble.make(n8));sumAll();}
			if(p == in9 && (n9>=min)){getOutLatestNumber().setValue(n9);fireLatestNumber(BDouble.make(n9));sumAll();}
			if(p == in10 && (n10>=min)){getOutLatestNumber().setValue(n10);fireLatestNumber(BDouble.make(n10));sumAll();}
			if(p == in11 && (n11>=min)){getOutLatestNumber().setValue(n11);fireLatestNumber(BDouble.make(n11));sumAll();}
			if(p == in12 && (n12>=min)){getOutLatestNumber().setValue(n12);fireLatestNumber(BDouble.make(n12));sumAll();}
			if(p == in13 && (n13>=min)){getOutLatestNumber().setValue(n13);fireLatestNumber(BDouble.make(n13));sumAll();}
			if(p == in14 && (n14>=min)){getOutLatestNumber().setValue(n14);fireLatestNumber(BDouble.make(n14));sumAll();}
			if(p == in15 && (n15>=min)){getOutLatestNumber().setValue(n15);fireLatestNumber(BDouble.make(n15));sumAll();}
			if(p == in16 && (n16>=min)){getOutLatestNumber().setValue(n16);fireLatestNumber(BDouble.make(n16));sumAll();}
			if(p == in17 && (n17>=min)){getOutLatestNumber().setValue(n17);fireLatestNumber(BDouble.make(n17));sumAll();}
			if(p == in18 && (n18>=min)){getOutLatestNumber().setValue(n18);fireLatestNumber(BDouble.make(n18));sumAll();}
			if(p == in19 && (n19>=min)){getOutLatestNumber().setValue(n19);fireLatestNumber(BDouble.make(n19));sumAll();}
			if(p == in20 && (n20>=min)){getOutLatestNumber().setValue(n20);fireLatestNumber(BDouble.make(n20));sumAll();}
			if(p == in21 && (n21>=min)){getOutLatestNumber().setValue(n21);fireLatestNumber(BDouble.make(n21));sumAll();}
			if(p == in22 && (n22>=min)){getOutLatestNumber().setValue(n22);fireLatestNumber(BDouble.make(n22));sumAll();}
			if(p == in23 && (n23>=min)){getOutLatestNumber().setValue(n23);fireLatestNumber(BDouble.make(n23));sumAll();}
			if(p == in24 && (n24>=min)){getOutLatestNumber().setValue(n24);fireLatestNumber(BDouble.make(n24));sumAll();}
			
			if(p == outLatestNumber){ setOutLastOutputChange(BAbsTime.make()); }
		}
	}
	
	
	public void sumAll()
	{
		try
		{
			double	sum = 0.0;
			for(int i=1;i<=24;i++)
			{
				BStatusNumeric value = (BStatusNumeric) get( "in" + i );
				if(value.getStatus().isValid())
				{
					double x = value.getValue();
					sum	= sum + x;
				}
			}
			getOutSum().setValue(sum);
			return;
		}
		catch (Exception e) 
		{
			logger.error("\r\n\t\t" + getSlotPath()	+ "\r\n\t\t" + e.getMessage() + "\r\n\t\t" + e.getStackTrace());
			return;
		}
	}

	public static final Log logger = Log.getLog("axCommunity.LatestNumber");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BLatestNumber.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");


}
