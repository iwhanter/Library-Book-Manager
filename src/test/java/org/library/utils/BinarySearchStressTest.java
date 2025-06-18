package org.library.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.library.model.Book;
import org.library.service.LibraryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

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
        int bookCount = 2000;
        List<String> bookTitles = new ArrayList<>();
        
        // Добавление книг
        for (int i = 0; i < bookCount; i++) {
            String title = "Book" + String.format("%04d", i);
            service.addBook(new Book(title, "Author" + i, 2000 + random.nextInt(24)));
            bookTitles.add(title);
        }
        
        // Тест производительности бинарного поиска vs линейного поиска
        String searchTitle = bookTitles.get(bookCount / 2);
        
        // Бинарный поиск
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Book found = service.binarySearchBookByTitle(searchTitle);
            assertNotNull(found);
        }
        long binarySearchTime = System.currentTimeMillis() - startTime;
        
        // Линейный поиск (симуляция)
        startTime = System.currentTimeMillis();
        List<Book> allBooks = service.getAllBooks();
        for (int i = 0; i < 1000; i++) {
            Book found = null;
            for (Book book : allBooks) {
                if (book.getTitle().equalsIgnoreCase(searchTitle)) {
                    found = book;
                    break;
                }
            }
            assertNotNull(found);
        }
        long linearSearchTime = System.currentTimeMillis() - startTime;
        
        // Бинарный поиск должен быть значительно быстрее
        assertTrue(binarySearchTime < linearSearchTime);
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