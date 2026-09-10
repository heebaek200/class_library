package com.tenco.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// 코드 수정
public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/library?serverTimezone=Asia/Seoul";
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = SimpleStringEncoder.revealText(System.getenv("DB_PASSWORD"));

    private static final HikariDataSource DATA_SOURCE;

    // 첫 getConnection() 호출 시점에 커넥션 풀 작성
    static {
        HikariConfig config = new HikariConfig();

        // 1. 기본 연결 정보 설정
        config.setJdbcUrl(URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);

        // 2. 커넥션 풀 크기 설정
        config.setMaximumPoolSize(10);      // 동시 최대 10개 연결 유지 (스프링부트 기본 값)
        config.setMinimumIdle(5);           // 요청이 없어도 최소 5개 준비 상태로 유지

        // 3. 풀이 가득 찼을 때 빈 연결을 기다리는 최대 시간 (milliseconds)
        config.setConnectionTimeout(3_000);

        // 4. 풀 생성(DB 연결 객체 N개 설정)
        DATA_SOURCE = new HikariDataSource(config);
    }

    /**
     * 데이터베이스와 연결하고 커넥션 반환
     *
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DATA_SOURCE.getConnection();

        return connection;
    }

    // 프로그램 종료 시 풀 전체 정리
    public static void close() {
        if (!DATA_SOURCE.isClosed()) {
            DATA_SOURCE.close();
        }
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
