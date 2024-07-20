package com.scm.services;

import com.scm.enities.User;
import java.util.*;

public interface UserService {

User saveUser(User user);

Optional<User> getByUSerId(String id);

Optional<User> updateUser(User user);

void deleteUser(String id);

boolean isUserExist(String userId);

boolean isUserExistByEmail(String email);

List<User> getAllUsers();

//add more method related to user service(logic)

public User getUserByEmail(String email);

//get user by email token
public User getUserByEmailToken(String emailToken);

}
