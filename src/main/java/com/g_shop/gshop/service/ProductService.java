package com.g_shop.gshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.g_shop.gshop.models.Product;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class ProductService {
    
    private static final String PRODUCTS_COLLECTION = "products";
    private static final String RATING_COLLECTION = "ratings";
    private static final String COMMENTS_COLLECTION = "comments";

    public List<Product> getProducts(String category) throws FirebaseAuthException, InterruptedException, ExecutionException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference productsCollection = firestore.collection(PRODUCTS_COLLECTION);
        ApiFuture<QuerySnapshot> querySnapshot = null;

        try {
            if(category.equalsIgnoreCase("allProducts"))
                querySnapshot = productsCollection.get();
            else
                querySnapshot = productsCollection.whereEqualTo("category", category).get();

            while(!querySnapshot.isDone()) {
                if(querySnapshot.isCancelled())
                    return null;
            }
            QuerySnapshot snapshot = querySnapshot.get();
            List<QueryDocumentSnapshot> items = snapshot.getDocuments();
            List<Product> productList = new ArrayList<>()
    ;
            for(int i=0; i<items.size(); i++) {
                QueryDocumentSnapshot item = items.get(i);
                Double price = Double.parseDouble(item.get("price").toString());
                Double rating = Double.parseDouble(item.get("rating").toString());
                Product product = new Product((String)item.get("pID"),(String)item.get("name"),
                (String)item.get("category"),(String)item.get("description"),(String)item.get("image"),
                price, rating, (int)(long)item.get("quantity"),
                (int)(long)item.get("ratingCount"));

                productList.add(product);
            }

            return productList;
        }
        catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getProductDetails(String pID) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference productsCollection = firestore.collection(PRODUCTS_COLLECTION);
        DocumentReference productDocRef = productsCollection.document(pID);
        ApiFuture<DocumentSnapshot> productSnapshotFuture;
        try{
            productSnapshotFuture = productDocRef.get();

            while(!productSnapshotFuture.isDone()) {
                if(productSnapshotFuture.isCancelled())
                    return null;
            }

            DocumentSnapshot productSnapshot = productSnapshotFuture.get();
            return productSnapshot.getData();
        }
        catch(Exception e) {
            return null;
        }
    }
}
