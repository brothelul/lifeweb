package com.brotherlu.lifeweb.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
				String infoUrl = CommonConstant.HOST+"/life/user/info/detail/"+userNo;
				CommonResultVo userInfoResult = new CommonResultVo();
				HttpUtil.doPost(infoUrl, "", userInfoResult);
				if(userInfoResult.getStatusCode() == 200) {
					Map<String, Object> userInfoResultMap = (Map<String, Object>) 
							JSONParseUtil.Json2Object((String)userInfoResult.getData(), Map.class);
					String userInfoJson = 
							JSONObject.toJSONString(userInfoResultMap.get("data"));
					logger.info(">>>>>>>>>>>>>>>>>>>>>>user Info:"+userInfoJson+">>>>>>>>>>>>>>>>>>>>>>");
					UserInfoVo  userInfo = (UserInfoVo) 
							JSONParseUtil.Json2Object(userInfoJson,UserInfoVo.class);
					return userInfo;
				}
			}
		}
		
		return null;
	}
}
