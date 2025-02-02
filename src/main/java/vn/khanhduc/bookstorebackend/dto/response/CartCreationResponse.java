package vn.khanhduc.bookstorebackend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CartCreationResponse implements Serializable {
    private Long cartId;
    private Long userId;
    private Long totalElements;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items;
}
