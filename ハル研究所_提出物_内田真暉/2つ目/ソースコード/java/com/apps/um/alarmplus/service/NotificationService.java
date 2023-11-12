package com.apps.um.alarmplus.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.activity.MainActivity;
import com.apps.um.alarmplus.listcomponent.ListItem;
import com.apps.um.alarmplus.util.DatabaseHelper;
import com.apps.um.alarmplus.util.Util;

public class NotificationService extends Service {
    private NotificationManager notificationManager;
    DatabaseHelper helper;
    String channelId = "Notification_Id";
    int alarmId;

    private final int FORE_ID = 1;

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(notificationManager.getNotificationChannel(channelId) == null) {
            createNotification(channelId);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        helper = DatabaseHelper.getInstance(this);
        alarmId = intent.getIntExtra(getString(R.string.alarm_id), -1);
        startNotification(helper, alarmId);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        //return mBind;
    }


    public void startNotification(DatabaseHelper helper,int alarmId) {
        ListItem listItem = Util.getAlarmsByID(helper, alarmId);

        Intent notificationClick = new Intent(this, MainActivity.class);
        notificationClick.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationClick, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("アラームの時間: " + listItem.getTime())
                .setSmallIcon(R.drawable.icon2)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        if(notificationManager != null) {
            startForeground(FORE_ID, notification);
        }
    }

    private void createNotification(String channelId) {

        CharSequence name = "サービス起動中の通知"; // なんでもOK。
        String description = "サービス起動中の通知は、AlarmXのための通知です。"; // なんでもOK。

        NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);

        // 通知の登録
        notificationManager.createNotificationChannel(channel);
    }



}
