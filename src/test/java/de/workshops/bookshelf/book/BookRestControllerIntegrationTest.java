package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class BookRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRestController bookRestController;

    @Autowired
    private BookRepository bookRepository;

    @TestConfiguration
    static class JacksonTestConfiguration {

        @Bean
        public ObjectMapper mapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

            return mapper;
        }
    }

    @Test
    @WithMockUser
    void getAllBooks() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/book"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", is("Clean Code")))
                .andReturn();
        String jsonPayload = mvcResult.getResponse().getContentAsString();

        Book[] books = objectMapper.readValue(jsonPayload, Book[].class);
        assertEquals(3, books.length);
        assertEquals("Clean Code", books[1].getTitle());
    }

    @Test
    @WithMockUser
    void testWithRestAssuredMockMvc() {
        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders
                        .standaloneSetup(bookRestController)
                        .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
        );
        RestAssuredMockMvc.
                given().
                log().all().
                when().
                get("/book").
                then().
                log().all().
                statusCode(200).
                body("author[0]", equalTo("Erich Gamma"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createBook() throws JsonProcessingException, UnsupportedEncodingException {
        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders
                        .standaloneSetup(bookRestController)
                        .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
        );
        RestAssuredMockMvc.postProcessors(csrf().asHeader());

        Book book = new Book();
        book.setAuthor("Eric Evans");
        book.setTitle("Domain-Driven Design: Tackling Complexity in the Heart of Software");
        book.setIsbn("978-0321125217");
        book.setDescription("This is not a book about specific technologies. It offers readers a systematic approach to domain-driven design, presenting an extensive set of design best practices, experience-based techniques, and fundamental principles that facilitate the development of software projects facing complex domains.");

        MockMvcResponse mockMvcResponse = RestAssuredMockMvc.
                given().
                log().all().
                body(book).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                when().
                post("/book").
                andReturn();
        mockMvcResponse.
                then().
                log().all().
                statusCode(200).
                body("author", equalTo("Eric Evans"));

        String jsonPayload = mockMvcResponse.mvcResult().getResponse().getContentAsString();
        Book newBook = objectMapper.readValue(jsonPayload, Book.class);
        bookRepository.delete(newBook);
    }
}
