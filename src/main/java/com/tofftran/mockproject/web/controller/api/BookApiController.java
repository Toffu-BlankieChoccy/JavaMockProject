package com.tofftran.mockproject.web.controller.api;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookApiController {
    private final BookService bookService;

    public BookApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(Pageable pageable){
        Page<Book> books = bookService.findAllBooks(pageable);
        return  ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id){
        Book book = bookService.findBookById(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book bookDetails){
        Book book = bookService.createBook(bookDetails);
        return ResponseEntity.status(201).body(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody Book bookDetails){
        Book book = bookService.updateBook(id, bookDetails);
        return ResponseEntity.status(201).body(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook (@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
