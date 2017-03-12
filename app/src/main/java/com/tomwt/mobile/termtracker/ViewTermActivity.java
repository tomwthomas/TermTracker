package com.tomwt.mobile.termtracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ViewTermActivity extends AppCompatActivity {

    private  String action;
    private EditText editor;
    private String termFilter;
    private String oldText;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Term Information");

        editor = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("THIS IS A NEW TITLE...");
        } else {
            action = Intent.ACTION_EDIT;
            termFilter = DBOpenHelper.NOTES_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.NOTES_ALL_COLUMNS, termFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTES_DETAILS));
            editor.setText(oldText);
            editor.requestFocus();
        }
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
//                    deleteNote();
                } else if (oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }

        }
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
        getContentResolver().update(TermTrackerProvider.CONTENT_URI, values, termFilter, null);
        Toast.makeText(this, "NOTE UPDATED...", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
        getContentResolver().insert(TermTrackerProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
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
