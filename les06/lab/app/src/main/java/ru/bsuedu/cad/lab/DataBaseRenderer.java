package ru.bsuedu.cad.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataBaseRenderer implements Renderer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConcreteCategoryProvider categoryProvider;

    @Autowired
    private ConcreteProductProvider productProvider;

    @Override
    public void render() {


        List<Category> categories = categoryProvider.getCategory();
        categories.forEach(category -> jdbcTemplate.update("INSERT INTO CATEGORIES (category_id, name, description) VALUES (?, ?, ?)", category.getCategory_id(), category.getName(), category.getDescription()));


        List<Product> products = productProvider.getProducts();
        products.forEach(product -> jdbcTemplate.update("INSERT INTO PRODUCTS (product_id, name, description, category_id, price, stock_quantity, image_url, created_at, updated_at) VALUES (?, ?, ?, ?, ?,?,?,?,?)",
                product.getProductId(), product.getName(), product.getDescription(), product.getCategoryId(), product.getPrice(), product.getStockQuantity(), product.getImageUrl(), product.getCreatedAt(), product.getUpdatedAt()));
    }
}
