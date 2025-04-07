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

    public List<Map<String, Object>> getAllProductsWithLocations() {
        List<Warehouse> warehouses = warehouseRepository.findAll();

        return warehouses.stream()
                .flatMap(warehouse -> warehouse.getProducts().stream()
                        .map(product -> {
                            Map<String, Object> productWithLocation = new HashMap<>();
                            productWithLocation.put("productId", product.getProductId());
                            productWithLocation.put("name", product.getName());
                            productWithLocation.put("category", product.getCategory());
                            productWithLocation.put("quantity", product.getQuantity());
                            productWithLocation.put("location", warehouse.getLocation());
                            return productWithLocation;
                        }))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getProductById(String productId) {
        List<Warehouse> warehouses = warehouseRepository.findByProductsProductId(productId);

        if (warehouses.isEmpty()) {
            throw new NoSuchElementException("Product not found");
        }

        // Finde das Produkt-Objekt
        Product product = warehouses.stream()
                .flatMap(warehouse -> warehouse.getProducts().stream())
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        // Extrahiere Lagerstandorte
        List<String> locations = warehouses.stream()
                .map(Warehouse::getLocation)
                .collect(Collectors.toList());

        // Ergebnis als Map zusammenbauen
        Map<String, Object> result = new HashMap<>();
        result.put("productId", product.getProductId());
        result.put("name", product.getName());
        result.put("category", product.getCategory());
        result.put("quantity", product.getQuantity());
        result.put("locations", locations);

        return result;
    }


    public void deleteProductFromWarehouse(String warehouseId, String productId) {
            Warehouse warehouse = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new RuntimeException("Warehouse not found"));
            warehouse.getProducts().removeIf(p -> p.getProductId().equals(productId));
            warehouseRepository.save(warehouse);
        }
}
