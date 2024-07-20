package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.scm.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    //user create and login using java code with in memory service

    // @Bean
    // public UserDetailsService userDetailsService(){

    //    UserDetails user = User
    //    .withDefaultPasswordEncoder()
    //    .username("admin123")
    //    .password("admin123")
    //    .roles("ADMIN","USER")
    //    .build();

    //    UserDetails user2 = User
    //    .withUsername("user123")
    //    .password("abc")
    //     .roles("ADMIN", "USER")
    //     .build();
       

    //    var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user,user2);

    //     return inMemoryUserDetailsManager; 
    // }

    @Autowired
    private SecurityCustomUserDetailService securityCustomUserDetailService;

    @Autowired
    private OAuthenticationSuccessHAndler oAuthenticationSuccessHAndler;

    @Autowired
    private AuthFailureHAndler authFailureHAndler;
         

//configuration of authentication provider
@Bean
public AuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider ();
    //userdetail service ka objet
    
    // SecurityCustomUserDetailService securityCustomUserDetailService = new SecurityCustomUserDetailService();
    daoAuthenticationProvider.setUserDetailsService(securityCustomUserDetailService);
    
    //password encoder ka bean
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
}

//security filter chain
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

    //configuration

    //urls ko configure kia h ki konse pubic rhenge aur konse private rhenge
    httpSecurity.authorizeHttpRequests(authorize->{
        // authorize.requestMatchers("/home","/register","/service").permitAll();
        authorize.requestMatchers("/user/**").authenticated();
        authorize.anyRequest().permitAll();
    });


    //form default login
    //agar hume kuch bhi change krna hua to hum yaha aayenge form login se related
    // httpSecurity.formLogin(Customizer.withDefaults());

    httpSecurity.formLogin(formLogin->{

        //login page

        formLogin.loginPage("/login");
        formLogin.loginProcessingUrl("/authenticate");
        formLogin.successForwardUrl("/user/profile");
        // formLogin.failureForwardUrl("/login?error?=true");
        formLogin.usernameParameter("email");
        formLogin.passwordParameter("password");
        
     //   formLogin.failureHandler(new AuthenticationFailureHandler() {
            

    //         @Override
    //         public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    //                 AuthenticationException exception) throws IOException, ServletException {
    //             // TODO Auto-generated method stub
    //             throw new UnsupportedOperationException("Unimplemented method 'onAuthenticationFailure'");
    //         }
            
    //     });

    //     formLogin.successHandler(new AuthenticationSuccessHandler() {

    //         @Override
    //         public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    //                 Authentication authentication) throws IOException, ServletException {
    //             // TODO Auto-generated method stub
    //             throw new UnsupportedOperationException("Unimplemented method 'onAuthenticationSuccess'");
    //         }
            
    //     });

    formLogin.failureHandler(authFailureHAndler);

     });

     httpSecurity.csrf(AbstractHttpConfigurer::disable);
     
     //oauth configuration

// httpSecurity.oauth2Login(Customizer.withDefaults());

     httpSecurity.oauth2Login(oauth->{

        oauth.loginPage("/login");
        oauth.successHandler(oAuthenticationSuccessHAndler);

     });

     httpSecurity.logout(logoutForm->{
        logoutForm.logoutUrl("/do-logout");
        logoutForm.logoutSuccessUrl("/login?logout=true");
     });


    return httpSecurity.build();

    
}

@Bean
public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
}

}
