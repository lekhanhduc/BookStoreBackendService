package vn.khanhduc.bookstorebackend.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse implements Serializable {
    private Long bookId;
    private String title;
    private BigDecimal priceBook;
    private String thumbnail;
    private Long quantity;
    private BigDecimal totalPrice;
}
