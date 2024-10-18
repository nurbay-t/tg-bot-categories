package kz.nurbay.telegrambot.repository;

import kz.nurbay.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}