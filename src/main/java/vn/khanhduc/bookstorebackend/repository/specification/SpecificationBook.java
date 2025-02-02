package vn.khanhduc.bookstorebackend.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import vn.khanhduc.bookstorebackend.model.Book;

@RequiredArgsConstructor
public class SpecificationBook implements Specification<Book> {

    private final SpecSearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull Root<Book> root,
                                 CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
        return switch (criteria.getOperation()){
            case EQUALITY -> {
                if(root.get(criteria.getKey()).getJavaType().equals(String.class)){
                    yield criteriaBuilder.like(root.get(criteria.getKey()), "%" +criteria.getValue()+ "%");
                } else {
                    yield criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
                }
            }
            case NEGATION -> criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(root.get(criteria.getKey()), String.format("%%%s%%", criteria.getValue()));
            case START_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case END_WITH -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }
}
