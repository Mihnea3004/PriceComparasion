package com.example.PriceComparasion;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

@Document(indexName = "price_history")
public class PriceHistory {

    @Id
    private String id;
    private String productId; // ID of the associated product
    
    private Long date;   // Date of the price entry
    private double price;     // Price on the given date

    public PriceHistory(String id, String productId, Long date, double price) {
        this.id = id;
        this.productId = productId;
        this.date = date;
        this.price = price;
    }

    public PriceHistory() {
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public LocalDate getDate() {
        return Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setDate(LocalDate date) {
        this.date = date.getLong(ChronoField.EPOCH_DAY);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

