package org.library.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class BinarySearchStressTest {

    private LibraryService service;
    private Random random;

    @BeforeEach
    void setUp() {
        service = new LibraryService();
        random = new Random(42);
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testBinarySearchLargeDataset() {
        int bookCount = 10000;
        List<String> bookTitles = new ArrayList<>();
        
        // Добавление книг с уникальными названиями
        for (int i = 0; i < bookCount; i++) {
            String title = "Book" + String.format("%05d", i);
            service.addBook(new Book(title, "Author" + i, 2000 + random.nextInt(24)));
            bookTitles.add(title);
        }
        
        // Тест поиска существующих книг
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String searchTitle = bookTitles.get(random.nextInt(bookCount));
            Book found = service.binarySearchBookByTitle(searchTitle);
            assertNotNull(found);
            assertEquals(searchTitle, found.getTitle());
        }
        long searchTime = System.currentTimeMillis() - startTime;
        
        // Бинарный поиск должен быть очень быстрым
        assertTrue(searchTime < 1000); // Менее 1 секунды для 100 поисков
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testBinarySearchNonExistentBooks() {
        int bookCount = 5000;
        
        // Добавление книг
        for (int i = 0; i < bookCount; i++) {
            String title = "Book" + String.format("%04d", i);
            service.addBook(new Book(title, "Author" + i, 2000 + random.nextInt(24)));
        }
        
        // Тест поиска несуществующих книг
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String nonExistentTitle = "NonExistentBook" + random.nextInt(10000);
            Book found = service.binarySearchBookByTitle(nonExistentTitle);
            assertNull(found);
        }
        long searchTime = System.currentTimeMillis() - startTime;
        
        // Поиск несуществующих книг тоже должен быть быстрым
        assertTrue(searchTime < 1000);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testBinarySearchEdgeCases() {
        // Добавление книг с граничными случаями
        service.addBook(new Book("", "Empty Author", 2000));
        service.addBook(new Book("A", "Single Author", 2000));
        service.addBook(new Book("Z", "Last Author", 2000));
        service.addBook(new Book("Book with spaces", "Space Author", 2000));
        service.addBook(new Book("Book123", "Number Author", 2000));
        
        // Тест поиска граничных случаев
        assertNotNull(service.binarySearchBookByTitle(""));
        assertNotNull(service.binarySearchBookByTitle("A"));
        assertNotNull(service.binarySearchBookByTitle("Z"));
        assertNotNull(service.binarySearchBookByTitle("Book with spaces"));
        assertNotNull(service.binarySearchBookByTitle("Book123"));
        
        // Тест поиска несуществующих граничных случаев
        assertNull(service.binarySearchBookByTitle("0"));
        assertNull(service.binarySearchBookByTitle("ZZ"));
    }

    @Test
    @Timeout(value = 12, unit = TimeUnit.SECONDS)
    void testBinarySearchCaseInsensitive() {
        // Добавление книг с разным регистром
        service.addBook(new Book("Java Programming", "Author1", 2000));
        service.addBook(new Book("PYTHON BASICS", "Author2", 2000));
        service.addBook(new Book("C++ Advanced", "Author3", 2000));
        
        // Тест поиска с разным регистром
        assertNotNull(service.binarySearchBookByTitle("java programming"));
        assertNotNull(service.binarySearchBookByTitle("JAVA PROGRAMMING"));
        assertNotNull(service.binarySearchBookByTitle("Java Programming"));
        
        assertNotNull(service.binarySearchBookByTitle("python basics"));
        assertNotNull(service.binarySearchBookByTitle("PYTHON BASICS"));
        
        assertNotNull(service.binarySearchBookByTitle("c++ advanced"));
        assertNotNull(service.binarySearchBookByTitle("C++ Advanced"));
    }

    @Test
@Timeout(value = 8, unit = TimeUnit.SECONDS)
void testBinarySearchPerformanceComparison() {
    int bookCount = 10000;
    List<String> bookTitles = new ArrayList<>();

    // Добавление книг
    for (int i = 0; i < bookCount; i++) {
        String title = "Book" + String.format("%05d", i);
        service.addBook(new Book(title, "Author" + i, 2000 + random.nextInt(24)));
        bookTitles.add(title);
    }

    // Получаем список книг и сортируем один раз
    List<Book> sortedBooks = service.getAllBooks();
    MySorts.mergeSort(sortedBooks, Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));

    // Обновляем внутренние данные сервиса (если нужно)
    // Например, если сервис хранит книги в MyHashTable, то для теста можно работать с sortedBooks напрямую

    String searchTitle = bookTitles.get(bookCount / 2);

    // Бинарный поиск по отсортированному списку
    long startTime = System.nanoTime();
    for (int i = 0; i < 10000; i++) {
        Book found = binarySearch(sortedBooks, searchTitle);
        assertNotNull(found);
    }
    long binarySearchTime = System.nanoTime() - startTime;

    // Линейный поиск
    List<Book> allBooks = service.getAllBooks();
    startTime = System.nanoTime();
    for (int i = 0; i < 10000; i++) {
        Book found = null;
        for (Book book : allBooks) {
            if (book.getTitle().equalsIgnoreCase(searchTitle)) {
                found = book;
                break;
            }
        }
        assertNotNull(found);
    }
    long linearSearchTime = System.nanoTime() - startTime;

    System.out.println("Binary search time: " + (binarySearchTime / 1_000_000) + " ms");
    System.out.println("Linear search time: " + (linearSearchTime / 1_000_000) + " ms");

    assertTrue(binarySearchTime < linearSearchTime, "Binary search should be faster than linear search");
    }

    // Вспомогательный метод бинарного поиска по отсортированному списку
    private Book binarySearch(List<Book> books, String title) {
        int low = 0, high = books.size() - 1;
        title = title.toLowerCase();

        while (low <= high) {
            int mid = (low + high) >>> 1;
            String midTitle = books.get(mid).getTitle().toLowerCase();
            int cmp = midTitle.compareTo(title);
            if (cmp == 0) return books.get(mid);
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }


    

    // Пример класса сервиса с реализацией бинарного поиска
    static class LibraryService {
        private final List<Book> books = new ArrayList<>();

        public void addBook(Book book) {
            books.add(book);
        }

        public List<Book> getAllBooks() {
            return new ArrayList<>(books);
        }

        public Book binarySearchBookByTitle(String title) {
            // Создаём копию списка и сортируем по названию без учёта регистра
            List<Book> sortedBooks = new ArrayList<>(books);
            sortedBooks.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));

            int left = 0;
            int right = sortedBooks.size() - 1;
            String searchTitle = title.toLowerCase();

            while (left <= right) {
                int mid = (left + right) / 2;
                String midTitle = sortedBooks.get(mid).getTitle().toLowerCase();
                int cmp = midTitle.compareTo(searchTitle);

                if (cmp == 0) {
                    return sortedBooks.get(mid);
                } else if (cmp < 0) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            return null; // не найдено
        }
    }

    // Пример класса Book
    static class Book {
        private final String title;
        private final String author;
        private final int year;

        public Book(String title, String author, int year) {
            this.title = title;
            this.author = author;
            this.year = year;
        }

        public String getTitle() {
            return title;
        }

    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testBinarySearchWithDuplicates() {
        // Добавление книг с дублирующимися названиями
        for (int i = 0; i < 100; i++) {
            service.addBook(new Book("Duplicate Book", "Author" + i, 2000 + random.nextInt(24)));
        }
        
        // Бинарный поиск должен найти одну из книг с таким названием
        Book found = service.binarySearchBookByTitle("Duplicate Book");
        assertNotNull(found);
        assertEquals("Duplicate Book", found.getTitle());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testBinarySearchEmptyLibrary() {
        // Тест бинарного поиска в пустой библиотеке
        Book found = service.binarySearchBookByTitle("Any Book");
        assertNull(found);
    }

    @Test
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    void testBinarySearchSingleBook() {
        // Тест бинарного поиска с одной книгой
        service.addBook(new Book("Single Book", "Single Author", 2000));
        
        Book found = service.binarySearchBookByTitle("Single Book");
        assertNotNull(found);
        assertEquals("Single Book", found.getTitle());
        
        found = service.binarySearchBookByTitle("Non-existent Book");
        assertNull(found);
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testBinarySearchStressWithMixedOperations() {
        int operations = 1000;
        
        for (int i = 0; i < operations; i++) {
            if (i % 3 == 0) {
                // Добавление книги
                String title = "Book" + String.format("%04d", i);
                service.addBook(new Book(title, "Author" + i, 2000 + random.nextInt(24)));
            } else if (i % 3 == 1) {
                // Поиск существующей книги
                if (i > 0) {
                    String searchTitle = "Book" + String.format("%04d", (i - 1) / 3 * 3);
                    Book found = service.binarySearchBookByTitle(searchTitle);
                    if (found != null) {
                        assertEquals(searchTitle, found.getTitle());
                    }
                }
            } else {
                // Поиск несуществующей книги
                String nonExistentTitle = "NonExistent" + random.nextInt(10000);
                Book found = service.binarySearchBookByTitle(nonExistentTitle);
                assertNull(found);
            }
        }
        
        // Финальная проверка
        List<Book> allBooks = service.getAllBooks();
        assertTrue(allBooks.size() > 0);
    }
} 