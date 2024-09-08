package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    private BotMapper botMapper;

    @Data
    private class ErrorMsg {
        Boolean isValid = false;
        String msg = "";
    }


    @Override
    public Map<String, String> add(Map<String, String> params) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String title = params.get("title");
        String content = params.get("content");
        String description = params.get("description");

        Map<String, String> map = new HashMap<>();


        if (description.isEmpty()) {
            description = "这个用户很懒，什么也没留下~";
        }

        ErrorMsg errorMsg = checkValid(params);

        if (!errorMsg.isValid) {
            map.put("error_message", errorMsg.msg);
            return map;
        }


        Date now = new Date();

        Bot bot = new Bot(null, user.getId(), title, description, content, now, now);

        botMapper.insert(bot);

        map.put("error_message", "success");

        return map;
    }


    private ErrorMsg checkValid(Map<String, String> params) {
        ErrorMsg errorMsg = new ErrorMsg();

        String title = params.get("title");
        String content = params.get("content");
        String description = params.get("description");

        if (title.isEmpty()) {
            errorMsg.isValid = false;
            errorMsg.msg = "title is empty";
            return errorMsg;
        }

        if (content.isEmpty()) {
            errorMsg.isValid = false;
            errorMsg.msg = "content is empty";
            return errorMsg;
        }

        if (title.length() > 100) {
            errorMsg.isValid = false;
            errorMsg.msg = "title is longer than 100 characters";
            return errorMsg;
        }
        if (content.length() > 20000) {
            errorMsg.isValid = false;
            errorMsg.msg = "code is longer than 20000 characters";
            return errorMsg;
        }
        if (description.length() > 300) {
            errorMsg.isValid = false;
            errorMsg.msg = "description is longer than 300 characters";
            return errorMsg;
        }

        errorMsg.isValid = true;

        return errorMsg;
    }

}
