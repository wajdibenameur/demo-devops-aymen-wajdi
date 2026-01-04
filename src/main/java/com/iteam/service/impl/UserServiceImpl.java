package com.iteam.service.impl;

import com.iteam.entities.User;
import com.iteam.repositories.UserRepository;
import com.iteam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    @Override
    public void deleteUserById(Long id) {
         User deletedUser = findUserById(id);
         userRepository.delete(deletedUser);
    }

    @Override
    public User updateUser(Long id, User user) {
        User updatedUser = findUserById(id);
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        return userRepository.save(updatedUser);
    }
}
