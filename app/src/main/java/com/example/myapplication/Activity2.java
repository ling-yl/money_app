package com.example.myapplication;

import java.util.Map;
import java.util.HashMap;

public class Activity2 extends Activity{
    public String get_bonus;
    public String shop_id;
    public String user_id;

    public Activity2(String get_bonus){
        this.get_bonus=get_bonus;
    }

    public Activity2(){

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("get_bonus",get_bonus);
        result.put("shop_id",shop_id);

        return result;
    }

    public String get_Bonus() {
        return get_bonus;
    }

    public String user_id() {
        return user_id;
    }

    public String shop_id() {
        return shop_id;
    }

}
