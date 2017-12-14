package com.brotherlu.lifeweb.vo;

import java.io.Serializable;
import java.util.Date;

public class UserInfoVo implements Serializable {

    private Integer userNo;

    private String username;

    private Integer phone;

    private String email;

    private Date entryDate;

    private String entryIp;

	public Integer getUserNo() {
		return userNo;
	}

	public void setUserNo(Integer userNo) {
		this.userNo = userNo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getPhone() {
		return phone;
	}

	public void setPhone(Integer phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getEntryIp() {
		return entryIp;
	}

	public void setEntryIp(String entryIp) {
		this.entryIp = entryIp;
	}

}
