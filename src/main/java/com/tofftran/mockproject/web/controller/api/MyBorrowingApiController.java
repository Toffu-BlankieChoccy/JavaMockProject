package com.tofftran.mockproject.web.controller.api;

import com.tofftran.mockproject.data.dto.BorrowingDTO;
import com.tofftran.mockproject.data.repository.UserRepository;
import com.tofftran.mockproject.data.service.BorrowingService;
import com.tofftran.mockproject.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/my-borrowings")
public class MyBorrowingApiController {

    @Autowired
    private BorrowingService borrowingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> listMyBorrowingsAjax(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            Authentication authentication) {

        Long currentUserId = getCurrentUserId(authentication);

        page = Math.max(0, page);

        Sort sortOrder = Sort.unsorted();
        if (sort != null && !sort.isEmpty()){
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2){
                sortOrder = Sort.by(sortParams[0]).ascending();
                if ("desc".equalsIgnoreCase(sortParams[1])){
                    sortOrder = Sort.by(sortParams[0]).descending();
                }
            }
        } else {
            sortOrder = Sort.by("id").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<BorrowingDTO> borrowingPage;

        borrowingPage = borrowingService.findByFiltersAndUserId(search,status,currentUserId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("borrowings", borrowingPage.getContent());
        response.put("currentPage", page);
        response.put("totalPages", borrowingPage.getTotalPages());
        response.put("totalItems", borrowingPage.getTotalElements());
        response.put("size", size);
        response.put("search", search);
        response.put("status", status);
        response.put("sort", sort);

        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            return userService.findByEmail(email).get().getId();
        }
        throw new SecurityException("User not authenticated");
    }
}
