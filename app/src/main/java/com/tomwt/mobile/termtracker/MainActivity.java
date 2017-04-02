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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 1001;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        insertTerm("Term 1", "3/11/2017", "6/11/2017");
//
//        insertCourse("Course 1", "Course details...", "3/15/2017", "4/1/2017", "Pending", "Bob Smith");
//
//        insertNote("Note 1");



        // build out the progress bar area of the GUI
//        {
//            Log.d("MainActivity", "about to update text1");
//
//            final TextView textViewChange = (TextView) findViewById(R.id.txt_progressIndicator);
//            TermTrackerProvider TTP = new TermTrackerProvider();
//            Cursor cursor = TTP.getCurrentTermProgress(MainActivity.this);
//            cursor.moveToFirst();
//            textViewChange.setText(cursor.getString(0) + " of 4 Terms Completed");
//            cursor.close();
//
//            Log.d("MainActivity", "back from updated text1?");
//        }

            final TextView graduationProgress = (TextView) findViewById(R.id.txt_progressIndicator);
            TermTrackerProvider TTP = new TermTrackerProvider();
            Cursor cursor = TTP.getCountClosedTerms(MainActivity.this);
            cursor.moveToFirst();
            String closedTerms = cursor.getString(0);
            cursor.close();
            cursor = TTP.getCountTotalTerms(MainActivity.this);
            cursor.moveToFirst();
            String totalTerms = cursor.getString(0);
            cursor.getString(0);
            graduationProgress.setText(closedTerms + " of " + totalTerms + " Terms Completed");


//        // build out the list of upcoming milestones and display them in the GUI
//        {
//
////            public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
////            return termTrackerDB.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.NOTES_ALL_COLUMNS, selection, null, null, null, DBOpenHelper.NOTES_TIMESTAMP + " DESC");
////        }
//
////             Cursor cursor = getContentResolver().query(TermTrackerProvider.CONTENT_URI, DBOpenHelper.NOTES_ALL_COLUMNS, null, null, null, null);
////            Cursor cursor = getContentResolver().query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.NOTES_ALL_COLUMNS, null, null, null);
//
//            TermTrackerProvider TTP = new TermTrackerProvider();
//            Cursor cursor = TTP.getUpcomingMilestones(MainActivity.this);
//            String[] from = {DBOpenHelper.COURSES_TITLE};
//            int[] to = {android.R.id.text1};
//            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
//
//            ListView list = (ListView) findViewById(android.R.id.list);
//            list.setAdapter(cursorAdapter);
//        }


        // build out the list of upcoming milestones and display them in the GUI
        final Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
//        String alertsFilter = DBOpenHelper.ALERTS_PID + "=" + currentAssessmentID;
        String coursesFilter = DBOpenHelper.COURSES_START + " <= date('now') and " + DBOpenHelper.COURSES_END + " >= date('now')";
        //String alertsFilter = "date(" + DBOpenHelper.COURSES_START + ") <= date('now')";
        Cursor alertCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, coursesFilter, null, null);
        String[] from = {DBOpenHelper.COURSES_TITLE};
//        String[] from = {DBOpenHelper.COURSES_START};
        int[] to = {android.R.id.text1};
        android.widget.CursorAdapter cursorAdapter = new android.widget.SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, alertCursor, from, to, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewCourseActivity.class);
                Uri uri = Uri.parse(courseURI + "/" + id);
                Log.d("MainActivity", "courseURI: " + uri.toString());
                intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                //intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentTermID);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDITOR_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                // build out the list of upcoming milestones and display them in the GUI
                final Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
                //        String alertsFilter = DBOpenHelper.ALERTS_PID + "=" + currentAssessmentID;
                String coursesFilter = DBOpenHelper.COURSES_START + " <= date('now') and " + DBOpenHelper.COURSES_END + " >= date('now')";
                //String alertsFilter = "date(" + DBOpenHelper.COURSES_START + ") <= date('now')";
                Cursor alertCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, coursesFilter, null, null);
                String[] from = {DBOpenHelper.COURSES_TITLE};
                //        String[] from = {DBOpenHelper.COURSES_START};
                int[] to = {android.R.id.text1};
                android.widget.CursorAdapter cursorAdapter = new android.widget.SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, alertCursor, from, to, 0);

                ListView list = (ListView) findViewById(android.R.id.list);
                list.setAdapter(cursorAdapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, ViewCourseActivity.class);
                        Uri uri = Uri.parse(courseURI + "/" + id);
                        Log.d("MainActivity", "courseURI: " + uri.toString());
                        intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                        //intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentTermID);
                        startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    }
                });

            }
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
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    public void openManageCourses(View view) {
         Intent intent = new Intent(this, ManageCoursesActivity.class);
//        Intent intent = new Intent(this, ViewNoteActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    private void insertCourse(String title, String details, String startDate, String endDate, String status, String mentor) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSES_TITLE, title);
        values.put(DBOpenHelper.COURSES_DETAILS, details);
        values.put(DBOpenHelper.COURSES_START, startDate);
        values.put(DBOpenHelper.COURSES_END, endDate);
        values.put(DBOpenHelper.COURSES_STATUS, status);
        values.put(DBOpenHelper.COURSES_MENTOR, mentor);
        Uri courseURI = getContentResolver().insert(Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES), values);
        Log.d("MainActivity", "courseURI: " + courseURI.toString());
        Log.d("MainActivity", "Inserted a course " + courseURI.getLastPathSegment());
    }

    private void insertTerm(String title, String startDate, String endDate) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERMS_TITLE, title);
        values.put(DBOpenHelper.TERMS_START, startDate);
        values.put(DBOpenHelper.TERMS_END, endDate);
        Uri termURI = getContentResolver().insert(Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_TERMS), values);
        Log.d("MainActivity", "termURI: " + termURI.toString());
        Log.d("MainActivity", "Inserted a term " + termURI.getLastPathSegment());
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
        Uri notesURI = getContentResolver().insert(TermTrackerProvider.CONTENT_URI, values);
        Log.d("MainActivity", "Inserted a note " + notesURI.getLastPathSegment());
    }

}
