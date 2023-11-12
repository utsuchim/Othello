package com.apps.um.alarmplus.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.TintableCheckedTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.apps.um.alarmplus.MusicSelectListener;
import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.databinding.ActivityMainBinding;
import com.apps.um.alarmplus.listcomponent.ListAdapter;
import com.apps.um.alarmplus.listcomponent.ListItem;
import com.apps.um.alarmplus.util.DatabaseHelper;
import com.apps.um.alarmplus.util.ListItemManager;
import com.apps.um.alarmplus.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_REQ_CODE = 1;
    public static final int EDIT_REQ_CODE = 2;

    private ActivityMainBinding binding;
    private AlertDialog overlayDialog = null;
    private RecyclerView recyclerView = null;
    private ListAdapter adapter = null;
    private ConstraintLayout constraintLayout;
    private FloatingActionButton fbt;
    private Toolbar toolbar;
    private ActivityResultLauncher<Intent> startInputActivity;
    private ActivityResultLauncher<Intent> startMusicSelActivity;

    ArrayList<ListItem> data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startInputActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_CANCELED) {
            } else if(result.getResultCode() == RESULT_OK) {
                if(result.getData() != null) {
                    ArrayList<ListItem> dataAlarms = this.loadAlarms();
                    this.updateRV(dataAlarms);
                }
            }
        });

        startMusicSelActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if((result.getData() == null) || (result.getData().getIntExtra(getString(R.string.request_code), -1) != EDIT_REQ_CODE)) return;
            if(result.getResultCode() == RESULT_CANCELED) {
                int position = result.getData().getIntExtra(getString(R.string.rv_position), -1);
                adapter.toggleAlarmsExpand(position);
            } else if(result.getResultCode() == RESULT_OK) {
                String musicUri = result.getData().getStringExtra("musicUri");
                int position = result.getData().getIntExtra(getString(R.string.rv_position), -1);
                adapter.setAlarmsMusicUri(position, musicUri);
            }
        });

        fbt = binding.fbtn;
        fbt.setOnClickListener( v1 -> {
            Intent i = new Intent(this, InputActivity.class);
            i.putExtra(getString(R.string.request_code),NEW_REQ_CODE);
            startInputActivity.launch(i);
        });

        this.constraintLayout = new ConstraintLayout(this);

        data = this.loadAlarms();
        this.setRV(data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length == 0) return;
        if(requestCode == 1) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            } else {
                Toast.makeText(this, "アプリを起動できません。", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlertDialog alertDialog = this.overlayDialog;
        if(alertDialog == null) {
            this.overlayDialog = showDrawDialog();
        } else if(!alertDialog.isShowing()) {
            this.overlayDialog = showDrawDialog();
        }
    }

    @SuppressLint("Recycle")
    private ArrayList<ListItem> loadAlarms(){
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        ArrayList<ListItem> data = new ArrayList<>();

        data.add(new ListItem(ListAdapter.TYPE_ITEMS.Header));

        try(SQLiteDatabase db = helper.getReadableDatabase()) {
            String[] per = {"alarmId","alarmTime","musicUri", "toggle","label"};
            Cursor cs = db.query("alarms",per,null,null, null,null,"alarmId",null);
            if(cs.moveToFirst()) {
                do {
                    ListItem item = new ListItem(cs.getInt(0),cs.getString(1),cs.getString(2), Boolean.parseBoolean(cs.getString(3)), cs.getString(4));
                    data.add(item);
                }while (cs.moveToNext());
            }
        }

        data.add(new ListItem(ListAdapter.TYPE_ITEMS.Footer));

        return data;
    }

    private void setRV(ArrayList<ListItem> data){
        recyclerView = binding.mainRv;
        recyclerView.setHasFixedSize(true);

        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if(itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new ListAdapter(this, data);
        adapter.setMusicSelectListener(this::startMusicActivity);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    private void updateRV(ArrayList<ListItem> data){
        adapter = new ListAdapter(this, data);
        adapter.setMusicSelectListener(this::startMusicActivity);
        recyclerView.setAdapter(adapter);
    }


    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    });

    private AlertDialog showDrawDialog() {
        if(Settings.canDrawOverlays(getApplicationContext())) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_message));
        builder.setPositiveButton(getString(R.string.dialog_ok), (dialogInterface, i) -> {
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            intent.putExtra("packageName", Uri.parse("package" + getPackageName()));
            activityResultLauncher.launch(intent);
        });

        builder.setOnKeyListener((dialogInterface, i, keyEvent) -> i == 84);
        builder.setCancelable(false);
        builder.create();
        return builder.show();
    }

    private void startMusicActivity(int position, int alarmId) {
        Intent intent  = new Intent(this, MusicSelectActivity.class);
        intent.putExtra(getString(R.string.request_code), EDIT_REQ_CODE);
        intent.putExtra(getString(R.string.alarm_id), alarmId);
        intent.putExtra(getString(R.string.rv_position), position);
        startMusicSelActivity.launch(intent);
    }


}