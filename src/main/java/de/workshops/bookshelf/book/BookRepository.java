package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BookRepository {

    private final ObjectMapper mapper;
    private final ResourceLoader resourceLoader;

    public List<Book> getBooks() {
        try {
            final var resource = resourceLoader.getResource("classpath:books.json");
            return mapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return Collections.emptyList();
    }

    public Book createBook(Book book) {
        return book;
    }
}
