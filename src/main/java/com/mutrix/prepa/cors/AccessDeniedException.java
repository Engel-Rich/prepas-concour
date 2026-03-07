package com.mutrix.prepa.cors;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message){
        super(message);
    }
}
