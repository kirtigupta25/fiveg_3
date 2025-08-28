package com.lib.fiveg.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.lib.fiveg.entity.Application;
import com.lib.fiveg.entity.JobPost;
import com.lib.fiveg.entity.User;
import com.lib.fiveg.repository.ApplicationRepository;
import com.lib.fiveg.repository.JobPostRepository;

import jakarta.transaction.Transactional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobPostRepository jobPostRepository;

    // ✅ Apply to job → new application gets cached
    // Cache single app, evict seeker/recruiter lists
    @Transactional
    @CachePut(value = "application", key = "#result.id")
    @CacheEvict(value = { "applicationsBySeeker", "applicationsByRecruiter" }, allEntries = true)
    public Application applyToJob(Long jobId, User seeker) {
        JobPost job = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Optional<Application> existing = applicationRepository.findByJobAndSeeker(job, seeker);
        if (existing.isPresent()) {
            throw new RuntimeException("Already applied to this job");
        }

        Application app = new Application();
        app.setJob(job);
        app.setSeeker(seeker);
        app.setStatus("APPLIED");
        app.setAppliedAt(LocalDateTime.now());

        return applicationRepository.save(app);
    }

    // ✅ Cache applications for a seeker
    @Cacheable(value = "applicationsBySeeker", key = "#seeker.id")
    public List<Application> getApplicationsForSeeker(User seeker) {
        return applicationRepository.findBySeeker(seeker);
    }

    // ✅ Cache applications for a recruiter
    @Cacheable(value = "applicationsByRecruiter", key = "#recruiter.id")
    public List<Application> getApplicationsForRecruiter(User recruiter) {
        return applicationRepository.findByJob_Recruiter(recruiter);
    }

    // ✅ Update application status → refresh single app, evict seeker/recruiter lists
    @Transactional
    @CachePut(value = "application", key = "#appId")
    @CacheEvict(value = { "applicationsBySeeker", "applicationsByRecruiter" }, allEntries = true)
    public Application updateApplicationStatus(Long appId, String status, User recruiter) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!app.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("You are not allowed to update this application");
        }

        app.setStatus(status.toUpperCase());
        return applicationRepository.save(app);
    }
}
