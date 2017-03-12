package com.tomwt.mobile.termtracker;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    // Constants for db name and version
    private static final String DB_NAME = "termTracker.db";
    private static final int DB_VERSION = 1;

    // Constants to make working with DB easier throughout the application broken down by table
    // ALERTS TABLE
    public static final String TABLE_ALERTS = "tbl_alerts";
    public static final String ALERTS_ID = "_id";
    public static final String ALERTS_PID = "parentID";
    public static final String ALERTS_TYPE = "parentType";
    public static final String ALERTS_DATE = "alertDate";
    public static final String ALERTS_SENT = "alertSent";
    public static final String ALERTS_TEXT = "alertText";
    public static final String ALERTS_TIMESTAMP = "timestamp";
    public static final String[] ALERTS_ALL_COLUMNS = {ALERTS_ID, ALERTS_PID, ALERTS_TYPE, ALERTS_DATE, ALERTS_SENT, ALERTS_TEXT, ALERTS_TIMESTAMP};

    // ASSESSMENTS TABLE
    public static final String TABLE_ASSESSMENTS = "tbl_assessments";
    public static final String ASSESSMENTS_ID = "_id";
    public static final String ASSESSMENTS_TYPE = "type";
    public static final String ASSESSMENTS_TITLE = "title";
    public static final String ASSESSMENTS_DETAILS = "details";
    public static final String ASSESSMENTS_DUE = "dueDate";
    public static final String ASSESSMENTS_STATUS = "status";
    public static final String[] ASSESSMENTS_ALL_COLUMNS = {ASSESSMENTS_ID, ASSESSMENTS_TYPE, ASSESSMENTS_TITLE, ASSESSMENTS_DETAILS, ASSESSMENTS_DUE, ASSESSMENTS_STATUS};

    // COURSES TABLE
    public static final String TABLE_COURSES = "tbl_courses";
    public static final String COURSES_ID = "_id";
    public static final String COURSES_TITLE = "title";
    public static final String COURSES_DETAILS = "details";
    public static final String COURSES_START = "startDate";
    public static final String COURSES_END = "expectedEndDate";
    public static final String COURSES_STATUS = "status";
    public static final String COURSES_MENTOR = "mentorInfo";
    public static final String[] COURSES_ALL_COLUMNS = {COURSES_ID, COURSES_TITLE, COURSES_DETAILS, COURSES_START, COURSES_END, COURSES_STATUS, COURSES_MENTOR};

    // NOTES TABLE
    public static final String TABLE_NOTES = "tbl_notes";
    public static final String NOTES_ID = "_id";
    public static final String NOTES_PID = "parentID";
    public static final String NOTES_TYPE = "parentType";
    public static final String NOTES_DETAILS = "details";
    public static final String NOTES_IMG = "imgPath";
    public static final String NOTES_TIMESTAMP = "timestamp";
    public static final String[] NOTES_ALL_COLUMNS = {NOTES_ID, NOTES_PID, NOTES_TYPE, NOTES_DETAILS, NOTES_IMG, NOTES_TIMESTAMP};

    // TERMS TABLE
    public static final String TABLE_TERMS = "tbl_terms";
    public static final String TERMS_ID = "_id";
    public static final String TERMS_TITLE = "title";
    public static final String TERMS_START = "startDate";
    public static final String TERMS_END = "endDate";
    public static final String[] TERMS_ALL_COLUMNS = {TERMS_ID, TERMS_TITLE, TERMS_START, TERMS_END};

    // CoursesAssessmentsXRef TABLE - TODO::  May need to add CourseID to the Assessments table and allow only one term per course for query simplicity
    public static final String TABLE_COURSES_ASSESSMENTS_XREF = "tbl_coursesAssessmentsXRef";
    public static final String CAXREF_COURSEID = "courseID";
    public static final String CAXREF_ASSESSMENTID = "assessmentID";
    public static final String[] CAXREF_ALL_COLUMNS = {CAXREF_COURSEID, CAXREF_ASSESSMENTID};

    // TermsCoursesXRef TABLE - TODO::  May need to add TermID to the Courses table and allow only one term per course for query simplicity
    public static final String TABLE_TERMS_COURSES_XREF = "tbl_termsCoursesXRef";
    public static final String TCXREF_TERMID = "termID";
    public static final String TCXREF_COURSEID = "courseID";
    public static final String[] TCXREF_ALL_COLUMNS = {TCXREF_TERMID, TCXREF_COURSEID};


    // Creation statements to make working with the DB easier throughout the application broken down by table
    // SQL to create alerts table
    private static final String CREATE_TABLE_ALERTS =
            "CREATE TABLE " + TABLE_ALERTS + " (" +
                    ALERTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ALERTS_PID + " INTEGER, " +
                    ALERTS_TYPE + " INTEGER, " +
                    ALERTS_DATE + " TEXT, " +
                    ALERTS_SENT + " BOOLEAN, " +
                    ALERTS_TEXT + " TEXT, " +
                    ALERTS_TIMESTAMP + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    // SQL to create assessments table
    private static final String CREATE_TABLE_ASSESSMENTS =
            "CREATE TABLE " + TABLE_ASSESSMENTS + " (" +
                    ASSESSMENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENTS_TYPE + " INTEGER, " +
                    ASSESSMENTS_TITLE + " TEXT, " +
                    ASSESSMENTS_DETAILS + " TEXT, " +
                    ASSESSMENTS_DUE + " TEXT, " +
                    ASSESSMENTS_STATUS + " TEXT" +
                    ")";

    // SQL to create courses table
    private static final String CREATE_TABLE_COURSES =
            "CREATE TABLE " + TABLE_COURSES + " (" +
                    COURSES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSES_TITLE + " TEXT, " +
                    COURSES_DETAILS + " TEXT, " +
                    COURSES_START + " TEXT, " +
                    COURSES_END + " TEXT, " +
                    COURSES_STATUS + " TEXT, " +
                    COURSES_MENTOR + " TEXT" +
                    ")";

    // SQL to create notes table
    private static final String CREATE_TABLE_NOTES =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTES_PID + " INTEGER, " +
                    NOTES_TYPE + " INTEGER, " +
                    NOTES_DETAILS + " TEXT, " +
                    NOTES_IMG + " TEXT, " +
                    NOTES_TIMESTAMP + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    // SQL to creat terms table
    private static final String CREATE_TABLE_TERMS =
            "CREATE TABLE " + TABLE_TERMS + " (" +
                    TERMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERMS_TITLE + " TEXT, " +
                    TERMS_START + " TEXT, " +
                    TERMS_END + " TEXT" +
                    ")";

    // SQL to create CoursesAssessmentsXRef table
    private static final String CREATE_TABLE_CAXREF =
            "CREATE TABLE " + TABLE_COURSES_ASSESSMENTS_XREF + " (" +
                    CAXREF_COURSEID + " INTEGER, " +
                    CAXREF_ASSESSMENTID + " INTEGER" +
                    ")";

    // SQL to create TermsCoursesXRef table
    private static final  String CREATE_TABLE_TCXREF =
            "CREATE TABLE " + TABLE_TERMS_COURSES_XREF + " (" +
                    TCXREF_TERMID + " INTEGER, " +
                    TCXREF_COURSEID + " INTEGER" +
                    ")";



    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALERTS);
        db.execSQL(CREATE_TABLE_ASSESSMENTS);
        db.execSQL(CREATE_TABLE_COURSES);
        db.execSQL(CREATE_TABLE_NOTES);
        db.execSQL(CREATE_TABLE_TERMS);
        db.execSQL(CREATE_TABLE_CAXREF);
        db.execSQL(CREATE_TABLE_TCXREF);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES_ASSESSMENTS_XREF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS_COURSES_XREF);
        onCreate(db);
    }

}
