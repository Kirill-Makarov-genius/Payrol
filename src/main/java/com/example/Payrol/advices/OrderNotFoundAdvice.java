package com.example.Payrol.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.Payrol.exceptions.OrderNotFoundException;

@RestControllerAdvice
public class OrderNotFoundAdvice {
    

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String OrderNotFoundHandler(OrderNotFoundException ex){
        return ex.getMessage();
    }

}
