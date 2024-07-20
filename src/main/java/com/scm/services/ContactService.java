package com.scm.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.scm.enities.Contact;
import com.scm.enities.User;

import jakarta.mail.Multipart;

public interface ContactService{

//save contacts

 public Contact saveContacts(Contact contact);

//find all contacts

public List<Contact> getAll();

//delete contact
public void delete(String id);

// delete by contact
public void deleteByContact(Contact contact);


//update contact
public Contact updateContact(Contact contact);

//find contact by id
public Contact getById(String id);

//find contact
List<Contact> search(String phoneNUmber, String name, String email);

Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user);

Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy, String order,User user);

Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order,User user);

//get contact by user id
List<Contact> getByUserId(String userId);

//get contact by user
public Page<Contact> getByUser(User user, int page, int size, String sortField, String sortDirection) ;

//get actual data
public ByteArrayInputStream getActualData(String userId) throws IOException;

//dumping data from excel to db
public void save(MultipartFile file);

}
