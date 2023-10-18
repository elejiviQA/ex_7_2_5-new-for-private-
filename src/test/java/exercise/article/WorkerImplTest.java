package exercise.article;

import exercise.MyFunInterface;
import exercise.worker.Worker;
import exercise.worker.WorkerImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Log
@DisplayName("Проверка реализации работника")
class WorkerImplTest {

    @Mock
    private Library library;
    private Worker worker;
    private final List<Article> articles = new ArrayList<>();

    public String getTestCatalog() {
        return """
                Список доступных статей:
                    Абрикос
                    Яблоко
                """;
    }

    public List<String> getTestUnorderedTitles() {
        return List.of("Яблоко", "Абрикос");
    }

    public String getCatalog() {
        when(library.getAllTitles()).thenReturn(getTestUnorderedTitles());
        return worker.getCatalog();
    }

    public void runAssertion(MyFunInterface myFunFunction, String message) {
        AssertionError assertionError = null;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        try {
            myFunFunction.goFun(message);
        } catch (AssertionError aErr) {
            assertionError = aErr;
        } finally {
            if (assertionError == null) {
                log.info("[TEST WAS SUCCESSFUL]: " + message + stackTraceElements[2] + "\n");
            } else {
                log.info( "[TEST WAS FAILED]: " + message + stackTraceElements[2] + "\n");
                throw assertionError;
            }
        }
    }

    @BeforeEach
    void prepare() {
        MockitoAnnotations.openMocks(this);
        worker = new WorkerImpl(library);
    }

    @Test
    @DisplayName("Каталог статей из пустой библиотеки")
    void testGetCatalogFromEmptyLibrary() {
        runAssertion((msg) -> assertEquals("Список доступных статей:\n", worker.getCatalog(), msg), "Заголовок каталога должен совпадать с тестовым шаблоном\n");
    }

    @DisplayName("Каталог статей из непустой библиотеки")
    @Test
    void testGetCatalogFromNotEmptyLibrary() {
        assertAll("Требования к каталогу",
                () -> runAssertion((msg) -> assertEquals("Список доступных статей:", getCatalog().lines().findFirst().orElse(""), msg), "Заголовок каталога должен совпадать с тестовым шаблоном\n"),
                () -> runAssertion((msg) -> assertEquals(2, getCatalog().lines().skip(1).count(), msg), "Должно совпадать количество названий статей\n"),
                () -> runAssertion((msg) -> assertEquals(4,getCatalog().lines().skip(1).findFirst().orElse("").chars().limit(4).filter(c -> c == ' ').count(), msg), "Должен совпадать отступ строки\n"),
                () -> runAssertion((msg) -> assertEquals(10, getCatalog().chars().filter(c -> c == ' ').count(), msg), "Должно совпадать количество символов пробела\n"),
                () -> runAssertion((msg) -> assertEquals(3, getCatalog().chars().filter(c -> c == '\n').count(), msg), "Должно совпадать количество символов перевода строки\n"),
                () -> runAssertion((msg) -> assertTrue(getCatalog().lines().skip(1).map(String::strip).toList().containsAll(getTestUnorderedTitles()), msg), "Должны совпадать названия статей\n"),
                () -> runAssertion((msg) -> assertTrue(getCatalog().indexOf(getTestUnorderedTitles().get(1)) < getCatalog().indexOf(getTestUnorderedTitles().get(0)), msg), "Названия статей должны быть отсортированы в алфавитном порядке\n"));
        //неизвестные, неописанные причины
        runAssertion((msg) -> assertEquals(getTestCatalog(), getCatalog(), msg), "Каталог должен удовлетворять тестовому шаблону\n");
    }

    @DisplayName("Использование года статьи")
    @Test
    void testUseYearInStoreMethod() {
        articles.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        worker.addNewArticles(articles);
        runAssertion((msg) -> verify(library, description(msg)).store(2023, articles), "Для операции merge должно использоваться корректное число года\n");
    }

