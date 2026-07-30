package org.axcommunity.niagara.web;

import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Gets a specified web page as a StatusString
 *
 * @authors    Mike Arnott & Roman Ivanov, Kors Engineering
 */

public class BGetHTTP extends BComponent{

	private static BFacets tBox = BFacets.make("multiLine",true);
	
	
	class HttpThread extends Thread{
		public void run(){
			try {

				String url = getInURL().getValue();
				if (getHttpsOnly().getBoolean() && !url.toLowerCase().startsWith("https://"))
					throw new IOException("httpsOnly is set - refusing non-HTTPS URL: " + url);
				String response = Requester.get(new URL(url));
				getHttpOut().setValue(response);
			}catch (Exception e) {
				// Report through httpOut; never rethrow - an exception escaping a
				// raw worker thread dies silently.
				getHttpOut().setValue(e.toString());
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
	
	/**When true, only https:// URLs are fetched; plain http is refused. Recommended on untrusted networks - inURL is a linkable property, so treat it as operator input.*/
	public static final Property httpsOnly = newProperty(Flags.SUMMARY, new BStatusBoolean(false));
	public BStatusBoolean getHttpsOnly() { return (BStatusBoolean) get(httpsOnly); }
	public void setHttpsOnly(BStatusBoolean v) { set(httpsOnly, v); }
	
	/**Returned document*/
    public static final Property httpOut = newProperty(Flags.SUMMARY, new BStatusString(),tBox);
    public BStatusString getHttpOut() { return (BStatusString)get(httpOut); }
    public void setHttpOut(BStatusString v) { set(httpOut, v); }
    
    
    public BIcon getIcon() { return icon; }
    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/korsLogo.png");

    public static final Type TYPE = Sys.loadType(BGetHTTP.class);
    public Type getType() { return TYPE; }
}
class Requester{
	/**
	 * This class is simply a container for static methods.
	 */
	protected Requester(){}

	private static final int CONNECT_TIMEOUT_MS = 10000;
	private static final int READ_TIMEOUT_MS = 10000;
	private static final int MAX_RESPONSE_BYTES = 1024 * 1024;

	/**
	 * @param destination
	 * @return String with the response body.
	 * @throws IOException
	 */
	public static String get(URL destination) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) destination.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
		connection.setReadTimeout(READ_TIMEOUT_MS);

		StringBuilder response = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String line;
			while ((line = in.readLine()) != null) {
				if (response.length() + line.length() > MAX_RESPONSE_BYTES)
					throw new IOException("response exceeds " + MAX_RESPONSE_BYTES + " byte cap");
				response.append(line).append('\n');
			}
		} finally {
			connection.disconnect();
		}
		return response.toString();
	}
}


