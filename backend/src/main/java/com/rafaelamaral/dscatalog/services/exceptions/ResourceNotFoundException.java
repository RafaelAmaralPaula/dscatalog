package com.rafaelamaral.dscatalog.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    private static final Long serialVersionUID = 1L;

    public ResourceNotFoundException(String message){
        super(message);
    }

}
