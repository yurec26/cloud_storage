package org.example.back.repository;

import org.example.back.entity.Token;
import org.example.back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findByAuthToken(Token authToken);
}
