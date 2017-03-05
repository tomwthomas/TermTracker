package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // insertNote("New Note");

        // build out the progress bar area of the GUI
        {
            Log.d("MainActivity", "about to update text1");

            final TextView textViewChange = (TextView) findViewById(R.id.txt_progressIndicator);
            TermTrackerProvider TTP = new TermTrackerProvider();
            Cursor cursor = TTP.getCurrentTermProgress(MainActivity.this);
            cursor.moveToFirst();
            textViewChange.setText(cursor.getString(0) + " of 4 Terms Completed");
            cursor.close();

            Log.d("MainActivity", "back from updated text1?");
        }

        // build out the list of upcoming milestones and display them in the GUI
        {
            Cursor cursor = getContentResolver().query(TermTrackerProvider.CONTENT_URI, DBOpenHelper.NOTES_ALL_COLUMNS, null, null, null, null);
            String[] from = {DBOpenHelper.NOTES_DETAILS};
            int[] to = {android.R.id.text1};
            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);

            ListView list = (ListView) findViewById(android.R.id.list);
            list.setAdapter(cursorAdapter);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
        else {

        }
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
