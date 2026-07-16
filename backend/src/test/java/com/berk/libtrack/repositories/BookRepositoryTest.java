package com.berk.libtrack.repositories;

import com.berk.libtrack.TestcontainersConfig;
import com.berk.libtrack.domain.entities.BookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private long nextIsbn = 1000L;

    @Test
    void search_matchesByTitle_partialAndCaseInsensitive() {
        bookRepository.save(book("The Myth of Sisyphus", "Albert Camus", "philosophy"));
        bookRepository.save(book("Roadside Picnic", "Arkady Strugatsky", "sci-fi"));
        bookRepository.save(book("The Iliad", "Homer", "epic"));

        assertThat(search("myth").getContent())
                .extracting(BookEntity::getTitle)
                .containsExactly("The Myth of Sisyphus");
    }

    @Test
    void search_matchesByAuthor_caseInsensitive() {
        bookRepository.save(book("The Myth of Sisyphus", "Albert Camus", "philosophy"));
        bookRepository.save(book("The Iliad", "Homer", "epic"));

        assertThat(search("CAMUS").getContent())
                .extracting(BookEntity::getAuthor)
                .containsExactly("Albert Camus");
    }

    @Test
    void search_matchesByGenre_partial() {
        bookRepository.save(book("Roadside Picnic", "Arkady Strugatsky", "sci-fi"));
        bookRepository.save(book("The Iliad", "Homer", "epic"));

        assertThat(search("sci").getContent())
                .extracting(BookEntity::getGenre)
                .containsExactly("sci-fi");
    }

    @Test
    void search_returnsEmpty_whenNothingMatches() {
        bookRepository.save(book("The Iliad", "Homer", "epic"));

        assertThat(search("zzz").getContent()).isEmpty();
    }

    private Page<BookEntity> search(String term) {
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
                        term, term, term, PageRequest.of(0, 10));
    }

    private BookEntity book(String title, String author, String genre) {
        BookEntity book = new BookEntity();
        book.setIsbn(nextIsbn++);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setTotalCopies(5);
        book.setAvailableCopies(5);
        return book;
    }
}