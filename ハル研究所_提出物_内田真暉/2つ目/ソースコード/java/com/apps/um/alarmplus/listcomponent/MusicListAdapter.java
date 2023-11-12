package com.apps.um.alarmplus.listcomponent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.ToolbarListener;
import com.apps.um.alarmplus.util.Util;

import java.util.ArrayList;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

    public static final int READ_REQUEST_CODE = 42;

    private final ArrayList<MusicListItem> musicData;
    private Context context;
    private Toolbar toolbar;
    private ToolbarListener toolbarListener;

    public static class TYPE_ITEM {
        public static final int Header = 0;
        public static final int Normal = 1;
        public static final int Footer = 2;
    }


    public MusicListAdapter(Context context, ArrayList<MusicListItem> musicData) {
        this.musicData = musicData;
        this.context = context;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case TYPE_ITEM.Header:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_music_header, parent, false);
                break;
            case TYPE_ITEM.Footer:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_music_footer, parent, false);
                break;
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_music, parent, false);
        }

        toolbar = ((Activity)context).findViewById(R.id.toolbarConfirmMusic);
        toolbar.setNavigationIcon(R.drawable.ic_back_left);
        toolbar.setNavigationOnClickListener(view -> toolbarListener.onNavClick(Util.getSelectMusicUri(musicData)));

        return new MusicListViewHolder(v, viewType);
    }



    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int position) {
        int modPosition = holder.getLayoutPosition();

        if(getItemViewType(position) == TYPE_ITEM.Header) {
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(context.getString(R.string.request_code), READ_REQUEST_CODE);
                intent.setType("audio/*");
                ((Activity) context).startActivityForResult(Intent.createChooser(intent, "インポートできる音楽一覧"), 1);
            });
            return;
        }
        if(getItemViewType(position) == TYPE_ITEM.Footer) return;

        holder.radioButton.setChecked(musicData.get(position).isSelectMusic());
        holder.radioButton.setText(Util.removeFileNameExtension(musicData.get(position).getMusicFileName()));
        holder.radioButton.setOnClickListener(view -> {
            if(!musicData.get(position).isSelectMusic()) {
                changeRadioButton(musicData.get(position));
                notifyItemRangeChanged(0,getItemCount());
            }
        });

        holder.vertButton.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return musicData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(this.musicData.get(position).isHeader()) return TYPE_ITEM.Header;
        else if (this.musicData.get(position).isFooter()) return TYPE_ITEM.Footer;
        else return TYPE_ITEM.Normal;

    }

    private void changeRadioButton(MusicListItem changeItem) {
        for(MusicListItem listItem: musicData) {
            if(listItem.isFooter()) continue;
            listItem.setSelectMusic(listItem == changeItem);
        }
    }

    public void setNavOnClickListener(ToolbarListener toolbarListener) {
        this.toolbarListener = toolbarListener;
    }

}


