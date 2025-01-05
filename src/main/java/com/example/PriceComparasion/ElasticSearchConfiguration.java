package com.example.PriceComparasion;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;

@Configuration
public class ElasticSearchConfiguration {

    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticSearchConfiguration(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Bean
    public Boolean configurePriceHistoryIndex() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(PriceHistory.class);

        if (indexOps.exists()) {
            indexOps.delete();
        }
        indexOps.create();

        indexOps.putMapping(indexOps.createMapping(PriceHistory.class));

        return true; 
    }
}
