package com.brotherlu.lifeweb.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.brotherlu.lifeweb.service.CostInfoService;
import com.brotherlu.lifeweb.service.CostTypeService;
import com.brotherlu.lifeweb.utils.JSONParseUtil;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.UserInfoVo;

@Controller
public class CostInfoController {
	
	private Logger logger = LoggerFactory.getLogger(CostInfoController.class);
	
	@Autowired
	private CostInfoService costInfoService;
	@Autowired
	private CostTypeService costTypeService;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * the common request
	 * {
	 * 	"type_no":"", //类型待议
	 * 	"start_date":"",
	 * 	"end_date":"";
	 *	"only_me":ture,
	 *	"start_page": ,
	 *	"page_size":
	 * }
	 * 
	 * @param requestParams
	 * @param session
	 * @return
	 */
	@PostMapping("user/cost/info")
	@ResponseBody
	public CommonResultVo findCostByCondition(@RequestBody Map<String,Object> requestParams, 
			HttpSession session){
		UserInfoVo userInfoVo = (UserInfoVo) session.getAttribute("lifeUser");
		CommonResultVo resultVo = new CommonResultVo();
		if (userInfoVo == null) {
			resultVo.setStatusCode(401);
			resultVo.setMsg("请先登录");
			resultVo.setSuccess(CommonResultVo.FAIL);
		}else{
			/** redis get info **/
//			String key = "findCostByCondition_"+userInfoVo.getUserNo();
//			boolean hasValue = redisTemplate.hasKey(key);
//			ValueOperations<String, String> operations = redisTemplate.opsForValue();		
//			String costInfo;
//			if (hasValue) {
//				costInfo =  operations.get(key);
//				logger.info(">>>>>>>>>>>>>>>>get the cost info from cache, cost info:"+costInfo+">>>>>>>>>>>>>>>>");
//			}else{
				Integer userNo = (Integer) requestParams.get("user_no");
				boolean onlyMe = (boolean) requestParams.get("only_me");
				Integer typeNo = (Integer) requestParams.get("type_no");
				if (onlyMe) {
					userNo = userInfoVo.getUserNo();
					requestParams.put("user_no",userNo);
				}
				requestParams.remove("only_me");
				logger.info(">>>>>>>>>>>>>>>>>>user input info:"+JSONParseUtil.Object2Json(requestParams)+">>>>>>>>>>>>>>>>");
				
				if(userNo == null && (typeNo == null || typeNo ==-1)){
					userNo = userInfoVo.getUserNo();
					requestParams.put("user_no",userNo);
					requestParams.put("type_no",-1);
				}
				String costInfo = costInfoService.findCostByCondition(requestParams);
//				operations.set(key, costInfo);
//				logger.info(">>>>>>>>>>>>>>>>>put the cost info to the cache, cost info:"+costInfo+">>>>>>>>>>>>>>>>");
//			}
			resultVo.setStatusCode(200);
			resultVo.setData(costInfo);
			resultVo.setSuccess(CommonResultVo.SUCCESS);
		}
		return resultVo;
	}
	
	/**
	 * the simple request:
	 * {
     * 		"type_no":2,
     * 		"cost_money":"3.3",
     * 		"cost_date":"2017-12-14 20:12",
     * 		"cost_desc":"买菜"
	 * }
	 * @param requestParams
	 * @param session
	 * @return
	 */
	@PostMapping("user/cost/add")
	@ResponseBody
	public CommonResultVo addNewCost(@RequestBody Map<String, Object> requestParams,
			HttpSession session) {
		UserInfoVo userInfoVo = (UserInfoVo) session.getAttribute("lifeUser");
		CommonResultVo resultVo = new CommonResultVo();
		if (userInfoVo == null) {
			resultVo.setStatusCode(401);
			resultVo.setMsg("请先登录");
			resultVo.setSuccess(CommonResultVo.FAIL);
		}else{
			Integer userNo = userInfoVo.getUserNo();
			requestParams.put("user_no", userNo);
			
			boolean result = costInfoService.createNewCost(requestParams);
			resultVo.setStatusCode(result ? 200 : 500);
			resultVo.setMsg(result ? "创建成功" : "创建失败，请联系管理员。");
			resultVo.setSuccess(result ? CommonResultVo.SUCCESS : CommonResultVo.FAIL);
			resultVo.setData(result ? userInfoVo.getUsername() : null);
			if(result){
//				String key1 = "findCostByCondition_"+userNo;
				String key = "getTotalCost_"+userNo;
				if (redisTemplate.hasKey(key)) {
					redisTemplate.delete(key);
					logger.info(">>>>>>>>>>>>>>>>remove the toatal cost info from cache>>>>>>>>>>>>>>>>");
				}
			}
		}
		return resultVo;
	}
	
