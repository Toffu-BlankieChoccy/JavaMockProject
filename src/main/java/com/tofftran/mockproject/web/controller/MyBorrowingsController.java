package com.tofftran.mockproject.web.controller;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.entity.Borrowing;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.repository.UserRepository;
import com.tofftran.mockproject.data.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Controller
@RequestMapping("/myBorrowings")
public class MyBorrowingsController {
    @Autowired
    private BorrowingService borrowingService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String myBorrowings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String status,
            Model model) {
        page = Math.max(0, page); //Negative page number validation

        // Get userId from userDetails
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

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
        Page<BorrowingDTO> borrowingPage = borrowingService.findByUserWithFilters(user.getId(), search, status, pageable);

        // Check if page is out of scope
        if (page > borrowingPage.getTotalPages() - 1 && borrowingPage.getTotalPages() > 0) {
            model.addAttribute("errorMessage", "Page index out of range. Redirected to last page.");
            return "redirect:/myBorrowings?page=" + (borrowingPage.getTotalPages() - 1) + "&size=" + size +
                    (search != null ? "&search=" + search : "") +
                    (status != null ? "&status=" + status : "");
        }

        model.addAttribute("borrowings", borrowingPage.getContent());
        model.addAttribute("currentPage", borrowingPage.getNumber());
        model.addAttribute("totalPages", borrowingPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("totalItems", borrowingPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("status", status);

        return "myBorrowings/list";
    }
}
