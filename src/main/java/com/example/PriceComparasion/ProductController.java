package com.example.PriceComparasion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final PriceHistoryRepository priceHistoryRepository;

    private static final String INDEX_NAME = "products";

    public ProductController(RestClient restClient, ObjectMapper objectMapper, PriceHistoryRepository priceHistoryRepository) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Request request = new Request("POST", "/" + INDEX_NAME + "/_doc/" + product.getId());
            request.setJsonEntity(objectMapper.writeValueAsString(product));
            Response response = restClient.performRequest(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.CREATED.value()) {
            	savePriceHistory(product.getId(),product.getPrice());
                return ResponseEntity.status(HttpStatus.CREATED).body(product);
            }
        } catch (IOException e) {
            logger.error("Error creating product", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        try {
            Request request = new Request("GET", "/" + INDEX_NAME + "/_doc/" + productId);
            Response response = restClient.performRequest(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                String responseBody = EntityUtils.toString(response.getEntity());
                Map<String, Object> source = objectMapper.readValue(responseBody, Map.class);
                Product product = objectMapper.convertValue(source.get("_source"), Product.class);
                return ResponseEntity.ok(product);
            }
        } catch (IOException e) {
            logger.error("Error fetching product", e);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    private void savePriceHistory(String productId, Double oldPrice) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setPrice(oldPrice);
        priceHistory.setId(UUID.randomUUID().toString());
        priceHistory.setProductId(productId);
        priceHistory.setDate(LocalDate.now());
        priceHistoryRepository.save(priceHistory);
    }
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @RequestBody Product product) {
        try {
            Request getRequest = new Request("GET", "/" + INDEX_NAME + "/_doc/" + productId);
            Response getResponse = restClient.performRequest(getRequest);

            if (getResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                String existingProductJson = EntityUtils.toString(getResponse.getEntity());
                Product existingProduct = objectMapper.readValue(existingProductJson, Product.class);

                savePriceHistory(productId, product.getPrice());
                

                product.setPrice(product.getPrice());

                Request updateRequest = new Request("PUT", "/" + INDEX_NAME + "/_doc/" + productId);
                updateRequest.setJsonEntity(objectMapper.writeValueAsString(product));
                restClient.performRequest(updateRequest);

                return ResponseEntity.ok(product);
            }
        } catch (IOException e) {
            logger.error("Error updating product", e);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }



    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        try {
            Request deleteRequest = new Request("DELETE", "/" + INDEX_NAME + "/_doc/" + productId);
            Response deleteResponse = restClient.performRequest(deleteRequest);

            if (deleteResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                return ResponseEntity.noContent().build();
            }
        } catch (IOException e) {
            logger.error("Error deleting product", e);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(defaultValue = "low_to_high") String sortBy) {
        try {
            String sortClause = "";
            if ("low_to_high".equalsIgnoreCase(sortBy)) {
                sortClause = "\"sort\": [{\"price\": \"asc\"}]";
            } else if ("high_to_low".equalsIgnoreCase(sortBy)) {
                sortClause = "\"sort\": [{\"price\": \"desc\"}]";
            }

            String searchJson = """
                {
                  %s
                }
                """.formatted(sortClause);

            Request request = new Request("POST", "/" + INDEX_NAME + "/_search");
            request.setJsonEntity(searchJson);

            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

            List<Map<String, Object>> hits = (List<Map<String, Object>>) ((Map<String, Object>) responseMap.get("hits")).get("hits");
            List<Product> products = new ArrayList<>();
            for (Map<String, Object> hit : hits) {
                Product product = objectMapper.convertValue(hit.get("_source"), Product.class);
                products.add(product);
            }

            return ResponseEntity.ok(products);
        } catch (IOException e) {
            logger.error("Error searching products", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
