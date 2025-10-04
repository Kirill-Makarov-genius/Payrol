package com.example.Payrol.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.example.Payrol.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{


}
