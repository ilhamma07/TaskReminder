package com.imapp.taskreminder.model;

public class Notif {
    private String idNotif,titleNotif, messageNotif;

    public Notif(String idNotif, String titleNotif, String messageNotif) {
        this.idNotif = idNotif;
        this.titleNotif = titleNotif;
        this.messageNotif = messageNotif;
    }

    public String getIdNotif() {
        return idNotif;
    }

    public void setIdNotif(String idNotif) {
        this.idNotif = idNotif;
    }

    public String getTitleNotif() {
        return titleNotif;
    }

    public void setTitleNotif(String titleNotif) {
        this.titleNotif = titleNotif;
    }

    public String getMessageNotif() {
        return messageNotif;
    }

    public void setMessageNotif(String messageNotif) {
        this.messageNotif = messageNotif;
    }
}
