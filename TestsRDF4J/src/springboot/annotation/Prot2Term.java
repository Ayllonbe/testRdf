package springboot.annotation;

import java.net.URL;
import java.net.URLConnection;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;


public class Prot2Term {

  public static void main(String[] args) throws Exception {
    String requestURL = "https://www.ebi.ac.uk/QuickGO/services/annotation/search?geneProductId=Q197A1%2C%20O14770%2C%20P01106%2C%20P0C870%2C%20P22680";
    URL url = new URL(requestURL);

    URLConnection connection = url.openConnection();
    HttpURLConnection httpConnection = (HttpURLConnection)connection;

    httpConnection.setRequestProperty("Accept", "application/json");


    InputStream response = connection.getInputStream();
    int responseCode = httpConnection.getResponseCode();

    if(responseCode != 200) {
      throw new RuntimeException("Response code was not 200. Detected response was "+responseCode);
    }

    String output;
    Reader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
      StringBuilder builder = new StringBuilder();
      char[] buffer = new char[8192];
      int read;
      while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
        builder.append(buffer, 0, read);
      }
      output = builder.toString();
    }
    finally {
        if (reader != null) try {
          reader.close();
        } catch (IOException logOrIgnore) {
          logOrIgnore.printStackTrace();
        }
    }

    System.out.println(output);
    JSONObject root = new JSONObject( output);
    JSONArray jsonarray = root.getJSONArray("results");
    
    for (int i = 0; i < jsonarray.length(); ++i) {
        JSONObject rec = jsonarray.getJSONObject(i);
        System.out.println(rec.getString("geneProductId") +" "+rec.getString("qualifier")+" "+ rec.getString("goId") + " " + rec.getString("goEvidence"));
    }
    
  }
}