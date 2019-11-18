package com.zdrenka.model;

import java.time.LocalDate;
import java.util.Locale;

public class Comment {
    private String date;
    private String comment;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
