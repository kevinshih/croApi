package croApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import croApi.service.ApiService;

@RestController
@RequestMapping("/api")
public class ApiController extends BaseController {
	
	@Autowired
	private ApiService apiService;

	/**
	 * http://localhost:9003/croApi/api/getExtraRewards/?employee=123&baseYearlyPackage=2000000&year=1
	 * complexity O() 
	 * @param employee
	 * @return
	 */
	@GetMapping("/getExtraRewards")
	public String getExtraRewards(String employee, int baseYearlyPackage, int year){
		if(year<1 || year>4)
			return "{"+FAIL+":\"parameter \"year\" need between 1-4.\"}";
		return apiService.getExtraRewards(employee, baseYearlyPackage, year);
	}
}
