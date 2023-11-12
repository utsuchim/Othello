package com.apps.um.alarmplus.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.listcomponent.ListItem;
import com.apps.um.alarmplus.listcomponent.MusicListItem;
import com.apps.um.alarmplus.receiver.AlarmReceiver;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Util {

    private static final int HEADER = 0;
    private static final String TAG = "AlarmX";

    @SuppressLint("Recycle")
    public static ListItem getAlarmsByID(DatabaseHelper helper, int alarmId){
        ListItem item = null;
        try(SQLiteDatabase db = helper.getReadableDatabase()) {
            String[] per = {String.valueOf(alarmId)};
            Cursor cs = db.query("alarms",null,"alarmId = ?",per,null,null,"alarmId",null);
            if(cs != null && cs.moveToFirst()) {
                item = new ListItem(cs.getInt(0),cs.getString(1),cs.getString(2), Boolean.parseBoolean(cs.getString(3)), cs.getString(4));
            }
        }
        return item;
    }

    // アラームをセット
    @SuppressLint("ObsoleteSdkInt")
    public static void setAlarm(Context context, ListItem item){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(item.getHour()));
        calendar.set(Calendar.MINUTE, Integer.parseInt(item.getMinute()));
        calendar.set(Calendar.SECOND, 0);

        // 現在時刻を取得
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(System.currentTimeMillis());

        // 比較
        int diff = calendar.compareTo(nowCalendar);

        // 日付を設定
        if(diff <= 0){
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        }

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(context.getString(R.string.alarm_id), item.getAlarmId());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, item.getAlarmId(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        if(alarmIntent == null) return;
        Log.i(TAG, "SetAlarm" + " id: " + item.getAlarmId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmMgr.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), null), alarmIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }
    }

    public static void cancelAlarm(Context context, ListItem item) {
        AlarmManager alarmMrg = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, item.getAlarmId() , intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        if(alarmIntent == null) return;
        Log.i(TAG, "CancelAlarm" + " id: " + item.getAlarmId());
        alarmMrg.cancel(alarmIntent);
    }

    public static long getTimeDiff(ListItem listItem) {
        if(listItem == null) return 0;
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(listItem.getHour()));
        calendar.set(Calendar.MINUTE, Integer.parseInt(listItem.getMinute()));
        calendar.set(Calendar.SECOND, 0);

        int diff = calendar.compareTo(nowCalendar);
        if(diff <= 0) calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);

        Duration duration = Duration.between(nowCalendar.getTime().toInstant(), calendar.getTime().toInstant());
        return TimeUnit.MINUTES.convert(duration.getSeconds(), TimeUnit.SECONDS);
    }

    public static String getSelectMusicUri(ArrayList<MusicListItem> data) {
        String playMusicUri = null;
        for(MusicListItem listItem: data) {
            if(!listItem.isHeader() || !listItem.isFooter()) {
                if (listItem.isSelectMusic()) {
                    playMusicUri = listItem.getMusicUri();
                }
            }
        }
        return playMusicUri;
    }

    @SuppressLint("Recycle")
    public static boolean isContainMusic(MusicDataHelper helper,String musicUri){
        try(SQLiteDatabase db = helper.getReadableDatabase()) {
            String[] sqlUri = {musicUri};
            String[] per = {"musicUri"};
            Cursor cs = db.query("musicList", per, "musicUri = ?", sqlUri, null, null, "musicId", null);
            if(cs != null && cs.moveToFirst()) {
                return true;
            }
        }
        return false;
    }



    public static String removeFileNameExtension(String fileName) {
        if(fileName == null) return null;
        int point = fileName.lastIndexOf(".");
        if(point > -1) {
            return fileName.substring(0, point);
        }
        return fileName;
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        return new File(Objects.requireNonNull(getPathFromUri(context, uri))).getName();
    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        boolean isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        Log.e(TAG,"uri:" + uri.getAuthority());
        if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(
                    uri.getAuthority())) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }else {
                    return "/stroage/" + type +  "/" + split[1];
                }
            }else if ("com.android.providers.downloads.documents".equals(
                    uri.getAuthority())) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            }else if ("com.android.providers.media.documents".equals(
                    uri.getAuthority())) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                contentUri = MediaStore.Files.getContentUri("external");
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())) {//MediaStore
            return getDataColumn(context, uri, null, null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String[] projection = {
                MediaStore.Files.FileColumns.DATA
        };
        try (Cursor cursor = context.getContentResolver().query(
                uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int cindex = cursor.getColumnIndexOrThrow(projection[0]);
                return cursor.getString(cindex);
            }
        }
        return null;
    }

    public static String getYoutubeVideoId(String url) {
        if(url.contains("youtube.com/watch?v=")){
            int firstPoint = url.lastIndexOf("watch?v=") + 1;
            int secondPoint = url.indexOf("&")-1;
            return url.substring(firstPoint, secondPoint);
        }

        else if(url.contains("https://youtu.be/")) {
            int point = url.lastIndexOf("/") + 1;
            return url.substring(point);
        }
        return  url;
    }
}
