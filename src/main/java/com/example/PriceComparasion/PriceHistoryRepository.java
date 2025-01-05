package com.example.PriceComparasion;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends ElasticsearchRepository<PriceHistory, String> {
    List<PriceHistory> findByProductId(String productId);
}
