# Mongo_MidEng

## Paketstruktur

```
com.example.mongo
├── controller
│   └── WarehouseController.java
├── model
│   ├── Product.java
│   └── Warehouse.java
├── repository
│   └── WarehouseRepository.java
├── service
│   └── WarehouseService.java
└── DataSeeder.java
```

## Modelle

### Product.java

Das Product-Modell repräsentiert ein Produkt mit Eigenschaften wie ID, Name, Kategorie und Menge.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String productId;
    private String name;
    private String category;
    private int quantity;
}
```

### Warehouse.java

Das Warehouse-Modell repräsentiert ein Lager mit einer eindeutigen ID, einem Namen, einem Standort und einer Liste von Produkten.

```java
@Document(collection = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    @Id
    private String id;
    private String name;
    private String location;
    private List<Product> products;
}
```

## Repository

### WarehouseRepository.java

Das Repository-Interface bietet Datenbankzugriffsmethoden für Warehouse-Objekte. Es erweitert MongoRepository für grundlegende CRUD-Operationen und fügt eine benutzerdefinierte Methode hinzu.

```java
public interface WarehouseRepository extends MongoRepository<Warehouse, String> {
    // Findet alle Warehouses, die ein Produkt mit der angegebenen ID enthalten
    List<Warehouse> findByProductsProductId(String productId);
}
```

## Service

### WarehouseService.java

Der Service implementiert die Geschäftslogik und verwendet das Repository für Datenbankoperationen.

```java
@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    
    // Warehouse-Operationen
    public Warehouse addWarehouse(Warehouse warehouse) { /* ... */ }
    public List<Warehouse> getAllWarehouses() { /* ... */ }
    public Optional<Warehouse> getWarehouseById(String id) { /* ... */ }
    public void deleteWarehouseById(String id) { /* ... */ }
    
    // Produkt-Operationen
    public Warehouse addProductToWarehouse(String warehouseId, Product product) { /* ... */ }
    public Map<String, Object> getProductById(String productId) { /* ... */ }
    public void deleteProductFromWarehouse(String warehouseId, String productId) { /* ... */ }
    
    // Komplexe Abfrage: Alle Produkte mit ihren Standorten
    public List<Map<String, Object>> getAllProductsWithLocations() { /* ... */ }
}
```

Wichtige Funktionen:

```java
// Produkt zu einem Warehouse hinzufügen
public Warehouse addProductToWarehouse(String warehouseId, Product product) {
    Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new RuntimeException("Warehouse not found"));
    warehouse.getProducts().add(product);
    return warehouseRepository.save(warehouse);
}

// Produkt mit Standorten finden
public Map<String, Object> getProductById(String productId) {
    List<Warehouse> warehouses = warehouseRepository.findByProductsProductId(productId);
    
    if (warehouses.isEmpty()) {
        throw new NoSuchElementException("Product not found");
    }
    
    // Produkt-Objekt finden
    Product product = warehouses.stream()
            .flatMap(warehouse -> warehouse.getProducts().stream())
            .filter(p -> p.getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Product not found"));
    
    // Standorte extrahieren
    List<String> locations = warehouses.stream()
            .map(Warehouse::getLocation)
            .collect(Collectors.toList());
    
    Map<String, Object> result = new HashMap<>();
    result.put("productId", product.getProductId());
    result.put("name", product.getName());
    result.put("category", product.getCategory());
    result.put("quantity", product.getQuantity());
    result.put("locations", locations);
    
    return result;
}
```

## Controller

### WarehouseController.java

Der Controller stellt REST-Endpunkte bereit und nutzt den Service für die Ausführung von Geschäftslogik.

```java
@RestController
@RequestMapping("/api")
public class WarehouseController {
    private WarehouseService warehouseService;

    // Warehouse-Endpunkte
    @PostMapping
    public ResponseEntity<Warehouse> addWarehouse(@RequestBody Warehouse warehouse) { /* ... */ }
    
    @GetMapping("/warehouse")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() { /* ... */ }
    
    @GetMapping("/warehouses/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable String id) { /* ... */ }
    
    @DeleteMapping("/warehouse/{id}")
    public ResponseEntity<Warehouse> deleteWarehouse(@PathVariable String id) { /* ... */ }

    // Produkt-Endpunkte  
    @PostMapping("/product/{warehouseId}")
    public ResponseEntity<Warehouse> addProductToWarehouse(@RequestBody Product product, 
                                                        @PathVariable String warehouseId) { /* ... */ }
    
    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable String id) { /* ... */ }
    
    @DeleteMapping("/product/{warehouseId}/{productId}")
    public ResponseEntity<Void> deleteProductFromWarehouse(@PathVariable String warehouseId, 
                                                        @PathVariable String productId) { /* ... */ }
}
```

## Daten-Initialisierung

### DataSeeder.java

Der DataSeeder befüllt die Datenbank mit Testdaten beim Anwendungsstart.

```java
@Component
public class DataSeeder implements CommandLineRunner {
    private final WarehouseRepository warehouseRepository;
    
    @Override
    public void run(String... args) {
        warehouseRepository.deleteAll();
        
        // Beispieldaten erstellen
        List<Product> products = Arrays.asList(
            new Product("p1", "Schraube", "Baumaterial", 100),
            // weitere Produkte...
        );
        
        Warehouse warehouse1 = new Warehouse("warehouse1", "Lager Nord", "Berlin", products.subList(0, 5));
        Warehouse warehouse2 = new Warehouse("warehouse2", "Lager Süd", "München", products.subList(5, 10));
        
        warehouseRepository.saveAll(Arrays.asList(warehouse1, warehouse2));
    }
}
```

## Technische Highlights

1. **Dokumentorientierte Datenbank**: MongoDB wird für die Speicherung von verschachtelten Objekten (Warehouses mit Products) verwendet.

2. **Stream API**: Java Streams werden für komplexe Datenverarbeitung eingesetzt:
   ```java
   // Beispiel: Produktsuche über mehrere Warehouses
   warehouses.stream()
       .flatMap(warehouse -> warehouse.getProducts().stream())
       .filter(p -> p.getProductId().equals(productId))
       .findFirst()
   ```

3. **Exception Handling**: Spezifische Fehlerbehandlung für verschiedene Anwendungsfälle
   ```java
   .orElseThrow(() -> new NoSuchElementException("Product not found"));
   ```

4. **Optional API**: Sicherer Umgang mit potenziell nicht vorhandenen Werten
   ```java
   return warehouseRepository.findById(id)
       .map(ResponseEntity::ok)
       .orElse(ResponseEntity.notFound().build());
   ```

5. **Lombok**: Reduziert Boilerplate-Code durch Annotationen wie `@Data`, `@NoArgsConstructor` und `@AllArgsConstructor`