	@PostMapping("user/cost/total")
	@ResponseBody
	public CommonResultVo getTotalCost(@RequestBody Map<String, Object> requestParams,
			HttpSession session) {
		UserInfoVo userInfoVo = (UserInfoVo) session.getAttribute("lifeUser");
		CommonResultVo resultVo = new CommonResultVo();
		if (userInfoVo == null) {
			resultVo.setStatusCode(401);
			resultVo.setMsg("请先登录");
			resultVo.setSuccess(CommonResultVo.FAIL);
		}else{
			requestParams.remove("only_me");
			Integer userNo = userInfoVo.getUserNo();
			String typesKey = "getTypesByUser_"+userNo;
			ValueOperations operations = redisTemplate.opsForValue();
			
			List<Map> types;
			if (redisTemplate.hasKey(typesKey)) {
				types = (List<Map>) operations.get(typesKey);
				logger.info(">>>>>>>>>>>>>>>>get the types for cache, types"+JSONParseUtil.Object2Json(types)+">>>>>>>>>>>>>>>>>");
			} else{
				types = (List<Map>) costTypeService.getUserCostType(userInfoVo.getUserNo());
				operations.set(typesKey, types);
				logger.info(">>>>>>>>>>>>>>>>put the types into cache, types"+JSONParseUtil.Object2Json(types)+">>>>>>>>>>>>>>>>>");
			}
			/** get user typeNos **/
			if (types != null) {
//				JSONArray typesArray = (JSONArray) JSONParseUtil.Json2Object(typeNos, JSONArray.class);
				JSONArray costInfos = new JSONArray();
				String userTotalCostKey = "getTotalCost_"+userNo;
				if (redisTemplate.hasKey(userTotalCostKey)) {
					costInfos = (JSONArray) operations.get(userTotalCostKey);
					logger.info(">>>>>>>>>>> get user total cost info from cache, info:"+costInfos.toJSONString()+">>>>>>>>>>>>>>>>");
				} else{
					for (Object object : types) {
						Map<String,Object> type = (Map)object;
						requestParams.put("type_no",type.get("value"));
						//request 
						JSONArray costInfo =
								(JSONArray) costInfoService.findUserTotalCost(requestParams);
						if (costInfo != null) {
							costInfos.addAll(costInfo);
						}
					}
					
					operations.set(userTotalCostKey, costInfos);
					logger.info(">>>>>>>>>>> put user total cost info into cache, info:"+costInfos.toJSONString()+">>>>>>>>>>>>>>>>");

				}
				resultVo.setStatusCode(200);
				resultVo.setData(costInfos.toJSONString());
				resultVo.setSuccess(CommonResultVo.SUCCESS);
			} else{
				resultVo.setStatusCode(200);
				resultVo.setData(null);
				resultVo.setSuccess(CommonResultVo.SUCCESS);
			}

//			Integer typeNo = (Integer) requestParams.get("type_no");

			logger.info(">>>>>>>>>>>>>>>>>>user input info:"+JSONParseUtil.Object2Json(requestParams)+">>>>>>>>>>>>>>>>");
	
		}
		return resultVo;
	}
}
