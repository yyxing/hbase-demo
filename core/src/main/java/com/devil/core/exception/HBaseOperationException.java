package com.devil.core.exception;

/**
 * @author Devil
 * @version 1.0
 * @date 2020/6/6 10:07
 */
public class HBaseOperationException extends RuntimeException{

    public HBaseOperationException(String message) {
        super(message);
    }

}
