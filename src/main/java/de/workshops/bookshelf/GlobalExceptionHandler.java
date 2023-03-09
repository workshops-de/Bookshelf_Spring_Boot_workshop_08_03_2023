package de.workshops.bookshelf;

import de.workshops.bookshelf.book.BookException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BookException.class)
    ProblemDetail handleBookException(BookException e) {
       ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
       problemDetail.setTitle("Book Not Found");
       problemDetail.setType(URI.create("http://localhost:8080/book_exception.html"));
       problemDetail.setProperty("timestamp", Instant.now());

       return problemDetail;
   }
}
