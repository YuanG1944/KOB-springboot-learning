package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
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
            map.put("error_message", "You are not allowed to modify this bot");
            return map;
        }

        String title = data.get("title");
        String content = data.get("content");
        String description = data.get("description");

        Date now = new Date();
        Bot updatedBot = new Bot(bot.getId(), user.getId(), title, description, content, bot.getRating(),
                bot.getCreateTime(), now);

        botMapper.updateById(updatedBot);

        map.put("error_message", "Bot has been updated");

        return map;
    }
}
