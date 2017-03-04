package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertNote("New Note");





        Cursor cursor = getContentResolver().query(TermTrackerProvider.CONTENT_URI, DBOpenHelper.NOTES_ALL_COLUMNS, null, null, null, null);
        String[] from = {DBOpenHelper.NOTES_DETAILS};
        int[] to = {android.R.id.text1};
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);
    }

    // called when the user clicks the Manage Terms button in the UI
    public void openManageTerms(View view) {
        Intent intent = new Intent(this, ManagedTermsActivity.class);
        startActivity(intent);
    }

    public void openManageCourses(View view) {
        Intent intent = new Intent(this, ManageCoursesActivity.class);
        startActivity(intent);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
        Uri notesURI = getContentResolver().insert(TermTrackerProvider.CONTENT_URI, values);
        Log.d("MainActivity", "Inserted a note " + notesURI.getLastPathSegment());
    }
}
