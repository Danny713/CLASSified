package com.classified.classified;

import android.util.Log;

public class Bookmark {

    private String courseId;

    public Bookmark(String courseId) {
        this.courseId = courseId;
    }

    public Bookmark(){

    }

    public String getCourseId() {
        return courseId;
    }

}