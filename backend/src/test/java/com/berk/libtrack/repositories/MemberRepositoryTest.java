package com.berk.libtrack.repositories;

import com.berk.libtrack.TestcontainersConfig;
import com.berk.libtrack.domain.entities.MemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByFullNameContainingIgnoreCase_matchesPartialAndCaseInsensitive() {
        memberRepository.save(member(1L, "berk", "berk@gmail.com"));
        memberRepository.save(member(2L, "elçin", "elcin@gmail.com"));
        memberRepository.save(member(3L, "azmi", "azmi@gmail.com"));

        assertThat(search("BERK").getContent())
                .extracting(MemberEntity::getFullName)
                .containsExactly("berk");

        assertThat(search("elçin").getContent())
                .extracting(MemberEntity::getFullName)
                .containsExactly("elçin");

        assertThat(search("az").getContent())
                .extracting(MemberEntity::getFullName)
                .containsExactly("azmi");

        assertThat(search("böööööö").getContent()).isEmpty();
    }

    @Test
    void findByMemberNo_returnsMatch_andEmptyForUnknown() {
        memberRepository.save(member(42L, "berk", "berk@gmail.com"));

        Optional<MemberEntity> found = memberRepository.findByMemberNo(42L);
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("berk");

        assertThat(memberRepository.findByMemberNo(999L)).isEmpty();
    }

    private Page<MemberEntity> search(String term) {
        return memberRepository.findByFullNameContainingIgnoreCase(term, PageRequest.of(0, 10));
    }

    private MemberEntity member(Long memberNo, String fullName, String email) {
        return MemberEntity.builder()
                .fullName(fullName)
                .memberNo(memberNo)
                .email(email)
                .isActive(true)
                .build();
    }
}