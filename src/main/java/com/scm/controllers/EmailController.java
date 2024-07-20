package com.scm.controllers;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.scm.enities.User;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

// @RestController
@Controller
// @CrossOrigin("*")
@RequestMapping("/api/v1/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {
        // sending email
        emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessage());

        return ResponseEntity.ok(Message.builder().content("Email send successfully !!").build());
    }

    @PostMapping(value = "/send-with-file")
    public ResponseEntity<?> sendEmailWithHtml(@RequestPart EmailRequest emailRequest,
            @RequestPart("file") MultipartFile file) throws IOException {

        emailService.sendEmailWithFile(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessage(),
                file.getInputStream());
        return ResponseEntity.ok(Message.builder().content("Email send successfully !!").build());
    }

    // email verification handler
    @RequestMapping("/verify-email/{emailToken}")
    public String emailVerification(@PathVariable("emailToken") String emailToken, HttpSession session) {
        // User user = (User) session.getAttribute("savedUser");

        User user = userService.getUserByEmailToken(emailToken);        
        if (user.getEmailToken().equals(emailToken)) {
            user.setEnabled(true);
            user.setEmailVerified(true);
            userRepo.save(user);
            session.setAttribute("message",
                    Message.builder().content("User is verified successfully").type(MessageType.green).build());

        }

        // String emailTokenFromDatabase = user.getEmailToken();
        // if (emailTokenFromDatabase.equals(emailToken)) {
        // user.setEnabled(true);
        // user.setEmailVerified(true);
        // userRepo.save(user);
        // session.setAttribute("message",
        // Message.builder().content("Verification Successful
        // !!").type(MessageType.green).build());
        // }
        return "login";
    }

    // getting user by eamil token
    @RequestMapping("/getUserByEmailToken/{emailToken}")
    public String getUserByEmailToken(@PathVariable("emailToken") String emailToken) {
        // List<User> userList = this.userService.getUserByUserToken(emailToken);
        // for(User user : userList){
        // System.out.println("User Name is : "+user.getUsername());
        // }
        User user = this.userService.getUserByEmailToken(emailToken);
        System.out.println("Username now is : " + user.getUsername());
        return "";
    }

}
