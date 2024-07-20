package com.scm.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.scm.services.EmailService;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSeviceImpl implements EmailService {

    @Value("${spring.mail.properties.domain_name}")
    private String domainName;

    @Autowired
    private JavaMailSender javaMailSender;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(EmailSeviceImpl.class);

    @Override
    public boolean sendEmail(String message, String subject, String sender, String recipient, String username,
            String password) {

        boolean flag = false;

        String host = "smtp-mail.outlook.com";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        logger.info("Email has been sent..");

        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }

        });

        session.setDebug(true);

        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom(sender);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mimeMessage.setSubject(subject);
            // mimeMessage.setText(message);
            mimeMessage.setContent(message, "text/html");

            Transport.send(mimeMessage);
            System.out.println("Send email success...................");
            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    @Override
    public void sendEmail(String to, String subject, String messsage) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(messsage);
        simpleMailMessage.setFrom(domainName);
        javaMailSender.send(simpleMailMessage);

    }

    @Override
    public void sendEmail(String[] to, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom(domainName);

        javaMailSender.send(simpleMailMessage);

    }

    @Override
    public void sendEmailWithHtml(String to, String subject, String htmlContent) {

        MimeMessage simpleMailMessage = this.javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(simpleMailMessage, true, "UTF-8");

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setFrom(domainName);
            this.javaMailSender.send(simpleMailMessage);

        } catch (MessagingException e) {

            e.printStackTrace();
        }

    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, File file) {

        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(to);
            messageHelper.setFrom(domainName);
            messageHelper.setSubject(subject);
            messageHelper.setText(message);

            FileSystemResource fileSystemResource = new FileSystemResource(file);
            messageHelper.addAttachment(fileSystemResource.getFilename(), file);

            javaMailSender.send(mimeMessage);
            logger.info("Email is send with file using api with postman !!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, InputStream is) {

        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(to);
            messageHelper.setFrom("at2384828@gmail.com");
            messageHelper.setSubject(subject);
            messageHelper.setText(message);

            File file = new File("src/main/resources/emailTemplates/test.png");
            Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileSystemResource fileSystemResource = new FileSystemResource(file);

            messageHelper.addAttachment(fileSystemResource.getFilename(), file);

            javaMailSender.send(mimeMessage);
            logger.info("Email is send with file using api with postman !!");

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
