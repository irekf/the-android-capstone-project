package com.acpcoursera.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acpcoursera.model.UserInfo;

@Controller
public class TestController {

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/print/{text}", method = RequestMethod.GET)
    public @ResponseBody String returnUserText(@PathVariable String text) {
        return "You sent: " + text;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public @ResponseBody String signupTest(@RequestBody UserInfo info) {

        System.out.println(info);

//        userDetailsManager.createUser(new UserAccount("user2", passwordEncoder.encode("pass2"), "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT"));
//        userDetailsManager.createUser(new UserAccount("user3", passwordEncoder.encode("pass3"), "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT"));

        return "you sent some user info";
    }

}
