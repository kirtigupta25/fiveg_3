package com.lib.fiveg.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lib.fiveg.entity.Role;
import com.lib.fiveg.repository.RoleRepository;

import java.util.Arrays;
import java.util.List;

@Configuration
public class RoleSeeder {

    @Bean
    public CommandLineRunner run(RoleRepository roleRepository) { //rolerepository help us to interact with database and check if the role already exist or not
        return args -> {
            List<String> roles = Arrays.asList("ADMIN", "RECRUITER", "SEEKER");

            for (String roleName : roles) {
                if (!roleRepository.existsByName(roleName)) {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                    System.out.println("Inserted role: " + roleName);
                }
            }
        };
    }
}
