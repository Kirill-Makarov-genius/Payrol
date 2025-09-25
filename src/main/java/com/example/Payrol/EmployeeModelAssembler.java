package com.example.Payrol;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

// A simple interfase has one method - toModel(). It it based on converting a non-model object(Employee)
// into a model-based object(EntityModel<Employee>)
@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>>{
    

// methodOn(Controller.class) call the controller without calling it for allows us to get method
// withSelfRel() just marks it as the self link for entity
// withRel("nameRoot") marks this root with custom nameRoot
// linkTo() + methodOn() help to build links safely
    @Override
    public EntityModel<Employee> toModel(Employee employee) {
        
        return EntityModel.of(employee, 
        linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
        linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
    }
    


}
