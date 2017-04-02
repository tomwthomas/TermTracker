package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

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
//    private EditText statusEditor;
    private Spinner statusSpinner;
    private EditText mentorEditor;
    private String titleTextOld;
    private String detailsTextOld;
    private String startTextOld;
    private String endTextOld;
//    private String statusTextOld;
    private int statusSpinnerOld;
    private String mentorTextOld;

    private int currentCourseID;
    private int PID;

    final Calendar alertCal = Calendar.getInstance();




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
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(ViewCourseActivity.this, ViewAssessmentActivity.class);
                intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentCourseID);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
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
//        statusEditor = (EditText) findViewById(R.id.data_status);
        statusSpinner = (Spinner) findViewById(R.id.statusSpinner);
        mentorEditor = (EditText) findViewById(R.id.data_mentor);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.course_status_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setOnItemSelectedListener(new SpinnerActivity());

//        int currentPosition = spinner.getSelectedItemPosition();
//        spinner.setSelection(2);


        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);
        PID = intent.getIntExtra(TermTrackerProvider.CONTENT_PARENT_ID, 0);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            getSupportActionBar().setTitle("INTENT.INSERT (uri==null)...");
            fab.hide();
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
            currentCourseID = Integer.parseInt(uri.getLastPathSegment());
            coursesFilter = DBOpenHelper.COURSES_ID + "=" + currentCourseID;
            Cursor courseCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, coursesFilter, null, null);
            courseCursor.moveToFirst();
            titleTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_TITLE));
            detailsTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_DETAILS));
            startTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_START));
            endTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_END));
//            statusTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_STATUS));
            statusSpinnerOld = Integer.parseInt(courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_STATUS)));
//            statusSpinnerOld = 1; // TODO:  refactor this once code has been migrated to spinner instead of EditText
            mentorTextOld = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_MENTOR));
            titleEditor.setText(titleTextOld);
            detailsEditor.setText(detailsTextOld);
            startEditor.setText(startTextOld);
            endEditor.setText(endTextOld);
//            statusEditor.setText(statusTextOld);
            statusSpinner.setSelection(statusSpinnerOld);
            mentorEditor.setText(mentorTextOld);

            // if we are calling this activity from Manage Courses we won't have a PID - so get one from the DB
            if(PID == 0)
                PID = Integer.parseInt(courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSES_ID)));

            // build out the list of assessments for this course and display them in the GUI
            final Uri assessmentsURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ASSESSMENTS);
            String assessmentsFilter = DBOpenHelper.ASSESSMENTS_COURSEID + "=" + currentCourseID;
            Cursor assessmentCursor = getContentResolver().query(assessmentsURI, DBOpenHelper.ASSESSMENTS_ALL_COLUMNS, assessmentsFilter, null, null);
            String[] from = {DBOpenHelper.ASSESSMENTS_TITLE};
            int[] to = {android.R.id.text1};
            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, assessmentCursor, from, to, 0);

            ListView list = (ListView) findViewById(android.R.id.list);
            list.setAdapter(cursorAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewCourseActivity.this, ViewAssessmentActivity.class);
                    Uri uri = Uri.parse(assessmentsURI + "/" + id);
                    Log.d("ViewCourseActivity", "assessmentURI: " + uri.toString());
                    intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                    intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentCourseID);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                }
            });
        }


        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                alertCal.set(Calendar.YEAR, year);
                alertCal.set(Calendar.MONTH, monthOfYear);
                alertCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(startEditor);
            }
        };

        final DatePickerDialog.OnDateSetListener datePickerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                alertCal.set(Calendar.YEAR, year);
                alertCal.set(Calendar.MONTH, monthOfYear);
                alertCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(endEditor);
            }
        };

        startEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    new DatePickerDialog(ViewCourseActivity.this, datePicker, alertCal.get(Calendar.YEAR), alertCal.get(Calendar.MONTH), alertCal.get(Calendar.DAY_OF_MONTH)).show();
                }
                else {
                    // add lost focus here if appropriate
                }
            }
        });

        endEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    new DatePickerDialog(ViewCourseActivity.this, datePickerEnd, alertCal.get(Calendar.YEAR), alertCal.get(Calendar.MONTH), alertCal.get(Calendar.DAY_OF_MONTH)).show();
                }
                else {
                    // add lost focus here if appropriate
                }
            }
        });


    }

    private void updateLabel(EditText toUpdate) {
//        String myFormat = "MM/dd/yy";
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

//        EditText alertDate = (EditText) findViewById(R.id.data_alertDate);
        toUpdate.setText(sdf.format(alertCal.getTime()));
        toUpdate.setCursorVisible(false);
        View view = this.getCurrentFocus();
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDITOR_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                // build out the list of assessments for this course and display them in the GUI
                final Uri assessmentsURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ASSESSMENTS);
                String assessmentsFilter = DBOpenHelper.ASSESSMENTS_COURSEID + "=" + currentCourseID;
                Cursor assessmentCursor = getContentResolver().query(assessmentsURI, DBOpenHelper.ASSESSMENTS_ALL_COLUMNS, assessmentsFilter, null, null);
                String[] from = {DBOpenHelper.ASSESSMENTS_TITLE};
                int[] to = {android.R.id.text1};
                CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, assessmentCursor, from, to, 0);

                ListView list = (ListView) findViewById(android.R.id.list);
                list.setAdapter(cursorAdapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ViewCourseActivity.this, ViewAssessmentActivity.class);
                        Uri uri = Uri.parse(assessmentsURI + "/" + id);
                        Log.d("ViewCourseActivity", "assessmentURI: " + uri.toString());
                        intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentCourseID);
                        startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    }
                });

            }
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
//        String statusTextNew = statusEditor.getText().toString().trim();
        int statusSpinnerNew = statusSpinner.getSelectedItemPosition();
        String mentorTextNew = mentorEditor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (titleTextNew.length() == 0 || detailsTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0 || mentorTextNew.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertCourse(titleTextNew, detailsTextNew, startTextNew, endTextNew, statusSpinnerNew, mentorTextNew);
                }
                break;
            case Intent.ACTION_EDIT:
                if (titleTextNew.length() == 0 || detailsTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0 || mentorTextNew.length() == 0) { // REFACTORED
//                    deleteNote();
                } else if (titleTextOld.equals(titleTextNew) && detailsTextOld.equals(detailsTextNew) && startTextOld.equals(startTextNew) && endTextOld.equals(endTextNew) && statusSpinnerOld == statusSpinnerNew && mentorTextOld.equals(mentorTextNew)) { // REFACTORED
                    setResult(RESULT_CANCELED);
                } else {
//                    updateNote(newText);
                    // REFACTORED:: ADDED
                    updateCourse(titleTextNew, detailsTextNew, startTextNew, endTextNew, statusSpinnerNew, mentorTextNew);
                }

        }

        Intent intent = new Intent();
        intent.putExtra("returnValue", "9999");
        setResult(RESULT_OK, intent);
        finish();
    }

    // REFACTORED:: ADDED
    private void updateCourse(String titleText, String detailsText, String startText, String endText, int statusText, String mentorText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSES_TERMID, PID);
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

    private void insertCourse(String title, String details, String startDate, String endDate, int status, String mentor) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSES_TERMID, PID);
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