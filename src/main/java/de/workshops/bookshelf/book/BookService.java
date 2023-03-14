package de.workshops.bookshelf.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> getBooks() throws BookException {
        List<Book> bookList = bookRepository.findAll();
        if (bookList.isEmpty()) {
            throw new BookException();
        }

        return bookList;
    }

    public Book getSingleBook(String isbn) throws BookException {
        return getBooks()
                .stream()
                .filter(book -> hasIsbn(book, isbn)).findFirst().orElseThrow(BookException::new);
    }

    public Book searchBookByAuthor(String author) throws BookException {
        return getBooks()
                .stream()
                .filter(book -> hasAuthor(book, author)).findFirst().orElseThrow(BookException::new);
    }

    public List<Book> searchBooks(BookSearchRequest request) throws BookException {
        List<Book> bookList = getBooks()
                .stream()
                .filter(book -> hasAuthor(book, request.author()))
                .filter(book -> hasIsbn(book, request.isbn()))
                .toList();

        if (bookList.isEmpty()) {
            throw new BookException();
        }

        return bookList;
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    private boolean hasIsbn(Book book, String isbn) {
        return isbn == null || book.getIsbn().equals(isbn);
    }

    private boolean hasAuthor(Book book, String author) {
        return author == null || book.getAuthor().contains(author);
    }
}
