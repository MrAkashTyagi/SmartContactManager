package com.scm.controllers;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.enities.User;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgetController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    Random random = new Random(1000);

    // email id form open handler
    @RequestMapping("/forget")
    public String openEmailForm() {
        return "user/forget_email_form";
    }

    // send otp handler
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email, HttpSession session) {
        System.out.println("Email is : " + email);

        // generating otp of 4 digit

        int otp = random.nextInt(9999);
        System.out.println("OTP is : " + otp);

        // otp send to email code here

        String subject = "Test email";
        String message = ""
                + "<div style='border:1px solid #e2e2e2'>"
                + "<h3>"
                + "OTP is "
                + "<b>"
                + otp
                + "</b>"
                + "</h3>"
                + "</div>";

        String sender = "sender_email";
        String recipient = email;
        String username = "sender_username";
        String password = "senderEmail_pasword";
        boolean flag = this.emailService.sendEmail(message, subject, sender, recipient, username, password);


        if (flag) {
            session.setAttribute("otpFromMail", otp);
            session.setAttribute("email", email);
            return "user/verify_otp_form";

        } else {
            session.setAttribute("message",
                    Message.builder().content("Check your email address !!").type(MessageType.red).build());
            return "redirect:/user/forget_email_form";
        }

        // return "user/verify_otp_form";

    }

    // verify otp handler

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession session, Authentication authentication) {
        int otpFromMail = (int) session.getAttribute("otpFromMail");
        String email = (String) session.getAttribute("email");
        System.out.println("OTP is : " + otp);
        System.out.println("Otp from email is : " + otpFromMail);
        System.out.println("Email from mail is : " + email);
        if (otpFromMail == otp) {
            // password change form

            // String userName = Helper.getEmailOfLoggedInUser(authentication);
            User user = userService.getUserByEmail(email);

            if (user == null) {
                // send error message
                session.setAttribute("message",
                        Message.builder().content("User does not exists with this email !!").type(MessageType.red)
                                .build());
                return "redirect:/user/forget_email_form";
            } else {
                // send change password form
            }

            return "user/password_change_form";
        } else {

            session.setAttribute("message",
                    Message.builder().content("Entered Otp is not correct !!").type(MessageType.red).build());
            return "user/verify_otp_form";
        }

    }

    // change password handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {

        // get user from session
        String email = (String) session.getAttribute("email");
        User user = userService.getUserByEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(newpassword));
        userRepo.save(user);
        session.setAttribute("message",
                Message.builder().content("Password is changed successfully !! ").type(MessageType.green).build());

        return "redirect:/login?change=password changed successfully";
    }

}
