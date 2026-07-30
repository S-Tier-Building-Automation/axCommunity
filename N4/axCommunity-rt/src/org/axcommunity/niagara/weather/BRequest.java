/* SPDX-License-Identifier: GPL-2.0-only */

package org.axcommunity.niagara.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Static HTTP GET helper used by {@link BFireFoxxWeather} to retrieve the
 * weather feed.
 *
 * <p>Opens the given {@link URL} with hard 10-second connect/read timeouts
 * and returns the response body as a String, aborting with an
 * {@link java.io.IOException} if the body exceeds a 1 MB cap — a hung or
 * runaway endpoint can no longer stall the calling worker thread.</p>
 *
 * @author Ron Lea, FireFoxx Controls
 */
public class BRequest
{
  
  
////////////////////////////////////////////////////////////////
//Main
////////////////////////////////////////////////////////////////
  
      private static final int CONNECT_TIMEOUT_MS = 10000;
      private static final int READ_TIMEOUT_MS = 10000;
      private static final int MAX_RESPONSE_BYTES = 1024 * 1024;

      public static String get(URL paramURL)
      throws IOException
      {
        HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURL.openConnection();
        localHttpURLConnection.setRequestMethod("GET");
        localHttpURLConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        localHttpURLConnection.setReadTimeout(READ_TIMEOUT_MS);

        StringBuilder response = new StringBuilder();
        try (BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localHttpURLConnection.getInputStream()))) {
          String str2;
          while ((str2 = localBufferedReader.readLine()) != null) {
            if (response.length() + str2.length() > MAX_RESPONSE_BYTES)
              throw new IOException("response exceeds " + MAX_RESPONSE_BYTES + " byte cap");
            response.append(str2).append('\n');
          }
        } finally {
          localHttpURLConnection.disconnect();
        }
        return response.toString();
      }
  

  
////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////


}
