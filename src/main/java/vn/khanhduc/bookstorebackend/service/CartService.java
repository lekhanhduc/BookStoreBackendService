package vn.khanhduc.bookstorebackend.service;

import vn.khanhduc.bookstorebackend.dto.request.CartCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.CartCreationResponse;

public interface CartService {
    CartCreationResponse createCart(CartCreationRequest request);
    void deleteCart(Long id);
}
