package vn.khanhduc.bookstorebackend.repository.specification;

import lombok.Getter;
import lombok.Setter;
import vn.khanhduc.bookstorebackend.common.SearchOperation;

@Getter
@Setter
public class SpecSearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;
    private Boolean orPredicate;

    public SpecSearchCriteria(String key, SearchOperation operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String orPredicate, String key, SearchOperation operation, Object value) {
        this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String key, String operation, Object value, String prefix, String suffix) {
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
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }

}
