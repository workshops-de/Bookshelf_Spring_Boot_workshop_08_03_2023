package de.workshops.bookshelf.book;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@Validated
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() throws BookException {
        return bookService.getBooks();
    }

    @GetMapping("/{isbn}")
    public Book getSingleBook(@PathVariable @Size(min = 10, max = 14) String isbn) throws BookException {
        return bookService.getSingleBook(isbn);
    }

    @GetMapping(params = "author")
    public Book searchBookByAuthor(@RequestParam @NotBlank @Size(min = 3) String author) throws BookException {
        return bookService.searchBookByAuthor(author);
    }

    @PostMapping("/search")
    public List<Book> searchBooks(@RequestBody @Valid BookSearchRequest request) throws BookException {
        return bookService.searchBooks(request);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }
}
