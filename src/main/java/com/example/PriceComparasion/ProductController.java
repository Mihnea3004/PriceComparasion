package com.example.PriceComparasion;


import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductSearchRepository productSearchRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public ProductController(ProductSearchRepository productSearchRepository, 
                             PriceHistoryRepository priceHistoryRepository,
                             ElasticsearchOperations elasticsearchOperations) {
        this.productSearchRepository = productSearchRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * Create a new product and its first price history entry.
     *
     * @param product The product to create.
     * @return The created product.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productSearchRepository.save(product);

        // Create initial price history entry
        PriceHistory initialPriceHistory = new PriceHistory();
        initialPriceHistory.setProductId(savedProduct.getId());
        initialPriceHistory.setDate(LocalDate.now()); // Use LocalDate
        initialPriceHistory.setPrice(savedProduct.getPrice());
        priceHistoryRepository.save(initialPriceHistory);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * Get a list of all products with optional sorting.
     *
     * @param sortBy Sort criteria: "low_to_high", "high_to_low".
     * @return A list of all products.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(defaultValue = "relevance") String sortBy) {
        Iterable<Product> products = productSearchRepository.findAll();
        List<Product> productList = new ArrayList<>();
        products.forEach(productList::add);

        // Sorting
        if ("low_to_high".equalsIgnoreCase(sortBy)) {
            productList.sort(Comparator.comparing(Product::getPrice));
        } else if ("high_to_low".equalsIgnoreCase(sortBy)) {
            productList.sort(Comparator.comparing(Product::getPrice).reversed());
        }

        return ResponseEntity.ok(productList);
    }

    /**
     * Get a product by ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return The product with the specified ID or 404 if not found.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        Optional<Product> product = productSearchRepository.findById(productId);
        return product.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing product and its price history.
     *
     * @param productId     The ID of the product to update.
     * @param productDetails The updated product details.
     * @return The updated product or 404 if the product does not exist.
     */
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @RequestBody Product productDetails) {
        Optional<Product> productOptional = productSearchRepository.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Add price history if price changes
            if (productDetails.getPrice() != product.getPrice()) {
                PriceHistory priceHistory = new PriceHistory();
                priceHistory.setProductId(productId);
                priceHistory.setDate(LocalDate.now());
                priceHistory.setPrice(productDetails.getPrice());
                priceHistoryRepository.save(priceHistory);
            }

            // Update product details
            product.setName(productDetails.getName());
            product.setCategory(productDetails.getCategory());
            product.setPrice(productDetails.getPrice());
            product.setLink(productDetails.getLink());
            product.setManufacturer(productDetails.getManufacturer());
            product.setAvailability(productDetails.getAvailability());

            Product updatedProduct = productSearchRepository.save(product);
            return ResponseEntity.ok(updatedProduct);
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a product by ID.
     *
     * @param productId The ID of the product to delete.
     * @return A response indicating the result of the operation.
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        if (productSearchRepository.existsById(productId)) {
            productSearchRepository.deleteById(productId);
            priceHistoryRepository.deleteAll(priceHistoryRepository.findByProductId(productId));
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Search for products by name using Elasticsearch.
     *
     * @param query The search query.
     * @param sortBy The sorting criteria: "low_to_high", "high_to_low".
     * @return A list of products matching the search criteria.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "relevance") String sortBy) {

        // Build the search query
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.match(t -> t.field("name").query(query)))
                .build();

        // Execute the search
        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);

        // Extract search results
        List<Product> products = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        // Sort results if requested
        if ("low_to_high".equalsIgnoreCase(sortBy)) {
            products.sort(Comparator.comparing(Product::getPrice));
        } else if ("high_to_low".equalsIgnoreCase(sortBy)) {
            products.sort(Comparator.comparing(Product::getPrice).reversed());
        }

        return ResponseEntity.ok(products);
    }
}
