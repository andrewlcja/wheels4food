/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.UserDAO;
import java.util.ArrayList;
import java.util.List;
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
            errorList.add("POC Name cannot be blank");
        }

        if (pocNumber.equals("")) {
            errorList.add("POC Number cannot be blank");
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
            User oldUser = userDAO.getUser(username);
            
//            if (user.getUsername().equals(oldUser.getUsername())) {
//                errorList.add("Username already exist");
//            }
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new UpdateUserResponse(false, errorList);
        }
        
        if (!email.contains("@")) {
            errorList.add("Invalid email");
        }

        if (!errorList.isEmpty()) {
            return new UpdateUserResponse(false, errorList);
        }
        
        try {
            userDAO.updateUser(user);
            return new UpdateUserResponse(true, errorList); 
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new UpdateUserResponse(false, errorList); 
        }
    }
}
