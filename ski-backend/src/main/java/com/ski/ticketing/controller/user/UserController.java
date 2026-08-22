package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.dto.UserLoginDTO;
import com.ski.ticketing.pojo.dto.UserRegisterDTO;
import com.ski.ticketing.pojo.dto.UserProfileUpdateDTO;
import com.ski.ticketing.pojo.dto.UsernameUpdateDTO;
import com.ski.ticketing.pojo.dto.PasswordUpdateDTO;
import com.ski.ticketing.pojo.entity.User;
import com.ski.ticketing.pojo.vo.UserLoginVO;
import com.ski.ticketing.properties.JwtProperties;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.UserService;
import com.ski.ticketing.util.JwtUtil;
import com.ski.ticketing.utils.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/user/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private AliOSSUtil aliOSSUtil;

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        log.info("用户登录：{}", dto.getUsername());

        User user = userService.login(dto);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(Long.valueOf(user.getId()))
                .userName(user.getUsername())
                .name(user.getName())
                .avatar(user.getAvatar())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }

    @GetMapping("/profile")
    public Result<User> profile(Authentication authentication) {
        User user = userService.getById((Long) authentication.getPrincipal());
        if (user == null) return Result.error("用户不存在");
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(Authentication authentication, @Valid @RequestBody UserProfileUpdateDTO dto) {
        User user = userService.updateProfile((Long) authentication.getPrincipal(), dto);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/profile/avatar")
    public Result<User> updateAvatar(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error("请选择头像图片");
        if (file.getSize() > 2 * 1024 * 1024) return Result.error("头像图片不能超过2MB");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return Result.error("只能上传图片文件");

        String url = aliOSSUtil.upload(file);
        User user = userService.updateAvatar((Long) authentication.getPrincipal(), url);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile/username")
    public Result<User> updateUsername(Authentication authentication, @Valid @RequestBody UsernameUpdateDTO dto) {
        User user = userService.updateUsername((Long) authentication.getPrincipal(), dto);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile/password")
    public Result<User> updatePassword(Authentication authentication, @Valid @RequestBody PasswordUpdateDTO dto) {
        User user = userService.updatePassword((Long) authentication.getPrincipal(), dto);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody UserRegisterDTO dto) {
        log.info("用户注册：{}", dto.getUsername());
        User user = userService.register(dto);
        user.setPassword(null);
        return Result.success(user);
    }
}
