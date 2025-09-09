package com.tofftran.mockproject.web.controller.api;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingApiController {
    @Autowired
    private BorrowingService borrowingService;

    @GetMapping
    public ResponseEntity<Page<BorrowingDTO>> getAllBorrowings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<BorrowingDTO> borrowings = borrowingService.findByFilters(keyword, startDate, endDate, pageable);
        return ResponseEntity.ok(borrowings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowingDTO> getBorrowingById(@PathVariable Long id) {
        BorrowingDTO borrowing = borrowingService.findBorrowingById(id);
        if (borrowing != null) {
            return ResponseEntity.ok(borrowing);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowingDTO> returnBook(@PathVariable Long id) {
        try {
            BorrowingDTO borrowing = borrowingService.returnBook(id);
            return ResponseEntity.ok(borrowing);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestParam Long userId,
                                        @RequestParam Long bookId) {
        try {

            System.out.println("Borrowing book - User ID: " + userId + ", Book ID: " + bookId); // Debug log
            Borrowing borrowing = borrowingService.borrowBook(userId, bookId);
            BorrowingDTO borrowingDTO = new BorrowingDTO(
                    borrowing.getId(),
                    borrowing.getBook().getId(),
                    borrowing.getBook().getTitle(),
                    borrowing.getUser().getId(),
                    borrowing.getUser().getName(),
                    borrowing.getBorrowDate(),
                    borrowing.getReturnDate(),
                    borrowing.getDueDate()
            );
            return ResponseEntity.status(201).body(borrowingDTO);
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BorrowingDTO> deleteBorrowing(@PathVariable Long id) {
        borrowingService.deleteBorrowing(id);
        return ResponseEntity.noContent().build();
    }
}
