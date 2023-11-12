package com.apps.um.alarmplus.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.databinding.ActivityInputBinding;
import com.apps.um.alarmplus.listcomponent.ListItem;
import com.apps.um.alarmplus.util.DatabaseHelper;
import com.apps.um.alarmplus.util.Util;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class InputActivity extends AppCompatActivity {
    private final String TAG = "InputActivity";

    private ActivityInputBinding binding;

    private TimePicker timePicker = null;
    private DatabaseHelper helper = null;
    private EditText editTextLabel = null;

    private AppCompatButton button;
    Intent returnIntent = null;

    TextView musicTextView;

    String musicUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInputBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        timePicker = binding.timePicker;
        editTextLabel = binding.editAlarmText;
        musicTextView = binding.inputMusicName;
        button = binding.inputSaveButton;

        helper = DatabaseHelper.getInstance(this);

        // キャンセルボタンの設定
        Toolbar toolbar = binding.toolbarInput;
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            Intent i = new Intent();
            setResult(RESULT_CANCELED, i);
            finish();
        });
        // 保存ボタンの設定

        timePicker.setIs24HourView(true);
        Calendar c = Calendar.getInstance();
        timePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(c.get(Calendar.MINUTE) + 1);


        ActivityResultLauncher<Intent> MusicSelLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_CANCELED) {
            } else if(result.getResultCode() == RESULT_OK) {
                Intent i = result.getData();
                if (i != null && (i.getIntExtra(getString(R.string.request_code), -1) != MainActivity.EDIT_REQ_CODE) ) {
                    musicUri = i.getStringExtra(getString(R.string.select_music_uri));
                    String musicFileName = new File(Objects.requireNonNull(Util.getPathFromUri(this, Uri.parse(musicUri)))).getName();
                    musicTextView.setText(Util.removeFileNameExtension(musicFileName));
                }
            }
        });

        LinearLayout linearLayout = binding.musicSelButton;
        linearLayout.setOnClickListener(view -> {
            Intent i = new Intent(InputActivity.this, MusicSelectActivity.class);
            i.putExtra(getString(R.string.request_code) ,MainActivity.NEW_REQ_CODE);
            MusicSelLauncher.launch(i);
        });

        button.setOnClickListener(v -> {
            saveAlarm();
            returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }

    private void saveAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // アラームラベルの設定
        String alarmLabel = editTextLabel.getText().toString();

        // 時刻登録の準備
        String alarmTime = String.format(Locale.US, "%02d", hour) + ":" + String.format(Locale.US, "%02d", minute);

        int alarmId = helper.createData(alarmTime, musicUri, true, alarmLabel);

        ListItem listItem = new ListItem(alarmId, alarmTime, musicUri, true, alarmLabel);
        Util.setAlarm(InputActivity.this, listItem);

        Toast.makeText(InputActivity.this, R.string.alarm_save_msg, Toast.LENGTH_SHORT).show();
    }
}