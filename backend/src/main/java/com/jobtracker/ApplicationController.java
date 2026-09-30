package com.jobtracker;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    record AppReq(String company, String role, JobApplication.Status status, String jobDescription,
                  Integer matchScore, String notes, LocalDate followUpOn) {}
    record StatusReq(JobApplication.Status status) {}

    private final JobApplicationRepository repo;

    ApplicationController(JobApplicationRepository repo) { this.repo = repo; }

    @GetMapping
    List<JobApplication> list(@AuthenticationPrincipal Long userId) {
        return repo.findByUserIdOrderByIdDesc(userId);
    }

    @PostMapping
    JobApplication create(@AuthenticationPrincipal Long userId, @RequestBody AppReq r) {
        JobApplication a = new JobApplication();
        a.setUserId(userId);
        return repo.save(copy(a, r));
    }

    @PutMapping("/{id}")
    JobApplication update(@AuthenticationPrincipal Long userId, @PathVariable Long id, @RequestBody AppReq r) {
        return repo.save(copy(find(userId, id), r));
    }

    @PatchMapping("/{id}/status")
    JobApplication move(@AuthenticationPrincipal Long userId, @PathVariable Long id, @RequestBody StatusReq r) {
        JobApplication a = find(userId, id);
        a.setStatus(r.status());
        return repo.save(a);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        repo.delete(find(userId, id));
    }

    private JobApplication find(Long userId, Long id) {
        return repo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private JobApplication copy(JobApplication a, AppReq r) {
        if (r.company() == null || r.company().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company is required");
        a.setCompany(r.company());
        a.setRole(r.role());
        if (r.status() != null) a.setStatus(r.status());
        a.setJobDescription(r.jobDescription());
        a.setMatchScore(r.matchScore());
        a.setNotes(r.notes());
        a.setFollowUpOn(r.followUpOn());
        return a;
    }
}
