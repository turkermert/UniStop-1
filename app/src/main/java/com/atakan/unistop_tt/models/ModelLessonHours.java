package com.atakan.unistop_tt.models;

public class ModelLessonHours {
    private String uid;
    private String mondayDep, tuesdayDep, wednesdayDep, thursdayDep, fridayDep, mondayRet, tuesdayRet, wednesdayRet, thursdayRet, fridayRet;

    public ModelLessonHours() {
    }

    public ModelLessonHours(String uid, String mondayDep, String tuesdayDep, String wednesdayDep, String thursdayDep, String fridayDep, String mondayRet, String tuesdayRet, String wednesdayRet, String thursdayRet, String fridayRet) {
        this.uid = uid;
        this.mondayDep = mondayDep;
        this.tuesdayDep = tuesdayDep;
        this.wednesdayDep = wednesdayDep;
        this.thursdayDep = thursdayDep;
        this.fridayDep = fridayDep;
        this.mondayRet = mondayRet;
        this.tuesdayRet = tuesdayRet;
        this.wednesdayRet = wednesdayRet;
        this.thursdayRet = thursdayRet;
        this.fridayRet = fridayRet;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMondayDep() {
        return mondayDep;
    }

    public void setMondayDep(String mondayDep) {
        this.mondayDep = mondayDep;
    }

    public String getTuesdayDep() {
        return tuesdayDep;
    }

    public void setTuesdayDep(String tuesdayDep) {
        this.tuesdayDep = tuesdayDep;
    }

    public String getWednesdayDep() {
        return wednesdayDep;
    }

    public void setWednesdayDep(String wednesdayDep) {
        this.wednesdayDep = wednesdayDep;
    }

    public String getThursdayDep() {
        return thursdayDep;
    }

    public void setThursdayDep(String thursdayDep) {
        this.thursdayDep = thursdayDep;
    }

    public String getFridayDep() {
        return fridayDep;
    }

    public void setFridayDep(String fridayDep) {
        this.fridayDep = fridayDep;
    }

    public String getMondayRet() {
        return mondayRet;
    }

    public void setMondayRet(String mondayRet) {
        this.mondayRet = mondayRet;
    }

    public String getTuesdayRet() {
        return tuesdayRet;
    }

    public void setTuesdayRet(String tuesdayRet) {
        this.tuesdayRet = tuesdayRet;
    }

    public String getWednesdayRet() {
        return wednesdayRet;
    }

    public void setWednesdayRet(String wednesdayRet) {
        this.wednesdayRet = wednesdayRet;
    }

    public String getThursdayRet() {
        return thursdayRet;
    }

    public void setThursdayRet(String thursdayRet) {
        this.thursdayRet = thursdayRet;
    }

    public String getFridayRet() {
        return fridayRet;
    }

    public void setFridayRet(String fridayRet) {
        this.fridayRet = fridayRet;
    }
}
