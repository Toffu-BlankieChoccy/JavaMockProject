package com.tofftran.mockproject.data.service;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.User;
import com.tofftran.mockproject.data.repository.UserRepository;
import com.tofftran.mockproject.exception.ConflictException;
import com.tofftran.mockproject.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user){
        if (userRepository.existsByEmail(user.getEmail())){
            throw new ConflictException("Email " + user.getEmail() + " is already exists");
        }
        return userRepository.save(user);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public Page<User> findAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public Page<User> findByNameOrEmail(String keyword,Pageable pageable){
        return userRepository.findByNameOrEmail(keyword, pageable);
    }

    public User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User updateUser(Long id, User userDetails){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getEmail().equals(userDetails.getEmail()) && userRepository.existsByEmail(userDetails.getEmail())){
            throw new ConflictException("Email " + userDetails.getEmail() + " already exists");
        }

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
    }
}
