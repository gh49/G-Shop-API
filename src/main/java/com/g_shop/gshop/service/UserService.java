package com.g_shop.gshop.service;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Service
public class UserService {
    
    private static final String USERS_COLLECTION = "users";

    public String verifyUser(String decodedToken) throws FirebaseAuthException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseToken token = null;
        
        token = auth.verifyIdToken(decodedToken);
        
        return token.getEmail();
    }

}
