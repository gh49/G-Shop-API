package com.g_shop.gshop.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import com.g_shop.gshop.models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class UserService {
    
    private static final String USERS_COLLECTION = "users";

    public FirebaseToken verifyUser(String decodedToken) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        try{
            FirebaseToken token = auth.verifyIdToken(decodedToken);
            if(token == null)
                return null;
            System.out.println(token.getEmail());
            return token;
        }
        catch(Exception e) {
            return null;
        }
    }

    public User getUserDetails(String decodedToken) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        try {
            FirebaseToken token = auth.verifyIdToken(decodedToken);
            String uID = token.getUid();

            Firestore firestore = FirestoreClient.getFirestore();
            CollectionReference usersCollectionRef = firestore.collection(USERS_COLLECTION);
            System.out.println("HELLOW1");
            ApiFuture<DocumentSnapshot> userDataFuture = usersCollectionRef.document(uID).get();

            while(!userDataFuture.isDone()) {
                if(userDataFuture.isCancelled())
                    return null;
            }

            DocumentSnapshot userData = userDataFuture.get();
            System.out.println(uID);
            return User.fromJson(userData.getData());
        }
        catch(Exception e) {
            return null;
        }
    }

    public boolean updateUser(Map<String, Object> data) {
        FirebaseToken token = verifyUser((String)data.get("decodedToken"));
        if(token == null){
            System.out.println("Token not found");
            return false;
        }
        String uID = token.getUid();
        data = extractUpdatableData(data);

        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference usersCollectionRef = firestore.collection(USERS_COLLECTION);
        try{
            ApiFuture<WriteResult> updateFuture = usersCollectionRef.document(uID).update(data);
            while(!updateFuture.isDone()) {
                if(updateFuture.isCancelled())
                    return false;
            }

            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    private Map<String, Object> extractUpdatableData(Map<String, Object> data) {
        Map<String, Object> newData = new HashMap<>();
        if(data.get("name") != null) {
            newData.put("name", data.get("name"));
        }
        if(data.get("phoneNumber") != null) {
            newData.put("phoneNumber", data.get("phoneNumber"));
        }
        if(data.get("gender") != null) {
            newData.put("gender", data.get("gender"));
        }
        if(data.get("dateOfBirth") != null) {
            newData.put("dateOfBirth", data.get("dateOfBirth"));
        }

        return newData;
    }

}
