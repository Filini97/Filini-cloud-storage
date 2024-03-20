package ru.filini.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.filini.cloudstorage.tables.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
