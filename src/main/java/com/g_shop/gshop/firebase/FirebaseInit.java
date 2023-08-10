package com.g_shop.gshop.firebase;

import java.io.FileInputStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirebaseInit {
    
    @PostConstruct
    public void init() {
        try{
            FileInputStream serviceAccount = new FileInputStream("./firebase_admin_key.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase Initialized Successfully");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
