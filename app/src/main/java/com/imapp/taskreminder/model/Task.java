package com.imapp.taskreminder.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {

    private String idTask,titleTask, dateTask, timeTask, descTask, statTask ;

    public Task(){
    }

    public Task(String idTask, String titleTask, String dateTask, String timeTask, String descTask, String statTask) {
        this.idTask = idTask;
        this.titleTask = titleTask;
        this.dateTask = dateTask;
        this.timeTask = timeTask;
        this.descTask = descTask;
        this.statTask = statTask;
    }


    public String getIdTask() {
        return idTask;
    }

    public void setIdTask(String idTask) {
        this.idTask = idTask;
    }

    public String getTitleTask() {
        return titleTask;
    }

    public void setTitleTask(String titleTask) {
        this.titleTask = titleTask;
    }

    public String getDateTask() {
        return dateTask;
    }

    public void setDateTask(String dateTask) {
        this.dateTask = dateTask;
    }

    public String getTimeTask() {
        return timeTask;
    }

    public void setTimeTask(String timeTask) {
        this.timeTask = timeTask;
    }

    public String getDescTask() {
        return descTask;
    }

    public void setDescTask(String descTask) {
        this.descTask = descTask;
    }

    public String getStatTask() {
        return statTask;
    }

    public void setStatTask(String statTask) {
        this.statTask = statTask;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idTask);
        dest.writeString(this.titleTask);
        dest.writeString(this.dateTask);
        dest.writeString(this.timeTask);
        dest.writeString(this.descTask);
        dest.writeString(this.statTask);
    }

    protected Task(Parcel in) {
        this.idTask = in.readString();
        this.titleTask = in.readString();
        this.dateTask = in.readString();
        this.timeTask = in.readString();
        this.descTask = in.readString();
        this.statTask = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
