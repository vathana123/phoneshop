package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository repository;

    private RefreshToken createToken(String token) {
        return repository.save(
                RefreshToken.builder()
                        .token(token)
                        .expiresAt(LocalDateTime.now().plusDays(1))
                        .build()
        );
    }

    @Test
    @DisplayName("Should find refresh token by token")
    void shouldFindByToken() {
        // given
        RefreshToken saved = createToken("abc123");

        // when
        Optional<RefreshToken> result = repository.findByToken("abc123");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Should return empty when token not found")
    void shouldReturnEmpty_whenTokenNotFound() {
        // when
        Optional<RefreshToken> result = repository.findByToken("not_exist");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should not return revoked token due to SQLRestriction")
    void shouldNotReturnRevokedToken() {
        // given
        RefreshToken token = createToken("revoked-token");

        token.setRevokedAt(LocalDateTime.now());
        repository.save(token);

        // when
        Optional<RefreshToken> result = repository.findByToken("revoked-token");

        // then
        assertThat(result).isEmpty(); // 🔥 filtered by @SQLRestriction
    }

    @Test
    @DisplayName("Should enforce unique token constraint")
    void shouldNotAllowDuplicateToken() {
        // given
        createToken("duplicate-token");

        // expect
        assertThatThrownBy(() ->
                createToken("duplicate-token")
        ).isInstanceOf(Exception.class);
    }
}