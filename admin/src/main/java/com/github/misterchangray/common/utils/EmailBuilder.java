package com.github.misterchangray.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * 简单的邮件发送器;
 * 请注意此工具类需要注入使用
 * @author Created by rui.zhang on 2018/6/2.
 * @author rui.zhang
 * @version ver1.0
 * @email misterchangray@hotmail.com
 * @description
 */
@Component
public class EmailBuilder extends  JavaMailSenderImpl{
    private static String emailHost;
    private static Integer emailPort;
    private static String emailUsername;
    private static String emailPassword;
    private static EmailBuilder emailBuilder = new EmailBuilder();
    private static Properties properties = new Properties();


    public static void main(String[] args) throws IOException {
        EmailBuilder.build().sendSimpleEmail("jioulongzi@qq.com", "914590431@qq.com", "hello", "world");
    }


    /**
     * 发送简单邮件
     * @param from 发件人
     * @param to 收件人
     * @param subject 主题
     * @param text 正文
     */
    public void sendSimpleEmail(String from, String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();//消息构造器
        message.setFrom(from);//发件人
        message.setTo(to);//收件人
        message.setSubject(subject);//主题
        message.setText(text);//正文
        this.send(message);
    }


    /**
     * 构建一个邮件发送器
     * @return
     */
    public static EmailBuilder build() {
        emailBuilder.setHost(emailHost);//指定用来发送Email的邮件服务器主机名
        emailBuilder.setPort(emailPort);//默认端口，标准的SMTP端口
        emailBuilder.setUsername(emailUsername);//用户名
        emailBuilder.setPassword(emailPassword);//密码
        emailBuilder.setProtocol("smtp");
        // 设定properties
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.ssl.enable", true);
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.timeout", 25000);
        emailBuilder.setJavaMailProperties(properties);

        return emailBuilder;
    }





    /** 以下为辅助方法和注入方法 **/
    private EmailBuilder() {}

    @Value("${email.host}")
    public void setEmailHost(String emailHost) {
        EmailBuilder.emailHost = emailHost;
    }

    @Value("${email.port}")
    public void setEmailPort(Integer emailPort) {
        EmailBuilder.emailPort = emailPort;
    }
    @Value("${email.username}")
    public void setEmailUsername(String emailUsername) {
        EmailBuilder.emailUsername = emailUsername;
    }
    @Value("${email.password}")
    public void setEmailPassword(String emailPassword) {
        EmailBuilder.emailPassword = emailPassword;
    }
}
