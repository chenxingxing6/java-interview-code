package com.handwrite.threadpool.my.reject;

/**
 * User: lanxinghua
 * Date: 2019/9/7 15:00
 * Desc:
 */
public class MyRejectException extends RuntimeException{
    public MyRejectException() {
    }

    public MyRejectException(String message) {
        super(message);
    }

    public MyRejectException(String message, Throwable cause) {
        super(message, cause);
    }
}
