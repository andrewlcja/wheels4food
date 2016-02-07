/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.PendingRegistrationDAO;
import dao.UserDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import model.ApprovePendingRegistrationResponse;
import model.CreatePendingRegistrationRequest;
import model.CreatePendingRegistrationResponse;
import model.DeletePendingRegistrationResponse;
import model.PendingRegistration;
import model.User;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import utility.HashUtility;

/**
 *
 * @author andrew.lim.2013
 */
public class PendingRegistrationService {

    @Autowired
    PendingRegistrationDAO pendingRegistrationDAO;

    @Autowired
    UserDAO userDAO;

    public CreatePendingRegistrationResponse createPendingRegistrationRequest(CreatePendingRegistrationRequest request) {
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword().trim();
        String organizationName = request.getOrganizationName().trim();
        String email = request.getEmail().trim();
        String address = request.getAddress().trim();
        String postalCodeStr = request.getPostalCode().trim();
        String pocName = request.getPocName().trim();
        String pocNumber = request.getPocNumber().trim();
        String licenseNumber = request.getLicenseNumber().trim();
        String description = request.getDescription().trim();
        String role = request.getRole().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (username.equals("")) {
            errorList.add("Username cannot be blank");
        }

        if (password.equals("")) {
            errorList.add("Password cannot be blank");
        }

        if (confirmPassword.equals("")) {
            errorList.add("Confirmation Password cannot be blank");
        }

        if (organizationName.equals("")) {
            errorList.add("Organization Name cannot be blank");
        }

        if (email.equals("")) {
            errorList.add("Email cannot be blank");
        }

        if (address.equals("")) {
            errorList.add("Address cannot be blank");
        }

        if (postalCodeStr.equals("")) {
            errorList.add("Postal Code cannot be blank");
        }

        if (pocName.equals("")) {
            errorList.add("Point of Contact Name cannot be blank");
        }

        if (pocNumber.equals("")) {
            errorList.add("Point of Contact Number cannot be blank");
        }

        if (licenseNumber.equals("")) {
            errorList.add("License Number cannot be blank");
        }
        
        if (description.equals("")) {
            errorList.add("Organization Description cannot be blank");
        }

        if (role.equals("")) {
            errorList.add("Role cannot be blank");
        }

        if (!errorList.isEmpty()) {
            return new CreatePendingRegistrationResponse(false, errorList);
        }

        try {
            if (username.contains(" ")) {
                errorList.add("Username cannot contain empty spaces");
            } else if (userDAO.getUser(username) != null || pendingRegistrationDAO.getPendingRegistrationByUsername(username) != null) {
                errorList.add("Username already exists");
            }
            
            if (userDAO.getUserByOrganization(organizationName) != null || pendingRegistrationDAO.getPendingRegistrationByOrganization(organizationName) != null) {
                errorList.add("Organization Name already exists");
            }

            if (!password.equals(confirmPassword)) {
                errorList.add("Password and Confirmation Password must be the same");
            }

            if (!email.contains("@") || email.length() == 1) {
                errorList.add("Invalid email");
            } else if (userDAO.getUserByEmail(email) != null) {
                errorList.add("Email already exists");
            }

            int postalCode;
            try {
                postalCode = Integer.parseInt(postalCodeStr);

                if (postalCodeStr.length() != 6) {
                    errorList.add("Postal Code must be a 6-digit integer");
                }
            } catch (NumberFormatException e) {
                errorList.add("Postal Code must be a 6-digit integer");
            }

            if (!errorList.isEmpty()) {
                return new CreatePendingRegistrationResponse(false, errorList);
            }

            //generate hash password and salt
            String[] credentials = HashUtility.getHashAndSalt(password);
            String hashedPassword = credentials[0];
            String salt = credentials[1];

            pendingRegistrationDAO.createPendingRegistration(new PendingRegistration(username, hashedPassword, salt, organizationName, email, address, postalCodeStr, pocName, pocNumber, licenseNumber, description, role));
            return new CreatePendingRegistrationResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreatePendingRegistrationResponse(false, errorList);
        }
    }
    
