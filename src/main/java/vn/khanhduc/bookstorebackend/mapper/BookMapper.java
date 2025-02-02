package vn.khanhduc.bookstorebackend.mapper;

import vn.khanhduc.bookstorebackend.dto.response.BookDetailResponse;
import vn.khanhduc.bookstorebackend.model.Book;

import java.util.List;

public class BookMapper {
    private BookMapper() {}

    public static List<BookDetailResponse> bookDetailResponses (List<Book> books) {
        return books.stream()
                .map(book -> BookDetailResponse.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .isbn(book.getIsbn())
                        .authorName(book.getAuthor().getFullName())
                        .price(book.getPrice())
                        .description(book.getDescription())
                        .language(book.getLanguage())
                        .thumbnail(book.getThumbnail())
                        .bookPath(book.getBookPath())
                        .build())
                .toList();
    }
}
