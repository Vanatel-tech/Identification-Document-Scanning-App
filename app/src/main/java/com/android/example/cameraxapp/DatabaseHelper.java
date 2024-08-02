package com.android.example.cameraxapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_data.db";
    private static final int DATABASE_VERSION = 4; // Increment version

    public static final String TABLE_USER_SIGNUP = "UserSignup";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ID_NUMBER = "id_number";
    public static final String COLUMN_DOCUMENT = "document";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PURPOSE_OF_VISIT = "purpose_of_visit";
    public static final String COLUMN_SIGNUP_TIME = "signup_time";
    public static final String COLUMN_SIGNOUT_TIME = "signout_time";
    public static final String COLUMN_FLOOR = "floor"; // New column
    public static final String COLUMN_ADDITIONAL_DETAILS = "additional_details"; // New column

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_USER_SIGNUP + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ID_NUMBER + " TEXT, " +
                COLUMN_DOCUMENT + " TEXT, " +
                COLUMN_COUNTRY + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_PURPOSE_OF_VISIT + " TEXT, " +
                COLUMN_SIGNUP_TIME + " TEXT, " +
                COLUMN_SIGNOUT_TIME + " TEXT, " +
                COLUMN_FLOOR + " TEXT, " + // New column
                COLUMN_ADDITIONAL_DETAILS + " TEXT)"; // New column
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SIGNUP);
        onCreate(db);
    }

    public Cursor getVisitorsNotSignedOut() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER_SIGNUP + " WHERE " + COLUMN_SIGNOUT_TIME + " IS NULL", null);
    }

    public boolean signOutVisitor(String idNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SIGNOUT_TIME, getCurrentTimestamp());
        return db.update(TABLE_USER_SIGNUP, values, COLUMN_ID_NUMBER + " = ?", new String[]{idNumber}) > 0;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
