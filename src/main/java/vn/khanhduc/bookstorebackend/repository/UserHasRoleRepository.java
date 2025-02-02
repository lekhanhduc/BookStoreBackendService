package vn.khanhduc.bookstorebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.khanhduc.bookstorebackend.model.UserHasRole;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
}
