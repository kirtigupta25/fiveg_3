package com.lib.fiveg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lib.fiveg.entity.Application;
import com.lib.fiveg.entity.JobPost;
import com.lib.fiveg.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
	Optional<Application> findByJobAndSeeker(JobPost job, User seeker);
    List<Application> findBySeeker(User seeker);
    List<Application> findByJob(JobPost job);
    List<Application> findByJob_Recruiter(User recruiter);
    
    // For recruiter: all applications for jobs they posted
    @Query("SELECT a FROM Application a WHERE a.job.recruiter.id = :recruiterId")
    List<Application> findAllByRecruiterId(@Param("recruiterId") Long recruiterId);

    // For seeker: all applications submitted by them
    List<Application> findAllBySeekerId(Long seekerId);
}
