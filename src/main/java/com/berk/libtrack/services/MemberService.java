package com.berk.libtrack.services;

import com.berk.libtrack.domain.entities.MemberEntity;

public interface MemberService {

    MemberEntity save(MemberEntity memberEntity);

    Boolean isExists(Long id);

    MemberEntity partialUpdate(Long id, MemberEntity memberEntity);

    void delete(Long id);
}
