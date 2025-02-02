package vn.khanhduc.bookstorebackend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class CartCreationRequest implements Serializable {

    @NotNull(message = "BookId cannot be null")
    @Min(value = 1, message = "BookID must be greater than 0")
    private Long bookId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Long quantity;
}
