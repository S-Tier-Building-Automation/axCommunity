package org.axcommunity.niagara.logic;

import javax.baja.log.Log;
import javax.baja.status.*;
import javax.baja.sys.*;


/**
 * This object accepts up to 24 Status Numeric inputs and will output the latest 
 * value if it meets the minimum required length as configured in the "inMinLength" slot
 * and provided it does not match any values from the slot "inCsvExclusionList".
 * 
 *
 * @author		Justin Koffler, Texas Power Systems
 * @version		12.02.18
 */



public class BLatestStringNotMatching extends BComponent
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	ACTION SLOTS   ////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Action ClearOutput = newAction(0|Flags.ASYNC|Flags.DEFAULT_ON_CLONE,null);
	public void ClearOutput(){invoke(ClearOutput,null,null);}
	public void doClearOutput()
	{
		getOutLatestString().setValue("");
		getOutStringLength().setValue(0);
		fireLatestString(BString.make(""));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//    CONFIG INPUTS   //////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Comma separated values representing the string you wish to exclude from output. */
	public static final Property inCsvExclusionList = newProperty(Flags.SUMMARY, new BStatusString(""));
	public BStatusString getInCsvExclusionList() { return (BStatusString)get(inCsvExclusionList);}
	public void setInCsvExclusionList(BStatusString v) {set(inCsvExclusionList,v);}

	private static String[] matchList = {"In_Equals_Csv", "In_Starts_With_Csv", "In_Ends_With_Csv", "In_Contains_Csv"};
	private static BEnumRange enumMatch  = BEnumRange.make(matchList);
	
	/** Match method to use for comparison.*/
	public static final Property inMatchMode = newProperty(0, BDynamicEnum.make(0, enumMatch),null);
	public BDynamicEnum getInMatchMode() { return (BDynamicEnum)get(inMatchMode); }
	public void setInMatchMode(BDynamicEnum v) { set(inMatchMode,v,null); }
	
	/** When comparing input to exclusion list should case be ignored? */
	public final static Property inIgnoreCase = newProperty(0, new BStatusBoolean(true));
	public BStatusBoolean getInIgnoreCase() { return (BStatusBoolean)get(inIgnoreCase); }
	public void setInIgnoreCase(BStatusBoolean v) { set(inIgnoreCase, v); }
	
	/** Minimum length input value must have before being eligible for latest output. */
	public static final Property inMinLength  = newProperty(0, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getInMinLength() {return (BStatusNumeric) get(inMinLength); }
	public void setInMinLength(BStatusNumeric v) {set(inMinLength, v);}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	OUTPUTS   /////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Latest string received from input slots not matching any values from input slot 'inCsvExclusionList'. */
	public static final Property outLatestString  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getOutLatestString() {return (BStatusString) get(outLatestString); }
	public void setOutLatestString(BStatusString v) {set(outLatestString, v);}
	
	/** Length of string currently in output slot. */
	public static final Property outStringLength  = newProperty(0, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutStringLength() {return (BStatusNumeric) get(outStringLength); }
	public void setOutStringLength(BStatusNumeric v) {set(outStringLength, v);}
	
	/** Number of string values to exclude from output. */
	public static final Property outNumberOfExcludedStrings  = newProperty(0, new BStatusNumeric(0), BFacets.makeNumeric(0));
	public BStatusNumeric getOutNumberOfExcludedStrings() {return (BStatusNumeric) get(outNumberOfExcludedStrings); }
	public void setOutNumberOfExcludedStrings(BStatusNumeric v) {set(outNumberOfExcludedStrings, v);}
	
	/** True if the latest input matched a value in the exclusion list. */
	public final static Property outLatestInputWasExcluded = newProperty(0, new BStatusBoolean(false));
	public BStatusBoolean getOutLatestInputWasExcluded() { return (BStatusBoolean)get(outLatestInputWasExcluded); }
	public void setOutLatestInputWasExcluded(BStatusBoolean v) { set(outLatestInputWasExcluded, v); }
	
	/** Timestamp for when 'outLatestString' was last updated. */
	public static final Property outLastOutputChange = newProperty(0, BAbsTime.make(), BFacets.make("showSeconds",true));
	public BAbsTime getOutLastOutputChange() { return (BAbsTime)get(outLastOutputChange); }
	public void setOutLastOutputChange(BAbsTime v) { set(outLastOutputChange, v); }
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	TOPIC SLOTS   /////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final Topic LatestString = newTopic(0);
	public void fireLatestString(BString event){fire(LatestString,event,null);}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	INPUTS   //////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final Property in1  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn1() {return (BStatusString) get(in1); }
	public void setIn1(BStatusString v) {set(in1, v);}
	
	public static final Property in2  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn2() {return (BStatusString) get(in2); }
	public void setIn2(BStatusString v) {set(in2, v);}
	
	public static final Property in3  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn3() {return (BStatusString) get(in3); }
	public void setIn3(BStatusString v) {set(in3, v);}
	
	public static final Property in4  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn4() {return (BStatusString) get(in4); }
	public void setIn4(BStatusString v) {set(in4, v);}
	
	public static final Property in5  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn5() {return (BStatusString) get(in5); }
	public void setIn5(BStatusString v) {set(in5, v);}
	
	public static final Property in6  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn6() {return (BStatusString) get(in6); }
	public void setIn6(BStatusString v) {set(in6, v);}
	
	public static final Property in7  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn7() {return (BStatusString) get(in7); }
	public void setIn7(BStatusString v) {set(in7, v);}
	
	public static final Property in8  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn8() {return (BStatusString) get(in8); }
	public void setIn8(BStatusString v) {set(in8, v);}
	
	public static final Property in9  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn9() {return (BStatusString) get(in9); }
	public void setIn9(BStatusString v) {set(in9, v);}
	
	public static final Property in10  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn10() {return (BStatusString) get(in10); }
	public void setIn10(BStatusString v) {set(in10, v);}
	
	public static final Property in11  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn11() {return (BStatusString) get(in11); }
	public void setIn11(BStatusString v) {set(in11, v);}
	
	public static final Property in12  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn12() {return (BStatusString) get(in12); }
	public void setIn12(BStatusString v) {set(in12, v);}
	
	public static final Property in13  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn13() {return (BStatusString) get(in13); }
	public void setIn13(BStatusString v) {set(in13, v);}
	
	public static final Property in14  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn14() {return (BStatusString) get(in14); }
	public void setIn14(BStatusString v) {set(in14, v);}
	
	public static final Property in15  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn15() {return (BStatusString) get(in15); }
	public void setIn15(BStatusString v) {set(in15, v);}
	
	public static final Property in16  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn16() {return (BStatusString) get(in16); }
	public void setIn16(BStatusString v) {set(in16, v);}
	
	public static final Property in17  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn17() {return (BStatusString) get(in17); }
	public void setIn17(BStatusString v) {set(in17, v);}
	
	public static final Property in18  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn18() {return (BStatusString) get(in18); }
	public void setIn18(BStatusString v) {set(in18, v);}
	
	public static final Property in19  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn19() {return (BStatusString) get(in19); }
	public void setIn19(BStatusString v) {set(in19, v);}
	
	public static final Property in20  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn20() {return (BStatusString) get(in20); }
	public void setIn20(BStatusString v) {set(in20, v);}
	
	public static final Property in21  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn21() {return (BStatusString) get(in21); }
	public void setIn21(BStatusString v) {set(in21, v);}
	
	public static final Property in22  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn22() {return (BStatusString) get(in22); }
	public void setIn22(BStatusString v) {set(in22, v);}
	
	public static final Property in23  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn23() {return (BStatusString) get(in23); }
	public void setIn23(BStatusString v) {set(in23, v);}
	
	public static final Property in24  = newProperty(0|Flags.SUMMARY, new BStatusString());
	public BStatusString getIn24() {return (BStatusString) get(in24); }
	public void setIn24(BStatusString v) {set(in24, v);}
	
	
	


	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**    METHOD INVOKED WHEN ANY OF THE INPUTS CHANGES VALUES   *////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void changed(Property p, Context cx)
	{
		if(!Sys.atSteadyState() || !isRunning())return;
		if(isRunning())
		{
			double min	= (double) getInMinLength().getValue();
			
			String s1	= getIn1().getValue();
			String s2	= getIn2().getValue();
			String s3	= getIn3().getValue();
			String s4	= getIn4().getValue();
			String s5	= getIn5().getValue();
			String s6	= getIn6().getValue();
			String s7	= getIn7().getValue();
			String s8	= getIn8().getValue();
			String s9	= getIn9().getValue();
			String s10	= getIn10().getValue();
			String s11	= getIn11().getValue();
			String s12	= getIn12().getValue();
			String s13	= getIn13().getValue();
			String s14	= getIn14().getValue();
			String s15	= getIn15().getValue();
			String s16	= getIn16().getValue();
			String s17	= getIn17().getValue();
			String s18	= getIn18().getValue();
			String s19	= getIn19().getValue();
			String s20	= getIn20().getValue();
			String s21	= getIn21().getValue();
			String s22	= getIn22().getValue();
			String s23	= getIn23().getValue();
			String s24	= getIn24().getValue();
		
			if(p == in1		&& (s1.length()>=min	&& excluded(s1)==false))	{getOutLatestString().setValue(s1);fireLatestString(BString.make(s1));getOutStringLength().setValue(s1.length());}
			if(p == in2		&& (s2.length()>=min	&& excluded(s2)==false))	{getOutLatestString().setValue(s2);fireLatestString(BString.make(s2));getOutStringLength().setValue(s2.length());}
			if(p == in3		&& (s3.length()>=min	&& excluded(s3)==false))	{getOutLatestString().setValue(s3);fireLatestString(BString.make(s3));getOutStringLength().setValue(s3.length());}
			if(p == in4		&& (s4.length()>=min	&& excluded(s4)==false))	{getOutLatestString().setValue(s4);fireLatestString(BString.make(s4));getOutStringLength().setValue(s4.length());}
			if(p == in5		&& (s5.length()>=min	&& excluded(s5)==false))	{getOutLatestString().setValue(s5);fireLatestString(BString.make(s5));getOutStringLength().setValue(s5.length());}
			if(p == in6		&& (s6.length()>=min	&& excluded(s6)==false))	{getOutLatestString().setValue(s6);fireLatestString(BString.make(s6));getOutStringLength().setValue(s6.length());}
			if(p == in7		&& (s7.length()>=min	&& excluded(s7)==false))	{getOutLatestString().setValue(s7);fireLatestString(BString.make(s7));getOutStringLength().setValue(s7.length());}
			if(p == in8		&& (s8.length()>=min	&& excluded(s8)==false))	{getOutLatestString().setValue(s8);fireLatestString(BString.make(s8));getOutStringLength().setValue(s8.length());}
			if(p == in9		&& (s9.length()>=min	&& excluded(s9)==false))	{getOutLatestString().setValue(s9);fireLatestString(BString.make(s9));getOutStringLength().setValue(s9.length());}
			if(p == in10	&& (s10.length()>=min	&& excluded(s10)==false))	{getOutLatestString().setValue(s10);fireLatestString(BString.make(s10));getOutStringLength().setValue(s10.length());}
			if(p == in11	&& (s11.length()>=min	&& excluded(s11)==false))	{getOutLatestString().setValue(s11);fireLatestString(BString.make(s11));getOutStringLength().setValue(s11.length());}
			if(p == in12	&& (s12.length()>=min	&& excluded(s12)==false))	{getOutLatestString().setValue(s12);fireLatestString(BString.make(s12));getOutStringLength().setValue(s12.length());}
			if(p == in13	&& (s13.length()>=min	&& excluded(s13)==false))	{getOutLatestString().setValue(s13);fireLatestString(BString.make(s13));getOutStringLength().setValue(s13.length());}
			if(p == in14	&& (s14.length()>=min	&& excluded(s14)==false))	{getOutLatestString().setValue(s14);fireLatestString(BString.make(s14));getOutStringLength().setValue(s14.length());}
			if(p == in15	&& (s15.length()>=min	&& excluded(s15)==false))	{getOutLatestString().setValue(s15);fireLatestString(BString.make(s15));getOutStringLength().setValue(s15.length());}
			if(p == in16	&& (s16.length()>=min	&& excluded(s16)==false))	{getOutLatestString().setValue(s16);fireLatestString(BString.make(s16));getOutStringLength().setValue(s16.length());}
			if(p == in17	&& (s17.length()>=min	&& excluded(s17)==false))	{getOutLatestString().setValue(s17);fireLatestString(BString.make(s17));getOutStringLength().setValue(s17.length());}
			if(p == in18	&& (s18.length()>=min	&& excluded(s18)==false))	{getOutLatestString().setValue(s18);fireLatestString(BString.make(s18));getOutStringLength().setValue(s18.length());}
			if(p == in19	&& (s19.length()>=min	&& excluded(s19)==false))	{getOutLatestString().setValue(s19);fireLatestString(BString.make(s19));getOutStringLength().setValue(s19.length());}
			if(p == in20	&& (s20.length()>=min	&& excluded(s20)==false))	{getOutLatestString().setValue(s20);fireLatestString(BString.make(s20));getOutStringLength().setValue(s20.length());}
			if(p == in21	&& (s21.length()>=min	&& excluded(s21)==false))	{getOutLatestString().setValue(s21);fireLatestString(BString.make(s21));getOutStringLength().setValue(s21.length());}
			if(p == in22	&& (s22.length()>=min	&& excluded(s22)==false))	{getOutLatestString().setValue(s22);fireLatestString(BString.make(s22));getOutStringLength().setValue(s22.length());}
			if(p == in23	&& (s23.length()>=min	&& excluded(s23)==false))	{getOutLatestString().setValue(s23);fireLatestString(BString.make(s23));getOutStringLength().setValue(s23.length());}
			if(p == in24	&& (s24.length()>=min	&& excluded(s24)==false))	{getOutLatestString().setValue(s24);fireLatestString(BString.make(s24));getOutStringLength().setValue(s24.length());}
			
			if(p == outLatestString){ setOutLastOutputChange(BAbsTime.make()); }
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Determines if new input string matches any values in the exclusion list and returns true if a match is found. */
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean excluded(String inVal)
	{
		getOutLatestInputWasExcluded().setValue(false);
		
		try
		{
			String	csv		= getInCsvExclusionList().getValue();
			
			if(csv.trim().length()<1)
			{
				return false;
			}
			else if(getInIgnoreCase().getValue()==true)
			{
				inVal	= inVal.toLowerCase();
				csv		= csv.toLowerCase();
			}
		
			//Create an array using a ',' as the split delimiter.
			String[] arrExcluded	= csv.split(",");

			getOutNumberOfExcludedStrings().setValue(arrExcluded.length);
			
			for (int i = 0;i<arrExcluded.length;i++)
			{
				switch(getInMatchMode().getOrdinal())
				{
					case 0:
						if(inVal.trim().equals(arrExcluded[i].trim()))
						{
							getOutLatestInputWasExcluded().setValue(true);
							return true;
						}
						break;
						
					// case 1:
						// if(inVal.trim().equalsIgnoreCase(arrExcluded[i].trim()))
						// {
							// getOutLatestInputWasExcluded().setValue(true);
							// return true;
						// }
						// break;
					
					case 1:
						if(inVal.trim().startsWith(arrExcluded[i].trim()))
						{
							getOutLatestInputWasExcluded().setValue(true);
							return true;
						}
						break;
					
					case 2:
						if(inVal.trim().endsWith(arrExcluded[i].trim()))
						{
							getOutLatestInputWasExcluded().setValue(true);
							return true;
						}
						break;
					
					case 3:
						if(inVal.trim().indexOf(arrExcluded[i].trim()) >= 0)
						{
							getOutLatestInputWasExcluded().setValue(true);
							return true;
						}
						break;
				}
			}
			return false;
		}
		catch (Exception e) 
		{
			logger.error("\r\n\t\t" + getSlotPath()	+ "\r\n\t\t" + e.getMessage() + "\r\n\t\t" + e.getStackTrace());
			return false;
		}
	}

	// ON START AND ON STOP RELATED STUFF. //////////////////////////////////////////////////////////////////
	public void started() throws Exception { try { onStart(); } catch(Throwable t) { throw new Exception(t); } }
	public void stopped() throws Exception { try { onStop(); } catch(Throwable t) { throw new Exception(t); } }
	
	public void onStart() throws Exception{	}
	public void onStop() throws Exception{ }
	

	public static final Log logger = Log.getLog("axCommunity.LatestStringNotMatching");
	
	public Type getType() { return TYPE; }
	public static final Type TYPE = Sys.loadType(BLatestStringNotMatching.class);

	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/JustinKoffler.png");


}
