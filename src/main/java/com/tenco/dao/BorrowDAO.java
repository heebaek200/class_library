package com.tenco.dao;

import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;
import com.tenco.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 도서 대출/반납 관련 SQL을 실행하는 DAO
 */
public class BorrowDAO {

    // 1. 현재 대출 중인 도서 목록 조회
    // 1.1. JOIN 해서 도서 이름까지 출력
    public List<Borrow> getBorrowedBooks() {
        List<Borrow> borrowList = new ArrayList<>();

        String sql = """
                SELECT
                      br.id
                    , br.book_id
                    , bk.title
                    , bk.author
                    , bk.publisher
                    , bk.publication_year
                    , bk.isbn
                    , bk.available
                    , br.student_id
                    , st.name AS student_name
                    , st.student_id AS student_no
                    , br.borrow_date
                    , br.return_date
                FROM
                    borrows AS br
                JOIN books AS bk
                     ON br.book_id = bk.id
                JOIN students AS st
                     ON br.student_id = st.id
                WHERE
                    br.return_date IS NULL
                ORDER BY br.id;
                """;
        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                Map<Integer, Book> bookMap = new HashMap<>();
                Map<Integer, Student> studentMap = new HashMap<>();

                while (resultSet.next()) {

                    // borrows 레코드
                    Borrow borrow = new Borrow();
                    Date dateReturnDate = resultSet.getDate("return_date");
                    int bookId = resultSet.getInt("book_id");
                    int studentId = resultSet.getInt("student_id");

                    borrow.setId(resultSet.getInt("id"));
                    borrow.setBookId(bookId);
                    borrow.setStudentId(studentId);
                    borrow.setBorrowDate(resultSet.getDate("borrow_date").toLocalDate());
                    borrow.setReturnDate(dateReturnDate != null ? dateReturnDate.toLocalDate() : null);

                    // JOIN된 books 레코드 + HashMap으로 중복방지
                    Book book = bookMap.get(bookId);
                    if (book == null) {
                        book = new Book(
                                bookId,
                                resultSet.getString("title"),
                                resultSet.getString("author"),
                                resultSet.getString("publisher"),
                                resultSet.getInt("publication_year"),
                                resultSet.getString("isbn"),
                                resultSet.getBoolean("available"));
                        bookMap.put(bookId, book);
                    }
                    borrow.setBook(book);

                    // JOIN된 students 레코드 + HashMap으로 중복방지
                    Student student = studentMap.get(studentId);
                    if (student == null) {
                        student = new Student(
                                studentId,
                                resultSet.getString("student_name"),
                                resultSet.getString("student_no")
                        );
                        studentMap.put(studentId, student);
                    }
                    borrow.setStudent(student);


                    borrowList.add(borrow);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return borrowList;
    }

    // 이하 내일 과제

    // 2. 도서 대출 기능 (트랜잭션)
    // 2.1. 대출 가능 여부 SELECT
    // 2.2. 도서 대출 INSERT

    // 3. 도서 반납 기능 (트랜잭션)
    // 3.1. 대출 기록 확인 SELECT
    // 3.2. 반납 기록 등록 UPDATE?

}
