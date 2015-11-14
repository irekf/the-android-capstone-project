package com.acpcoursera.controllers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

import com.acpcoursera.model.CheckInData;
import com.acpcoursera.model.Follower;
import com.acpcoursera.model.Following;
import com.acpcoursera.model.GcmMessage;
import com.acpcoursera.model.GcmResponse;
import com.acpcoursera.model.UserAccount;
import com.acpcoursera.model.UserGcm;
import com.acpcoursera.model.UserInfo;
import com.acpcoursera.repository.CheckInDataRepository;
import com.acpcoursera.repository.UserGcmRepository;
import com.acpcoursera.repository.UserInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private CheckInDataRepository usersCheckIn;

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

    @RequestMapping(value = "/checkin", method = RequestMethod.POST)
    public ResponseEntity<Void> checkIn(OAuth2Authentication auth,
    		@RequestBody CheckInData data) {

    	String username = auth.getName();
    	data.setUsername(username);

    	data.setCheckInTimestamp(new Timestamp(System.currentTimeMillis()));
    	usersCheckIn.save(data);

    	/* send GCM */
    	UserGcm gcmInfo = usersGcm.findByUsername(username);
    	GcmMessage message = new GcmMessage();
    	message.addRecipient(gcmInfo.getToken());
    	message.addDataField("table", "check_in_data");

    	GcmResponse response = sendGcmMessage(message);
    	System.out.println(response);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/checkin", method = RequestMethod.GET)
    public ResponseEntity<List<CheckInData>> getCheckInData(OAuth2Authentication auth) {

    	String username = auth.getName();

    	List<CheckInData> checkInData = usersCheckIn.findAllByUsername(username);

    	return new ResponseEntity<List<CheckInData>>(checkInData, HttpStatus.OK);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserInfo>> getUserList(OAuth2Authentication auth) {

    	String username = auth.getName();
    	UserInfo currentUser = new UserInfo();
    	currentUser.setUsername(username);

    	List<UserInfo> allUsersInfo = usersInfo.findAll();
    	allUsersInfo.remove(currentUser);

    	return new ResponseEntity<List<UserInfo>>(allUsersInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "/followers", method = RequestMethod.GET)
    public ResponseEntity<List<Follower>> getFollowers(OAuth2Authentication auth) {

    	String username = auth.getName();
    	UserInfo currentUser = new UserInfo();
    	currentUser.setUsername(username);

    	List<Follower> followers = new ArrayList<>();

    	Follower f1 = new Follower();
    	f1.setUsername("irekf");
    	f1.setFollowerName("patrick22");
    	f1.setTeen(true);
    	f1.setMajorData(true);
    	f1.setMinorData(true);
    	f1.setFollowerFullName("Patrick Star");
    	f1.setAccepted(true);

    	Follower f2 = new Follower();
    	f2.setUsername("irekf");
    	f2.setFollowerName("will89");
    	f2.setTeen(false);
    	f2.setMajorData(true);
    	f2.setMinorData(false);
    	f2.setFollowerFullName("Will Bill");
    	f2.setAccepted(false);

    	Follower f3 = new Follower();
    	f3.setUsername("irekf");
    	f3.setFollowerName("scooby_2");
    	f3.setTeen(false);
    	f3.setMajorData(true);
    	f3.setMinorData(false);
    	f3.setFollowerFullName("Helen Green");
    	f3.setAccepted(true);
    	f3.setPending(true);

    	followers.add(f1);
    	followers.add(f2);
    	followers.add(f3);

    	return new ResponseEntity<List<Follower>>(followers, HttpStatus.OK);
    }

    @RequestMapping(value = "/following", method = RequestMethod.GET)
    public ResponseEntity<List<Following>> getFollowing(OAuth2Authentication auth) {

    	String username = auth.getName();
    	UserInfo currentUser = new UserInfo();
    	currentUser.setUsername(username);

    	List<Following> following = new ArrayList<>();

    	Following f1 = new Following();
    	f1.setUsername("irekf");
    	f1.setFollowingName("abcde");
    	f1.setMajorData(true);
    	f1.setMinorData(true);
    	f1.setFollowingFullName("Ann Smith");
    	f1.setPending(true);

    	Following f2 = new Following();
    	f2.setUsername("irekf");
    	f2.setFollowingName("driver");
    	f2.setMajorData(true);
    	f2.setMinorData(false);
    	f2.setFollowingFullName("Jane Lee");
    	f2.setInvite(true);

    	Following f3 = new Following();
    	f3.setUsername("irekf");
    	f3.setFollowingName("bee09");
    	f3.setMajorData(true);
    	f3.setMinorData(false);
    	f3.setFollowingFullName("George C.");

    	following.add(f1);
    	following.add(f2);
    	following.add(f3);

    	return new ResponseEntity<List<Following>>(following, HttpStatus.OK);
    }


    private GcmResponse sendGcmMessage(GcmMessage message) {

    	GcmResponse response = new GcmResponse();

    	try {
    		URL gcmUrl = new URL("https://gcm-http.googleapis.com/gcm/send");

    		HttpURLConnection connection = (HttpURLConnection) gcmUrl.openConnection();
    		connection.setRequestMethod("POST");
    		connection.setDoOutput(true);
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setRequestProperty("Authorization", "key=" + System.getProperty("gcmkey"));

    		ObjectMapper mapper = new ObjectMapper();
    		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    		mapper.writeValue(out, message);
    		out.flush();
    		out.close();

    		int responseCode = connection.getResponseCode();
    		if (responseCode == 200) {
    			InputStreamReader in = new InputStreamReader(connection.getInputStream());
    			response = mapper.readValue(in, GcmResponse.class);
    		}
    		response.setCode(responseCode);

    	}
    	catch (IOException e) {
    		System.out.println(e.getMessage());
    	}

    	return response;
    }

}
