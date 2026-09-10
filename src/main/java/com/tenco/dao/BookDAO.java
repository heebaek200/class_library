package com.tenco.dao;

import com.tenco.dto.Book;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // 도서 전체 검색 기능
    public List<Book> getAllBook() {
        List<Book> bookList = new ArrayList<>();

        String sql = "SELECT * FROM books ORDER BY id";
        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    bookList.add(createBook(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bookList;
    }


    // 제목으로 도서 검색 기능
    public List<Book> getBookByTitle(String title) {
        List<Book> bookList = new ArrayList<>();
        String sql = """
                SELECT *
                FROM books
                WHERE title LIKE ?
                ORDER BY id;
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                // 「%」, 「_」 문자열을 와일드카드로 판단하지 않도록 이스케이프
                String preparedTitle = "%" + DatabaseUtil.escapeForLike(title) + "%";

                preparedStatement.setString(1, preparedTitle);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    bookList.add(createBook(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bookList;
    }


    // 도서 등록 기능
    public int addBook(Book book) {
        int rows = 0;

        String sql = """
                INSERT INTO books
                       (title, author, publisher, publication_year, isbn)
                VALUES (?    , ?     , ?        , ?               , ?   )
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, book.getTitle());
                preparedStatement.setString(2, book.getAuthor());
                preparedStatement.setString(3, book.getPublisher());
                preparedStatement.setInt(4, book.getPublicationYear());
                preparedStatement.setString(5, book.getIsbn());

                rows = preparedStatement.executeUpdate();
                System.out.println(rows + " 행 추가되었습니다.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rows;
    }

    /**
     * SELECT로 조회한 결과로 Book 타입의 레코드 생성
     * - getAllBook()와 getBookByBookID() 공통부분
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private Book createBook(ResultSet resultSet) throws SQLException {
        Book book = new Book(
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getString("publisher"),
                resultSet.getInt("publication_year"),
                resultSet.getString("publisher"),
                resultSet.getBoolean("available")
        );
        return book;
    }

}
