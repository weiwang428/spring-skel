package com.cepheid.cloud.skel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cepheid.cloud.skel.model.Item;

/***
 * This is a ItemRepository class which extends from JpaRepository, and provides
 * the CRUD operation to the database.
 * 
 * @author Wei Wang
 * @version 1.0
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
	/**
	 * Find all the Items with a given name.
	 * 
	 * @param mName Name of the item object to search.
	 * @return A collection of Item objects which has the given search name.
	 */
	Optional<List<Item>> findAllBymName(String mName);
}
