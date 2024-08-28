package test.java.utils;

import main.java.utils.LamportClock;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LamportClockTest
 * --
 * This file is testing the main.java.utils.LamportClock.java
 * --
 * Each function will test each of function of the class to verify that it is working as expected.
 */
class LamportClockTest {

    @Test
    void getTime() {
        LamportClock clock = new LamportClock();

        int time = clock.getTime();

        assertEquals(0, time, "Initial time should be 0");
    }

    @Test
    void tick() {
        LamportClock clock = new LamportClock();

        clock.tick();
        int time = clock.getTime();

        assertEquals(1, time, "Time should be increased by 1");
    }

    @Test
    void receiveTime() {
        LamportClock clock = new LamportClock();

        clock.receiveTime(5);
        int time = clock.getTime();

        assertEquals(6, time, "If time coming in is5, then time after should be 6 (5 + 0 + 1 tick)");
    }
}
