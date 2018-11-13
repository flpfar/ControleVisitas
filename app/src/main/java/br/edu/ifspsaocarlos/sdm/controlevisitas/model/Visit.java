package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Visit implements Parcelable{
    public static final int SITUATION_SCHEDULED = 1000;
    public static final int SITUATION_INPROGRESS = 1001;
    public static final int SITUATION_COMPLETED = 1002;

    public String id;
    public String client;
    public String client_id;
    public String employee;
    public String date;
    public String startTime;
    public String closingTime;
    public String reason;
    public String notes;
    public int situation;
    public String images_id;
    public String audios_id;
    public String keywords; //keywords will be a single string with separators
    //keywords: <id, word>
//    public Map<String, String> keywords;

    public Visit(){}

    //data needed to start a visit
    public Visit(String client, String date, String startTime, String reason){
        this.client = client;
        this.date = date;
        this.startTime = startTime;
        this.reason = reason;
        this.situation = SITUATION_INPROGRESS;
    }


    protected Visit(Parcel in) {
        id = in.readString();
        client = in.readString();
        employee = in.readString();
        date = in.readString();
        startTime = in.readString();
        closingTime = in.readString();
        reason = in.readString();
        notes = in.readString();
        situation = in.readInt();
        images_id = in.readString();
        audios_id = in.readString();
        keywords = in.readString();
        client_id = in.readString();
    }

    public static final Creator<Visit> CREATOR = new Creator<Visit>() {
        @Override
        public Visit createFromParcel(Parcel in) {
            return new Visit(in);
        }

        @Override
        public Visit[] newArray(int size) {
            return new Visit[size];
        }
    };

    public String getKeywords() {
        return keywords;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
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

//    public Map<String, String> getKeywords() {
//        return keywords;
//    }
//
//    public void setKeywords(Map<String, String> keywords) {
//        this.keywords = keywords;
//    }

    @Exclude
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(client);
        dest.writeString(employee);
        dest.writeString(date);
        dest.writeString(startTime);
        dest.writeString(closingTime);
        dest.writeString(reason);
        dest.writeString(notes);
        dest.writeInt(situation);
        dest.writeString(images_id);
        dest.writeString(audios_id);
        dest.writeString(keywords);
        dest.writeString(client_id);
    }
}
