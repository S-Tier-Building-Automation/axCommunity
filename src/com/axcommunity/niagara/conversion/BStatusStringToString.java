package com.axcommunity.niagara.conversion;

import javax.baja.sys.*;
import javax.baja.status.*;
/**
 * Converts an Status String input into a String output.
 * @author Mike Arnott, Kors Engineering
*/
public class BStatusStringToString
  extends BComponent
{
  
   public static final Property out = newProperty(Flags.TRANSIENT|Flags.READONLY|Flags.SUMMARY, "",null);
   public String getOut() { return getString(out); }
   public void setOut(String v) { setString(out,v,null); }
   public static final Property in = newProperty(Flags.TRANSIENT|Flags.SUMMARY, new BStatusString(),null);
   public BStatusString getIn() { return (BStatusString)get(in); }
   public void setIn(BStatusString v) { set(in,v,null); }

  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BStatusStringToString.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  
  public void started()
  {
    setOut(  getIn().getValue() );
    
   }

  public void changed(Property p, Context cx)
  {
 	if(!Sys.atSteadyState() || !isRunning()){
		return;
	}

    if (p == in)
    {
    	   setOut(  getIn().getValue() );
    }
  }

 


  public BIcon getIcon() { return icon; }
  private static final BIcon icon = BIcon.make("local:|module://axCommunity/com/axcommunity/niagara/graphics/korsLogo.png");

  
}
