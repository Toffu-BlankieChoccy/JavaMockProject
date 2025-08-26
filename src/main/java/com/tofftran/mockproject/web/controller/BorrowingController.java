package com.tofftran.mockproject.web.controller;

import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.service.BookService;
import com.tofftran.mockproject.data.service.BorrowingService;
import com.tofftran.mockproject.data.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {
    private final BookService bookService;
    private final UserService userService;
    private final  BorrowingService borrowingService;

    public BorrowingController(BookService bookService, UserService userService, BorrowingService borrowingService) {
        this.bookService = bookService;
        this.userService = userService;
        this.borrowingService = borrowingService;
    }

    @GetMapping
    public String listBorrowings(Model model){
        model.addAttribute("borrowings", borrowingService.findAllBorrowings());
        return "borrowing/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model){
        model.addAttribute("borrowings", new Borrowing());
        model.addAttribute("books", bookService.findAllBooks());
        model.addAttribute("users", userService.findAllUsers());
        return "borrowing/add";
    }

    @PostMapping
    public String addBorrowing(@Valid @ModelAttribute("borrowing") Borrowing borrowing, BindingResult result, Model model){
        if (result.hasErrors()){
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("users", userService.findAllUsers());
            return "borrowing/add";
        }
        try{
            borrowingService.createBorrowing(borrowing);
            return "redirect:/borrowings";
        }
        catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("users", userService.findAllUsers());
            return "borrowing/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Borrowing borrowing =borrowingService.findBorrowingById(id);
        model.addAttribute("borrowings", borrowing);
        model.addAttribute("books", bookService.findAllBooks());
        model.addAttribute("users", userService.findAllUsers());
        return "borrowing/edit";
    }

    @PostMapping("/{id}")
    public String updateBorrowing(@PathVariable Long id, @Valid @ModelAttribute("Borrowing") Borrowing borrowing,BindingResult result, Model model){
        if (result.hasErrors()){
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("users", userService.findAllUsers());
            return "borrowing/edit";
        }
        try {
            borrowingService.updateBorrowing(id, borrowing);
            return "redirect:/borrowings";
        } catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("users", userService.findAllUsers());
            return "borrowing/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBorrowing(@PathVariable Long id, Model model){
        try{
            borrowingService.deleteBorrowing(id);
            return "redirect:/borrowings";
        } catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("borrowings", borrowingService.findAllBorrowings());
            return "borrowing/list";
        }
    }
}