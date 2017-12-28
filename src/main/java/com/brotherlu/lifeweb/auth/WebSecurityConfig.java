package com.brotherlu.lifeweb.auth;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.thymeleaf.context.Context;

import com.brotherlu.lifeweb.constants.CommonConstant;
import com.brotherlu.lifeweb.utils.FormatUtil;
import com.brotherlu.lifeweb.utils.MailUtil;

@Configuration
public class WebSecurityConfig extends WebMvcConfigurerAdapter {

    /**
     * 登录session key
     */
    public final static String SESSION_KEY = "lifeUser";

    @Bean
    public SecurityInterceptor getSecurityInterceptor() {
        return new SecurityInterceptor();
    }
    
	@Autowired
	private MailUtil mailUtil;	
	
	/**
	 * 异常处理
	 */
	@Override
	public void configureHandlerExceptionResolvers(
			List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(new HandlerExceptionResolver() {
			
			@Override
			public ModelAndView resolveException(HttpServletRequest request,
					HttpServletResponse response, Object object, Exception e) {
				
	            String[] receiver = {"1285823170@qq.com"};
	            String[] carbonCopy = {"1285823170@qq.com"};
	            String template = CommonConstant.EMAIL_ERROR;
	            String subject = "Life web internal error";
	            Context context = new Context();
	            String errorMessage;
				
	            Writer writer = new StringWriter();
	            PrintWriter printWriter = new PrintWriter(writer);
	            e.printStackTrace(printWriter);
	            errorMessage = writer.toString();
	            
	            context.setVariable("username", "Admin");
	            context.setVariable("methodName","");
	            context.setVariable("errorMessage", errorMessage);
	            context.setVariable("occurredTime", FormatUtil.Date2String(new Date(), CommonConstant.LIFE_DATE_PATTERN));

	            try {	       
	                mailUtil.sendTemplateEmail(null, receiver, carbonCopy, subject, template, context);
	            } catch (Exception ec) {
	                ec.printStackTrace();
	            }
	            
				return new ModelAndView("/error/500");
			}
		});
		//super.configureHandlerExceptionResolvers(exceptionResolvers);
	}

	/**
	 * 页面拦截
	 */
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration addInterceptor = registry.addInterceptor(getSecurityInterceptor());

        // 排除配置
        addInterceptor.excludePathPatterns("/error");
        addInterceptor.excludePathPatterns("/user/login");
        addInterceptor.excludePathPatterns("/login");
//        addInterceptor.excludePathPatterns("/js/*");
//        addInterceptor.excludePathPatterns("/imag/*");
//        addInterceptor.excludePathPatterns("/fonts/*");
//        addInterceptor.excludePathPatterns("/css/*");

        // 拦截配置
        addInterceptor.addPathPatterns("/**");
    }

    private class SecurityInterceptor extends HandlerInterceptorAdapter {
 
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            HttpSession session = request.getSession();
            if (session.getAttribute(SESSION_KEY) != null)
                return true;

            // 跳转登录
            String url = "/login";
            response.sendRedirect(url);
            return false;
        }     
    }
}
