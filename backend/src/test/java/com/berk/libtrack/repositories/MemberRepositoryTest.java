package com.berk.libtrack.repositories;

import com.berk.libtrack.TestcontainersConfig;
import com.berk.libtrack.domain.entities.MemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void memberSearch_matchesNameOrEmail(){
        memberRepository.save(member(1L, "berk", "berk@gmail.com", true));
        memberRepository.save(member(2L, "elçin", "elçin@gmail.com", true));
        memberRepository.save(member(3L, "azmi", "azmi@gmail.com", true));

        assertThat(search("BERK", (Pageable) memberRepository.findAll()).getContent()).hasSize(1);
        assertThat(search("elçin", (Pageable) memberRepository.findAll()).getContent()).hasSize(1);
        assertThat(search("az", (Pageable) memberRepository.findAll()).getContent()).hasSize(1);
        assertThat(search("böööööö", (Pageable) memberRepository.findAll()).getContent()).isEmpty();
    }

    private MemberEntity member(Long memberNo, String fullName, String email, boolean isActive) {
        return MemberEntity.builder()
                .fullName(fullName)
                .memberNo(memberNo)
                .email(email)
                .isActive(isActive)
                .build();
    }

    private Page<MemberEntity> search(String term, Pageable pageable)
    {
        return memberRepository.findByFullNameContainingIgnoreCase(term, pageable);
    }

}