package com.brotherlu.lifeweb.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.apache.http.util.EntityUtils;

import com.brotherlu.lifeweb.constants.CommonConstant;
import com.brotherlu.lifeweb.exception.LifeHttpException;
import com.brotherlu.lifeweb.vo.CommonResultVo;

/**
 *  the http request util
 * @author mosesc
 *
 */
public class HttpUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	public static void doGet(String url, CommonResultVo resultVo){
		logger.info(">>>>>>>>request info:{url:"+url+"}>>>>>>>>>>>>>>>");
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			parseResponse(response,resultVo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LifeHttpException(e.getMessage());
		} 
	}
	public static void doPost(String url, String content, CommonResultVo resultVo){
		logger.info(">>>>>>>>request info:{url:"+url+",content:"+content+"}>>>>>>>>>>>>>>>");
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		if(!StringUtils.isEmpty(content)){
			StringEntity entity = new StringEntity(content,CommonConstant.UTF8);
			entity.setContentEncoding(CommonConstant.UTF8);
			entity.setContentType(CommonConstant.JSONHEADER);
			post.setEntity(entity);
		}
		
		post.setHeader("sign",CommonConstant.SIGN);
		
		try {
			HttpResponse response = client.execute(post);
			parseResponse(response,resultVo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LifeHttpException(e.getMessage());
		} 
	}
	
	private static void parseResponse(HttpResponse response, CommonResultVo resultVo) throws ParseException, IOException{
		HttpEntity entity = response.getEntity();
		resultVo.setStatusCode(response.getStatusLine().getStatusCode());
		resultVo.setSuccess(CommonResultVo.SUCCESS);
		if(entity != null){
			String body = EntityUtils.toString(entity, CommonConstant.UTF8);
			resultVo.setData(body);
			logger.info(">>>>>>>>>>>>response body:"+body+">>>>>>>>>>>>>>>>>");
		}
	}
	
	public static void main(String[] args) {
		doPost("http://localhost:8089/user/login","", new CommonResultVo());
	}
}
