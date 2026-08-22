package com.ski.ticketing.service.Impl;

import com.ski.ticketing.pojo.dto.UserLoginDTO;
import com.ski.ticketing.pojo.dto.UserRegisterDTO;
import com.ski.ticketing.pojo.dto.UserProfileUpdateDTO;
import com.ski.ticketing.pojo.dto.UsernameUpdateDTO;
import com.ski.ticketing.pojo.dto.PasswordUpdateDTO;
import com.ski.ticketing.pojo.entity.User;
import com.ski.ticketing.service.UserService;
import com.ski.ticketing.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getById(Long id) {
        return userMapper.getById(id);
    }

    @Override
    public User login(UserLoginDTO dto) {
        User user = userMapper.getByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        return user;
    }

    @Override
    public User register(UserRegisterDTO dto) {
        User existing = userMapper.getByUsername(dto.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        
        userMapper.insert(user);
        return user;
    }

    @Override
    public User updateProfile(Long id, UserProfileUpdateDTO dto) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setName(dto.getName().trim());
        user.setPhone(dto.getPhone().trim());
        userMapper.updateProfile(user);
        return getById(id);
    }

    @Override
    public User updateAvatar(Long id, String avatarUrl) {
        User user = requireUser(id);
        user.setAvatar(avatarUrl);
        userMapper.updateAvatar(user);
        return getById(id);
    }

    @Override
    public User updateUsername(Long id, UsernameUpdateDTO dto) {
        User user = requireUser(id);
        String username = dto.getUsername().trim();
        if (username.equals(user.getUsername())) {
            throw new RuntimeException("新用户名不能与当前用户名相同");
        }
        ensureMonthlyChangeAllowed(user.getUsernameUpdateTime(), "用户名");
        User existing = userMapper.getByUsername(username);
        if (existing != null && !existing.getId().equals(user.getId())) {
            throw new RuntimeException("用户名已存在");
        }
        user.setUsername(username);
        userMapper.updateUsername(user);
        return getById(id);
    }

    @Override
    public User updatePassword(Long id, PasswordUpdateDTO dto) {
        User user = requireUser(id);
        String currentPassword = DigestUtils.md5DigestAsHex(dto.getCurrentPassword().getBytes());
        if (!currentPassword.equals(user.getPassword())) {
            throw new RuntimeException("当前密码不正确");
        }
        if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
            throw new RuntimeException("新密码不能与当前密码相同");
        }
        ensureMonthlyChangeAllowed(user.getPasswordUpdateTime(), "密码");
        user.setPassword(DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes()));
        userMapper.updatePassword(user);
        return getById(id);
    }

    private User requireUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    private void ensureMonthlyChangeAllowed(LocalDateTime lastUpdateTime, String fieldName) {
        if (lastUpdateTime == null) {
            return;
        }
        LocalDateTime nextUpdateTime = lastUpdateTime.plusMonths(1);
        if (LocalDateTime.now().isBefore(nextUpdateTime)) {
            String time = nextUpdateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"));
            throw new RuntimeException(fieldName + "每月仅可修改一次，下次可修改时间：" + time);
        }
    }
}
