
package ru.bsuedu.cad.lab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVParser implements Parser {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Product> parse(String csvData) {
        List<Product> products = new ArrayList<>();
        String[] lines = csvData.split("\n");

        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            Product product = new Product(
                    Integer.parseInt(fields[0]), // productId
                    fields[1],                   // name
                    fields[2],                   // description
                    Integer.parseInt(fields[3]), // categoryId
                    new BigDecimal(fields[4]),   // price
                    Integer.parseInt(fields[5]), // stockQuantity
                    fields[6],                   // imageUrl
                    LocalDate.parse(fields[7],DATE_FORMATTER), // createdAt
                    LocalDate.parse(fields[8],DATE_FORMATTER)  // updatedAt
            );
            products.add(product);
        }
        return products;
    }
}

