package com.apps.um.alarmplus.tabs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.ToolbarListener;
import com.apps.um.alarmplus.listcomponent.MusicListAdapter;
import com.apps.um.alarmplus.listcomponent.MusicListItem;
import com.apps.um.alarmplus.util.DatabaseHelper;
import com.apps.um.alarmplus.util.MusicDataHelper;
import com.apps.um.alarmplus.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class TabsAdapter extends FragmentStateAdapter {

    private static final int Pages = 2;
    private Context context;
    private Uri musicUri;
    private String nowSelectUri;
    private Fragment fragment;

    private ToolbarListener toolbarListener;

    public static class TYPE_FRAGMENT {
        public static final int Tab1 = 1;
        public static final int Tab2 = 2;
    }

    public TabsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.context = fragmentActivity;
    }


    public TabsAdapter(@NonNull FragmentActivity fragmentActivity, int alarmId) {
        super(fragmentActivity);
        this.context = fragmentActivity;
        this.nowSelectUri = Util.getAlarmsByID(DatabaseHelper.getInstance(context), alarmId).getMusicUri();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        fragment = null;
        Bundle bundle = new Bundle();
        FloatingActionButton fab = ((Activity) context).findViewById(R.id.music_fab);
        if (getItemViewType(position) == TYPE_FRAGMENT.Tab1) {
            fragment = new MusicTabs1(context, loadMusicListData());
            ((MusicTabs1) fragment).setNavOnClickListener(musicUri -> toolbarListener.onNavClick(musicUri));
        } else if (getItemViewType(position) == TYPE_FRAGMENT.Tab2){
            fragment = new MusicTabs2(context);
        }
        /*bundle.putInt("position", position);
        fragment.setArguments(bundle);*/
        return Objects.requireNonNull(fragment);
    }

    @Override
    public int getItemCount() {
        return Pages;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) return TYPE_FRAGMENT.Tab1;
        else if(position == 1) return TYPE_FRAGMENT.Tab2;
        return 0;
    }


    public String getTitlePage(int pos){
        String title = null;
        if(pos == 0) title = "マイミュージック";
        else if(pos == 1) title = "効果音";
        return title;
    }

    public Fragment getFragment() {
        if(fragment != null) return fragment;
        return null;
    }

    @SuppressLint("Recycle")
    private ArrayList<MusicListItem> loadMusicListData() {
        MusicDataHelper musicDataHelper = MusicDataHelper.getInstance(context);
        ArrayList<MusicListItem> musicListData = new ArrayList<>();

        musicListData.add(new MusicListItem(MusicListAdapter.TYPE_ITEM.Header));

        try(SQLiteDatabase db = musicDataHelper.getReadableDatabase()) {
            String[] per = {"musicId", "musicFileName", "musicUri"};
            Cursor cs = db.query("musicList", per, null, null, null, null, "musicId", null);
            if (cs.moveToFirst()) {
                do {
                    MusicListItem musicListItem = new MusicListItem(cs.getInt(0), cs.getString(1), cs.getString(2), false);
                    if(nowSelectUri != null && Objects.equals(musicListItem.getMusicUri(), nowSelectUri)) musicListItem.setSelectMusic(true);
                    musicListData.add(musicListItem);
                } while (cs.moveToNext());
            }
        }

        musicListData.add(new MusicListItem(MusicListAdapter.TYPE_ITEM.Footer));
        return musicListData;
    }

    public void setNavOnClickListener(ToolbarListener toolbarListener) {
        this.toolbarListener = toolbarListener;
    }
}
