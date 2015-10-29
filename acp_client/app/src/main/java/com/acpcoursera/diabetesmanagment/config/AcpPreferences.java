package com.acpcoursera.diabetesmanagment.config;

public class AcpPreferences {

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static final String CLIENT_KEYSTORE_PASS = "acppass";

    public static final String SERVER_SCHEME = "https";
    public static final String SERVER_ADDRESS = "192.168.0.101";
    public static final int SERVER_PORT = 8443;

    // TODO: obfuscate maybe?
    public static final String SERVER_CLIENT_ID = "mobile";
    public static final String SERVER_CLIENT_SECRET = "12345";

    public static final String SHARED_PREF_FILE =
            "com.acpcoursera.diabetesmanagement.PREFERENCE_FILE_KEY";
    public static final String ACCESS_TOKEN_PREF = "access_token_pref";

    public static final int NET_DELAY_SEC = 3;

}
