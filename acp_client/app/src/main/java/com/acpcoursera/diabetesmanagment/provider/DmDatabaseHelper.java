package com.acpcoursera.diabetesmanagment.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.File;

import static com.acpcoursera.diabetesmanagment.provider.DmContract.*;

public class DmDatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = DmDatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "acp.db";

    interface Tables {
        String FOLLOWERS = "followers";
        String FOLLOWING = "following";
    }

    private static final String SQL_CREATE_FOLLOWERS
            = "CREATE TABLE " + Tables.FOLLOWERS + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FollowersColumns.USERNAME + " TEXT    NOT NULL, "
            + FollowersColumns.FOLLOWER_NAME + " TEXT    NOT NULL, "
            + FollowersColumns.ACCEPTED + " INTEGER NOT NULL, "
            + FollowersColumns.MAJOR_DATA + " INTEGER NOT_NULL, "
            + FollowersColumns.MINOR_DATE + " INTEGER NOT_NULL"
            + " )";

    private static final String SQL_CREATE_FOLLOWING
            = "CREATE TABLE " + Tables.FOLLOWING + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FollowingColumns.USERNAME + " TEXT    NOT NULL, "
            + FollowingColumns.FOLLOWING_NAME + " TEXT    NOT NULL, "
            + FollowingColumns.PENDING + " INTEGER NOT NULL, "
            + FollowingColumns.MAJOR_DATA + " INTEGER NOT_NULL, "
            + FollowingColumns.MINOR_DATE + " INTEGER NOT_NULL"
            + " )";

    public DmDatabaseHelper(Context context) {
        // TODO consider something different than cachedir
        super(context, context.getCacheDir() + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FOLLOWERS);
        db.execSQL(SQL_CREATE_FOLLOWING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.FOLLOWERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.FOLLOWING);
    }
}
