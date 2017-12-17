package com.brotherlu.lifeweb.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brotherlu.lifeweb.service.CostTypeService;
import com.brotherlu.lifeweb.vo.CommonResultVo;
import com.brotherlu.lifeweb.vo.CostTypeVo;
import com.brotherlu.lifeweb.vo.UserInfoVo;

@Controller
public class CostTypeController {

	@Autowired
	private CostTypeService costTypeService;
	
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
			String costTypeVo = costTypeService.getUserCostType(userNo);
			resultVo.setStatusCode(200);
			resultVo.setData(costTypeVo);
			resultVo.setSuccess(CommonResultVo.SUCCESS);
		}
		return resultVo;
	}
}
