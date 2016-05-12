package org.axcommunity.niagara.string;

import com.tridium.util.EscUtil;

import javax.baja.status.BStatusString;
import javax.baja.sys.*;

public class BParentFolderInfo extends BComponent{
	public void started() throws Exception
	{
		getParentName().setValue(this.getParent().getDisplayName(null));
	}

	
	static String escape(String s)
	{
		return EscUtil.slot.escape(s);
	}

	static String unescape(String s)
	{
		return EscUtil.slot.unescape(s);
		
	}
	public static final Action refresh = newAction(0);

	public void refresh()
	{
		invoke(refresh, null);
	}

	public void doRefresh()
	{
		getParentName().setValue(this.getParent().getDisplayName(null));

	}
	

	/**parent folder name*/
	public static final Property parentName = newProperty(Flags.SUMMARY, new BStatusString());
	public void setParentName(BStatusString v) { set(parentName, v); }
	public BStatusString getParentName() { 
		return (BStatusString)get(parentName); 
	}
	public BIcon getIcon() { return icon; }
	private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

	public static final Type TYPE = Sys.loadType(BParentFolderInfo.class);
	public Type getType() { return TYPE; }

//	public void renamed(BParentFolderInfo bi){

//	}

}
