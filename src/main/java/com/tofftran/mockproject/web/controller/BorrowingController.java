package com.tofftran.mockproject.web.controller;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.service.BookService;
import com.tofftran.mockproject.data.service.BorrowingService;
import com.tofftran.mockproject.data.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {
    private final BookService bookService;
    private final UserService userService;
    private final BorrowingService borrowingService;

    public BorrowingController(BookService bookService, UserService userService, BorrowingService borrowingService) {
        this.bookService = bookService;
        this.userService = userService;
        this.borrowingService = borrowingService;
    }

    // Read all borrowings
    @GetMapping
    @Transactional(readOnly = true)
    public String listBorrowings(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(required = false) String search ,Model model) {
        page = Math.max(0, page); //Negative page number validation
        Pageable pageable = PageRequest.of(page, size);
        Page<BorrowingDTO> borrowingPage = borrowingService.findAllBorrowings(pageable);

        if (search != null && !search.isEmpty()){
            borrowingPage = borrowingService.findByBookTitleOrUserName(search, pageable);
        } else borrowingPage = borrowingService.findAllBorrowings(pageable);

        model.addAttribute("borrowings", borrowingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", borrowingPage.getTotalPages());
        model.addAttribute("totalItems", borrowingPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("search", search);

        return "borrowing/list";
    }

    // Add form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("borrowing", new BorrowingDTO());
        model.addAttribute("books", bookService.findAllBooks());
        model.addAttribute("users", userService.findAllUsers());
        return "borrowing/add";
    }

    // Handle add form submission
    @PostMapping
    public String addBorrowing(@Valid @ModelAttribute("borrowing") BorrowingDTO borrowingDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("users", userService.findAllUsers());
            return "borrowing/add";
        }
        try {
            // Convert DTO to entity
            Borrowing borrowing = new Borrowing();
            borrowing.setId(borrowingDTO.getId());
            Book book = new Book();
            book.setId(borrowingDTO.getBookId());
            borrowing.setBook(book);
            User user = new User();
            user.setId(borrowingDTO.getUserId());
            borrowing.setUser(user);
            borrowing.setBorrowDate(borrowingDTO.getBorrowDate());
            borrowing.setReturnDate(borrowingDTO.getReturnDate());
            borrowingService.createBorrowing(borrowing);
            return "redirect:/borrowings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("users", userService.findAllUsers());
            return "borrowing/add";
        }
    }

    // Edit form
    @GetMapping("/edit/{id}")
    @Transactional(readOnly = true)
    public String showEditForm(@PathVariable Long id, Model model) {
        BorrowingDTO borrowing = borrowingService.findBorrowingById(id);
        model.addAttribute("borrowing", borrowing);
        model.addAttribute("books", bookService.findAllBooks());
        model.addAttribute("users", userService.findAllUsers());
        return "borrowing/edit";
    }

    // Handle edit form submission
    @PostMapping("/{id}")
    public String updateBorrowing(@PathVariable Long id, @Valid @ModelAttribute("borrowing") BorrowingDTO borrowingDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("users", userService.findAllUsers());
            return "borrowing/edit";
        }
        try {
            borrowingService.updateBorrowing(id, borrowingDTO);
            return "redirect:/borrowings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("users", userService.findAllUsers());
            return "borrowing/edit";
        }
    }

    // Delete
    @GetMapping("/delete/{id}")
    public String deleteBorrowing(@PathVariable Long id, Model model) {
        try {
            borrowingService.deleteBorrowing(id);
            return "redirect:/borrowings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            Pageable pageable = PageRequest.of(0, 5); // Default page and size
            model.addAttribute("borrowings", borrowingService.findAllBorrowings(pageable));
            return "borrowing/list";
        }
    }
}