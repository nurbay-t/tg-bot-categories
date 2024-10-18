package kz.nurbay.telegrambot.repository;

import kz.nurbay.telegrambot.model.Category;
import kz.nurbay.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByNameAndUserId(String parentName, Long userId);

    boolean existsByUserIdAndParentIsNull(Long userId);

    List<Category> findAllByUserId(Long userId);

    Optional<Category> findByNameAndUser(String elementName, User user);
}
