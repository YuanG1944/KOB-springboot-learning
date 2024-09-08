package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public final static MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addPlayer(Integer userId, Integer rating) {
        System.out.println("add player: " + userId + " rating: " + rating);

        matchingPool.addPlayer(userId, rating);
        return "Add player successful";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("remove player: " + userId);

        matchingPool.removePlayer(userId);
        return "Remove player successful";
    }
}
