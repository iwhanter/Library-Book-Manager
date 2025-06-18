package org.library.service;

import org.library.model.Book;
import org.library.model.User;
import org.library.utils.MyHashTable;
import org.library.utils.MySorts;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

public class LibraryService {
    private MyHashTable<String, Book> books;
    private MyHashTable<String, User> users;
    private User loggedInUser;

    public LibraryService() {
        books = new MyHashTable<>();
        users = new MyHashTable<>();

        users.put("admin", new User("admin", "adminpass"));
    }

    public boolean registerUser(String username, String password) {
        if (users.get(username) == null) {
            users.put(username, new User(username, password));
            return true;
        }
        return false;
    }

    public boolean loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            this.loggedInUser = user;
            return true;
        }
        this.loggedInUser = null;
        return false;
    }

    public void logoutUser() {
        this.loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void addBook(Book book) {
        books.put(book.getTitle() + book.getAuthor() + book.getYear(), book);
    }

    public List<Book> searchBooks(String query) {
        List<Book> result = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                Integer.toString(book.getYear()).contains(query)) {
                result.add(book);
            }
        }
        return result;
    }

    public Book binarySearchBookByTitle(String title) {
        List<Book> allBooks = getAllBooks();
        MySorts.mergeSort(allBooks, Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));

        int low = 0;
        int high = allBooks.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Book midBook = allBooks.get(mid);
            int cmp = midBook.getTitle().compareToIgnoreCase(title);

            if (cmp == 0) {
                return midBook;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public boolean borrowBook(String title) {
        if (loggedInUser == null) {
            System.out.println("Для выдачи книги необходимо войти в систему.");
            return false;
        }
        Book book = findBookByTitle(title);
        if (book != null && !book.isBorrowed()) {
            book.setBorrowed(true);
            book.setBorrowedBy(loggedInUser.getUsername());
            System.out.println("Книга \"" + book.getTitle() + "\" выдана пользователю " + loggedInUser.getUsername());
            return true;
        } else if (book != null && book.isBorrowed()) {
            System.out.println("Книга \"" + book.getTitle() + "\" уже выдана пользователю " + book.getBorrowedBy() + ".");
            return false;
        } else {
            System.out.println("Книга с названием \"" + title + "\" не найдена.");
            return false;
        }
    }

    public boolean returnBook(String title) {
        if (loggedInUser == null) {
            System.out.println("Для возврата книги необходимо войти в систему.");
            return false;
        }
        Book book = findBookByTitle(title);
        if (book != null && book.isBorrowed() && loggedInUser.getUsername().equals(book.getBorrowedBy())) {
            book.setBorrowed(false);
            book.setBorrowedBy(null);
            System.out.println("Книга \"" + book.getTitle() + "\" возвращена.");
            return true;
        } else if (book != null && book.isBorrowed() && !loggedInUser.getUsername().equals(book.getBorrowedBy())) {
            System.out.println("Книга \"" + book.getTitle() + "\" была взята другим пользователем: " + book.getBorrowedBy() + ". Вы не можете ее вернуть.");
            return false;
        } else if (book != null && !book.isBorrowed()) {
            System.out.println("Книга \"" + book.getTitle() + "\" не была выдана.");
            return false;
        } else {
            System.out.println("Книга с названием \"" + title + "\" не найдена.");
            return false;
        }
    }


    private Book findBookByTitle(String title) {
        for (Book book : books.values()) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> sortBooks(String sortType, String sortAlgorithmType) {
        List<Book> all = getAllBooks();
        Comparator<Book> comparator;

        switch (sortType) {
            case "1": 
                comparator = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
                break;
            case "2":
                comparator = Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
                break;
            case "3":
                comparator = Comparator.comparingInt(Book::getYear);
                break;
            default:
                System.out.println("Некорректный выбор критерия сортировки. Сортировка не выполнена.");
                return all;
        }

        switch (sortAlgorithmType) {
            case "1":
                MySorts.bubbleSort(all, comparator);
                break;
            case "2":
                MySorts.mergeSort(all, comparator);
                break;
            case "3":
                insertionSort(all, comparator);
                break;
            case "4":
                all.sort(comparator);
                break;
            default:
                System.out.println("Некорректный выбор алгоритма сортировки. Используется встроенная сортировка.");
                all.sort(comparator);
                break;
        }
        return all;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    private void insertionSort(List<Book> list, Comparator<Book> comparator) {
        for (int i = 1; i < list.size(); i++) {
            Book current = list.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(list.get(j), current) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, current);
        }
    }
} 