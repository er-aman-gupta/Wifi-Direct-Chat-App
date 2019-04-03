package com.example.bluetoothchatapp;

public class DataClass {
    String s;
    boolean who;

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public boolean isWho() {
        return who;
    }

    public void setWho(boolean who) {
        this.who = who;
    }

    public DataClass(String s, boolean who) {
        this.s = s;
        this.who = who;
    }
}
