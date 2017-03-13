package com.tomwt.mobile.termtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

public class ViewCourseActivity extends AppCompatActivity {

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
    }


    private void finishEditing() {
//        String newText = editor.getText().toString().trim();
//
//        // REFACTORED:: ADDED
//        String titleTextNew = titleEditor.getText().toString().trim();
//        String startTextNew = startEditor.getText().toString().trim();
//        String endTextNew = endEditor.getText().toString().trim();
//
//        switch (action) {
//            case Intent.ACTION_INSERT:
//                if (titleTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0) {
//                    setResult(RESULT_CANCELED);
//                } else {
//                    insertTerm(titleTextNew, startTextNew, endTextNew);
//                }
//                break;
//            case Intent.ACTION_EDIT:
//                if (newText.length() == 0 || titleTextNew.length() == 0 || startTextNew.length() == 0 || endTextNew.length() == 0) { // REFACTORED
////                    deleteNote();
//                } else if (oldText.equals(newText) && titleTextOld.equals(titleTextNew) && startTextOld.equals(startTextNew) && endTextOld.equals(endTextNew)) { // REFACTORED
//                    setResult(RESULT_CANCELED);
//                } else {
//                    updateNote(newText);
//                    // REFACTORED:: ADDED
//                    updateTerm(titleTextNew, startTextNew, endTextNew);
//                }
//
//        }
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
