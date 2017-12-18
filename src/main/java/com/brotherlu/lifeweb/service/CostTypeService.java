package com.brotherlu.lifeweb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brotherlu.lifeweb.constants.CommonConstant;
import com.brotherlu.lifeweb.utils.HttpUtil;
import com.brotherlu.lifeweb.utils.JSONParseUtil;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.CostTypeVo;

@Service
public class CostTypeService {
	private Logger logger = LoggerFactory.getLogger(CostTypeService.class);
	
	/**
	 *  get user's types by userNo
	 *  request params :
	 *  user_no
	 * @param userNo
	 * @return
	 */
	public Object getUserCostType(Integer userNo){
		CommonResultVo resultVo = new CommonResultVo();
		String url = CommonConstant.HOST + "/life/cost/type/list/"+userNo;
		HttpUtil.doPost(url, null, resultVo);
		if (resultVo.getStatusCode() == 200) {
			String returnData = (String) resultVo.getData();
			Map<String, Object> map = (Map<String, Object>) JSONParseUtil.Json2Object(returnData, Map.class);
			JSONArray jsonArray = (JSONArray) map.get("data");
			List<Map<String, Object>> result = new ArrayList<>();
			/** return a jsonArray, parse **/
			if (jsonArray != null && jsonArray.size() > 0) {
				for(int i = 0; i < jsonArray.size(); i++) {
					Map<String, Object> resultMap = new HashMap<>();
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					resultMap.put("value", jsonObject.get("typeNo"));
					resultMap.put("text", jsonObject.get("typeName"));
					result.add(resultMap);
				}
			}
			return result;
		}
		return null;
	}
}
