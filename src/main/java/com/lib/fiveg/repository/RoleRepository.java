package com.lib.fiveg.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lib.fiveg.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
	boolean existsByName(String name);
	Optional<Role>findByName(String name);
}
