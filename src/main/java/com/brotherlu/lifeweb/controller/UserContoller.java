package com.brotherlu.lifeweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserContoller {
	
	@RequestMapping("user/login")
	public String userLogin() {
		
		return "login";
	}
	
	@RequestMapping("user/index")
	public String toIndex() {
		
		return "index";
	}
	
	@RequestMapping("user/order")
	public String toOrder() {
		
		return "order";
	}
}
