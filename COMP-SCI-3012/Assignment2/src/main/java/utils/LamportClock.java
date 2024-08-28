package main.java.utils;

/**
 * main.java.utils.LamportClock
 * --
 * This file is implementation of the main.java.utils.LamportClock
 * --
 * This code handles, getting time, incrementing time, and receiving time. As it is not a requirement of the
 * assignment handling the overflow of the clock will not be implemented.
 */
public class LamportClock {
    private int clock = 0;

    public LamportClock() {
        this.clock = 0;
    }

    public synchronized int getTime() {
        return clock;
    }

    public synchronized void tick() {
        this.clock++;
    }

    public synchronized void receiveTime(int time) {
        this.clock = time + this.clock;
        tick();
    }
}
