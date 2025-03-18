package ru.bsuedu.cad.lab;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component ("htmlRenderer")
public class HTMLTableRenderer implements Renderer {

    private final ProductProvider provider;

    public HTMLTableRenderer(ProductProvider provider) {
        this.provider = provider;
    }

    @Override
    public void render() {
        List<Product> products = provider.getProducts();
        String html = generateHTML(products);
        saveToFile("products.html", html);
    }

    private String generateHTML(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("<title>Product List</title>\n");
        sb.append("<style>\n");
        sb.append("table { border-collapse: collapse; width: 100%; }\n");
        sb.append("th, td { border: 1px solid black; padding: 8px; text-align: left; }\n");
        sb.append("th { background-color: #f2f2f2; }\n");
        sb.append("</style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<h1>Product List</h1>\n");
        sb.append("<table>\n");
        sb.append("<tr>\n");
        sb.append("<th>Product ID</th>\n");
        sb.append("<th>Name</th>\n");
        sb.append("<th>Description</th>\n");
        sb.append("<th>Category ID</th>\n");
        sb.append("<th>Price</th>\n");
        sb.append("<th>Stock Quantity</th>\n");
        sb.append("<th>Image URL</th>\n");
        sb.append("<th>Created At</th>\n");
        sb.append("<th>Updated At</th>\n");
        sb.append("</tr>\n");

        for (Product product : products) {
            sb.append("<tr>\n");
            sb.append("<td>").append(product.getProductId()).append("</td>\n");
            sb.append("<td>").append(product.getName()).append("</td>\n");
            sb.append("<td>").append(product.getDescription()).append("</td>\n");
            sb.append("<td>").append(product.getCategoryId()).append("</td>\n");
            sb.append("<td>").append(product.getPrice()).append("</td>\n");
            sb.append("<td>").append(product.getStockQuantity()).append("</td>\n");
            sb.append("<td>").append(product.getImageUrl()).append("</td>\n");
            sb.append("<td>").append(product.getCreatedAt()).append("</td>\n");
            sb.append("<td>").append(product.getUpdatedAt()).append("</td>\n");
            sb.append("</tr>\n");
        }

        sb.append("</table>\n");
        sb.append("</body>\n");
        sb.append("</html>");

        return sb.toString();
    }

    private void saveToFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
            System.out.println("HTML file write succeed: " + fileName);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
