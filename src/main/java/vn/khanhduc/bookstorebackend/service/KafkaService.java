package vn.khanhduc.bookstorebackend.service;

import org.springframework.kafka.support.Acknowledgment;
import vn.khanhduc.bookstorebackend.model.BookElasticSearch;

public interface KafkaService {
    void saveBookToElasticSearch(BookElasticSearch book , Acknowledgment acknowledgment);
}
