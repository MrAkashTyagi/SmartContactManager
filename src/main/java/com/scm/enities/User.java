package com.scm.enities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "user")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User implements UserDetails{

@Id
private String userId;
@Column(name = "user_name",nullable = false)
private String name;
@Column(unique = true, nullable = false)
private String email;
private String password;
@Column(length = 1000)
private String about;
@Column(length = 1000)
private String profilePic;
private String phoneNumber;
//information
private boolean enabled = false;
private boolean emailVerified = false;
private boolean phoneVerified = false;  


//Self, Google, facebook, LinkedIn, Github
@Enumerated(value = EnumType.STRING)
private Providers provider = Providers.SELF;
private String providerId;

//add more fields if needed

@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
private List<Contact> contacts = new ArrayList<>();

@ElementCollection(fetch = FetchType.EAGER)
private List<String> roleList = new ArrayList<>();

private String emailToken;


// list of roles [USER,ADMIN]
// collection of SimpleGrantedAuthority[roles{ADMIN,USER}]
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<SimpleGrantedAuthority> roles = roleList.stream().map(role-> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
    return roles;
}

@Override
public String getUsername() {
    return this.email;
}

@Override
public boolean isAccountNonExpired() {
    return true;
}

@Override
public boolean isAccountNonLocked() {
    return true;
}

@Override
public boolean isCredentialsNonExpired() {
    return true;
} 

@Override
public boolean isEnabled(){
    return this.enabled;
}

public String getPassword(){
    return this.password;
}

}
