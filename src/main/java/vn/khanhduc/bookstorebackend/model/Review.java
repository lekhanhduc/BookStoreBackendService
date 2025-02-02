package vn.khanhduc.bookstorebackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity(name = "Review")
@Table(name = "reviews")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends AbstractEntity<Long>{

    @Column(name = "content")
    private String content;

    @Column(name = "rating")
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_review_id")
    private Review parentReview;

    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> replies;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
