package com.tenco.dao;

import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;
import com.tenco.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
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
                    int bookId = resultSet.getInt("book_id");
                    int studentId = resultSet.getInt("student_id");
                    Date dateBorrow = resultSet.getDate("borrow_date");
                    Date dateReturnDate = resultSet.getDate("return_date");

                    Borrow.BorrowBuilder borrowBuilder = Borrow.builder();
                    borrowBuilder
                            .id(resultSet.getInt("id"))
                            .bookId(bookId)
                            .studentId(studentId)
                            .borrowDate(dateBorrow != null ? dateBorrow.toLocalDate() : null)
                            .returnDate(dateReturnDate != null ? dateReturnDate.toLocalDate() : null);

                    // JOIN된 books 레코드 + HashMap으로 중복방지
                    Book book = bookMap.get(bookId);
                    if (book == null) {
                        book = Book.builder()
                                .id(bookId)
                                .title(resultSet.getString("title"))
                                .author(resultSet.getString("author"))
                                .publisher(resultSet.getString("publisher"))
                                .publicationYear(resultSet.getInt("publication_year"))
                                .isbn(resultSet.getString("isbn"))
                                .available(resultSet.getBoolean("available"))
                            .build();
                        bookMap.put(bookId, book);
                    }
                    borrowBuilder
                            .book(book);

                    // JOIN된 students 레코드 + HashMap으로 중복방지
                    Student student = studentMap.get(studentId);
                    if (student == null) {
                        student = Student.builder()
                                .id(studentId)
                                .name(resultSet.getString("student_name"))
                                .studentId(resultSet.getString("student_no"))
                            .build();
                        studentMap.put(studentId, student);
                    }
                    borrowBuilder
                            .student(student);

                    Borrow borrow = borrowBuilder.build();
                    borrowList.add(borrow);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return borrowList;
    }

    // 2. 도서 대출 기능 (트랜잭션)
    // 2.1. 대출 가능 여부 SELECT
    // 2.2. 도서 대출 INSERT
    // 2.3. 도서 대출 가능 여부 UPDATE
    public void borrowBook(int bookId, int studentId) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            // 트랜잭션 시작
            connection.setAutoCommit(false);

            try {
                // 대출 가능 여부 확인
                String checkSql = """
                        SELECT available FROM books WHERE id = ?
                        """;
                try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                    checkStatement.setInt(1, bookId);

                    try (ResultSet rs = checkStatement.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("존재하지 않는 도서입니다. ID : " + bookId);
                        }

                        if (!rs.getBoolean("available")) {
                            throw new SQLException("현재 대출 중인 도서입니다. 반납 후 이용가능합니다.");
                        }
                    }
                }

                // 도서 대출
                String borrowSql = """
                        INSERT INTO borrows
                               (book_id, student_id, borrow_date)
                        VALUES (?      , ?         , ?          )
                        """;
                int rows;
                try (PreparedStatement borrowStatement = connection.prepareStatement(borrowSql)) {
                    borrowStatement.setInt(1, bookId);
                    borrowStatement.setInt(2, studentId);
                    borrowStatement.setDate(3, Date.valueOf(LocalDate.now()));

                    rows = borrowStatement.executeUpdate();
                }
                if (rows < 0) {
                    throw new SQLException("적용된 기록이 없습니다.");
                }

                // 도서 상태 변경 (대출 불가로)
                String updateSql = """
                        UPDATE books SET
                            available = FALSE
                        WHERE
                            id = ?
                        """;
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setInt(1, bookId);
                    updateStatement.executeUpdate();
                }

                // 확정 처리
                connection.commit();
            } catch (Exception e) {
                // 롤백 처리
                connection.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // 3. 도서 반납 기능 (트랜잭션)
    // 3.1. 대출 기록 확인 SELECT
    // 3.2. 반납 기록 수정 UPDATE
    // 3.2. 대출 가능 여부 수정 UPDATE
    public void returnBook (int bookId, int studentId) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            // 트랜잭션 시작
            connection.setAutoCommit(false);

            try {
                // 반납 대기 확인
                String checkSql = """
                        SELECT
                           id
                         , borrow_date
                         , return_date
                       FROM borrows
                       WHERE
                           book_id = ?
                       AND student_id = ?
                       ORDER BY
                           return_date IS NULL
                         , borrow_date
                       LIMIT 1
                        """;
                int borrowId = 0;
                try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                    checkStatement.setInt(1, bookId);
                    checkStatement.setInt(2, studentId);

                    try (ResultSet rs = checkStatement.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("대출한 기록이 없는 도서입니다. ID : " + bookId);
                        }

                        if (rs.getDate("return_date") != null) {
                            throw new SQLException("이미 반납이 완료된 도서입니다.");
                        }

                        borrowId = rs.getInt("id");
                    }
                }

                // 도서 반납
                String returnSql = """
                        UPDATE borrows SET
                            return_date = ?
                        WHERE
                            id = ?
                        """;
                int rows;
                try (PreparedStatement returnStatement = connection.prepareStatement(returnSql)) {
                    returnStatement.setDate(1, Date.valueOf(LocalDate.now()));
                    returnStatement.setInt(2, borrowId);

                    rows = returnStatement.executeUpdate();
                }
                if (rows < 0) {
                    throw new SQLException("대출 기록이 없어 반납 적용할 수 없습니다.");
                }

                // 도서 상태 변경 (대출 가능으로)
                String updateSql = """
                        UPDATE books SET
                            available = TRUE
                        WHERE
                            id = ?
                        """;
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setInt(1, bookId);
                    updateStatement.executeUpdate();
                }
                if (rows < 0) {
                    throw new SQLException("책 내용이 없어 반납 적용할 수 없습니다.");
                }

                // 확정 처리
                connection.commit();
            } catch (Exception e) {
                // 롤백 처리
                connection.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
