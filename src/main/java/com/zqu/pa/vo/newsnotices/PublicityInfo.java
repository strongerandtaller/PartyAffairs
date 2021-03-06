package com.zqu.pa.vo.newsnotices;

import java.util.Date;

import com.zqu.pa.utils.DateToString;

public class PublicityInfo {
    
    private int id;
    
    private String title;
    
    private String date;
    
    private String source;
    
    private String creatorId;
    
    private String creator; //录入人名字

    private int state;
    
    private String lastTime;
    
    private int click;
    
    private String coverpath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = DateToString.getDateString("yyyy/MM/dd HH:mm:ss", date);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = DateToString.getDateString("yyyy/MM/dd HH:mm:ss", lastTime);
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public String getCoverpath() {
        return coverpath;
    }

    public void setCoverpath(String coverpath) {
        this.coverpath = coverpath;
    }
    
}
