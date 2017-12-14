package com.brotherlu.lifeweb.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JSONParseUtil {
	/**
	 * 将JSON转为Object
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static Object Json2Object(String json, Class clazz) {
		return JSONObject.parseObject(json, clazz);
	}
	
	public static String Object2Json(Object object) {
		return JSON.toJSONString(object);
	}
	
//	public String json2LifeData(String json) {
//		JSONObject jsonObject = JSONObject.parseObject(json);
//		jsonObject.
//	}
	
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<>();
		map.put("username", "test");
		map.put("password", "1236");
		System.out.println(Object2Json(map));
	}
}
