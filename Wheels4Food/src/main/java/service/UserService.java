/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.SupplyDAO;
import dao.UserDAO;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import model.ActivateUserResponse;
import model.ChangePasswordRequest;
import model.ChangePasswordResponse;
import model.DeleteUserResponse;
import model.Supply;
import model.SuspendUserResponse;
import model.UpdateUserResponse;
import model.User;
import model.UserLoginRequest;
import model.UserLoginResponse;
import model.CreatePendingResetPasswordRequest;
import model.CreatePendingResetPasswordResponse;
import model.VerifyResetPasswordTokenResponse;
import model.PendingResetPassword;
import model.ResetPasswordRequest;
import model.ResetPasswordResponse;
import org.springframework.beans.factory.annotation.Autowired;
import utility.ConfigUtility;
import utility.HashUtility;

/**
 *
 * @author andrew.lim.2013
 */
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    SupplyDAO supplyDAO;

    public List<User> getUserListRequest() throws Exception {
        return userDAO.retrieveAll();
    }

    public List<User> getUserListByRoleRequest(String role) throws Exception {
        return userDAO.getUserListByRole(role);
    }

    public List<User> getVolunteerListByOrganizationRequest(String organizationName) throws Exception {
        return userDAO.getVolunteerListByOrganization(organizationName);
    }

    public User getUserByUsernameRequest(String username) throws Exception {
        return userDAO.getUser(username);
    }

    public UserLoginResponse userLoginRequest(UserLoginRequest request) {
//        try {
//            User ffth = userDAO.getUser("ffth");
//            for (int i = 1001; i <= 2000; i++) {
//                supplyDAO.createSupply(new Supply(ffth, "", "TestSupply" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//            }
//        } catch (Exception e) {
//            
//        } 

//        try {            
//            for (int i = 1; i <= 5; i++) {
//                User u = userDAO.getUser("svwo" + i);
//                supplyDAO.createSupply(new Supply(u, "", "8 treasure Soup (Canned Food) - 800g", "Food", "Can", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Bamboo Shoots (Canned Food) - 800g", "Food", "Can", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "4 Bean Mix (Canned Food) - 800g", "Food", "Can", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Evaporated Milk (Canned Food) - 800g", "Condiments", "Can", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Black Pepper (Bottle) - 800g", "Condiments", "Bottle", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Blueberry Pie Filling/Topping (Canned Food) - 800g", "Food", "Can", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Chicken Broth (Packet) – 800ml", "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Pork Leg with Mushroom (Canned Food) - 800g", "Food", "Can", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Fruit Cocktails (Canned Fruits) - 800g", "Food", "Can", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Pasta Sauce (Cabonara) (Canned Food) - 800g", "Condiments", "Can", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//            }
//        } catch (Exception e) {
//
//        }

        User user = null;

        //retrieve fields
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        //validations
        //check for blank username
        if (username.equals("") || password.equals("")) {
            return new UserLoginResponse(false, "Username / password cannot be blank");
        }

        try {
            user = userDAO.getUser(request.getUsername());

            if (user == null) {
                return new UserLoginResponse(false, "Invalid username / password");
            }

            if (!HashUtility.verify(password, user.getHashedPassword(), user.getSalt())) {
                return new UserLoginResponse(false, "Invalid username / password");
            }

            if (user.getStatus().equals("Inactive")) {
                return new UserLoginResponse(false, "Account suspended");
            }

            return new UserLoginResponse(true, user);
        } catch (Exception e) {
            return new UserLoginResponse(false, e.getMessage());
        }
    }

    public DeleteUserResponse deleteUserRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new DeleteUserResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                userDAO.deleteUser(id);
                return new DeleteUserResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new DeleteUserResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new DeleteUserResponse(false, errorList);
        }
    }

    public SuspendUserResponse suspendUserRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new SuspendUserResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                User user = userDAO.getUserById(id);
                user.setStatus("Inactive");
                userDAO.updateUser(user);
                return new SuspendUserResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new SuspendUserResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new SuspendUserResponse(false, errorList);
        }
    }

    public ActivateUserResponse activateUserRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new ActivateUserResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                User user = userDAO.getUserById(id);
                user.setStatus("Active");
                userDAO.updateUser(user);
                return new ActivateUserResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new ActivateUserResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new ActivateUserResponse(false, errorList);
        }
    }

    public UpdateUserResponse updateUserRequest(User user) {
        String username = user.getUsername().trim();
        String organizationName = user.getOrganizationName().trim();
        String email = user.getEmail().trim();
        String address = user.getAddress().trim();
        String postalCode = user.getPostalCode().trim();
        String pocName = user.getPocName().trim();
        String pocNumber = user.getPocNumber().trim();
        String licenseNumber = user.getLicenseNumber().trim();
        String description = user.getDescription().trim();
        String role = user.getRole().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (username.equals("")) {
            errorList.add("Username cannot be blank");
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

        if (postalCode.equals("")) {
            errorList.add("Postal Code cannot be blank");
        }

        if (pocName.equals("")) {
            if (role.equals("Volunteer")) {
                errorList.add("Full Name cannot be blank");
            } else {
                errorList.add("Point of Contact Name cannot be blank");
            }
        }

        if (pocNumber.equals("")) {
            if (role.equals("Volunteer")) {
                errorList.add("Mobile Number cannot be blank");
            } else {
                errorList.add("Point of Contact Number cannot be blank");
            }
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
            return new UpdateUserResponse(false, errorList);
        }

        try {
            User oldUser = userDAO.getUserById(user.getId());

            if (!user.getUsername().equals(oldUser.getUsername())) {
                if (userDAO.getUser(username) != null) {
                    errorList.add("Username already exists");
                }
            }

            if (!email.contains("@") || email.length() == 1) {
                errorList.add("Invalid email");
            } else if (!email.equals(oldUser.getEmail())) {
                if (userDAO.getUserByEmail(email) != null) {
                    errorList.add("Email already exists");
                }
            } else if (role.equals("Volunteer") && !pocNumber.equals(oldUser.getPocNumber())) {
                if (userDAO.getUserByMobileNumber(pocNumber) != null) {
                    errorList.add("Mobile Number already exists");
                }
            }

            if (!errorList.isEmpty()) {
                return new UpdateUserResponse(false, errorList);
            }

            userDAO.updateUser(user);
            return new UpdateUserResponse(true, errorList);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new UpdateUserResponse(false, errorList);
        }
    }

    public ChangePasswordResponse changePasswordRequest(ChangePasswordRequest request) {
        String username = request.getUsername().trim();
        String oldPassword = request.getOldPassword().trim();
        String newPassword = request.getNewPassword().trim();
        String confirmNewPassword = request.getConfirmNewPassword().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        //check if the fields entered are empty
        if (username.equals("")) {
            errorList.add("Username cannot be blank");
        }

        if (oldPassword.equals("")) {
            errorList.add("Current Password cannot be blank");
        }

        if (newPassword.equals("")) {
            errorList.add("New Password cannot be blank");
        }

        if (confirmNewPassword.equals("")) {
            errorList.add("Confirmation Password cannot be blank");
        }

        if (!errorList.isEmpty()) {
            return new ChangePasswordResponse(false, errorList);
        }

        try {
            User user = userDAO.getUser(username);

            //check if the password entered is the same as current one
            if (!HashUtility.verify(oldPassword, user.getHashedPassword(), user.getSalt())) {
                errorList.add("Current Password entered is incorrect");
            }

            //check if the old passwords and current password are the same
            if (HashUtility.verify(newPassword, user.getHashedPassword(), user.getSalt())) {
                errorList.add("New Password entered cannot be the same as your Current Password");
            }

            //check if the new password and the confirmed new password are the same.
            if (!newPassword.equals(confirmNewPassword)) {
                errorList.add("New Password and Confirmation Password do not match");
            }

            if (!errorList.isEmpty()) {
                return new ChangePasswordResponse(false, errorList);
            }

            String[] result = HashUtility.getHashAndSalt(confirmNewPassword);
            user.setHashedPassword(result[0]);
            user.setSalt(result[1]);

            userDAO.updateUser(user);
            return new ChangePasswordResponse(true, errorList);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new ChangePasswordResponse(false, errorList);
        }
    }

    public CreatePendingResetPasswordResponse createPendingResetPasswordRequest(CreatePendingResetPasswordRequest request) {
        String email = request.getEmail().trim();
        String endPoint = request.getEndPoint().trim();
        ArrayList<String> errorList = new ArrayList<String>();

        if (email.equals("")) {
            errorList.add("Email cannot be blank.");
        }

        if (endPoint.equals("")) {
            errorList.add("End Point cannot be blank.");
        }

        if (!errorList.isEmpty()) {
            return new CreatePendingResetPasswordResponse(false, errorList);
        }

        if (!email.contains("@") || email.length() == 1) {
            errorList.add("Invalid email.");
            return new CreatePendingResetPasswordResponse(false, errorList);
        }

        try {
            //check if email exists, even if it does not exist, return success case (for security)
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                return new CreatePendingResetPasswordResponse(true, null);
            }

            //generate 40-digit random token
            String token = HashUtility.generateRandomString(40);

            //generate hashed token and salt
            String[] result = HashUtility.getHashAndSalt(token);
            String hashedToken = result[0];
            String salt = result[1];

            //generate expiry date
            Timestamp expiryDate = new Timestamp(System.currentTimeMillis());

            //check if pending reset password exists
            PendingResetPassword pendingResetPassword = userDAO.getPendingResetPassword(user.getUsername());

            if (pendingResetPassword != null) {
                pendingResetPassword.setHashedToken(hashedToken);
                pendingResetPassword.setSalt(salt);
                pendingResetPassword.setExpiryDate(expiryDate);
                userDAO.updatePendingResetPassword(pendingResetPassword);
            } else {
                userDAO.createPendingResetPassword(new PendingResetPassword(user.getUsername(), hashedToken, salt, expiryDate));
            }

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

            String body = "<div>\n"
                    + "            <p>Dear " + user.getUsername() + ",</p><br/>\n"
                    + "            <p>Reset your password through this <a href='" + endPoint + "/Reset/" + token + "'>link</a>. Please take note that the link will expire in 15 minutes time.</p><br/>\n"
                    + "            <p>Regards,</p>\n"
                    + "            <p>Wheels4Food Team</p>\n"
                    + "        </div>";

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Wheels4Food - Reset Password");
            message.setText(body, "UTF-8", "html");
            Transport.send(message);

            return new CreatePendingResetPasswordResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new CreatePendingResetPasswordResponse(false, errorList);
        }
    }

    public VerifyResetPasswordTokenResponse verifyResetPasswordTokenRequest(String token) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (token.equals("")) {
            errorList.add("Token cannot be blank.");
            return new VerifyResetPasswordTokenResponse(false, errorList);
        }

        try {
            List<PendingResetPassword> pendingResetPasswordList = userDAO.getPendingResetPasswordList();

            boolean isVerified = false;
            PendingResetPassword pending = null;
            for (int i = 0; i < pendingResetPasswordList.size(); i++) {
                PendingResetPassword pendingResetPassword = pendingResetPasswordList.get(i);

                if (HashUtility.verify(token, pendingResetPassword.getHashedToken(), pendingResetPassword.getSalt())) {
                    isVerified = true;
                    pending = pendingResetPassword;
                    break;
                }
            }

            if (isVerified) {
                Timestamp expiryDate = pending.getExpiryDate();
                long duration = 15 * 60 * 1000;

                if (System.currentTimeMillis() - expiryDate.getTime() <= duration) {
                    return new VerifyResetPasswordTokenResponse(true, null);
                } else {
                    errorList.add("Expired Token.");
                    return new VerifyResetPasswordTokenResponse(false, errorList);
                }
            } else {
                errorList.add("Invalid Token.");
                return new VerifyResetPasswordTokenResponse(false, errorList);
            }
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new VerifyResetPasswordTokenResponse(false, errorList);
        }
    }

    public ResetPasswordResponse resetPasswordRequest(ResetPasswordRequest request) {
        String username = request.getUsername().trim();
        String newPassword = request.getNewPassword().trim();
        String confirmNewPassword = request.getConfirmNewPassword();

        ArrayList<String> errorList = new ArrayList<String>();

        if (username.equals("")) {
            errorList.add("Username cannot be blank.");
        }

        if (newPassword.equals("")) {
            errorList.add("New Password cannot be blank.");
        }

        if (confirmNewPassword.equals("")) {
            errorList.add("Confirmation Password cannot be blank.");
        }

        if (!errorList.isEmpty()) {
            return new ResetPasswordResponse(false, errorList);
        }

        try {
            PendingResetPassword pendingResetPassword = userDAO.getPendingResetPassword(username);
            User user = userDAO.getUser(username);

            if (pendingResetPassword == null) {
                errorList.add("Username does not exists.");
            }

            //check if the new password and the confirmed new password are the same.
            if (!newPassword.equals(confirmNewPassword)) {
                errorList.add("New Password and Confirmation Password do not match");
            }

            if (!errorList.isEmpty()) {
                return new ResetPasswordResponse(false, errorList);
            }

            String[] result = HashUtility.getHashAndSalt(confirmNewPassword);
            user.setHashedPassword(result[0]);
            user.setSalt(result[1]);

            userDAO.updateUser(user);
            userDAO.deletePendingResetPassword(pendingResetPassword.getId());

            return new ResetPasswordResponse(true, errorList);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new ResetPasswordResponse(false, errorList);
        }
    }
}
