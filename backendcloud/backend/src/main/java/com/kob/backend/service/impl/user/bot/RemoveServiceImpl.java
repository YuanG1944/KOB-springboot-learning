package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Map<String, String> map = new HashMap<>();

        String botIdStr = data.get("bot_id");

        if (botIdStr.isEmpty()) {
            map.put("error_message", "You must provide a botId");
            return map;
        }

        int botId = Integer.parseInt(botIdStr);

        Bot bot = botMapper.selectById(botId);

        if (bot == null) {
            map.put("error_message", "This bot does not exist");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("error_message", "You are not allowed to delete this bot");
            return map;
        }

        botMapper.deleteById(botId);

        map.put("error_message", "You have been deleted");

        return map;
    }
}
