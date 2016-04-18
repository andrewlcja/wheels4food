/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.SupplyDAO;
import dao.UserDAO;
import java.io.BufferedReader;
import java.io.FileReader;
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
//            for (int i = 1; i <= 6; i++) {
//                userDAO.createUser(new User("rvwo" + i, "3oYviHe/dSligL1mCivgDM1w7DxOt2roMxpBoCO1VPE=", "rFeEYmd0lzo=", "RVWO" + i, "rvwo" + i + "@gmail.com", "RVWO" + i + " Road", "12345" + i, "Mr RVWO" + i, "9123456" + i, "123", "VWO", "Requester", 0, "Active"));
//                userDAO.createUser(new User("svwo" + i, "3oYviHe/dSligL1mCivgDM1w7DxOt2roMxpBoCO1VPE=", "rFeEYmd0lzo=", "SVWO" + i, "svwo" + i + "@gmail.com", "SVWO" + i + " Road", "12345" + i, "Mr SVWO" + i, "912345" + i + "6", "123", "VWO", "Supplier", 0, "Active"));
//                userDAO.createUser(new User("vol" + i, "3oYviHe/dSligL1mCivgDM1w7DxOt2roMxpBoCO1VPE=", "rFeEYmd0lzo=", "RVWO" + i, "vol" + i + "@gmail.com", "NA", "NA", "Mr Volunteer" + i, "91234" + i + "56", "NA", "NA", "Volunteer", 0, "Active"));
//            }
//        } catch (Exception e) {
//            
//        } 
//        String csvFile = "C:\\Users\\andrew.lim.2013\\Desktop\\supplies.csv";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
//        try {
//
//            br = new BufferedReader(new FileReader(csvFile));
//            br.readLine();
//            while ((line = br.readLine()) != null) {
//
//                // use comma as separator
//                String[] values = line.split(cvsSplitBy);
//
//                System.out.println(values[1] + ", " + values[2] + ", " + values[3] + ", " + values[4] + ", " + values[5] + ", " + values[6] + ", " + values[7]);
//                User ffth = userDAO.getUser("ffth");
//                supplyDAO.createSupply(new Supply(ffth, "", values[1], values[2], values[3], Integer.parseInt(values[4]), 1, Integer.parseInt(values[4]), Integer.parseInt(values[4]), "NA", Float.parseFloat(values[7]), "4/4/2016"));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
//            User ffth = userDAO.getUser("ffth");
//            for (int i = 1001; i <= 2000; i++) {
//                supplyDAO.createSupply(new Supply(ffth, "", "TestSupply" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//            }
//        } catch (Exception e) {
//            
//        } 
//        try {            
//            for (int i = 1; i <= 6; i++) {
//                User u = userDAO.getUser("svwo" + i);
//                supplyDAO.createSupply(new Supply(u, "", "Apple" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Banana" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Orange" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Longan" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Potato" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Carrot" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Grapes" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Mango" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Lettuce" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//                supplyDAO.createSupply(new Supply(u, "", "Rice" + i, "Food", "Packet", 100, 10, 50, 50, "31/12/2016", 3.0f, "15/3/2016"));
//            }
//        } catch (Exception e) {
//
//        }
        ConfigUtility config = new ConfigUtility();
        User user = null;

        //retrieve fields
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        try {
            //validations
            //check for blank username
            if (username.equals("") || password.equals("")) {
                return new UserLoginResponse(false, config.getProperty("login_blank"));
            }

            user = userDAO.getUser(request.getUsername());

            if (user == null) {
                return new UserLoginResponse(false, config.getProperty("login_invalid"));
            }

            if (!HashUtility.verify(password, user.getHashedPassword(), user.getSalt())) {
                return new UserLoginResponse(false, config.getProperty("login_invalid"));
            }

            if (user.getStatus().equals("Inactive")) {
                return new UserLoginResponse(false, config.getProperty("account_suspended"));
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
        ConfigUtility config = new ConfigUtility();
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

        try {
            //validations
            if (username.equals("")) {
                errorList.add(config.getProperty("username_blank"));
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

            if (postalCode.equals("")) {
                errorList.add(config.getProperty("postal_blank"));
            }

            if (pocName.equals("")) {
                if (role.equals("Volunteer")) {
                    errorList.add(config.getProperty("full_name_blank"));
                } else {
                    errorList.add(config.getProperty("poc_blank"));
                }
            }

            if (pocNumber.equals("")) {
                if (role.equals("Volunteer")) {
                    errorList.add(config.getProperty("mobile_blank"));
                } else {
                    errorList.add(config.getProperty("poc_number_blank"));
                }
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
                return new UpdateUserResponse(false, errorList);
            }

            User oldUser = userDAO.getUserById(user.getId());

            if (!user.getUsername().equals(oldUser.getUsername())) {
                if (userDAO.getUser(username) != null) {
                    errorList.add(config.getProperty("username_exists"));
                }
            }

            if (!email.contains("@") || email.length() == 1) {
                errorList.add(config.getProperty("email_invalid"));
            } else if (!email.equals(oldUser.getEmail())) {
                if (userDAO.getUserByEmail(email) != null) {
                    errorList.add(config.getProperty("email_exists"));
                }
            } else if (role.equals("Volunteer") && !pocNumber.equals(oldUser.getPocNumber())) {
                if (userDAO.getUserByMobileNumber(pocNumber) != null) {
                    errorList.add(config.getProperty("mobile_exists "));
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
        ConfigUtility config = new ConfigUtility();
        String username = request.getUsername().trim();
        String oldPassword = request.getOldPassword().trim();
        String newPassword = request.getNewPassword().trim();
        String confirmNewPassword = request.getConfirmNewPassword().trim();

        ArrayList<String> errorList = new ArrayList<String>();

        try {
            //check if the fields entered are empty
            if (username.equals("")) {
                errorList.add(config.getProperty("username_blank"));
            }

            if (oldPassword.equals("")) {
                errorList.add(config.getProperty("current_password_blank"));
            }

            if (newPassword.equals("")) {
                errorList.add(config.getProperty("new_password_blank"));
            }

            if (confirmNewPassword.equals("")) {
                errorList.add(config.getProperty("confirmation_password_blank"));
            }

            if (!errorList.isEmpty()) {
                return new ChangePasswordResponse(false, errorList);
            }

            User user = userDAO.getUser(username);

            //check if the password entered is the same as current one
            if (!HashUtility.verify(oldPassword, user.getHashedPassword(), user.getSalt())) {
                errorList.add(config.getProperty("current_password_invalid"));
            }

            //check if the old passwords and current password are the same
            if (HashUtility.verify(newPassword, user.getHashedPassword(), user.getSalt())) {
                errorList.add(config.getProperty("password_match"));
            }

            //check if the new password and the confirmed new password are the same.
            if (!newPassword.equals(confirmNewPassword)) {
                errorList.add(config.getProperty("password_match"));
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
        ConfigUtility config = new ConfigUtility();
        String email = request.getEmail().trim();
        String endPoint = request.getEndPoint().trim();
        ArrayList<String> errorList = new ArrayList<String>();

        try {
            if (email.equals("")) {
                errorList.add(config.getProperty("email_blank"));
            }

            if (endPoint.equals("")) {
                errorList.add(config.getProperty("end_point_blank"));
            }

            if (!errorList.isEmpty()) {
                return new CreatePendingResetPasswordResponse(false, errorList);
            }

            if (!email.contains("@") || email.length() == 1) {
                errorList.add(config.getProperty("email_invalid"));
                return new CreatePendingResetPasswordResponse(false, errorList);
            }

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
        ConfigUtility config = new ConfigUtility();
        ArrayList<String> errorList = new ArrayList<String>();

        try {
            if (token.equals("")) {
                errorList.add(config.getProperty("token_blank"));
                return new VerifyResetPasswordTokenResponse(false, errorList);
            }

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
                    errorList.add(config.getProperty("token_expired"));
                    return new VerifyResetPasswordTokenResponse(false, errorList);
                }
            } else {
                errorList.add(config.getProperty("token_invalid"));
                return new VerifyResetPasswordTokenResponse(false, errorList);
            }
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new VerifyResetPasswordTokenResponse(false, errorList);
        }
    }

    public ResetPasswordResponse resetPasswordRequest(ResetPasswordRequest request) {
        ConfigUtility config = new ConfigUtility();
        String username = request.getUsername().trim();
        String newPassword = request.getNewPassword().trim();
        String confirmNewPassword = request.getConfirmNewPassword();

        ArrayList<String> errorList = new ArrayList<String>();

        try {
            if (username.equals("")) {
                errorList.add(config.getProperty("username_blank"));
            }

            if (newPassword.equals("")) {
                errorList.add(config.getProperty("new_password_blank"));
            }

            if (confirmNewPassword.equals("")) {
                errorList.add(config.getProperty("confirmation_password_blank"));
            }

            if (!errorList.isEmpty()) {
                return new ResetPasswordResponse(false, errorList);
            }

            PendingResetPassword pendingResetPassword = userDAO.getPendingResetPassword(username);
            User user = userDAO.getUser(username);

            if (pendingResetPassword == null) {
                errorList.add(config.getProperty("username_exists"));
            }

            //check if the new password and the confirmed new password are the same.
            if (!newPassword.equals(confirmNewPassword)) {
                errorList.add(config.getProperty("password_match"));
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
