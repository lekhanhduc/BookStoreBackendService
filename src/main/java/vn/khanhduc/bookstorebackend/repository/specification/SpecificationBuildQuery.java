package vn.khanhduc.bookstorebackend.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.khanhduc.bookstorebackend.common.SearchOperation;
import vn.khanhduc.bookstorebackend.model.Book;
import java.util.ArrayList;
import java.util.List;

public class SpecificationBuildQuery {

    private final List<SpecSearchCriteria> criteria;

    public SpecificationBuildQuery() {
        this.criteria = new ArrayList<>();
    }

    public SpecificationBuildQuery with(String key, String operation, String value, String prefix, String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public SpecificationBuildQuery with(String orPredicate, String key, String operation, String value, String prefix, String suffix) {
        SearchOperation searchOperation = SearchOperation.getOperation(operation.charAt(0));
        if(searchOperation == SearchOperation.EQUALITY) {
            boolean startWith = prefix != null && prefix.equals(SearchOperation.ZERO_OR_MORE_REGEX);
            boolean endWith = suffix != null && suffix.equals(SearchOperation.ZERO_OR_MORE_REGEX);
            if(startWith && endWith) {
                searchOperation = SearchOperation.CONTAINS;
            } else if(startWith) {
                searchOperation = SearchOperation.END_WITH;
            } else {
                searchOperation = SearchOperation.START_WITH;
            }
        }
        criteria.add(new SpecSearchCriteria(orPredicate, key, searchOperation, value));
        return this;
    }

    public Specification<Book> buildQuery() {
        if(criteria.isEmpty()) return null;

        Specification<Book> specification = new SpecificationBook(criteria.get(0));
        if(criteria.size() > 1) {
            for(int i = 1; i < criteria.size(); i++) {
                specification = criteria.get(i).getOrPredicate()
                        ? specification.or(new SpecificationBook(criteria.get(i)))
                        : specification.and(new SpecificationBook(criteria.get(i)));
            }
        }
        return specification;
    }
}
