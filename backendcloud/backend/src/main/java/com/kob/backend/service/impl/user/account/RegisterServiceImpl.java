package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmPassword) {
        Map<String, String> map = new HashMap<String, String>();
        if (username == null) {
            map.put("error_message", "username is null");
            return map;
        }
        if (password == null) {
            map.put("error_message", "password is null");
            return map;
        }
        if (confirmPassword == null) {
            map.put("error_message", "confirmPassword is null");
            return map;
        }

        username = username.trim();

        if (username.isEmpty()) {
            map.put("error_message", "username is empty");
            return map;
        }

        if (username.length() > 100) {
            map.put("error_message", "username too long");
            return map;
        }

        if (password.length() > 100 || confirmPassword.length() > 100) {
            map.put("error_message", "password too long");
            return map;
        }

        if (!password.equals(confirmPassword)) {
            map.put("error_message", "passwords do not match");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()) {
            map.put("error_message", "username already exist");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/41057_lg_5b25efa0af.jpg";

        User user = new User(null, username, encodedPassword, photo, 1500);
        userMapper.insert(user);

        map.put("success_message", "user registered successfully");

        return map;
    }
}
