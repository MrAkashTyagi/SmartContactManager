package com.scm.controllers;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.scm.enities.MyOrder;
import com.scm.enities.User;
import com.scm.helper.Helper;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.repositories.MyOrderRepo;
import com.scm.repositories.UserRepo;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
// import lombok.var;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MyOrderRepo myOrderRepo;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    // user dashboard
    @RequestMapping(value = "/dashboard")
    public String userDashboard() {

        return "user/dashboard";
    }

    // user profile page
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile(Model model, Authentication authentication) {

        return "user/profile";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String userProfilePage(Model model, Authentication authentication) {

        return "user/profile";
    }

    // user add contact

    // user view contact

    // user edit contact

    // user search contact

    // user delete contact page

    // creartign order for payment

    @PostMapping("/create_order")
    @ResponseBody
    public String crateOrder(@RequestBody Map<String, Object> data, Authentication authentication) throws Exception {
        System.out.println("printttttt"+data);
        // System.out.println("hey order function executed..");
        int amount = Integer.parseInt(data.get("amount").toString());
        System.out.println(amount);

        var client = new RazorpayClient("rzp_test_CAn5REuRuX60JT", "0oU80BjwCkUzSkqj9mghnr7V");

        JSONObject ob = new JSONObject();
        ob.put("amount", amount * 100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn_12345");
        ob.put("payment_capture", 1);

        // creating new order
        // Order order = client.Orders.create(ob);

        Order order = client.orders.create(ob);
        System.out.println(order);

        // if you want you can save it to your database
        // save the order in database

        String userName = Helper.getEmailOfLoggedInUser(authentication);
        System.out.println("heyyyyyyyy"+userName);

        MyOrder myOrder = new MyOrder();
        myOrder.setAmount(order.get("amount") + "");
        myOrder.setOrderId(order.get("id"));
        myOrder.setPaymentId(null);
        myOrder.setStatus("created");
        // myOrder.setUser(Helper.getEmailOfLoggedInUser(authentication));
        myOrder.setUser(userService.getUserByEmail(userName));
        myOrder.setReceipt(order.get("receipt"));

        this.myOrderRepo.save(myOrder);

        return order.toString();
    }

    @PostMapping("/update_order")

    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {

        MyOrder myOrder = this.myOrderRepo.findByOrderId(data.get("order_id").toString());
        myOrder.setPaymentId(data.get("payment_id").toString());
        myOrder.setStatus(data.get("status").toString());
        this.myOrderRepo.save(myOrder);

        System.out.println(data);
        return ResponseEntity.ok(Map.of("msg", "updated"));

    }

    // open settings handler

    @GetMapping("/settings")
    public String openSettings() {
        return "user/settings";
    }

    // change password handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword, Authentication authentication, HttpSession session) {

        System.out.println("OLD Password " + oldPassword);
        System.out.println("New Password " + newPassword);

        // get old password from logged in user
        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);
        String password = user.getPassword();
        System.out.println("Password is : " + password);
        System.out.println("User : " + user);

        if (this.bCryptPasswordEncoder.matches(oldPassword, password)) {
            // change the password
            user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            session.setAttribute("message", Message.builder().content("Your password is changed successfully !!")
                    .type(MessageType.green).build());
            userRepo.save(user);

        } else {
            // error
            session.setAttribute("message",
                    Message.builder().content("Old password did not match !!").type(MessageType.red).build());
            return "redirect:/user/settings";
        }

        return "redirect:/user/profile";
    }

}
