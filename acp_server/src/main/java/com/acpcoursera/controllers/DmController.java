package com.acpcoursera.controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;

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
import com.acpcoursera.model.GcmMessage;
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

    	/* test GCM */
    	GcmMessage message = new GcmMessage();
    	message.addRecipient("eZuBU0zk_g8:APA91bFtpw2VOtJjzvj"
    			+ "Q5y21eWpIiBR5uX1O_4u1wIDEMPWERA1pHH5cAqavf"
    			+ "MgcMUJPChbXrbrQveSICp6gHihu6LpYvg-9YdBqkTk"
    			+ "S_ZLsBSnCwf-ztYHxa9kT320rzG5p4MBA5IDl");
    	message.addDataField("follower_name", "Will");
		try {
			int responseCode = sendGcmMessage(message);
			System.out.println("response code: " + responseCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private int sendGcmMessage(GcmMessage message) throws IOException {

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

    	System.out.println("Message sent: " + mapper.writeValueAsString(message));

    	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;

		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		System.out.println("GCM response: " + response.toString());

		int responseCode = connection.getResponseCode();
    	return responseCode;
    }

}
