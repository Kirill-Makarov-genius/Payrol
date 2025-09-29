package com.example.Payrol.config;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.Payrol.entities.Employee;
import com.example.Payrol.entities.Order;
import com.example.Payrol.enums.Status;
import com.example.Payrol.repositories.EmployeeRepository;
import com.example.Payrol.repositories.OrderRepository;

@Configuration
public class LoadDatabase {
    
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    
    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository){

        return args -> {
            log.info("Preloading " + employeeRepository.save(new Employee("Kirill", "Makarov",  "Builder")));
            log.info("Preloading " + employeeRepository.save(new Employee("Mark", "Zuckerberg", "Programmer")));
            log.info("Preloading " + orderRepository.save(new Order("Some description", Status.COMPLETED)));
            log.info("Preloading " + orderRepository.save(new Order("Some another description", Status.COMPLETED)));
        };

    }
    

}
