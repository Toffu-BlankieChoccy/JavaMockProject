package com.tofftran.mockproject.web.controller.api;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingApiController {
    @Autowired
    private BorrowingService borrowingService;

    // Endpoint cho cả view và AJAX
    @GetMapping
    @ResponseBody
    public String listBorrowings(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String sort,
                                 Model model,
                                 @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
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
        Page<BorrowingDTO> borrowingPage = borrowingService.findAllBorrowings(pageable);

        if ((search != null && !search.trim().isEmpty()) || (status != null && !status.isEmpty())) {
            borrowingPage = borrowingService.findByFilters(search, status, pageable);
        }

        // Nếu không phải AJAX, trả về view
        model.addAttribute("borrowings", borrowingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", borrowingPage.getTotalPages());
        model.addAttribute("totalItems", borrowingPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        return "borrowing/apiListTest"; // Trả về template apiListTest.html
    }

    // Endpoint riêng cho AJAX (tuỳ chọn)
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBorrowings(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size,
                                                             @RequestParam(required = false) String search,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String sort) {
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
        Page<BorrowingDTO> borrowingPage = borrowingService.findAllBorrowings(pageable);

        if ((search != null && !search.trim().isEmpty()) || (status != null && !status.isEmpty())) {
            borrowingPage = borrowingService.findByFilters(search, status, pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", borrowingPage.getContent());
        response.put("currentPage", borrowingPage.getNumber());
        response.put("totalPages", borrowingPage.getTotalPages());
        response.put("totalItems", borrowingPage.getTotalElements());
        response.put("size", borrowingPage.getSize());

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
