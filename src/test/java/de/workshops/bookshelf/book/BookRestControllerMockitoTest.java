package de.workshops.bookshelf.book;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(BookRestController.class)
class BookRestControllerMockitoTest {

    @Autowired
    private BookRestController bookRestController;

    @MockBean
    private BookService bookService;

    @Test
    void getAllBooks() throws BookException {
        Mockito.when(bookService.getBooks()).thenReturn(Collections.emptyList());

        assertEquals(0, bookRestController.getAllBooks().size());
    }
}
