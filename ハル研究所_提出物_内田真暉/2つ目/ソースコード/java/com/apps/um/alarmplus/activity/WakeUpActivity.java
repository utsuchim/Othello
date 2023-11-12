package com.apps.um.alarmplus.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.databinding.ActivityWakeUpBinding;
import com.apps.um.alarmplus.listcomponent.ListItem;
import com.apps.um.alarmplus.service.SoundService;
import com.apps.um.alarmplus.util.DatabaseHelper;
import com.apps.um.alarmplus.util.Util;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import ng.max.slideview.SlideView;


public class WakeUpActivity extends AppCompatActivity {

    private SlideView stopButton;
    //private AppCompatButton stopButton;
    private DatabaseHelper helper;
    private ActivityWakeUpBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWakeUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setShowWhenLocked(true);
        setTurnScreenOn(true);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardManager.requestDismissKeyguard(this, null);

        TextView dateView = findViewById(R.id.dateWakeUp);
        TextView timeView = findViewById(R.id.timeWakeUp);

        int alarmId = getIntent().getIntExtra(getString(R.string.alarm_id), -1);
        helper = DatabaseHelper.getInstance(this);
        ListItem listItem = Util.getAlarmsByID(helper, alarmId);

        Calendar calendar = Calendar.getInstance();
        dateView.setText(getString(R.string.wakeUp_text_date, calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)));
        timeView.setText(getString(R.string.wakeUp_text_time, listItem.getTime()));

        /*stopButton = findViewById(R.id.button_alarm_stop);
        stopButton.setOnClickListener(v -> {
            stopService(new Intent(this, SoundService.class));
            helper.updateToggle(alarmId, false);
            Toast.makeText(this, "アラームが停止しました。", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });*/

        stopButton = binding.slideButton;
        stopButton.setOnSlideCompleteListener(slideView -> {
            stopService(new Intent(this, SoundService.class));
            helper.updateToggle(alarmId, false);
            Toast.makeText(this, "アラームが停止しました。", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        setShowWhenLocked(false);
        setTurnScreenOn(false);
    }

}