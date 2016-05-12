package org.axcommunity.niagara.math;

import javax.baja.status.*;
import javax.baja.sys.*;
/**
 * Copies input on transition of trigger from false to true
 * @author Mike Arnott, Kors Engineering
 */
public class BTrCopy extends BComponent{
	
	boolean fire = false;
	
	public void started(){
		try{
			if (getSlot("Copy").isProperty()){
				//Check for slot
			}
		}
		catch(Exception ex){
			add("Copy", new BStatusNumeric(0), Flags.SUMMARY);
			add("Input", new BStatusNumeric(0), Flags.SUMMARY);
		}
	}
	
	public void changed(Property property, Context context){
        super.changed(property, context);
       	if(!Sys.atSteadyState()){
    		return;
    	}
       try{
	        if (property == facets){
	        	setFacets(getSlot("Copy"), getFacets());
	        	setFacets(getSlot("Input"), getFacets());
	        }
	        
	        if (property == trigger){
	        	if (getTrigger().getValue() && fire == false){
	        		double a;
	        		a = ((BStatusNumeric)get(getProperty("Input"))).getValue();
	        		set(getProperty("Copy"), new BStatusNumeric(a));
	            	fire = true;
	        	}
	        	else {
	        		fire = false;
	        	}
	        }
		}
		catch(Exception e){
			System.out.println(BAbsTime.now().toString() +   ": Kors Component Error at " + this.getSlotPath().toString() + ":" + e.toString());
		}

    }

	public static final Property facets = newProperty(0, BFacets.makeNumeric(1));
	public static final Property trigger = newProperty(Flags.SUMMARY, new BStatusBoolean(false));

    public BFacets getFacets() { return (BFacets)get(facets); }
	public BStatusBoolean getTrigger() { return (BStatusBoolean)get(trigger); }
    
	public void setFacets(BFacets v) { set(facets, v); }
    public void setTrigger(BStatusBoolean v) { set(trigger, v); }
    
    public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");
    
    public static final Type TYPE = Sys.loadType(BTrCopy.class);
    public Type getType() { return TYPE; }
}