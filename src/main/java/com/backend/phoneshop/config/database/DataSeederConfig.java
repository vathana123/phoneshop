package com.backend.phoneshop.config.database;

import com.backend.phoneshop.dto.data.UserInputDto;
import com.backend.phoneshop.entity.Permission;
import com.backend.phoneshop.repository.PermissionRepository;
import com.backend.phoneshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataSeederConfig {

    private final PermissionRepository repo;
    private final UserService userService;

    @Profile("dev")
    @Bean
    CommandLineRunner seedPermissions() {
        return args -> {
//            List<String> recourseTargets = List.of("BRAND", "CATEGORY", "CATEGORY_TYPE", "PRODUCT", "ROLE");
//            for (String recourseTarget :  recourseTargets) {
//
//                List<String> permissions = List.of(
//                        recourseTarget+ "_READ",
//                        recourseTarget+ "_CREATE",
//                        recourseTarget+ "_UPDATE",
//                        recourseTarget+ "_DELETE"
//                );
//
//                for (String name : permissions) {
//                    if (!repo.existsByName(name)) {
//                        Permission p = new Permission();
//                        p.setName(name);
//                        p.setRecourseTarget(recourseTarget);
//                        repo.save(p);
//                    }
//                }
//            }
//            System.out.println(userService.save(UserInputDto.builder().name("Admin").username("admin").roles(Set.of(1L)).build()));
        };
    }
}