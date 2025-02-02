package vn.khanhduc.bookstorebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
public class BookCreationRequest implements Serializable {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Isbn cannot be blank")
    private String isbn;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Price cannot be null")
    private BigDecimal price;

    @NotBlank(message = "Language cannot be blank")
    private String language;
}
