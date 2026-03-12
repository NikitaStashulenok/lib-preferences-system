package com.example.library.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DemoUsersBootstrapTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DemoUsersBootstrap demoUsersBootstrap;

    @Test
    void run_doesNotOverwriteExistingDemoUsers() {
        ReflectionTestUtils.setField(demoUsersBootstrap, "seedEnabled", true);
        ReflectionTestUtils.setField(demoUsersBootstrap, "demoPassword", "demo-pass");

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new User()));

        demoUsersBootstrap.run();

        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
