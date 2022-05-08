package croApi.service;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
	
	
	
	private String processExtraRewards(JSONObject jsonObj, int baseYearlyPackage, int year) throws JSONException {
		
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
		String responseJson = "{\"The past 365 days' Transaction\":"+Double.toString(allTransaction)+","
				+ "\"The past 365 days' Volume\":"+Double.toString(allVolume)+","
				+ "\"VWAP for the past 365 days\":"+Double.toString(vwap)+","
				+ "\"The "+ yearString(year) +" Year Rewards in USD.\":"+Double.toString(firstYearRewards)+"}";
		return responseJson;
	}
	
	
}
