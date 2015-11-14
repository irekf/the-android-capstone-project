package com.acpcoursera.diabetesmanagment.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.File;

import static com.acpcoursera.diabetesmanagment.provider.DmContract.*;

public class DmDatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = DmDatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 7;

    private static final String DATABASE_NAME = "acp.db";

    interface Tables {
        String FOLLOWERS = "followers";
        String FOLLOWING = "following";
        String CHECK_IN_DATA = "check_in_data";
        String REMINDERS = "reminders";
    }

    private static final String SQL_CREATE_FOLLOWERS
            = "CREATE TABLE " + Tables.FOLLOWERS + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FollowersColumns.USERNAME + " TEXT NOT NULL, "
            + FollowersColumns.FOLLOWER_NAME + " TEXT NOT NULL, "
            + FollowersColumns.FOLLOWER_FULL_NAME + " TEXT NOT NULL, "
            + FollowersColumns.IS_TEEN + " INTEGER NOT NULL, "
            + FollowersColumns.ACCEPTED + " INTEGER NOT NULL, "
            + FollowersColumns.PENDING + " INTEGER NOT NULL, "
            + FollowersColumns.MAJOR_DATA + " INTEGER NOT_NULL, "
            + FollowersColumns.MINOR_DATE + " INTEGER NOT_NULL"
            + " )";

    private static final String SQL_CREATE_FOLLOWING
            = "CREATE TABLE " + Tables.FOLLOWING + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FollowingColumns.USERNAME + " TEXT NOT NULL, "
            + FollowingColumns.FOLLOWING_NAME + " TEXT NOT NULL, "
            + FollowingColumns.FOLLOWING_FULL_NAME + " TEXT NOT NULL, "
            + FollowingColumns.PENDING + " INTEGER NOT NULL, "
            + FollowingColumns.IS_INVITE + " INTEGER NOT NULL, "
            + FollowingColumns.MAJOR_DATA + " INTEGER NOT_NULL, "
            + FollowingColumns.MINOR_DATE + " INTEGER NOT_NULL"
            + " )";

    private static final String SQL_CREATE_CHECK_IN_DATA
            = "CREATE TABLE " + Tables.CHECK_IN_DATA + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CheckInDataColumns.USERNAME + " TEXT NOT NULL, "
            + CheckInDataColumns.SUGAR_LEVEL + " REAL NOT NULL, "
            + CheckInDataColumns.SUGAR_LEVEL_TIME + " TEXT NOT NULL, "
            + CheckInDataColumns.MEAL + " TEXT NOT NULL, "
            + CheckInDataColumns.MEAL_TIME + " TEXT NOT NULL, "
            + CheckInDataColumns.INSULIN_DOSAGE + " REAL NOT NULL, "
            + CheckInDataColumns.INSULIN_TIME + " TEXT NOT NULL, "
            + CheckInDataColumns.MOOD_LEVEL + " INTEGER NOT_NULL, "
            + CheckInDataColumns.STRESS_LEVEL + " INTEGER NOT_NULL, "
            + CheckInDataColumns.ENERGY_LEVEL + " INTEGER NOT_NULL, "
            + CheckInDataColumns.CHECK_IN_TIME + " TEXT NOT_NULL"
            + " )";

    private static final String SQL_CREATE_REMINDERS
            = "CREATE TABLE " + Tables.REMINDERS + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ReminderColumns.HOUR_OF_DAY + " INTEGER NOT NULL, "
            + ReminderColumns.MINUTE + " INTEGER NOT NULL, "
            + ReminderColumns.IS_ENABLED + " INTEGER NOT NULL"
            + " )";

    public DmDatabaseHelper(Context context) {
        // TODO consider something different than cachedir
        super(context, context.getCacheDir() + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FOLLOWERS);
        db.execSQL(SQL_CREATE_FOLLOWING);
        db.execSQL(SQL_CREATE_CHECK_IN_DATA);
        db.execSQL(SQL_CREATE_REMINDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.FOLLOWERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.FOLLOWING);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CHECK_IN_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.REMINDERS);
        onCreate(db);
    }

    public void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, db.getVersion(), db.getVersion());
    }
}
