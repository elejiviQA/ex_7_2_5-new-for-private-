package exercise.article;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryImpl implements Library {
    private final Map<Integer, List<Article>> storage = new HashMap<>();
    private final Map<Integer, List<Article>> tempStorage = new HashMap<>();

    {
        List<Article> article2023 = new ArrayList<>();
        List<Article> article2022 = new ArrayList<>();

        article2023.add(new Article(
                "Как составить резюме тестировщику",
                "Работодатель тратит всего 20 секунд на первичный просмотр резюме. Поэтому очень важно составить резюме Junior-тестировщика таким образом, чтобы hr-специалист сразу видел ваши ключевые навыки. О том, как это сделать, читайте в блоге Kata Academy.",
                "Сергей Сергеев",
                LocalDate.of(2023, 1, 18)));
        article2023.add(new Article(
                "Почему важны soft skills?",
                "Мягкие навыки помогают решать задачи и взаимодействовать с другими людьми. Можно обладать хорошими знаниями и умениями, но без развитых soft skills очень трудно работать в современных компаниях. Особенно айтишникам. Рассказываем, почему.",
                "Иван Иванов",
                LocalDate.of(2023, 1, 6)));

        article2022.add(new Article(
                "Как правильно изучать языки программирования",
                "Как эффективно изучать программирование. Советы, которые помогут лучше и эффективнее учить любой язык программирования.",
                "Сергей Сергеев",
                LocalDate.of(2022, 10, 22)));
        article2023.add(new Article(
                "Сколько времени нужно, чтобы выучить Java",
                "Java является одним из наиболее используемых языков программирования, с него часто начинают путь в IT. В мире насчитывается более 8 миллионов Java-разработчиков. И их количество постоянно растет. Сколько времени нужно, чтобы выучить этот язык программирования? Спойлер: не так много, как может показаться.",
                "Петр Петров",
                LocalDate.of(2022, 8, 26)));
        article2023.add(new Article(
                "7 мифов об IT",
                "Есть тонны заблуждений о том, что такое работа в IT-сфере. Некоторые из них никогда не были правдой, другие – пережиток прошлого. В этой статье мы попытались развеять самые популярные мифы о работе в сфере технологий.",
                "Константин Константинов",
                LocalDate.of(2022, 1, 16)));

        storage.put(2022, article2022);
        storage.put(2023, article2023);
    }

    @Override
    public void store(int year, List<Article> articles) {
        System.out.println("Сохраняем новые статьи");
        tempStorage.merge(year, articles, (oldList, newList) -> {
            oldList.addAll(newList);
            return oldList;
        });
    }

    @Override
    public void updateCatalog() {
        System.out.println("Обновляем каталог");
        tempStorage.forEach((key, value) -> storage.merge(key, value, (oldList, newList) -> {
            oldList.addAll(newList);
            return oldList;
        }));
        tempStorage.clear();
    }

    @Override
    public List<String> getAllTitles() {
        System.out.println("Формируем список названий статей");
        return storage.values().stream().flatMap(List::stream).map(Article::getTitle).toList();
    }
}