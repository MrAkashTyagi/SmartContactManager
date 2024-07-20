package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.enities.Providers;
import com.scm.enities.User;
import com.scm.helper.AppConstants;
import com.scm.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthenticationSuccessHAndler implements AuthenticationSuccessHandler{

Logger logger = LoggerFactory.getLogger(OAuthenticationSuccessHAndler.class);

@Autowired
private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
                logger.info("onAuthenticationSuccess");

                //identify the provider

               var oAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
               String authorizedClientRegisterId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
               logger.info(authorizedClientRegisterId);

               var oauth2User = (DefaultOAuth2User)authentication.getPrincipal();
               oauth2User.getAttributes().forEach((key,value)->{

                logger.info(key+ " : " + value);

               });

               User user = new User();
               user.setUserId(UUID.randomUUID().toString());
               user.setRoleList(List.of(AppConstants.ROLE_USER));
               user.setEmailVerified(true);
               user.setEnabled(true);
               user.setPassword("dummy");
               
               

               if (authorizedClientRegisterId.equalsIgnoreCase("google")) {
                
                //google 
                //google attributes

                user.setEmail(oauth2User.getAttribute("email").toString());
                user.setProfilePic(oauth2User.getAttribute("picture").toString());
                user.setName(oauth2User.getAttribute("name").toString());
                user.setProviderId(oauth2User.getName());
                user.setProvider(Providers.GOOGLE);
                user.setAbout("This account is created using google");


               }else if(authorizedClientRegisterId.equalsIgnoreCase("github")){

                //github
                //github attributes

                String email = oauth2User.getAttribute("email") != null ? 
                oauth2User.getAttribute("email").toString() : oauth2User.getAttribute("login").toString()+"@gmail.com";

                String puicture = oauth2User.getAttribute("avatar_url").toString();

                String name = oauth2User.getAttribute("login").toString();

                String providerID = oauth2User.getName();

                user.setEmail(email);
                user.setName(name);
                user.setProfilePic(puicture);
                user.setProviderId(providerID);
                user.setProvider(Providers.GITHUB);
                user.setAbout("this account is created using github");
                
               }else if(authorizedClientRegisterId.equalsIgnoreCase("facebook")){

                //facebook
                //facebook attributes

               }else{
                logger.info("Unknown provider");

               }
                



                /*
                // data save into database
                DefaultOAuth2User user = (DefaultOAuth2User)authentication.getPrincipal();

                String email = user.getAttribute("email").toString();
                String name = user.getAttribute("name").toString();
                String picture = user.getAttribute("picture").toString();

                //create user and save to database

                User user2 = new User();
                user2.setEmail(email);
                user2.setName(name);
                user2.setProfilePic(picture);
                user2.setPassword("password");
                user2.setUserId(UUID.randomUUID().toString());
                user2.setProvider(Providers.GOOGLE);
                user2.setEnabled(true);
                user2.setEmailVerified(true);
                user2.setProviderId(user.getName());
                user2.setRoleList(List.of(AppConstants.ROLE_USER));
                user2.setAbout("user is created using google");


                User user3 = userRepo.findByEmail(email).orElse(null);
                if (user3==null) {
                    userRepo.save(user2);    
                    logger.info("User Saved"+email);
                }


               // logger.info(user.getName());                            

                // user.getAttributes().forEach((key,value)->{
                //     logger.info("{}=>{}",key,value);
                // });

                //logger.info(user.getAuthorities().toString());


 */
    User user3 = userRepo.findByEmail(user.getEmail()).orElse(null);
        if (user3==null) {
            userRepo.save(user);    
            
}


                new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
       
    }

//

}
