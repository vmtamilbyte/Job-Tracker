package com.jobtracker;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    record MatchReq(String resumeText, String jobDescription) {}

    private final MatchService service;

    MatchController(MatchService service) { this.service = service; }

    @PostMapping
    MatchService.MatchResult match(@RequestBody MatchReq r) {
        return service.analyze(r.resumeText(), r.jobDescription());
    }
}