    //UPDATE CATALOG
    @DisplayName("Обновление каталога статьей со всеми атрибутами")
    @Test
    void testAddNewArticle() {
        articles.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        worker.addNewArticles(articles);
        runAssertion((msg) -> verify(library, description(msg)).updateCatalog(), "Статье со всеми атрибутами должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьей без указанной даты")
    @Test
    void testAddNewArticleWithoutDate() {
        articles.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", null));
        worker.addNewArticles(articles);
        runAssertion((msg) -> verify(library, description(msg)).updateCatalog(), "Статья без указанной даты должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьей со всеми пустыми значениями атрибутов")
    @Test
    void testAddNullableArticle() {
        articles.add(new Article(null, null, null, null));
        worker.addNewArticles(articles);
        runAssertion((msg) -> verify(library, never()).updateCatalog(), "Статья со всеми пустыми значениями атрибутов не должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьями с одинаковым названием")
    @Test
    void testAddNewArticleWithSameNames() {
        articles.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        articles.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        worker.addNewArticles(articles);
        runAssertion((msg) -> verify(library, never()).updateCatalog(), "В библиотеку должны добавляться только уникальные статьи\n");
    }

    @DisplayName("Обновление каталога статьей без контента")
    @Test
    void testAddNewArticleWithoutContent() {
        articles.add(new Article("Hello, Java!", null, "noBrain", LocalDate.of(2023, 10, 11)));
        worker.addNewArticles(articles);
        runAssertion((msg) -> verify(library, never()).updateCatalog(), "Статья без контента не должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьей без автора")
    @Test
    void testAddNewArticleWithoutAuthor() {
        articles.add(new Article("Hello, Java!", "Some code", null, LocalDate.of(2023, 10, 11)));
        worker.addNewArticles(articles);
        runAssertion((msg) -> verify(library, never()).updateCatalog(), "Статья без автора не должна быть добавлена в библиотеку\n");
    }


    //PREPARE ARTICLES
    @DisplayName("Подготовка статьи со всеми атрибутами")
    @Test
    void testPrepareNewArticle() {
        articles.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        runAssertion((msg) -> assertEquals(1, worker.prepareArticles(articles).size(), msg), "Статья со всеми атрибутами должна быть сохранена \n");
    }

    @DisplayName("Подготовка статьи без названия")
    @Test
    void testPrepareNewArticleWithoutTitle() {
        articles.add(new Article(null, "Some code", "noBrain", LocalDate.of(2023, 10, 11)));
        runAssertion((msg) -> assertEquals(0, worker.prepareArticles(articles).size(), msg), "Статья без названия не должна быть сохранена\n");
    }

    @DisplayName("Подготовка статьи без контента")
    @Test
    void testPrepareNewArticleWithoutContent() {
        articles.add(new Article("Hello, Java!", null, "noBrain", LocalDate.of(2023, 10, 11)));
        runAssertion((msg) -> assertEquals(0, worker.prepareArticles(articles).size(), msg), "Статья без контента не должна быть сохранена\n");
    }

    @DisplayName("Подготовка статьи без автора")
    @Test
    void testPrepareNewArticleWithoutAuthor() {
        articles.add(new Article("Hello, Java!", "Some code", null, LocalDate.of(2023, 10, 11)));
        runAssertion((msg) -> assertEquals(0, worker.prepareArticles(articles).size(), msg), "Статья без автора не должна быть сохранена\n");
    }

    @DisplayName("Подготовка статьи без даты")
    @Test
    void testPrepareNewArticleWithoutDate() {
        articles.add(new Article("Hello, Java!", "Some code", "noBrain", null));
        runAssertion((msg) -> assertEquals(LocalDate.now(), worker.prepareArticles(articles).get(0).getCreationDate(), msg), "Статья без указанной даты должна получать текущую дату\n");
    }

    @DisplayName("Подготовка статей с одинаковым названием")
    @Test
    void testPrepareNewArticleWithSameNames() {
        articles.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        articles.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        runAssertion((msg) -> assertEquals(1, worker.prepareArticles(articles).size(), msg), "На сохранение должны передаваться только уникальные статьи\n");
    }

    @DisplayName("Подготовка статьи с пустыми значениями атрибутов")
    @Test
    void testPrepareNullableArticle() {
        articles.add(new Article(null, null, null, null));
        runAssertion((msg) -> assertEquals(0, worker.prepareArticles(articles).size(), msg), "Статья с пустыми значениями атрибутов не должна быть сохранена\n");
    }
}