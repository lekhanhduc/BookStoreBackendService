package vn.khanhduc.bookstorebackend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BookDetailResponse implements Serializable {
    private Long id;
    private String authorName;
    private String title;
    private String isbn;
    private String description;
    private BigDecimal price;
    private String language;
    private String thumbnail;
    private String bookPath;
}
