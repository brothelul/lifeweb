package com.brotherlu.lifeweb.utils;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import freemarker.template.Configuration;
import freemarker.template.Template;


@Component
public class MailUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;
    
    @Autowired  
    Configuration configuration; 

    /**
     * 异步执行
     * @param deliver
     * @param receiver
     * @param carbonCopy
     * @param subject
     * @param template
     * @param context
     * @throws Exception
     */
    @Async
    public void sendTemplateEmail(String deliver, String[] receiver, String[] carbonCopy, String subject, String template, Context context) throws Exception {
        long startTimestamp = System.currentTimeMillis();
        logger.info("Start send mail ...");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            String content = templateEngine.process(template, context);
            if (StringUtils.isEmpty(deliver)) {
				deliver = "Life Admin<brotherlu@aliyun.com>";
			}            
            messageHelper.setFrom(deliver);          
            messageHelper.setTo(receiver);
            if (carbonCopy != null) {
                messageHelper.setCc(carbonCopy);
			}
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);

            mailSender.send(message);
            logger.info("Send mail to success, cost {} million seconds", System.currentTimeMillis() - startTimestamp);
        } catch (MessagingException e) {
            logger.error("Send mail failed "+receiver+", error message is {} \n", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Async
    public void sendTemplateEmail(String deliver, String[] receiver, String[] carbonCopy, String subject, String template, Map context) throws Exception {
        long startTimestamp = System.currentTimeMillis();
        logger.info("Start send mail ...");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);

            Template t = configuration.getTemplate(template+".html");
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(t, context);
            if (StringUtils.isEmpty(deliver)) {
				deliver = "Life Admin<brotherlu@aliyun.com>";
			}            
            messageHelper.setFrom(deliver);          
            messageHelper.setTo(receiver);
            if (carbonCopy != null) {
                messageHelper.setCc(carbonCopy);
			}
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);

            mailSender.send(message);
            logger.info("Send mail to success, cost {} million seconds", System.currentTimeMillis() - startTimestamp);
        } catch (MessagingException e) {
            logger.error("Send mail failed "+receiver+", error message is {} \n", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}