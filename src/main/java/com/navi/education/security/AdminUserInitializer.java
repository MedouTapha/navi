package com.navi.education.security;

import com.navi.education.model.entity.User;
import com.navi.education.model.enums.Role;
import com.navi.education.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crée le compte administrateur au premier démarrage si aucun utilisateur n'existe.
 * Identifiants fournis via les variables d'environnement ADMIN_USERNAME / ADMIN_PASSWORD.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Un compte utilisateur existe déjà — création de l'administrateur ignorée.");
            return;
        }

        String password = adminPassword;
        if (password == null || password.isBlank()) {
            password = "admin123";
            log.warn("====================================================================");
            log.warn("  ATTENTION : ADMIN_PASSWORD non défini.");
            log.warn("  Mot de passe administrateur par défaut utilisé : 'admin123'");
            log.warn("  CHANGEZ-LE IMMÉDIATEMENT en production via la variable ADMIN_PASSWORD.");
            log.warn("====================================================================");
        }

        User admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(password))
                .fullName("مدير النظام")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userRepository.save(admin);
        log.info("Compte administrateur créé avec succès (utilisateur='{}').", adminUsername);
    }
}
