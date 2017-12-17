package com.brotherlu.lifeweb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

public class FormatUtil {
	public static Date String2Date(String DateString, String pattern) throws ParseException{
		if (StringUtils.isEmpty(DateString)) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.parse(DateString);
	}
	
	public static String Date2String(Date date, String pattern){
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
}
