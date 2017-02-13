package com.mavenspring.exception;

/**
 * Created by magic on 2017/1/19.
 * 秒杀相关的所有业务异常
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }

}
