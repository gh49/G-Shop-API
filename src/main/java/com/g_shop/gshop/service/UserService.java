package com.g_shop.gshop.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.g_shop.gshop.models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class UserService {
    
    private static final String USERS_COLLECTION = "users";
    private static final String CARTS_COLLECTION = "carts";
    private static final String PRODUCTS_COLLECTION = "products";
    private static final String PURCHASES_COLLECTION = "purchases";

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

    public boolean addToCart(Map<String, Object> data) {
        String decodedToken = null;
        String pID = null;

        try {
            decodedToken = (String)data.get("decodedToken");
            pID = (String)data.get("pID");
        } catch (Exception e) {
            return false;
        }

        FirebaseToken token = verifyUser(decodedToken);
        if(token == null){
            System.out.println("Token not found");
            return false;
        }
        if(!isProduct(pID)) {
            System.out.println("Product does not exist");
            return false;
        }
        if(!productHasQuantity(pID)) {
            System.out.println("Product's quantity is 0");
            return false;
        }

        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference cartsCollectionRef = firestore.collection(CARTS_COLLECTION);
        DocumentReference userCartDocRef = cartsCollectionRef.document(token.getUid());
        CollectionReference productsInCartCollectionRef = userCartDocRef.collection("products");

        Map<String, Object> pIDMap = new HashMap<String, Object>();
        pIDMap.put("pID", pID);
        try {
            ApiFuture<DocumentReference> cartProductDocFuture = productsInCartCollectionRef.add(pIDMap);
            
            while(!cartProductDocFuture.isDone()) {
                if(cartProductDocFuture.isCancelled())
                    return false;
            }

            DocumentReference cartProductDoc = cartProductDocFuture.get();
            return true;
        } catch (Exception e) {
            return false;
        }
        
    }

    public boolean productHasQuantity(String pID) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference productsCollectionRef = firestore.collection(PRODUCTS_COLLECTION);
        DocumentReference productDocRef = productsCollectionRef.document(pID);

        try {
            ApiFuture<DocumentSnapshot> productDocSnapshotFuture = productDocRef.get();

        while(!productDocSnapshotFuture.isDone()) {
            if(productDocSnapshotFuture.isCancelled()) {
                return false;
            }
        }

        DocumentSnapshot productDocSnapshot = productDocSnapshotFuture.get();

        Long quantity = (Long)productDocSnapshot.getData().get("quantity");
        System.out.println(quantity);

        return quantity > 0;
        }
        catch(Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public boolean isProduct(String pID) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference productsCollectionRef = firestore.collection(PRODUCTS_COLLECTION);
        if(pID == null)
            return false;

        DocumentReference productDocRef = productsCollectionRef.document(pID);

        try {
            ApiFuture<DocumentSnapshot> productDocSnapshotFuture = productDocRef.get();

        while(!productDocSnapshotFuture.isDone()) {
            if(productDocSnapshotFuture.isCancelled()) {
                return false;
            }
        }

        DocumentSnapshot productDocSnapshot = productDocSnapshotFuture.get();
        return productDocSnapshot.exists();
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
