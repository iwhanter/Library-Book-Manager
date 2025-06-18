package org.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.library.model.Book;
import org.library.model.User;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceStressTest {

    private LibraryService service;
    private Random random;

    @BeforeEach
    void setUp() {
        service = new LibraryService();
        random = new Random(42); // Фиксированный seed для воспроизводимости
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testLargeBookCollectionOperations() {
        int bookCount = 5000;
        
        // Добавление большого количества книг
        for (int i = 0; i < bookCount; i++) {
            Book book = new Book("Book" + i, "Author" + (i % 100), 2000 + (i % 24));
            service.addBook(book);
        }
        
        List<Book> allBooks = service.getAllBooks();
        assertEquals(bookCount, allBooks.size());
        
        // Тест поиска
        for (int i = 0; i < 100; i++) {
            int searchIndex = random.nextInt(bookCount);
            List<Book> found = service.searchBooks("Book" + searchIndex);
            assertTrue(found.size() >= 1);
        }
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testBinarySearchPerformance() {
        // Добавление книг в случайном порядке
        for (int i = 0; i < 1000; i++) {
            Book book = new Book("Book" + random.nextInt(10000), "Author" + i, 2000 + random.nextInt(24));
            service.addBook(book);
        }
        
        // Тест бинарного поиска
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String searchTitle = "Book" + random.nextInt(10000);
            Book found = service.binarySearchBookByTitle(searchTitle);
            // Не проверяем результат, так как книга может не существовать
        }
        long searchTime = System.currentTimeMillis() - startTime;
        
        // Поиск должен быть быстрым
        assertTrue(searchTime < 5000); // Менее 5 секунд для 100 поисков
    }

    @Test
    @Timeout(value = 25, unit = TimeUnit.SECONDS)
    void testUserManagementStress() {
        int userCount = 2000;
        
        // Регистрация большого количества пользователей
        for (int i = 0; i < userCount; i++) {
            String username = "user" + i;
            String password = "password" + i;
            assertTrue(service.registerUser(username, password));
        }
        
        // Попытка повторной регистрации
        assertFalse(service.registerUser("user0", "newpassword"));
        
        // Тест входа пользователей
        for (int i = 0; i < 100; i++) {
            int userIndex = random.nextInt(userCount);
            String username = "user" + userIndex;
            String password = "password" + userIndex;
            
            assertTrue(service.loginUser(username, password));
            assertEquals(username, service.getLoggedInUser().getUsername());
            service.logoutUser();
        }
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testBookBorrowingStress() {
        // Добавление книг
        for (int i = 0; i < 1000; i++) {
            Book book = new Book("Book" + i, "Author" + i, 2000 + random.nextInt(24));
            service.addBook(book);
        }
        
        // Регистрация пользователей
        for (int i = 0; i < 100; i++) {
            service.registerUser("user" + i, "password" + i);
        }
        
        // Симуляция множественных операций выдачи/возврата
        for (int i = 0; i < 500; i++) {
            int userIndex = random.nextInt(100);
            int bookIndex = random.nextInt(1000);
            
            // Вход пользователя
            service.loginUser("user" + userIndex, "password" + userIndex);
            
            // Попытка выдачи книги
            service.borrowBook("Book" + bookIndex);
            
            // Попытка возврата книги
            service.returnBook("Book" + bookIndex);
            
            service.logoutUser();
        }
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testSortingAlgorithmsStress() {
        // Добавление большого количества книг
        for (int i = 0; i < 2000; i++) {
            Book book = new Book("Book" + random.nextInt(10000), "Author" + random.nextInt(1000), 1900 + random.nextInt(124));
            service.addBook(book);
        }
        
        // Тест всех алгоритмов сортировки
        String[] sortTypes = {"1", "2", "3"}; // Название, Автор, Год
        String[] algorithms = {"1", "2", "3", "4"}; // Пузырьковая, Слиянием, Вставками, Встроенная
        
        for (String sortType : sortTypes) {
            for (String algorithm : algorithms) {
                long startTime = System.currentTimeMillis();
                List<Book> sorted = service.sortBooks(sortType, algorithm);
                long sortTime = System.currentTimeMillis() - startTime;
                
                // Проверка, что сортировка завершилась
                assertNotNull(sorted);
                assertTrue(sorted.size() > 0);
                
                // Проверка, что сортировка не заняла слишком много времени
                assertTrue(sortTime < 10000); // Менее 10 секунд
            }
        }
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testSearchPerformance() {
        // Добавление книг с различными паттернами
        for (int i = 0; i < 3000; i++) {
            String title = "Book" + i + "Title";
            String author = "Author" + i + "Name";
            int year = 2000 + (i % 24);
            service.addBook(new Book(title, author, year));
        }
        
        // Тест производительности поиска
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            String query = "Book" + random.nextInt(3000);
            List<Book> found = service.searchBooks(query);
            // Не проверяем результат, так как поиск может не найти ничего
        }
        long searchTime = System.currentTimeMillis() - startTime;
        
        // Поиск должен быть быстрым
        assertTrue(searchTime < 5000); // Менее 5 секунд для 200 поисков
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testConcurrentUserSessions() {
        // Регистрация пользователей
        for (int i = 0; i < 50; i++) {
            service.registerUser("user" + i, "password" + i);
        }
        
        // Добавление книг
        for (int i = 0; i < 100; i++) {
            service.addBook(new Book("Book" + i, "Author" + i, 2000 + random.nextInt(24)));
        }
        
        // Симуляция множественных сессий пользователей
        for (int i = 0; i < 1000; i++) {
            int userIndex = random.nextInt(50);
            
            // Вход
            assertTrue(service.loginUser("user" + userIndex, "password" + userIndex));
            assertNotNull(service.getLoggedInUser());
            
            // Операции
            int bookIndex = random.nextInt(100);
            service.searchBooks("Book" + bookIndex);
            
            // Выход
            service.logoutUser();
            assertNull(service.getLoggedInUser());
        }
    }

    @Test
    @Timeout(value = 8, unit = TimeUnit.SECONDS)
    void testEdgeCases() {
        // Тест с пустыми строками
        service.addBook(new Book("", "", 2000));
        service.addBook(new Book("Book", "", 2000));
        service.addBook(new Book("", "Author", 2000));
        
        List<Book> found = service.searchBooks("");
        assertTrue(found.size() >= 3);
        
        // Тест с очень длинными строками
        String longString = "A".repeat(1000);
        service.addBook(new Book(longString, longString, 2000));
        
        found = service.searchBooks(longString);
        assertTrue(found.size() >= 1);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testMemoryEfficiency() {
        // Добавление и удаление большого количества данных
        for (int cycle = 0; cycle < 10; cycle++) {
            // Добавление книг
            for (int i = 0; i < 500; i++) {
                service.addBook(new Book("Book" + cycle + "_" + i, "Author" + i, 2000 + random.nextInt(24)));
            }
            
            // Регистрация пользователей
            for (int i = 0; i < 100; i++) {
                service.registerUser("user" + cycle + "_" + i, "password" + i);
            }
            
            // Операции поиска и сортировки
            service.searchBooks("Book");
            service.sortBooks("1", "2");
        }
        
        // Проверка, что система все еще работает
        List<Book> allBooks = service.getAllBooks();
        assertTrue(allBooks.size() > 0);
    }

    @Test
    @Timeout(value = 12, unit = TimeUnit.SECONDS)
    void testMixedOperationsStress() {
        // Смешанные операции: добавление, поиск, сортировка, пользователи
        for (int i = 0; i < 1000; i++) {
            // Добавление книги
            service.addBook(new Book("Book" + i, "Author" + (i % 50), 2000 + random.nextInt(24)));
            
            // Регистрация пользователя (каждый 10-й)
            if (i % 10 == 0) {
                service.registerUser("user" + i, "password" + i);
            }
            
            // Поиск (каждый 5-й)
            if (i % 5 == 0) {
                service.searchBooks("Book" + random.nextInt(i + 1));
            }
            
            // Сортировка (каждый 20-й)
            if (i % 20 == 0) {
                service.sortBooks("1", "2");
            }
        }
        
        // Финальная проверка
        List<Book> allBooks = service.getAllBooks();
        assertTrue(allBooks.size() >= 1000);
    }
} 