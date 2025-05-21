package com.tpagiles.app_licencia.scheduler;

import com.tpagiles.app_licencia.repository.LicenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class LicenciaVigenciaScheduler {

    private final LicenciaRepository licenciaRepo;

    @Scheduled(cron = "0 0 0 * * *")//basicamente lo hago todos los dias a las 00:00
    @Transactional
    public void marcarVencidas() {
        licenciaRepo.deactivateExpired(LocalDate.now());
    }
}
