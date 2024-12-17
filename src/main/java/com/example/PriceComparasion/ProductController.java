package com.example.PriceComparasion;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.elastic.clients.elasticsearch._types.query_dsl.Query.Builder;
import co.elastic.clients.util.ObjectBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductSearchRepository productRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    public ProductController(ProductSearchRepository productRepository, ElasticsearchTemplate elasticsearchTemplate) {
        this.productRepository = productRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    /**
     * Create a new product.
     *
     * @param product The product to create.
     * @return The created product.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * Get a product by ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return The product or 404 if not found.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing product.
     *
     * @param productId The ID of the product to update.
     * @param product   The updated product details.
     * @return The updated product or 404 if not found.
     */
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @RequestBody Product product) {
        if (productRepository.existsById(productId)) {
            product.setId(productId); // Ensure the product ID is preserved
            Product updatedProduct = productRepository.save(product);
            return ResponseEntity.ok(updatedProduct);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a product by ID.
     *
     * @param productId The ID of the product to delete.
     * @return No content or 404 if not found.
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get all products with optional sorting.
     *
     * @param sortBy The sorting criteria: "low_to_high" or "high_to_low".
     * @return A list of products.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(defaultValue = "low_to_high") String sortBy) {
        Iterable<Product> products = productRepository.findAll();
        List<Product> productList = (List<Product>) products;

        // Sorting
        if ("low_to_high".equalsIgnoreCase(sortBy)) {
            productList.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        } else if ("high_to_low".equalsIgnoreCase(sortBy)) {
            productList.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        }

        return ResponseEntity.ok(productList);
    }
}
