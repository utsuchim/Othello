package com.apps.um.alarmplus.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.activity.WakeUpActivity;
import com.apps.um.alarmplus.service.SoundService;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private Context context;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;


        /*PowerManager powerManager = ((PowerManager) context.getSystemService(Context.POWER_SERVICE));
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE, "AlarmX");
        wakeLock.acquire(10*60*1000L *//*10 minutes*//*);
        wakeLock.release();*/

        int alarmId = intent.getIntExtra(context.getString(R.string.alarm_id), -1);

        Intent startAlarmActivity = new Intent(context, WakeUpActivity.class);
        startAlarmActivity.putExtra(context.getString(R.string.alarm_id), alarmId);
        startAlarmActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startAlarmActivity);

        Intent musicService = new Intent(context, SoundService.class);
        musicService.putExtra(context.getString(R.string.alarm_id), alarmId);
        context.startService(musicService);

        /*final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                timer.cancel();
                timer.purge();
                if (AlarmReceiver.this.wakeLock != null) {
                    AlarmReceiver.this.wakeLock.release();
                    AlarmReceiver.this.wakeLock = null;
                }
            }
        }, 3000L);*/

    }
}