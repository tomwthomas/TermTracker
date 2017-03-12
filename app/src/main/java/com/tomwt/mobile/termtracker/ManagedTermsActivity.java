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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

}
