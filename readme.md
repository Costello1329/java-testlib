# Java-testlib

Кастомная библиотека для тестирования решений задач на `java`, использующая принцип инстанцирования схожих объектов, примения к одинаковых операций и их сравнения.

## Обзор пакетов и классов
+ `package core`
  + `Test`: аннотация, которой нужно пометить пользовательский метод тестирования.
  + `TestContext`: класс, который передается пользовательскому методу тестирования, содержащий текущий контест тестера. Например, именно при помощи этого класса, можно заллогировать какую-то операцию, либо прервать тестирование по какой-то причине.
  + `Tester`: Основной класс, являющийся точкой входа `testlib`. Получает пользовательский класс, содержащий тесты, помеченные аннотациями `Test`, список `jar`-архивов с решениями (первый в списке трактуется как эталонное решение, остальные – как проверяемые) и список классов для загрузки (для каждого `jar`-архива, список свой, но размеры списков и индексы схожих классов в них – должны совпадать).
+ `package components`
  + `Entity`: сущность, с которой работает `testlib`. Имеет два конструктора: двуаргументный конструктор можно использовать для примитивов, трехаргументный – для объектов.
  + `Generator`: создает набор **схожих** классов на основании их списка и списка их аргументов.
     Например, у нас есть класс `RussianDeveloper` и `IndianDeveloper`. Оба – получают в конструкторе два аргумента: `String name` и `? salary`. Однако, `RussianEmployee` получает зарплату в виде числа `int salary`, а `IndianEmployee` – в виде `boolean hasSalary`. `Generator` как раз позволяет создать инстанс этих двух классов, на основании списка `Supplier`'ов их аргументов. В нашем случае, нам надо создать два `Supplier`'а аргументов: один – для имени, второй – для зарплаты. Сами `Supplier`'ы аргументов могут работать как декларативно (просто возвращая какое-то фиксированное значение), так и рандомно (генерировать значение случайно на-лету).
     
     Напишем классы разработчиков:
     ```java17
     public record RussianDeveloper (String name, int salary) {}
     public record IndianDeveloper (String name, boolean hasSalary) {}
     ```
     
     Напишем `NameSupplier`:
     ```
     record NameSupplier () implements Supplier<List<Entity>> {
         @Override
         public List<Entity> get () {
             return List.of(
                 new Entity("Roman", String.class, "new String(\"Roman\")"),
                 new Entity("Romanujan", String.class, "new String(\"Romanujan\")")
             );
         }
     }
     ```
     
     Напишем `SalarySupplier`:
     ```java17
     record SalarySupplier () implements Supplier<List<Entity>> {
         private static final SplittableRandom RANDOM = new SplittableRandom();

         @Override
         public List<Entity> get () {
             final int value = RANDOM.nextInt(1000, 5000);
             return List.of(
                 new Entity(value, int.class),
                 new Entity(value >= 3000, boolean.class)
             );
         }
     }
     ```
     
     Напишем `Main`:
     ```java17
     public static void main (final String[] args) {
         final Generator generator = new Generator(
             new TestContext(),
             List.of(RussianDeveloper.class, IndianDeveloper.class),
             List.of(new NameSupplier(), new SalarySupplier())
         );

         final List<Entity> employees = generator.get();
         System.out.println(employees);
     }
     ```
  + `Entity`: сущность, с которой работает `testlib`. Имеет два конструктора: двуаргументный конструктор можно использовать для примитивов, трехаргументный – для объектов.
  + `MethodCall`: описание вызова метода. Принимает название метода и список сущностей-аргументов.
  + `OperationsApplier`: класс, применяющий операции к переданным сущностям. Например, у нас есть два класса: `Square` и `Triangle`, и мы хотим применить к ним список одинаковых операций: `rotate(-.5)`, потом `translate(13., 29.)`, ну и `scale(.5)` Для этого, создадим `OperationsApplier` и будем применять операции до тех пор, пока они есть:
    ```java17
    /// Список фигур, полученный, например, от генератора:
    final List<Entity> shapes = ...;
    
    final OperationsApplier operationsApplier = new OperationsApplier(
        testContext,
        List.of(
            new MethodCall("rotate", List.of(new Entity(-.5, double.class))),
            new MethodCall("translate", List.of(new Entity(13., double.class), new Entity(29., double.class))),
            new MethodCall("scale", List.of(new Entity(.5, double.class))),
        )
    );
    
    while (operationsApplier.hasNext())
        operationsApplier.apply("shape", shapes);
    ```
  + `EntityComparator`: класс, сравнивающий схожие сущности посредством вызова на них цепочек методов и сравнения их результатов с помощью переданных предикатов. Например, пусть у нас есть два инстанса класса `Square`, и мы хотим создать компаратор, сравнивающий их на основе их характеристик с точностью `1e-6`. Посмотреть код по этому примеру можно [тут](https://github.com/Costello1329/simple-geometry-tests-demo/blob/master/src/Tests.java).

## Copyright

![Creative Commons Licence](https://i.creativecommons.org/l/by-sa/4.0/88x31.png)

Все материалы доступны по лицензии [Creative Commons «Attribution-ShareAlike» 4.0](http://creativecommons.org/licenses/by-sa/4.0/). \
При заимствовании любых материалов из данного репозитория, необходимо оставить ссылку на него, а также, указать мое имя: **Константин Леладзе**.

__© Konstantin Leladze__
