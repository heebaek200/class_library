package com.tenco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 학생 테이블
 */
@Data
@AllArgsConstructor
@Builder
public class Student {
    private int id;                         // 학생ID
    private String name;                    // 학생 이름
    private String studentId;               // 학번

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", studentId='" + studentId + '\'' +
                '}';
    }
}