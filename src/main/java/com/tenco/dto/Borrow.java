package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 대출 테이블
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrow {
    private int id;                         // 대출ID
    private int bookId;                     // 도서ID
    private int studentId;                  // 학생ID
    private LocalDate borrowDate;           // 대출일
    private LocalDate returnDate;           // 반납일

    private Book book;
    private Student student;

    @Override
    public String toString() {
        return "Borrow{" +
                "\n    id=" + id +
                "\n  , bookId=" + bookId +
                "\n  , studentId=" + studentId +
                "\n  , borrowDate=" + borrowDate +
                "\n  , returnDate=" + returnDate +
                "\n  , book=" + (book == null ? null : book.toString()) +
                "\n  , student=" + (student == null ? null : student.toString()) +
                "\n}\n";
    }
}