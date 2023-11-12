package com.apps.um.alarmplus.listcomponent;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.um.alarmplus.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.cachapa.expandablelayout.ExpandableLayout;


public class ListViewHolder extends RecyclerView.ViewHolder {

    TextView next_alarm; //Header
    FloatingActionButton selectCancelButton; //SelectModeHeader
    FloatingActionButton selectDeleteButton;
    TextView selectText;
    TextView alarmLabel; //Normal
    TextView timeText;
    TextView inputMusicText;
    TimePicker timePicker;
    SwitchCompat toggle;
    ExpandableLayout expandableLayout;
    AppCompatButton deleteButton;
    AppCompatButton saveButton;
    LinearLayout musicSelButton;
    FloatingActionButton editLabelButton;
    FloatingActionButton copyAlarmButton;

    ListViewHolder(View itemView, int viewType){
        super(itemView);
        if(viewType == ListAdapter.TYPE_ITEMS.Header) {
            this.next_alarm = itemView.findViewById(R.id.next_alarm);
        } else if(viewType == ListAdapter.TYPE_ITEMS.Normal) {
            this.alarmLabel = itemView.findViewById(R.id.alarm_label_text);
            this.timeText = itemView.findViewById(R.id.time);
            this.timePicker = itemView.findViewById(R.id.time_picker);
            this.toggle = itemView.findViewById(R.id.toggle_switch);
            this.deleteButton = itemView.findViewById(R.id.input_action_delete);
            this.saveButton = itemView.findViewById(R.id.input_action_save);
            this.musicSelButton = itemView.findViewById(R.id.music_sel_button);
            this.inputMusicText = itemView.findViewById(R.id.input_musicName);
            this.editLabelButton = itemView.findViewById(R.id.edit_label);
            this.copyAlarmButton = itemView.findViewById(R.id.copy_alarm);
            this.expandableLayout = itemView.findViewById(R.id.expandable_layout);
        } else if(viewType == ListAdapter.TYPE_ITEMS.SelectModeHeader) {
            this.selectCancelButton = itemView.findViewById(R.id.select_cancel_button);
            this.selectDeleteButton = itemView.findViewById(R.id.select_delete_button);
            this.selectText = itemView.findViewById(R.id.select_text);
        } else if(viewType == ListAdapter.TYPE_ITEMS.Footer) {
        }
    }
}
