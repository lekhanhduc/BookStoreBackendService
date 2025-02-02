package vn.khanhduc.bookstorebackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import vn.khanhduc.bookstorebackend.model.BookElasticSearch;
import vn.khanhduc.bookstorebackend.repository.BookElasticRepository;
import vn.khanhduc.bookstorebackend.service.KafkaService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "KAFKA-SERVICE")
public class KafkaServiceImpl implements KafkaService {

    private final BookElasticRepository bookElasticRepository;

    @Override
    @KafkaListener(topics = "save-to-elastic-search", groupId = "book-elastic-search")
    public void saveBookToElasticSearch(BookElasticSearch book, Acknowledgment acknowledgment) {
       try {
           log.info("Start saving book to elasticsearch ");
           if(book != null && !bookElasticRepository.existsById(book.getId())) {
               bookElasticRepository.save(book);
               log.info("saved book to elasticsearch success ");
               // Commit offset sau khi xử lý thành công
               acknowledgment.acknowledge();
           } else {
               log.error("saving book to elasticsearch failed");
           }
       }catch (Exception e) {
           log.error("Error while saving book to Elasticsearch: {}", e.getMessage());
           throw e; // ném ngoại lệ để kafka tự retry
       }
    }
}
