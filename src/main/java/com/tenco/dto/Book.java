package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 도서 테이블
 */
@Data
@AllArgsConstructor
@Builder
public class Book {
    private int id;                     // 도서ID
    private String title;               // 도서 제목
    private String author;              // 저자
    private String publisher;           // 출판사
    private int publicationYear;        // 출판년도
    private String isbn;                // ISBN
    private boolean available;          // 대출 가능 여부

    public Book(String title, String author, String publisher, int publicationYear, String isbn, boolean available) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.available = available;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publicationYear=" + publicationYear +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
