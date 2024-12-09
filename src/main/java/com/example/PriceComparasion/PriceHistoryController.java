package com.example.PriceComparasion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/price-history")
public class PriceHistoryController {

    private final PriceHistoryRepository priceHistoryRepository;

    public PriceHistoryController(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    /**
     * Get the price history for a specific product.
     *
     * @param productId The ID of the product.
     * @return A list of price history entries for the product.
     */
    @GetMapping
    public ResponseEntity<List<PriceHistory>> getPriceHistory(@PathVariable String productId) {
        List<PriceHistory> history = priceHistoryRepository.findByProductId(productId);
        return ResponseEntity.ok(history);
    }
}
