package com.scm.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scm.enities.Contact;
import com.scm.enities.User;
import com.scm.helper.Helper;
import com.scm.helper.ResourceNotFoundException;
import com.scm.repositories.ContactRepo;
import com.scm.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public Contact saveContacts(Contact contact) {

        String contactId = UUID.randomUUID().toString();
        contact.setId(contactId);

        return contactRepo.save(contact);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public void delete(String id) {

        var contact = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource is not found by the given id" + id));
        contactRepo.delete(contact);
    }

    @Override
    public void deleteByContact(Contact contact) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByContact'");
    }

    @Override
    public Contact updateContact(Contact contact) {
        var contactOld = contactRepo.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contactOld.setName(contact.getName());
        contactOld.setAddress(contact.getAddress());
        contactOld.setEmail(contact.getEmail());
        contactOld.setDescription(contact.getDescription());
        contactOld.setFavorite(contact.isFavorite());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setPicture(contact.getPicture());
        contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

        return contactRepo.save(contactOld);
    }

    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource is not found by the given id" + id));
    }

    @Override
    public List<Contact> search(String phoneNumber, String name, String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    @Override
    public List<Contact> getByUserId(String userId) {
        return contactRepo.findByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUser(user, pageable);
    }

    @Override
    public Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepo.findByNameContainingAndUser(nameKeyword, user, pageable);
    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy,
            String order, User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByPhoneNumberContainingAndUser(phoneNumberKeyword, user, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order,
            User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByEmailContainingAndUser(emailKeyword, user, pageable);
    }

    public ByteArrayInputStream getActualData(String userId) throws IOException {
        // List<Contact> contactList = this.contactRepo.findAll();
        List<Contact> contactList = this.contactRepo.findByUserId(userId);
        System.out.println("Excel all data is :" + contactList);
        ByteArrayInputStream stream = Helper.dataToExcel(contactList);
        return stream;
    }

    // saving data from excel to db
    public void save(MultipartFile file) {
        try {
            List<Contact> contacts = Helper.convertExcelToListOfContacts(file.getInputStream());
            this.contactRepo.saveAll(contacts);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
