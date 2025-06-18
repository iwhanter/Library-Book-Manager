package org.library.utils;

import org.junit.jupiter.api.Test;
import org.library.model.Book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MySortsTest {

    private List<Book> getSampleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Clean Code", "Robert Martin", 2008));
        books.add(new Book("The Pragmatic Programmer", "Andrew Hunt", 1999));
        books.add(new Book("Code Complete", "Steve McConnell", 1993));
        books.add(new Book("Refactoring", "Martin Fowler", 1999));
        return books;
    }

    @Test
    void testBubbleSortByTitle() {
        List<Book> books = getSampleBooks();
        MySorts.bubbleSort(books, Comparator.comparing(Book::getTitle));
        assertEquals("Clean Code", books.get(0).getTitle());
        assertEquals("Code Complete", books.get(1).getTitle());
        assertEquals("Refactoring", books.get(2).getTitle());
        assertEquals("The Pragmatic Programmer", books.get(3).getTitle());
    }

    @Test
    void testMergeSortByYear() {
        List<Book> books = getSampleBooks();
        MySorts.mergeSort(books, Comparator.comparingInt(Book::getYear));
        assertEquals(1993, books.get(0).getYear());
        assertEquals(1999, books.get(1).getYear());
        assertEquals(1999, books.get(2).getYear());
        assertEquals(2008, books.get(3).getYear());
    }

    @Test
    void testBubbleSortEmptyList() {
        List<Book> books = new ArrayList<>();
        MySorts.bubbleSort(books, Comparator.comparing(Book::getTitle));
        assertEquals(0, books.size());
    }

    @Test
    void testMergeSortSingleElementList() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Single Book", "Author", 2020));
        MySorts.mergeSort(books, Comparator.comparing(Book::getTitle));
        assertEquals(1, books.size());
        assertEquals("Single Book", books.get(0).getTitle());
    }
} 