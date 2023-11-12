package com.apps.um.alarmplus.listcomponent;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.um.alarmplus.R;


public class MusicListViewHolder extends RecyclerView.ViewHolder {
    RadioButton radioButton;
    LinearLayout vertButton;

    MusicListViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        if(viewType == MusicListAdapter.TYPE_ITEM.Header) {
        } else if(viewType == MusicListAdapter.TYPE_ITEM.Normal) {
            this.radioButton = itemView.findViewById(R.id.radioButton);
            this.vertButton = itemView.findViewById(R.id.music_list_item_vert);
        } else if(viewType == MusicListAdapter.TYPE_ITEM.Footer) {
        }
    }
}
