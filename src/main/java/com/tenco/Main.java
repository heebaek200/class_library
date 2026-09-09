package com.tenco;

import com.tenco.dao.BookDAO;
import com.tenco.dao.BorrowDAO;
import com.tenco.dao.StudentDAO;
import com.tenco.dto.Book;
import com.tenco.dto.Borrow;
import com.tenco.dto.Student;

import java.lang.reflect.Array;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        BookDAO bookDAO = new BookDAO();

        // borrows
        BorrowDAO borrowDAO = new BorrowDAO();
        List<Borrow> result = borrowDAO.getBorrowedBooks();
        for (Borrow borrow : result) {
            System.out.print(borrow);
        }

    }
}