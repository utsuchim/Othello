package com.apps.um.alarmplus.listcomponent;


public class ListItem {

    private int alarmId = -1;
    private String time;
    private String musicUri = null;
    private boolean toggle = true;
    private String alarmLabel = null;
    private boolean expanded = false;

    private boolean selectMode = false;

    private boolean header = false;
    private boolean footer = false;

    private boolean selectModeHeader = false;

    public ListItem() {
    }

    public ListItem(int itemType) {
        if(itemType == ListAdapter.TYPE_ITEMS.Header) {
            this.header = true;
        } else if(itemType == ListAdapter.TYPE_ITEMS.Footer) {
            this.footer = true;
        } else if(itemType == ListAdapter.TYPE_ITEMS.SelectModeHeader) {
            this.header = true;
            this.selectModeHeader = true;
        }
    }

    public ListItem(int alarmId, String time, String musicUri, boolean toggle, String alarmLabel) {
        this.alarmId = alarmId;
        this.time = time;
        this.musicUri = musicUri;
        this.toggle = toggle;
        this.alarmLabel = alarmLabel;
        this.expanded = false;
    }

    public String getTime() {
        return time;
    }

    public String getHour(){
        return getTime().substring(0,2);
    }

    public String getMinute(){
        return getTime().substring(3,5);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public String getMusicUri() {
        return musicUri;
    }

    public void setMusicUri(String musicUri) {
        this.musicUri = musicUri;
    }

    public boolean isToggle() {
        return toggle;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public String getAlarmLabel() {
        return alarmLabel;
    }

    public void setAlarmLabel(String alarmLabel) {
        this.alarmLabel = alarmLabel;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public boolean isFooter() {
        return footer;
    }

    public void setFooter(boolean footer) {
        this.footer = footer;
    }

    public boolean isSelectModeHeader() {
        return selectModeHeader;
    }

    public void setSelectModeHeader(boolean selectModeHeader) {
        this.selectModeHeader = selectModeHeader;
    }

    public boolean isSelectMode() {
        return selectMode;
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
