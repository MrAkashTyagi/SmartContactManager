package com.scm.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.enities.Contact;
import com.scm.enities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helper.AppConstants;
import com.scm.helper.Helper;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.services.ContactService;
import com.scm.services.EmailService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private EmailService emailService;

    Logger logger = org.slf4j.LoggerFactory.getLogger(ContactController.class);

    // contact form handler
    @RequestMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return "user/addContact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String SaveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult,
            Authentication authentication,
            HttpSession session) {
        // process the form data
        System.out.println(contactForm);

        String username = Helper.getEmailOfLoggedInUser(authentication);

        Contact contact = new Contact();

        // validate form

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(error -> logger.info(error.toString()));

            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/addContact";
        }

        // process the contact picture

        // image process
        logger.info("File information : {}",
        contactForm.getContactImage().getOriginalFilename());

        // upload krne ka code

        // process the form data

        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setLinkedInLink(contactForm.getLinkedinLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setUser(userService.getUserByEmail(username));

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {

            String fileName = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), fileName);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(fileName);

        }
        // set the contact picture url

        // save form data to db
        contactService.saveContacts(contact);

        // adding message
        // model.addAttribute("message", MessageType.green);
        session.setAttribute("message",
                Message.builder().content("Contact saved successfully").type(MessageType.green).build());

        return "redirect:/user/contacts/add";
    }

    // view contacts

    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        // load all the user conacts

        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    // search handler
    @RequestMapping("/search")
    public String searchHandler(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication)

    {
        logger.info("field is {} keyword {}", contactSearchForm.getField(), contactSearchForm.getValue());

        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContact = null;

        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy,
                    direction, user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        }
        logger.info("PAge contact {}", pageContact);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        return "user/search";
    }

    // delete handler

    @RequestMapping("/delete/{contactId}")
    public String deleteContactHandler(@PathVariable("contactId") String contactId,
            HttpSession session) {

        contactService.delete(contactId);
        logger.info("contact id {}", contactId);

        session.setAttribute("message",
                Message.builder()
                        .content("Contact is deleted successfully !!")
                        .type(MessageType.green)
                        .build());
        return "redirect:/user/contacts";

    }

    // update view handler

    @RequestMapping("/view/{id}")
    public String updateContactHandler(@PathVariable("id") String id,
            Model model) {

        Contact contact = contactService.getById(id);

        ContactForm contactForm = new ContactForm();
        contactForm.setEmail(contact.getEmail());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setLinkedinLink(contact.getLinkedInLink());
        contactForm.setName(contact.getName());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setPicture(contact.getPicture());

        model.addAttribute("contactId", id);

        model.addAttribute("contactForm", contactForm);

        // contactService.updateContact(null);

        return "user/update_contact_view";
    }

    // contact update handler

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId,
            @Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult,
            Model model)

    {
        // update the contact

        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";

        }

        var contact = contactService.getById(contactId);
        contact.setId(contactId);
        contact.setName(contactForm.getName());
        contact.setAddress(contactForm.getAddress());
        contact.setEmail(contactForm.getEmail());
        contact.setFavorite(contactForm.isFavorite());
        contact.setLinkedInLink(contactForm.getLinkedinLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setDescription(contactForm.getDescription());

        // process image

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {

            logger.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            contact.setCloudinaryImagePublicId(fileName);
            contact.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);
        } else {
            logger.info("file is empty");
        }

        var updatedContact = contactService.updateContact(contact);
        logger.info("updated contact {}", updatedContact);
        model.addAttribute("message", Message
                .builder()
                .content("Contact updated successfully !!")
                .type(MessageType.green)
                .build());
        return "redirect:/user/contacts/view/" + contactId;
    }

    // sending email handler
    @RequestMapping(value = "/sendEmail")
    public String sendEmail() {

        String message = "Testing email";
        String subject = "test subject";
        String sender = "sender_email";
        String recipient = "receipient_email";
        String userName = "sender_username";
        String password = "sender_password";

        emailService.sendEmail(message, subject, sender, recipient, userName, password);

        return "user/successEmail";
    }

    @RequestMapping(value = "/download/{userId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadExcel(@PathVariable("userId") String userId) throws IOException {

        String fileName = "contacts.xlsx";
        ByteArrayInputStream actualData = this.contactService.getActualData(userId);
        InputStreamResource file = new InputStreamResource(actualData);

        ResponseEntity<Resource> body = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName" + fileName)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);

        return body;
    }


}
