package com.capitalcity.site.web;

import com.capitalcity.site.domain.JobApplication;
import com.capitalcity.site.dto.JobApplicationPayload;
import com.capitalcity.site.dto.JobPostingResponse;
import com.capitalcity.site.dto.SubmissionAck;
import com.capitalcity.site.service.CareerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
public class CareerController {

    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @GetMapping("/postings")
    public List<JobPostingResponse> postings() {
        return careerService.listOpenPostings();
    }

    @PostMapping("/applications")
    public ResponseEntity<SubmissionAck> apply(@Valid @RequestBody JobApplicationPayload payload) {
        JobApplication saved = careerService.apply(payload);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SubmissionAck(saved.getId(), "Application received."));
    }
}
