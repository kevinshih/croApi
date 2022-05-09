package croApi.service;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;


@Service
public class ApiService extends BaseService{

	public String getExtraRewards(String employee, int baseYearlyPackage, int year) {
		logger.info("{} is checking her {} year's extra rewards. The base yearly package is {}.",employee,yearString(year),baseYearlyPackage);
		String requestCandlestickByDay = "https://api.crypto.com/v2/public/get-candlestick?instrument_name=CRO_USDT&timeframe=1D";
		JSONObject jsonObj = null;
		String responseJson = "";
		try {
			jsonObj = getApiData(requestCandlestickByDay);
			responseJson = processExtraRewards(jsonObj,baseYearlyPackage,year);
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseJson;
	}
	
	
	/**
	 * VMAP is reference from : https://www.investopedia.com/terms/v/vwap.asp
	 * @param jsonObj
	 * @param baseYearlyPackage
	 * @param year
	 * @return
	 * @throws JSONException
	 * @throws IOException 
	 */
	private String processExtraRewards(JSONObject jsonObj, int baseYearlyPackage, int year) throws JSONException, IOException {
		
		JSONArray results = jsonObj.getJSONObject("result").getJSONArray("data");
		long allTransaction = 0;
		long allVolume = 0;
		for ( int i = 0; i < 600; ++i ) {
            if(i<235) continue;
			JSONObject temp = results.getJSONObject(i);
			double typicalPrice = formatDouble((temp.getDouble("h") + temp.getDouble("l") + temp.getDouble("c"))/3);
			long totalTransaction = (long) (typicalPrice * temp.getLong("v"));
			allTransaction += totalTransaction;
			allVolume += formatDouble(temp.getLong("v"));
        }

		logger.info("The past 365 days' Transaction:{}",Long.toString(allTransaction));
		logger.info("The past 365 days' Volume:{}",Long.toString(allVolume));
		double vwap = formatDouble((double)allTransaction / (double)allVolume);
		
		logger.info("VWAP for the past 365 days:{}",Double.toString(vwap));
		double firstYearRewards = (baseYearlyPackage * year / 10) * vwap;
		logger.info("The First Year Rewards: {}",Double.toString(firstYearRewards));
		HashMap<String,Double> map = getCurrency();
		String responseJson = "{\"The past 365 days' Transaction\":"+Double.toString(allTransaction)+","
				+ "\"The past 365 days' Volume\":"+Double.toString(allVolume)+","
				+ "\"VWAP for the past 365 days\":"+Double.toString(vwap)+","
				+ "\"The "+ yearString(year) +" Year Rewards in USD.\":"+Double.toString(firstYearRewards)+","
				+ "\"The "+ yearString(year) +" Year Rewards in Euro.\":"+Double.toString(firstYearRewards*map.get("Euro"))+","
				+ "\"The "+ yearString(year) +" Year Rewards in HKD.\":"+Double.toString(firstYearRewards*map.get("Hong Kong Dollar"))+","
				+ "\"The "+ yearString(year) +" Year Rewards in TWND.\":"+Double.toString(firstYearRewards*map.get("Taiwan New Dollar"))+"}";
		return responseJson;
	}
	
	private HashMap<String, Double> getCurrency() throws IOException {
		
		Document doc = Jsoup.connect("https://www.x-rates.com/table/?from=USD&amount=1")
				.data("query", "Java").userAgent("Chrome/50.0.2661.87").timeout(3000).post();
		Element content = doc.getElementById("content").child(0).child(0).child(0).child(0).child(0).siblingElements().get(5).child(0).siblingElements().get(0);
		HashMap<String,Double> map = new HashMap<>();
		for(Element e: content.children()) {
			map.put(e.child(0).text(), Double.parseDouble(e.child(0).siblingElements().get(0).text()));
		}
		System.out.println("Euro:"+map.get("Euro"));
		System.out.println("Hong Kong Dollar:"+map.get("Hong Kong Dollar"));
		System.out.println("Taiwan New Dollar:"+map.get("Taiwan New Dollar"));
		return map;
	}
	
}
