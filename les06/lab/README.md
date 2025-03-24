# Отчет о лаботаротоной работе

## Цель работы
Изучить технологии работы с базами данными

## Выполнение работы
1. Скопируйте результат выполнения лабораторной работы № 2 в директорию /les06/lab/

2. Подключите к приложению встраиваемую базу данных H2 используя EmbeddedDatabaseBuilder

3. Напишите SQL скрипт создающие две таблицы "Продукты" (PRODUCTS) и "Категории" (CATEGORIES) (не забудьте про внешние ключи).

4. Настройте EmbeddedDatabaseBuilder так, чтобы он при старте приложения выполнял данный скрипт и создавал в базе данных таблицы CATEGORIES и PRODUCTS.

5. Для таблицы "Категории" создайте Java класс Category, для моделирования данной сущности (аналогичный классу Product). И класс ConcreteCategoryProvider, аналогичный ConcreteProductProvider, данный класс должен
предоставлять данные из CSV файла category.csv. CSV-файл должен располагаться в директории src/main/resources вашего приложения

6. Добавьте еще одну имплементацию интерфейса Renderer - DataBaseRenderer которая сохраняет данные считанные из SCV файлов в таблицы базы данных. Реализация DataBaseRenderer должна использоваться пол умолчанию.

7. Реализуйте класс CategoryRequest, данный класс должен выполнять запрос к базе данных получающий следующую информацию - список категорий количество товаров в которых больше единицы. Данная информация должна выводиться в консоль с помощью библиотеки для логирования logback, уровень лога INFO.

8. Приложение должно запускаться с помощью команды gradle run, выводить необходимую информацию в консоль и успешно завершаться.

9. Оформите отчет о выполнении лабораторной работы в виде файла README.md в директории les06/lab. Отчет должен содержать обновленную UML-диаграмму классов в формате mermaid.

