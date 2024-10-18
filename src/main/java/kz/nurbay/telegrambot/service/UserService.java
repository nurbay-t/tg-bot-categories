package kz.nurbay.telegrambot.service;

import kz.nurbay.telegrambot.model.User;
import kz.nurbay.telegrambot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user in the system if the user with the specified ID does not already exist.
     *
     * @param userId the ID of the user to check and create if necessary
     */
    public void createUserIfNotExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            User newUser = new User();
            newUser.setId(userId);
            userRepository.save(newUser);
        }
    }
}
