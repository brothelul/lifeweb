package com.brotherlu.lifeweb.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brotherlu.lifeweb.service.UserService;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.UserInfoVo;

@Controller
public class UserContoller {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("user/login")
	@ResponseBody
	public CommonResultVo userLogin(@RequestBody Map<String, Object> requestParams,
			HttpSession session) {
		CommonResultVo resultVo = new CommonResultVo();
    	String username = (String) requestParams.get("username");
    	String password = (String) requestParams.get("password");
    	UserInfoVo userInfoVo = userService.userLogin(username, password);
    	
    	/** 用户信息为空则返回login  **/
    	if (userInfoVo == null) {
    		resultVo.setStatusCode(401);
    		resultVo.setSuccess(CommonResultVo.FAIL);
    		resultVo.setMsg("用户名或密码不正确");
			return resultVo;
		} else {
	    	session.setAttribute("lifeUser", userInfoVo); 
    		resultVo.setStatusCode(200);
    		resultVo.setMsg(userInfoVo.getUsername());
    		resultVo.setSuccess(CommonResultVo.SUCCESS);
		}
    	   	
		return resultVo;
	}
	
//	public CommonResultVo findUserTotalCost() {
//		
//	}
	
	@RequestMapping("user/index")
	public String toIndex() {
		
		return "index";
	}
	
	@RequestMapping("/login")
	public String toLogin() {
		
		return "login";
	}
	
	@RequestMapping("user/order")
	public String toOrder() {
		
		return "order";
	}
	
}
