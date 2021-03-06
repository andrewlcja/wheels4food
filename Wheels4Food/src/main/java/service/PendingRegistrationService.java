/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.NotificationDAO;
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
import model.Notification;
import model.PendingRegistration;
import model.User;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import utility.ConfigUtility;
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

    @Autowired
    NotificationDAO notificationDAO;

    public CreatePendingRegistrationResponse createPendingRegistrationRequest(CreatePendingRegistrationRequest request) {
        ConfigUtility config = new ConfigUtility();

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

        try {
            //validations
            if (username.equals("")) {
                errorList.add(config.getProperty("username_blank"));
            }

            if (password.equals("")) {
                errorList.add(config.getProperty("password_blank"));
            }

            if (confirmPassword.equals("")) {
                errorList.add(config.getProperty("confirmation_password_blank"));
            }

            if (organizationName.equals("")) {
                errorList.add(config.getProperty("organization_name_blank"));
            }

            if (email.equals("")) {
                errorList.add(config.getProperty("email_blank"));
            }

            if (address.equals("")) {
                errorList.add(config.getProperty("address_blank"));
            }

            if (postalCodeStr.equals("")) {
                errorList.add(config.getProperty("postal_blank"));
            }

            if (pocName.equals("")) {
                errorList.add(config.getProperty("poc_blank"));
            }

            if (pocNumber.equals("")) {
                errorList.add(config.getProperty("poc_number_blank"));
            }

            if (licenseNumber.equals("")) {
                errorList.add(config.getProperty("license_blank"));
            }

            if (description.equals("")) {
                errorList.add(config.getProperty("organization_description_blank"));
            }

            if (role.equals("")) {
                errorList.add(config.getProperty("role_blank"));
            }

            if (!errorList.isEmpty()) {
                return new CreatePendingRegistrationResponse(false, errorList);
            }

            if (username.contains(" ")) {
                errorList.add(config.getProperty("username_empty_spaces"));
            } else if (userDAO.getUser(username) != null || pendingRegistrationDAO.getPendingRegistrationByUsername(username) != null) {
                errorList.add(config.getProperty("username_exists"));
            }

            if (userDAO.getUserByOrganization(organizationName) != null || pendingRegistrationDAO.getPendingRegistrationByOrganization(organizationName) != null) {
                errorList.add(config.getProperty("organization_name_exists"));
            }

            if (!password.equals(confirmPassword)) {
                errorList.add(config.getProperty("password_match"));
            }

            if (!email.contains("@") || email.length() == 1) {
                errorList.add(config.getProperty("email_invalid"));
            } else if (userDAO.getUserByEmail(email) != null) {
                errorList.add(config.getProperty("email_exists"));
            }

            int pocNum;
            try {
                pocNum = Integer.parseInt(pocNumber);

                if (pocNumber.length() != 8) {
                    errorList.add(config.getProperty("poc_number_digit"));
                } else if (userDAO.getUserByMobileNumber(pocNumber) != null) {
                    errorList.add(config.getProperty("poc_number_exists"));
                }
            } catch (NumberFormatException e) {
                errorList.add(config.getProperty("poc_number_digit"));
            }

            int postalCode;
            try {
                postalCode = Integer.parseInt(postalCodeStr);

                if (postalCodeStr.length() != 6) {
                    errorList.add(config.getProperty("postal_digit"));
                }
            } catch (NumberFormatException e) {
                errorList.add(config.getProperty("postal_digit"));
            }

            if (!errorList.isEmpty()) {
                return new CreatePendingRegistrationResponse(false, errorList);
            }

            //generate hash password and salt
            String[] credentials = HashUtility.getHashAndSalt(password);
            String hashedPassword = credentials[0];
            String salt = credentials[1];

            pendingRegistrationDAO.createPendingRegistration(new PendingRegistration(username, hashedPassword, salt, organizationName, email, address, postalCodeStr, pocName, pocNumber, licenseNumber, description, role));

            //create notification
            if (role.equals("Supplier")) {
                User user = userDAO.getUser("gtl");
                notificationDAO.createNotification(new Notification(user, "PendingRegistrations", "A new user, <b>" + username + "</b>, has signed up. Click here to go to <b>Pending Registrations</b>."));
            } else if (role.equals("Requester")) {
                User user = userDAO.getUser("ffth");
                notificationDAO.createNotification(new Notification(user, "PendingRegistrations", "A new user, <b>" + username + "</b>, has signed up. Click here to go to <b>Pending Registrations</b>."));
            }

            return new CreatePendingRegistrationResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreatePendingRegistrationResponse(false, errorList);
        }
    }

    public CreatePendingRegistrationResponse createVolunteerPendingRegistrationRequest(CreatePendingRegistrationRequest request) {
        ConfigUtility config = new ConfigUtility();

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

        try {
            //validations
            if (username.equals("")) {
                errorList.add(config.getProperty("username_blank"));
            }

            if (password.equals("")) {
                errorList.add(config.getProperty("password_blank"));
            }

            if (confirmPassword.equals("")) {
                errorList.add(config.getProperty("confirmation_password_blank"));
            }

            if (organizationName.equals("")) {
                errorList.add(config.getProperty("organization_name_blank"));
            }

            if (email.equals("")) {
                errorList.add(config.getProperty("email_blank"));
            }

            if (pocName.equals("")) {
                errorList.add(config.getProperty("full_name_blank"));
            }

            if (pocNumber.equals("")) {
                errorList.add(config.getProperty("mobile_blank"));
            }

            if (role.equals("")) {
                errorList.add(config.getProperty("role_blank"));
            }

            if (!errorList.isEmpty()) {
                return new CreatePendingRegistrationResponse(false, errorList);
            }

            if (username.contains(" ")) {
                errorList.add(config.getProperty("username_empty_spaces"));
            } else if (userDAO.getUser(username) != null || pendingRegistrationDAO.getPendingRegistrationByUsername(username) != null) {
                errorList.add(config.getProperty("username_exists"));
            }

            if (!password.equals(confirmPassword)) {
                errorList.add(config.getProperty("password_match"));
            }

            if (!email.contains("@") || email.length() == 1) {
                errorList.add(config.getProperty("email_invalid"));
            } else if (userDAO.getUserByEmail(email) != null) {
                errorList.add(config.getProperty("email_exists"));
            }

            int pocNum;
            try {
                pocNum = Integer.parseInt(pocNumber);

                if (pocNumber.length() != 8) {
                    errorList.add(config.getProperty("mobile_digit"));
                } else if (userDAO.getUserByMobileNumber(pocNumber) != null) {
                    errorList.add(config.getProperty("mobile_exists"));
                }
            } catch (NumberFormatException e) {
                errorList.add(config.getProperty("mobile_digit"));
            }

            if (!errorList.isEmpty()) {
                return new CreatePendingRegistrationResponse(false, errorList);
            }

            //generate hash password and salt
            String[] credentials = HashUtility.getHashAndSalt(password);
            String hashedPassword = credentials[0];
            String salt = credentials[1];

            pendingRegistrationDAO.createPendingRegistration(new PendingRegistration(username, hashedPassword, salt, organizationName, email, address, postalCodeStr, pocName, pocNumber, licenseNumber, description, role));

            //create notification
            User user = userDAO.getUserByOrganization(organizationName);
            notificationDAO.createNotification(new Notification(user, "PendingRegistrations", "A new user, <b>" + username + "</b>, has signed up. Click here to go to <b>Pending Registrations</b>."));

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

    public List<PendingRegistration> getPendingRegistrationListByOrganizationRequest(String role) throws Exception {
        return pendingRegistrationDAO.getPendingRegistrationListByOrganization(role);
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
            errorList.add("Id must be an number");
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

                //get properties
                ConfigUtility config = new ConfigUtility();
                final String emailUsername = config.getProperty("email_username");
                final String emailPassword = config.getProperty("email_password");

                //send email
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");

                Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(emailUsername, emailPassword);
                            }
                        });

                try {
                    String recipient = email;

                    String name = "";
                    if (role.equals("VWO")) {
                        name = organizationName;
                    } else {
                        name = pocName;
                    }

                    String body = "<div>\n"
                            + "            <p>Dear " + name + ",</p><br/>\n"
                            + "            <p>Thank you for registering with Wheels4Food, your registration has been approved! Please login using the link below:</p>\n"
                            + "            <p>Login through our <a href='http://apps.greentransformationlab.com/Wheels4Food/Login'>Web Portal</a> or download our mobile app <a href='https://play.google.com/store/apps/details?id=com.ionicframework.simplelogin448241&hl=en'>here</a></p><br/>\n"
                            + "            <p>Regards,</p>\n"
                            + "            <p>Wheels4Food Team</p>\n"
                            + "        </div>";
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(emailUsername));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                    message.setSubject("Wheels4Food - Registration Complete");
                    message.setText(body, "UTF-8", "html");

                    Transport.send(message);
                } catch (MessagingException e) {
                    errorList.add(e.getMessage());
                    return new ApprovePendingRegistrationResponse(false, errorList);
                }

                return new ApprovePendingRegistrationResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new ApprovePendingRegistrationResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an number");
            return new ApprovePendingRegistrationResponse(false, errorList);
        }
    }
}
