package com.example.Payrol.controllers;



import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Payrol.components.OrderModelAssembler;
import com.example.Payrol.entities.Order;
import com.example.Payrol.enums.Status;
import com.example.Payrol.exceptions.OrderNotFoundException;
import com.example.Payrol.repositories.OrderRepository;

@RestController
public class OrderContoller {

    // Dependecy Injection of repository and assembler
    private final OrderRepository orderRepository;
    private final OrderModelAssembler assembler;

    OrderContoller(OrderRepository orderRepository, OrderModelAssembler orderModelAssembler){
        this.orderRepository = orderRepository;
        this.assembler = orderModelAssembler;
    }

    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> all(){
        List<EntityModel<Order>> orders = orderRepository.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList()); 

        return CollectionModel.of(orders, 
        linkTo(methodOn(OrderContoller.class).all()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> one(@PathVariable Long id){

        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order){
 
        order.setStatus(Status.IN_PROGRESS);
        EntityModel<Order> newOrder = assembler.toModel(orderRepository.save(order));

        return ResponseEntity
            .created(newOrder.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(newOrder);

    }

    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id){

        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        
        if (order.getStatus() == Status.IN_PROGRESS){
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
        }

        // HttpStatus.METHOD_NOT_ALLOWED sets HTTP 405
        // HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE tells that the response body is Problem Details JSON(application/problem+json)
        // Problem.create() constructs the stuctured error message describing what went wrong
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(Problem.create()
                .withTitle("Method not allowed")
                .withDetail("You can't cancel an order that is in the " + order.getStatus() + " status!"));
    }
    
    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id){

        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.IN_PROGRESS){
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(order));
        }
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(Problem.create()
                .withTitle("Method not allowed")
                .withDetail("You can't complete an order with status " + order.getStatus() + " status!"));



    }


}
