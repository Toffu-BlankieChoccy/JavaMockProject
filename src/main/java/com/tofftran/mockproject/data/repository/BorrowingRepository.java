package com.tofftran.mockproject.data.repository;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Page<Borrowing> findByBookAndReturnDateIsNull(Book book, Pageable pageable);

    long countByUserAndReturnDateIsNull(User user); //Borrowing book count by user

    List<Borrowing> findByUser(User user);

    @Query("SELECT new com.tofftran.mockproject.data.dto.BorrowingDTO(b.id, b.book.id, b.book.title, b.user.id, b.user.name, b.borrowDate, b.returnDate, b.dueDate) " +
            "FROM Borrowing b JOIN b.book JOIN b.user " +
            "WHERE b.user.id = :userId " +
            "AND ((:keyword IS NULL OR :keyword = '' OR LOWER(b.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "AND ((:status IS NULL OR :status = '' OR " +
            "    (:status = 'RETURNED' AND b.returnDate IS NOT NULL) OR " +
            "    (:status = 'NOT_RETURNED' AND b.returnDate IS NULL) OR " +
            "    (:status = 'OVERDUE' AND b.returnDate IS NULL AND b.dueDate < CURRENT_DATE)))")
    Page<BorrowingDTO> findByFiltersAndUserId(@Param("keyword") String keyword, @Param("status") String status, @Param("userId") Long userId, Pageable pageable);


    @Query("SELECT new com.tofftran.mockproject.data.dto.BorrowingDTO(b.id, b.book.id, b.book.title, b.user.id, b.user.name, b.borrowDate, b.returnDate, b.dueDate) " + "FROM Borrowing b JOIN b.book JOIN b.user " + "WHERE ((:keyword IS NULL OR :keyword = '' OR LOWER(b.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " + "OR LOWER(b.user.name) LIKE LOWER(CONCAT('%', :keyword, '%')))) " + "AND ((:status IS NULL OR :status = '' OR " + "    (:status = 'RETURNED' AND b.returnDate IS NOT NULL) OR " + "    (:status = 'NOT_RETURNED' AND b.returnDate IS NULL) OR " + "    (:status = 'OVERDUE' AND b.returnDate IS NULL AND b.dueDate < CURRENT_DATE)))")
    Page<BorrowingDTO> findByFilters(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);

    @Query("SELECT new com.tofftran.mockproject.data.dto.BorrowingDTO(b.id, b.book.id, b.book.title, b.user.id, b.user.name, b.borrowDate, b.returnDate, b.dueDate) " + "FROM Borrowing b JOIN b.book JOIN b.user " + "WHERE LOWER(b.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " + "OR LOWER(b.user.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<BorrowingDTO> findByBookTitleOrUserName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.tofftran.mockproject.data.dto.BorrowingDTO(b.id, b.book.id, b.book.title, b.user.id, b.user.name, b.borrowDate, b.returnDate, b.dueDate) " + "FROM Borrowing b JOIN b.book JOIN b.user")
    Page<BorrowingDTO> findAllBorrowingDTOs(Pageable pageable);

    @Query("SELECT new com.tofftran.mockproject.data.dto.BorrowingDTO(b.id, b.book.id, b.book.title, b.user.id, b.user.name, b.borrowDate, b.returnDate, b.dueDate) " + "FROM Borrowing b JOIN b.book JOIN b.user WHERE b.id = :id")
    Optional<BorrowingDTO> findBorrowingDTOById(@Param("id") Long id);
}
