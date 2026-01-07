package com.iteam.service.impl;

import com.iteam.Exceptions.NotFoundEntityExceptions;
import com.iteam.Exceptions.UserAlreadyExistsExceptions;
import com.iteam.entities.User;
import com.iteam.repositories.UserRepository;
import com.iteam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {

        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsExceptions("User already exists with email : " + user.getEmail());
        }
        return userRepository.save(user);
       // return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()->new NotFoundEntityExceptions("No User present with the ID: " + id));
    }

    @Override
    public void deleteUserById(Long id) {

        Optional<User> existingUser = userRepository.findById(id);
        if(!existingUser.isPresent()){
            throw new NotFoundEntityExceptions("No User present with the ID: " + id);
        } else {
            userRepository.delete(existingUser.get());
        }




//         User deletedUser = findUserById(id);
//         userRepository.delete(deletedUser);
    }

    @Override
    public User updateUser(Long id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if(!existingUser.isPresent()) {
            throw new NotFoundEntityExceptions("No User present with the ID: " + id);
        } else {
            User updateUser = existingUser.get();
            updateUser.setFirstName(user.getFirstName());
            updateUser.setLastName(user.getLastName());
            updateUser.setEmail(user.getEmail());
            updateUser.setPhoneNumber(user.getPhoneNumber());
            return userRepository.save(updateUser);
        }
       /* User updatedUser = findUserById(id);
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        return userRepository.save(updatedUser);*/

    }
}
