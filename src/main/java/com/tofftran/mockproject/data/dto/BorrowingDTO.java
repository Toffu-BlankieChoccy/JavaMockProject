package com.tofftran.mockproject.data.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
public class BorrowingDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate borrowDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate returnDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    public BorrowingDTO() {
    }

    public BorrowingDTO(Long id, Long bookId, String bookTitle, Long userId, String userName, LocalDate borrowDate, LocalDate returnDate, LocalDate dueDate) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.userName = userName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.dueDate = dueDate;
    }

}
