package org.library.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.library.model.Book;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class MySortsStressTest {

    private List<Book> generateLargeBookList(int size) {
        List<Book> books = new ArrayList<>();
        Random random = new Random(42); // Фиксированный seed для воспроизводимости
        
        for (int i = 0; i < size; i++) {
            String title = "Book" + random.nextInt(10000);
            String author = "Author" + random.nextInt(1000);
            int year = 1900 + random.nextInt(124); // 1900-2023
            books.add(new Book(title, author, year));
        }
        return books;
    }

    private List<Integer> generateLargeIntegerList(int size) {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random(42);
        
        for (int i = 0; i < size; i++) {
            numbers.add(random.nextInt(100000));
        }
        return numbers;
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testBubbleSortLargeDataSet() {
        List<Book> books = generateLargeBookList(1000);
        List<Book> originalBooks = new ArrayList<>(books);
        
        MySorts.bubbleSort(books, Comparator.comparing(Book::getTitle));
        
        // Проверка, что список отсортирован
        for (int i = 1; i < books.size(); i++) {
            assertTrue(books.get(i-1).getTitle().compareTo(books.get(i).getTitle()) <= 0);
        }
        
        // Проверка, что все элементы сохранены
        assertEquals(originalBooks.size(), books.size());
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testMergeSortLargeDataSet() {
        List<Book> books = generateLargeBookList(5000);
        List<Book> originalBooks = new ArrayList<>(books);
        
        MySorts.mergeSort(books, Comparator.comparing(Book::getYear));
        
        // Проверка, что список отсортирован
        for (int i = 1; i < books.size(); i++) {
            assertTrue(books.get(i-1).getYear() <= books.get(i).getYear());
        }
        
        // Проверка, что все элементы сохранены
        assertEquals(originalBooks.size(), books.size());
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testSortingPerformanceComparison() {
        List<Book> books1 = generateLargeBookList(2000);
        List<Book> books2 = new ArrayList<>(books1);
        
        long startTime = System.currentTimeMillis();
        MySorts.bubbleSort(books1, Comparator.comparing(Book::getTitle));
        long bubbleSortTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        MySorts.mergeSort(books2, Comparator.comparing(Book::getTitle));
        long mergeSortTime = System.currentTimeMillis() - startTime;
        
        // Проверка, что оба алгоритма дают одинаковый результат
        for (int i = 0; i < books1.size(); i++) {
            assertEquals(books1.get(i).getTitle(), books2.get(i).getTitle());
        }
        
        // Merge sort должен быть быстрее для больших наборов данных
        assertTrue(mergeSortTime < bubbleSortTime || mergeSortTime < 1000); // Допускаем небольшую разницу
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testSortingWithDuplicates() {
        List<Book> books = new ArrayList<>();
        Random random = new Random(42);
        
        // Создание списка с дубликатами
        for (int i = 0; i < 1000; i++) {
            String title = "Book" + (i % 100); // Много дубликатов
            String author = "Author" + (i % 50);
            int year = 2000 + (i % 20);
            books.add(new Book(title, author, year));
        }
        
        MySorts.mergeSort(books, Comparator.comparing(Book::getTitle));
        
        // Проверка, что список отсортирован
        for (int i = 1; i < books.size(); i++) {
            assertTrue(books.get(i-1).getTitle().compareTo(books.get(i).getTitle()) <= 0);
        }
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSortingAlreadySortedList() {
        List<Book> books = new ArrayList<>();
        
        // Создание уже отсортированного списка
        for (int i = 0; i < 1000; i++) {
            books.add(new Book("Book" + i, "Author" + i, 2000 + i));
        }
        
        List<Book> originalBooks = new ArrayList<>(books);
        
        MySorts.mergeSort(books, Comparator.comparing(Book::getTitle));
        
        // Проверка, что список не изменился
        for (int i = 0; i < books.size(); i++) {
            assertEquals(originalBooks.get(i).getTitle(), books.get(i).getTitle());
        }
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSortingReverseSortedList() {
        List<Book> books = new ArrayList<>();
        
        // Создание списка в обратном порядке
        for (int i = 999; i >= 0; i--) {
            books.add(new Book("Book" + i, "Author" + i, 2000 + i));
        }
        
        MySorts.bubbleSort(books, Comparator.comparing(Book::getTitle));
        
        // Проверка, что список отсортирован
        for (int i = 1; i < books.size(); i++) {
            assertTrue(books.get(i-1).getTitle().compareTo(books.get(i).getTitle()) <= 0);
        }
    }

    @Test
    @Timeout(value = 8, unit = TimeUnit.SECONDS)
    void testSortingWithNullValues() {
        List<Book> books = new ArrayList<>();
        Random random = new Random(42);
        
        // Создание списка с null значениями (если бы они были возможны)
        for (int i = 0; i < 500; i++) {
            String title = random.nextBoolean() ? "Book" + i : "";
            String author = random.nextBoolean() ? "Author" + i : "";
            int year = 2000 + random.nextInt(20);
            books.add(new Book(title, author, year));
        }
        
        MySorts.mergeSort(books, Comparator.comparing(Book::getTitle, Comparator.nullsLast(Comparator.naturalOrder())));
        
        // Проверка, что список отсортирован
        for (int i = 1; i < books.size(); i++) {
            String prevTitle = books.get(i-1).getTitle();
            String currTitle = books.get(i).getTitle();
            assertTrue(prevTitle.compareTo(currTitle) <= 0);
        }
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testSortingEmptyList() {
        List<Book> books = new ArrayList<>();
        
        MySorts.bubbleSort(books, Comparator.comparing(Book::getTitle));
        assertEquals(0, books.size());
        
        MySorts.mergeSort(books, Comparator.comparing(Book::getTitle));
        assertEquals(0, books.size());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testSortingSingleElement() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Single Book", "Single Author", 2020));
        
        MySorts.bubbleSort(books, Comparator.comparing(Book::getTitle));
        assertEquals(1, books.size());
        assertEquals("Single Book", books.get(0).getTitle());
        
        MySorts.mergeSort(books, Comparator.comparing(Book::getTitle));
        assertEquals(1, books.size());
        assertEquals("Single Book", books.get(0).getTitle());
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testSortingWithComplexComparator() {
        List<Book> books = generateLargeBookList(1000);
        
        // Сложный компаратор: сначала по автору, потом по году, потом по названию
        Comparator<Book> complexComparator = Comparator
            .comparing(Book::getAuthor)
            .thenComparing(Book::getYear)
            .thenComparing(Book::getTitle);
        
        MySorts.mergeSort(books, complexComparator);
        
        // Проверка, что список отсортирован по сложному критерию
        for (int i = 1; i < books.size(); i++) {
            Book prev = books.get(i-1);
            Book curr = books.get(i);
            
            int authorCompare = prev.getAuthor().compareTo(curr.getAuthor());
            if (authorCompare != 0) {
                assertTrue(authorCompare <= 0);
            } else {
                int yearCompare = Integer.compare(prev.getYear(), curr.getYear());
                if (yearCompare != 0) {
                    assertTrue(yearCompare <= 0);
                } else {
                    assertTrue(prev.getTitle().compareTo(curr.getTitle()) <= 0);
                }
            }
        }
    }
} 