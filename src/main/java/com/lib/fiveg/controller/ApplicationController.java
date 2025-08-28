package com.lib.fiveg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lib.fiveg.entity.Application;
import com.lib.fiveg.entity.User;
import com.lib.fiveg.repository.UserRepository;
import com.lib.fiveg.service.ApplicationService;
import com.lib.fiveg.util.SecurityUtils;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserRepository userRepository;

    // 1. Apply to job
    @PostMapping("/job/{jobId}")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<?> apply(@PathVariable Long jobId) {
    	String username = SecurityUtils.getCurrentUsername();
    	User seeker = userRepository.findByUsername(username)
    	    .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Application application = applicationService.applyToJob(jobId, seeker);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Seeker: View own applications
    @GetMapping("/my")
    @PreAuthorize("hasRole('SEEKER')")
    public ResponseEntity<?> getOwnApplications() {
    	String username = SecurityUtils.getCurrentUsername();
    	User seeker = userRepository.findByUsername(username)
    	    .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(applicationService.getApplicationsForSeeker(seeker));
    }
   // 3. Recruiter: View all applications to their jobs
    @GetMapping("/received")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getReceivedApplications() {
    	String username = SecurityUtils.getCurrentUsername();
    	User recruiter = userRepository.findByUsername(username)
    	    .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        return ResponseEntity.ok(applicationService.getApplicationsForRecruiter(recruiter));
    }

    // 4. Recruiter: Update application status
    @PutMapping("/{appId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> updateStatus(@PathVariable Long appId, @RequestParam String status) {
    	String username = SecurityUtils.getCurrentUsername();
    	User recruiter = userRepository.findByUsername(username)
    	    .orElseThrow(() -> new RuntimeException("Recruiter not found"));
        try {
            Application updated = applicationService.updateApplicationStatus(appId, status, recruiter);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
