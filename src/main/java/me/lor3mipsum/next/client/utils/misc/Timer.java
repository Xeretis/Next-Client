package me.lor3mipsum.next.client.utils.misc;

public class Timer {
    public long ms;

    public Timer() {
        this.ms = 0;
    }

    public boolean hasPassed(int ms) {
        return System.currentTimeMillis() - this.ms >= ms;
    }

    public void reset() {
        this.ms = System.currentTimeMillis();
    }
}