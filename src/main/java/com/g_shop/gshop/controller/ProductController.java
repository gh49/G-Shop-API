package com.g_shop.gshop.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.g_shop.gshop.models.Product;
import com.g_shop.gshop.service.ProductService;
import com.g_shop.gshop.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/get_by_category", method = RequestMethod.GET)
    public List<Product> getProducts(@RequestParam String category) throws FirebaseAuthException, InterruptedException, ExecutionException {
        try{
            return productService.getProducts(category);
        }
        catch(Exception e) {
            return null;
        }
    }

    @RequestMapping(value = "/get_details", method = RequestMethod.GET)
    public Map<String, Object> getProductDetails(@RequestParam String pID) throws FirebaseAuthException, InterruptedException, ExecutionException {
        return productService.getProductDetails(pID);
    }
    
    
}
