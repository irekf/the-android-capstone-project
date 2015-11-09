package com.acpcoursera.diabetesmanagment.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class DmContract {

    private static String TAG = DmContract.class.getSimpleName();

    public static final String CONTENT_TYPE_APP_BASE
            = "acpcoursera.diabetesmanagment.provider.dmprovider.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;

    interface FollowersColumns {
        String USERNAME = "username";
        String FOLLOWER_NAME = "follower_name";
        String ACCEPTED = "accepted";
        String MAJOR_DATA = "major_data";
        String MINOR_DATE = "minor_data";
    }

    interface FollowingColumns {
        String USERNAME = "username";
        String FOLLOWING_NAME = "following_name";
        String PENDING = "pending";
        String MAJOR_DATA = "major_data";
        String MINOR_DATE = "minor_data";
    }

    interface CheckInDataColumns {
        String USERNAME = "username";
        String SUGAR_LEVEL = "sugar_level";
        String SUGAR_LEVEL_TIME = "sugar_level_time";
        String MEAL = "meal";
        String MEAL_TIME = "meal_time";
        String INSULIN_DOSAGE = "insulin_dosage";
        String INSULIN_ADMINISTRATION_TIME = "insulin_administration_time";
        String MOOD_LEVEL = "mood_level";
        String STRESS_LEVEL = "stress_level";
        String ENERGY_LEVEL = "energy_level";
        String CHECK_IN_TIME = "check_in_time";
    }

    public static final String CONTENT_AUTHORITY
            = "com.acpcoursera.diabetesmanagment.provider.dmprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_FOLLOWERS = "followers";
    private static final String PATH_FOLLOWING = "following";
    private static final String PATH_CHECK_IN_DATA = "check_in_data";

    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static class Followers implements FollowersColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOLLOWERS).build();

        public static final String CONTENT_TYPE_ID = "follower";

        public static Uri buildFollowersUri() {
            return CONTENT_URI;
        }

        public static Uri buildFollowerUri(String tagId) {
            return CONTENT_URI.buildUpon().appendPath(tagId).build();
        }

    }

    public static class Following implements FollowingColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FOLLOWING).build();

        public static final String CONTENT_TYPE_ID = "following";

        public static Uri buildFollowingsUri() {
            return CONTENT_URI;
        }

        public static Uri buildFollowingsUri(String tagId) {
            return CONTENT_URI.buildUpon().appendPath(tagId).build();
        }

    }

    public static class CheckInData implements CheckInDataColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHECK_IN_DATA).build();

        public static final String CONTENT_TYPE_ID = "check_in_data";

        public static Uri buildCheckInDataUri() {
            return CONTENT_URI;
        }

        public static Uri buildCheckInDatumUri(String tagId) {
            return CONTENT_URI.buildUpon().appendPath(tagId).build();
        }

    }

}
