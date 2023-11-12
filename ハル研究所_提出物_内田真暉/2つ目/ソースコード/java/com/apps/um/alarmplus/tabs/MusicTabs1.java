package com.apps.um.alarmplus.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.ToolbarListener;
import com.apps.um.alarmplus.listcomponent.MusicListAdapter;
import com.apps.um.alarmplus.listcomponent.MusicListItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MusicTabs1 extends Fragment {

    private final int READ_REQUEST_CODE = 42;
    private Context context;

    private RecyclerView musicRV;
    private MusicListAdapter musicAdapter;
    private View viewTab1;

    private ArrayList<MusicListItem> musicListData;

    private FloatingActionButton fab;

    private ToolbarListener toolbarListener;


    public MusicTabs1(Context context, ArrayList<MusicListItem> musicListData) {
        this.context = context;
        this.musicListData = musicListData;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewTab1 = inflater.inflate(R.layout.fragment_music1, container, false);
        return viewTab1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = ((Activity) context).findViewById(R.id.music_fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(context.getString(R.string.request_code), READ_REQUEST_CODE);
            intent.setType("audio/*");
            ((Activity) context).startActivityForResult(Intent.createChooser(intent, "インポートできる音楽一覧"), 1);
        });
        setMusicRV();
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    private void setMusicRV() {
        musicRV = viewTab1.findViewById(R.id.rv_music1);
        musicRV.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView.ItemAnimator itemAnimator = musicRV.getItemAnimator();
        if(itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        musicAdapter = new MusicListAdapter(context, musicListData);
        musicAdapter.setNavOnClickListener(musicUri -> toolbarListener.onNavClick(musicUri));

        musicRV.setLayoutManager(manager);
        musicRV.setAdapter(musicAdapter);
    }

    public void setNavOnClickListener(ToolbarListener toolbarListener) {
        this.toolbarListener = toolbarListener;
    }


    /*private void setNavClickListener(String musicUri) {
        Intent i = new Intent();
        if(musicUri != null) {
            i.putExtra("musicUri", musicUri);
            if(requestCode == MainActivity.EDIT_REQ_CODE) {
                i.putExtra(getString(R.string.request_code), MainActivity.EDIT_REQ_CODE);
                i.putExtra(getString(R.string.rv_position), context.getIntExtra(getString(R.string.rv_position), -1));
            }
            setResult(Activity.RESULT_OK, i);
        } else {
            if(requestCode == MainActivity.EDIT_REQ_CODE) {
                i.putExtra(getString(R.string.request_code), MainActivity.EDIT_REQ_CODE);
                i.putExtra(getString(R.string.rv_position), getIntent().getIntExtra(getString(R.string.rv_position), -1));
            }
            setResult(Activity.RESULT_CANCELED, i);
        }
        finish();*/
}
