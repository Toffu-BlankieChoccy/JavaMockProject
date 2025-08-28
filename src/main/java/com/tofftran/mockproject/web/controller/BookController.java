package com.tofftran.mockproject.web.controller;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    //Read all books
    @GetMapping
    public String listBooks(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(required = false) String search, Model model){
        page = Math.max(0, page); //Negative page number validation
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookService.findAllBooks(pageable);

        if (search != null && !search.isEmpty()){
            bookPage = bookService.findByTitle(search.trim(), pageable);
        } else {
            bookPage = bookService.findAllBooks(pageable);
        }

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("totalItems", bookPage.getTotalElements());
        model.addAttribute("search", search);
        return "book/list";
    }

    //Add form
    @GetMapping("/add")
    public String showAddForm(Model model){
        model.addAttribute("book", new Book());
        return "book/add";
    }

    //Handle add form submission
    @PostMapping
    public String addBook(@Valid @ModelAttribute("book") Book book, BindingResult result, Model model){
        if (result.hasErrors()){
            return "book/add";
        }
        try{
            bookService.createBook(book);
            return "redirect:/books";
        } catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "book/add";
        }
    }

    //Edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Book book = bookService.findBookById(id);
        model.addAttribute("book", book);
        return "book/edit";
    }

    //Handle edit form submission
    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id, @Valid @ModelAttribute("book") Book book, BindingResult result, Model model){
        if (result.hasErrors()){
            return "book/edit";
        }
        try{
            bookService.updateBook(id, book);
            return "redirect:/books";
        } catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "book/edit";
        }
    }

    //Delete
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, Model model){
        try{
            bookService.deleteBook(id);
            return "redirect:/books";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            Pageable pageable = PageRequest.of(0, 5);
            model.addAttribute("books", bookService.findAllBooks(pageable).getContent());
            return "book/list";
        }
    }
}