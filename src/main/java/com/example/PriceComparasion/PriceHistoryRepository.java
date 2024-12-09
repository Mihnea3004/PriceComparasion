package com.example.PriceComparasion;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PriceHistoryRepository extends ElasticsearchRepository<PriceHistory, String> {

    // Custom query method to find price history by productId
    List<PriceHistory> findByProductId(String productId);
}
