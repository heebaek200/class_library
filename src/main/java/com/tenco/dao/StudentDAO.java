package com.tenco.dao;

import com.tenco.dto.Student;
import com.tenco.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // 학생 등록 기능
    public int addStudent(Student student) {
        int rows = 0;

        String sql = """
                INSERT INTO students
                       (name, student_id)
                VALUES (?   , ?         )
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, student.getName());
                preparedStatement.setString(2, student.getStudentId());

                rows = preparedStatement.executeUpdate();
                System.out.println(rows + " 행 추가되었습니다.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rows;
    }

    // 데이터 조회
    public List<Student> getAllStudent() {
        List<Student> studentList = new ArrayList<>();

        String sql = "SELECT * FROM students ORDER BY id";
        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    studentList.add(createStudent(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return studentList;
    }

    // 학번으로 학생 조회
    public Student getStudentByStudentID(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection connection = DatabaseUtil.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, studentId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return createStudent(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * SELECT로 조회한 결과로 Student 타입의 레코드 생성
     * - getAllStudent()와 getStudentByStudentID() 공통부분
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private Student createStudent(ResultSet resultSet) throws SQLException {
        Student student = new Student(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("student_id")
        );
        return student;
    }

}
