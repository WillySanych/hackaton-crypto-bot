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
public class CryptStateChanger {
    private final Map<Long, CryptState> cryptStateMap = new HashMap<>();

    public void saveCryptState(Long userId, CryptState cryptState) {
        log.info("Change crypt state to {}", cryptState.name());
        cryptStateMap.put(userId, cryptState);
    }
}
