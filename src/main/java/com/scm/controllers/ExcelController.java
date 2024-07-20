package com.scm.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.helper.Helper;
import com.scm.services.ContactService;

@Controller
public class ExcelController {

    @Autowired
    private ContactService contactService;



        @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (Helper.checkExcelFormat(file)) {
            // upload
            this.contactService.save(file);
            return ResponseEntity.ok(Map.of("message", "File is uploaded successfully !! Data is saved to db !!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file only");

        }
    }

}
