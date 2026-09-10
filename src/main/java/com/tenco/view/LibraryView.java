package com.tenco.view;

import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;
import com.tenco.service.LibraryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// 사용자의 입출력을 처리하는 View 클래스
// 키보드 입력을 받아 Service에 넘기고 결과를 화면에 출력합니다.
// SQL을 실행하지 않고, 업무 규칙을 판단하지 않습니다.
public class LibraryView {

    private final LibraryService libraryService = new LibraryService();
    private final Scanner scanner = new Scanner(System.in);

    // 현재 학생 정보가 null이 아니라면 로그인된 상태 / null이라면 로그인이 필요한 기능에서 로그인 요청을 유도한다.
    private Integer currentStudentId = null;
    private String currentStudentName = null;
    private Student currentStudent = null;

    // 로그인
    private void logIn() throws SQLException {
        System.out.println(" - 학번을 입력해주세요 :");
        String studentId = scanner.nextLine();

        Student student = libraryService.getStudentByStudentId(studentId);
        if (student != null) {
            // 로그인 정보 저장
            currentStudentId = student.getId();
            currentStudentName = student.getName();
            currentStudent = student;

            System.out.println("로그인에 성공하였습니다. " + currentStudentName + " 님, 환영합니다.");
        } else {
            System.out.println("로그인에 실패하였습니다.");
        }
    }

    // 로그인 판별
    public boolean checkLogIn() {
        boolean checker = currentStudentId != null;
        if (!checker) {
            System.out.println("로그인 되어 있지 않습니다. 먼저 로그인 해주세요.");
        }

        return checker;
    }

    // 로그아웃
    private void logOut() {
        System.out.println(" - 로그아웃 하시겠습니까? Y/N");
        String yes = scanner.nextLine().trim().toUpperCase();

        if (yes.equals("Y")) {
            System.out.println("로그아웃 되었습니다. " + currentStudentName + " 님, 좋은 하루되세요.");

            // 로그아웃
            currentStudentId = null;
            currentStudentName = null;
            currentStudent = null;
        } else {
            System.out.println("로그아웃하지 않았습니다. 조작으로 돌아갑니다.");
        }
    }

    // 도서 추가 기능
    private void addBook() throws SQLException {
        Book.BookBuilder bookBuilder = Book.builder();
        System.out.println("등록할 도서의 제목을 입력해주세요 :");
        bookBuilder.title(scanner.nextLine().trim());

        System.out.println("저자를 입력해주세요 :");
        bookBuilder.author(scanner.nextLine().trim());

        System.out.println("출판사를 입력해주세요 :");
        bookBuilder.publisher(scanner.nextLine().trim());

        System.out.println("출판년도를 입력해주세요 :");
        int publicationYear;
        try {
            publicationYear = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            publicationYear = 0;
        }
        bookBuilder.publicationYear(publicationYear);

        System.out.println("ISBN을 입력해주세요 :");
        bookBuilder.isbn(scanner.nextLine().trim());

        libraryService.addBook(bookBuilder.build());

        System.out.println("도서 등록이 완료되었습니다.");
    }

    // 전체 도서 조회
    private void getAllBooks() throws SQLException {
        List<Book> bookList = libraryService.getAllBooks();
        for (Book book : bookList) {
            System.out.println("  " + book.toString());
        }

        if (bookList.size() == 0) {
            System.out.println("등록된 도서가 없습니다.");
        }
    }

    // 도서 제목 검색
    private void getBookByTitle() throws SQLException {
        System.out.println("검색할 도서의 제목을 입력해주세요 :");
        String title = scanner.nextLine().trim();

        List<Book> bookList = libraryService.getBookByTitle(title);
        for (Book book : bookList) {
            System.out.println("  " + book.toString());
        }

        if (bookList.size() == 0) {
            System.out.println("입력한 제목의 도서를 찾을 수 없습니다.");
        }
    }

