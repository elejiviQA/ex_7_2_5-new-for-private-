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
    private final List<Article> ARTICLES = new ArrayList<>();

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
                log.info("[TEST WAS FAILED]: " + message + stackTraceElements[2] + "\n");
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
        runAssertion((msg) -> assertEquals("Список доступных статей:\n", worker.getCatalog(), msg), "Заголовок пустого каталога должен совпадать с тестовым шаблоном\n");
    }

    @DisplayName("Каталог статей из непустой библиотеки") //неизвестные, неописанные причины
    @Test
    void testGetCatalogFromNotEmptyLibrary() {
        runAssertion((msg) -> assertEquals(getTestCatalog(), getCatalog(), msg), "Каталог должен удовлетворять тестовому шаблону\n");
    }

    @DisplayName("Заголовок каталога")
    @Test
    void testCatalogHeader() {
        runAssertion((msg) -> assertEquals("Список доступных статей:", getCatalog().lines().findFirst().orElse(""), msg), "Заголовок каталога должен совпадать с тестовым шаблоном\n");
    }

    @DisplayName("Количество названий статей")
    @Test
    void testNumOfTitlesNames() {
        runAssertion((msg) -> assertEquals(2, getCatalog().lines().skip(1).count(), msg), "Должно совпадать количество названий статей\n");
    }

    @DisplayName("Отступ строки")
    @Test
    void testLineIndent() {
        runAssertion((msg) -> assertEquals(4, getCatalog().lines().skip(1).findFirst().orElse("").chars().limit(4).filter(c -> c == ' ').count(), msg), "Должен совпадать отступ строки\n");
    }

    @DisplayName("Символы пробела")
    @Test
    void testNumOfSpaceCh() {
        runAssertion((msg) -> assertEquals(10, getCatalog().chars().filter(c -> c == ' ').count(), msg), "Должно совпадать количество символов пробела\n");
    }

    @DisplayName("Символы перевода строки")
    @Test
    void testNumOfLineFeedCh() {
        runAssertion((msg) -> assertEquals(3, getCatalog().chars().filter(c -> c == '\n').count(), msg), "Должно совпадать количество символов перевода строки\n");
    }

    @DisplayName("Алфавитный порядок названий статей")
    @Test
    void testAlphabetOrder() {
        runAssertion((msg) -> assertTrue(getCatalog().indexOf(getTestUnorderedTitles().get(1)) < getCatalog().indexOf(getTestUnorderedTitles().get(0)), msg), "Названия статей должны быть отсортированы в алфавитном порядке\n");
    }

    @DisplayName("Названия статей")
    @Test
    void testTitlesNames() {
        runAssertion((msg) -> assertTrue(getCatalog().lines().skip(1).map(String::strip).toList().containsAll(getTestUnorderedTitles()), msg), "Должны совпадать названия статей\n");
    }

    @DisplayName("Использование года статьи")
    @Test
    void testUseYearInStoreMethod() {
        ARTICLES.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        worker.addNewArticles(ARTICLES);
        runAssertion((msg) -> verify(library, description(msg)).store(2023, ARTICLES), "Для операции merge должно использоваться корректное число года\n");
    }

    @DisplayName("Обновление каталога статьей со всеми непустыми атрибутами")
    @Test
    void testAddNewArticle() {
        ARTICLES.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        worker.addNewArticles(ARTICLES);
        runAssertion((msg) -> verify(library, description(msg)).updateCatalog(), "Статья с непустыми значениями атрибутов должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьей без указанной даты")
    @Test
    void testAddNewArticleWithoutDate() {
        ARTICLES.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", null));
        worker.addNewArticles(ARTICLES);
        runAssertion((msg) -> verify(library, description(msg)).updateCatalog(), "Статья без указанной даты в итоге должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьей со всеми пустыми значениями атрибутов")
    @Test
    void testAddNullableArticle() {
        ARTICLES.add(new Article(null, null, null, null));
        worker.addNewArticles(ARTICLES);
        runAssertion((msg) -> verify(library, never()).updateCatalog(), "Статья со всеми пустыми значениями атрибутов не должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьей без названия")
    @Test
    void testAddNewArticleWithoutTitle() {
        ARTICLES.add(new Article(null, "Some code", "noBrain", LocalDate.of(2023, 10, 11)));
        worker.addNewArticles(ARTICLES);
        runAssertion((msg) -> verify(library, never()).updateCatalog(), "Статья без названия не должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьей без контента")
    @Test
    void testAddNewArticleWithoutContent() {
        ARTICLES.add(new Article("Hello, Java!", null, "noBrain", LocalDate.of(2023, 10, 11)));
        worker.addNewArticles(ARTICLES);
        runAssertion((msg) -> verify(library, never()).updateCatalog(), "Статья без контента не должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Обновление каталога статьей без автора")
    @Test
    void testAddNewArticleWithoutAuthor() {
        ARTICLES.add(new Article("Hello, Java!", "Some code", null, LocalDate.of(2023, 10, 11)));
        worker.addNewArticles(ARTICLES);
        runAssertion((msg) -> verify(library, never()).updateCatalog(), "Статья без автора не должна быть добавлена в библиотеку\n");
    }

    @DisplayName("Подготовка статьи со всеми непустыми атрибутами")
    @Test
    void testPrepareNewArticle() {
        ARTICLES.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        runAssertion((msg) -> assertEquals(1, worker.prepareArticles(ARTICLES).size(), msg), "Статья со всеми непустыми атрибутами должна быть сохранена \n");
    }

    @DisplayName("Подготовка статьи без названия")
    @Test
    void testPrepareNewArticleWithoutTitle() {
        ARTICLES.add(new Article(null, "Some code", "noBrain", LocalDate.of(2023, 10, 11)));
        runAssertion((msg) -> assertEquals(0, worker.prepareArticles(ARTICLES).size(), msg), "Статья без названия не должна быть сохранена\n");
    }

    @DisplayName("Подготовка статьи без контента")
    @Test
    void testPrepareNewArticleWithoutContent() {
        ARTICLES.add(new Article("Hello, Java!", null, "noBrain", LocalDate.of(2023, 10, 11)));
        runAssertion((msg) -> assertEquals(0, worker.prepareArticles(ARTICLES).size(), msg), "Статья без контента не должна быть сохранена\n");
    }

    @DisplayName("Подготовка статьи без автора")
    @Test
    void testPrepareNewArticleWithoutAuthor() {
        ARTICLES.add(new Article("Hello, Java!", "Some code", null, LocalDate.of(2023, 10, 11)));
        runAssertion((msg) -> assertEquals(0, worker.prepareArticles(ARTICLES).size(), msg), "Статья без автора не должна быть сохранена\n");
    }

    @DisplayName("Подготовка статьи без даты")
    @Test
    void testPrepareNewArticleWithoutDate() {
        ARTICLES.add(new Article("Hello, Java!", "Some code", "noBrain", null));
        runAssertion((msg) -> assertEquals(LocalDate.now(), worker.prepareArticles(ARTICLES).get(0).getCreationDate(), msg), "Статья без указанной даты должна получать текущую дату\n");
    }

    @DisplayName("Подготовка статей с одинаковым названием")
    @Test
    void testPrepareNewArticleWithSameNames() {
        ARTICLES.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        ARTICLES.add(new Article("Hello, Mockito!", "Where is verify?", "noBrain", LocalDate.of(2023, 10, 16)));
        runAssertion((msg) -> assertEquals(1, worker.prepareArticles(ARTICLES).size(), msg), "На сохранение должны передаваться только уникальные статьи\n");
    }

    @DisplayName("Подготовка статьи с пустыми значениями атрибутов")
    @Test
    void testPrepareNullableArticle() {
        ARTICLES.add(new Article(null, null, null, null));
        runAssertion((msg) -> assertEquals(0, worker.prepareArticles(ARTICLES).size(), msg), "Статья с пустыми значениями атрибутов не должна быть сохранена\n");
    }
}