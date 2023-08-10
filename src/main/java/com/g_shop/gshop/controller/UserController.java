package com.g_shop.gshop.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.g_shop.gshop.models.Product;
import com.g_shop.gshop.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public String verifyUser(@RequestParam String decodedToken) throws FirebaseAuthException {
        return userService.verifyUser(decodedToken);
    }
    
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public List<Integer> test(@RequestParam Integer num) throws FirebaseAuthException {
        List<Integer> list = new ArrayList<Integer>();
        list.add(num);
        list.add(num+1);
        
        return list;
    }

    @RequestMapping(value = "/test2", method = RequestMethod.GET)
    public Integer test2(@RequestParam Integer num) throws FirebaseAuthException {
        return num * 5;
    }
}
