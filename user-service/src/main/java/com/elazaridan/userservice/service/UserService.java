package com.elazaridan.userservice.service;

import com.elazaridan.userservice.dto.user.UserRequest;
import com.elazaridan.userservice.dto.user.UserResponse;
import com.elazaridan.userservice.model.User;
import com.elazaridan.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(UserRequest userRequest)
    {
        User user = User.builder().userName(userRequest.getUserName()).password(userRequest.getPassword()).isAdmin(userRequest.isAdmin()).build();
        userRepository.save(user);
        log.info("User with id {} is created",user.getId());
        return UserResponse.builder().id(user.getId()).isAdmin(user.isAdmin()).userName(user.getUserName()).build();
    }

    public List<UserResponse> getAllUsers()
    {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .userName(user.getUserName())
                        .id(user.getId())
                        .isAdmin(user.isAdmin())
                        .build()).toList();
    }

    public UserResponse getUser(String id) {
        User user = userRepository.findById(id).get();
        return UserResponse.builder().id(user.getId()).userName(user.getUserName()).isAdmin(user.isAdmin()).build();
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
