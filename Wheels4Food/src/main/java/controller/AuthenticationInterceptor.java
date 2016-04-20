/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author andrew.lim.2013 
 */
@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (!uri.contains("UserLoginRequest") && !uri.contains("GetUserListByRoleRequest") 
                && !uri.contains("CreatePendingRegistrationRequest") && !uri.contains("CreateVolunteerPendingRegistrationRequest") 
                && !uri.contains("CreatePendingResetPasswordRequest") && !uri.contains("VerifyResetPasswordTokenRequest")
                && !uri.contains("ResetPasswordRequest")) {
            User user = (User) request.getSession().getAttribute("LOGGEDIN_USER");
            
//            if (user == null) {
//                response.sendRedirect("/Wheels4Food/Login");
//                return false;
//            }
        }
        
        return true;
    }
}
