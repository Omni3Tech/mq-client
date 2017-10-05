package io.cdsoft.anypoint.mq.client.impl;

public class AnypointException extends Exception {

    public AnypointException(String message) {
        super(message);
    }

    public AnypointException(Throwable cause) {
        super(cause);
    }

    public AnypointException(String message, Throwable cause) {
        super(message, cause);
    }
}
