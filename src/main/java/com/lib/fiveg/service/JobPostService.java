package com.lib.fiveg.service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lib.fiveg.entity.JobPost;
import com.lib.fiveg.entity.User;
import com.lib.fiveg.repository.JobPostRepository;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
@Service
public class JobPostService {

    @Autowired
    private JobPostRepository jobPostRepository;

    @CacheEvict(value = "jobs", allEntries = true)
    public JobPost saveJob(JobPost jobPost) {
    	jobPost.setCreatedAt(LocalDateTime.now());
        return jobPostRepository.save(jobPost);
    }

    @Cacheable(value = "jobs")
    public List<JobPost> getAllJobs() {
        return jobPostRepository.findAll();
    }

    @Cacheable(value = "job", key = "#id")
    public Optional<JobPost> getJobById(Long id) {
        return jobPostRepository.findById(id);
    }

    @CacheEvict(value = {"jobs", "job"}, allEntries = true)
    public void deleteJob(Long id) {
        jobPostRepository.deleteById(id);
    }
    
    @Transactional
    @CachePut(value = "job", key = "#jobId") // job and jobs are two different redis keys
    @CacheEvict(value = "jobs", allEntries = true)
    public Optional<JobPost> updateJobPost(Long jobId, JobPost updatedJob, User currentRecruiter) {
        Optional<JobPost> existingJobOpt = jobPostRepository.findById(jobId);

        if (existingJobOpt.isPresent()) {
            JobPost existingJob = existingJobOpt.get();

            // Make sure only the recruiter who created it can update
            if (!existingJob.getRecruiter().getId().equals(currentRecruiter.getId())) {
                return Optional.empty(); // Unauthorized attempt
            }

            existingJob.setTitle(updatedJob.getTitle());
            existingJob.setDescription(updatedJob.getDescription());
            existingJob.setLocation(updatedJob.getLocation());
            existingJob.setCompany(updatedJob.getCompany());

            jobPostRepository.save(existingJob);
            return Optional.of(existingJob);
        }

        return Optional.empty(); // Job not found
    }

    @Transactional
    @CacheEvict(value = {"jobs", "job"}, allEntries = true)
    public boolean deleteJobPost(Long jobId, User recruiter) {
        Optional<JobPost> optionalJob = jobPostRepository.findById(jobId);

        if (optionalJob.isPresent()) {
            JobPost job = optionalJob.get();
            if (job.getRecruiter().getId().equals(recruiter.getId())) {
                jobPostRepository.delete(job);
                return true;
            }
        }
        return false;
    }
    
    public List<JobPost> searchJobs(String title, String location, String company) {
        return jobPostRepository.findAll().stream()
            .filter(job -> title == null || job.getTitle().toLowerCase().contains(title.toLowerCase()))
            .filter(job -> location == null || job.getLocation().toLowerCase().contains(location.toLowerCase()))
            .filter(job -> company == null || job.getCompany().toLowerCase().contains(company.toLowerCase()))
            .collect(Collectors.toList());
    }
    @Cacheable(value = "jobsByRecruiter", key = "#recruiter.id")//recruiter.id access id of the recruiter object
    public List<JobPost> getJobsByRecruiter(User recruiter) {
        System.out.println("Fetching Recruiter from DB...");
        return jobPostRepository.findByRecruiter(recruiter);
    }

}
