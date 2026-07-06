package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.UserDto;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.domain.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<UserEntity, UserDto> {

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .memberId(userEntity.getMemberEntity() == null ? null : userEntity.getMemberEntity().getId())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .build();
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        MemberEntity memberRef = null;
        if (userDto.getMemberId() != null) {
            memberRef = new MemberEntity();
            memberRef.setId(userDto.getMemberId());
        }

        return UserEntity.builder()
                .id(userDto.getId())
                .memberEntity(memberRef)
                .username(userDto.getUsername())
                .role(userDto.getRole())
                .build();
    }
}