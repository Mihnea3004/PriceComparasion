package com.example.PriceComparasion;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "products")
public class Product {

    @Id
    private String id;
    private String name;
    private String category;
    private double price;
    private String link;
    private String manufacturer;
    private boolean available;

    public Product(String id, String name, String category, double price, String link, String manufacturer, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.link = link;
        this.manufacturer = manufacturer;
        this.available = available;
    }
    
    public String getId() {
    	return id;
    }
    public String getName() {
    	return name;
    }
    public String getCategory() {
    	return category;
    }
    public double getPrice() {
    	return price;
    }
    public String getLink() {
    	return link;
    }
    public String getManufacturer() {
    	return manufacturer;
    }
    public boolean getAvailability() {
    	return available;
    }
    public void setId(String id) {
    	this.id = id;
    }
    public void setName(String name) {
    	this.name = name;
    }
    public void setCategory(String category) {
    	this.category = category;
    }
    public void setPrice(Double price) {
    	this.price = price;
    }
    public void setLink(String link) {
    	this.link = link;
    }
    public void setManufacturer(String manufacturer) {
    	this.manufacturer = manufacturer;
    }
    public void setAvailability(boolean available) {
    	this.available = available;
    }
}
