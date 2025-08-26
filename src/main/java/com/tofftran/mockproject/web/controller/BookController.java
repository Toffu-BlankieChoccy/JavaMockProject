package com.tofftran.mockproject.web.controller;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.service.BookService;
import jakarta.validation.Valid;
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
    public String listBooks(Model model){
        model.addAttribute("books", bookService.findAllBooks());
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
            model.addAttribute("books", bookService.findAllBooks());
            return "book/list";
        }
    }
}
