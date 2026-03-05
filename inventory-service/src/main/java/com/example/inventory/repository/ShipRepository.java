package com.example.inventory.repository;

import com.example.inventory.model.Ship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipRepository extends JpaRepository<Ship, String> {
}
