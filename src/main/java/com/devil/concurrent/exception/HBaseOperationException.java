package com.devil.concurrent.exception;

/**
 * @Description
 * @ClassName H
 * @Author Devil
 * @date 2020.05.26 23:23
 */
public class HBaseOperationException extends RuntimeException{

    public HBaseOperationException(String message) {
        super(message);
    }
}
