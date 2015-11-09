package com.acpcoursera.diabetesmanagment.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class DmProvider extends ContentProvider {

    private static String TAG = DmProvider.class.getSimpleName();

    private DmDatabaseHelper mOpenHelper;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public DmProvider() {
    }

    public final static int FOLLOWER_VALUES_ITEM = 100;
    public final static int FOLLOWER_VALUES_ITEMS = 110;
    public final static int FOLLOWING_VALUES_ITEM = 200;
    public final static int FOLLOWING_VALUES_ITEMS = 210;
    public final static int CHECK_IN_DATA_VALUES_ITEM = 300;
    public final static int CHECK_IN_DATA_VALUES_ITEMS = 310;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher =
                new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DmContract.CONTENT_AUTHORITY,
                DmDatabaseHelper.Tables.FOLLOWERS,
                FOLLOWER_VALUES_ITEMS);
        matcher.addURI(DmContract.CONTENT_AUTHORITY,
                DmDatabaseHelper.Tables.FOLLOWERS + "/#",
                FOLLOWER_VALUES_ITEM);

        matcher.addURI(DmContract.CONTENT_AUTHORITY,
                DmDatabaseHelper.Tables.FOLLOWING,
                FOLLOWING_VALUES_ITEMS);
        matcher.addURI(DmContract.CONTENT_AUTHORITY,
                DmDatabaseHelper.Tables.FOLLOWING + "/#",
                FOLLOWING_VALUES_ITEM);

        matcher.addURI(DmContract.CONTENT_AUTHORITY,
                DmDatabaseHelper.Tables.CHECK_IN_DATA,
                CHECK_IN_DATA_VALUES_ITEMS);
        matcher.addURI(DmContract.CONTENT_AUTHORITY,
                DmDatabaseHelper.Tables.CHECK_IN_DATA + "/#",
                CHECK_IN_DATA_VALUES_ITEM);

        return matcher;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted = 0;

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FOLLOWER_VALUES_ITEMS:
                db.delete(DmDatabaseHelper.Tables.FOLLOWERS, selection, selectionArgs);
                break;
            case FOLLOWER_VALUES_ITEM:
                db.delete(DmDatabaseHelper.Tables.FOLLOWERS,
                        addKeyIdCheckToSelection(selection, ContentUris.parseId(uri)),
                        selectionArgs);
                break;
            case FOLLOWING_VALUES_ITEMS:
                db.delete(DmDatabaseHelper.Tables.FOLLOWING, selection, selectionArgs);
                break;
            case FOLLOWING_VALUES_ITEM:
                db.delete(DmDatabaseHelper.Tables.FOLLOWING,
                        addKeyIdCheckToSelection(selection, ContentUris.parseId(uri)),
                        selectionArgs);
                break;
            case CHECK_IN_DATA_VALUES_ITEMS:
                db.delete(DmDatabaseHelper.Tables.CHECK_IN_DATA, selection, selectionArgs);
                break;
            case CHECK_IN_DATA_VALUES_ITEM:
                db.delete(DmDatabaseHelper.Tables.CHECK_IN_DATA,
                        addKeyIdCheckToSelection(selection, ContentUris.parseId(uri)),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case FOLLOWER_VALUES_ITEMS:
                return DmContract.makeContentItemType(DmContract.Followers.CONTENT_TYPE_ID);
            case FOLLOWER_VALUES_ITEM:
                return DmContract.makeContentType(DmContract.Followers.CONTENT_TYPE_ID);
            case FOLLOWING_VALUES_ITEMS:
                return DmContract.makeContentItemType(DmContract.Following.CONTENT_TYPE_ID);
            case FOLLOWING_VALUES_ITEM:
                return DmContract.makeContentType(DmContract.Following.CONTENT_TYPE_ID);
            case CHECK_IN_DATA_VALUES_ITEMS:
                return DmContract.makeContentItemType(DmContract.CheckInData.CONTENT_TYPE_ID);
            case CHECK_IN_DATA_VALUES_ITEM:
                return DmContract.makeContentType(DmContract.CheckInData.CONTENT_TYPE_ID);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        String table;
        Uri result;

        switch (sUriMatcher.match(uri)) {
            case FOLLOWER_VALUES_ITEMS:
                table = DmDatabaseHelper.Tables.FOLLOWERS;
                result = DmContract.Followers.buildFollowersUri();
                break;
            case FOLLOWING_VALUES_ITEMS:
                table = DmDatabaseHelper.Tables.FOLLOWING;
                result = DmContract.Following.buildFollowingsUri();
                break;
            case CHECK_IN_DATA_VALUES_ITEMS:
                table = DmDatabaseHelper.Tables.CHECK_IN_DATA;
                result = DmContract.CheckInData.buildCheckInDataUri();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        final long rowsInserted = mOpenHelper.getWritableDatabase().insert(table, null, values);

        if (rowsInserted > 0) {
            Uri newUri = ContentUris.withAppendedId(result, rowsInserted);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        } else {
            // TODO find out why it has to be handled here and not in the example
//            throw new SQLException("Fail to add a new record into " + uri);
            return null;
        }

    }

    @Override
    public int bulkInsert(@NonNull Uri uri, ContentValues[] values) {

        String table;
        int rowsInserted = 0;

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FOLLOWER_VALUES_ITEMS:
                table = DmDatabaseHelper.Tables.FOLLOWERS;
                break;
            case FOLLOWING_VALUES_ITEMS:
                table = DmDatabaseHelper.Tables.FOLLOWING;
                break;
            case CHECK_IN_DATA_VALUES_ITEMS:
                table = DmDatabaseHelper.Tables.CHECK_IN_DATA;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                final long id = db.insert(table, null, value);
                if (id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsInserted;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DmDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case FOLLOWER_VALUES_ITEMS:
                queryBuilder.setTables(DmDatabaseHelper.Tables.FOLLOWERS);
                break;
            case FOLLOWER_VALUES_ITEM:
                queryBuilder.setTables(DmDatabaseHelper.Tables.FOLLOWERS);
                selection = addKeyIdCheckToSelection(selection, ContentUris.parseId(uri));
                break;
            case FOLLOWING_VALUES_ITEMS:
                queryBuilder.setTables(DmDatabaseHelper.Tables.FOLLOWING);
                break;
            case FOLLOWING_VALUES_ITEM:
                queryBuilder.setTables(DmDatabaseHelper.Tables.FOLLOWING);
                selection = addKeyIdCheckToSelection(selection, ContentUris.parseId(uri));
                break;
            case CHECK_IN_DATA_VALUES_ITEMS:
                queryBuilder.setTables(DmDatabaseHelper.Tables.CHECK_IN_DATA);
                break;
            case CHECK_IN_DATA_VALUES_ITEM:
                queryBuilder.setTables(DmDatabaseHelper.Tables.CHECK_IN_DATA);
                selection = addKeyIdCheckToSelection(selection, ContentUris.parseId(uri));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        final Cursor cursor = queryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int rowsUpdated = 0;

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case FOLLOWER_VALUES_ITEMS:
                rowsUpdated = db.update(DmDatabaseHelper.Tables.FOLLOWERS, values,
                        selection, selectionArgs);
                break;
            case FOLLOWER_VALUES_ITEM:
                rowsUpdated = db.update(DmDatabaseHelper.Tables.FOLLOWERS, values,
                        addKeyIdCheckToSelection(selection, ContentUris.parseId(uri)),
                        selectionArgs);
                break;
            case FOLLOWING_VALUES_ITEMS:
                rowsUpdated = db.update(DmDatabaseHelper.Tables.FOLLOWING, values,
                        selection, selectionArgs);
                break;
            case FOLLOWING_VALUES_ITEM:
                rowsUpdated = db.update(DmDatabaseHelper.Tables.FOLLOWING, values,
                        addKeyIdCheckToSelection(selection, ContentUris.parseId(uri)),
                        selectionArgs);
                break;
            case CHECK_IN_DATA_VALUES_ITEMS:
                rowsUpdated = db.update(DmDatabaseHelper.Tables.CHECK_IN_DATA, values,
                        selection, selectionArgs);
                break;
            case CHECK_IN_DATA_VALUES_ITEM:
                rowsUpdated = db.update(DmDatabaseHelper.Tables.CHECK_IN_DATA, values,
                        addKeyIdCheckToSelection(selection, ContentUris.parseId(uri)),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }


    private static String addKeyIdCheckToSelection(String selection, long id) {
        String newSelection;
        if (TextUtils.isEmpty(selection))
            newSelection = "";
        else
            newSelection = selection + " AND ";

        return newSelection + " _id = " + "'" + id + "'";
    }
}
