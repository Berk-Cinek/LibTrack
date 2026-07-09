package com.berk.libtrack.integration;

import com.berk.libtrack.TestcontainersConfig;
import com.berk.libtrack.domain.entities.BookEntity;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.repositories.BookRepository;
import com.berk.libtrack.repositories.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
class ApiIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;


    @Test
    void journey_registerLoginBorrowReturn_stockMovesCorrectly() {
        MemberEntity member = memberRepository.save(member(7001L, "Journey Person", "journey@x.com"));
        BookEntity book = bookRepository.save(book("Journey Book", 3));


        ResponseEntity<Void> reg = rest.postForEntity("/auth/register",
                Map.of("memberNo", 7001, "username", "journeyuser", "password", "pw123456"),
                Void.class);
        assertThat(reg.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String memberCookie = login("journeyuser", "pw123456");

        ResponseEntity<String> borrow = rest.exchange("/loans/borrow", HttpMethod.POST,
                jsonWithCookie(Map.of("bookId", book.getId()), memberCookie), String.class);
        assertThat(borrow.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(bookRepository.findById(book.getId()).orElseThrow()
                .getAvailableCopies()).isEqualTo(2);

        String adminCookie = login("admin", "1");
        Long loanId = extractId(borrow.getBody());
        ResponseEntity<String> patch = rest.exchange("/loans/" + loanId, HttpMethod.PATCH,
                jsonWithCookie(Map.of("status", "RETURNED"), adminCookie), String.class);
        assertThat(patch.getStatusCode()).isEqualTo(HttpStatus.OK);


        assertThat(bookRepository.findById(book.getId()).orElseThrow()
                .getAvailableCopies()).isEqualTo(3);
    }


    @Test
    void security_anonymousCanBrowseBooks_butNotLoans() {
        assertThat(rest.getForEntity("/books", String.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(rest.getForEntity("/loans", String.class).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void security_memberGets403OnAdminEndpoint() {
        memberRepository.save(member(7002L, "Plain Member", "plain@x.com"));
        rest.postForEntity("/auth/register",
                Map.of("memberNo", 7002, "username", "plainmember", "password", "pw123456"),
                Void.class);
        String cookie = login("plainmember", "pw123456");

        ResponseEntity<String> resp = rest.exchange("/loans", HttpMethod.GET,
                new HttpEntity<>(cookieHeaders(cookie)), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }



    @Test
    void borrow_atThreeLoanCap_returns409WithMessage() {
        MemberEntity capped = memberRepository.save(member(7003L, "Capped Person", "capped@x.com"));
        BookEntity b1 = bookRepository.save(book("Cap Book 1", 5));
        BookEntity b2 = bookRepository.save(book("Cap Book 2", 5));
        BookEntity b3 = bookRepository.save(book("Cap Book 3", 5));
        BookEntity b4 = bookRepository.save(book("Cap Book 4", 5));

        rest.postForEntity("/auth/register",
                Map.of("memberNo", 7003, "username", "cappeduser", "password", "pw123456"),
                Void.class);
        String cookie = login("cappeduser", "pw123456");

        for (BookEntity b : new BookEntity[]{b1, b2, b3}) {
            rest.exchange("/loans/borrow", HttpMethod.POST,
                    jsonWithCookie(Map.of("bookId", b.getId()), cookie), String.class);
        }

        ResponseEntity<String> fourth = rest.exchange("/loans/borrow", HttpMethod.POST,
                jsonWithCookie(Map.of("bookId", b4.getId()), cookie), String.class);

        assertThat(fourth.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(fourth.getBody()).contains("3 active loans");   // your handler's JSON message
    }

    private String login(String username, String password) {
        ResponseEntity<String> resp = rest.postForEntity("/auth/login",
                Map.of("username", username, "password", password), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String setCookie = resp.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(setCookie).isNotNull();
        return setCookie.split(";")[0];
    }

    private HttpHeaders cookieHeaders(String cookie) {
        HttpHeaders h = new HttpHeaders();
        h.add(HttpHeaders.COOKIE, cookie);
        return h;
    }

    private HttpEntity<Map<String, Object>> jsonWithCookie(Map<String, Object> body, String cookie) {
        HttpHeaders h = cookieHeaders(cookie);
        h.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, h);
    }

    private Long extractId(String json) {
        var m = java.util.regex.Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(json);
        assertThat(m.find()).isTrue();
        return Long.parseLong(m.group(1));
    }

    private MemberEntity member(Long no, String name, String email) {
        return MemberEntity.builder().memberNo(no).fullName(name).email(email).isActive(true).build();
    }

    private BookEntity book(String title, int copies) {
        BookEntity b = new BookEntity();
        b.setIsbn(Math.abs((long) title.hashCode()));
        b.setTitle(title);
        b.setAuthor("Test Author");
        b.setGenre("test");
        b.setTotalCopies(copies);
        b.setAvailableCopies(copies);
        return b;
    }
}