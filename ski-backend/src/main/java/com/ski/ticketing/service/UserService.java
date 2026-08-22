package com.ski.ticketing.service;

import com.ski.ticketing.pojo.dto.UserLoginDTO;
import com.ski.ticketing.pojo.dto.UserRegisterDTO;
import com.ski.ticketing.pojo.dto.UserProfileUpdateDTO;
import com.ski.ticketing.pojo.dto.UsernameUpdateDTO;
import com.ski.ticketing.pojo.dto.PasswordUpdateDTO;
import com.ski.ticketing.pojo.entity.User;

public interface UserService {

    User getById(Long id);

    User login(UserLoginDTO dto);

    User register(UserRegisterDTO dto);

    User updateProfile(Long id, UserProfileUpdateDTO dto);

    User updateAvatar(Long id, String avatarUrl);

    User updateUsername(Long id, UsernameUpdateDTO dto);

    User updatePassword(Long id, PasswordUpdateDTO dto);
}
