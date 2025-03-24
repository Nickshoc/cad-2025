package ru.bsuedu.cad.lab;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Component
public class ResourceFileReader implements Reader {
    @Value("${product.path}")
    private String productFilePath;

    @Value("${category.path}")
    private String categoryFilePath;

    @Override
    public String read() {
        return readFile(productFilePath);
    }

    public String readCategoryFile() {
        return readFile(categoryFilePath);
    }
    private String readFile(String filePath) {
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }
    @EventListener
    public void init(ContextRefreshedEvent event) {
        System.out.println("ResourceFileReader initialized at: " + LocalDateTime.now());
    }
}

