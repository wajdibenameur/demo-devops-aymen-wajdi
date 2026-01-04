package com.iteam.service;

import com.iteam.entities.User;

import java.util.List;

public interface UserService {

    User createUser(User user);
    List<User> findAll();
    User findUserById(Long id);
    void deleteUserById(Long id);
    User updateUser(Long id,User user);




}
