package com.tofftran.mockproject;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.repository.BookRepository;
import com.tofftran.mockproject.data.repository.BorrowingRepository;
import com.tofftran.mockproject.data.repository.UserRepository;
import com.tofftran.mockproject.data.service.BookService;
import com.tofftran.mockproject.exception.ConflictException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;
    private BookService bookService;

    public DataInitializer(BookRepository bookRepository, UserRepository userRepository, BorrowingRepository borrowingRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.borrowingRepository = borrowingRepository;
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            // Test valid data
//            try {
//                Book book = new Book(null, "The Catcher in the Rye", "978-031676948", LocalDate.of(1951, 7, 16), 8.99, null);
//                bookRepository.save(book);
//                System.out.println("Saved valid book: " + book);
//
//                User user = new User(null, "Alice Johnson", "alice@example.com", "555123457", null);
//                userRepository.save(user);
//                System.out.println("Saved valid user: " + user);
//
//                Borrowing borrowing = new Borrowing(null, book, user, LocalDate.now(), null);
//                borrowingRepository.save(borrowing);
//                System.out.println("Saved valid borrowing: " + borrowing);
//            } catch (ConstraintViolationException e) {
//                System.out.println("Validation error: " + e.getMessage());
//            }
//
//            // Test invalid data
//            try {
//                Book invalidBook = new Book(null, "", "978-0316769488", null, -5.0, null); // Invalid title, publishedDate, price
//                bookRepository.save(invalidBook);
//            } catch (ConstraintViolationException e) {
//                System.out.println("Expected validation error for book: " + e.getMessage());
//            }
//
//            try {
//                User invalidUser = new User(null, "A", "invalid-email", null, null); // Invalid name, email
//                userRepository.save(invalidUser);
//            } catch (ConstraintViolationException e) {
//                System.out.println("Expected validation error for user: " + e.getMessage());
//            }
//
//            // Test delete book
//            try {
//                bookService.deleteBook(1L); // Book đang được mượn (returnDate=null)
//            } catch (ConflictException e) {
//                System.out.println("Expected conflict error: " + e.getMessage());
//            }
        };
    }

    ;
}

