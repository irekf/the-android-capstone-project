package com.acpcoursera.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acpcoursera.UserAccount;

@Controller
public class TestController {

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @RequestMapping(value = "/print/{text}", method = RequestMethod.GET)
    public @ResponseBody String returnUserText(@PathVariable String text) {
        return "You sent: " + text;
    }

    @RequestMapping(value = "/signup/{text}", method = RequestMethod.GET)
    public @ResponseBody String signupTest(@PathVariable String text) {

        userDetailsManager.createUser(new UserAccount("user1", "pass1"));

        return "You sent this text using /signup/{text}: " + text;
    }

}
