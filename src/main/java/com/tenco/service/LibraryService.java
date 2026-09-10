package com.tenco.service;

import com.tenco.dao.BookDAO;
import com.tenco.dao.BorrowDAO;
import com.tenco.dao.StudentDAO;
import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;

import java.sql.SQLException;
import java.util.List;

// 호출 흐름 View -> Service -> DAO -> DB
public class LibraryService {

    // 인터페이스 먼저 설계... 는 이번엔 하지 않음
    // Service 하나가 DAO 세개를 연관관계로 소유
    private final BookDAO bookDAO = new BookDAO();          // 컴포지션 관계 .... 인터페이스 적용해서 다형성 추구 설계도 생각해보자.
    private final StudentDAO studentDAO = new StudentDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();


    // 도서 추가 기능
    // 1. 제목과 저자가 비어 있는지 확인 (둘 중 하나라도 없으면 중단)
    // 2. 통과하면 DAO에 INSERT 처리를 위임한다.
    public void addBook(Book book) throws SQLException {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty() ||
                book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new SQLException("도서 제목과 저자는 필수 입력 항목입니다.");
        }

        bookDAO.addBook(book);
    }

    // 전체 도서 조회
    // 검사할 규칙이 없으므로 DAO 결과를 그대로 넘깁니다.
    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBook();
    }

    // 도서 제목 검색
    public List<Book> getBookByTitle(String title) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new SQLException("검색어를 입력해주세요");
        }

        return bookDAO.getBookByTitle(title);
    }

    // 학생 등록
    // 이름, 학번이 비어있는지 검사, 통과하면 DAO에 INSERT 처리를 위임한다.
    // 유니크 걸려있는 student_id는 DB에서 확인해야 한다. 이번에는 처리하지 않는다.
    public void addStudent(Student student) throws SQLException {
        if (student.getName() == null || student.getName().trim().isEmpty() ||
            student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new SQLException("이름과 학변은 필수입력 항목입니다.");
        }

        studentDAO.addStudent(student);
    }

    // 전체 학생 조회
    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudent();
    }

    // 로그인 (학번으로 학생 찾기)
    // 1. 학번이 비어있는지 검사
    // 2. DAO에서 해당 학번을 찾는다.
    // 3. 찾으면 Student를 없으면 null을 그대로 View에 돌려준다.
    // 비밀번호 없이 학번만으로 로그인
    public Student getStudentByStudentId(String studentId) throws SQLException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new SQLException("학번을 입력해주세요");
        }
        return studentDAO.getStudentByStudentID(studentId);
    }

    // 도서 대출
    // 1. 도서 ID와 학생 ID가 1 이상인지 검사 (AUTO_INCREMENT는 1부터 시작함)
    // 2. 통과하면 DAO의 트랜잭션 메서드에 위임한다.
    // 로그인 여부 확인은 View에서 처리한다.
    public void borrowBook (int bookId, int studentId) throws SQLException {
        if (bookId <= 0 || studentId <= 0) {
            throw new SQLException("유효한 도서 ID와 유효한 학생 ID를 입력해주세요.");
        }

        borrowDAO.borrowBook(bookId, studentId);
    }

    // 대출 중인 도서 조회
    public List<Borrow> getBorrowedBooks() throws SQLException {
        return borrowDAO.getBorrowedBooks();
    }

    // 도서 반납
    public void returnBook(int bookId, int studentId) throws SQLException {
        if (bookId <= 0 || studentId <= 0) {
            throw new SQLException("유효한 도서 ID와 유효한 학생 ID를 입력해주세요.");
        }

        borrowDAO.returnBook(bookId, studentId);
    }



}
