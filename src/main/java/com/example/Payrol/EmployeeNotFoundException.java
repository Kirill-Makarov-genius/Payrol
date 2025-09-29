package com.example.Payrol;

public class EmployeeNotFoundException extends RuntimeException {
    
    EmployeeNotFoundException(Long id){
        super("Cound not find employee " + id);
    }

}
