package com.iteam.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DiagnosticTest {

    @Test
    void simpleTest() {
        System.out.println("Test de diagnostic - ça fonctionne !");
        assertThat(1 + 1).isEqualTo(2);
    }
}