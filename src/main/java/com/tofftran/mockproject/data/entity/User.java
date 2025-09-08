package com.tofftran.mockproject.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 2, max = 50, message = "Name must be from 2 to 50 characters")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;

    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
    private String phoneNumber;

    @Size(min = 8, message = "Password length must be up to 8 characters")
    private String password;

    private Instant createdOn;

    private Instant updatedOn;

    @Enumerated(EnumType.STRING)
    private Role role; // Thêm trường role

    public enum Role {
        ADMIN,
        USER
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Borrowing> borrowings = new ArrayList<>();
}
