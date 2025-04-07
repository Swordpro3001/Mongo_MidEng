package com.example.mongo.service;

import com.example.mongo.model.Product;
import com.example.mongo.model.Warehouse;
import com.example.mongo.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WarehouseService {
        private final WarehouseRepository warehouseRepository;

        public WarehouseService(WarehouseRepository warehouseRepository) {
            this.warehouseRepository = warehouseRepository;
        }

        public Warehouse addWarehouse(Warehouse warehouse) {
            return warehouseRepository.save(warehouse);
        }

        public List<Warehouse> getAllWarehouses() {
            return warehouseRepository.findAll();
        }

        public Optional<Warehouse> getWarehouseById(String id) {
            return warehouseRepository.findById(id);
        }

        public void deleteWarehouseById(String id) {
            warehouseRepository.deleteById(id);
        }

        public Warehouse addProductToWarehouse(String warehouseid, Product product) {
            Warehouse warehouse = warehouseRepository.findById(warehouseid)
                        .orElseThrow(() -> new RuntimeException("Warehouse not found")) ;
            warehouse.getProducts().add(product);
            return warehouseRepository.save(warehouse);
        }
}
