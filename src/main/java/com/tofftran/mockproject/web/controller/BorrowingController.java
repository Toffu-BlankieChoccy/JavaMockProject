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
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String sort, Model model) {
//        page = Math.max(0, page); //Negative page number validation
//
//        Sort sortOrder = Sort.unsorted();
//        if (sort != null && !sort.isEmpty()) {
//            String[] sortParams = sort.split(",");
//            if (sortParams.length == 2) {
//                sortOrder = Sort.by(sortParams[0]).ascending();
//                if ("desc".equalsIgnoreCase(sortParams[1])) {
//                    sortOrder = Sort.by(sortParams[0]).descending();
//                }
//            }
//        } else {
//            sortOrder = Sort.by("id").descending(); // Default sort
//        }
//
//        Pageable pageable = PageRequest.of(page, size, sortOrder);
//        Page<BorrowingDTO> borrowingPage = borrowingService.findAllBorrowings(pageable);
//
//
//        if ((search != null && !search.trim().isEmpty()) || (status != null && !status.isEmpty())) {
//            borrowingPage = borrowingService.findByFilters(search, status, pageable);
//        }
//
//
//            // Check if page is out of scope
//        if (page > borrowingPage.getTotalPages() - 1 && borrowingPage.getTotalPages() > 0) {
//            model.addAttribute("errorMessage", "Page index out of range. Redirected to last page.");
//            return "redirect:/borrowings?page=" + (borrowingPage.getTotalPages() - 1) + "&size=" + size +
//                    (search != null ? "&search=" + search : "") +
//                    (status != null ? "&status=" + status : "");
//        }

//        model.addAttribute("borrowings", borrowingPage.getContent());
//        model.addAttribute("totalPages", borrowingPage.getTotalPages());
//        model.addAttribute("totalItems", borrowingPage.getTotalElements());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("size", size);
//        model.addAttribute("search", search);
//        model.addAttribute("status", status);
//        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", Math.max(0, page));
        model.addAttribute("size", size);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("status", status != null ? status : "");
        model.addAttribute("sort", sort != null ? sort : "");
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