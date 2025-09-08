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
import java.util.Optional;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingApiController {
//    private final BorrowingService borrowingService;
//
//    public BorrowingApiController(BorrowingService borrowingService) {
//        this.borrowingService = borrowingService;
//    }
//
//    @GetMapping
//    public ResponseEntity<Page<BorrowingDTO>> getAllBorrowings(Pageable pageable){
//        Page<BorrowingDTO> borrowingDTOS = borrowingService.findAllBorrowings(pageable);
//        return ResponseEntity.ok(borrowingDTOS);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<BorrowingDTO> getBorrowingById(@PathVariable Long id){
//        BorrowingDTO borrowingDTO = borrowingService.findBorrowingById(id);
//        return ResponseEntity.ok(borrowingDTO);
//    }
//
//    @PostMapping
//    public ResponseEntity<BorrowingDTO> createBorrowing(@RequestBody BorrowingDTO borrowingDTODetails){
//        BorrowingDTO borrowingDTO = borrowingService.postBorrowing(borrowingDTODetails);
//        return ResponseEntity.status(201).body(borrowingDTO);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<BorrowingDTO> updateBorrowing(@PathVariable Long id, @RequestBody BorrowingDTO borrowingDetails){
//        BorrowingDTO borrowingDTO = borrowingService.putBorrowing(id, borrowingDetails);
//        return ResponseEntity.status(201).body(borrowingDTO);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<BorrowingDTO> deleteBorrowing(@PathVariable Long id){
//        borrowingService.deleteBorrowing(id);
//        return ResponseEntity.noContent().build();
//    }

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

    @PostMapping
    public ResponseEntity<Borrowing> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            Borrowing borrowing = borrowingService.borrowBook(userId, bookId);
            return ResponseEntity.ok(borrowing);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Borrowing> returnBook(@PathVariable Long id) {
        try {
            Borrowing borrowing = borrowingService.returnBook(id);
            return ResponseEntity.ok(borrowing);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
