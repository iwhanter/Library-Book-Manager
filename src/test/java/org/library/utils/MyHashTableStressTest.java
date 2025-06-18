package org.library.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.library.model.Book;
import org.library.model.User;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class MyHashTableStressTest {

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testLargeDataSetInsertion() {
        MyHashTable<String, Integer> table = new MyHashTable<>();
        int size = 10000;
        
        // Вставка большого количества элементов
        for (int i = 0; i < size; i++) {
            table.put("key" + i, i);
        }
        
        // Проверка, что все элементы доступны
        for (int i = 0; i < size; i++) {
            assertEquals(i, table.get("key" + i));
        }
        
        List<Integer> values = table.values();
        assertEquals(size, values.size());
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testLargeDataSetWithBooks() {
        MyHashTable<String, Book> table = new MyHashTable<>();
        int size = 5000;
        
        // Создание и вставка большого количества книг
        for (int i = 0; i < size; i++) {
            Book book = new Book("Book" + i, "Author" + i, 2000 + i % 50);
            table.put("book" + i, book);
        }
        
        // Проверка поиска
        for (int i = 0; i < size; i += 100) {
            Book found = table.get("book" + i);
            assertNotNull(found);
            assertEquals("Book" + i, found.getTitle());
        }
        
        List<Book> books = table.values();
        assertEquals(size, books.size());
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testConcurrentAccessPattern() {
        MyHashTable<String, User> table = new MyHashTable<>();
        int operations = 10000;
        
        // Симуляция множественных операций вставки/удаления
        for (int i = 0; i < operations; i++) {
            String key = "user" + (i % 1000);
            if (i % 3 == 0) {
                // Удаление
                table.remove(key);
            } else {
                // Вставка/обновление
                table.put(key, new User(key, "password" + i));
            }
        }
        
        // Проверка финального состояния
        List<User> users = table.values();
        assertTrue(users.size() > 0);
        assertTrue(users.size() <= 1000);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testHashCollisionStress() {
        MyHashTable<String, Integer> table = new MyHashTable<>();
        
        // Создание ключей, которые могут вызывать коллизии
        for (int i = 0; i < 1000; i++) {
            String key = "collision" + (i % 10); // Много ключей с одинаковым хешем
            table.put(key, i);
        }
        
        // Проверка, что последнее значение сохранилось
        assertEquals(999, table.get("collision9"));
    }

    @Test
    @Timeout(value = 8, unit = TimeUnit.SECONDS)
    void testRandomAccessPattern() {
        MyHashTable<Integer, String> table = new MyHashTable<>();
        Random random = new Random(42); // Фиксированный seed для воспроизводимости
        int operations = 5000;
        
        for (int i = 0; i < operations; i++) {
            int key = random.nextInt(1000);
            String value = "value" + i;
            
            if (random.nextBoolean()) {
                table.put(key, value);
            } else {
                table.get(key);
            }
        }
        
        // Проверка, что таблица не сломалась
        assertNotNull(table.values());
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMemoryEfficiency() {
        MyHashTable<String, String> table = new MyHashTable<>();
        int size = 10000;
        
        // Заполнение таблицы
        for (int i = 0; i < size; i++) {
            table.put("key" + i, "value" + i);
        }
        
        // Удаление половины элементов
        for (int i = 0; i < size; i += 2) {
            table.remove("key" + i);
        }
        
        // Проверка, что оставшиеся элементы доступны
        List<String> values = table.values();
        assertEquals(size / 2, values.size());
        
        for (int i = 1; i < size; i += 2) {
            assertEquals("value" + i, table.get("key" + i));
        }
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testNullKeyHandling() {
        MyHashTable<String, Integer> table = new MyHashTable<>();
        
        // Тест с null ключами
        table.put(null, 42);
        assertEquals(42, table.get(null));
        
        table.put(null, 100);
        assertEquals(100, table.get(null));
        
        assertTrue(table.remove(null));
        assertNull(table.get(null));
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testEmptyTableOperations() {
        MyHashTable<String, Integer> table = new MyHashTable<>();
        
        // Операции с пустой таблицей
        assertNull(table.get("nonexistent"));
        assertFalse(table.remove("nonexistent"));
        
        List<Integer> values = table.values();
        assertNotNull(values);
        assertEquals(0, values.size());
    }
} 