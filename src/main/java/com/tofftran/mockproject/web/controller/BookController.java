package com.tofftran.mockproject.web.controller;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.repository.UserRepository;
import com.tofftran.mockproject.data.service.BookService;
import com.tofftran.mockproject.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/books")
public class BookController {
    @Autowired
    private final BookService bookService;

    @Autowired
    private UserRepository userRepository;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    //Read all books
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String listBooks(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String sort,
                            Model model,
                            Principal principal){
        page = Math.max(0, page); //Negative page number validation

        Sort sortOrder = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                sortOrder = Sort.by(sortParams[0]).ascending();
                if ("desc".equalsIgnoreCase(sortParams[1])) {
                    sortOrder = Sort.by(sortParams[0]).descending();
                }
            }
        } else {
            sortOrder = Sort.by("id").descending(); // Default sort
        }


        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Book> bookPage = bookService.findAllBooks(pageable);

        if (search != null && !search.isEmpty()){
            bookPage = bookService.findByTitle(search.trim(), pageable);
        } else {
            bookPage = bookService.findAllBooks(pageable);
        }

        // Check if page is out of scope
        if (page > bookPage.getTotalPages() - 1 && bookPage.getTotalPages() > 0) {
            // Redirect to the last page
            model.addAttribute("errorMessage", "Page index out of range. Redirected to last page.");
            return "redirect:/users?page=" + (bookPage.getTotalPages() - 1) + "&size=" + size + (search != null ? "&search=" + search : "");
        }

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("totalItems", bookPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        // Thêm userId vào model nếu user đã đăng nhập
        if (principal != null) {
            String email = principal.getName();
            User user = userRepository.findPreciseEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            model.addAttribute("userId", user.getId());
        }


        return "book/list";
    }

    //Add form
    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showAddForm(Model model){
        model.addAttribute("book", new Book());
        return "book/add";
    }

    //Handle add form submission
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model){
        Book book = bookService.findBookById(id);
        model.addAttribute("book", book);
        return "book/edit";
    }

    //Handle edit form submission
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN')")
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