package com.acpcoursera.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acpcoursera.model.UserAccount;
import com.acpcoursera.model.UserGcm;
import com.acpcoursera.model.UserInfo;
import com.acpcoursera.repository.UserGcmRepository;
import com.acpcoursera.repository.UserInfoRepository;

@Controller
public class DmController {

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserInfoRepository usersInfo;

    @Autowired
    private UserGcmRepository usersGcm;

    @RequestMapping(value = "/print/{text}", method = RequestMethod.GET)
    public @ResponseBody String returnUserText(@PathVariable String text) {
        return "You sent: " + text;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<Void> signUp(@RequestBody UserInfo info) {

        if (userDetailsManager.userExists(info.getUsername())) {
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        userDetailsManager.createUser(
                new UserAccount(
                        info.getUsername(),
                        passwordEncoder.encode(info.getPassword()),
                        "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                );

        usersInfo.save(info);
        usersGcm.save(new UserGcm(info.getUsername(), null));

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/gcmtoken", method = RequestMethod.POST)
    public ResponseEntity<Void> sendGcmToken(OAuth2Authentication auth,
            @RequestParam("token") String token) {

    	String username = auth.getName();
    	UserGcm gcmInfo = usersGcm.findByUsername(username);
    	gcmInfo.setToken(token);
    	usersGcm.save(gcmInfo);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<Void> logout(OAuth2Authentication auth) {

    	String username = auth.getName();
    	UserGcm gcmInfo = usersGcm.findByUsername(username);
    	gcmInfo.setToken(null);
    	usersGcm.save(gcmInfo);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
