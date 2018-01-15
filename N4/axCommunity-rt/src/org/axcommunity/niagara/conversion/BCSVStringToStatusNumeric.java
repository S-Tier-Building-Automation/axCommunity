package org.axcommunity.niagara.conversion;

import java.nio.ByteBuffer;

import javax.baja.status.*;
import javax.baja.sys.*;

/**
 * Inputs a comma-separted string of numbers and outputs specified number of numeric outputs.
 * Also attempts to convert specified range of inputs to ascii string.
 * @author Mike Arnott, Kors Engineering
 */

public class BCSVStringToStatusNumeric extends BComponent {

	   private static String [] slotNames;
	   private static int oldSlotNumber = 0;
	   private static boolean runOnce = false;
	   private static final int MINSLOTS = 0;
	   private static final int MAXSLOTS = 200;

	    public void started(){
	        if (runOnce == false) {        
	            slotNames = new String[MAXSLOTS];
	            for (int x = 0; x < slotNames.length; ++x) {
	                if ( x < 10) {
	                    slotNames[x] = "Input0" + Integer.toString(x);
	                } else {
	                    slotNames[x] = "Input" + Integer.toString(x);
	                }
	            }
	            runOnce = true;
	        }
	    }
	
	public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!Sys.atSteadyState() || !isRunning()){
    		return;
    	}
       	
        if (property == numberOfItems){
            int numberInput = (int)getNumberOfItems().getValue();
            if (numberInput >= MINSLOTS && numberInput <MAXSLOTS) {
                if (numberInput > oldSlotNumber) {
                    for (int x = 0; x < numberInput; ++x){
                        if (this.getSlot(slotNames[x])==null){
                            this.add(slotNames[x], new BStatusNumeric(), Flags.SUMMARY);
                        }
                    }
                    oldSlotNumber = numberInput;
                }

                if (numberInput < oldSlotNumber) {
                    for (int x = oldSlotNumber; x > numberInput - 1; --x) {
                        if (this.getSlot(slotNames[x])!=null) {
                            this.remove(slotNames[x]);
                        }
                    }
                    oldSlotNumber = numberInput;
                }
            }
        }
        if(property==stringIn){
        	if(getStringIn().getValue().length()>0){
        	String splt[] = split( getStringIn().getValue(),',');
        	double b = 0;
        	double offset = getStartingAtElement().getValue();
        	if (splt.length > 0){
	        	for(int l = (int)offset;l < splt.length + offset;++l){
	        		if(l<getNumberOfItems().getValue() + offset){
	        			if(l<=splt.length){
	        				try{
	        					b = Double.valueOf(splt[l]).doubleValue();
	        					set(getProperty(slotNames[l - (int)offset]), new BStatusNumeric( b));
	        				}
	        				catch (NumberFormatException dex) {
	        					//not a number input, set number to 0
	        					set(getProperty(slotNames[l - (int)offset]), new BStatusNumeric(0));
	        				}
	        			}
	        		}
	        		else{
	        			try{
	        			Property xx = getProperty(slotNames[l - (int)offset]);
		        			if (xx.isProperty()){
		        				set(xx, new BStatusNumeric(0));
		        			}
	        			}
		        		catch (NullPointerException nex){
		        			
		        		}
		        		catch (ArrayIndexOutOfBoundsException aex){
		        			
		        		}
	        		}
	        		
	        		//convert byte array to chars
	        		char bs[];
	        		if(getIsTwoAsciiPerNumber())
	        		{
	        		 bs = new char[(int)getNumberOfItems().getValue()*2];
	        		}
	        		else bs = new char[(int)getNumberOfItems().getValue()];
	        		
	        		
	        		for(int s = 0;s < getNumberOfItems().getValue();s++){
	        			double a;
		        		a = ((BStatusNumeric)get(getProperty(slotNames[s]))).getValue();
	        			if(getIsTwoAsciiPerNumber())
	        			{
	        				//make a 2 byte array from the string
	        				short ai = (short)a;
	        				byte[] bytes = ByteBuffer.allocate(2).putShort(ai).array();
        					//for 2 byte arrays, add characters in either forward or reverse order
        					if(getSwapCharacters())
        					{
        						bs[s*2]=(char)bytes[1];
        						bs[s*2 + 1]=(char)bytes[0];
        					}
        					else
        					{
        						bs[s*2]=(char)bytes[0];
        						bs[s*2 + 1]=(char)bytes[1];	        							
        					}
	        			}
	        			else bs[s]=(char)a;
	        		}
	        		String bstring = new String(bs);
	        		getStringOut().setValue(bstring);
	        	}
        	}
        }
        }
	}
	
    /**CSV String Input*/
    public static final Property stringIn = newProperty(Flags.SUMMARY, new BStatusString(""));
    public BStatusString getStringIn() { return (BStatusString)get(stringIn);}
    public void setStringIn(BStatusString v) {set(stringIn,v);}

    /**ASCII String Output*/
    public static final Property stringOut = newProperty(Flags.SUMMARY, new BStatusString(""));
    public BStatusString getStringOut() { return (BStatusString)get(stringOut);}
    public void setStringOut(BStatusString v) {set(stringOut,v);}

    
    /*copied from "bdynamic list"
     * (non-Javadoc)
     * @see javax.baja.sys.BComponent#getIcon()
     */

    /**Number of items to convert*/
    public static final Property numberOfItems = newProperty(Flags.SUMMARY, new BStatusNumeric(1));
    /**Element to start at (0 based)*/
    public static final Property startingAtElement = newProperty(Flags.SUMMARY, new BStatusNumeric(0));
    
    public BStatusNumeric getNumberOfItems() { return (BStatusNumeric)get(numberOfItems); }
    public BStatusNumeric getStartingAtElement() { return (BStatusNumeric)get(startingAtElement); }
    
    public void setNumberOfItems(BStatusNumeric v) { set(numberOfItems, v); }
    public void setStartingAtElement(BStatusNumeric v) { set(startingAtElement, v); }
    
	/**If true, set the output string assuming 2 characters per number, else 1 character per number*/
	public static final Property isTwoAsciiPerNumber = newProperty(0, true);
	public boolean getIsTwoAsciiPerNumber() { return getBoolean(isTwoAsciiPerNumber);}
	public void setIsTwoAsciiPerNumber(boolean v) {setBoolean(isTwoAsciiPerNumber,v);}

	/**If true, characters are swapped per number (only applies if isTwoAsciiPerNumber==true)*/
	public static final Property swapCharacters = newProperty(0, false);
	public boolean getSwapCharacters() { return getBoolean(swapCharacters);}
	public void setSwapCharacters(boolean v) {setBoolean(swapCharacters,v);}
   
    
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
	    
    public static final Type TYPE = Sys.loadType(BCSVStringToStatusNumeric.class);
    public Type getType() { return TYPE; }   

    
    /**
     * Parse a string into an array, using a given delimiter.
     */
    private static String[] split(String str, char delim)
    {                  
      if (str.indexOf(delim) == -1) 
      {
        if (str.length() == 0) return new String[0];
        else return new String[] { str };
      }

      String[] list = new String[8];

      int a = 0;
      int b = 0;
      int n = 0;
      while (b < str.length())
      {
        if (str.charAt(b) == delim)
        {
          list = ensureCapacity(list, n);
          list[n++] = str.substring(a, b);
          a = ++b;
        }
        else
        {
          b++;
        }
      }
      list = ensureCapacity(list, n);
      list[n++] = str.substring(a, str.length());

      if (n == list.length)
      {
        return list;
      }
      else
      {
        String[] trim = new String[n];
        System.arraycopy(list, 0, trim, 0, n);
        return trim;
      }
    }

    /**
     * Ensure the given string has the specified capacity. If
     * so then return x, otherwise return a bigger String array
     * with the existing contents.
     */
    private static String[] ensureCapacity(String[] x, int len)
    {
      if (len < x.length) return x;
      String[] expand = new String[x.length*2];
      System.arraycopy(x, 0, expand, 0, x.length);
      return expand;
    }

 

}
