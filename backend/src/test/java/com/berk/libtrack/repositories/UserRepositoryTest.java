package com.berk.libtrack.repositories;

import com.berk.libtrack.TestcontainersConfig;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void existsByMemberEntityId_trueForClaimedMember_falseForFreeMember() {
        MemberEntity claimed = memberRepository.save(member(2001L, "Claimed Person", "claimed@x.com"));
        MemberEntity free = memberRepository.save(member(2002L, "Free Person", "free@x.com"));

        userRepository.save(UserEntity.builder()
                .memberEntity(claimed)
                .username("claimeduser")
                .password("$2a$10$")
                .role("MEMBER")
                .build());


        assertThat(userRepository.existsByMemberEntity_Id(claimed.getId())).isTrue();
        assertThat(userRepository.existsByMemberEntity_Id(free.getId())).isFalse();
    }

    @Test
    void existsByUsername_detectsTakenAndFreeNames() {
        MemberEntity m = memberRepository.save(member(2003L, "Someone", "someone@x.com"));
        userRepository.save(UserEntity.builder()
                .memberEntity(m)
                .username("takenname")
                .password("$2a$10$")
                .role("MEMBER")
                .build());

        assertThat(userRepository.existsByUsername("takenname")).isTrue();
        assertThat(userRepository.existsByUsername("freename")).isFalse();
    }

    private MemberEntity member(Long memberNo, String fullName, String email) {
        return MemberEntity.builder()
                .memberNo(memberNo)
                .fullName(fullName)
                .email(email)
                .isActive(true)
                .build();
    }
}