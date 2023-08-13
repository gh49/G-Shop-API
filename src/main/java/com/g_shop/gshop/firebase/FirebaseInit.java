package com.g_shop.gshop.firebase;

import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.auth.http.HttpTransportFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.http.HttpTransportOptions.DefaultHttpTransportFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirebaseInit {
    
    @PostConstruct
    public void init() {
        try{
            String deployPath = "/etc/secrets/";
            String testPath = "./";
            FileInputStream serviceAccount = new FileInputStream(testPath + "firebase_admin_key.json");
            GoogleCredentials credentials;
            credentials = GoogleCredentials.fromStream(serviceAccount);
            //credentials = getDefaultCredentials();
            FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(credentials)
            .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase Initialized Successfully");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public GoogleCredentials getDefaultCredentials() throws URISyntaxException {
        

        return ServiceAccountCredentials.newBuilder().setClientId("111781004238009863304").
            setClientEmail("firebase-adminsdk-d79wc@g-shop-de75e.iam.gserviceaccount.com").
            setPrivateKeyId("9549317ac16db0fdb7feb5b64f3a90e46f53375a").
            setHttpTransportFactory(new DefaultHttpTransportFactory()).
            setTokenServerUri(new URI("https://oauth2.googleapis.com/token")).
            setProjectId("g-shop-de75e").
            setQuotaProjectId(null).build();
    }

}
