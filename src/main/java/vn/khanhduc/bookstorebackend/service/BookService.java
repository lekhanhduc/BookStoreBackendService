package vn.khanhduc.bookstorebackend.service;

import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.bookstorebackend.dto.request.BookCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.BookCreationResponse;
import vn.khanhduc.bookstorebackend.dto.response.BookDetailResponse;
import vn.khanhduc.bookstorebackend.dto.response.PageResponse;
import vn.khanhduc.bookstorebackend.model.BookElasticSearch;

public interface BookService {
    BookCreationResponse uploadBook(BookCreationRequest request, MultipartFile thumbnail, MultipartFile book);
    BookDetailResponse getBookById(Long id);
    PageResponse<BookDetailResponse> getAllBook(int page, int size);
    PageResponse<BookDetailResponse> getBookWithSortMultiFieldAndSearch(int page, int size, String sortBy, String user, String... search);
    PageResponse<BookDetailResponse> getBookWithSortAndSearchSpecification(int page, int size, String sortBy, String[] books, String[] users);
    PageResponse<BookDetailResponse> getBookWithSortAndSearchByKeyword(int page, int size, String keyword);
    PageResponse<BookElasticSearch> searchElastic(int page, int size, String keyword);
}
