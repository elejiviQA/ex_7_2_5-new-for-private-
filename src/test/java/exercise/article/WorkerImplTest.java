package exercise.article;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import exercise.worker.Worker;
import exercise.worker.WorkerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class WorkerImplTest {
    private final String testCatalog = """
                Список доступных статей:
                    Абрикос
                    Яблоко
                """;
    private final String testElementDelimiter = " {4}";
    private final String testCatalogHeader = Arrays.stream(testCatalog.replaceAll(testElementDelimiter, "").split("\n")).toList().get(0).strip();
    private final Stream<String> testTitles = Stream.of("Яблоко", "Абрикос");

    @Mock
    private Library library;
    private Worker worker;
    private List<String> titles;
    private List<Article> articles;

    @BeforeEach
    void prepare() {
        MockitoAnnotations.openMocks(this);
        worker = new WorkerImpl(library);
        when(library.getAllTitles()).thenReturn(List.of("Яблоко", "Абрикос"));
        titles = new ArrayList<>(Arrays.stream(worker.getCatalog().replaceAll(testElementDelimiter, "").split("\n")).toList());
        articles = new ArrayList<>();
    }

    //assertTrue(worker.getCatalog().contains("Яблоко"));
    @DisplayName("Формирование каталога статей")
    @Test
    public void testFormationOfTitles() {
        assertEquals(testCatalog, worker.getCatalog(),
                "Не соответствие шаблону: неверная реализация");
    }

    //
    @DisplayName("Количество статей")
    @Test
    public void testNumOfTitles() {
        if (testCatalogHeader.equals(titles.get(0).strip())) {
            titles.remove(0);
        }
        assertTrue(testTitles.allMatch(titles::contains), "Не совпадает количество названий статей");
    }

    //
    @DisplayName("Количество символов перевода строки")
    @Test
    public void testNumOfFeedLineCh() {
        assertEquals(testCatalog.chars().filter(c -> c == '\n').count(),
                worker.getCatalog().chars().filter(c -> c == '\n').count(),
                "Не совпадает количество символов перевода строки");
    }

    //
    @DisplayName("Заголовок каталога")
    @Test
    public void testCatalogHeader() {
        assertEquals(testCatalog.lines().findFirst().orElse("Пустой шаблон"),
                titles.get(0).strip(),
                "Заголовок не совпадает");
    }

    //
    @DisplayName("Названия статей")
    @Test
    public void testTitlesNames() {
        titles = titles.stream().map(String::strip).collect(Collectors.toList());
        if (testCatalogHeader.equals(titles.get(0).strip())) {
            titles.remove(0);
        }
        assertTrue(testTitles.allMatch(titles::contains),
                "Не совпадают названия статей");
    }

    //
    @DisplayName("Количество символов пробела")
    @Test
    public void testNumOfSpaceChars() {
        assertEquals(testCatalog.chars().filter(c -> c == ' ').count(),
                worker.getCatalog().chars().filter(c -> c == ' ').count(),
                "Не совпадает количество символов пробела");
    }

    //
    @DisplayName("Названия статей в алфавитном порядке")
    @Test
    public void testIsAlphabeticalOrderedTitles() {
        if (testCatalog.lines().findFirst().orElse("Пустой шаблон").equals(titles.get(0).strip())) {
            titles.remove(0);
        }
        assertEquals(testTitles.sorted().toList(), titles,
                "Названия статей не отсортированы в алфавитном порядке");
    }

    @DisplayName("Сохранение статей")
    @Test
    public void testSaveArticles() {
        articles.add(new Article(
                "Hello, Mockito!",
                "Where is verify?",
                "noBrain",
                LocalDate.of(2023, 10, 16)));
        worker.addNewArticles(articles);
        verify(library,
                description("Неверная реализация работы с числом года: merge с использованием некорректного года"))
                .store(2023, articles);
    }

    @DisplayName("Статья без названия")
    @Test
    public void testArticleWithoutTitle() {
        articles.add(new Article(
                null,
                "Some code",
                "noBrain",
                LocalDate.of(2023, 10, 11)));
        assertEquals(0, worker.prepareArticles(articles).size(),
                "Неверная реализация обработки: статья без названия не должна быть сохранена");
    }

    @DisplayName("Статья без контента")
    @Test
    public void testArticleWithoutContent() {
        articles.add(new Article(
                "Hello, Java!",
                null,
                "noBrain",
                LocalDate.of(2023, 10, 11)));
        assertEquals(0, worker.prepareArticles(articles).size(),
                "Неверная реализация обработки: статья без контента не должна быть сохранена");
    }

    @DisplayName("Статья без автора")
    @Test
    public void testArticleWithoutAuthor() {
        articles.add(new Article(
                "Hello, Java!",
                "Some code",
                null,
                LocalDate.of(2023, 10, 11)));
        assertEquals(0, worker.prepareArticles(articles).size(),
                "Неверная реализация обработки: статья без автора не должна быть сохранена");
    }

    @DisplayName("Статья без даты")
    @Test
    public void testArticleWithoutDate() {
        articles.add(new Article(
                "Hello, Java!",
                "Some code",
                "noBrain",
                null));
        assertEquals(worker
                        .prepareArticles(articles)
                        .get(0)
                        .getCreationDate(),
                LocalDate.now(),
                "Неверная реализация обработки: статья без указанной даты должна получить текущую дату");
    }

    // добавил distinct().collect(Collectors.toList()); в реализации prepareArticles у worker
    @DisplayName("Статьи с одинаковым названием")
    @Test
    public void testArticleWithSameNames() {
        articles.add(new Article(
                "Hello, Mockito!",
                "Where is verify?",
                "noBrain",
                LocalDate.of(2023, 10, 16)));
        articles.add(new Article(
                "Hello, Mockito!",
                "Where is verify?",
                "noBrain",
                LocalDate.of(2023, 10, 16)));
        assertEquals(1, worker.prepareArticles(articles).size());
    }
}