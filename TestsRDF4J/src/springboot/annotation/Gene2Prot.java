package springboot.annotation;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;


public class Gene2Prot{

  public static void main(String[] args) throws Exception {
	  
		List<String> symb =new ArrayList<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{	
		add("CA1");
		add("EPB41");
		add("ISCA1L");
		add("LOC389293");
		add("YOD1");
		add("GYPB");
		add("NFIX");
		add("GYPE");
		add("RBM38");
		add("TRIM58");
		add("C20orf108");
		add("ALAS2");
		add("PDZK1IP1");
		add("RPIA");
		add("KRT1");
		add("TMOD1");
		add("MICALCL");
		add("RUNDC3A");
		add("XK");
		add("VWCE");
		add("SLC4A1");
		add("FECH");
		add("RAP1GAP");
		add("OSBP2");
		add("C1orf128");
		add("MXI1");
		add("SLC6A8");
		add("C14orf45");
		add("TNS1");
		add("EPB42");
		add("HBD");
		add("HPS1");
		add("MARCH8");
		add("BPGM");
		add("ALDH5A1");
		add("RBM38");
		add("HEMGN");
		add("LOC100131164");
		add("EPB41");
	
	
	}};
	
	StringBuffer sb = new StringBuffer();
	sb.append("https://www.ebi.ac.uk/QuickGO/services/geneproduct/search?taxonId=9606&query=");
	for(String s : symb) {
		sb.append(s+"%2C");
	}
	sb.delete(sb.length()-4, sb.length());
	System.out.println(sb.toString());
	  
  //  String requestURL = "https://www.ebi.ac.uk/QuickGO/services/geneproduct/search?taxonId=9606&query=CYP7A1&dbSubset=Swiss-Prot"; // separation %2C%
	String requestURL = sb.toString();
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
  }
}