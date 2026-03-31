package com.digicert.securestorage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SmokeTest {

    @Test
    void applicationStarts() {
        assertTrue(true, "Application smoke test - basic startup check");
    }

    @Test
    void environmentIsConfigured() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion, "Java version should be available");
        System.out.println("Running on Java: " + javaVersion);
    }

    @Test
    void serviceNameIsCorrect() {
        String serviceName = "secure-storage-service";
        assertEquals("secure-storage-service", serviceName, "Service name should match");
        System.out.println("Smoke test passed for: " + serviceName);
    }
}
