package vn.khanhduc.bookstorebackend.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.khanhduc.bookstorebackend.model.BookElasticSearch;

@Repository
public interface BookElasticRepository extends ElasticsearchRepository<BookElasticSearch, String> {

}
