package com.pollaris;

import org.junit.jupiter.api.Test;

import com.pollaris.poller.PollerId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Generic test class to test equality and hash properties of PollerId class
 */
public class PollerIdTest {

    @Test
    public void testEquals_SameObject() {
        PollerId id = PollerId.fresh();
        assertTrue(id.equals(id), "An object should be equal to itself");
    }

    @Test
    public void testEquals_Null() {
        PollerId id = PollerId.fresh();
        assertFalse(id.equals(null), "An object should not be equal to null");
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals_DifferentClass() {
        PollerId id = PollerId.fresh();
        String other = "not a PollerId";
        assertFalse(id.equals(other), "An object should not be equal to an object of different class");
    }

    @Test
    public void testEquals_SameId() {
        PollerId id1 = PollerId.mkOfInteger(456);
        PollerId id2 = PollerId.mkOfInteger(456);
        assertTrue(id1.equals(id2), "Objects with the same id should be equal");
    }

    @Test
    public void testEquals_DifferentId() {
        PollerId id1 = PollerId.fresh();
        PollerId id2 = PollerId.fresh();
        assertFalse(id1.equals(id2), "Objects with different ids should not be equal");
    }
}
