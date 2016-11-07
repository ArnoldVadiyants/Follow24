package com.newstee.helper;

/**
 * Created by Arnold on 03.03.2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SOCIAL_NETWORK_KEY = "sn_key";
    public static final String KEY_SOCIAL_NETWORK_ID = "sn_id";
  public static final String KEY_FB_ID = "fb_id";
    public static final String KEY_GG_ID = "gg_id";
    public static final String KEY_VK_ID = "vk_id";
    public static final String KEY_TW_ID = "tw_id";



    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT,"+ KEY_PASSWORD + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE, "+  KEY_SOCIAL_NETWORK_KEY+ " TEXT UNIQUE, "+ KEY_SOCIAL_NETWORK_ID + " TEXT UNIQUE"+")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String id, String name, String email, String password, String snKey,String snId) {
        deleteUsers();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // Id
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PASSWORD, password); // Password
        values.put(KEY_SOCIAL_NETWORK_KEY, snKey);
        values.put(KEY_SOCIAL_NETWORK_ID, snId); //
        // Inserting Row
        long ids = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put(KEY_ID, cursor.getString(0));
            user.put(KEY_NAME, cursor.getString(1));
            user.put(KEY_EMAIL, cursor.getString(3));
            user.put(KEY_PASSWORD, cursor.getString(2));
            user.put(KEY_SOCIAL_NETWORK_KEY,cursor.getString(4));
            user.put(KEY_SOCIAL_NETWORK_ID,cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public boolean updateUser(String id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

      /*  Cursor c = db.query(TABLE_USER, null, null, null, null, null, null);
        long rowId = -1;
        if (c.moveToFirst()) {
            rowId = c.getLong(0);
        }*/
        String where = KEY_ID + "=" + id;
            ContentValues args = new ContentValues();
            if (name != null) {
                args.put(KEY_NAME, name);
            }
            if (email != null) {
                args.put(KEY_EMAIL, email);
            }

        return db.update(TABLE_USER, args,where, null) > 0;
    }
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
