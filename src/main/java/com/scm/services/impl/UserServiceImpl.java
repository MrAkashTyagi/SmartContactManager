package com.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.enities.User;
import com.scm.helper.AppConstants;
import com.scm.helper.Helper;
import com.scm.helper.ResourceNotFoundException;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    HttpSession httpSession;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public User saveUser(User user) {
        // user id have to generate
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);

        // paasword encode
        // user.setPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // user.setProfilePic(userId);

        // set user role
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        logger.info(user.getProvider().toString());
        String emailToken = UUID.randomUUID().toString();
        user.setEmailToken(emailToken);
        User savedUser = userRepo.save(user);
        httpSession.setAttribute("savedUser", savedUser);
        String emailLink = Helper.getLinkForEmailVerification(emailToken);
        this.emailService.sendEmail(savedUser.getEmail(), "Verify Account: Email Smart Contact Manager", emailLink);
        return savedUser;
    }

    public Optional<User> getByUSerId(String id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User user2 = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Resources not found"));
        // update user2 from user

        user2.setName(user.getName());
        user2.setEmail(user.getEmail());
        user2.setPassword(user.getPassword());
        user2.setAbout(user.getAbout());
        user2.setPhoneNumber(user.getPhoneNumber());
        user2.setProfilePic(user.getProfilePic());
        user2.setEnabled(user.isEnabled());
        user2.setEmailVerified(user.isEmailVerified());
        user2.setPhoneVerified(user.isPhoneVerified());
        user2.setProvider(user.getProvider());
        user2.setProviderId(user.getProviderId());

        // save the user in database
        User save = userRepo.save(user2);
        return Optional.ofNullable(save);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        userRepo.delete(user);
    }

    @Override
    public boolean isUserExist(String userId) {
        User user = userRepo.findById(userId).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {

        return userRepo.findByEmail(email).orElse(null);

    }

    @Override
    public User getUserByEmailToken(String emailToken) {        
        return userRepo.findByEmailToken(emailToken);
    }

}
