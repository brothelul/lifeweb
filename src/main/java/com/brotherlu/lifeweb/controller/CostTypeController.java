package com.brotherlu.lifeweb.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brotherlu.lifeweb.service.CostTypeService;
import com.brotherlu.lifeweb.utils.JSONParseUtil;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.UserInfoVo;

@Controller
public class CostTypeController {

	private static Logger logger = LoggerFactory.getLogger(CostTypeController.class);
	
	@Autowired
	private CostTypeService costTypeService;
	@Autowired
	private RedisTemplate redisTemplate;
	
	@GetMapping("user/types")
	@ResponseBody
	public CommonResultVo getTypesByUser(HttpSession session){
		CommonResultVo resultVo = new CommonResultVo();
		UserInfoVo userInfoVo = (UserInfoVo) session.getAttribute("lifeUser");
		if (userInfoVo == null) {
			resultVo.setStatusCode(401);
			resultVo.setSuccess(CommonResultVo.FAIL);
			resultVo.setMsg("请先登录");
		}else{
			Integer userNo = userInfoVo.getUserNo();
			String key = "getTypesByUser_"+userNo;
			ValueOperations operations = redisTemplate.opsForValue();
			Object types;
			if (redisTemplate.hasKey(key)) {
				types = operations.get(key);
				logger.info(">>>>>>>>>>>>>>>>get the types info from cache, types:"+JSONParseUtil.Object2Json(types)+">>>>>>>>>>>");
			}else{
				types = costTypeService.getUserCostType(userNo);
				operations.set(key, types);
				logger.info(">>>>>>>>>>>>>>>>put the types into cache, types:"+JSONParseUtil.Object2Json(types)+">>>>>>>>>>>>>>>>>>>");
			}
			resultVo.setStatusCode(200);
			resultVo.setData(JSONParseUtil.Object2Json(types));
			resultVo.setSuccess(CommonResultVo.SUCCESS);
		}
		return resultVo;
	}
}
