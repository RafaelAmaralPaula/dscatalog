package com.rafaelamaral.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException{
    private static final Long serialVersionUID = 1L;

    public EntityNotFoundException(String message){
        super(message);
    }

}