![image](https://github.com/user-attachments/assets/d323dfb6-99fb-4466-8d1b-fec526aa6bfe)

``` mermaid

classDiagram
    note "Товары для зоомагазина"
    CategoryRequest 
    Reader <|.. ResourceFileReader
    Parser <|.. CSVParser
    ProductProvider <|.. ConcreteProductProvider
    ConcreteProductProvider o-- Parser
    ConcreteProductProvider o-- Reader
    Renderer <|.. DataBaseReader
    DataBaseReader o-- ProductProvider
    DataBaseReader o-- CategoryProvider
    ConcreteCategoryProvider o-- Reader
    CategoryProvider <|.. ConcreteCategoryProvider
    ProductProvider .. Product
    Parser .. Product
    ProductProvider .. Category
    Parser .. Category

    

    class  Product {
        +long productId
        +String name
        +String description
        +int categoryId
        +BigDecimal price
        +int stockQuantity
        +String imageUrl
        +Date createdAt
        +Date updatedAt
    }

    class Category {
        +int categoryId
        +String name
        +String description
    }


    class  Reader{
        + String read()
    }
    <<interface>> Reader

    class ResourceFileReader {
        -String productFilePath
        -String categoryFilePath
        + String read()
        + String readCategoryFile()
        + String readFile(String filePath)
    }

    class  Parser{
        + List[Product] parse(String)
    }
    <<interface>> Parser

    class CSVParser {
        + List[Product] parse(String)
    }

    class  Renderer{
        +void render()
    }
    <<interface>> Renderer


    class ProductProvider {
        + List[Product] getProducts()
    }
    <<interface>> ProductProvider

    class ConcreteProductProvider{
        - Reader reader
        - Parser parser
       + List[Product] getProducts()
    }

    class DataBaseReader {
        -JdbcTemplate jdbcTemplate
        -ConcreteCategoryProvider categoryProvider
        -ConcreteProductProvider productProvider
        + void render()
    }

    class CategoryProvider {
       + List<Category> getCategory()
    }

    class ConcreteCategoryProvider {
        - Reader reader
        - Parser parser
       +List[Category] getCategories()
    }

    class CategoryRequest {
        -Logger logger
        -JdbcTemplate jdbcTemplate
        +void printCategories() 
    }

```

## Выводы
Были приобретены практические навыки технологиям работы с базами данными

## Контрольные вопросы

1.

JDBC (Java Database Connectivity) является основным способом взаимодействия с базами данных в Java-приложениях. В Spring Framework поддержка JDBC интегрирована через несколько компонентов, что упрощает работу с базами данных, снижает уровень шума и повышает читаемость и надежность кода.

Spring предоставляет мощную инфраструктуру для работы с JDBC, что позволяет упростить процесс подключения, выполнения запросов, обработки ошибок и управления транзакциями.

2.

org.springframework.jdbc.core

3.

Настроить сервер базы данных.

Добавить зависимости. 

Настроить свойства соединения с базой данных. 

Определить сущность.

Создать интерфейс репозитория. 

4. 

JdbcTemplate — это один из ключевых компонентов Spring Framework, предназначенный для упрощения работы с базой данных. Он предоставляет удобный и мощный API для выполнения SQL-запросов, обработки результатов и управления транзакциями, избавляя разработчика от необходимости вручную управлять ресурсами, такими как соединения с базой данных, и обработки исключений.

Преимущества JdbcTemplate:

Простота использования: Весь процесс работы с JDBC значительно упрощен. Вместо того, чтобы писать вручную код для получения соединения, создания и выполнения SQL-запросов, обработки исключений и освобождения 
ресурсов, Spring делает это за вас.

Обработка исключений: JdbcTemplate обрабатывает все исключения JDBC, преобразуя их в более удобные исключения Spring.

Управление ресурсами: Внутри JdbcTemplate используются механизмы управления ресурсами, такие как соединения, которые автоматически закрываются, избегая утечек памяти.

5.

@Override
    public Persona getPersonaById(Long id) {

        try {
           var connection =   dataSource.getConnection();
           var statement = connection.prepareStatement("SELECT * FROM personas WHERE id = ?");
           statement.setLong(1, id);
           var resultSet = statement.executeQuery();
           LOGGER.info(resultSet.toString());
           while (resultSet.next()) {
            Long personaId = resultSet.getLong(1);
            String name = resultSet.getString(2);
            String arcana = resultSet.getString(3);
            int level = resultSet.getInt(4);
            int strength = resultSet.getInt(5);
            int magic = resultSet.getInt(6);
            int endurance = resultSet.getInt(7);
            int agility = resultSet.getInt(8);
            int luck = resultSet.getInt(9);
            long characterId = resultSet.getLong(10);

            return new Persona(personaId, name,arcana, level,strength,magic,endurance,agility,luck, characterId);
        }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return new Persona();

    }

6.

    private RowMapper<Persona> personaRowMapper() {
        return (rs, rowNum) -> new Persona(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("arcana"),
                rs.getInt("level"),
                rs.getInt("strength"),
                rs.getInt("magic"),
                rs.getInt("endurance"),
                rs.getInt("agility"),
                rs.getInt("luck"),
                rs.getLong("character_id"));
    }


7.

jdbcTemplate.update(
    "INSERT INTO schema.tableName (column1, column2) VALUES (?, ?)",
    var1, var2
);

8.

jdbcTemplate.update(
        "insert into t_actor (first_name, last_name) values (?, ?)",
        "Leonor", "Watling");

jdbcTemplate.update("delete from t_actor where id = ?", actorId.toLong())

9.

JdbcTemplate: Это основное средство для работы с JDBC в Spring. Он инкапсулирует работу с соединениями, управляет обработкой исключений и упрощает выполнение SQL-запросов и обновлений.

10. 

JdbcDaoSupport — это абстрактный класс в Spring, который упрощает использование JdbcTemplate для доступа к базе данных. Этот класс предоставляет поддержку для работы с JdbcTemplate, что позволяет вам сосредоточиться на бизнес-логике и не заботиться о настройке соединений или обработке ошибок.

