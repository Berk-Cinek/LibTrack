package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.UserDto;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void mapTo_withMember_usesMemberId() {
        MemberEntity member = MemberEntity.builder().id(42L).build();
        UserEntity user = UserEntity.builder()
                .id(1L)
                .memberEntity(member)
                .username("bob")
                .role("MEMBER")
                .build();

        UserDto dto = userMapper.mapTo(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getMemberId()).isEqualTo(42L);
        assertThat(dto.getUsername()).isEqualTo("bob");
        assertThat(dto.getRole()).isEqualTo("MEMBER");
    }

    @Test
    void mapTo_withoutMember_memberIdIsNull() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .memberEntity(null)
                .username("bob")
                .role("MEMBER")
                .build();

        UserDto dto = userMapper.mapTo(user);

        assertThat(dto.getMemberId()).isNull();
        assertThat(dto.getUsername()).isEqualTo("bob");
    }


    @Test
    void mapFrom_withMemberId_createsMemberReference() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .memberId(42L)
                .username("bob")
                .role("MEMBER")
                .build();

        UserEntity user = userMapper.mapFrom(dto);

        assertThat(user.getMemberEntity()).isNotNull();
        assertThat(user.getMemberEntity().getId()).isEqualTo(42L);
    }

    @Test
    void mapFrom_withoutMemberId_leavesMemberNull() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .memberId(null)
                .username("bob")
                .role("MEMBER")
                .build();

        UserEntity user = userMapper.mapFrom(dto);

        assertThat(user.getMemberEntity()).isNull();
        assertThat(user.getUsername()).isEqualTo("bob");
    }

    @Test
    void mapFrom_neverCopiesPassword() {
        UserDto dto = UserDto.builder().id(1L).memberId(42L).username("bob").role("MEMBER").build();

        UserEntity user = userMapper.mapFrom(dto);

        assertThat(user.getPassword()).isNull();
    }
}