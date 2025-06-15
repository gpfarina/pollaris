package com.pollaris;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import com.pollaris.action.LogAction;
import com.pollaris.action.NoOpAction;
import com.pollaris.action.Result;
import com.pollaris.event.Event;

public class ActionsTest {
    final PrintStream standardOut = System.out;
    @Test
    public void testLog(){
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        LogAction logAction = new LogAction();
        logAction.execute(mkEvent());
        String expectedResult = "Event has occurred: </home/user/testLocation, 1970-01-01T00:00:00Z, {attr0=attr0desc, attr1=attr1desc}>";
        assertEquals(expectedResult, outputStreamCaptor.toString().trim());
        System.setOut(standardOut);

    }

    @Test
    public void testNop(){
        assertEquals(new NoOpAction().execute(mkEvent()), Result.SUCCESS);
    }

    private static Event mkEvent(){
        return new Event() {

            @Override
            public Instant timestamp() {
                return Instant.ofEpochMilli(0);
            }

            @Override
            public Map<String, String> metadata() {
                Map<String, String> map= new HashMap<>(); 
                map.put("attr0", "attr0desc");
                map.put("attr1", "attr1desc");
                return map;
            }

            @Override
            public Path location() {
                return Paths.get("/home/user/testLocation");
            }
        };
    }
}
