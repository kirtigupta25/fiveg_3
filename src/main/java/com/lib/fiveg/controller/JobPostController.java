package com.lib.fiveg.controller;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lib.fiveg.entity.JobPost;
import com.lib.fiveg.entity.User;
import com.lib.fiveg.repository.UserRepository;
import com.lib.fiveg.service.JobPostService;
import com.lib.fiveg.util.SecurityUtils;

@RestController
@RequestMapping("/api/jobs")
public class JobPostController {

    @Autowired
    private JobPostService jobPostService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> postJob(@RequestBody JobPost jobPost) { //allow us to return http response(full control including header,status , code
        String username = SecurityUtils.getCurrentUsername();
        User recruiter = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        jobPost.setRecruiter(recruiter);
        return ResponseEntity.ok(jobPostService.saveJob(jobPost));
    }

    @GetMapping
    public ResponseEntity<?> getAllJobs() {
        return ResponseEntity.ok(jobPostService.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPost> getJobById(@PathVariable Long id) {
        return jobPostService.getJobById(id)
                .map(job -> ResponseEntity.ok().body(job))
                .orElseGet(() -> ResponseEntity.status(404).build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String company
    ) {
        return ResponseEntity.ok(jobPostService.searchJobs(title, location, company));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
    	String username = SecurityUtils.getCurrentUsername();
    	User recruiter = userRepository.findByUsername(username)
    	    .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        boolean deleted = jobPostService.deleteJobPost(id, recruiter);

        if (deleted) {
            return ResponseEntity.ok("Job deleted successfully");
        } else {
            return ResponseEntity.status(403).body("Not allowed to delete this job or job not found");
        }
    }

    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobPost> updateJob(@PathVariable Long id, @RequestBody JobPost jobPost) {
    	String username = SecurityUtils.getCurrentUsername();
    	User recruiter = userRepository.findByUsername(username)
    	    .orElseThrow(() -> new RuntimeException("Recruiter not found"));


        JobPost updatedJob = jobPostService.updateJobPost(id, jobPost, recruiter)
            .orElseThrow(() -> new RuntimeException("Not allowed to update this job or job not found"));

        return ResponseEntity.ok(updatedJob);
    }
    
    @GetMapping("/my-posts")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getMyPostedJobs() {
        String username = SecurityUtils.getCurrentUsername();
        User recruiter = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        return ResponseEntity.ok(jobPostService.getJobsByRecruiter(recruiter));
    }

}
