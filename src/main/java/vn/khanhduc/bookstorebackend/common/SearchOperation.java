package vn.khanhduc.bookstorebackend.common;

public enum SearchOperation {
    EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, START_WITH, END_WITH, CONTAINS;

    public static final String OR_PREDICATE = "'";
    public static final String ZERO_OR_MORE_REGEX = "*";

    public static SearchOperation getOperation(char input) {
        return switch (input) {
            case '~' -> LIKE;
            case ':' -> EQUALITY;
            case '!' -> NEGATION;
            case '>' -> GREATER_THAN;
            case '<' -> LESS_THAN;
            case '.' -> START_WITH;
            case '$' -> END_WITH;
            default -> null;
        };
    }
}
