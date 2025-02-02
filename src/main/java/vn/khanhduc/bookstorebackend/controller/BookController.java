package vn.khanhduc.bookstorebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.bookstorebackend.dto.request.BookCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.BookCreationResponse;
import vn.khanhduc.bookstorebackend.dto.response.BookDetailResponse;
import vn.khanhduc.bookstorebackend.dto.response.PageResponse;
import vn.khanhduc.bookstorebackend.dto.response.ResponseData;
import vn.khanhduc.bookstorebackend.model.BookElasticSearch;
import vn.khanhduc.bookstorebackend.service.BookService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j(topic = "BOOK-CONTROLLER")
public class BookController {

    private final BookService bookService;

    @GetMapping("/search-book-with-elasticsearch")
    ResponseData<PageResponse<BookElasticSearch>> searchElastic(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {

        var result = bookService.searchElastic(page, size, keyword);
        return ResponseData.<PageResponse<BookElasticSearch>>builder()
                .code(HttpStatus.OK.value())
                .message("Get Books with elastic search")
                .data(result)
                .build();
    }

    @PostMapping("/upload-books")
    ResponseData<BookCreationResponse> uploadBook(
            @RequestPart(name = "request") @Valid BookCreationRequest request,
            @RequestPart(name = "thumbnail") MultipartFile thumbnail,
            @RequestPart(name = "book-pdf", required = false) MultipartFile bookPdf
            ) {
        log.info("Upload book controller start ...!");
        var result = bookService.uploadBook(request, thumbnail, bookPdf);

        return ResponseData.<BookCreationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Uploaded success")
                .data(result)
                .build();
    }

    @GetMapping("/books/{id}")
    ResponseData<BookDetailResponse> getBookById(@PathVariable Long id) {
        var result = bookService.getBookById(id);
        return ResponseData.<BookDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get Book By Id = " +id)
                .data(result)
                .build();
    }
    @GetMapping("/books")
    ResponseData<PageResponse<BookDetailResponse>> getAll(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {

        var result = bookService.getAllBook(page, size);
        return ResponseData.<PageResponse<BookDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get All Books")
                .data(result)
                .build();
    }

    @GetMapping("/books-search-criteria")
    ResponseData<PageResponse<BookDetailResponse>> getAllBookAndSearchCriteria(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) String sortBy,
            @RequestParam(name = "user", required = false) String user,
            @RequestParam(name = "search", required = false) String... search
    ) {

        var result = bookService.getBookWithSortMultiFieldAndSearch(page, size, sortBy, user, search);
        return ResponseData.<PageResponse<BookDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get All Books And Search Criteria")
                .data(result)
                .build();
    }

    @GetMapping("/books-search-specification")
    ResponseData<PageResponse<BookDetailResponse>> getAllBookAndSearchBySpecification(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) String sortBy,
            @RequestParam(name = "user", required = false) String[] users,
            @RequestParam(name = "book", required = false) String[] books
    ) {
        var result = bookService.getBookWithSortAndSearchSpecification(page, size, sortBy, books, users);
        return ResponseData.<PageResponse<BookDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get All Books And Search Specification")
                .data(result)
                .build();
    }

    @GetMapping("/books-search-keyword")
    ResponseData<PageResponse<BookDetailResponse>> getAllBookAndSearchByKeyword(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "key", required = false) String keyword
    ) {
        var result = bookService.getBookWithSortAndSearchByKeyword(page, size, keyword);
        return ResponseData.<PageResponse<BookDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get All Books And Search Keyword")
                .data(result)
                .build();
    }

}
