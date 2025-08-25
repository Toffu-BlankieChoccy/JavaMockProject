package com.tofftran.mockproject.data.service;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.repository.BookRepository;
import com.tofftran.mockproject.data.repository.BorrowingRepository;
import com.tofftran.mockproject.data.repository.UserRepository;
import com.tofftran.mockproject.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingService {
    private final BorrowingRepository borrowingRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    public BorrowingService(BorrowingRepository borrowingRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Borrowing createBorrowing(Borrowing borrowing){
        //Validate book and user existence
        Book book = bookRepository.findById(borrowing.getBook().getId())
                .orElseThrow(()-> new ResourceNotFoundException("Book not found with id: " + borrowing.getBook().getId()));
        User user = userRepository.findById(borrowing.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + borrowing.getUser().getId()));

        //Validate borrow date
        if (borrowing.getBorrowDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        }

        //Validate return date
        if (borrowing.getReturnDate() != null && borrowing.getReturnDate().isBefore(borrowing.getBorrowDate())){
            throw new IllegalArgumentException("Return day cannot be before borrow date");
        }

        borrowing.setBook(book);
        borrowing.setUser(user);
        return borrowingRepository.save(borrowing);
    }

    public List<Borrowing> findAllBorrowings(){
        return borrowingRepository.findAll();
    }

    public Borrowing findBorrowingById (Long id){
        return borrowingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing is not found with id: " + id));
    }

    public Borrowing updateBorrowing(Long id, Borrowing borrowingDetails){
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Borrowing is not found with id: " + id));

        //Validate book and user existence
        Book book = bookRepository.findById(borrowingDetails.getBook().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + borrowingDetails.getBook().getId()));
        User user = userRepository.findById(borrowingDetails.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + borrowingDetails.getUser().getId()));

        //Validate borrow date
        if (borrowingDetails.getBorrowDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        };

        // Validate return date if present
        if (borrowingDetails.getReturnDate() != null && borrowingDetails.getReturnDate().isBefore(borrowingDetails.getBorrowDate())) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }

        borrowing.setBook(book);
        borrowing.setUser(user);
        borrowing.setBorrowDate(borrowingDetails.getBorrowDate());
        borrowing.setReturnDate(borrowingDetails.getReturnDate());
        return borrowingRepository.save(borrowing);
    }

    public void deleteBorrowing(Long id){
        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing not found with id: " + id));
        borrowingRepository.deleteById(id);
    }
}
