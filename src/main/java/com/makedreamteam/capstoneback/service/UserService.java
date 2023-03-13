package com.makedreamteam.capstoneback.service;

import com.makedreamteam.capstoneback.domain.User;
import com.makedreamteam.capstoneback.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}
    public User join(User user){
        // 중복제거 포함
        validateDuplicateUser(user);
        userRepository.save(user);
        return user;
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByName(user.getName())
                .ifPresent(m ->{
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    public List<User> findMembers(){
        return userRepository.findAll();
    }
    public Optional<User> findOne(Long userId){
        return userRepository.findById(userId);
    }
}
