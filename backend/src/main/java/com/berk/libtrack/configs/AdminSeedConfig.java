package com.berk.libtrack.configs;

import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.domain.entities.UserEntity;
import com.berk.libtrack.repositories.MemberRepository;
import com.berk.libtrack.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeedConfig {

    @Bean
    public CommandLineRunner seedDefaultAdmin(UserRepository userRepository,
                                              MemberRepository memberRepository,
                                              PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isPresent()) {
                return;
            }

            MemberEntity adminMember = MemberEntity.builder()
                    .memberNo(999999L)
                    .fullName("System Admin")
                    .email("admin@libtrack.local")
                    .isActive(true)
                    .build();
            memberRepository.save(adminMember);

            UserEntity adminUser = UserEntity.builder()
                    .memberEntity(adminMember)
                    .username("admin")
                    .password(passwordEncoder.encode("1"))
                    .role("ADMIN")
                    .build();
            userRepository.save(adminUser);
        };
    }
}