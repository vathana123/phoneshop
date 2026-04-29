package com.backend.phoneshop.config.security;

import com.backend.phoneshop.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestSecurityHandlersTest {

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @BeforeEach
    void setUp() {
        restAuthenticationEntryPoint = new RestAuthenticationEntryPoint(handlerExceptionResolver);
        restAccessDeniedHandler = new RestAccessDeniedHandler(handlerExceptionResolver);
    }

    @Test
    void shouldReturnUnauthorizedMessageForAuthenticationFailures() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/brands");
        MockHttpServletResponse response = new MockHttpServletResponse();

        restAuthenticationEntryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException("Authentication required")
        );

        verify(handlerExceptionResolver).resolveException(
                eq(request),
                eq(response),
                isNull(),
                argThat(ex -> ex instanceof ApiException apiException
                        && apiException.getStatus() == HttpStatus.UNAUTHORIZED
                        && "Authentication token is required."
                        .equals(apiException.getMessage()))
        );
    }

    @Test
    void shouldReturnForbiddenMessageForAuthorizationFailures() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/brands");
        MockHttpServletResponse response = new MockHttpServletResponse();

        restAccessDeniedHandler.handle(
                request,
                response,
                new AccessDeniedException("Forbidden")
        );

        verify(handlerExceptionResolver).resolveException(
                eq(request),
                eq(response),
                isNull(),
                argThat(ex -> ex instanceof ApiException apiException
                        && apiException.getStatus() == HttpStatus.FORBIDDEN
                        && "Your account does not have permission to access this resource."
                        .equals(apiException.getMessage()))
        );
    }
}
