package com.apps.um.alarmplus.listcomponent;

import java.util.Objects;

public class MusicListItem {
    int musicId = -1;
    String musicFileName = null;
    String musicUri = null;
    boolean header = false;
    boolean footer = false;
    boolean selectMusic = false;

    public MusicListItem(int itemType) {
        if(itemType == MusicListAdapter.TYPE_ITEM.Header) {
            header = true;
        } else if (itemType == MusicListAdapter.TYPE_ITEM.Footer) {
            footer = true;
        } else {
        }
    }

    public MusicListItem(int musicId, String musicFileName, String musicUri, boolean selectMusic) {
        this.musicId = musicId;
        this.musicFileName = musicFileName;
        this.musicUri = musicUri;
        this.selectMusic = selectMusic;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public String getMusicFileName() {
        return musicFileName;
    }

    public void setMusicFileName(String musicFileName) {
        this.musicFileName = musicFileName;
    }

    public String getMusicUri() {
        return musicUri;
    }

    public void setMusicUri(String musicUri) {
        this.musicUri = musicUri;
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

    public boolean isSelectMusic() {
        return selectMusic;
    }

    public void setSelectMusic(boolean selectMusic) {
        this.selectMusic = selectMusic;
    }
}
