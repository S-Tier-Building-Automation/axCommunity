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
  
      public static String get(URL paramURL)
      throws IOException
      {
        HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURL.openConnection();
        localHttpURLConnection.setRequestMethod("GET");
        localHttpURLConnection.setRequestProperty("Host", paramURL.getHost());
        localHttpURLConnection.setDoOutput(true);
        localHttpURLConnection.connect();

        String str1 = "";
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localHttpURLConnection.getInputStream()));
        String str2 = "";
        while ((str2 = localBufferedReader.readLine()) != null) {
          str1 = str1 + str2 + '\n';
        }

        localHttpURLConnection.disconnect();
        return str1;
      }
  

  
////////////////////////////////////////////////////////////////
//Final
////////////////////////////////////////////////////////////////


}
