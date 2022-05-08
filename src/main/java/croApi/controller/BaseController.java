package croApi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseController {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	public static String SUCCESS = "SUCCESS";
	public static String FAIL = "FAIL";
}
