package io.github.devasterisk.springboot.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import io.github.devasterisk.springboot.api.entity.User;
import io.github.devasterisk.springboot.api.repository.UserRepository;
import io.github.devasterisk.springboot.api.service.UserService;

/**
 * @author Justin Park (aka.asterisk@gmail.com)
 * @since 2018-10-10
 */
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 가입
    @Override
    public User join(String username, String password) {
        return userRepository.save(new User(username, password));
    }

    // 인증 & 개인정보 조회
    @Override
    public User authentication(String token) {
        // authorization으로부터 type과 credential을 분리
        String[] split = token.split(" ");
        String type = split[0];
        String credential = split[1];

        User user = null;

        if ("Basic".equalsIgnoreCase(type)) {
            // credential을 디코딩하여 username과 password를 분리
            String decoded = new String(Base64Utils.decodeFromString(credential));
            String[] usernameAndPassword = decoded.split(":");

            user = userRepository.findByUsernameAndPassword(usernameAndPassword[0], usernameAndPassword[1]);
        }
        return user;
    }

    // 비밀번호 업데이트
    @Override
    public User updatePassword(String token, String password) {
        User user = this.authentication(token);
        user.setPassword(password);
        return userRepository.save(user);
    }

    // 탈퇴
    @Override
    public void withdraw(String token) {
        User user = this.authentication(token);
        userRepository.delete(user);
    }
}
