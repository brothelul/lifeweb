package com.brotherlu.lifeweb.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.brotherlu.lifeweb.constants.CommonConstant;
import com.brotherlu.lifeweb.utils.HttpUtil;
import com.brotherlu.lifeweb.utils.JSONParseUtil;
import com.brotherlu.lifeweb.vo.CommonResultVo;

@Service
public class UserNoticesService {
	public String getUserNotices(Map<String,Object> map) {
		CommonResultVo resultVo = new CommonResultVo();
		String content = JSONParseUtil.Object2Json(map);
		String url = CommonConstant.HOST + "/life/group/notice/list";
		HttpUtil.doPost(url, content, resultVo);
		if (resultVo.getStatusCode() == 200) {
			String result = (String) resultVo.getData();
			Map<String, Object> resultData = (Map<String, Object>) JSONParseUtil.Json2Object(result, Map.class);
			return JSONParseUtil.Object2Json(resultData.get("data"));
		}
		return null;
	}
	
	public boolean createNewNotice(Map<String,Object> map) {
		CommonResultVo resultVo = new CommonResultVo();
		String content = JSONParseUtil.Object2Json(map);
		String url = CommonConstant.HOST + "/life/group/notice/add";
		HttpUtil.doPost(url, content, resultVo);
		if (resultVo.getStatusCode() == 200) {
			return true;
		}
		return false;
	}
}
