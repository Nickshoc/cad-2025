
package ru.bsuedu.cad.lab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class ConcreteCategoryProvider implements CategoryProvider {

    private final ResourceFileReader reader;
    private final Parser parser;

    @Autowired
    public ConcreteCategoryProvider(ResourceFileReader reader, Parser parser) {
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public List<Category> getCategory() {
        String data = reader.readCategoryFile(); // Чтение category.csv
        return parser.parseCategory(data); // Парсинг данных
    }
}