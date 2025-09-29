package com.example.Payrol.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Payrol.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    
} 
    

