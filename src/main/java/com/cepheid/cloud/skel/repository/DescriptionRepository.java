package com.cepheid.cloud.skel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cepheid.cloud.skel.model.Description;

/***
 * This is a DescriptionRepository class which extends from JpaRepository, and
 * provides the CRUD operation to the database.
 * 
 * @author Wei Wang
 * @version 1.0
 */
public interface DescriptionRepository extends JpaRepository<Description, Long> {

	/**
	 * Find all the Descriptions with a given content.
	 * 
	 * @param mContent Content of the description object to search.
	 * @return A collection of description objects which has the given search
	 *         content.
	 */
	List<Description> findAllBymContent(String mContent);
}
