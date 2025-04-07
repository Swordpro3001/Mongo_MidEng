package com.example.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import com.example.mongo.model.Warehouse;

public interface WarehouseRepository extends MongoRepository<Warehouse, String> {
    List<Warehouse> findByProductsProductId(String productId);
}
