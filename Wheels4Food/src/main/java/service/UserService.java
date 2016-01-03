/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.UserDAO;
import java.util.ArrayList;
import java.util.List;
import model.ChangePasswordRequest;
import model.ChangePasswordResponse;
import model.DeleteUserResponse;
import model.UpdateUserResponse;
import model.User;
import model.UserLoginRequest;
import model.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import utility.HashUtility;

/**
 *
 * @author andrew.lim.2013
 */
public class UserService {

    @Autowired
    UserDAO userDAO;

    public List<User> getUserListRequest() throws Exception {
        return userDAO.retrieveAll();
    }

    public List<User> getUserListByRoleRequest(String role) throws Exception {
        return userDAO.getUserListByRole(role);
    }
    
    public User getUserByUsernameRequest(String username) throws Exception {
        return userDAO.getUser(username);
    }

    public UserLoginResponse userLoginRequest(UserLoginRequest request) {
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

    public UpdateUserResponse updateUserRequest(User user) {
        String username = user.getUsername().trim();
        String organizationName = user.getOrganizationName().trim();
        String email = user.getEmail().trim();
        String address = user.getAddress().trim();
        String postalCode = user.getPostalCode().trim();
        String pocName = user.getPocName().trim();
        String pocNumber = user.getPocNumber().trim();
        String licenseNumber = user.getLicenseNumber().trim();
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
            errorList.add("Point of Contact Name cannot be blank");
        }

        if (pocNumber.equals("")) {
            errorList.add("Point of Contact Number cannot be blank");
        }

        if (licenseNumber.equals("")) {
            errorList.add("License Number cannot be blank");
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
}
