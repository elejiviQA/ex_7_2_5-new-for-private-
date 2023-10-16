package exercise;


import exercise.article.Article;
import exercise.article.LibraryImpl;
import exercise.worker.Worker;
import exercise.worker.WorkerImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        Worker worker = new WorkerImpl(new LibraryImpl());
        System.out.println(worker.getCatalog());

        System.out.println();
        System.out.println();
        System.out.println("Добавление новых статей");
        List<Article> newArticles = new ArrayList<>();

        newArticles.add(new Article(
                null,
                "Мягкие навыки помогают решать задачи и взаимодействовать с другими людьми. Можно обладать хорошими знаниями и умениями, но без развитых soft skills очень трудно работать в современных компаниях. Особенно айтишникам. Рассказываем, почему.",
                "Иван Иванов",
                LocalDate.of(2023, 1, 6)));
        newArticles.add(new Article(
                "Что такое функциональное чтение? Или как эффективно учиться программированию",
                "Функциональное чтение позволяет эффективнее работать с новой информацией. Как этот метод поможет читать книги для программистов и не только? Разбираем в сегодняшней статье.",
                "Иван Иванов",
                null));

        worker.addNewArticles(newArticles);
        System.out.println("...");
        System.out.println("Проверяем каталог");

        System.out.println(worker.getCatalog());
    }
}