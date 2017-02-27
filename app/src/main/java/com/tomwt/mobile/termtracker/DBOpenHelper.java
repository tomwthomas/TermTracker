package com.tomwt.mobile.termtracker;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    // Constants for db name and version
    private static final String DB_NAME = "termTracker.db";
    private static final int DB_VERSION = 1;

    // Constants to make working with DB easier throughout the application broken down by table
    // NOTES TABLE
    public static final String TABLE_NOTES = "tbl_notes";
    public static final String NOTES_ID = "id";
    public static final String NOTES_PID = "parentID";
    public static final String NOTES_TYPE = "parentType";
    public static final String NOTES_DETAILS = "details";
    public static final String NOTES_IMG = "imgPath";
    public static final String NOTES_TIMESTAMP = "timestamp";

    //SQL to create notes table
    private static final String CREATE_TABLE_NOTES =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTES_PID + " INTEGER, " +
                    NOTES_TYPE + " INTEGER, " +
                    NOTES_DETAILS + " TEXT, " +
                    NOTES_IMG + " TEXT, " +
                    NOTES_TIMESTAMP + " TEXT default CURRENT_TIMESTAMP" +
                    ")";


    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
}
