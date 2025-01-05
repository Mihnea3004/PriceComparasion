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

    @GetMapping
    public ResponseEntity<List<PriceHistory>> getPriceHistory(@PathVariable String productId) {
        List<PriceHistory> histories = priceHistoryRepository.findByProductId(productId);
        if (histories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(histories);
    }
}


