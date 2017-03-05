package com.tomwt.mobile.termtracker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


public class TermTrackerProvider extends ContentProvider {

    private static final String AUTHORITY = "com.tomwt.mobile.termtracker.termtrackerprovider";
    private static final String BASE_PATH = "tbl_notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int NOTES = 1; // give me the entire dataSet
    private static final int NOTES_ID = 2; // give me a single record

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", NOTES_ID);
    }

    private SQLiteDatabase termTrackerDB;

    @Override
    public boolean onCreate() {
        DBOpenHelper dbHelper = new DBOpenHelper(getContext());
        termTrackerDB = dbHelper.getWritableDatabase();
        // dbHelper.onUpgrade(termTrackerDB, 1, 1); // nuke old version of database and recreate with new DB schema
        return true;
    }

    public void onCreate(Context context) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        termTrackerDB = dbOpenHelper.getWritableDatabase();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if(uriMatcher.match(uri) == NOTES_ID) {
            selection = DBOpenHelper.NOTES_ID + "=" + uri.getLastPathSegment();
        }

        return termTrackerDB.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.NOTES_ALL_COLUMNS, selection, null, null, null, DBOpenHelper.NOTES_TIMESTAMP + " DESC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = termTrackerDB.insert(DBOpenHelper.TABLE_NOTES, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return termTrackerDB.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return termTrackerDB.update(DBOpenHelper.TABLE_NOTES, values, selection, selectionArgs);
    }

    public Cursor getCurrentTermProgress(Context context) {
        String sql = "select " + DBOpenHelper.NOTES_ID + " from " + DBOpenHelper.TABLE_NOTES;
        String[] selectionArgs = {};

        return rawQuery(context, sql, selectionArgs);
    }

    private Cursor rawQuery(Context context, String sql, String[] selectionArgs) {
        if(termTrackerDB == null)
            onCreate(context);

        return termTrackerDB.rawQuery(sql, selectionArgs);
    }
}
