package croApi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class BaseService {
	
	public Logger logger = LoggerFactory.getLogger(getClass());
	public static String SUCCESS = "SUCCESS";
	public static String FAILE = "FAILE";
	
	protected JSONObject getApiData(String requestURL) throws IOException, JSONException {
		
		JSONObject jsonObj = null;
		URL url = new URL(requestURL);
		HttpsURLConnection conn  = (HttpsURLConnection)url.openConnection();
		InputStream is = conn.getInputStream();
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    StringBuilder sb = new StringBuilder();
	    
	    String inputLine = "";
	    while ((inputLine = br.readLine()) != null) {
	        sb.append(inputLine);
	    }
	    jsonObj = new JSONObject(sb.toString());
	    br.close();
	    isr.close();
	    is.close();
	    conn.disconnect();
		return jsonObj;
	}
	
	protected double formatDouble(double number) {
		BigDecimal bd = new BigDecimal(number).setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	protected String yearString(int year) {
		switch(year) {
			case 1:
				return "1st";
			case 2:
				return "2ed";
			case 3:
				return "3rd";
			case 4:
				return "4th";
		}
		return FAILE;
	}
}