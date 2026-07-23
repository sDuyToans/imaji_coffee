package com.duytoan.imajicoffee.imaji_coffee_be.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom resource not found exception to give wanted info
 * @author duytoan
 * @since 10/2025
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue){
        super(String.format("%s not found with the given input data %s: '%s", resourceName, fieldName, fieldValue));
    }
}
