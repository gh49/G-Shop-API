package com.g_shop.gshop.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.g_shop.gshop.models.User;
import com.g_shop.gshop.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public boolean verifyUser(@RequestParam String decodedToken) throws FirebaseAuthException {
        if(userService.verifyUser(decodedToken) == null)
            return false;
        return true;
    }

    @RequestMapping(value = "/get_details", method = RequestMethod.GET)
    public User getUserDetails(@RequestParam String decodedToken) {
        User user = userService.getUserDetails(decodedToken);
        System.out.println(user);
        return user;
    }

    @RequestMapping(value = "/update_user", method = RequestMethod.POST)
    public boolean updateUser(@RequestBody Map<String, Object> data) {
        return userService.updateUser(data);
    }

    @RequestMapping(value = "/test_update_user", method = RequestMethod.POST)
    public User updateUserTest(@RequestBody Map<String, Object> data) {
        User user = User.fromJson(data);
        return user;
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
