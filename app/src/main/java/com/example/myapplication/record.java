package com.example.myapplication;

public class record {
    private String get;
    private String getname;
    private String pay;
    private String payname;
    private String states;
    private Integer money;
    private String time;
    private String good_name;
    public record(String get, String getname, String pay, String payname, String states, Integer money, String time,String good_name) {
        this.get = get;
        this.getname = getname;
        this.pay = pay;
        this.payname = payname;
        this.states = states;
        this.money = money;
        this.time = time;
        this.good_name = good_name;
    }
    public record(String get, String getname, String pay, String payname, String states, Integer money, String time) {
        this.get = get;
        this.getname = getname;
        this.pay = pay;
        this.payname = payname;
        this.states = states;
        this.money = money;
        this.time = time;

    }
    public record()
    {

    }

    public String getGet() {
        return get;
    }

    public void setGet(String get) {
        this.get = get;
    }

    public String getGetname() {
        return getname;
    }

    public void setGetname(String getname) {
        this.getname = getname;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getPayname() {
        return payname;
    }

    public void setPayname(String payname) {
        this.payname = payname;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGood_name() {
        return good_name;
    }

    public void setGood_name(String good_name) {
        this.good_name = good_name;
    }
}