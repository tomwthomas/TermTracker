package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ViewCourseActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 1001;

    private String action;
    private EditText editor;
    private String notesFilter;
    private String oldText;

    // REFACTORED::  ADDED
    private String coursesFilter;
    private EditText titleEditor;
    private EditText detailsEditor;
    private EditText startEditor;
    private EditText endEditor;
    private EditText statusEditor;
    private EditText mentorEditor;
    private String titleTextOld;
    private String detailsTextOld;
    private String startTextOld;
    private String endTextOld;
    private String statusTextOld;
    private String mentorTextOld;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Course Information");

        editor = (EditText) findViewById(R.id.editText);
        // REFACTORED:: ADDED
        titleEditor = (EditText) findViewById(R.id.data_title);
        detailsEditor = (EditText) findViewById(R.id.data_details);
        startEditor = (EditText) findViewById(R.id.data_startDate);
        endEditor = (EditText) findViewById(R.id.data_endDate);
        statusEditor = (EditText) findViewById(R.id.data_status);
        mentorEditor = (EditText) findViewById(R.id.data_mentor);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            getSupportActionBar().setTitle("INTENT.INSERT (uri==null)...");
        } else {
            action = Intent.ACTION_EDIT;
//            notesFilter = DBOpenHelper.NOTES_ID + "=" + uri.getLastPathSegment();
//
//            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.NOTES_ALL_COLUMNS, notesFilter, null, null);
//            cursor.moveToFirst();
//            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTES_DETAILS));
//            editor.setText(oldText);
//            editor.requestFocus();

            // REFACTORED:: ADDED
            Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
            coursesFilter = DBOpenHelper.COURSES_ID + "=" + uri.getLastPathSegment();
            Cursor courseCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, coursesFilter, null, null);
            courseCursor.moveToFirst();
            titleTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_TITLE));
            detailsTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_DETAILS));
            startTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_START));
            endTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_END));
            statusTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_STATUS));
            mentorTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_MENTOR));
            titleEditor.setText(titleTextOld);
            detailsEditor.setText(detailsTextOld);
            startEditor.setText(startTextOld);
            endEditor.setText(endTextOld);
            statusEditor.setText(statusTextOld);
            mentorEditor.setText(mentorTextOld);

//            // build out the list of courses for this term and display them in the GUI
//            final Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
//            Cursor courseCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, null, null, null);
//            String[] from = {DBOpenHelper.COURSES_TITLE};
//            int[] to = {android.R.id.text1};
//            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, courseCursor, from, to, 0);
//
//            ListView list = (ListView) findViewById(android.R.id.list);
//            list.setAdapter(cursorAdapter);
//
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(ViewTermActivity.this, ViewCourseActivity.class);
//                    Uri uri = Uri.parse(courseURI + "/" + id);
//                    Log.d("ViewTermActivity", "courseURI: " + uri.toString());
//                    intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
//                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
//                }
//            });
        }
    }


    private void finishEditing() {
//        String newText = editor.getText().toString().trim();
//
        // REFACTORED:: ADDED
        String titleTextNew = titleEditor.getText().toString().trim();
        String detailsTextNew = detailsEditor.getText().toString().trim();
        String startTextNew = startEditor.getText().toString().trim();
        String endTextNew = endEditor.getText().toString().trim();
        String statusTextNew = statusEditor.getText().toString().trim();
        String mentorTextNew = mentorEditor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (titleTextNew.length() == 0 || detailsTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0 || statusTextNew.length() == 0 || mentorTextNew.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertCourse(titleTextNew, detailsTextNew, startTextNew, endTextNew, statusTextNew, mentorTextNew);
                }
                break;
            case Intent.ACTION_EDIT:
                if (titleTextNew.length() == 0 || detailsTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0 || statusTextNew.length() == 0 || mentorTextNew.length() == 0) { // REFACTORED
//                    deleteNote();
                } else if (titleTextOld.equals(titleTextNew) && detailsTextOld.equals(detailsTextNew) && startTextOld.equals(startTextNew) && endTextOld.equals(endTextNew) && statusTextOld.equals(statusTextNew) && mentorTextOld.equals(mentorTextNew)) { // REFACTORED
                    setResult(RESULT_CANCELED);
                } else {
//                    updateNote(newText);
                    // REFACTORED:: ADDED
                    updateCourse(titleTextNew, detailsTextNew, startTextNew, endTextNew, statusTextNew, mentorTextNew);
                }

        }

        Intent intent = new Intent();
        intent.putExtra("returnValue", "9999");
        setResult(RESULT_OK, intent);
        finish();
    }

    // REFACTORED:: ADDED
    private void updateCourse(String titleText, String detailsText, String startText, String endText, String statusText, String mentorText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSES_TITLE, titleText);
        values.put(DBOpenHelper.COURSES_DETAILS, detailsText);
        values.put(DBOpenHelper.COURSES_START, startText);
        values.put(DBOpenHelper.COURSES_END, endText);
        values.put(DBOpenHelper.COURSES_STATUS, statusText);
        values.put(DBOpenHelper.COURSES_MENTOR, mentorText);
        Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
        getContentResolver().update(courseURI, values, coursesFilter, null);
        Toast.makeText(this, "COURSE UPDATED...", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
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
        Log.d("ViewCourseActivity", "courseURI: " + courseURI.toString());
        Log.d("ViewCourseActivity", "Inserted a course " + courseURI.getLastPathSegment());
    }

    // all three of the below methods are required to ensure full capture of the user leaving this activity
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                // NavUtils.navigateUpFromSameTask(this);
                Intent intent = new Intent();
                intent.putExtra("returnValue", "9999");
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

}