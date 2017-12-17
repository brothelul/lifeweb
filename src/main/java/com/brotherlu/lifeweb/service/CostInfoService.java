package com.brotherlu.lifeweb.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import net.minidev.json.JSONUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.brotherlu.lifeweb.constants.CommonConstant;
import com.brotherlu.lifeweb.utils.FormatUtil;
import com.brotherlu.lifeweb.utils.HttpUtil;
import com.brotherlu.lifeweb.utils.JSONParseUtil;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.CostInfoSummary;

@Service
public class CostInfoService {
	private Logger logger = LoggerFactory.getLogger(CostInfoService.class);
	private static final int MAXSIZE = 10;
	
	/**
	 * common conditions has six fields 
	 * startpage|pagesizeuserNo|typeNo|startDate|endDate 
	 * this is for type: query_orders
	 * @param conditions
	 * @return
	 */
	public String findCostByCondition(Map<String,Object> conditions){
		Integer satrtPage = (Integer) conditions.get("start_page");
		Integer pageSize = (Integer) conditions.get("page_size");
		String sartDateString = (String) conditions.get("start_date");
		String endDateString = (String) conditions.get("end_date");
		
		/** validate the input info **/
		try {
			FormatUtil.String2Date(sartDateString, CommonConstant.WEB_DATE_PATTERN);
		} catch (ParseException e) {
			logger.warn(">>>>>>>>>>>>>这个小样输入的date：start——"+sartDateString +">>>>>>>>>>>>>>>>");
			return null;
		}
		
		try {
			FormatUtil.String2Date(sartDateString, CommonConstant.WEB_DATE_PATTERN);
		} catch (ParseException e) {
			logger.warn(">>>>>>>>>>>>>这个小样输入的date：end——"+endDateString+">>>>>>>>>>>>>>>>");
			return null;
		}
		
		if (StringUtils.isEmpty(sartDateString)) {
			sartDateString = "2012-7-12 12:00"; //别问我为什么，问女主人就好了
		}
		
		if (StringUtils.isEmpty(endDateString)) {
			endDateString = FormatUtil.Date2String(new Date(), CommonConstant.WEB_DATE_PATTERN);
		}
		
		if (pageSize == null || pageSize == 0){
			pageSize = MAXSIZE;
		}
		if (satrtPage == null) {
			satrtPage = 0;
		}else{
			satrtPage = pageSize*satrtPage;
		}				
		
		/** build the query info **/
		conditions.put("start_page", satrtPage);
		conditions.put("page_size", pageSize);
		conditions.put("mode_type", CommonConstant.QUERY_TYPE_ORDERS);
		
		CommonResultVo resultVo = new CommonResultVo();
		String costInfoJSON = JSONParseUtil.Object2Json(conditions);
		String url = CommonConstant.HOST+"/life/cost/list";
		HttpUtil.doPost(url, costInfoJSON, resultVo);
		
		/** parson response info **/
		if (resultVo.getStatusCode() == 200) {
			String dataJSON = (String) resultVo.getData();
			Map<String,Object> dataMap = 
					(Map<String, Object>) JSONParseUtil.Json2Object(dataJSON, Map.class); 
			String costInfo = JSONObject.toJSONString(dataMap.get("data"));
			return costInfo;
		}
		
		return null;
	}
	
	/**
	 * save the cost info from page
	 * the input params include:
	 * userNo|typeNo|costMoney|costDate|costDesc
	 * @param map
	 * @return
	 */
	public boolean createNewCost(Map<String, Object> map) {
		if (map.size() == 5) {
			/** format cost date **/
			CommonResultVo resultVo = new CommonResultVo();
			String costDateString = (String) map.get("cost_date");
			try {
				Date costDate = FormatUtil.String2Date(costDateString, CommonConstant.WEB_DATE_PATTERN);
				if (costDate == null) {
					costDate = new Date();
					costDateString = FormatUtil.Date2String(costDate, CommonConstant.WEB_DATE_PATTERN);
					map.put("cost_date", costDateString);
				}
			} catch (ParseException e) {
				logger.warn("<<<<<<<<<<<<<<<<<<format cost Date failed, cost_date:"+costDateString +"<<<<<<<<<<<<<<<");
				return false;
			}
			
			String costMoneyString = (String) map.get("cost_money");
			try {
				/** check if money value is validated **/
				BigDecimal costMoney = new BigDecimal(costMoneyString);
			} catch (Exception e) {
				return false;
			}
			
			/** create new cost **/
			String url = CommonConstant.HOST + "/life/cost/add";
			HttpUtil.doPost(url, JSONParseUtil.Object2Json(map), resultVo);
			
			/** parse response **/
			if (resultVo.getStatusCode() == 200) {
				String returnDate = (String) resultVo.getData();
				Map<String, Object> dataMap = 
						(Map<String, Object>) JSONParseUtil.Json2Object(returnDate, Map.class);
				if ((int)dataMap.get("code") == 200) {
					return true;
				}
			}
		}
		return false;
	}

	public String findUserTotalCost(Map<String, Object> conditions) {
		Integer satrtPage = (Integer) conditions.get("start_page");
		Integer pageSize = (Integer) conditions.get("page_size");
		String sartDateString = (String) conditions.get("start_date");
		String endDateString = (String) conditions.get("end_date");
		
		/** validate the input info **/
		try {
			FormatUtil.String2Date(sartDateString, CommonConstant.WEB_DATE_PATTERN);
		} catch (ParseException e) {
			logger.warn(">>>>>>>>>>>>>这个小样输入的date：start——"+sartDateString +">>>>>>>>>>>>>>>>");
			return null;
		}
		
		try {
			FormatUtil.String2Date(sartDateString, CommonConstant.WEB_DATE_PATTERN);
		} catch (ParseException e) {
			logger.warn(">>>>>>>>>>>>>这个小样输入的date：end——"+endDateString+">>>>>>>>>>>>>>>>");
			return null;
		}
		
		if (StringUtils.isEmpty(sartDateString)) {
			sartDateString = "2012-7-12 12:00"; //别问我为什么，问女主人就好了
		}
		
		if (StringUtils.isEmpty(endDateString)) {
			endDateString = FormatUtil.Date2String(new Date(), CommonConstant.WEB_DATE_PATTERN);
		}
		
		if (pageSize == null || pageSize == 0){
			pageSize = MAXSIZE;
		}
		if (satrtPage == null) {
			satrtPage = 0;
		}else{
			satrtPage = pageSize*satrtPage;
		}				
		
		/** build the query info **/
		conditions.put("start_page", satrtPage);
		conditions.put("page_size", pageSize);
		conditions.put("mode_type", CommonConstant.QUERY_USER_COST);
		
		CommonResultVo resultVo = new CommonResultVo();
		String costInfoJSON = JSONParseUtil.Object2Json(conditions);
		String url = CommonConstant.HOST+"/life/cost/list";
		HttpUtil.doPost(url, costInfoJSON, resultVo);
		
		/** parson response info **/
		if (resultVo.getStatusCode() == 200) {
			String dataJSON = (String) resultVo.getData();
			Map<String,Object> dataMap = 
					(Map<String, Object>) JSONParseUtil.Json2Object(dataJSON, Map.class); 
			String costInfo = JSONObject.toJSONString(dataMap.get("data"));
			return costInfo;
		}
		
		return null;
	}
}
