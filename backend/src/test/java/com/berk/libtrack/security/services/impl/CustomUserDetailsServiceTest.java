package com.berk.libtrack.security.services.impl;

import com.berk.libtrack.domain.entities.UserEntity;
import com.berk.libtrack.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_returnsUserDetails() {
        UserEntity userEntity = UserEntity.builder()
                .username("berk")
                .password("hashed_password")
                .role("ADMIN")
                .build();

        when(userRepository.findByUsername("berk")).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("berk");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("berk");
        assertThat(userDetails.getPassword()).isEqualTo("hashed_password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_throwsExceptionWhenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: unknown");
    }
}