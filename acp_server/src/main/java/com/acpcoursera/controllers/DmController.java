package com.acpcoursera.controllers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
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
import com.acpcoursera.repository.FollowerRepository;
import com.acpcoursera.repository.FollowingRepository;
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

    @Autowired
    private FollowerRepository followers;

    @Autowired
    private FollowingRepository followings;

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
    public ResponseEntity<List<UserInfo>> getUserList(OAuth2Authentication auth,
    		@RequestParam("teen_only") boolean teenOnly) {

    	String username = auth.getName();
    	UserInfo currentUser = new UserInfo();
    	currentUser.setUsername(username);

    	List<Follower> allFollowers = followers.findAllByUsername(username);
    	List<Following> allFollowing = followings.findAllByUsername(username);

    	List<UserInfo> allUsersInfo = null;
    	if (teenOnly) {
    		allUsersInfo = usersInfo.findAllByUserType(UserInfo.TYPE_TEEN);
    	}
    	else {
    		allUsersInfo = usersInfo.findAll();
    	}

    	for (Follower f : allFollowers) {
    		allUsersInfo.remove(new UserInfo(f.getFollowerName()));
    	}
    	for (Following f : allFollowing) {
    		allUsersInfo.remove(new UserInfo(f.getFollowingName()));
    	}
    	allUsersInfo.remove(currentUser);

    	return new ResponseEntity<List<UserInfo>>(allUsersInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "/followers", method = RequestMethod.GET)
    public ResponseEntity<List<Follower>> getFollowers(OAuth2Authentication auth) {

    	String username = auth.getName();
    	UserInfo currentUser = new UserInfo();
    	currentUser.setUsername(username);

    	List<Follower> allFollowers = followers.findAllByUsername(username);

    	return new ResponseEntity<List<Follower>>(allFollowers, HttpStatus.OK);
    }

    @RequestMapping(value = "/following", method = RequestMethod.GET)
    public ResponseEntity<List<Following>> getFollowing(OAuth2Authentication auth) {

    	String username = auth.getName();
    	UserInfo currentUser = new UserInfo();
    	currentUser.setUsername(username);

    	List<Following> allFollowing = followings.findAllByUsername(username);

    	return new ResponseEntity<List<Following>>(allFollowing, HttpStatus.OK);
    }

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    public ResponseEntity<Void> follow(OAuth2Authentication auth,
    		@RequestParam("username_to_follow") String usernameToFollow,
    		@RequestParam("major_data") boolean majorData,
    		@RequestParam("minor_data") boolean minorData) {

    	String username = auth.getName();

    	UserInfo followerUserInfo = usersInfo.findByUsername(username);
    	if (followerUserInfo == null) {
    		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    	}

    	String followerFullName = followerUserInfo.getFirstName()
    			+ " " + followerUserInfo.getSecondName();
    	boolean isTeen = followerUserInfo.getUserType().equals(UserInfo.TYPE_TEEN);

    	Follower follower = new Follower();
    	follower.setUsername(usernameToFollow);
    	follower.setFollowerName(username);
    	follower.setFollowerFullName(followerFullName);
    	follower.setTeen(isTeen);
    	follower.setAccepted(false);
    	follower.setPending(false);
    	follower.setMajorData(majorData);
    	follower.setMinorData(minorData);


    	UserInfo followingUserInfo = usersInfo.findByUsername(usernameToFollow);
    	if (followingUserInfo == null) {
    		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    	}

    	String followingFullName = followingUserInfo.getFirstName()
    			+ " " + followingUserInfo.getSecondName();

    	Following following = new Following();
    	following.setUsername(username);
    	following.setFollowingName(usernameToFollow);
    	following.setFollowingFullName(followingFullName);
    	following.setPending(true);
    	following.setInvite(false);
    	following.setMajorData(majorData);
    	following.setMinorData(minorData);

    	followers.save(follower);
    	followings.save(following);

    	UserGcm followingGcmInfo = usersGcm.findByUsername(username);
    	UserGcm followerGcmInfo = usersGcm.findByUsername(usernameToFollow);

    	GcmMessage followingMessage = new GcmMessage();
    	followingMessage.addRecipient(followingGcmInfo.getToken());
    	followingMessage.addDataField("table", "following");

    	GcmMessage followerMessage = new GcmMessage();
    	followerMessage.addRecipient(followerGcmInfo.getToken());
    	followerMessage.addDataField("table", "follower");

    	GcmResponse followingResponse = sendGcmMessage(followingMessage);
    	GcmResponse followerResponse = sendGcmMessage(followerMessage);

    	System.out.println(followingResponse);
    	System.out.println(followerResponse);

    	return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public ResponseEntity<Void> invite(OAuth2Authentication auth,
    		@RequestParam("username_to_invite") String usernameToInvite,
    		@RequestParam("major_data") boolean majorData,
    		@RequestParam("minor_data") boolean minorData) {

    	String username = auth.getName();

    	UserInfo followerUserInfo = usersInfo.findByUsername(usernameToInvite);
    	if (followerUserInfo == null) {
    		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    	}

    	String followerFullName = followerUserInfo.getFirstName()
    			+ " " + followerUserInfo.getSecondName();
    	boolean isTeen = followerUserInfo.getUserType().equals(UserInfo.TYPE_TEEN);

    	Follower follower = new Follower();
    	follower.setUsername(username);
    	follower.setFollowerName(usernameToInvite);
    	follower.setFollowerFullName(followerFullName);
    	follower.setTeen(isTeen);
    	follower.setAccepted(true);
    	follower.setPending(true);
    	follower.setMajorData(majorData);
    	follower.setMinorData(minorData);


    	UserInfo followingUserInfo = usersInfo.findByUsername(username);
    	if (followingUserInfo == null) {
    		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    	}

    	String followingFullName = followingUserInfo.getFirstName()
    			+ " " + followingUserInfo.getSecondName();

    	Following following = new Following();
    	following.setUsername(usernameToInvite);
    	following.setFollowingName(username);
    	following.setFollowingFullName(followingFullName);
    	following.setPending(true);
    	following.setInvite(true);
    	following.setMajorData(majorData);
    	following.setMinorData(minorData);

    	followers.save(follower);
    	followings.save(following);

    	UserGcm followingGcmInfo = usersGcm.findByUsername(usernameToInvite);
    	UserGcm followerGcmInfo = usersGcm.findByUsername(username);

    	GcmMessage followingMessage = new GcmMessage();
    	followingMessage.addRecipient(followingGcmInfo.getToken());
    	followingMessage.addDataField("table", "following");

    	GcmMessage followerMessage = new GcmMessage();
    	followerMessage.addRecipient(followerGcmInfo.getToken());
    	followerMessage.addDataField("table", "follower");

    	GcmResponse followingResponse = sendGcmMessage(followingMessage);
    	GcmResponse followerResponse = sendGcmMessage(followerMessage);

    	System.out.println(followingResponse);
    	System.out.println(followerResponse);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/accept", method = RequestMethod.POST)
    public ResponseEntity<Void> accept(OAuth2Authentication auth,
    		@RequestParam("username_to_accept") String usernameToAccept,
    		@RequestParam("is_invite") boolean isInvite) {

    	String username = auth.getName();

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/decline", method = RequestMethod.POST)
    public ResponseEntity<Void> decline(OAuth2Authentication auth,
    		@RequestParam("username_to_decline") String usernameToDecline,
    		@RequestParam("is_invite") boolean isInvite) {

    	String username = auth.getName();

        return new ResponseEntity<Void>(HttpStatus.OK);
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
