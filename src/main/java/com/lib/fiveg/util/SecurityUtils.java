package com.lib.fiveg.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // Gets the logged-in username
    }
}
//spring security maintain the security context with hold the information for the each thread
//SecurityContextHolder ->utility class
//SecurityContextHolder.getContext().getAuthentication() return the information of the user