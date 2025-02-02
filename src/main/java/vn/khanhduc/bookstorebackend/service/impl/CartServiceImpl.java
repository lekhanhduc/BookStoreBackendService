package vn.khanhduc.bookstorebackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.khanhduc.bookstorebackend.dto.request.CartCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.CartCreationResponse;
import vn.khanhduc.bookstorebackend.dto.response.CartItemResponse;
import vn.khanhduc.bookstorebackend.exception.AppException;
import vn.khanhduc.bookstorebackend.exception.ErrorCode;
import vn.khanhduc.bookstorebackend.model.Book;
import vn.khanhduc.bookstorebackend.model.Cart;
import vn.khanhduc.bookstorebackend.model.User;
import vn.khanhduc.bookstorebackend.repository.BookRepository;
import vn.khanhduc.bookstorebackend.repository.CartRepository;
import vn.khanhduc.bookstorebackend.repository.UserRepository;
import vn.khanhduc.bookstorebackend.service.CartService;
import vn.khanhduc.bookstorebackend.utils.SecurityUtils;
import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CART-SERVICE")
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public CartCreationResponse createCart(CartCreationRequest request) {
       String email = SecurityUtils.getCurrentLogin()
               .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

       Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

       Cart cart = Cart.builder()
                .book(book)
                .user(user)
                .quantity(request.getQuantity())
                .price(book.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                .build();
       cartRepository.save(cart);

       var carts = cartRepository.findAllByUserId(user.getId());
       log.info("carts {}", carts);
       Long totalElements = cartRepository.countByUserId(user.getId());
       return CartCreationResponse.builder()
                .cartId(cart.getId())
                .userId(user.getId())
                .totalPrice(carts.stream().map(CartItemResponse::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalElements(totalElements)
                .items(carts)
                .build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void deleteCart(Long id) {

        String email = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if(!Objects.equals(user, cart.getUser())) {
            throw new AppException(ErrorCode.ACCESS_DINED);
        }
        cartRepository.delete(cart);
    }

}
