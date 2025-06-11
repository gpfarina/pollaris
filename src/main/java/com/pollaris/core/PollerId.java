package com.pollaris.core;

public class PollerId {
    Integer uuid;
    private PollerId(Integer id){
        this.uuid = id;
    }
    public static PollerId mkOfInteger(Integer id){
        if(id==null || id <= 0){
            throw new IllegalArgumentException("Invalid uuid: " + id);
        }
        return new PollerId(id);
    }
    public Integer getId(){return this.uuid;}
}
