package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.enities.Contact;
import com.scm.services.ContactService;

@RestController
// @MultipartConfig
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;


    // get contact by id

    @GetMapping("/contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {

        return contactService.getById(contactId);
    }



}
