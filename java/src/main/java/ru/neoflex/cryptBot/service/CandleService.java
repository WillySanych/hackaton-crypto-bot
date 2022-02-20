package ru.neoflex.cryptBot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import ru.neoflex.cryptBot.entity.Candle;
import ru.neoflex.cryptBot.repository.CandleRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandleService {
    private final CandleRepository candleRepository;

    @Transactional
    public Candle findById (String id) throws ServiceException {
        log.info("Request to find user bi id: {}", id);
        return candleRepository.findByFigi(id).orElseThrow(() -> new ServiceException("No such crypt with the specified ID"));
    }
}
