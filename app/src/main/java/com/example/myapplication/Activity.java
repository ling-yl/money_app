package com.example.myapplication;

import java.util.Map;
import java.util.HashMap;

public class Activity {
    private String aid;
    private String name;
    private String location;
    private String datetime;
    private Integer bonus;
    private String introduction;
    private String uid;
    private String get_bonus;
    private String record_id;
    private String shop;
    private String states;
    private String shop_id;

    public Activity( String aid,String name,String location,String datetime,Integer bonus,String introduction,String uid){
        this.aid=aid;
        this.name=name;
        this.location=location;
        this.datetime=datetime;
        this.bonus=bonus;
        this.introduction=introduction;
        this.uid=uid;

    }

    public Activity(String aid,String uid,String get_bonus){
        this.aid=aid;
        this.uid=uid;
        this.get_bonus=get_bonus;

    }

    public Activity(String record_id){
        this.record_id=record_id;
    }


    public Activity(){

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("aid",  aid);
        result.put("name",  name);
        result.put("location", location);
        result.put("datetime", datetime);
        result.put("bonus", bonus);
        result.put("introduction", introduction);
        result.put("uid", uid);
        result.put("get_bonus",get_bonus);
        result.put("record_id",record_id);

        return result;
    }


    public String getName(){
        return name;
    }

    public String getRecord_id(){
        return record_id;

    }

    public String getUid() {
        return uid;
    }

    public String get_Bonus() {
        return get_bonus;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Integer getBonus() {
        return bonus;
    }

    public String getShop_id() {
        return shop_id;
    }





    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }


    public String getShop(){
        return shop;
    }

}


