package me.lor3mipsum.next.api.util.misc;

public class NoStackTraceThrowable extends RuntimeException{
    public NoStackTraceThrowable(final String msg) {
        super(msg);
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public String toString() {
        return "java.lang.IllegalArgumentException";
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
