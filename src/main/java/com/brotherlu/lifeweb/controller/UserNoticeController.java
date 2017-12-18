package com.brotherlu.lifeweb.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brotherlu.lifeweb.service.UserNoticesService;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.UserInfoVo;

@Controller
public class UserNoticeController {
	
	private Logger logger = LoggerFactory.getLogger(UserNoticeController.class);
	
	@Autowired
	private UserNoticesService userNoticeService;
	@Autowired
	private RedisTemplate redisTemplate;
	
	@GetMapping("/user/notices")
	@ResponseBody
	public CommonResultVo getUserNotices(HttpSession session) {
		CommonResultVo resultVo = new CommonResultVo();
		UserInfoVo userInfoVo = (UserInfoVo) session.getAttribute("lifeUser");
		Integer userNo = userInfoVo.getUserNo();
		String userNotice;
		
		String key = "getUserNotices_"+userNo;
		ValueOperations operations = redisTemplate.opsForValue();
		if (redisTemplate.hasKey(key)) {
			userNotice = (String) operations.get(key);
			logger.info(">>>>>>>>>>get user notice form cache, user notices:"+userNotice+">>>>>>>>>>>>>>>>>>>");
		} else{
			Map<String, Object> map = new HashMap<>();
			map.put("user_no", userNo);
			userNotice = userNoticeService.getUserNotices(map);
			operations.set(key, userNotice);
			logger.info(">>>>>>>>>>>>>set the user notice into cache, user notice:"+userNotice+">>>>>>>>>>>>>>>");
		}	
	
		resultVo.setData(userNotice);
		resultVo.setStatusCode(200);
		resultVo.setSuccess(CommonResultVo.SUCCESS);
		return resultVo;
	}
	
	@PostMapping("/user/new/notice")
	@ResponseBody
	public CommonResultVo createNewNotice(@RequestBody Map<String, Object> requestParams,
			HttpSession session) {
		CommonResultVo resultVo = new CommonResultVo();
		UserInfoVo userInfoVo = (UserInfoVo) session.getAttribute("lifeUser");
		requestParams.put("user_no", userInfoVo.getUserNo());
		
		boolean result = userNoticeService.createNewNotice(requestParams);
		resultVo.setStatusCode(result ? 200 : 500);
		resultVo.setMsg(result ? userInfoVo.getUsername() : "创建通知失败");
		resultVo.setSuccess(result ? CommonResultVo.SUCCESS : CommonResultVo.FAIL);
		/** if save the notice successfully, then put to cache **/
		if (result) {
			String key = "getUserNotices_"+userInfoVo.getUserNo();
			redisTemplate.delete(key);
			logger.info(">>>>>>>>>>>>>>>>>>>remove the user notice cache successfully<<<<<<<<<<<<<<<");
		}
		return resultVo;
	}
}