    public CreatePendingRegistrationResponse createVolunteerPendingRegistrationRequest(CreatePendingRegistrationRequest request) {
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword().trim();
        String organizationName = request.getOrganizationName().trim();
        String email = request.getEmail().trim();
        String address = "NA";
        String postalCodeStr = "NA";
        String pocName = request.getPocName().trim();
        String pocNumber = request.getPocNumber().trim();
        String licenseNumber = "NA";
        String description = "NA";
        String role = request.getRole().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (username.equals("")) {
            errorList.add("Username cannot be blank");
        }

        if (password.equals("")) {
            errorList.add("Password cannot be blank");
        }

        if (confirmPassword.equals("")) {
            errorList.add("Confirmation Password cannot be blank");
        }

        if (organizationName.equals("")) {
            errorList.add("Organization Name cannot be blank");
        }

        if (email.equals("")) {
            errorList.add("Email cannot be blank");
        }

        if (pocName.equals("")) {
            errorList.add("Full Name cannot be blank");
        }

        if (pocNumber.equals("")) {
            errorList.add("Mobile Number cannot be blank");
        }

        if (role.equals("")) {
            errorList.add("Role cannot be blank");
        }

        if (!errorList.isEmpty()) {
            return new CreatePendingRegistrationResponse(false, errorList);
        }

        try {
            if (username.contains(" ")) {
                errorList.add("Username cannot contain empty spaces");
            } else if (userDAO.getUser(username) != null || pendingRegistrationDAO.getPendingRegistrationByUsername(username) != null) {
                errorList.add("Username already exists");
            }

            if (!password.equals(confirmPassword)) {
                errorList.add("Password and Confirmation Password must be the same");
            }

            if (!email.contains("@") || email.length() == 1) {
                errorList.add("Invalid email");
            } else if (userDAO.getUserByEmail(email) != null) {
                errorList.add("Email already exists");
            }
            
            if (userDAO.getUserByMobileNumber(pocNumber) != null) {
                errorList.add("Mobile number already exists");
            }

            if (!errorList.isEmpty()) {
                return new CreatePendingRegistrationResponse(false, errorList);
            }

            //generate hash password and salt
            String[] credentials = HashUtility.getHashAndSalt(password);
            String hashedPassword = credentials[0];
            String salt = credentials[1];

            pendingRegistrationDAO.createPendingRegistration(new PendingRegistration(username, hashedPassword, salt, organizationName, email, address, postalCodeStr, pocName, pocNumber, licenseNumber, description, role));
            return new CreatePendingRegistrationResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreatePendingRegistrationResponse(false, errorList);
        }
    }

    public List<PendingRegistration> getPendingRegistrationListRequest() throws Exception {
        return pendingRegistrationDAO.retrieveAll();
    }
    
    public List<PendingRegistration> getPendingRegistrationListByRoleRequest(String role) throws Exception {
        return pendingRegistrationDAO.getPendingRegistrationListByRole(role);
    }
    
    public List<PendingRegistration> getVolunteerPendingRegistrationListByOrganizationRequest(String role) throws Exception {
        return pendingRegistrationDAO.getVolunteerPendingRegistrationListByOrganization(role);
    }

    public PendingRegistration getPendingRegistrationByIdRequest(int id) throws Exception {
        return pendingRegistrationDAO.getPendingRegistrationById(id);
    }

    public DeletePendingRegistrationResponse deletePendingRegistrationRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new DeletePendingRegistrationResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                pendingRegistrationDAO.deletePendingRegistration(id);
                return new DeletePendingRegistrationResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new DeletePendingRegistrationResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new DeletePendingRegistrationResponse(false, errorList);
        }
    }

    public ApprovePendingRegistrationResponse approvePendingRegistrationRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new ApprovePendingRegistrationResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                PendingRegistration pendingRegistration = pendingRegistrationDAO.getPendingRegistrationById(id);

                if (pendingRegistration == null) {
                    errorList.add("No pending registration for this id");
                    return new ApprovePendingRegistrationResponse(false, errorList);
                }

                String username = pendingRegistration.getUsername();
                String password = pendingRegistration.getHashedPassword();
                String salt = pendingRegistration.getSalt();
                String organizationName = pendingRegistration.getOrganizationName();
                String email = pendingRegistration.getEmail();
                String address = pendingRegistration.getAddress();
                String postalCode = pendingRegistration.getPostalCode();
                String pocName = pendingRegistration.getPocName();
                String pocNumber = pendingRegistration.getPocNumber();
                String licenseNumber = pendingRegistration.getLicenseNumber();
                String description = pendingRegistration.getDescription();
                String role = pendingRegistration.getRole();

                User user = new User(username, password, salt, organizationName, email, address, postalCode, pocName, pocNumber, licenseNumber, description, role, 0, "Active");

                pendingRegistrationDAO.approvePendingRegistration(user);
                pendingRegistrationDAO.deletePendingRegistration(id);

//                Email registrationEmail = new SimpleEmail();
//                registrationEmail.setHostName("smtp.googlemail.com");
//                registrationEmail.setSmtpPort(465);
//                registrationEmail.setAuthenticator(new DefaultAuthenticator("wheels4food@gmail.com", "wheels4food2015"));
//                registrationEmail.setSSLOnConnect(true);
//                registrationEmail.setFrom("wheels4food@gmail.com");
//                registrationEmail.setSubject("Wheels4Food - Registration Complete");
//                registrationEmail.setMsg("Welcome to Wheels4Food!");
//                registrationEmail.addTo(email);
//                registrationEmail.send();
                
                return new ApprovePendingRegistrationResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new ApprovePendingRegistrationResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new ApprovePendingRegistrationResponse(false, errorList);
        }
    }
}
