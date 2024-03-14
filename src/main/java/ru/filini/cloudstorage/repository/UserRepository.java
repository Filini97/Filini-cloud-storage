package ru.filini.cloudstorage.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.filini.cloudstorage.tables.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
