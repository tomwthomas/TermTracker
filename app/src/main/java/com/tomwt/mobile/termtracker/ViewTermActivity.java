package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

public class ViewTermActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 1001;

    private String action;
//    private EditText editor;
//    private String notesFilter;
//    private String oldText;

    // REFACTORED::  ADDED
    private String termsFilter;
    private EditText titleEditor;
    private EditText startEditor;
    private EditText endEditor;
    private String titleTextOld;
    private String startTextOld;
    private String endTextOld;

    private int currentTermID;

    final Calendar alertCal = Calendar.getInstance();


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

//        editor = (EditText) findViewById(R.id.editText);
        // REFACTORED:: ADDED
        titleEditor = (EditText) findViewById(R.id.data_title);
        startEditor = (EditText) findViewById(R.id.data_startDate);
        endEditor = (EditText) findViewById(R.id.data_endDate);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
//            getSupportActionBar().setTitle("INTENT.INSERT (uri==null)...");
            fab.hide();
        } else {
            action = Intent.ACTION_EDIT;
//            notesFilter = DBOpenHelper.NOTES_ID + "=" + uri.getLastPathSegment();

//            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.NOTES_ALL_COLUMNS, notesFilter, null, null);
//            cursor.moveToFirst();
//            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTES_DETAILS));
//            editor.setText(oldText);
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
                    new DatePickerDialog(ViewTermActivity.this, datePicker, alertCal.get(Calendar.YEAR), alertCal.get(Calendar.MONTH), alertCal.get(Calendar.DAY_OF_MONTH)).show();
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
                    new DatePickerDialog(ViewTermActivity.this, datePickerEnd, alertCal.get(Calendar.YEAR), alertCal.get(Calendar.MONTH), alertCal.get(Calendar.DAY_OF_MONTH)).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action != Intent.ACTION_INSERT) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_terms, menu);
        }

        return true;
    }

    private void removeTerm() {
        Toast.makeText(this, "REMOVE TERM CALLED...", Toast.LENGTH_LONG).show();
        // TODO:  can not remove a term if there are courses still assigned per requirements
        // check if any courses have this termID
        // if not delete this term
        // else alert the user via toast that their are courses attached, delete those first

        Uri courseURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_COURSES);
        String courseFilter = DBOpenHelper.COURSES_TERMID + "=" + currentTermID;
        Cursor courseCursor = getContentResolver().query(courseURI, DBOpenHelper.COURSES_ALL_COLUMNS, courseFilter, null, null);
        int termCount = courseCursor.getCount();
        if(termCount > 0) {
            Toast.makeText(this, "This term has courses assigned.\n\nREMOVE DENIED", Toast.LENGTH_LONG).show();
        }
        else {
            Uri termURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_TERMS);
            getContentResolver().delete(termURI, termsFilter, null);
            action = Intent.ACTION_DELETE;
            Toast.makeText(this, "TERM DELETED...", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void finishEditing() {
//        String newText = editor.getText().toString().trim();

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
                if (titleTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0) { // REFACTORED
//                    deleteNote();
                } else if (titleTextOld.equals(titleTextNew) && startTextOld.equals(startTextNew) && endTextOld.equals(endTextNew)) { // REFACTORED
                    setResult(RESULT_CANCELED);
                } else {
//                    updateNote(newText);
                    // REFACTORED:: ADDED
                    updateTerm(titleTextNew, startTextNew, endTextNew);
                }

        }
        Intent intent = new Intent();
        intent.putExtra("returnValue", "9999");
        setResult(RESULT_OK, intent);
        finish();
    }

//    private void updateNote(String noteText) {
//        ContentValues values = new ContentValues();
//        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
//        getContentResolver().update(TermTrackerProvider.CONTENT_URI, values, notesFilter, null);
//        Toast.makeText(this, "NOTE UPDATED...", Toast.LENGTH_SHORT).show();
//        setResult(RESULT_OK);
//    }
//
//    private void insertNote(String noteText) {
//        ContentValues values = new ContentValues();
//        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
//        getContentResolver().insert(TermTrackerProvider.CONTENT_URI, values);
//        setResult(RESULT_OK);
//    }

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
            case R.id.menu_removeTerm:
                removeTerm();
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
