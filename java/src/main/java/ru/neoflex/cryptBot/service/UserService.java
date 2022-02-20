package ru.neoflex.cryptBot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import ru.neoflex.cryptBot.entity.User;
import ru.neoflex.cryptBot.repository.UserRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User findById(Long id) throws ServiceException {
        log.info("Request to find user by id: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(User user) {
        log.info("Request to update user by id: {}", user.getId());
        userRepository.save(user);
    }

    @Transactional
    public boolean isExist(Long id) {
        log.info("Request to find user by id: {}", id);
        User user = findById(id);
        log.info("Return result of find.");
        return user != null;
    }
}
