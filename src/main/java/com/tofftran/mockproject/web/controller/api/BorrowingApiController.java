package com.tofftran.mockproject.web.controller.api;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingApiController {
    @Autowired
    private BorrowingService borrowingService;

    @GetMapping // New endpoint for AJAX calls
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> listBorrowingsAjax(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {

        // Your existing logic is perfect, just wrap in ResponseEntity
        page = Math.max(0, page);

        Sort sortOrder = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                sortOrder = Sort.by(sortParams[0]).ascending();
                if ("desc".equalsIgnoreCase(sortParams[1])) {
                    sortOrder = Sort.by(sortParams[0]).descending();
                }
            }
        } else {
            sortOrder = Sort.by("id").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<BorrowingDTO> borrowingPage;

        if ((search != null && !search.trim().isEmpty()) || (status != null && !status.isEmpty())) {
            borrowingPage = borrowingService.findByFilters(search, status, pageable);
        } else {
            borrowingPage = borrowingService.findAllBorrowings(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("borrowings", borrowingPage.getContent());
        response.put("currentPage", page);
        response.put("totalPages", borrowingPage.getTotalPages());
        response.put("totalItems", borrowingPage.getTotalElements());
        response.put("size", size);
        response.put("search", search);
        response.put("status", status);
        response.put("sort", sort);

        // Return ResponseEntity for consistent API responses
        return ResponseEntity.ok(response);
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


//    @PutMapping("/{id}/return")
//    public ResponseEntity<BorrowingDTO> returnBook(@PathVariable Long id) {
//        try {
//            BorrowingDTO borrowing = borrowingService.returnBook(id);
//            return ResponseEntity.ok(borrowing);
//        } catch (IllegalStateException e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Map<String, Object>> returnBook(@PathVariable Long id) {
        try {
            borrowingService.returnBook(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Book returned successfully");
            return ResponseEntity.ok(response);  // Trả về Map có success=true
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to return book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
