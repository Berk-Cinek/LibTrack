package com.berk.libtrack.repositories.specs;

import com.berk.libtrack.domain.entities.LoanEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class LoanSpecifications {

    public static Specification<LoanEntity> search(String term) {
        return (root, query, cb) -> {
            String pattern = "%" + term.toLowerCase() + "%";

            Predicate titleMatch = cb.like(cb.lower(root.get("bookEntity").get("title")), pattern);
            Predicate statusMatch = cb.like(cb.lower(root.get("status").as(String.class)), pattern);

            Predicate combined = cb.or(titleMatch, statusMatch);

            try {
                Long memberId = Long.parseLong(term);
                Predicate memberMatch = cb.equal(root.get("memberEntity").get("id"), memberId);
                combined = cb.or(combined, memberMatch);
            } catch (NumberFormatException ignored) {
            }

            return combined;
        };
    }
}