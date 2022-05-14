package com.rafaelamaral.dscatalog.services.exceptions;

public class DataBaseException extends RuntimeException{
    private static final Long serialVersionUID = 1L;

    public DataBaseException(String message){
        super(message);
    }

}
