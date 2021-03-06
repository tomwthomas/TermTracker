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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class ViewAssessmentActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 1001;

    private String action;
//    private EditText editor;
//    private String notesFilter;
//    private String oldText;

    // REFACTORED::  ADDED
    private String assessmentsFilter;
    private EditText titleEditor;
    private EditText detailsEditor;
//    private EditText typeEditor;
    private Spinner typeSpinner;
//    private EditText statusEditor;
    private Spinner statusSpinner;
    private String titleTextOld;
    private String detailsTextOld;
//    private String typeTextOld;
    private int typeSpinnerOld;
//    private String statusTextOld;
    private int statusSpinnerOld;

    private int currentAssessmentID;
    private int PID;



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assessment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assessment Information");

//        editor = (EditText) findViewById(R.id.editText);
        // REFACTORED:: ADDED
        titleEditor = (EditText) findViewById(R.id.data_title);
        detailsEditor = (EditText) findViewById(R.id.data_details);
//        typeEditor = (EditText) findViewById(R.id.data_type);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
//        statusEditor = (EditText) findViewById(R.id.data_status);
        statusSpinner = (Spinner) findViewById(R.id.statusSpinner);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.assessments_type_array, android.R.layout.simple_spinner_dropdown_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new SpinnerActivity());

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.assessments_status_array, android.R.layout.simple_spinner_dropdown_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new SpinnerActivity());

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);
        PID = intent.getIntExtra(TermTrackerProvider.CONTENT_PARENT_ID, 0);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
//            getSupportActionBar().setTitle("INTENT.INSERT (uri==null)...");
//            fab.hide();
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
            Uri assessmentURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ASSESSMENTS);
            currentAssessmentID = Integer.parseInt(uri.getLastPathSegment());
            assessmentsFilter = DBOpenHelper.ASSESSMENTS_ID + "=" + currentAssessmentID;
            Cursor assessmentCursor = getContentResolver().query(assessmentURI, DBOpenHelper.ASSESSMENTS_ALL_COLUMNS, assessmentsFilter, null, null);
            assessmentCursor.moveToFirst();
            titleTextOld = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENTS_TITLE));
            detailsTextOld = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENTS_DETAILS));
//            typeTextOld = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENTS_TYPE));
            typeSpinnerOld = Integer.parseInt(assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENTS_TYPE)));
//            typeSpinnerOld = 1;  // TODO:  refactor this once code has been migrated to spinner instead of EditText
//            statusTextOld = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENTS_STATUS));
            statusSpinnerOld = Integer.parseInt(assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENTS_STATUS)));
//            statusSpinnerOld = 1;  // TODO:  refactor this once code has been migrated to spinner instead of EditText
            titleEditor.setText(titleTextOld);
            detailsEditor.setText(detailsTextOld);
//            typeEditor.setText(typeTextOld);
            typeSpinner.setSelection(typeSpinnerOld);
