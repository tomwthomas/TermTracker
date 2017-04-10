package com.tomwt.mobile.termtracker;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ManagedTermsActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 1001;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managed_terms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(ManagedTermsActivity.this, ViewTermActivity.class);
//                Uri termURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_TERMS);
//                Uri uri = Uri.parse(TermTrackerProvider.CONTENT_URI + "/" + id);
//                Log.d("ManagedTermsActivity", "termURI: " + uri.toString());
//                intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Terms");

        // build out the list of upcoming milestones and display them in the GUI
        // REFACTORED:: ADDED
        Uri termURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_TERMS);
        // REFACTORED:: Cursor cursor = getContentResolver().query(TermTrackerProvider.CONTENT_URI, DBOpenHelper.NOTES_ALL_COLUMNS, null, null, null, null);
        Cursor cursor = getContentResolver().query(termURI, DBOpenHelper.TERMS_ALL_COLUMNS, null, null, null, null);
        // REFACTORED:: String[] from = {DBOpenHelper.NOTES_DETAILS};
        String[] from = {DBOpenHelper.TERMS_TITLE}; // TERMS_TITLE
        int[] to = {android.R.id.text1};
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManagedTermsActivity.this, ViewTermActivity.class);
                Uri uri = Uri.parse(TermTrackerProvider.CONTENT_URI + "/" + id);
                Log.d("ManagedTermsActivity", "termURI: " + uri.toString());
                intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
                // startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDITOR_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
//                String returnedValue = data.getStringExtra("returnValue");
//                Toast.makeText(this, "value returned: " + returnedValue, Toast.LENGTH_LONG).show();

                // build out the list of upcoming milestones and display them in the GUI
                // REFACTORED:: ADDED
                Uri termURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_TERMS);
                // REFACTORED:: Cursor cursor = getContentResolver().query(TermTrackerProvider.CONTENT_URI, DBOpenHelper.NOTES_ALL_COLUMNS, null, null, null, null);
                Cursor cursor = getContentResolver().query(termURI, DBOpenHelper.TERMS_ALL_COLUMNS, null, null, null, null);
                // REFACTORED:: String[] from = {DBOpenHelper.NOTES_DETAILS};
                String[] from = {DBOpenHelper.TERMS_TITLE}; // TERMS_TITLE
                int[] to = {android.R.id.text1};
                CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);

                ListView list = (ListView) findViewById(android.R.id.list);
                list.setAdapter(cursorAdapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ManagedTermsActivity.this, ViewTermActivity.class);
                        Uri uri = Uri.parse(TermTrackerProvider.CONTENT_URI + "/" + id);
                        Log.d("ManagedTermsActivity", "termURI: " + uri.toString());
                        intent.putExtra(TermTrackerProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, EDITOR_REQUEST_CODE);
                        // startActivity(intent);
                    }
                });


            }
        }
    }

    private void finishEditing() {

        Intent intent = new Intent();
        intent.putExtra("returnValue", "9999");
        setResult(RESULT_OK, intent);
        finish();
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
