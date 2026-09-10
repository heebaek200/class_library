package com.tenco.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/library?serverTimezone=Asia/Seoul";
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = SimpleStringEncoder.revealText(System.getenv("DB_PASSWORD"));

    /**
     * 데이터베이스와 연결하고 커넥션 반환
     *
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);

        //System.out.println("Connecting To Database... : %s %s".formatted(
        //        connection.getMetaData().getDatabaseProductName(),
        //        connection.getMetaData().getDatabaseProductVersion()
        //));

        return connection;
    }

    /**
     * 「%」, 「_」 문자열을 와일드카드로 판단하지 않도록 이스케이프
     *
     * @param keyword 입력 문자열
     * @return
     */
    public static String escapeForLike(String keyword) {
        if (keyword == null) return "";
        return keyword.replace("_", "\\_").replace("%", "\\%");
    }

}
