package com.cepheid.cloud.skel;

import java.util.stream.Stream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cepheid.cloud.skel.controller.ItemController;
import com.cepheid.cloud.skel.model.Description;
import com.cepheid.cloud.skel.model.Item;
import com.cepheid.cloud.skel.model.ItemState;
import com.cepheid.cloud.skel.repository.ItemRepository;

@SpringBootApplication(scanBasePackageClasses = { ItemController.class, SkelApplication.class })
@EnableJpaRepositories(basePackageClasses = { ItemRepository.class })
public class SkelApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkelApplication.class, args);
	}

	@Bean
	ApplicationRunner initItems(ItemRepository repository) {
		return args -> {
			Stream.of("Lord of the rings", "Hobbit", "Silmarillion", "Unfinished Tales and The History of Middle-earth")
					.forEach(name -> {
						// Create a new item to hold the initial data information.
						Item item = new Item(name, ItemState.VALID);
						// Add the description list to the new created item.
						Stream.of("This is just a test content", "This is just another test content")
								.forEach(content -> {
									Description d = new Description(content);
									item.addDescription(d);
								});
						// Save the new created item.
						repository.save(item);
					});
			repository.findAll().forEach(System.out::println);
		};
	}

}
