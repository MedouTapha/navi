package com.navi.education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerService implements CommandLineRunner {

    private final BranchService branchService;

    @Override
    public void run(String... args) {
        log.info("Initialisation des données de l'application...");
        branchService.initializeBranches();
        log.info("Initialisation terminée.");
    }
}
