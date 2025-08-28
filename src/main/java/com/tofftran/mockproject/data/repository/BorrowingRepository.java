package com.tofftran.mockproject.data.repository;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.Borrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Page<Borrowing> findByBookAndReturnDateIsNull(Book book, Pageable pageable);

    @Query("SELECT new com.tofftran.mockproject.data.dto.BorrowingDTO(b.id, b.book.id, b.book.title, b.user.id, b.user.name, b.borrowDate, b.returnDate) " +
            "FROM Borrowing b JOIN b.book JOIN b.user")
    Page<BorrowingDTO> findAllBorrowingDTOs(Pageable pageable);

    @Query("SELECT new com.tofftran.mockproject.data.dto.BorrowingDTO(b.id, b.book.id, b.book.title, b.user.id, b.user.name, b.borrowDate, b.returnDate) " +
            "FROM Borrowing b JOIN b.book JOIN b.user WHERE b.id = :id")
    Optional<BorrowingDTO> findBorrowingDTOById(@Param("id") Long id);
}
