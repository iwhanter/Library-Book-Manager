package org.library.model;

import java.util.Objects;

public class Book {
    private String title;
    private String author;
    private int year;
    private boolean isBorrowed;
    private String borrowedBy;

    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.isBorrowed = false;
        this.borrowedBy = null;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public boolean isBorrowed() { return isBorrowed; }
    public String getBorrowedBy() { return borrowedBy; }

    public void setBorrowed(boolean borrowed) { isBorrowed = borrowed; }
    public void setBorrowedBy(String borrowedBy) { this.borrowedBy = borrowedBy; }

    @Override
    public String toString() {
        return String.format("%s, %s (%d) [Статус: %s%s]",
                             title, author, year,
                             isBorrowed ? "Выдана" : "Доступна",
                             isBorrowed ? ", Пользователь: " + borrowedBy : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year && Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year);
    }
} 