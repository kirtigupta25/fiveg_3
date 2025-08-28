package com.lib.fiveg.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobPost job;

    @ManyToOne
    private User seeker; //by defalut the jpa wil give the name to the foreign key

    @Column(nullable = false)
    private String status; // APPLIED, ACCEPTED, REJECTED

    private LocalDateTime appliedAt = LocalDateTime.now();

    public Application() {}

    public Long getId() {
        return id;
    }

    public JobPost getJob() {
        return job;
    }

    public User getSeeker() {
        return seeker;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setJob(JobPost job) {
        this.job = job;
    }

    public void setSeeker(User seeker) {
        this.seeker = seeker;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}
