package com.example.mongo;


import com.example.mongo.model.Product;
import com.example.mongo.model.Warehouse;
import com.example.mongo.repository.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final WarehouseRepository warehouseRepository;

    public DataSeeder(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public void run(String... args) {
        warehouseRepository.deleteAll();

        // 10 Products in 3 Categories
        List<Product> products = Arrays.asList(
                new Product("p1", "Schraube", "Baumaterial", 100),
                new Product("p2", "Nagel", "Baumaterial", 200),
                new Product("p3", "Hammer", "Werkzeug", 50),
                new Product("p4", "Säge", "Werkzeug", 30),
                new Product("p5", "Bohrer", "Werkzeug", 40),
                new Product("p6", "Holzplatte", "Baumaterial", 80),
                new Product("p7", "Ziegel", "Baumaterial", 500),
                new Product("p8", "Farbeimer", "Malerei", 60),
                new Product("p9", "Pinsel", "Malerei", 120),
                new Product("p10", "Rolle", "Malerei", 90)
        );

        Warehouse warehouse1 = new Warehouse("warehouse1", "Lager Nord", "Berlin", products.subList(0, 5));
        Warehouse warehouse2 = new Warehouse("warehouse2", "Lager Süd", "München", products.subList(5, 10));

        warehouseRepository.saveAll(Arrays.asList(warehouse1, warehouse2));

        System.out.println("Seeded 10 products across 2 warehouses.");
    }
}