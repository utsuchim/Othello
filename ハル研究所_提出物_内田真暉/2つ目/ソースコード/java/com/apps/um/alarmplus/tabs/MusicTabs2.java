package com.apps.um.alarmplus.tabs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.listcomponent.MusicListAdapter;
import com.apps.um.alarmplus.listcomponent.MusicListItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class MusicTabs2 extends Fragment {
    private Context context;

    private RecyclerView musicRV;
    private RecyclerView.Adapter musicAdapter;
    private FloatingActionButton fab;
    

    MusicTabs2(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = ((Activity) context).findViewById(R.id.music_fab);
        //setMusicRV(view, getSong());
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setVisibility(View.GONE);
    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        try {
            File[] files = file.listFiles();
            if(files != null) {
                for(File singleFile: files) {
                    if(singleFile.isDirectory() && !singleFile.isHidden()) {
                        arrayList.addAll(findSong(singleFile));
                    } else {
                        if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".m4a")) {
                            arrayList.add(singleFile);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /*public ArrayList<MusicListItem> getSong() {
        ArrayList<File> musicFileList = findSong(Environment.getExternalStorageDirectory());
        ArrayList<MusicListItem> musicList = new ArrayList<>();
        
        MusicListItem header = new MusicListItem();
        header.setHeader(true);
        musicList.add(header);
        
        for(int i = 0;i < musicFileList.size(); i++) {
            MusicListItem item = new MusicListItem();
            item.setMusicName(musicFileList.get(i).getName());
            musicList.add(item);
        }
        
        MusicListItem footer = new MusicListItem();
        footer.setFooter(true);
        musicList.add(footer);
        
        return musicList;
    }*/
    
    /*private void setMusicRV(View view, ArrayList<MusicListItem> data) {
        musicRV = view.findViewById(R.id.rv_music2);
        musicRV.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(musicRV.getContext(), new LinearLayoutManager(getActivity()).getOrientation());
        musicRV.addItemDecoration(decoration);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        musicRV.setLayoutManager(manager);
        musicAdapter = new MusicListAdapter(context, data);
        musicRV.setAdapter(musicAdapter);
    }*/

}
