package org.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.model.Book;
import org.library.model.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    private LibraryService service;

    @BeforeEach
    void setUp() {
        service = new LibraryService();
    }

    @Test
    void testUserRegistrationAndLogin() {
        assertTrue(service.registerUser("testuser", "testpass"));
        assertFalse(service.registerUser("testuser", "anotherpass"));

        assertTrue(service.loginUser("testuser", "testpass"));
        assertNotNull(service.getLoggedInUser());
        assertEquals("testuser", service.getLoggedInUser().getUsername());

        assertFalse(service.loginUser("wronguser", "wrongpass"));
        assertNull(service.getLoggedInUser());

        assertFalse(service.loginUser("testuser", "wrongpass"));
        assertNull(service.getLoggedInUser());

        assertTrue(service.loginUser("admin", "adminpass"));
        assertNotNull(service.getLoggedInUser());
        assertEquals("admin", service.getLoggedInUser().getUsername());
        service.logoutUser();
    }

    @Test
    void testLogoutUser() {
        service.registerUser("logoutuser", "pass");
        service.loginUser("logoutuser", "pass");
        assertNotNull(service.getLoggedInUser());
        service.logoutUser();
        assertNull(service.getLoggedInUser());
    }

    @Test
    void testAddAndGetAllBooks() {
        Book b1 = new Book("A", "Author1", 2000);
        Book b2 = new Book("B", "Author2", 2001);
        service.addBook(b1);
        service.addBook(b2);
        List<Book> all = service.getAllBooks();
        assertTrue(all.contains(b1));
        assertTrue(all.contains(b2));
        assertEquals(2, all.size());
    }

    @Test
    void testSearchBooks() {
        Book b1 = new Book("Java Programming", "Smith", 2010);
        Book b2 = new Book("Python Basics", "Jones", 2015);
        service.addBook(b1);
        service.addBook(b2);
        List<Book> found = service.searchBooks("java");
        assertTrue(found.contains(b1));
        assertFalse(found.contains(b2));
        assertEquals(1, found.size());

        found = service.searchBooks("2015");
        assertTrue(found.contains(b2));
        assertEquals(1, found.size());

        found = service.searchBooks("nonexistent");
        assertTrue(found.isEmpty());
    }

    @Test
    void testBinarySearchBookByTitle() {
        service.addBook(new Book("Algorithm Design", "Author A", 2000));
        service.addBook(new Book("Data Structures", "Author B", 2005));
        service.addBook(new Book("Operating Systems", "Author C", 2010));

        Book found = service.binarySearchBookByTitle("Data Structures");
        assertNotNull(found);
        assertEquals("Data Structures", found.getTitle());

        found = service.binarySearchBookByTitle("algorithm design");
        assertNotNull(found);
        assertEquals("Algorithm Design", found.getTitle());

        found = service.binarySearchBookByTitle("Nonexistent Book");
        assertNull(found);

        service = new LibraryService();
        found = service.binarySearchBookByTitle("Any Book");
        assertNull(found);
    }

    @Test
    void testBorrowAndReturnBook() {
        service.registerUser("borrower", "pass123");
        service.loginUser("borrower", "pass123");
        User currentUser = service.getLoggedInUser();
        assertNotNull(currentUser);

        Book book = new Book("Test Book", "Test Author", 2020);
        service.addBook(book);

        assertTrue(service.borrowBook("Test Book"));
        assertTrue(book.isBorrowed());
        assertEquals("borrower", book.getBorrowedBy());

        assertFalse(service.borrowBook("Test Book"));

        assertFalse(service.borrowBook("Nonexistent Book"));

        assertTrue(service.returnBook("Test Book"));
        assertFalse(book.isBorrowed());
        assertNull(book.getBorrowedBy());

        assertFalse(service.returnBook("Test Book"));

        assertFalse(service.returnBook("Nonexistent Book"));

        Book book2 = new Book("Another Book", "Another Author", 2021);
        service.addBook(book2);
        service.borrowBook("Another Book");
        service.logoutUser();
        service.registerUser("otheruser", "otherpass");
        service.loginUser("otheruser", "otherpass");
        assertFalse(service.returnBook("Another Book"));
    }

    @Test
    void testBorrowAndReturnBookWithoutLogin() {
        Book book = new Book("Login Test Book", "Test Author", 2020);
        service.addBook(book);

        service.logoutUser();
        assertFalse(service.borrowBook("Login Test Book"));
        assertFalse(service.returnBook("Login Test Book"));
    }

    @Test
    void testSortBooks() {
        Book b1 = new Book("C", "B", 2002);
        Book b2 = new Book("A", "A", 2001);
        Book b3 = new Book("B", "C", 2003);
        service.addBook(b1);
        service.addBook(b2);
        service.addBook(b3);

        List<Book> sortedByTitleBubble = service.sortBooks("1", "1");
        assertEquals("A", sortedByTitleBubble.get(0).getTitle());
        assertEquals("B", sortedByTitleBubble.get(1).getTitle());
        assertEquals("C", sortedByTitleBubble.get(2).getTitle());

        List<Book> sortedByAuthorMerge = service.sortBooks("2", "2");
        assertEquals("A", sortedByAuthorMerge.get(0).getAuthor());
        assertEquals("B", sortedByAuthorMerge.get(1).getAuthor());
        assertEquals("C", sortedByAuthorMerge.get(2).getAuthor());

        List<Book> sortedByYearInsertion = service.sortBooks("3", "3");
        assertEquals(2001, sortedByYearInsertion.get(0).getYear());
        assertEquals(2002, sortedByYearInsertion.get(1).getYear());
        assertEquals(2003, sortedByYearInsertion.get(2).getYear());

        List<Book> sortedByTitleBuiltIn = service.sortBooks("1", "4");
        assertEquals("A", sortedByTitleBuiltIn.get(0).getTitle());
        assertEquals("B", sortedByTitleBuiltIn.get(1).getTitle());
        assertEquals("C", sortedByTitleBuiltIn.get(2).getTitle());

        List<Book> originalBooks = new java.util.ArrayList<>(service.getAllBooks());
        List<Book> invalidSortType = service.sortBooks("99", "1");
        assertEquals(originalBooks.size(), invalidSortType.size());

        List<Book> invalidAlgoType = service.sortBooks("1", "99");
        assertEquals("A", invalidAlgoType.get(0).getTitle());
    }
} 