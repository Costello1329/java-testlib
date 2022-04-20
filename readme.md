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

## Copyright

![Creative Commons Licence](https://i.creativecommons.org/l/by-sa/4.0/88x31.png)

Все материалы доступны по лицензии [Creative Commons «Attribution-ShareAlike» 4.0](http://creativecommons.org/licenses/by-sa/4.0/). \
При заимствовании любых материалов из данного репозитория, необходимо оставить ссылку на него, а также, указать мое имя: **Константин Леладзе**.

__© Konstantin Leladze__
