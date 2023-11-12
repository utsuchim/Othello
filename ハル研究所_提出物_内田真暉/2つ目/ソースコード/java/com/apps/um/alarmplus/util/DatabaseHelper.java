package com.apps.um.alarmplus.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_FILENAME = "alarms.db";
    static final private int VERSION = 1;
    private static DatabaseHelper sSingleton = null;

    DatabaseHelper(Context context){
        super(context, DATABASE_FILENAME, null, VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new DatabaseHelper(context);
        }
        return sSingleton;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override//Uri: /storage/emulated/0/Download/
    public void onCreate(SQLiteDatabase db) {
        String cmd = "CREATE TABLE alarms (alarmId INTEGER PRIMARY KEY, alarmTime TEXT, musicUri TEXT, toggle TEXT, label TEXT);";
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(db != null) {
            db.execSQL("DROP TABLE IF EXISTS alarms;");
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int createData(String alarmTime, String musicUri, boolean toggle, String label) {
        int req;
        try(SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("alarmTime", alarmTime);
            cv.put("musicUri", musicUri);
            cv.put("toggle", String.valueOf(toggle));
            cv.put("label", label);
            req = (int) db.insert("alarms", null, cv);
        }
        return req;
    }
    public void updateToggle(int TargetId, boolean toggle) {
        try(SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("toggle", String.valueOf(toggle));
            db.update("alarms", cv, "alarmId = "+TargetId, null);
        }
    }

    public void updateData(int TargetId, String alarmTime, String musicUri, boolean toggle, String label) {
        try(SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("alarmTime", alarmTime);
            cv.put("musicUri", musicUri);
            cv.put("toggle", String.valueOf(toggle));
            cv.put("label",label);
            db.update("alarms", cv, "alarmId = "+TargetId, null);
        }
    }

    public void deleteTable() {
        try(SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DROP TABLE IF EXISTS alarms;");
        }
    }


    public void deleteDataById(int TargetId) {
        try(SQLiteDatabase db = getWritableDatabase()) {
            db.delete("alarms", "alarmId = "+TargetId, null);
        }
    }
}
