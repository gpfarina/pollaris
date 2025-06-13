package com.pollaris.poller;

import java.util.Objects;

/**
 * A basic container class for id to assign to pollers.
 */
public class PollerId {
    Integer id;
    private PollerId(Integer id){
        this.id = id;
    }
    public static PollerId mkOfInteger(Integer id){
        if(id==null || id < 0){ // let's only allow for non negative ids.
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        return new PollerId(id);
    }
    public Integer getId(){return this.id;}
    
    // instances of this class are used as keys in hashmaps so we override this method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PollerId)) return false;
        PollerId other = (PollerId) o;
        return this.id == other.id;
    }

    // instances of this class are used as keys in hashmaps so we override this method
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
