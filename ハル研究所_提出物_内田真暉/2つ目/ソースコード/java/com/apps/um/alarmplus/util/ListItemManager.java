package com.apps.um.alarmplus.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.apps.um.alarmplus.R;
import com.apps.um.alarmplus.listcomponent.ListAdapter;
import com.apps.um.alarmplus.listcomponent.ListItem;
import com.apps.um.alarmplus.receiver.AlarmReceiver;
import com.apps.um.alarmplus.service.NotificationService;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.FactoryConfigurationError;

public class ListItemManager {
    private final String TAG = "ListItemManager";

    private ArrayList<ListItem> data;
    private final Context context;
    private final DatabaseHelper helper;

    private ListItem nextAlarm = null;

    private ListItemMangerListener mangerListener;

    public ListItemManager(Context context, ArrayList<ListItem> data) {
        this.data = data;
        this.context = context;
        this.helper = DatabaseHelper.getInstance(context);
        if(getAlarmsToggleOnCount() < 0) {
            this.nextAlarm = getNextAlarm();
        }
        sortAlarms();
    }

    public ListItem getListItem(int pos) {
        return this.data.get(pos);
    }

    public ListItem getListItemById(int alarmId) {
        ListItem rItem = null;
        for(ListItem item: data) {
            if((item.isHeader() || item.isFooter())) continue;
            if(item.getAlarmId() == alarmId) rItem = item;
        }
        return rItem;
    }

    public int getPositionById(int alarmId) {
        int pos = -1;
        for(int i = 0; i < data.size(); i++) {
            if((getListItem(i).isHeader() || getListItem(i).isFooter())) continue;
            if(getListItem(i).getAlarmId() == alarmId) pos = i;
        }
        return pos;
    }

    public void removeListItem(int pos) {
        this.data.remove(pos);
    }

    public void insertListItem(int pos, ListItem item) {
        this.data.add(pos, item);
    }

    public void updateListItem(int pos, ListItem item){
        this.data.set(pos, item);
    }

    public void updateAlarm(int pos, String alarmTime, String musicUri, boolean toggle, String label) {
        helper.updateData(getListItem(pos).getAlarmId(), alarmTime, musicUri,toggle, label);
        ListItem listItem = new ListItem(getListItem(pos).getAlarmId(), alarmTime, musicUri,toggle, label);
        updateListItem(pos, listItem);
        if(toggle) Util.setAlarm(context, listItem);
        else Util.cancelAlarm(context, listItem);
        sortAlarms();
    }

    public void deleteAlarm(int pos){
        if(getListItem(pos).isToggle()) Util.cancelAlarm(context, getListItem(pos));
        helper.deleteDataById(getListItem(pos).getAlarmId());
        removeListItem(pos);
        sortAlarms();
    }

    public void deleteRangeAlarms(HashMap<Integer,Integer> editData) {
        for (int alarmId : editData.keySet()) {
            if (getListItemById(alarmId).isToggle())
                Util.cancelAlarm(context, getListItemById(alarmId));
            helper.deleteDataById(alarmId);
            removeListItem(getPositionById(alarmId));
        }
        sortAlarms();
    }

    public void copyAlarm(int pos) {
        ListItem item = getListItem(pos);
        int copyAlarmId = helper.createData(item.getTime(), item.getMusicUri(), item.isToggle(), item.getAlarmLabel());
        ListItem copyItem = new ListItem(copyAlarmId, item.getTime(), item.getMusicUri(), item.isToggle(), item.getAlarmLabel());
        insertListItem(pos, copyItem);
        if(item.isToggle()) Util.setAlarm(context, copyItem);
        sortAlarms();
    }

    public void sortAlarms(){
        Log.i(TAG, "SortAlarms");
        final ArrayList<ListItem> beforeItem = new ArrayList<>(data);
        data.sort((first, second) -> {
            if (first.isHeader() || second.isHeader() || first.isFooter() || second.isFooter()) return 0;
            return Long.compare(Util.getTimeDiff(first), Util.getTimeDiff(second));
        });

        data.sort((first, second) -> {
            if (first.isHeader() || second.isHeader() || first.isFooter() || second.isFooter()) return 0;
            else if(first.isToggle() && !second.isToggle()) return -1;
            else if(!first.isToggle() && second.isToggle()) return 1;
            else return 0;
        });

        if(isListItemMove(beforeItem, data)) {
            if(mangerListener != null) mangerListener.onListItemMove();
        }
        if(getAlarmsToggleOnCount() > 0) {
            if(getNextAlarm() != nextAlarm) {
                startNotification(getNextAlarm().getAlarmId());
                nextAlarm = getNextAlarm();
            }
        } else {
            stopNotification();
        }

    }

    public ListItem getNextAlarm() {
        ListItem listItem = null;
        for(ListItem item: data) {
            if(item.isHeader() || item.isFooter() || !item.isToggle()) continue;
            if(listItem == null) listItem = item;
            else {
                if(Util.getTimeDiff(listItem) > Util.getTimeDiff(item)) {
                    listItem = item;
                }
            }
        }

        return listItem;
    }

    public int getAlarmsToggleOnCount() {
        int result = 0;
        for(ListItem item: data) {
            if(item.isHeader() || item.isFooter() || !item.isToggle()) continue;
            result++;
        }
        return result;
    }

    public void setSelectMode() {
        ListItem listItem = new ListItem(ListAdapter.TYPE_ITEMS.SelectModeHeader);
        updateListItem(0, listItem);
    }

    public void cancelSelectMode() {
        ListItem listItem = new ListItem(ListAdapter.TYPE_ITEMS.Header);
        updateListItem(0, listItem);
    }

    public long getTimeDiff(int pos) {
        ListItem listItem = getListItem(pos);
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(listItem.getHour()));
        calendar.set(Calendar.MINUTE, Integer.parseInt(listItem.getMinute()));
        calendar.set(Calendar.SECOND, 0);

        int diff = calendar.compareTo(nowCalendar);
        if(diff <= 0) calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);

        Duration duration = Duration.between(nowCalendar.getTime().toInstant(), calendar.getTime().toInstant());
        return TimeUnit.MINUTES.convert(duration.getSeconds(), TimeUnit.SECONDS);
    }

    private void startNotification(int alarmId) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(context.getString(R.string.alarm_id), alarmId);
        context.startForegroundService(intent);
        Log.i(TAG, "StartNotification");
    }

    private void stopNotification() {
        Intent intent = new Intent(context, NotificationService.class);
        context.stopService(intent);
    }

    private boolean isListItemMove(ArrayList<ListItem> before, ArrayList<ListItem> after) {
        for(int i = 0; i < data.size(); i++) {
            if(before.get(i).getAlarmId() != after.get(i).getAlarmId()) return true;
        }
        return false;
    }

    public ArrayList<ListItem> getListItems() {
        return data;
    }

    public void setListItems(ArrayList<ListItem> data) {
        this.data = data;
    }

    public interface ListItemMangerListener {
        void  onListItemMove();
    }

    public void setMangerListener(ListItemMangerListener mangerListener) {
        this.mangerListener = mangerListener;
    }
}
