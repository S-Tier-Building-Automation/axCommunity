package org.axcommunity.niagara.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.baja.io.Base64;
import javax.baja.status.BStatusString;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;


/**
 * Gets a specified web page as a StatusString
 *
 * @authors    Mike Arnott & Roman Ivanov, Kors Engineering
 * 1/3/2107	   Additions by Julio Romero, added username/password
 */

public class BGetHTTP extends BComponent{

	private static BFacets tBox = BFacets.make("multiLine",true);
	
	
	class HttpThread extends Thread{
		public void run(){
			try {

				String url = getInURL().getValue();
				String username = getInUsername().getValue();
				String password = getInPassword().getValue();
				String response = Requester.get(new URL(url), username, password);
				getHttpOut().setValue(response);
			}catch (Exception e) {
				//logger.warning( e.getClass().getName() + " : " + e.getMessage() + "\n");
				getHttpOut().setValue(e.toString());
				throw new RuntimeException(e);
			}
		}
	}
	/**Invoke this action to refresh the web page*/
	public static final Action refresh = newAction(0);
	public void refresh() { 
		invoke(refresh, null);
	}
	//invokes
	public void doRefresh(){
		new HttpThread().start();
	}

	/**Enter the URL to Get here*/
	public static final Property inURL = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getInURL() {
		return (BStatusString) get(inURL); 
	}
	public void setInURL(BStatusString v) {
		set(inURL, v);
	}
	
	public static final Property inUsername = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getInUsername() {
		return (BStatusString) get(inUsername); 
	}
	public void setInUsername(BStatusString v) {
		set(inUsername, v);
	}

	public static final Property inPassword = newProperty(Flags.SUMMARY, new BStatusString());
	public BStatusString getInPassword() {
		return (BStatusString) get(inPassword); 
	}
	public void setInPassword(BStatusString v) {
		set(inPassword, v);
	}
	
	/**Returned document*/
    public static final Property httpOut = newProperty(Flags.SUMMARY, new BStatusString(),tBox);
    public BStatusString getHttpOut() { return (BStatusString)get(httpOut); }
    public void setHttpOut(BStatusString v) { set(httpOut, v); }
    
    
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("local:|module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

    public static final Type TYPE = Sys.loadType(BGetHTTP.class);
    public Type getType() { return TYPE; }
}
class Requester{
	/**
	 * This class is simply a container for static methods.
	 */
	protected Requester(){}

	/**
	 * @param destination
	 * @return String with the response body.
	 * @throws IOException
	 */
	public static String get(URL destination, String login, String password) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) destination.openConnection();
		if (login!=null && !"".equals(login.trim())) {
			String authCode = Base64.encode((login + ":" + password).getBytes());
			connection.setRequestProperty("Authorization", "Basic " + authCode);
		}
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Host", destination.getHost());
		connection.setDoOutput(true);
		connection.connect();

		String inString = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inBuffer = "";
		while ((inBuffer = in.readLine()) != null) {
			inString += inBuffer + "\n";
		}

		connection.disconnect();
		return inString;
	}
}


