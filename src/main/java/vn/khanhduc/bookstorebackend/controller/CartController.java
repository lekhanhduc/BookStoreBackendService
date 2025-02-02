package vn.khanhduc.bookstorebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.khanhduc.bookstorebackend.dto.request.CartCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.CartCreationResponse;
import vn.khanhduc.bookstorebackend.dto.response.ResponseData;
import vn.khanhduc.bookstorebackend.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CartController {

    private final CartService cartService;

    @PostMapping("/carts")
    ResponseData<CartCreationResponse> creationCart(@RequestBody @Valid CartCreationRequest request) {
        var result = cartService.createCart(request);

        return ResponseData.<CartCreationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Created success")
                .data(result)
                .build();
    }

    @DeleteMapping("/carts/{id}")
    ResponseData<Void> creationCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete cart success")
                .build();
    }

}
