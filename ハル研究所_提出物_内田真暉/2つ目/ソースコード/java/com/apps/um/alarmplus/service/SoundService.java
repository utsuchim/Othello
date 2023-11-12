package com.apps.um.alarmplus.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.listcomponent.ListItem;
import com.apps.um.alarmplus.util.DatabaseHelper;
import com.apps.um.alarmplus.util.Util;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;


public class SoundService extends Service implements MediaPlayer.OnCompletionListener{
    private final String TAG = "SoundService";

    MediaPlayer mediaPlayer;
    int alarmId;
    DatabaseHelper helper;
    AudioManager audioManager;

    int bVolume;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmId = intent.getIntExtra(getString(R.string.alarm_id), -1);
        helper = DatabaseHelper.getInstance(this);
        ListItem listItem = Util.getAlarmsByID(helper, alarmId);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        bVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 4, AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);

        if(listItem.getMusicUri() == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.doramaturugi);
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.start();
        } else {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(Util.getPathFromUri(this, Uri.parse(listItem.getMusicUri())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "非同期エラー");
                Toast.makeText(SoundService.this, R.string.async_error, Toast.LENGTH_SHORT).show();
                return false;
            });
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.prepareAsync();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bVolume, 0);
        audioManager = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.release();
        mediaPlayer = null;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bVolume, 0);
        audioManager = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
