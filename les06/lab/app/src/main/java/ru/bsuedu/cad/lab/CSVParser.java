
package ru.bsuedu.cad.lab;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVParser implements Parser {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Product> parse(String csvData) {
        List<Product> products = new ArrayList<>();
        String[] lines = csvData.split("\n");

        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            Product product = new Product(
                    Integer.parseInt(fields[0]), fields[1], fields[2], Integer.parseInt(fields[3]), new BigDecimal(fields[4]), Integer.parseInt(fields[5]), fields[6], LocalDate.parse(fields[7], DATE_FORMATTER), LocalDate.parse(fields[8], DATE_FORMATTER)
            );
            products.add(product);
        }
        return products;
    }

    @Override
    public List<Category> parseCategory(String data) {
        List<Category> categories = new ArrayList<>();
        String[] lines = data.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            Category category = new Category(Integer.parseInt(fields[0]), fields[1], fields[2]);
            categories.add(category);
        }
        return categories;
    }
}


