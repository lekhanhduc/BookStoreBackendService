package vn.khanhduc.bookstorebackend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.khanhduc.bookstorebackend.dto.response.BookDetailResponse;
import vn.khanhduc.bookstorebackend.dto.response.PageResponse;
import vn.khanhduc.bookstorebackend.mapper.BookMapper;
import vn.khanhduc.bookstorebackend.model.Book;
import vn.khanhduc.bookstorebackend.model.User;
import vn.khanhduc.bookstorebackend.repository.criteria.SearchCriteria;
import vn.khanhduc.bookstorebackend.repository.criteria.SearchCriteriaQueryConsumer;
import vn.khanhduc.bookstorebackend.repository.specification.SpecSearchCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
@Slf4j(topic = "SEARCH-REPOSITORY")
public class SearcherRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<BookDetailResponse> getBookWithSortMultiFieldAndSearch(int page, int size, String sortBy, String user, String... search) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = criteriaQuery.from(Book.class);
        Predicate predicate = criteriaBuilder.conjunction();

        List<SearchCriteria> criteriaList = new ArrayList<>();
        if(search != null) {
            for(String s : search) {
                Pattern pattern = Pattern.compile("(\\w+?)([:<>!])(.*)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }
        SearchCriteriaQueryConsumer queryConsumer = new SearchCriteriaQueryConsumer(criteriaBuilder, root, predicate);

        if(!criteriaList.isEmpty()) {
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
            criteriaQuery.where(predicate);
        }

        if(StringUtils.hasLength(user)) {
            log.info("Sort Book and Join User");
            Join<Book, User> userJoin = root.join("author");
            Predicate likeToFullName = criteriaBuilder.like(userJoin.get("fullName"), String.format("%%%s%%", user));
            Predicate likeToEmail = criteriaBuilder.like(userJoin.get("email"), String.format("%%%s%%", user));
            Predicate finalPredicate = criteriaBuilder.or(likeToEmail, likeToFullName);

            criteriaQuery.where(predicate, finalPredicate);
        }

        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)([:><!])(asc:desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")) {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(columnName)));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(columnName)));
                }
            }
        }
        List<Book> bookList = entityManager.createQuery(criteriaQuery)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();

        Long totalElements = getTotalElements(criteriaList, user);

        return PageResponse.<BookDetailResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .totalElements(totalElements)
                .data(BookMapper.bookDetailResponses(bookList))
                .build();
    }

    private Long getTotalElements (List<SearchCriteria> criteriaList, String user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Book> root = query.from(Book.class);
        // Khởi tạo Predicate ban đầu là TRUE
        Predicate predicate = criteriaBuilder.conjunction();

        if (!criteriaList.isEmpty()) {
            SearchCriteriaQueryConsumer queryConsumer = new SearchCriteriaQueryConsumer(criteriaBuilder, root, predicate);
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();
        }

        if(StringUtils.hasLength(user)) {
            Join<Book, User> userJoin = root.join("author");
            Predicate likeToFullName = criteriaBuilder.like(userJoin.get("fullName"), String.format("%%%s%%", user));
            Predicate likeToEmail = criteriaBuilder.like(userJoin.get("email"), String.format("%%%s%%", user));
            Predicate finalPre = criteriaBuilder.or(likeToFullName, likeToEmail);
            query.where(predicate, finalPre);
        }
        query.select(criteriaBuilder.count(root)).where(predicate);

        return entityManager.createQuery(query)
                .getSingleResult();
    }

    public PageResponse<BookDetailResponse> getBookJoinUser(Pageable pageable, String[] books, String[] users) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = criteriaQuery.from(Book.class);

        Join<Book, User> userJoin = root.join("author", JoinType.LEFT);

        var userPredicate = new ArrayList<Predicate>();
        var bookPredicate = new ArrayList<Predicate>();

        var pattern = Pattern.compile("(\\w+?)([:><!~^$.])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
        for(var book : books) {
            var matcher = pattern.matcher(book);
            if(matcher.find()) {
                SpecSearchCriteria searchCriteria = new SpecSearchCriteria(
                        matcher.group(1), matcher.group(2), matcher.group(3),
                        matcher.group(4), matcher.group(5));
                Predicate predicate = toBookPredicate(criteriaBuilder, root, searchCriteria);
                bookPredicate.add(predicate);
            }
        }
        for(var user : users) {
            var matcher = pattern.matcher(user);
            if(matcher.find()) {
                SpecSearchCriteria searchCriteria = new SpecSearchCriteria(
                        matcher.group(1), matcher.group(2), matcher.group(3),
                        matcher.group(4), matcher.group(5));
                Predicate predicate = toUserPredicate(criteriaBuilder, userJoin, searchCriteria);
                userPredicate.add(predicate);
            }
        }
        Predicate finalUserPredicate = criteriaBuilder.and(userPredicate.toArray(new Predicate[0]));
        Predicate finalBookPredicate = criteriaBuilder.and(bookPredicate.toArray(new Predicate[0]));
        Predicate finalPredicate = criteriaBuilder.or(finalUserPredicate, finalBookPredicate); // các điều kiện của book liên kết với user thông qua toán tử and
        criteriaQuery.where(finalPredicate);

        List<Book> bookList = entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalElements = getTotalElements(books, users);
        return PageResponse.<BookDetailResponse>builder()
                .currentPage(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .totalPages((int) Math.ceil((double) totalElements / pageable.getPageSize()))
                .totalElements(totalElements)
                .data(BookMapper.bookDetailResponses(bookList))
                .build();
    }

    private Long getTotalElements(String[] books, String[] users) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Book> root = criteriaQuery.from(Book.class);
        Join<Book, User> userJoin = root.join("author");

        List<Predicate> userPredicate = new ArrayList<>();
        List<Predicate> bookPredicate = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\w+?)([:><!~^$.])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");

        for(String book : books) {
            Matcher matcher = pattern.matcher(book);
            if(matcher.find()) {
                SpecSearchCriteria searchCriteria = new SpecSearchCriteria(
                        matcher.group(1), matcher.group(2), matcher.group(3),
                        matcher.group(4), matcher.group(5));
                Predicate predicate = toBookPredicate(criteriaBuilder, root, searchCriteria);
                bookPredicate.add(predicate);
            }
        }
        for(String user : users) {
            Matcher matcher = pattern.matcher(user);
            if(matcher.find()) {
                SpecSearchCriteria searchCriteria = new SpecSearchCriteria(
                        matcher.group(1), matcher.group(2), matcher.group(3),
                        matcher.group(4), matcher.group(5));
                Predicate predicate = toUserPredicate(criteriaBuilder, userJoin, searchCriteria);
                userPredicate.add(predicate);
            }
        }
        Predicate finalUserPredicate = criteriaBuilder.and(userPredicate.toArray(new Predicate[0]));
        Predicate finalBookPredicate = criteriaBuilder.and(bookPredicate.toArray(new Predicate[0]));
        Predicate finalPredicate = criteriaBuilder.and(finalUserPredicate, finalBookPredicate);

        criteriaQuery.select(criteriaBuilder.count(root)).where(finalPredicate);
        return entityManager.createQuery(criteriaQuery)
                .getSingleResult();
    }

    private Predicate toUserPredicate(CriteriaBuilder criteriaBuilder,
                                      Join<Book, User> userJoin,
                                      SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> {
                if(userJoin.get(criteria.getKey()).getJavaType().equals(String.class)) {
                    yield criteriaBuilder.like(userJoin.get(criteria.getKey()), String.format("%%%s%%", criteria.getValue()));
                } else {
                    yield criteriaBuilder.equal(userJoin.get(criteria.getKey()), criteria.getValue());
                }
            }
            case NEGATION -> criteriaBuilder.notEqual(userJoin.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> criteriaBuilder.greaterThanOrEqualTo(userJoin.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> criteriaBuilder.lessThanOrEqualTo(userJoin.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> criteriaBuilder.like(userJoin.get(criteria.getKey()), String.format("%%%s%%", criteria.getValue()));
            case START_WITH -> criteriaBuilder.like(userJoin.get(criteria.getKey()), criteria.getValue() + "%");
            case END_WITH -> criteriaBuilder.like(userJoin.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> criteriaBuilder.like(userJoin.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    private Predicate toBookPredicate(CriteriaBuilder criteriaBuilder,
                                      Root<Book> root,
                                      SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> {
                if(root.get(criteria.getKey()).getJavaType().equals(String.class)) {
                    yield criteriaBuilder.like(root.get(criteria.getKey()), String.format("%%%s%%", criteria.getValue()));
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
