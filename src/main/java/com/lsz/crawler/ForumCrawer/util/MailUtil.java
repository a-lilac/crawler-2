package com.lsz.crawler.ForumCrawer.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2016/11/28.
 */
public class MailUtil {
    private   String username=null;
    private  String password=null;
    private  String host=null;
    private String protocol=null;


    public  void readPro(){
        ResourceBundle resource=ResourceBundle.getBundle("mail");
         username=resource.getString("from");
         password=resource.getString("password");
         host=resource.getString("host");
         protocol=resource.getString("protocol");
    }
    public  void sendSimpleMail(String email,String subject,String context){
        readPro();
        Properties props=new Properties();
        //开启debug
        props.setProperty("mail.debug","true");
        //需要身份验证
        props.setProperty("mail.smtp.auth","true");
        //设置邮件服务器主机名
        props.setProperty("mail.host",host);
        //发送邮件协议名称
        props.setProperty("mail.transport.protocol",protocol);
        //设置环境
        Session session=Session.getInstance(props);
        try {
            //根据session得到transport对象
            Transport ts=session.getTransport();
            ts.connect(host,username,password);
            //创建邮件
            Message msg=createSimpleMail(session,email,subject,context);
            //发送邮件
            ts.sendMessage(msg,msg.getAllRecipients());
            ts.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建只包含文本邮件
     * @param session
     * @return
     */
    public  MimeMessage createSimpleMail(Session session,String email,String subject,String context) throws MessagingException {

        MimeMessage msg=new MimeMessage(session);
        //发件人
        msg.setFrom(new InternetAddress(username));
        //收件人
        msg.setRecipient(Message.RecipientType.TO,new InternetAddress(email));
        //标题
        msg.setSubject(subject);
        //文本内容
        msg.setContent(context,"text/html;charset=utf-8");
        //返回邮件对象
        return msg;
    }
    public  MimeMessage createImageMail(Session session,String email,String subject,String content,String imgpath) throws MessagingException, IOException {
        MimeMessage msg=new MimeMessage(session);
        //发件人
        msg.setFrom(new InternetAddress(username));
        //收件人
        msg.setRecipient(Message.RecipientType.TO,new InternetAddress(email));
        //标题
        msg.setSubject(subject);
        //邮件数据
        MimeBodyPart text=new MimeBodyPart();
        text.setContent(content,"text/html;charset=utf-8");
        //准备图片数据
        MimeBodyPart image=new MimeBodyPart();
        DataHandler dh=new DataHandler(new FileDataSource(imgpath));
        image.setDataHandler(dh);
        image.setContentID("xxx.jpg");
        //描述数据关系
        MimeMultipart mm=new MimeMultipart();
        mm.addBodyPart(text);
        mm.addBodyPart(image);
        mm.setSubType("related");

        msg.setContent(mm);
        msg.saveChanges();
        //创建好的邮件写到E盼以文件形式进行保存
        //msg.writeTo(new FileOutputStream("E:\\邮件发送\\email.eml"));
        //返回邮件
        return msg;
    }
    public void sendFileEmail(String subject, String sendHtml, String receiveUser, File attachment) throws MessagingException {
        readPro();
        Properties props=new Properties();
        //开启debug
        props.setProperty("mail.debug","true");
        //需要身份验证
        props.setProperty("mail.smtp.auth","true");
        //设置邮件服务器主机名
        props.setProperty("mail.host",host);
        //发送邮件协议名称
        props.setProperty("mail.transport.protocol",protocol);
        //设置环境
        Session session=Session.getInstance(props);
        MimeMessage message=new MimeMessage(session);
        Transport  transport=null;


        try {
            // 发件人
            InternetAddress from = new InternetAddress(username);
            message.setFrom(from);

            // 收件人
            InternetAddress to = new InternetAddress(receiveUser);
            message.setRecipient(Message.RecipientType.TO, to);
            // 邮件主题
            message.setSubject(MimeUtility.encodeWord(subject));
            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();

            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(sendHtml, "text/html;charset=utf-8");
            multipart.addBodyPart(contentPart);

            // 添加附件的内容
            if (attachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));

                // 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
                // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
                //sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                //messageBodyPart.setFileName("=?GBK?B?" + enc.encode(attachment.getName().getBytes()) + "?=");

                //MimeUtility.encodeWord可以避免文件名乱码
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
                multipart.addBodyPart(attachmentBodyPart);
            }

            // 将multipart对象放到message中
            message.setContent(multipart);
            // 保存邮件
            message.saveChanges();
            //传输
            transport = session.getTransport(protocol);
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(host, username, password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());

            System.out.println("send success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
