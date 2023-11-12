package com.apps.um.alarmplus.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDataHelper extends SQLiteOpenHelper {

    private final static String DATABASE_FILENAME = "alarm_music.db";
    static final private int VERSION = 2;
    private static MusicDataHelper sSingleton = null;

    MusicDataHelper(Context context){
        super(context, DATABASE_FILENAME, null, VERSION);
    }

    public static synchronized MusicDataHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new MusicDataHelper(context);
        }
        return sSingleton;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override//Uri: /storage/emulated/0/Download/
    public void onCreate(SQLiteDatabase db) {
        String cmd = "CREATE TABLE musicList (musicId INTEGER PRIMARY KEY, musicFileName TEXT,musicUri TEXT);";
        db.execSQL(cmd);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(db != null) {
            db.execSQL("DROP TABLE IF EXISTS musicList;");
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createMusicData(String musicFileName, String musicUri) {
        try(SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("musicFileName", musicFileName);
            cv.put("musicUri", musicUri);
            db.insert("musicList", null, cv);
        }
    }

    public void updateMusicData(int musicId, String musicFileName, String musicUri) {
        try(SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("musicFileName", musicFileName);
            cv.put("musicUri", musicUri);
            db.update("musicList", cv, "musicId = "+ musicId, null);
        }
    }

    public void deleteTable() {
        try(SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DROP TABLE IF EXISTS musicList;");
        }
    }

    public void deleteMusicDataById(int musicId) {
        try(SQLiteDatabase db = getWritableDatabase()) {
            db.delete("musicList", "musicId = " + musicId, null);
        }
    }
}
