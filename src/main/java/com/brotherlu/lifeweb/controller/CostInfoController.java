package com.brotherlu.lifeweb.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brotherlu.lifeweb.service.CostInfoService;
import com.brotherlu.lifeweb.utils.JSONParseUtil;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.CostInfoSummary;
import com.brotherlu.lifeweb.vo.UserInfoVo;

@Controller
public class CostInfoController {
	
	private Logger logger = LoggerFactory.getLogger(CostInfoController.class);
	
	@Autowired
	private CostInfoService costInfoService;
	
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

			String costInfo = 
					costInfoService.findCostByCondition(requestParams);
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
			Integer userNo = (Integer) requestParams.get("user_no");
			boolean onlyMe = (boolean) requestParams.get("only_me");
			Integer typeNo = (Integer) requestParams.get("type_no");
			if (onlyMe) {
				userNo = userInfoVo.getUserNo();
				requestParams.put("user_no",userNo);
			}
			requestParams.remove("only_me");
			logger.info(">>>>>>>>>>>>>>>>>>user input info:"+JSONParseUtil.Object2Json(requestParams)+">>>>>>>>>>>>>>>>");
			
//			if(userNo == null && (typeNo == null || typeNo ==-1)){
//				userNo = userInfoVo.getUserNo();
//				requestParams.put("user_no",userNo);
//				requestParams.put("type_no",-1);
//			}

			String costInfo = 
					costInfoService.findUserTotalCost(requestParams);
			resultVo.setStatusCode(200);
			resultVo.setData(costInfo);
			resultVo.setSuccess(CommonResultVo.SUCCESS);
		}
		return resultVo;
	}
}
