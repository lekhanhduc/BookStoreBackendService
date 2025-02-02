package vn.khanhduc.bookstorebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.khanhduc.bookstorebackend.model.Role;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    boolean existsByName(String name);
    Optional<Role> findByName(String name);

}
