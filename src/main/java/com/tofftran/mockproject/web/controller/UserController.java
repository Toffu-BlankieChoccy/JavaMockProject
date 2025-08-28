package com.tofftran.mockproject.web.controller;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Read all users
    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(required = false) String search, Model model){
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.findAllUsers(pageable);

        if (search != null && !search.isEmpty()){
            userPage = userService.findByNameOrEmail(search, pageable);
        } else {
            userPage = userService.findAllUsers(pageable);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("search", search);
        return "user/list";
    }

    //Add form
    @GetMapping("/add")
    public String showAddForm(Model model){
        model.addAttribute("user", new User());
        return "user/add";
    }

    //Handle add form submission
    @PostMapping
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        if (result.hasErrors()){
            return "user/add";
        }
        try{
            userService.createUser(user);
            return "redirect:/users";
        } catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "user/add";
        }
    }

    //Edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "user/edit";
    }

    //Handle edit form submission
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        if (result.hasErrors()){
            return "user/edit";
        }
        try{
            userService.updateUser(id, user);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, Model model){
        try{
            userService.deleteUser(id);
            return "redirect:/users";
        } catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            Pageable pageable = PageRequest.of(0, 5);
            model.addAttribute("users", userService.findAllUsers());
            return "user/list";
        }
    }


}