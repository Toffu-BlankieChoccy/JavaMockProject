package com.tofftran.mockproject.data.service;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.repository.BookRepository;
import com.tofftran.mockproject.data.repository.BorrowingRepository;
import com.tofftran.mockproject.exception.ConflictException;
import com.tofftran.mockproject.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;

    public BookService(BookRepository bookRepository, BorrowingRepository borrowingRepository) {
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
    }

    public Book createBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new ConflictException("ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public Page<Book> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
    }

    public Page<Book> findByTitle(String title, Pageable pageable){
        return bookRepository.findByTitle(title, pageable);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (!book.getIsbn().equals(bookDetails.getIsbn()) && bookRepository.existsByIsbn(bookDetails.getIsbn())) {
            throw new ConflictException("ISBN " + bookDetails.getIsbn() + " already exists");
        }

        book.setTitle(bookDetails.getTitle());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublishedDate(bookDetails.getPublishedDate());
        book.setPrice(bookDetails.getPrice());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
    Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        // Check if book is currently borrowed
        if (!borrowingRepository.findByBookAndReturnDateIsNull(book, Pageable.unpaged()).isEmpty()) {
            throw new ConflictException("Cannot delete book with id " + id + " because it is currently borrowed");
        }
        bookRepository.deleteById(id);
}
}
