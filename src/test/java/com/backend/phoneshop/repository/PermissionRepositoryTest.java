package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.Permission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    @DisplayName("Should return true when permission exists by name")
    void shouldReturnTrue_whenPermissionExists() {
        // given
        Permission permission = Permission.builder()
                .name("READ_USER")
                .recourseTarget("USER")
                .build();

        permissionRepository.save(permission);

        // when
        boolean exists = permissionRepository.existsByName("READ_USER");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when permission does not exist")
    void shouldReturnFalse_whenPermissionDoesNotExist() {
        // when
        boolean exists = permissionRepository.existsByName("WRITE_USER");

        // then
        assertThat(exists).isFalse();
    }
}