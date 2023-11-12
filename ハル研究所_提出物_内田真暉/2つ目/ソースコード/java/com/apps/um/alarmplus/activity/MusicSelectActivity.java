package com.apps.um.alarmplus.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.ToolbarListener;
import com.apps.um.alarmplus.databinding.ActivityMusicSelectBinding;
import com.apps.um.alarmplus.listcomponent.MusicListAdapter;
import com.apps.um.alarmplus.listcomponent.MusicListItem;
import com.apps.um.alarmplus.tabs.TabsAdapter;
import com.apps.um.alarmplus.util.MusicDataHelper;
import com.apps.um.alarmplus.util.Util;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Objects;

public class MusicSelectActivity extends AppCompatActivity {
    private final String TAG = "MusicSelectActivity";
    private TabsAdapter tabsAdapter;
    private ViewPager2 viewPager2;
    private int requestCode;
    private int alarmId;
    private int position;
    private TabLayout tabLayout;
    private ActivityMusicSelectBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMusicSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"起動できません", Toast.LENGTH_SHORT).show();
            finish();
        }

        requestCode = getIntent().getIntExtra(getString(R.string.request_code), -1);
        alarmId = getIntent().getIntExtra(getString(R.string.alarm_id), -1);
        position = getIntent().getIntExtra(getString(R.string.rv_position),-1);


        viewPager2 = binding.musicViewPager;
        tabLayout = binding.musicTabLayout;
        if(requestCode == MainActivity.NEW_REQ_CODE) tabsAdapter = new TabsAdapter(this);
        else if(requestCode == MainActivity.EDIT_REQ_CODE) tabsAdapter = new TabsAdapter(this, alarmId);
        tabsAdapter.setNavOnClickListener(this::setFinish);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setAdapter(tabsAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(tabsAdapter.getTitlePage(position))).attach();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && (data != null ? data.getIntExtra(getString(R.string.request_code), MusicListAdapter.READ_REQUEST_CODE) : 0) == MusicListAdapter.READ_REQUEST_CODE) {
            MusicDataHelper helper = MusicDataHelper.getInstance(this);
            String musicUri = data.getData().toString();
            String fileName = Objects.requireNonNull(DocumentFile.fromSingleUri(this, Uri.parse(musicUri))).getName();
            Log.i(TAG, musicUri + " " + fileName);
            if(Util.isContainMusic(helper, musicUri)) {
                Toast.makeText(this, "すでにインポート済みです", Toast.LENGTH_LONG).show();
                return;
            }
            helper.createMusicData(fileName,musicUri);
            tabsAdapter = new TabsAdapter(this, alarmId);
            viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
            viewPager2.setAdapter(tabsAdapter);
        }
    }

    private void setFinish(String musicUri) {
        Intent i = new Intent();
        if(musicUri != null) {
            i.putExtra(getString(R.string.select_music_uri), musicUri);
            if(requestCode == MainActivity.EDIT_REQ_CODE) {
                i.putExtra(getString(R.string.request_code), requestCode);
                i.putExtra(getString(R.string.rv_position), position);
            }
            setResult(Activity.RESULT_OK, i);
        } else {
            if(requestCode == MainActivity.EDIT_REQ_CODE) {
                i.putExtra(getString(R.string.request_code), requestCode);
                i.putExtra(getString(R.string.rv_position), position);
            }
            setResult(Activity.RESULT_CANCELED, i);
        }
        finish();
    }
}
