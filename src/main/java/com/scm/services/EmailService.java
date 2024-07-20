package com.scm.services;

import java.io.File;
import java.io.InputStream;

public interface EmailService {

    // send email old way: using email service
    public boolean sendEmail(String message, String subject, String sender, String recipient, String username,
            String password);

    //send email to single person
    void sendEmail(String to, String subject, String messsage);

    //seding email to multiple persons
    void sendEmail(String []to, String subject, String message);

    //send email with HTML content
    void sendEmailWithHtml(String to, String subject, String htmlContent);

    //send email with file
    void sendEmailWithFile(String to, String subject, String message, File file);

    //send email with file
    void sendEmailWithFile(String to, String subject, String message, InputStream is);


}
