package com.example.Payrol.controllers;


import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Payrol.EmployeeModelAssembler;
import com.example.Payrol.entities.Employee;
import com.example.Payrol.exceptions.EmployeeNotFoundException;
import com.example.Payrol.repositories.EmployeeRepository;

@RestController
public class EmployeeController {
    
    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;
    EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler){
        this.repository = repository;
        this.assembler = assembler;
    }


    @GetMapping("/employees")
    public CollectionModel<EntityModel<Employee>> all(){
        List<EntityModel<Employee>> employees = repository.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());
        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    // ResponseEntity is a Full HTTP response with body, status and headers
    @PostMapping("/employees")
    public ResponseEntity<EntityModel<Employee>> newEmployee(@RequestBody Employee newEmployee){
        EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));
        
    // enityModel.getRequiredLink return the link with the given relation, in out example is SELF link
    // URI is a Uniform Resource Identifier. It's a string that identifies a resource(something you can name or address)
    // URL is a type of URI that tells where to access the resource
        return ResponseEntity
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityModel);
    }


    @GetMapping("/employees/{id}")
    public EntityModel<Employee> one(@PathVariable Long id){
        Employee employee = repository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));
        return assembler.toModel(employee);

    } 

    @PutMapping("/employees/{id}")
    public ResponseEntity<EntityModel<Employee>> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id){
        

        Employee updatedEmployee = repository.findById(id)
            .map(employee -> {
                employee.setName(newEmployee.getName());
                employee.setRole(newEmployee.getRole());
                return repository.save(employee);
            })
            .orElseGet(() -> {
                return repository.save(newEmployee);
            });
        EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityModel);
    }
    
    // noContent() create a response with HTTP status 204 no content. Useful when API perform a request but doesn't need to return any data.
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<EntityModel<Employee>> deleteEmployee(@PathVariable Long id){
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }



}
