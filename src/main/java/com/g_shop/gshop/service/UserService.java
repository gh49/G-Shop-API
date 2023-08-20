package com.g_shop.gshop.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.g_shop.gshop.models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldMask;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class UserService {
    
    private static final String USERS_COLLECTION = "users";
    private static final String CARTS_COLLECTION = "carts";
    private static final String PRODUCTS_COLLECTION = "products";
    private static final String ORDERS_COLLECTION = "orders";

    public FirebaseToken verifyUser(String decodedToken) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        try{
            FirebaseToken token = auth.verifyIdToken(decodedToken);
            if(token == null)
                return null;
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
        System.out.println("HI");
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
            ApiFuture<DocumentSnapshot> userDocFuture = usersCollectionRef.document(uID).get();
            while(!userDocFuture.isDone()) {
                if(userDocFuture.isCancelled())
                    return false;
            }

            DocumentSnapshot userDoc = userDocFuture.get();
            ApiFuture<WriteResult> updateFuture;
            if(!userDoc.exists()) {
                data.put("uID", uID);
                updateFuture = usersCollectionRef.document(uID).create(data);
            }
            else
                updateFuture = usersCollectionRef.document(uID).update(data);

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

    public boolean checkout(String decodedToken) {
        FirebaseToken token = verifyUser(decodedToken);
        if(token == null){
            System.out.println("Token not found");
            return false;
        }

        try {
            List<String> productList = getProductsFromCartWithDelete(token);
            if(productList == null || productList.isEmpty()) {
                return false;
            }

            Map<String, Integer> map = new HashMap<>();
            if(!canBuyProducts(productList, map)) {
                return false;
            }

            for (String pID : productList) {
                deductQuantity(pID, map.get(pID));
                map.put(pID, 0);
            }

            createOrder(token, productList);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void createOrder(FirebaseToken token, List<String> pIDList) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference ordersColRef = firestore.collection(ORDERS_COLLECTION);

        Map<String, Object> data = new HashMap<>();
        String dateTime = LocalDate.now().toString() + " " + LocalTime.now().toString();
        data.put("uID", token.getUid());
        data.put("dateTime", dateTime);
        data.put("total", getTotal(pIDList));
        data.put("products", pIDList);

        ApiFuture<DocumentReference> orderDocFuture = ordersColRef.add(data);
        while (!orderDocFuture.isDone()) {
            if(orderDocFuture.isCancelled()) {
                throw new IllegalArgumentException("Failed lol");
            }
        }
    }

    public Double getTotal(List<String> pIDList) {
        double total = 0.0;
        for (String pID : pIDList) {
            total += getProductPrice(pID);
        }
        return total;
    }

    public void deductQuantity(String pID, Integer quantity) {
        if(quantity == 0)
            return;
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference productsCollectionRef = firestore.collection(PRODUCTS_COLLECTION);
        DocumentReference productDocRef = productsCollectionRef.document(pID);

        if(pID == null)
            return;
        if(!isProduct(pID))
            return;
        
        Map<String, Object> data = new HashMap<>();
        data.put("quantity", getProductQuantity(pID) - quantity);

            ApiFuture<WriteResult> productWriteResult = productDocRef.update(data);
            while (!productWriteResult.isDone()) {
                if(productWriteResult.isCancelled()) {
                    return;
                }
            }
            return;
    }

    public boolean canBuyProducts(List<String> pIDList, Map<String, Integer> map) {
        try {
            for(String pID : pIDList) {
                if(map.get(pID) == null) {
                    map.put(pID, 1);
                }
                else{
                    int previousValue = map.get(pID);
                    map.put(pID, previousValue + 1);
                }
            }

            for (String pID : pIDList) {
                if(getProductQuantity(pID) - map.get(pID) < 0) {
                    return false;
                }
            }

            return true;
            
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public ArrayList<String> getProductsFromCartWithDelete(FirebaseToken token) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference cartsCollectionRef = firestore.collection(CARTS_COLLECTION);
        DocumentReference userCartDocRef = cartsCollectionRef.document(token.getUid());
        CollectionReference userCartProductsRef = userCartDocRef.collection("products");

        try {
            ApiFuture<QuerySnapshot> query = userCartProductsRef.get();

            while(!query.isDone()) {
            if(query.isCancelled()) {
                return null;
            }

            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> docsSnapshot = querySnapshot.getDocuments();

            if(docsSnapshot.size() <= 0) {
                return null;
            }

            ArrayList<String> list = new ArrayList<>();

            for(QueryDocumentSnapshot docSnapshot : docsSnapshot) {
                deleteDocument(docSnapshot.getReference());
                list.add((String)docSnapshot.get("pID"));
            }
            deleteDocument(userCartDocRef);

            return list;
        }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return null;
    }

    public boolean deleteDocument(DocumentReference doc) {
        try {
            ApiFuture<WriteResult> deleteFuture = doc.delete();
            while (!deleteFuture.isDone()) {
                if (deleteFuture.isCancelled()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Double getProductPrice(String pID) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference productsCollectionRef = firestore.collection(PRODUCTS_COLLECTION);

        if(pID == null)
            return null;
        if(!isProduct(pID))
            return null;

        DocumentReference productDocRef = productsCollectionRef.document(pID);
        //productDocRef.get();
        try {
            ApiFuture<DocumentSnapshot> productDocSnapshotFuture = productDocRef.get(FieldMask.of("price"));

        while(!productDocSnapshotFuture.isDone()) {
            if(productDocSnapshotFuture.isCancelled()) {
                return null;
            }
        }

        DocumentSnapshot productDocSnapshot = productDocSnapshotFuture.get();
        return (Double)productDocSnapshot.getData().get("price");
        }
        catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
        
    }

    public Integer getProductQuantity(String pID) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference productsCollectionRef = firestore.collection(PRODUCTS_COLLECTION);
        if(pID == null)
            return null;
        if(!isProduct(pID))
            return null;

        DocumentReference productDocRef = productsCollectionRef.document(pID);
        try {
            ApiFuture<DocumentSnapshot> productDocSnapshotFuture = productDocRef.get(FieldMask.of("quantity"));

        while(!productDocSnapshotFuture.isDone()) {
            if(productDocSnapshotFuture.isCancelled()) {
                return null;
            }
        }

        DocumentSnapshot productDocSnapshot = productDocSnapshotFuture.get();
        System.out.println(productDocSnapshot.getData().get("quantity"));
        return (int)(long)productDocSnapshot.getData().get("quantity");
        }
        catch(Exception e) {
            System.out.println(e.toString());
            return null;
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
