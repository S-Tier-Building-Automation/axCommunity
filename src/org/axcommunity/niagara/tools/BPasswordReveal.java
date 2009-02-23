package org.axcommunity.niagara.tools;

import javax.baja.collection.BITable;
import javax.baja.collection.Column;
import javax.baja.collection.ColumnList;
import javax.baja.collection.TableCursor;
import javax.baja.naming.BOrd;
import javax.baja.security.BPassword;
import javax.baja.sys.Action;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BInteger;
import javax.baja.sys.BObject;
import javax.baja.sys.BStation;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.user.BUser;

import com.tridium.fox.sys.BFoxClientConnection;
import com.tridium.nd.BNiagaraStation;


/**
 * BPasswordReveal is a simple program to retrieve stored passwords.  It can query the
 * UserService or the stations within the Niagara Network.
 *
 * @author    Tucker Watson
 * @creation  02 Feb 07
 * @company   Activelogix (http://www.activelogix.com)
 * @blog      http://www.tuckwat.com
 */

public class BPasswordReveal extends BComponent
{
	/*-
	
	class BPasswordReveal
	{
		actions
		{
			getUsers()
			getNiagaraNetwork() 
		}
		properties
		{	
			out: String
			default {[""]}
			slotfacets {[ BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)) ]}
			flags { summary }
		}	  
	}
	
	-*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $org.axcommunity.niagara.tools.BPasswordReveal(895799751)1.0$ @*/
/* Generated Sun Feb 22 14:52:04 EST 2009 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "out"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>out</code> property.
   * @see org.axcommunity.niagara.tools.BPasswordReveal#getOut
   * @see org.axcommunity.niagara.tools.BPasswordReveal#setOut
   */
  public static final Property out = newProperty(Flags.SUMMARY, "",BFacets.make(BFacets.MULTI_LINE, BBoolean.TRUE, BFacets.FIELD_WIDTH, BInteger.make(100)));
  
  /**
   * Get the <code>out</code> property.
   * @see org.axcommunity.niagara.tools.BPasswordReveal#out
   */
  public String getOut() { return getString(out); }
  
  /**
   * Set the <code>out</code> property.
   * @see org.axcommunity.niagara.tools.BPasswordReveal#out
   */
  public void setOut(String v) { setString(out,v,null); }

////////////////////////////////////////////////////////////////
// Action "getUsers"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>getUsers</code> action.
   * @see org.axcommunity.niagara.tools.BPasswordReveal#getUsers()
   */
  public static final Action getUsers = newAction(0,null);
  
  /**
   * Invoke the <code>getUsers</code> action.
   * @see org.axcommunity.niagara.tools.BPasswordReveal#getUsers
   */
  public void getUsers() { invoke(getUsers,null,null); }

////////////////////////////////////////////////////////////////
// Action "getNiagaraNetwork"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>getNiagaraNetwork</code> action.
   * @see org.axcommunity.niagara.tools.BPasswordReveal#getNiagaraNetwork()
   */
  public static final Action getNiagaraNetwork = newAction(0,null);
  
  /**
   * Invoke the <code>getNiagaraNetwork</code> action.
   * @see org.axcommunity.niagara.tools.BPasswordReveal#getNiagaraNetwork
   */
  public void getNiagaraNetwork() { invoke(getNiagaraNetwork,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BPasswordReveal.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

	public void doGetUsers()
	{
		BUser curUser;
		String curPass;

		StringBuffer buf = new StringBuffer();
		BObject[] userObjects = getObjectsFromBql("station:|slot:/|bql:select slotPath from baja:User");

		for (int i = 0; i < userObjects.length; i++)
		{
			curUser = (BUser) userObjects[i];
			curPass = curUser.getPassword().getString();

			if (curPass.length() == 0)
				curPass = "[empty]";

			buf.append(curUser.getUsername()).append(" : ").append(curPass).append("\n");
		}

		setOut(buf.toString());
	}
	
	public void doGetNiagaraNetwork()
	{
		BFoxClientConnection curConn;
		BNiagaraStation curStation;
		String curPass;
		StringBuffer buf = new StringBuffer();

		BObject[] niagaraStations = getObjectsFromBql("station:|slot:/|bql:select slotPath from niagaraDriver:NiagaraStation");

		for (int i = 0; i < niagaraStations.length; i++)
		{
			curStation = (BNiagaraStation) niagaraStations[i];
			curConn = (BFoxClientConnection) curStation.get("clientConnection");
			curPass = ((BPassword) curConn.get("password")).getString();

			if (curPass.length() == 0)
				curPass = "[empty]";

			buf.append(curStation.get("address")).append(" : ").append(curConn.get("username")).append(" : ").append(curPass).append("\n");
		}

		setOut(buf.toString());
		
	}

	public BObject[] getObjectsFromBql(String bqlString)
	{
		BOrd curOrd;

		// Grab reference to local station
		BStation station = (BStation) BOrd.make("station:|slot:/").get();

		// Run the BQL query and get the resulting table
		BITable result = (BITable) BOrd.make(bqlString).get(station);

		ColumnList columns = result.getColumns();

		// Note we're assuming column 0 is "slotPath"
		Column slotPathColumn = columns.get(0);
		TableCursor c = (TableCursor) result.cursor();

		// Create the Array of BObjects to return
		BObject[] objs = new BObject[result.size()];

		int i = 0;
		while (c.next())
		{
			curOrd = BOrd.make(c.get(slotPathColumn).toString());

			// Resolve each ord to a BObject, store in our Array
			objs[i++] = (BObject) curOrd.resolve(station).get();
		}
		
		
		return objs;
	}
}