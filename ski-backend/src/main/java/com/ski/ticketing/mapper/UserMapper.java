package com.ski.ticketing.mapper;

import com.ski.ticketing.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User getById(Long id);

    User getByUsername(String username);

    int insert(User user);

    int updateProfile(User user);

    int updateAvatar(User user);

    int updateUsername(User user);

    int updatePassword(User user);
}
