package vn.khanhduc.bookstorebackend.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.bookstorebackend.dto.request.BookCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.BookCreationResponse;
import vn.khanhduc.bookstorebackend.dto.response.BookDetailResponse;
import vn.khanhduc.bookstorebackend.dto.response.PageResponse;
import vn.khanhduc.bookstorebackend.exception.ErrorCode;
import vn.khanhduc.bookstorebackend.exception.AppException;
import vn.khanhduc.bookstorebackend.mapper.BookMapper;
import vn.khanhduc.bookstorebackend.model.Book;
import vn.khanhduc.bookstorebackend.model.BookElasticSearch;
import vn.khanhduc.bookstorebackend.model.User;
import vn.khanhduc.bookstorebackend.repository.BookRepository;
import vn.khanhduc.bookstorebackend.repository.SearcherRepository;
import vn.khanhduc.bookstorebackend.repository.UserRepository;
import vn.khanhduc.bookstorebackend.repository.specification.SpecificationBuildQuery;
import vn.khanhduc.bookstorebackend.service.BookService;
import vn.khanhduc.bookstorebackend.service.CloudinaryService;
import vn.khanhduc.bookstorebackend.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "BOOK-SERVICE")
public class BookServiceImpl implements BookService {

    @PersistenceContext
    private EntityManager entityManager;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final SearcherRepository searcherRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PageResponse<BookElasticSearch> searchElastic(int page, int size, String keyword) {
        NativeQuery query;
        if (keyword == null) {
            query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(PageRequest.of(page - 1, size))
                    .build();
        } else {
            query = NativeQuery.builder()
                    .withQuery(q -> q.bool(b -> b
                            .should(s -> s.match(m -> m.field("title").query(keyword)
                                    .fuzziness("AUTO")
                                    .minimumShouldMatch("70%")
                                    .boost(2.0F)))
                            .should(s -> s.match(m -> m.field("author_name").query(keyword)
                                    .fuzziness("AUTO")
                                    .minimumShouldMatch("70%")))
                            .should(s -> s.match(m -> m.field("description")
                                    .fuzziness("AUTO")
                                    .minimumShouldMatch("70%")
                                    .query(keyword)))
                            .should(s -> s.matchPhrasePrefix(m -> m.field("isbn").query(keyword)))// matchPhrasePrefix: giống tìm kiếm like
                            .should(s -> s.match(m -> m.field("language").query(keyword)))
                    ))
                    .withPageable(PageRequest.of(page - 1, size))
                    .build();
        }

        SearchHits<BookElasticSearch> searchHits = elasticsearchTemplate.search(query, BookElasticSearch.class);

        long totalElements = searchHits.getTotalHits();

        return PageResponse.<BookElasticSearch>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages((int) Math.ceil(totalElements / (double) size))
                .totalElements(totalElements)
                .build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public BookCreationResponse uploadBook(BookCreationRequest request,
                                           MultipartFile thumbnail,
                                           MultipartFile bookPdf) {
        String email = SecurityUtils.getCurrentLogin()
                        .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        log.info("Upload book start ...!");
        String thumbnailUrl = cloudinaryService.uploadImage(thumbnail);
        String bookPath = null;
        if(bookPdf != null) {
            bookPath = cloudinaryService.uploadImage(bookPdf);
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .price(request.getPrice())
                .language(request.getLanguage())
                .thumbnail(thumbnailUrl)
                .bookPath(bookPath)
                .author(user)
                .publisher(user.getEmail())
                .build();

        bookRepository.save(book);

        BookElasticSearch bookElasticSearch = BookElasticSearch.builder()
                .id(book.getId().toString())
                .title(book.getTitle())
                .price(book.getPrice())
                .description(book.getDescription())
                .language(book.getLanguage())
                .authorName(book.getAuthor().getFullName())
                .build();

        log.info("Uploaded success");

        kafkaTemplate.send("save-to-elastic-search", bookElasticSearch);
//        kafkaTemplate.send("save-to-elastic-search", user.getEmail(), bookElasticSearch);

        return BookCreationResponse.builder()
                .authorName(user.getFullName())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .language(book.getLanguage())
                .price(book.getPrice())
                .thumbnail(book.getThumbnail())
                .bookPath(book.getBookPath())
                .build();
    }

    @Override
    public BookDetailResponse getBookById(Long id) {
        log.info("Get Book By Id {}", id);
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        return BookDetailResponse.builder()
                .id(book.getId())
                .authorName(book.getAuthor().getFullName())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .language(book.getLanguage())
                .price(book.getPrice())
                .thumbnail(book.getThumbnail())
                .bookPath(book.getBookPath())
                .build();
    }

    @Override
    public PageResponse<BookDetailResponse> getAllBook(int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Book> bookPage = bookRepository.findAll(pageable);

        List<Book> books = bookPage.getContent();

        return PageResponse.<BookDetailResponse>builder()
                .currentPage(page)
                .pageSize(pageable.getPageSize())
                .totalPages(bookPage.getTotalPages())
                .totalElements(bookPage.getTotalElements())
                .data(BookMapper.bookDetailResponses(books))
                .build();
    }

    @Override
    public PageResponse<BookDetailResponse> getBookWithSortMultiFieldAndSearch(int page, int size, String sortBy, String user, String... search) {
        return searcherRepository.getBookWithSortMultiFieldAndSearch(page, size, sortBy, user, search);
    }

    @Override
    public PageResponse<BookDetailResponse> getBookWithSortAndSearchSpecification(int page, int size, String sortBy, String[] books, String[] users) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if(books != null && users != null) {
            // xử lý join
            log.info("Search Book Join User");
            return searcherRepository.getBookJoinUser(pageable, books, users);
        }

        log.info("Search Book not Join");
        SpecificationBuildQuery specificationBuilder = new SpecificationBuildQuery();
        if(books != null) {
            for(String book : books) {
                Pattern pattern = Pattern.compile("(\\w+?)([:><!~^$.])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
                Matcher matcher = pattern.matcher(book);
                if(matcher.find()) {
                    specificationBuilder.with(matcher.group(1), matcher.group(2), matcher.group(3),
                            matcher.group(4), matcher.group(5));
                }
            }
        }
        Page<Book> pageBooks = bookRepository.findAll(specificationBuilder.buildQuery(), pageable);
        List<Book> listBooks = pageBooks.getContent();
        return PageResponse.<BookDetailResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageBooks.getTotalPages())
                .totalElements(pageBooks.getTotalElements())
                .data(BookMapper.bookDetailResponses(listBooks))
                .build();
    }

    @Override
    public PageResponse<BookDetailResponse> getBookWithSortAndSearchByKeyword(int page, int size, String keyword) {
        var pageable = PageRequest.of(page - 1, size);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = criteriaQuery.from(Book.class);

        var listPredicate = new ArrayList<Predicate>();
        if(StringUtils.hasLength(keyword)) {
//            root.getModel().getDeclaredSingularAttributes().forEach(attribute -> {
//                if(attribute.getJavaType().equals(String.class)) {
//                    listPredicate.add(criteriaBuilder.like(root.get(attribute.getName()), String.format("%%%s%%", keyword)));
//                }
//            });
            Predicate toTitle = criteriaBuilder.like(root.get("title"), String.format("%%%s%%", keyword));
            Predicate toLanguage = criteriaBuilder.like(root.get("language"), String.format("%%%s%%", keyword));
            Predicate toPrice = criteriaBuilder.like(root.get("description"), String.format("%%%s%%", keyword));
            listPredicate.add(toTitle);
            listPredicate.add(toLanguage);
            listPredicate.add(toPrice);

            Join<Book, User> userJoin = root.join("author", JoinType.LEFT);
            Predicate toFullNamePredicate = criteriaBuilder.like(userJoin.get("fullName"), String.format("%%%s%%", keyword));
            Predicate toEmailPredicate = criteriaBuilder.like(userJoin.get("email"), String.format("%%%s%%", keyword));
            listPredicate.add(toFullNamePredicate);
            listPredicate.add(toEmailPredicate);
        }
        Predicate predicate = listPredicate.isEmpty() ? criteriaBuilder.conjunction()
                : criteriaBuilder.or(listPredicate.toArray(new Predicate[0]));
        criteriaQuery.where(predicate);

        List<Book> bookList = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(size)
                .getResultList();

        Long totalElements = getTotalElement(keyword);

        return PageResponse.<BookDetailResponse>builder()
                .currentPage(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .totalPages((int) Math.ceil((double) totalElements / size))
                .totalElements(totalElements)
                .data(BookMapper.bookDetailResponses(bookList))
                .build();
    }

    private Long getTotalElement(String keyword) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Book> root = criteriaQuery.from(Book.class);

        List<Predicate> listPredicate = new ArrayList<>();
        if(StringUtils.hasLength(keyword)) {
            Predicate toTitle = criteriaBuilder.like(root.get("title"), String.format("%%%s%%", keyword));
            Predicate toLanguage = criteriaBuilder.like(root.get("language"), String.format("%%%s%%", keyword));
            Predicate toPrice = criteriaBuilder.like(root.get("description"), String.format("%%%s%%", keyword));
            listPredicate.add(toTitle);
            listPredicate.add(toLanguage);
            listPredicate.add(toPrice);

            Join<Book, User> userJoin = root.join("author", JoinType.LEFT);
            Predicate toFullNamePredicate = criteriaBuilder.like(userJoin.get("fullName"), String.format("%%%s%%", keyword));
            Predicate toEmailPredicate = criteriaBuilder.like(userJoin.get("email"), String.format("%%%s%%", keyword));
            listPredicate.add(toFullNamePredicate);
            listPredicate.add(toEmailPredicate);
        }
        Predicate predicate = listPredicate.isEmpty() ? criteriaBuilder.conjunction()
                : criteriaBuilder.or(listPredicate.toArray(new Predicate[0]));
        criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);

        return entityManager.createQuery(criteriaQuery)
                .getSingleResult();
    }

}
