package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.Role;
import com.backend.phoneshop.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // -------------------------
    // Helper
    // -------------------------
    private User createUser(String username) {
        Role role = roleRepository.save(
                Role.builder().name("USER").build()
        );

        return userRepository.save(
                User.builder()
                        .name("Vathana")
                        .username(username)
                        .password("123456")
                        .roles(Set.of(role))
                        .build()
        );
    }

    // -------------------------
    // TESTS
    // -------------------------

    @Test
    @DisplayName("Should return true when username exists")
    void shouldReturnTrue_whenExistsByUsername() {
        // given
        createUser("vathana");

        // when
        boolean exists = userRepository.existsByUsername("vathana");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when username does not exist")
    void shouldReturnFalse_whenUsernameNotExists() {
        // when
        boolean exists = userRepository.existsByUsername("unknown");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindByUsername() {
        // given
        User user = createUser("admin");

        // when
        Optional<User> result = userRepository.findByUsername("admin");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void shouldReturnEmpty_whenFindByUsername() {
        // when
        Optional<User> result = userRepository.findByUsername("not_found");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should not return soft deleted user")
    void shouldNotReturnDeletedUser() {
        // given
        User user = createUser("deleted_user");

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByUsername("deleted_user");
        boolean exists = userRepository.existsByUsername("deleted_user");

        // then
        assertThat(result).isEmpty();      // 🔥 filtered by SQLRestriction
        assertThat(exists).isFalse();      // 🔥 also filtered
    }
}