package com.acpcoursera.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> signupTest(@RequestBody UserInfo info) {

        if (userDetailsManager.userExists(info.getUsername())) {
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        info.setAuthorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT");
        info.setPassword(passwordEncoder.encode(info.getPassword()));

        userDetailsManager.createUser(info);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
