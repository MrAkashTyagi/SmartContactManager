package com.scm.helper;

// import static org.mockito.ArgumentMatchers.contains;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.multipart.MultipartFile;

import com.scm.enities.Contact;

public class Helper {

    public static String[] HEADERS = {
            "id",
            "adress",
            "description",
            "email",
            "favorite",
            "linkedinlink",
            "name",
            "phoneNumber",
            "picture",
            "websitelink",
            "cloudinaryImagePublicId"
    };

    public static String SHEET_NAME = "Contact_DETAILS";

    // data to excel
    public static ByteArrayInputStream dataToExcel(List<Contact> contactList) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            // crerate workbbook

            // create sheet
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            // create row: header row
            Row row = sheet.createRow(0);

            for (int i = 0; i < HEADERS.length; i++) {

                Cell cell = row.createCell(i);
                cell.setCellValue(HEADERS[i]);

            }

            // create row: value rows
            int rowIndex = 1;

            for (Contact contact : contactList) {
                Row dataRow = sheet.createRow(rowIndex++);

                dataRow.createCell(0).setCellValue(contact.getId());
                dataRow.createCell(1).setCellValue(contact.getAddress());
                dataRow.createCell(2).setCellValue(contact.getDescription());
                dataRow.createCell(3).setCellValue(contact.getEmail());
                dataRow.createCell(4).setCellValue(contact.isFavorite());
                dataRow.createCell(5).setCellValue(contact.getLinkedInLink());
                dataRow.createCell(6).setCellValue(contact.getName());
                dataRow.createCell(7).setCellValue(contact.getPhoneNumber());
                dataRow.createCell(8).setCellValue(contact.getPicture());
                dataRow.createCell(9).setCellValue(contact.getWebsiteLink());
                dataRow.createCell(10).setCellValue(contact.getCloudinaryImagePublicId());
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            workbook.close();
            out.close();

        }

    }

    // check if the file is of excel type
    public static boolean checkExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }
    }

    // converting excel to list of products
    public static List<Contact> convertExcelToListOfContacts(InputStream is) {
        List<Contact> contacts = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheet("Contact_Details");

            int rowNumber = 0;
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;

                }

                Iterator<Cell> cells = row.iterator();

                Contact contact = new Contact();

                int cid = 0;
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    switch (cid) {
                        case 0:
                            contact.setId(cell.getStringCellValue());
                            break;
                        case 1:
                            contact.setAddress(cell.getStringCellValue());
                            break;
                        case 2:
                            contact.setDescription(cell.getStringCellValue());
                            break;
                        case 3:
                            contact.setEmail(cell.getStringCellValue());
                            break;
                        case 4:
                            contact.setFavorite(cell.getBooleanCellValue());
                            break;

                        case 5:
                            contact.setLinkedInLink(cell.getStringCellValue());
                            break;

                        case 6:
                            contact.setName(cell.getStringCellValue());
                            break;

                        case 7:
                            contact.setPhoneNumber(cell.getStringCellValue());
                            break;

                        case 8:
                            contact.setPicture(cell.getStringCellValue());
                            break;

                        case 9:
                            contact.setWebsiteLink(cell.getStringCellValue());
                            break;

                        case 10:
                            contact.setCloudinaryImagePublicId(cell.getStringCellValue());
                            break;

                        // case 11:
                        // product.setSalePrice(cell.getNumericCellValue());
                        // break;

                        default:
                            break;

                    }

                    cid++;

                }

                contacts.add(contact);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return contacts;
    }

    public static String getEmailOfLoggedInUser(Authentication authentication) {

        // agar email id password se login kia h : email kasie nikalenge

        if (authentication instanceof OAuth2AuthenticationToken) {

            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientid = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

            var oauth2User = (OAuth2User) authentication.getPrincipal();

            var username = "";

            if (clientid.equalsIgnoreCase("google")) {
                // signin with google
                System.out.println("Getting email from google");
                username = oauth2User.getAttribute("email").toString();

            } else if (clientid.equalsIgnoreCase("github")) {
                // signin with github
                System.out.println("Getting email from github");
                username = oauth2User.getAttribute("email") != null ? oauth2User.getAttribute("email").toString()
                        : oauth2User.getAttribute("login").toString() + "@gmail.com";
            }

            return username;

        } else {
            System.out.println("Getting data from local database");
            return authentication.getName();
        }

    }


    //creating link for verification of the user

    public static String getLinkForEmailVerification(String emailToken){
        // String link = "http://localhost:8081/api/v1/email/auth/verify-email?token="+emailToken;

        String link = "Hello Shubham !!";
        return link;

    }

}
