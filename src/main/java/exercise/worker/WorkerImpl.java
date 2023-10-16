package exercise.worker;

import exercise.article.Article;
import exercise.article.Library;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkerImpl implements Worker {
    private final Library library;

    public WorkerImpl(Library library) {
        this.library = library;
    }

    @Override
    public void addNewArticles(List<Article> articles) {
        Map<Integer, List<Article>> newArticles = prepareArticles(articles).stream()
                .collect(Collectors.groupingBy(a -> a.getCreationDate().getYear()));
        newArticles.forEach(library::store);
        updateIfNecessary(newArticles);
    }

    @Override
    public String getCatalog() {
        StringBuilder sb = new StringBuilder("Список доступных статей:\n");
        library.getAllTitles()
                .stream()
                .sorted(String::compareTo)
                .forEachOrdered(title -> sb.append("    ").append(title).append("\n"));
        return sb.toString();
    }

    @Override
    public List<Article> prepareArticles(List<Article> articles) {
        List<Article> result = articles
                .stream()
                .filter(this::isArticleCorrect)
                .distinct()
                .collect(Collectors.toList());
        result.forEach(this::prepareDate);
        return result;
    }


    private boolean isArticleCorrect(Article article) {
        return !(nullOrBlank(article.getTitle()) ||
                nullOrBlank(article.getContent()) ||
                nullOrBlank(article.getAuthor()));
    }

    private boolean nullOrBlank(String s) {
        return s == null || s.isBlank();
    }

    private void prepareDate(Article article) {
        if (article.getCreationDate() == null) {
            article.setCreationDate(LocalDate.now());
        }
    }

    private void updateIfNecessary(Map<Integer, List<Article>> newArticles) {
        if (!newArticles.isEmpty()) {
            library.updateCatalog();
        }
    }
}