package ru.bsuedu.cad.lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CategoryRequest {

    private static final Logger logger = LoggerFactory.getLogger(CategoryRequest.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;



    public void printCategoriesWithMoreThanOneProduct() {

        String sql = "SELECT c.name, COUNT(p.product_id) AS product_count " +
                "FROM CATEGORIES c " +
                "JOIN PRODUCTS p ON c.category_id = p.category_id " +
                "GROUP BY c.name " +
                "HAVING COUNT(p.product_id) > 1";

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            String categoryName = rs.getString("name");
            int productCount = rs.getInt("product_count");
            logger.info("Category: {}, Product Count: {}", categoryName, productCount);
            return null;
        });
    }
}
