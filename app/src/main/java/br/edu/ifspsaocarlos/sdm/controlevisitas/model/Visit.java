package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.app.Application;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.Map;

public class Visit {
    public static final int SITUATION_SCHEDULED = 1000;
    public static final int SITUATION_INPROGRESS = 1001;
    public static final int SITUATION_COMPLETED = 1002;

    public String id;
    public String client;
    public String employee;
    public String date;
    public String startTime;
    public String closingTime;
    public String reason;
    public String notes;
    public int situation;
    public String images_id;
    public String audios_id;
    //keywords: <id, word>
    public Map<String, String> keywords;

    public Visit(){}

    //data needed to start a visit
    public Visit(String client, String date, String startTime, String reason){
        this.client = client;
        this.date = date;
        this.startTime = startTime;
        this.reason = reason;
        this.situation = SITUATION_INPROGRESS;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getSituation() {
        return situation;
    }

    public void setSituation(int situation) {
        this.situation = situation;
    }

    public String getImages_id() {
        return images_id;
    }

    public void setImages_id(String images_id) {
        this.images_id = images_id;
    }

    public String getAudios_id() {
        return audios_id;
    }

    public void setAudios_id(String audios_id) {
        this.audios_id = audios_id;
    }

    public Map<String, String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Map<String, String> keywords) {
        this.keywords = keywords;
    }

    public String getDateAndTime(){
        return date + " - " + startTime + " ~ " + isClosingTimeNull();
    }

    @Exclude
    public String isClosingTimeNull(){
        if(closingTime == null){
            return "";
        }
        return closingTime;
    }
}
