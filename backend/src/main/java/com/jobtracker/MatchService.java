package com.jobtracker;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
    public record MatchResult(int score, List<String> matched, List<String> missing) {}

    private static final List<String> SKILLS = List.of(
        "java", "spring boot", "spring framework", "hibernate", "jpa", "maven", "gradle", "junit", "mockito",
        "microservices", "rest", "graphql", "kafka", "rabbitmq", "redis", "sql", "mysql", "postgresql",
        "mongodb", "docker", "kubernetes", "aws", "azure", "gcp", "ci/cd", "jenkins", "github actions",
        "git", "linux", "javascript", "typescript", "react", "angular", "vue", "node.js", "html", "css",
        "python", "c++", "c#", "spring security", "jwt", "oauth", "agile", "scrum", "design patterns",
        "data structures", "algorithms", "oop", "multithreading", "unit testing", "testcontainers");

    public MatchResult analyze(String resume, String jd) {
        String r = resume == null ? "" : resume.toLowerCase();
        String j = jd == null ? "" : jd.toLowerCase();
        List<String> matched = new ArrayList<>(), missing = new ArrayList<>();
        for (String s : SKILLS) {
            if (!has(j, s)) continue;
            (has(r, s) ? matched : missing).add(s);
        }
        int total = matched.size() + missing.size();
        return new MatchResult(total == 0 ? 0 : Math.round(100f * matched.size() / total), matched, missing);
    }

    private static boolean has(String text, String skill) {
        return Pattern.compile("(?<![a-z0-9])" + Pattern.quote(skill) + "(?![a-z0-9])").matcher(text).find();
    }
}