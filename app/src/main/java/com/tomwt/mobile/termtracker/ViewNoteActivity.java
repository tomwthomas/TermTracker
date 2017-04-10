package com.tomwt.mobile.termtracker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
//import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
//import android.widget.AdapterView;
import android.widget.Button;
//import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
//import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewNoteActivity extends AppCompatActivity {

    // private static final int EDITOR_REQUEST_CODE = 1001;
    static final int REQUEST_TAKE_PHOTO = 1;

    private String action;
    private EditText noteEditor;
    private String notesFilter;
    private String noteTextOld;
    private String imgTextOld;
    private String imgTextNew;

    private Button btn_takePhoto;
    private ImageView imgview_takePhoto;
    private Uri note_img_file;

    private int parentType;
    private int PID;
//    private String currentNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        // check to see if we have permission to use the camera and only enable the corresponding button accordingly
        btn_takePhoto = (Button) findViewById(R.id.btn_take_picture);
        imgview_takePhoto = (ImageView) findViewById(R.id.image_for_note);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btn_takePhoto.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

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
        getSupportActionBar().setTitle("Note Information");

        noteEditor = (EditText) findViewById(R.id.data_title);

        Intent intent = getIntent();

//        Uri uri = intent.getParcelableExtra(TermTrackerProvider.CONTENT_ITEM_TYPE);
//        PID = uri.getLastPathSegment();
//        String parentType = intent.getParcelableExtra(TermTrackerProvider.CONTENT_PARENT_TYPE);
        parentType = intent.getIntExtra(TermTrackerProvider.CONTENT_PARENT_TYPE, 0);
        PID = intent.getIntExtra(TermTrackerProvider.CONTENT_PARENT_ID, 0);
        int noteCount;
        TermTrackerProvider TTP = new TermTrackerProvider();
        Cursor cursor = TTP.getAssessmentNoteCount(ViewNoteActivity.this, String.valueOf(PID), String.valueOf(parentType));
        cursor.moveToFirst();
        noteCount = Integer.parseInt(cursor.getString(0));
        cursor.close();

        if (noteCount == 0) {
            action = Intent.ACTION_INSERT;
//            getSupportActionBar().setTitle("INTENT.INSERT (uri==null)...");
        } else {
            action = Intent.ACTION_EDIT;

            Uri noteURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_NOTES);
//            currentNoteID = uri.getLastPathSegment();
            notesFilter = DBOpenHelper.NOTES_TYPE + "=" + parentType + " and " + DBOpenHelper.NOTES_PID + "=" + PID;
            Cursor noteCursor = getContentResolver().query(noteURI, DBOpenHelper.NOTES_ALL_COLUMNS, notesFilter, null, null);
            noteCursor.moveToFirst();
//            currentNoteID = noteCursor.getString(noteCursor.getColumnIndex(DBOpenHelper.NOTES_ID));
            noteTextOld = noteCursor.getString(noteCursor.getColumnIndex(DBOpenHelper.NOTES_DETAILS));
            imgTextOld = noteCursor.getString(noteCursor.getColumnIndex(DBOpenHelper.NOTES_IMG));
            imgTextNew = imgTextOld;
            noteEditor.setText(noteTextOld);
            if (imgTextOld != null)
                imgview_takePhoto.setImageURI(Uri.parse(imgTextOld));

        }

    }

    @Override
    public void onRequestPermissionsResult(int reqeustCode, String[] permissions, int[] grantResults) {
        // check if we were granted the permissions to use the camera and write files, adjust the button accordingly
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            btn_takePhoto.setEnabled(true);
        }
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        note_img_file = Uri.fromFile(getOutputMediaFile());
        note_img_file = FileProvider.getUriForFile(ViewNoteActivity.this, "com.tomwt.mobile.termtracker.fileprovider", getOutputMediaFile());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, note_img_file);
//        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                imgview_takePhoto.setImageURI(note_img_file);
//                imgview_takePhoto.setImageURI(Uri.parse("content://com.tomwt.mobile.termtracker.fileprovider/my_images/note_20170330_003535.jpg")); // COULD pull this from DB if we store it on creation
                imgTextNew = note_img_file.toString();
            }
        }
    }

    private File getOutputMediaFile() { // was static before we commented out the top director creator
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TermTracker");
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "note_" + timeStamp + ".jpg");
    }




    // below is for the dispatchTakePictureIntent path that is not being called by the button currently
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imgview_takePhoto.setImageBitmap(imageBitmap);
//        }
//    }



//    public void dispatchTakePictureIntent(View view) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.tomwt.mobile.termtracker.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }

//    String mCurrentPhotoPath;

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action != Intent.ACTION_INSERT) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_notes, menu);
        }

        return true;
    }

    private void shareNote() {
        Toast.makeText(this, "SHARE NOTE CALLED...", Toast.LENGTH_LONG).show();
        String[] addresses = {"ttho163@wgu.edu"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing a note from my TermTracker");
        intent.putExtra(Intent.EXTRA_TEXT, noteEditor.getText().toString().trim());
        if(intent.resolveActivity(getPackageManager()) != null) {
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Select email client:"));
        }
    }

    private void finishEditing() {
//        String newText = editor.getText().toString().trim();
//
        // REFACTORED:: ADDED
        String noteTextNew = noteEditor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (noteTextNew.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(noteTextNew, imgTextNew, PID, parentType);
                }
                break;
            case Intent.ACTION_EDIT: //
                if (noteTextNew.length() == 0) { // REFACTORED
//                    deleteNote();
                } else if (noteTextOld.equals(noteTextNew) && imgTextOld != null && imgTextOld.equals(imgTextNew)) { // REFACTORED
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(noteTextNew, imgTextNew, PID, parentType);
                }

        }

        Intent intent = new Intent();
        intent.putExtra("returnValue", "9999");
        setResult(RESULT_OK, intent);
        finish();
    }

    // REFACTORED:: ADDED
    private void updateNote(String noteText, String imgPath, int currentPID, int currentParentType) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
        values.put(DBOpenHelper.NOTES_IMG, imgPath);
        values.put(DBOpenHelper.NOTES_PID, currentPID);
        values.put(DBOpenHelper.NOTES_TYPE, currentParentType);
        Uri noteURI = Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_NOTES);
        getContentResolver().update(noteURI, values, notesFilter, null);
        Toast.makeText(this, "NOTE UPDATED...", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText, String imgPath, int currentPID, int currentParentType) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTES_DETAILS, noteText);
        values.put(DBOpenHelper.NOTES_IMG, imgPath);
        values.put(DBOpenHelper.NOTES_PID, currentPID);
        values.put(DBOpenHelper.NOTES_TYPE, currentParentType);
        Uri noteURI = getContentResolver().insert(Uri.withAppendedPath(TermTrackerProvider.CONTENT_URI_PATHLESS, DBOpenHelper.TABLE_NOTES), values);
        Log.d("ViewNoteActivity", "noteURI: " + noteURI.toString());
        Log.d("ViewNoteActivity", "Inserted a note " + noteURI.getLastPathSegment());
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
//                addAlert();
                return true;
            case R.id.menu_addNote:
//                addNote();
                return true;
            case R.id.menu_shareNote:
                shareNote();
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
