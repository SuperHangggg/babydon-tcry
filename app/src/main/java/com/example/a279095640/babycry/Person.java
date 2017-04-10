package com.example.a279095640.babycry;

/**
 * Created by 279095640 on 2017/4/10 0010.
 */

public class Person {
    private String name;
    private String no;
    private String time;

    public Person(String name, String no, String time) {
        this.name = name;
        this.no = no;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
