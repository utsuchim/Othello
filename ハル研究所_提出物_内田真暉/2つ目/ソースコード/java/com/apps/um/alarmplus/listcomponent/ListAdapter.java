package com.apps.um.alarmplus.listcomponent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.um.alarmplus.MusicSelectListener;
import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.SoftKeyBoardListener;
import com.apps.um.alarmplus.activity.MusicSelectActivity;
import com.apps.um.alarmplus.dialog.DialogEditLabel;
import com.apps.um.alarmplus.service.NotificationService;
import com.apps.um.alarmplus.tabs.TabsAdapter;
import com.apps.um.alarmplus.util.DatabaseHelper;
import com.apps.um.alarmplus.util.ListItemManager;
import com.apps.um.alarmplus.util.Util;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private final String TAG = "AlarmListAdapter";

    private final ListItemManager manager;
    private final Context context;
    private final FragmentManager fragmentManager;
    private FloatingActionButton fbt;
    private BottomAppBar bottomAppBar;
    private final HashMap<Integer, Integer> editData = new HashMap<>();// <alarmId, position>
    public boolean editMode = false;

    private MusicSelectListener musicSelectListener;

    private SoftKeyBoardListener softKeyBoardListener;

    public static class TYPE_ITEMS {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
        public static final int SelectModeHeader = 4;
    }

    private final int HEADER = 0;

    public ListAdapter(Context context, ArrayList<ListItem> data){
        this.context = context;
        this.manager = new ListItemManager(context, data);
        this.fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.i(TAG, "CreateViewHolder");
        bottomAppBar = ((Activity) context).findViewById(R.id.bottom_app_toolbar);
        fbt = ((Activity) context).findViewById(R.id.fbtn);
        View listView;
        switch (viewType) {
            case TYPE_ITEMS.Header:
                listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent,false);
                break;
            case TYPE_ITEMS.Footer:
                listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_footer, parent,false);
                break;
            case TYPE_ITEMS.SelectModeHeader:
                listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_select_mode, parent, false);
                break;
            default:
                listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        }
        return new ListViewHolder(listView, viewType);
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Log.i(TAG, "BindViewHolder");
        int modPosition = holder.getLayoutPosition();
        manager.setMangerListener(() -> notifyItemMoved(1, getItemCount()-1));

        if(getItemViewType(position) == TYPE_ITEMS.Footer) return;
        if(getItemViewType(position) == TYPE_ITEMS.Header) {
            if(editMode) {
                holder.next_alarm.setText(null);
            } else {
                if(manager.getNextAlarm() != null) {
                    int hour = (int) (Util.getTimeDiff(manager.getNextAlarm()) / 60);
                    int minute = (int) (Util.getTimeDiff(manager.getNextAlarm()) % 60);
                    if(hour <= 0) {
                        if(minute <= 0) holder.next_alarm.setText(context.getString(R.string.next_alarm_no_minute));
                        else holder.next_alarm.setText(context.getString(R.string.next_alarm_minute, String.valueOf(minute)));
                    } else {
                        holder.next_alarm.setText(context.getString(R.string.next_alarm, String.valueOf(hour), String.valueOf(minute)));
                    }
                } else {
                    holder.next_alarm.setText(context.getString(R.string.next_alarm_no));
                }
            }
            return;
        }

        if(getItemViewType(position) == TYPE_ITEMS.SelectModeHeader) {
            holder.selectText.setText(context.getString(R.string.select_toolbar_title, editData.size()));
            holder.selectCancelButton.setOnClickListener(v -> {
                editMode = false;
                manager.cancelSelectMode();
                fbt.setVisibility(View.VISIBLE);
                bottomAppBar.setVisibility(View.VISIBLE);
                editData.clear();
                notifyDataSetChanged();
            });

            holder.selectDeleteButton.setOnClickListener(v -> {
                if(editData.size() == 0) {
                    Toast.makeText(context, "アラームが選択されていません",Toast.LENGTH_SHORT).show();
                } else {
                    manager.deleteRangeAlarms(editData);
                    Toast.makeText(context, context.getString(R.string.alarm_select_delete_msg, editData.size()),Toast.LENGTH_SHORT).show();
                    editMode = false;
                    manager.cancelSelectMode();
                    fbt.setVisibility(View.VISIBLE);
                    bottomAppBar.setVisibility(View.VISIBLE);
                    editData.clear();
                    notifyDataSetChanged();
                }
            });

            return;
        }


        Log.i(TAG, "Id: " + manager.getListItem(position).getAlarmId());

        if(!editMode) {
            ((CardView) holder.itemView).setCardBackgroundColor(context.getColor(R.color.toggle_on_back));
            if(manager.getListItem(position).isToggle()) {
                toggleListItem(holder, 1.0f, R.color.toggle_on);
            } else {
                toggleListItem(holder, 0.5f, R.color.toggle_off);
            }
            holder.toggle.setClickable(true); holder.toggle.setFocusable(true);
            manager.getListItem(position).setSelectMode(false);
        } else {
            if(manager.getListItem(position).isSelectMode()) {
                ((CardView) holder.itemView).setCardBackgroundColor(context.getColor(R.color.select_alarm));
                Log.i(TAG, "select! pos: " + position);
            }else {
                ((CardView) holder.itemView).setCardBackgroundColor(context.getColor(R.color.toggle_on_back));
                Log.i(TAG, "don't select! pos: " + position);
            }
        }

        if(manager.getListItem(position).isExpanded()) holder.expandableLayout.expand(false);
        if(manager.getListItem(position).getAlarmLabel() != null) holder.alarmLabel.setText(manager.getListItem(position).getAlarmLabel());

        holder.timePicker.setIs24HourView(true);
        holder.timePicker.setHour(Integer.parseInt(manager.getListItem(position).getHour()));
        holder.timePicker.setMinute(Integer.parseInt(manager.getListItem(position).getMinute()));

        if(manager.getListItem(position).getMusicUri() != null) {
            String musicName = Util.removeFileNameExtension(new File(Objects.requireNonNull(Util.getPathFromUri(context, Uri.parse(manager.getListItem(position).getMusicUri())))).getName());
            holder.inputMusicText.setText(musicName);
        }

        holder.timeText.setText(manager.getListItem(position).getTime());
        holder.toggle.setChecked(manager.getListItem(position).isToggle());

        //ホルダークリック時
        holder.itemView.setOnClickListener(v1 -> {
            Log.i(TAG, "ListItemClick");
            if(editMode) {
                holder.toggle.setClickable(false); holder.toggle.setFocusable(false);
                if(manager.getListItem(modPosition).isSelectMode()) {
                    manager.getListItem(modPosition).setSelectMode(false);
                    this.editData.remove(manager.getListItem(modPosition).getAlarmId());
                }else {
                    manager.getListItem(modPosition).setSelectMode(true);
                    this.editData.put(manager.getListItem(modPosition).getAlarmId(), modPosition);
                }
                notifyItemRangeChanged(HEADER, getItemCount());

            } else {
                if(holder.expandableLayout.isExpanded()) {
                    holder.expandableLayout.collapse();
                    manager.getListItem(modPosition).setExpanded(false);
                } else {
                    holder.expandableLayout.expand();
                    manager.getListItem(modPosition).setExpanded(true);
                }
            }
        });

        //ホルダー長押し時
        holder.itemView.setOnLongClickListener(view -> {
            if(editMode) return true;
            Log.i(TAG, "ListItemLongClick");
            editMode = true;
            manager.setSelectMode();
            editData.clear();
            manager.getListItem(modPosition).setSelectMode(true);
            holder.toggle.setClickable(false); holder.toggle.setFocusable(false);
            editData.put(manager.getListItem(modPosition).getAlarmId(), modPosition);
            bottomAppBar.setVisibility(View.GONE);
            fbt.setVisibility(View.GONE);
            notifyDataSetChanged();
            return true;
        });

        //トグルスイッチクリック時
        holder.toggle.setOnClickListener(v -> {
            if(editMode) return;
            Log.i(TAG, "ToggleButtonClick");
            ListItem bindListItem = manager.getListItem(modPosition);
            if(((SwitchCompat) v).isChecked()) {
                ((SwitchCompat) v).setChecked(true);
                manager.updateAlarm(modPosition,bindListItem.getTime(), bindListItem.getMusicUri(), true, bindListItem.getAlarmLabel());
            } else {
                ((SwitchCompat) v).setChecked(false);
                manager.updateAlarm(modPosition,bindListItem.getTime(), bindListItem.getMusicUri(), false, bindListItem.getAlarmLabel());
            }
            notifyItemRangeChanged(HEADER, getItemCount());
        });

        //musicSelectクリック
        holder.musicSelButton.setOnClickListener(view -> {
            musicSelectListener.onItemClick(modPosition, manager.getListItem(modPosition).getAlarmId());
            Log.i(TAG, "MusicSelClick");
        });

        //削除ボタンクリック
        holder.deleteButton.setOnClickListener(view -> {
            manager.deleteAlarm(modPosition);
            notifyItemRemoved(modPosition);
            notifyItemRangeChanged(HEADER, getItemCount());
            Toast.makeText(context,R.string.alarm_delete_msg,Toast.LENGTH_SHORT).show();
            Log.i(TAG, "deleteButtonClick");
        });

        //保存ボタンクリック
        holder.saveButton.setOnClickListener(view -> {
            int hour = holder.timePicker.getHour();
            int minute = holder.timePicker.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            manager.getListItem(modPosition).setTime(String.format(Locale.US, "%02d", hour) + ":" + String.format(Locale.US, "%02d",minute));

            manager.updateAlarm(modPosition, manager.getListItem(modPosition).getTime(), manager.getListItem(modPosition).getMusicUri(),true, manager.getListItem(modPosition).getAlarmLabel());

            notifyItemRangeChanged(HEADER, getItemCount());

            Toast.makeText(context,R.string.alarm_save_msg,Toast.LENGTH_SHORT).show();
            Log.i(TAG, "SaveButtonClick");
        });

        //ラベルボタンクリック
        holder.editLabelButton.setOnClickListener(v -> {
            DialogEditLabel dialogEditLabel = new DialogEditLabel();
            dialogEditLabel.show(fragmentManager, "dialog");
            dialogEditLabel.setClickListener(label -> {
                manager.getListItem(modPosition).setAlarmLabel(label);
                notifyItemChanged(modPosition);
            });
            Log.i(TAG, "EditLabelDialogClick");
        });

        //コピーボタンクリック
        holder.copyAlarmButton.setOnClickListener(v -> {
            manager.copyAlarm(modPosition);
            notifyItemInserted(modPosition + 1);
            notifyItemRangeChanged(HEADER, getItemCount());
            Log.i(TAG, "copyButtonClick");
        });
    }

    @Override
    public int getItemCount() {
        return manager.getListItems().size();
    }

    @Override
    public int getItemViewType(int position) {
        if(manager.getListItem(position).isFooter()) return TYPE_ITEMS.Footer;
        else if(manager.getListItem(position).isHeader()) {
            if(manager.getListItem(position).isSelectModeHeader()) return TYPE_ITEMS.SelectModeHeader;
            else return TYPE_ITEMS.Header;
        }
        else return TYPE_ITEMS.Normal;
    }

    public ArrayList<ListItem> getAlarms() {
        return manager.getListItems();
    }

    public void setAlarmsMusicUri(int position, String musicUri) {
        if(!Objects.equals(manager.getListItem(position).getMusicUri(), musicUri)) {
            manager.getListItem(position).setMusicUri(musicUri);
            toggleAlarmsExpand(position);
            notifyItemChanged(position);
        }
    }

    public void toggleAlarmsExpand(int pos) {
        manager.getListItem(pos).setExpanded(!manager.getListItem(pos).isExpanded());
    }

    private void toggleListItem(ListViewHolder holder, float alpha, int color) {
        holder.itemView.setAlpha(alpha);
        holder.timeText.setTextColor(context.getColor(color));
    }

    public void setMusicSelectListener(MusicSelectListener musicSelectListener) {
        this.musicSelectListener = musicSelectListener;
    }

    public void setSoftKeyBoardListener(SoftKeyBoardListener softKeyBoardListener) {
        this.softKeyBoardListener = softKeyBoardListener;
    }

 }