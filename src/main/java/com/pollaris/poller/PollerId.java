package com.pollaris.poller;

import java.util.Objects;

public class PollerId {
    Integer uuid;
    private PollerId(Integer id){
        this.uuid = id;
    }
    public static PollerId mkOfInteger(Integer id){
        if(id==null || id < 0){
            throw new IllegalArgumentException("Invalid uuid: " + id);
        }
        return new PollerId(id);
    }
    public Integer getId(){return this.uuid;}
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PollerId)) return false;
        PollerId other = (PollerId) o;
        return this.uuid == other.uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
