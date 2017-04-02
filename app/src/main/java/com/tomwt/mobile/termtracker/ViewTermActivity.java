package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

public class ViewTermActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 1001;

    private String action;
    private EditText editor;
    private String notesFilter;
    private String oldText;

    // REFACTORED::  ADDED
    private String termsFilter;
    private EditText titleEditor;
    private EditText startEditor;
    private EditText endEditor;
    private String titleTextOld;
    private String startTextOld;
    private String endTextOld;

    private int currentTermID;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(ViewTermActivity.this, ViewCourseActivity.class);
                intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentTermID);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Term Information");

        editor = (EditText) findViewById(R.id.editText);
        // REFACTORED:: ADDED
        titleEditor = (EditText) findViewById(R.id.data_title);
        startEditor = (EditText) findViewById(R.id.data_startDate);
        endEditor = (EditText) findViewById(R.id.data_endDate);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            getSupportActionBar().setTitle("INTENT.INSERT (uri==null)...");
            fab.hide();
        } else {
            action = Intent.ACTION_EDIT;
            notesFilter = DBOpenHelper.NOTES_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.NOTES_ALL_COLUMNS, notesFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTES_DETAILS));
            editor.setText(oldText);
//            editor.requestFocus();

            // REFACTORED:: ADDED
            Uri termURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_TERMS);
            currentTermID = Integer.parseInt(uri.getLastPathSegment());
            termsFilter = DBOpenHelper.TERMS_ID + "=" + currentTermID;
            Cursor termCursor = getContentResolver().query(termURI, DBOpenHelper.TERMS_ALL_COLUMNS, termsFilter, null, null);
            termCursor.moveToFirst();
            titleTextOld = termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.TERMS_TITLE));
            startTextOld = termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.TERMS_START));
            endTextOld = termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.TERMS_END));
            titleEditor.setText(titleTextOld);
            startEditor.setText(startTextOld);
            endEditor.setText(endTextOld);

            // build out the list of courses for this term and display them in the GUI
            final Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
            String courseFilter = DBOpenHelper.COURSES_TERMID + "=" + currentTermID;
            Cursor courseCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, courseFilter, null, null);
            String[] from = {DBOpenHelper.COURSES_TITLE};
            int[] to = {android.R.id.text1};
            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, courseCursor, from, to, 0);

            ListView list = (ListView) findViewById(android.R.id.list);
            list.setAdapter(cursorAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewTermActivity.this, ViewCourseActivity.class);
                    Uri uri = Uri.parse(courseURI + "/" + id);
                    Log.d("ViewTermActivity", "courseURI: " + uri.toString());
                    intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                    intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentTermID);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDITOR_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
//                String returnedValue = data.getStringExtra("returnValue");
//                Toast.makeText(this, "value returned: " + returnedValue, Toast.LENGTH_LONG).show();

                // build out the list of courses for this term and display them in the GUI
                final Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
                String courseFilter = DBOpenHelper.COURSES_TERMID + "=" + currentTermID;
                Cursor courseCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, courseFilter, null, null);
                String[] from = {DBOpenHelper.COURSES_TITLE};
                int[] to = {android.R.id.text1};
                CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, courseCursor, from, to, 0);

                ListView list = (ListView) findViewById(android.R.id.list);
                list.setAdapter(cursorAdapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ViewTermActivity.this, ViewCourseActivity.class);
                        Uri uri = Uri.parse(courseURI + "/" + id);
                        Log.d("ViewTermActivity", "courseURI: " + uri.toString());
                        intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentTermID);
                        startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    }
                });


            }
        }
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        // REFACTORED:: ADDED
        String titleTextNew = titleEditor.getText().toString().trim();
        String startTextNew = startEditor.getText().toString().trim();
        String endTextNew = endEditor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (titleTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertTerm(titleTextNew, startTextNew, endTextNew);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0 || titleTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0) { // REFACTORED
//                    deleteNote();
                } else if (oldText.equals(newText) && titleTextOld.equals(titleTextNew) && startTextOld.equals(startTextNew) && endTextOld.equals(endTextNew)) { // REFACTORED
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                    // REFACTORED:: ADDED
                    updateTerm(titleTextNew, startTextNew, endTextNew);
                }

        }
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
        getContentResolver().update(TermTrackerProvider.CONTENT_URI, values, notesFilter, null);
        Toast.makeText(this, "NOTE UPDATED...", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
        getContentResolver().insert(TermTrackerProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    // REFACTORED:: ADDED
    private void updateTerm(String titleText, String startText, String endText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERMS_TITLE, titleText);
        values.put(DBOpenHelper.TERMS_START, startText);
        values.put(DBOpenHelper.TERMS_END, endText);
        Uri termURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_TERMS);
        getContentResolver().update(termURI, values, termsFilter, null);
        Toast.makeText(this, "TERM UPDATED...", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertTerm(String title, String startDate, String endDate) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERMS_TITLE, title);
        values.put(DBOpenHelper.TERMS_START, startDate);
        values.put(DBOpenHelper.TERMS_END, endDate);
        Uri termURI = getContentResolver().insert(Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_TERMS), values);
        Log.d("ViewTermActivity", "termURI: " + termURI.toString());
        Log.d("ViewTermActivity", "Inserted a term " + termURI.getLastPathSegment());
    }



    // all three of the below methods are required to ensure full capture of the user leaving this activity
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                NavUtils.navigateUpFromSameTask(this);
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
