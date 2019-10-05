package com.cepheid.cloud.skel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cepheid.cloud.skel.model.Description;

public interface DescriptionRepository extends JpaRepository<Description, Long> {
	
	// Define some query methods.
	List<Description> findAllBymContent(String mContent);
}
