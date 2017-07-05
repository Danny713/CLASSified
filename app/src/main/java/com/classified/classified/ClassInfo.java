package com.classified.classified;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by munyongjang on 6/26/17.
 */

public class ClassInfo {

    private final JSONObject classInfoJson;
    private String courseCode;
    private String courseTitle;
    private String courseId;

    // can add more fields as needed, all the info is already stored in classInfoJson
    public ClassInfo(JSONObject classInfoJson) {
        this.classInfoJson = classInfoJson;
        courseCode = this.getCourseCodeInternal();
        courseTitle = this.getCourseTitleInternal();
        courseId = this.getCourseIdInternal();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getCourseId() {
        return courseId;
    }

    private String getCourseIdInternal() {
        try {
            courseId = classInfoJson.getString("courseid");
            return courseId;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // getting the course code
    private String getCourseCodeInternal() {
        JSONArray listings = null;
        try {
            listings = classInfoJson.getJSONArray("listings");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // will just take the first listing
        JSONObject firstListing = null;
        try {
            firstListing = listings.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            courseCode = firstListing.getString("dept") + " "
                    + firstListing.getString("number");
            return courseCode;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // getting the course title
    private String getCourseTitleInternal() {
        try {
            courseTitle = classInfoJson.getString("title");
            return courseTitle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkIfQueryInCourseInfo(String query) {
        if (courseCode.replaceAll("\\s+","").toLowerCase().contains(query)) {
            return true;
        }
        if (courseTitle.replaceAll("\\s+","").toLowerCase().contains(query)) {
            return true;
        }
        return false;
    }
}
