package com.tofftran.mockproject.exception;

public class    ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