//            statusEditor.setText(statusTextOld);
            statusSpinner.setSelection(statusSpinnerOld);

            // build out the list of alerts for this assessment and display them in the GUI
            final Uri alertURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ALERTS);
            String alertsFilter = DBOpenHelper.ALERTS_PID + "=" + currentAssessmentID;
            Cursor alertCursor = getContentResolver().query(alertURI, DBOpenHelper.ALERTS_ALL_COLUMNS, alertsFilter, null, null);
            String[] from = {DBOpenHelper.ALERTS_DATE};
            int[] to = {android.R.id.text1};
            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, alertCursor, from, to, 0);

            ListView list = (ListView) findViewById(android.R.id.list);
            list.setAdapter(cursorAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ViewAssessmentActivity.this, ViewAssessmentAlertActivity.class);
                    Uri uri = Uri.parse(alertURI + "/" + id);
                    Log.d("ViewAssessAlertActivity", "alertURI: " + uri.toString());
                    intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                    intent.putExtra(TermTrackerProvider.CONTENT_PARENT_TYPE, TermTrackerProvider.TYPE_ASSESSMENT);
                    intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentAssessmentID);
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
                // build out the list of alerts for this assessment and display them in the GUI
                final Uri alertURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ALERTS);
                String alertsFilter = DBOpenHelper.ALERTS_PID + "=" + currentAssessmentID;
                Cursor alertCursor = getContentResolver().query(alertURI, DBOpenHelper.ALERTS_ALL_COLUMNS, alertsFilter, null, null);
                String[] from = {DBOpenHelper.ALERTS_DATE};
                int[] to = {android.R.id.text1};
                CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, alertCursor, from, to, 0);

                ListView list = (ListView) findViewById(android.R.id.list);
                list.setAdapter(cursorAdapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ViewAssessmentActivity.this, ViewAssessmentAlertActivity.class);
                        Uri uri = Uri.parse(alertURI + "/" + id);
                        Log.d("ViewAssessAlertActivity", "alertURI: " + uri.toString());
                        intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_TYPE, TermTrackerProvider.TYPE_ASSESSMENT);
                        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentAssessmentID);
                        startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action != Intent.ACTION_INSERT) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_assessments, menu);
        }

        return true;
    }

    private void addAlert() {
        Toast.makeText(this, "ADD ALERT CALLED...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ViewAssessmentActivity.this, ViewAssessmentAlertActivity.class);

//        // build a URI that refers to the specific alert we want to edit
//        Uri alertsURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ALERTS);
//        alertsURI = Uri.parse(alertsURI + "/" + currentID);
//        intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, alertsURI);
//        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_TYPE, TermTrackerProvider.TYPE_ASSESSMENT);
        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_TYPE, TermTrackerProvider.TYPE_ASSESSMENT);
        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentAssessmentID);

        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    private void addNote() {
        Toast.makeText(this, "ADD NOTE CALLED...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ViewAssessmentActivity.this, ViewNoteActivity.class);

//        // build a URI that refers to the specific note we want to edit
//        Uri notesURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_NOTES);
//        notesURI = Uri.parse(notesURI + "/" + currentID);
//        intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, notesURI);
        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_TYPE, TermTrackerProvider.TYPE_ASSESSMENT);
        intent.putExtra(TermTrackerProvider.CONTENT_PARENT_ID, currentAssessmentID);

        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    private void finishEditing() {
//        String newText = editor.getText().toString().trim();
//
        // REFACTORED:: ADDED
        String titleTextNew = titleEditor.getText().toString().trim();
        String detailsTextNew = detailsEditor.getText().toString().trim();
//        String typeTextNew = typeEditor.getText().toString().trim();
        int typeSpinnerNew = typeSpinner.getSelectedItemPosition();
//        String statusTextNew = statusEditor.getText().toString().trim();
        int statusSpinnerNew = statusSpinner.getSelectedItemPosition();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (titleTextNew.length() == 0 || detailsTextNew.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertAssessment(titleTextNew, detailsTextNew, typeSpinnerNew, statusSpinnerNew);
                }
                break;
            case Intent.ACTION_EDIT:
                if (titleTextNew.length() == 0 || detailsTextNew.length() == 0) { // REFACTORED
//                    deleteNote();
                } else if (titleTextOld.equals(titleTextNew) && detailsTextOld.equals(detailsTextNew) && typeSpinnerOld == typeSpinnerNew && statusSpinnerOld == statusSpinnerNew) { // REFACTORED
                    setResult(RESULT_CANCELED);
                } else {
//                    updateNote(newText);
                    // REFACTORED:: ADDED
                    updateAssessment(titleTextNew, detailsTextNew, typeSpinnerNew, statusSpinnerNew);
                }

        }

        Intent intent = new Intent();
        intent.putExtra("returnValue", "9999");
        setResult(RESULT_OK, intent);
        finish();
    }

    // REFACTORED:: ADDED
    private void updateAssessment(String titleText, String detailsText, int typeText, int statusText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENTS_COURSEID, PID);
        values.put(DBOpenHelper.ASSESSMENTS_TITLE, titleText);
        values.put(DBOpenHelper.ASSESSMENTS_DETAILS, detailsText);
        values.put(DBOpenHelper.ASSESSMENTS_TYPE, typeText);
        values.put(DBOpenHelper.ASSESSMENTS_STATUS, statusText);
        Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ASSESSMENTS);
        getContentResolver().update(courseURI, values, assessmentsFilter, null);
        Toast.makeText(this, "ASSESSMENT UPDATED...", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
    }

    private void insertAssessment(String title, String details, int type, int status) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENTS_COURSEID, PID);
        values.put(DBOpenHelper.ASSESSMENTS_TITLE, title);
        values.put(DBOpenHelper.ASSESSMENTS_DETAILS, details);
        values.put(DBOpenHelper.ASSESSMENTS_TYPE, type);
        values.put(DBOpenHelper.ASSESSMENTS_STATUS, status);
        Uri assessmentURI = getContentResolver().insert(Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_ASSESSMENTS), values);
        Log.d("ViewAssessmentActivity", "assessmentURI: " + assessmentURI.toString());
        Log.d("ViewCourseActivity", "Inserted an assessment " + assessmentURI.getLastPathSegment());
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
            case R.id.menu_addAlert:
                addAlert();
                return true;
            case R.id.menu_addNote:
                addNote();
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
