package de.workshops.bookshelf.book;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Valid
public class Book {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String author;

    @NotBlank
    @Size(min = 10, max = 13)
    private String isbn;
}
