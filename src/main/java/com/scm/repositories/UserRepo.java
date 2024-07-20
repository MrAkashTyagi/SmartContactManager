package com.scm.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.enities.User;
import java.util.List;




@Repository
public interface UserRepo extends JpaRepository<User, String> {

    //extra methods db related operations
    //custom query methods
    //custom finder methods

    User findByEmailToken(String emailToken);

    Optional<User> findByEmail(String email);


}
