package org.library.app;

import org.library.service.LibraryService;
import org.library.model.Book;
import org.library.model.User;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

public class LibraryApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryService libraryService = new LibraryService();

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                СИСТЕМА УПРАВЛЕНИЯ БИБЛИОТЕКОЙ               ║");
        System.out.println("║                    Library Book Manager                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        // Добавляем несколько тестовых книг для демонстрации
        addSampleBooks();
        
        User currentUser = null;

        while (true) {
            printMainMenu(currentUser);
            String choice = getValidChoice();

            if (currentUser == null) {
                currentUser = handleUnauthenticatedMenu(choice);
            } else {
                handleAuthenticatedMenu(choice, currentUser);
                if (choice.equals("8")) {
                    currentUser = null;
                }
            }
            
            if (choice.equals("0")) {
                break;
            }
        }
        
        System.out.println("\nСпасибо за использование системы управления библиотекой!");
        scanner.close();
    }

    private static void addSampleBooks() {
        Book[] sampleBooks = {
            new Book("Война и мир", "Лев Толстой", 1869),
            new Book("Преступление и наказание", "Федор Достоевский", 1866),
            new Book("Мастер и Маргарита", "Михаил Булгаков", 1967),
            new Book("1984", "Джордж Оруэлл", 1949),
            new Book("Гарри Поттер и философский камень", "Дж. К. Роулинг", 1997),
            new Book("Властелин колец", "Дж. Р. Р. Толкин", 1954),
            new Book("Алгоритмы. Построение и анализ", "Томас Кормен", 1990),
            new Book("Чистый код", "Роберт Мартин", 2008)
        };
        
        for (Book book : sampleBooks) {
            libraryService.addBook(book);
        }
    }

    private static void printMainMenu(User currentUser) {
        System.out.println("\n" + "═".repeat(60));
        if (currentUser == null) {
            System.out.println("🔐 АВТОРИЗАЦИЯ");
            System.out.println("1. 📝 Зарегистрироваться");
            System.out.println("2. 🔑 Войти в систему");
        } else {
            System.out.println("👤 Пользователь: " + currentUser.getUsername());
            System.out.println("📚 УПРАВЛЕНИЕ БИБЛИОТЕКОЙ");
            System.out.println("1. ➕ Добавить книгу");
            System.out.println("2. 🔍 Поиск книги (по подстроке)");
            System.out.println("3. 📊 Сортировка книг");
            System.out.println("4. 📖 Показать все книги");
            System.out.println("5. 📤 Выдать книгу");
            System.out.println("6. 📥 Вернуть книгу");
            System.out.println("7. 🎯 Точный поиск книги (бинарный поиск)");
            System.out.println("8. 🚪 Выйти из аккаунта");
        }
        System.out.println("0. ❌ Выход из программы");
        System.out.println("═".repeat(60));
    }

    private static String getValidChoice() {
        System.out.print("Выберите действие: ");
        return scanner.nextLine().trim();
    }

    private static User handleUnauthenticatedMenu(String choice) {
        switch (choice) {
            case "1":
                return handleRegistration();
            case "2":
                return handleLogin();
            case "0":
                return null;
            default:
                System.out.println("❌ Некорректный выбор. Пожалуйста, попробуйте снова.");
                return null;
        }
    }

    private static User handleRegistration() {
        System.out.println("\n📝 РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ");
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();
        
        if (username.isEmpty()) {
            System.out.println("❌ Имя пользователя не может быть пустым.");
            return null;
        }
        
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();
        
        if (password.isEmpty()) {
            System.out.println("❌ Пароль не может быть пустым.");
            return null;
        }
        
        if (libraryService.registerUser(username, password)) {
            System.out.println("✅ Пользователь " + username + " успешно зарегистрирован.");
            return libraryService.getLoggedInUser();
        } else {
            System.out.println("❌ Пользователь с таким именем уже существует.");
            return null;
        }
    }

    private static User handleLogin() {
        System.out.println("\n🔑 ВХОД В СИСТЕМУ");
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();
        
        if (libraryService.loginUser(username, password)) {
            User user = libraryService.getLoggedInUser();
            System.out.println("✅ Добро пожаловать, " + user.getUsername() + "!");
            return user;
        } else {
            System.out.println("❌ Неверное имя пользователя или пароль.");
            return null;
        }
    }

    private static void handleAuthenticatedMenu(String choice, User currentUser) {
        switch (choice) {
            case "1":
                handleAddBook();
                break;
            case "2":
                handleSearchBooks();
                break;
            case "3":
                handleSortBooks();
                break;
            case "4":
                handleShowAllBooks();
                break;
            case "5":
                handleBorrowBook();
                break;
            case "6":
                handleReturnBook();
                break;
            case "7":
                handleBinarySearch();
                break;
            case "8":
                libraryService.logoutUser();
                System.out.println("👋 Вы вышли из аккаунта.");
                break;
            case "0":
                break;
            default:
                System.out.println("❌ Некорректный выбор. Пожалуйста, попробуйте снова.");
        }
    }

    private static void handleAddBook() {
        System.out.println("\n➕ ДОБАВЛЕНИЕ НОВОЙ КНИГИ");
        System.out.print("Введите название книги: ");
        String title = scanner.nextLine().trim();
        
        if (title.isEmpty()) {
            System.out.println("❌ Название книги не может быть пустым.");
            return;
        }
        
        System.out.print("Введите автора: ");
        String author = scanner.nextLine().trim();
        
        if (author.isEmpty()) {
            System.out.println("❌ Имя автора не может быть пустым.");
            return;
        }
        
        int year = getValidYear();
        if (year == -1) return;
        
        Book newBook = new Book(title, author, year);
        libraryService.addBook(newBook);
        System.out.println("✅ Книга \"" + title + "\" успешно добавлена в библиотеку.");
    }

    private static int getValidYear() {
        while (true) {
            System.out.print("Введите год издания: ");
            try {
                int year = Integer.parseInt(scanner.nextLine().trim());
                if (year < 1000 || year > 2024) {
                    System.out.println("❌ Год должен быть между 1000 и 2024.");
                    continue;
                }
                return year;
            } catch (NumberFormatException e) {
                System.out.println("❌ Некорректный год. Пожалуйста, введите число.");
            }
        }
    }

    private static void handleSearchBooks() {
        System.out.println("\n🔍 ПОИСК КНИГ");
        System.out.print("Введите поисковый запрос: ");
        String query = scanner.nextLine().trim();
        
        if (query.isEmpty()) {
            System.out.println("❌ Поисковый запрос не может быть пустым.");
            return;
        }
        
        List<Book> found = libraryService.searchBooks(query);
        if (found.isEmpty()) {
            System.out.println("📭 Книги не найдены.");
        } else {
            System.out.println("📚 Найденные книги (" + found.size() + "):");
            for (int i = 0; i < found.size(); i++) {
                System.out.println((i + 1) + ". " + found.get(i));
            }
        }
    }

    private static void handleSortBooks() {
        System.out.println("\n📊 СОРТИРОВКА КНИГ");
        System.out.println("Выберите критерий сортировки:");
        System.out.println("1. 📖 По названию");
        System.out.println("2. 👤 По автору");
        System.out.println("3. 📅 По году издания");
        
        String sortType = getValidChoice();
        if (!sortType.matches("[1-3]")) {
            System.out.println("❌ Некорректный выбор критерия сортировки.");
            return;
        }
        
        System.out.println("Выберите алгоритм сортировки:");
        System.out.println("1. 🫧 Пузырьковая сортировка");
        System.out.println("2. 🔄 Сортировка слиянием");
        System.out.println("3. 📌 Сортировка вставками");
        System.out.println("4. ⚡ Встроенная сортировка Java");
        
        String algorithmType = getValidChoice();
        if (!algorithmType.matches("[1-4]")) {
            System.out.println("❌ Некорректный выбор алгоритма сортировки.");
            return;
        }
        
        List<Book> sorted = libraryService.sortBooks(sortType, algorithmType);
        if (sorted.isEmpty()) {
            System.out.println("📭 В библиотеке нет книг для сортировки.");
        } else {
            System.out.println("📚 Отсортированные книги (" + sorted.size() + "):");
            for (int i = 0; i < sorted.size(); i++) {
                System.out.println((i + 1) + ". " + sorted.get(i));
            }
        }
    }

    private static void handleShowAllBooks() {
        System.out.println("\n📖 ВСЕ КНИГИ В БИБЛИОТЕКЕ");
        List<Book> allBooks = libraryService.getAllBooks();
        if (allBooks.isEmpty()) {
            System.out.println("📭 В библиотеке нет книг.");
        } else {
            System.out.println("📚 Всего книг: " + allBooks.size());
            for (int i = 0; i < allBooks.size(); i++) {
                System.out.println((i + 1) + ". " + allBooks.get(i));
            }
        }
    }

    private static void handleBorrowBook() {
        System.out.println("\n📤 ВЫДАЧА КНИГИ");
        System.out.print("Введите точное название книги для выдачи: ");
        String title = scanner.nextLine().trim();
        
        if (title.isEmpty()) {
            System.out.println("❌ Название книги не может быть пустым.");
            return;
        }
        
        boolean success = libraryService.borrowBook(title);
        if (success) {
            System.out.println("✅ Книга успешно выдана.");
        }
    }

    private static void handleReturnBook() {
        System.out.println("\n📥 ВОЗВРАТ КНИГИ");
        System.out.print("Введите точное название книги для возврата: ");
        String title = scanner.nextLine().trim();
        
        if (title.isEmpty()) {
            System.out.println("❌ Название книги не может быть пустым.");
            return;
        }
        
        boolean success = libraryService.returnBook(title);
        if (success) {
            System.out.println("✅ Книга успешно возвращена.");
        }
    }

    private static void handleBinarySearch() {
        System.out.println("\n🎯 ТОЧНЫЙ ПОИСК КНИГИ (БИНАРНЫЙ ПОИСК)");
        System.out.print("Введите точное название книги: ");
        String title = scanner.nextLine().trim();
        
        if (title.isEmpty()) {
            System.out.println("❌ Название книги не может быть пустым.");
            return;
        }
        
        Book foundBook = libraryService.binarySearchBookByTitle(title);
        if (foundBook != null) {
            System.out.println("✅ Книга найдена:");
            System.out.println("   📖 " + foundBook);
        } else {
            System.out.println("❌ Книга с названием \"" + title + "\" не найдена.");
        }
    }
} 