    // 학생 등록
    private void addStudent() throws SQLException {
        Student.StudentBuilder studentBuilder = Student.builder();
        System.out.println("등록할 학생의 이름을 입력해주세요 :");
        studentBuilder.name(scanner.nextLine().trim());

        System.out.println("학번을 입력해주세요 :");
        studentBuilder.studentId(scanner.nextLine().trim());

        libraryService.addStudent(studentBuilder.build());

        System.out.println("학생 등록이 완료되었습니다.");
    }

    // 전체 학생 조회
    private void getAllStudents() throws SQLException {
        List<Student> studentList = libraryService.getAllStudents();
        for (Student student : studentList) {
            System.out.println("  " + student.toString());
        }

        if (studentList.size() == 0) {
            System.out.println("등록된 학생이 없습니다.");
        }
    }

    // 도서 대출
    private void borrowBook() throws SQLException {
        System.out.println("대출할 도서의 ID를 입력해주세요 :");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            bookId = 0;
        }

        libraryService.borrowBook(bookId, currentStudentId);
        System.out.println("도서를 대출하였습니다.");
    }

    // 대출 중인 도서 조회
    private void getBorrowedBooks() throws SQLException {
        List<Borrow> borrowList = libraryService.getBorrowedBooks();
        for (Borrow borrow : borrowList) {
            System.out.println("  " + borrow.toString());
        }

        if (borrowList.size() == 0) {
            System.out.println("현재 대출 중인 기록이 없습니다.");
        }
    }

    // 도서 반납
    private void returnBook() throws SQLException {
        System.out.println("반납할 도서의 ID를 입력해주세요 :");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            bookId = 0;
        }

        libraryService.returnBook(bookId, currentStudentId);
        System.out.println("도서를 반납하였습니다.");
    }


    // [처리 순서]
    // 메뉴를 출력한다.
    // 번호를 입력받는다.
    // 번호에 맞는 메서드를 호출한다.
    // 호출 중 SQLException이 나면 에러 메세지를 출력하고 루프는 돌아간다.
    // 0번을 입력하면 루프 종료 및 프로그램 종료
    public void start() {

        boolean continueCheck = true;

        while (continueCheck) {
            // 메뉴 출력
            // 사용자 입력값 받기
            System.out.println("=============================================");
            System.out.println("기능을 선택해주세요.. ");
            System.out.println("1: 로그인");
            System.out.println("2: 로그아웃");
            System.out.println("Q: 도서 등록");
            System.out.println("W : 전체 도서 조회");
            System.out.println("E : 도서 제목 검색");
            System.out.println("A: 학생 등록");
            System.out.println("S: 전체 학생 조회");
            System.out.println("Z: 도서 대출");
            System.out.println("X: 대출 중인 도서 조회");
            System.out.println("C: 도서 반납");
            System.out.println("0 : 종료");


            try {
                String command = scanner.nextLine().trim().toUpperCase();

                // 예시
                continueCheck = switch (command) {
                    case "1" -> {
                        logIn();
                        yield true;
                    }
                    case "2" -> {
                        logOut();
                        yield true;
                    }
                    case "Q" -> {
                        if (checkLogIn()) {
                            addBook();
                        }
                        yield true;
                    }
                    case "W" -> {
                        if (checkLogIn()) {
                            getAllBooks();
                        }
                        yield true;
                    }
                    case "E" -> {
                        if (checkLogIn()) {
                            getBookByTitle();
                        }
                        yield true;
                    }
                    case "A" -> {
                        if (checkLogIn()) {
                            addStudent();
                        }
                        yield true;
                    }
                    case "S" -> {
                        if (checkLogIn()) {
                            getAllStudents();
                        }
                        yield true;
                    }
                    case "Z" -> {
                        if (checkLogIn()) {
                            borrowBook();
                        }
                        yield true;
                    }
                    case "X" -> {
                        if (checkLogIn()) {
                            getBorrowedBooks();
                        }
                        yield true;
                    }
                    case "C" -> {
                        if (checkLogIn()) {
                            returnBook();
                        }
                        yield true;
                    }

                    case "0" -> {
                        System.out.println("프로그램을 종료합니다.");
                        yield false;
                    }
                    default -> {
                        System.out.println(command + " : 존재하지 않는 기능을 선택하였습니다.");
                        yield true;
                    }
                };
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
