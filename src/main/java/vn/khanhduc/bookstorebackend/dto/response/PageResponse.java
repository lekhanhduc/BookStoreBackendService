package vn.khanhduc.bookstorebackend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
public class PageResponse <T> implements Serializable {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private Long totalElements;

    @Builder.Default
    private List<T> data = Collections.emptyList();
}
