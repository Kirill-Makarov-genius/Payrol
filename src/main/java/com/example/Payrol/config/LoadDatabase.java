package com.example.Payrol;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.Payrol.entities.Employee;
import com.example.Payrol.repositories.EmployeeRepository;

@Configuration
public class LoadDatabase {
    
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    
    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository){

        return args -> {
            log.info("Preloading " + repository.save(new Employee("Kirill", "Makarov",  "Builder")));
            log.info("Preloading " + repository.save(new Employee("Mark", "Zuckerberg", "Programmer")));
        };

    }
    

}
