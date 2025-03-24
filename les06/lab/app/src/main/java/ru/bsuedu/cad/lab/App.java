package ru.bsuedu.cad.lab;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;


@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "ru.bsuedu.cad.lab")
@EnableAspectJAutoProxy
public class App
{
    public static void main(String[] args)
    {
        ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
        DataBaseRenderer renderer = context.getBean(DataBaseRenderer.class);
        renderer.render();
        CategoryRequest categoryRequest = context.getBean(CategoryRequest.class);
        categoryRequest.printCategoriesWithMoreThanOneProduct();
    }
}
