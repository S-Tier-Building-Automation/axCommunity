package org.axcommunity.niagara.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


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
