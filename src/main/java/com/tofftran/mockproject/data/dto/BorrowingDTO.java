package com.tofftran.mockproject.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class BorrowingDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public BorrowingDTO() {
    }

    public BorrowingDTO(Long id, Long bookId, String bookTitle, Long userId, String userName, LocalDate borrowDate, LocalDate returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.userName = userName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

}
