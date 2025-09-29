package com.example.Payrol.components;



import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.Payrol.controllers.OrderContoller;
import com.example.Payrol.entities.Order;
import com.example.Payrol.enums.Status;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {
    
    public EntityModel<Order> toModel(Order order){

        EntityModel<Order> orderModel = EntityModel.of(order,
        linkTo(methodOn(OrderContoller.class).one(order.getId())).withSelfRel(),
        linkTo(methodOn(OrderContoller.class).all()).withRel("orders"));

        if (order.getStatus() == Status.IN_PROGRESS){
            orderModel.add(linkTo(methodOn(OrderContoller.class).cancel(order.getId())).withRel("Cansel"));
            orderModel.add(linkTo(methodOn(OrderContoller.class).complete(order.getId())).withRel("Complete"));
        }

        return orderModel;
    }

}
