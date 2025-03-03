package ru.bsuedu.cad.lab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Product {
    public int product_id;
    public String name;
    public String description;
    public int category_id;
    public BigDecimal price;
    public int stock_quantity;
    public String image_url;
    public LocalDate created_at;
    public LocalDate updated_at;

    public Product(int product_id, String name, String description, int category_id, BigDecimal price, int stock_quantity, String image_url, LocalDate created_at, LocalDate updated_at )
    {
        this.product_id = product_id;
        this.name = name;
        this.description = description;
        this.category_id = category_id;
        this.price = price;
        this.stock_quantity = stock_quantity;
        this.image_url = image_url;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getProductId() {
        return product_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryId() {
        return category_id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stock_quantity;
    }

    public String getImageUrl() {
        return image_url;
    }

    public LocalDate getCreatedAt() {
        return created_at;
    }

    public LocalDate getUpdatedAt() {
        return updated_at;
    }

    public void setProductId(int i) {
    }

    public void setName(String field) {
    }

    public void setDescription(String field) {
    }

    public void setCategoryId(int i) {
    }

    public void setPrice(BigDecimal bigDecimal) {
    }

    public void setStockQuantity(int i) {
    }

    public void setImageUrl(String field) {
    }

    public void setCreatedAt(LocalDateTime parse) {
    }

    public void setUpdatedAt(LocalDateTime parse) {
    }
}