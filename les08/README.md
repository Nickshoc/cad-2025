# Отчет о лаботаротоной работе

## Цель работы
Изучить технологии работы с базами данных. JPA. Spring Data

## Выполнение работы
Созданий новое приложение или скопируйте результат выполнения лабораторной работы №3 в директорию /les08/lab/. Изменений будет много, возможно для вас будет проще создать проект заново.

Создайте DataSource соответствующий следующим требованиям

Должна использоваться база данных H2
Для реализации DataSource необходимо использовать библиотеку HikariCP, а именно HikariDataSource
Для работы с базой данных должна использоваться библиотека HIbernate, использующая технологию ORM
Схема схема данных должна создаваться автоматически на основании JPA сущностей.
Структура пакетов проекта должна иметь следующий вид

ru.bsuedu.cad.lab - основной пакет
ru.bsuedu.cad.lab.entity - JPA сущности
ru.bsuedu.cad.lab.repository - репозитории
ru.bsuedu.cad.lab.service - сервисы
ru.bsuedu.cad.lab.app - приложение
В пакете ru.bsuedu.cad.lab.entity создайте JPA сущности для следующей схемы базы данных.


В пакете ru.bsuedu.cad.lab.repository реализуйте репозитории для каждой сущности. Репозитории содержать методы по созданию, получение записи по идентификатору и получения всех записей для каждой сущности.

В пакете ru.bsuedu.cad.lab.service создайте сервисы для создания заказа и получению списка всех заказов.

В пакете ru.bsuedu.cad.lab.app реализуйте клиент для сервиса создания заказа, который создает новый заказ. Создание заказа должно выполняться в рамках транзакции. Выведите информацию о создании заказа в лог. Докажите, что заказ сохранился в базе данных. (Для того, чтобы создать заказ, необходимо заполнить таблицы базы данных на основании CSV файлов (category.csv, customer.csv, product.csv). Сделайте это любым удобным для вас способом. )

Приложение должно запускаться с помощью команды gradle run, выводить необходимую информацию в консоль и успешно завершаться.
Оформите отчет о выполнении лабораторной работы в виде файла README.md в директории les08/lab. Отчет должен содержать обновленную UML-диаграмму классов в формате mermaid.

![image](https://github.com/user-attachments/assets/4d29d535-3b3c-4a0e-b53b-93e8a6699182)

![image](https://github.com/user-attachments/assets/617e0c59-1158-419e-9f33-2e34256e88d2)


## Выводы 
Были изучены технологии работы с базами данных.

## Контрольные вопросы
1. Java Persistence API (JPA) — это спецификация для работы с объектно-реляционными данными в Java-приложениях. Она определяет стандартизированные интерфейсы для маппинга объектов Java на реляционные базы данных, но не является конкретной реализацией.
2. 
JPA — это стандартный интерфейс программирования для работы с объектно-реляционным отображением (ORM) в Java. Он предоставляет API для управления объектами в базе данных, а также для выполнения операций чтения и записи данных. JPA определяет набор аннотаций и интерфейсов, которые разработчик может использовать для описания сущностей базы данных и их отношений.

Hibernate — одна из реализаций JPA. Он предоставляет конкретную реализацию JPA API и дополнительные функции для работы с базами данных. Hibernate выполняет маппинг объектов Java на таблицы базы данных и обеспечивает автоматическую генерацию SQL-запросов для выполнения операций с данными.

3. Класс, который представляет таблицу в БД.
4. @Table(name = "users") — указывает, что сущность соответствует таблице users (необязательно, если имя совпадает).
5. С помощью @Id
6. @GeneratedValue(strategy = GenerationType.IDENTITY) — задает автоинкремент.
7. 
AUTO	Hibernate сам выбирает стратегию (по умолчанию).

IDENTITY	Использует автоинкремент (SERIAL в PostgreSQL, AUTO_INCREMENT в MySQL).

SEQUENCE	Использует SQL-секвенции (@SequenceGenerator).

TABLE	Хранит значения ключей в специальной таблице.

8. Использование атрибута name в аннотации @Column отличается от прямого указания имени поля тем, что с его помощью можно задать иное имя столбцу
9. 
@Entity

@Table(name = "demo_group", indexes = {

    @Index(name = "idx_group_number", columnList = "number"),

})

public class Group {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name="ID")

    private Long id;

    @Column(name="NUMBER", unique = true, nullable = false)

    private int number;

    @Column(name="DESCRIPTION", unique = false, nullable = false, length = 100)

    private String description;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)

10.

@Entity

@Table(name = "demo_student")

public class Student {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name="ID")

    private Long id;

    @Column(name="NAME", unique = false, nullable = false, length = 100)

    private String name;

    @ManyToOne

    @JoinColumn(name = "GROUP_ID")

    private Group group;

    @ManyToMany

    private Set<Course> courses = new HashSet<>();

}

    private List<Student> students = new ArrayList<>();

}
