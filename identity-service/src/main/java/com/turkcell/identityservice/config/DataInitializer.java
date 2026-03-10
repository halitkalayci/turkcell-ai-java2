package com.turkcell.identityservice.config;

import com.turkcell.identityservice.entity.Role;
import com.turkcell.identityservice.entity.User;
import com.turkcell.identityservice.repository.RoleRepository;
import com.turkcell.identityservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role roleAdmin = new Role("ROLE_ADMIN", "Administrator role");
                Role roleUser = new Role("ROLE_USER", "Regular user role");
                
                roleRepository.save(roleAdmin);
                roleRepository.save(roleUser);

                User admin = new User(
                        "admin",
                        "admin@turkcell.com",
                        passwordEncoder.encode("Admin123!")
                );
                admin.addRole(roleAdmin);
                admin.addRole(roleUser);
                userRepository.save(admin);

                User user = new User(
                        "user",
                        "user@turkcell.com",
                        passwordEncoder.encode("User123!")
                );
                user.addRole(roleUser);
                userRepository.save(user);

                System.out.println("=== Initial Data Created ===");
                System.out.println("Admin user: admin / Admin123!");
                System.out.println("Regular user: user / User123!");
                System.out.println("============================");
            }
        };
    }
}
