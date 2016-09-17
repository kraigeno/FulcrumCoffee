package com.qmatica.arsen.aglistview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class History {
    private long id;
    private String dateTime;
    private String comments;

    public History() {
        this.id = -1;   // new history object (db objects will have automatically generated primary keys - see second constructor
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        dateTime = sdf.format(c.getTime());

//        Date date = new Date();
//        dateTime = sdf.format(date);
    }

    public History(long id, String dateTime) {
        this.id = id;
        this.dateTime = dateTime;
    }

    public History(long id, String dateTime, String comments) {
        this.id = id;
        this.dateTime = dateTime;
        this.comments = comments;
    }

    public long getID() {
        return this.id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

}
