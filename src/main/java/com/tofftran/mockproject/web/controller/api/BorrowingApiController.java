package com.tofftran.mockproject.web.controller.api;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.service.BorrowingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingApiController {
    private final BorrowingService borrowingService;

    public BorrowingApiController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @GetMapping
    public ResponseEntity<Page<BorrowingDTO>> getAllBorrowings(Pageable pageable){
        Page<BorrowingDTO> borrowingDTOS = borrowingService.findAllBorrowings(pageable);
        return ResponseEntity.ok(borrowingDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowingDTO> getBorrowingById(@PathVariable Long id){
        BorrowingDTO borrowingDTO = borrowingService.findBorrowingById(id);
        return ResponseEntity.ok(borrowingDTO);
    }

    @PostMapping
    public ResponseEntity<BorrowingDTO> createBorrowing(@RequestBody BorrowingDTO borrowingDTODetails){
        BorrowingDTO borrowingDTO = borrowingService.postBorrowing(borrowingDTODetails);
        return ResponseEntity.status(201).body(borrowingDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowingDTO> updateBorrowing(@PathVariable Long id, @RequestBody BorrowingDTO borrowingDetails){
        BorrowingDTO borrowingDTO = borrowingService.putBorrowing(id, borrowingDetails);
        return ResponseEntity.status(201).body(borrowingDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BorrowingDTO> deleteBorrowing(@PathVariable Long id){
        borrowingService.deleteBorrowing(id);
        return ResponseEntity.noContent().build();
    }
}
