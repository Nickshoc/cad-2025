
package ru.bsuedu.cad.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ConsoleTableRenderer implements Renderer {
    private final ProductProvider provider;

    @Autowired
    public ConsoleTableRenderer(ProductProvider provider) {
        this.provider = provider;
    }

    @Override
    public void render() {
        List<Product> products = provider.getProducts();

        System.out.printf("%-10s %-30s %-30s %-10s %-10s %-10s %-40s %-20s %-20s%n",
                "ID", "Name", "Description", "Category", "Price", "Stock", "Image URL", "Created At", "Updated At");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        for (Product product : products) {
            System.out.printf("%-10d %-30s %-34s %-6d %-10s %-10d %-40s %-20s %-20s%n",
                    product.getProductId(),
                    product.getName(),
                    product.getDescription(),
                    product.getCategoryId(),
                    product.getPrice(),
                    product.getStockQuantity(),
                    product.getImageUrl(),
                    product.getCreatedAt(),
                    product.getUpdatedAt());
        }
    }
}

