package com.brotherlu.lifeweb.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.brotherlu.lifeweb.constants.CommonConstant;
import com.brotherlu.lifeweb.utils.HttpUtil;
import com.brotherlu.lifeweb.utils.JSONParseUtil;
import com.brotherlu.lifeweb.utils.Md5Util;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.UserInfoVo;

@Service
public class UserService {
	
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	public UserInfoVo userLogin(String username, String password) {
		/** user login**/
		CommonResultVo resultVo = new CommonResultVo();
		String url = CommonConstant.HOST+"/life/user/login";
		Map<String, String> userLogin = new HashMap<>();
		password = Md5Util.pwdDigest(password);
		userLogin.put("username", username);
		userLogin.put("password", password);
		String content = JSONParseUtil.Object2Json(userLogin);
		logger.info(">>>>>>>>>>>>>user login info: username="+username +" password="+password+">>>>>>>>>>>>>");
		HttpUtil.doPost(url, content, resultVo);
		
		/** get user info**/
		if(resultVo.getStatusCode() == 200){
			String httpReturn = (String) resultVo.getData();
			logger.info(">>>>>>>>>>>>>>user login return:"+httpReturn+">>>>>>>>>>>>>>>>>>>>");
			
			Integer userNo = null;
			Map<String, Object> resultMap = (Map<String, Object>) JSONParseUtil.Json2Object(httpReturn, Map.class);
			if ((int)resultMap.get("code") == 200) {
				userNo = (int)resultMap.get("data");
			}
			if(userNo != null) {
				UserInfoVo  userInfo;
				String key  = "userLogin_"+userNo;
				ValueOperations operations = redisTemplate.opsForValue();
				if (redisTemplate.hasKey(key)) {
					userInfo = (UserInfoVo) operations.get(key);
					logger.info(">>>>>>>>>>>>>get the user info from cache, user info:"+userInfo.toString()+">>>>>>>>>>>>>>");
					return userInfo;
				} else{
					String infoUrl = CommonConstant.HOST+"/life/user/info/detail/"+userNo;
					CommonResultVo userInfoResult = new CommonResultVo();
					HttpUtil.doPost(infoUrl, "", userInfoResult);
					if(userInfoResult.getStatusCode() == 200) {
						Map<String, Object> userInfoResultMap = (Map<String, Object>) 
								JSONParseUtil.Json2Object((String)userInfoResult.getData(), Map.class);
						String userInfoJson = 
								JSONObject.toJSONString(userInfoResultMap.get("data"));
						logger.info(">>>>>>>>>>>>>>>>>>>>>>user Info:"+userInfoJson+">>>>>>>>>>>>>>>>>>>>>>");
						userInfo = (UserInfoVo) 
								JSONParseUtil.Json2Object(userInfoJson,UserInfoVo.class);
						
						operations.set(key, userInfo);
						logger.info(">>>>>>>>>>>>>set the user notice into cache, user notice:"+userInfo.toString()+">>>>>>>>>>>>>>>");
						return userInfo;
					}
				}
			}
		}
		
		return null;
	}
}
