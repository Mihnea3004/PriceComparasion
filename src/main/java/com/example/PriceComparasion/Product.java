package com.example.PriceComparasion;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "products")
public class Product {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Double)
    private double price;

    @Field(type = FieldType.Keyword)
    private String manufacturer;

    @Field(type = FieldType.Boolean)
    private boolean available;
    
    @Field(type = FieldType.Keyword)
    private String link;

    // Getters and Setters

    
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
