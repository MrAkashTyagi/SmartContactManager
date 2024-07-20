package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.enities.Contact;
import com.scm.enities.User;
import com.scm.forms.UserForm;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {

        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String pageLoad(Model model) {

        model.addAttribute("name", "Akash");
        model.addAttribute("Youtube", "learncode with me");
        return "home";

    }

    // about route

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        return "about";
    }

    // services route
    @RequestMapping("/service")
    public String servicePage() {
        return "service";

    }

    // contact route

    @RequestMapping("/contact")
    public String contact() {
        return "contact";
    }

    // this is showing login page

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    // registration page

    @RequestMapping("/register")
    public String register(Model model) {
        UserForm userForm = new UserForm();
        // default data bhi daal skte hn
        // userForm.setName("Akash");
        // userForm.setEmail("akash@gmail.com");
        model.addAttribute("userForm", userForm);
        return "register";
    }

    // processing registre form
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult,
            HttpSession session) {
        System.out.println(userForm);

        // fetch the form data

        // UserForm

        // validate form data
        // ToDo
        if (bindingResult.hasErrors()) {
            return "register";

        }

        // save to database

        // userService

        // User user = User.builder()
        // .name(userForm.getName())
        // .email(userForm.getEmail())
        // .about(userForm.getAbout())
        // .phoneNumber(userForm.getPhoneNumber())
        // .password(userForm.getPassword())
        // .profilePic("https://www.freepik.com/free-vector/illustration-businessman_2606517.htm#query=default%20user&position=0&from_view=keyword&track=ais_user&uuid=926989ca-a26f-4817-8173-02896a74959e")
        // .build();

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setPassword(userForm.getPassword());
        user.setEnabled(false);
        user.setProfilePic(
                "https://www.freepik.com/free-vector/illustration-businessman_2606517.htm#query=default%20user&position=0&from_view=keyword&track=ais_user&uuid=926989ca-a26f-4817-8173-02896a74959e");

        User savedUser = userService.saveUser(user);
        System.out.println("user saved");

        // message

        // add the message

        Message message = Message.builder().content(
                "Registration Successful !! We have sent a verification email to your email id please click on verification link to verify !!")
                .type(MessageType.green).build();

        session.setAttribute("message", message);

        // redirect to login page

        return "redirect:/register";
    }

    // handle contact data

    @RequestMapping("/addContact")
    public String addUserContact(@ModelAttribute Contact contact, Model model) {
        System.out.println(contact);

        return "redirect:/user/contacts/addContact";
    }

}
