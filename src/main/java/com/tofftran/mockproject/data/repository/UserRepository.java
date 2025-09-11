package com.tofftran.mockproject.data.repository;

import com.tofftran.mockproject.data.entity.Book;
import com.tofftran.mockproject.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> findByNameOrEmail(String keyword, Pageable pageable);


//    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Optional<User> findByEmail(String email);


    //To find principle user id --- for authorization
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findPreciseEmail(@Param("email") String email);

    boolean existsByEmail(String email);
}
