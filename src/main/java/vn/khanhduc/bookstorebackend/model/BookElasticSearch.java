package vn.khanhduc.bookstorebackend.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Document(indexName = "book")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookElasticSearch implements Serializable {

    @Serial
    private static final long serialVersionUID = -5257626960164837310L;

    @Id
    private String id;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "isbn", type = FieldType.Text)
    private String isbn;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(name = "author_name", type = FieldType.Text)
    private String authorName;

    @Field(name = "price", type = FieldType.Text)
    private BigDecimal price;

    @Field(name = "language", type = FieldType.Text)
    private String language;
}
