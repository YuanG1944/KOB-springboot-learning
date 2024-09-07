package com.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.soap.SOAPBinding;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    // 维护全局连接信息
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();

    private Session session = null;
    private User user;

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;

    private Game game = null;


    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        System.out.println("Connected to websocket");

        Integer userId = JwtAuthentication.getUserId(token);

        if (userId == 1) {
            this.session.close();
            return;
        }

        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            WebSocketServer.users.put(userId, this);
            return;
        }

        this.session.close();
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("Disconnected from websocket");
        if (this.user != null) {
            WebSocketServer.users.remove(this.user.getId());
            WebSocketServer.matchPool.remove(this.user);
        }
    }

    private void startMatching() {
        System.out.println("startMatching");
        WebSocketServer.matchPool.add(this.user);

        while (matchPool.size() >= 2) {
            Iterator<User> iterator = WebSocketServer.matchPool.iterator();
            User a = iterator.next(), b = iterator.next();

            WebSocketServer.matchPool.remove(a);
            WebSocketServer.matchPool.remove(b);

            Game game = new Game(13, 14, 20, a.getId(), b.getId());
            game.createMap();
            WebSocketServer.users.get(a.getId()).game = game;
            WebSocketServer.users.get(b.getId()).game = game;
            game.start();


            JSONObject respGame = new JSONObject();
            respGame.put("a_id", game.getPlayerA().getId());
            respGame.put("a_sx", game.getPlayerA().getSx());
            respGame.put("a_sy", game.getPlayerA().getSy());
            respGame.put("b_id", game.getPlayerB().getId());
            respGame.put("b_sx", game.getPlayerB().getSx());
            respGame.put("b_sy", game.getPlayerB().getSy());
            respGame.put("map", game.getG());


            JSONObject respA = new JSONObject();
            JSONObject respB = new JSONObject();

            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("gamemap", game.getG());
            respA.put("game", respGame);
            WebSocketServer.users.get(a.getId()).sendMessage(respA.toJSONString());

            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("gamemap", game.getG());
            respB.put("game", respGame);
            WebSocketServer.users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopMatching() {
        System.out.println("stopMatching");
        WebSocketServer.matchPool.remove(this.user);
    }

    private void move(int direction) {
        if (game.getPlayerA().getId().equals(user.getId())) {
            game.setNextStepA(direction);
        }
        if (game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("Received message: " + message);
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            this.startMatching();
        }
        if ("stop-matching".equals(event)) {
            this.stopMatching();
        }
        if ("move".equals(event)) {
            this.move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}