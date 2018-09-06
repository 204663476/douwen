package com.huagongzi.douwen.untils;

/**
 * Created by 许鑫源 on 2018/8/7.
 */

public class Mydata {
    private String type;
    private String id;
    private String text;

    public Mydata(String type,String id,String text){
        this.type = type;
        this.id = id;
        this.text = text;
    }

    public String getType(){
        return type;
    }

    public String getId(){
        return id;
    }

    public String getText(){
        return text;
    }


}