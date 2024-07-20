package com.scm.enities;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Email {

private String sender;
private String recipient;
private String message;
private String subject;
private String username;
private String password;

}
