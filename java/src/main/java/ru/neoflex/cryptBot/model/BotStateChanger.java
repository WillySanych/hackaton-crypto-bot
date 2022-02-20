package ru.neoflex.cryptBot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Setter
@Getter
@Slf4j
public class BotStateChanger {
    private final Map<Long, BotState> botStateMap = new HashMap<>();

    public void saveBotState(Long userId, BotState botState) {
        log.info("Change bot state to {}", botState.name());
        botStateMap.put(userId, botState);
    }
}
