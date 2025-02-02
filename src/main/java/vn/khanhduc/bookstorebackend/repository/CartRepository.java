package vn.khanhduc.bookstorebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.khanhduc.bookstorebackend.dto.response.CartItemResponse;
import vn.khanhduc.bookstorebackend.model.Cart;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select count(*) from Cart c where c.user.id =:id")
    Long countByUserId(Long id);

    @Query("select new vn.khanhduc.bookstorebackend.dto.response.CartItemResponse" +
            "(c.book.id, c.book.title, c.book.price, c.book.thumbnail, c.quantity, c.price) " +
            "from Cart c " +
            "where c.user.id = :id")
    List<CartItemResponse> findAllByUserId(Long id);

}
