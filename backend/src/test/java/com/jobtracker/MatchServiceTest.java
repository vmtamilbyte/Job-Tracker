package com.jobtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MatchServiceTest {
    private final MatchService service = new MatchService();

    @Test
    void scoresOverlapAndListsMissingSkills() {
        var r = service.analyze("Java, Spring Boot and MySQL developer", "Need Java, Spring Boot, Docker, Kafka");
        assertEquals(50, r.score());
        assertTrue(r.matched().contains("java"));
        assertTrue(r.missing().contains("docker"));
        assertTrue(r.missing().contains("kafka"));
    }

    @Test
    void returnsZeroWhenJobDescriptionHasNoKnownSkills() {
        assertEquals(0, service.analyze("Java", "We value teamwork").score());
    }
}
