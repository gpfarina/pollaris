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
    /**
     * Makes a PollerId out of a valid integer.
     * @param id
     * @return a PollerId
    */
    public static PollerId mkOfInteger(Integer id){
        if(id==null || id < 0){ // let's only allow for non negative ids.
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        return new PollerId(id);
    }
    /**
     * @return a fresh PollerId
     */
    public static PollerId fresh(){
        int id = IdGen.getInstance().getCount();
        IdGen.getInstance().increment(); // important to increment to avoid collisions and have uniqueness
        return mkOfInteger(id);
    }
    
    public Integer getId(){return this.id;}
    
    // instances of this class are used as keys in hashmaps so we override this method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PollerId)) return false;
        PollerId other = (PollerId) o;
        return this.id.equals(other.id);
    }

    // instances of this class are used as keys in hashmaps so we override this method
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
