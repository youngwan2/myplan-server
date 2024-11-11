package com.myplan.server.exception;

public class InvalidTokenException extends  RuntimeException {

    public InvalidTokenException(String message){
        super(message);
    }
}
