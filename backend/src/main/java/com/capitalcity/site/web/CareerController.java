package com.capitalcity.site.web;

import com.capitalcity.site.domain.JobApplication;
import com.capitalcity.site.dto.JobApplicationPayload;
import com.capitalcity.site.dto.JobPostingResponse;
import com.capitalcity.site.dto.SubmissionAck;
import com.capitalcity.site.service.CareerService;
import com.capitalcity.site.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
public class CareerController {

    private final CareerService careerService;
    private final NotificationService notifications;

    public CareerController(CareerService careerService, NotificationService notifications) {
        this.careerService = careerService;
        this.notifications = notifications;
    }

    @GetMapping("/postings")
    public List<JobPostingResponse> postings() {
        return careerService.listOpenPostings();
    }

    @PostMapping("/applications")
    public ResponseEntity<SubmissionAck> apply(@Valid @RequestBody JobApplicationPayload payload) {
        JobApplication saved = careerService.apply(payload);
        notifications.applicationReceived(saved, saved.getJobPosting().getTitle());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SubmissionAck(saved.getId(), "Application received."));
    }
}
