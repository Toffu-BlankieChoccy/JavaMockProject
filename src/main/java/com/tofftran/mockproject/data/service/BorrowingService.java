package com.tofftran.mockproject.data.service;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.repository.BookRepository;
import com.tofftran.mockproject.data.repository.BorrowingRepository;
import com.tofftran.mockproject.data.repository.UserRepository;
import com.tofftran.mockproject.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingService {
    private final BorrowingRepository borrowingRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    private static final int MAX_BORROW_LIMIT = 5;

    private static final int DEFAULT_BORROW_DAYS = 7;


    public BorrowingService(BorrowingRepository borrowingRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    //Create (non api)
    @Transactional
    public Borrowing createBorrowing(Borrowing borrowing) {
        // Validate book and user existence
        Book book = bookRepository.findById(borrowing.getBook().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + borrowing.getBook().getId()));
        User user = userRepository.findById(borrowing.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + borrowing.getUser().getId()));

        // Validate borrow date
        if (borrowing.getBorrowDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        }

        // Validate return date
        if (borrowing.getReturnDate() != null && borrowing.getReturnDate().isBefore(borrowing.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }

        borrowing.setBook(book);
        borrowing.setUser(user);
        return borrowingRepository.save(borrowing);
    }

    //Create (api)
    @Transactional
    public BorrowingDTO postBorrowing(BorrowingDTO borrowingDTO) {
        Book book = bookRepository.findById(borrowingDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + borrowingDTO.getBookId()));
        User user = userRepository.findById(borrowingDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + borrowingDTO.getUserId()));

        if (borrowingDTO.getBorrowDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        }
        if (borrowingDTO.getReturnDate() != null && borrowingDTO.getReturnDate().isBefore(borrowingDTO.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setUser(user);
        borrowing.setBorrowDate(borrowingDTO.getBorrowDate());
        borrowing.setReturnDate(borrowingDTO.getReturnDate());

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        return convertToDTO(savedBorrowing);
    }


    @Transactional(readOnly = true)
    public Page<BorrowingDTO> findAllBorrowings(Pageable pageable) {
        return borrowingRepository.findAllBorrowingDTOs(pageable);
    }

    //Find by book title or user's name
    public Page<BorrowingDTO> findByBookTitleOrUserName(String keyword, Pageable pageable) {
        return borrowingRepository.findByBookTitleOrUserName(keyword, pageable);
    }

    //Find by ID
    @Transactional(readOnly = true)
    public BorrowingDTO findBorrowingById(Long id) {
        return borrowingRepository.findBorrowingDTOById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing not found with id: " + id));
    }

    //Filters
    @Transactional(readOnly = true)
    public Page<BorrowingDTO> findByFilters(String keyword, String status, Pageable pageable) {
        return borrowingRepository.findByFilters(keyword, status,pageable);
    }


    @Transactional(readOnly = true)
    public Page<BorrowingDTO> findByFiltersAndUserId(String keyword, String status, Long userId, Pageable pageable){
        return borrowingRepository.findByFiltersAndUserId(keyword, status, userId, pageable);
    }

    //Update (non api)
    @Transactional
    public Borrowing updateBorrowing(Long id, BorrowingDTO borrowingDetails) {
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing not found with id: " + id));

        // Validate book and user existence
        Book book = bookRepository.findById(borrowingDetails.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + borrowingDetails.getBookId()));
        User user = userRepository.findById(borrowingDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + borrowingDetails.getUserId()));

        // Validate borrow date
        if (borrowingDetails.getBorrowDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        }

        // Validate return date if present
        if (borrowingDetails.getReturnDate() != null && borrowingDetails.getReturnDate().isBefore(borrowingDetails.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }

        // Update fields
        borrowing.setBook(book);
        borrowing.setUser(user);
        borrowing.setBorrowDate(borrowingDetails.getBorrowDate());
        borrowing.setReturnDate(borrowingDetails.getReturnDate());
        return borrowingRepository.save(borrowing);
    }

    //Update (api)
    @Transactional
    public BorrowingDTO putBorrowing(Long id, BorrowingDTO borrowingDTO) {
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing not found with id: " + id));

        Book book = bookRepository.findById(borrowingDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + borrowingDTO.getBookId()));
        User user = userRepository.findById(borrowingDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + borrowingDTO.getUserId()));

        if (borrowingDTO.getBorrowDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        }

        if (borrowingDTO.getReturnDate() != null && borrowingDTO.getReturnDate().isBefore(borrowingDTO.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }

        borrowing.setBook(book);
        borrowing.setUser(user);
        borrowing.setBorrowDate(borrowingDTO.getBorrowDate());
        borrowing.setReturnDate(borrowingDTO.getReturnDate());

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        return convertToDTO(savedBorrowing);
    }


    @Transactional
    public void deleteBorrowing(Long id) {
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing not found with id: " + id));
        borrowingRepository.deleteById(id);
    }


    @Transactional
    public Borrowing borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available");
        }

        long currentBorrowings = borrowingRepository.countByUserAndReturnDateIsNull(user);
        if (currentBorrowings >= MAX_BORROW_LIMIT) {
            throw new IllegalStateException("You have reached the maximum borrow limit");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setBook(book);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setDueDate(LocalDate.now().plusDays(DEFAULT_BORROW_DAYS)); // 7 days
        borrowingRepository.save(borrowing);

        return borrowing;
    }


    @Transactional
    public BorrowingDTO returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing not found"));

        if (borrowing.getReturnDate() != null) {
            throw new IllegalStateException("Book already returned");
        }

        borrowing.setReturnDate(LocalDate.now());
        borrowingRepository.save(borrowing);

        return new BorrowingDTO(
                borrowing.getId(),
                borrowing.getBook().getId(),
                borrowing.getBook().getTitle(),
                borrowing.getUser().getId(),
                borrowing.getUser().getName(),
                borrowing.getBorrowDate(),
                borrowing.getReturnDate(),
                borrowing.getDueDate()
        );
    }

    public String getBorrowingStatus(Borrowing borrowing) {
        if (borrowing.getReturnDate() != null) return "RETURNED";
        if (borrowing.getDueDate().isBefore(LocalDate.now()) && borrowing.getReturnDate() == null) return "OVERDUE";
        return "BORROWED";
    }

    private BorrowingDTO convertToDTO(Borrowing borrowing) {
        return new BorrowingDTO(
                borrowing.getId(),
                borrowing.getBook().getId(),
                borrowing.getBook().getTitle(),
                borrowing.getUser().getId(),
                borrowing.getUser().getName(),
                borrowing.getBorrowDate(),
                borrowing.getReturnDate(),
                borrowing.getDueDate()
        );
    }
}
