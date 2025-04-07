package com.example.mongo.controller;

import com.example.mongo.model.Product;
import com.example.mongo.model.Warehouse;
import com.example.mongo.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class WarehouseController {
    private WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    public ResponseEntity<Warehouse> addWarehouse(@RequestBody Warehouse warehouse) {
        return ResponseEntity.ok(warehouseService.addWarehouse(warehouse));
    }

    @GetMapping("/warehouse")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/warehouses/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable String id) {
        return warehouseService.getWarehouseById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/warehouse/{id}")
    public ResponseEntity<Warehouse> deleteWarehouse(@PathVariable String id) {
        warehouseService.deleteWarehouseById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/product/{warehouseId}")
    public ResponseEntity<Warehouse> addProductToWarehouse(@RequestBody Product product, @PathVariable String warehouseId) {
        return ResponseEntity.ok(warehouseService.addProductToWarehouse(warehouseId, product));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable String id) {
        try{
            return ResponseEntity.ok(warehouseService.getProductById(id));
        } catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/product/{warehouseId}/{productId}")
    public ResponseEntity<Void> deleteProductFromWarehouse(@PathVariable String warehouseId, @PathVariable String productId) {
        warehouseService.deleteProductFromWarehouse(warehouseId, productId);
        return ResponseEntity.noContent().build();
    }
}
