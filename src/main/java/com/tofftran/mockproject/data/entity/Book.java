package com.tofftran.mockproject.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title cannot be null")
    @Size(min = 2, max = 100, message = "Title must between 2 - 100 characters")
    private String title;

    @NotNull(message = "ISBN cannot be null")
    @Size(min = 10, max = 13, message = "ISBN must between 10 to 13 characters")
    @Column(unique = true)
    private String isbn;

    @NotNull(message = "Published data cannot be null")
    private LocalDate publishedDate;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be a positive number")
    private double Price;

    //@OneToMany(mappedBy = "templates/book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Borrowing> borrowings = new ArrayList<>();

}
