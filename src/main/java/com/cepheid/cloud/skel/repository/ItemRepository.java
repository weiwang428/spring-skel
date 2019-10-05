package com.cepheid.cloud.skel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cepheid.cloud.skel.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	// Define some query methods.
	Optional<List<Item>> findAllBymName(String mName);
}
