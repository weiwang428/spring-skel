package com.cepheid.cloud.skel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.cepheid.cloud.skel.model.Description;
import com.cepheid.cloud.skel.model.Item;
import com.cepheid.cloud.skel.model.ItemState;
import java.util.Properties;

@RunWith(SpringRunner.class)
public class ItemControllerTest extends TestBase {

	@Test
	public void testGetItems() throws Exception {
		Builder itemController = getBuilder("/app/api/1.0/items");

		var items = itemController.get(new GenericType<Collection<Item>>() {
		});

		// We should only have exactly 4 entries.
		assertEquals(4, items.size());

		// Find the 1st item, and check its property.
		Item first_item = items.stream().filter(e -> e.getId() == 1).findFirst().get();
		assertNotNull(first_item);
		assertEquals("Lord of the rings", first_item.getName());
		assertEquals(ItemState.VALID, first_item.getState());

		Collection<Description> descriptions = first_item.getDescriptions();
		// This item should contain exactly 2 descriptions.
		assertEquals(2, descriptions.size());
		// Find the 1st Description;
		Description first_description = descriptions.stream().filter(e -> e.getId() == 1).findFirst().get();
		assertNotNull(first_description);
		assertEquals("This is just a test content", first_description.getContent());

	}

	@Test
	public void testGetItemById() throws Exception {
		Builder itemController = getBuilder("/app/api/1.0/items/item/2");

		Item item = itemController.get(Item.class);
		assertNotNull(item);
		assertEquals("Hobbit", item.getName());
		assertEquals(ItemState.VALID, item.getState());
		// Now check the descriptions of the item.
		var descriptions = item.getDescriptions();
		// This item should contain exactly 2 descriptions.
		assertEquals(2, descriptions.size());
		// Find the 1st Description;
		Description first_description = descriptions.stream().filter(e -> e.getId() == 3).findFirst().get();
		assertNotNull(first_description);
		assertEquals("This is just a test content", first_description.getContent());
	}

	@Test(expected = Exception.class)
	public void testGetItemByNonExistIdThenThrowException() throws Exception {

		Builder itemController = getBuilder("/app/api/1.0/items/item/6");
		// We should expect an exception here, since the id does not exist for an item.
		itemController.get(Item.class);
	}

	@Test
	public void testGetItemByName() throws Exception {
		Properties queryParam = new Properties();
		queryParam.setProperty("name", "Silmarillion");
		Builder itemController = getQueryBuilder("/app/api/1.0/items/item", queryParam);

		var items = itemController.get(new GenericType<Collection<Item>>() {
		});
		assertNotNull(items);

		// We should only have exactly 1 entry match the case.
		assertEquals(1, items.size());
		Item item = items.stream().findFirst().orElse(null);
		assertNotNull(item);
		assertEquals("Silmarillion", item.getName());
		assertEquals(ItemState.VALID, item.getState());
		// Now check the descriptions of the item.
		var descriptions = item.getDescriptions();
		// This item should contain exactly 2 descriptions.
		assertEquals(2, descriptions.size());
		// Find the 1st Description;
		Description first_description = descriptions.stream().filter(e -> e.getId() == 5).findFirst().get();
		assertNotNull(first_description);
		assertEquals("This is just a test content", first_description.getContent());
	}

	@Test(expected = Exception.class)
	public void testGetItemByNonExistNameThenThrowException() throws Exception {
		Properties queryParam = new Properties();
		queryParam.setProperty("name", "Name does not exist");
		Builder itemController = getQueryBuilder("/app/api/1.0/items/item", queryParam);

		// We should Expect an exception throw here since the API should return an
		// exception.
		itemController.get(new GenericType<Collection<Item>>() {
		});
	}

	@Test
	public void testGetItemByState() throws Exception {
		Properties queryParam = new Properties();
		queryParam.setProperty("state", "VALID");
		Builder itemController = getQueryBuilder("/app/api/1.0/items/item", queryParam);

		var items = itemController.get(new GenericType<Collection<Item>>() {
		});
		assertNotNull(items);

		// We should only have exactly 4 entry match the case.
		assertEquals(4, items.size());
	}

	@Test(expected = Exception.class)
	public void testGetItemByNonExistStateThenThrowExceptions() throws Exception {
		Properties queryParam = new Properties();
		queryParam.setProperty("state", "INVALID");
		Builder itemController = getQueryBuilder("/app/api/1.0/items/item", queryParam);

		itemController.get(new GenericType<Collection<Item>>() {
		});
	}

	@Test
	public void testGetItemByNameAndState() throws Exception {
		Properties queryParam = new Properties();
		queryParam.setProperty("name", "Hobbit");
		queryParam.setProperty("state", "VALID");
		Builder itemController = getQueryBuilder("/app/api/1.0/items/item", queryParam);

		var items = itemController.get(new GenericType<Collection<Item>>() {
		});
		assertNotNull(items);
		// We should only have exactly 1 entry match the case.
		assertEquals(1, items.size());

		Item item = items.stream().findFirst().orElse(null);
		assertNotNull(item);
		assertEquals("Hobbit", item.getName());
		assertEquals(ItemState.VALID, item.getState());
	}

	@Test(expected = Exception.class)
	public void testGetItemByNameAndStateWithNoMatchCauseException() throws Exception {
		Properties queryParam = new Properties();
		queryParam.setProperty("name", "Hobbit");
		queryParam.setProperty("state", "INVALID");
		Builder itemController = getQueryBuilder("/app/api/1.0/items/item", queryParam);

		itemController.get(new GenericType<Collection<Item>>() {
		});
	}

	@Test
	public void testAddNewItem() throws Exception {
		final String content = "This is a completely new content.";
		final String item_name = "New Name";
		final ItemState item_state = ItemState.VALID;
		Description d = new Description(content);
		Item item = new Item(item_name, item_state);
		item.addDescription(d);

		Builder itemController = getBuilder("/app/api/1.0/items");
		Item return_item = itemController.post(Entity.json(item), Item.class);
//		System.out.println(return_item);

		// Do the sanity check of the return item.
		assertNotNull(return_item);
		assertEquals(item_name, return_item.getName());
		assertEquals(item_state, return_item.getState());
		assertNotNull(return_item.getId());

		// Now Read from Database for this id, and check it is actually added to the
		// database.
		Long item_id = return_item.getId();
		itemController = getBuilder("/app/api/1.0/items/item/" + item_id);

		Item n_item = itemController.get(Item.class);
		assertNotNull(n_item);
		assertEquals(item_name, n_item.getName());
		assertEquals(item_state, n_item.getState());
		// Now check the descriptions of the n_item.
		var descriptions = n_item.getDescriptions();
		// This item should contain exactly 1 descriptions, since this is what we
		// create.
		assertEquals(1, descriptions.size());
		// Find the 1st Description;
		Description first_description = descriptions.stream().findFirst().orElse(null);
		assertNotNull(first_description);
		assertNotNull(first_description.getId());
		assertEquals(content, first_description.getContent());
	}

	@Test
	public void testAddNewItemWithExistingIdCauseAnUpdate() throws Exception {
		final String content = "This is a completely new content.";
		final Long item_id = 1L;
		final String item_name = "New Name";
		final ItemState item_state = ItemState.VALID;
		Description d = new Description(content);
		Item item = new Item(item_name, item_state);
		item.setId(item_id);
		item.addDescription(d);

		Builder itemController = getBuilder("/app/api/1.0/items");
		Item return_item = itemController.post(Entity.json(item), Item.class);
//		System.out.println(return_item);

		// Do the sanity check of the return item.
		assertNotNull(return_item);
		assertEquals(item_id, return_item.getId());
		assertEquals(item_name, return_item.getName());
		assertEquals(item_state, return_item.getState());
		assertNotNull(return_item.getId());

		// Now Read from Database for this id, and check it is actually added to the
		// database.
		itemController = getBuilder("/app/api/1.0/items/item/" + item_id);

		Item n_item = itemController.get(Item.class);
//		System.out.println(n_item);
		assertNotNull(n_item);
		assertEquals(item_name, n_item.getName());
		assertEquals(item_state, n_item.getState());
		// Now check the descriptions of the n_item.
		var descriptions = n_item.getDescriptions();
		// This item should contain exactly 1 descriptions now, since this new item only
		// contains one entry for the content.
		assertEquals(1, descriptions.size());
		// Find the 1st Description;
		Description found_description = descriptions.stream().filter(dd -> dd.getContent().equals(content)).findAny()
				.orElse(null);
//		System.out.println(found_description);
		assertNotNull(found_description);
		assertNotNull(found_description.getId());
		assertEquals(content, found_description.getContent());
	}

	@Test
	public void testUpdateItemWithExistingId() throws Exception {
		final String content = "This is a completely new content.";
		final Long item_id = 1L;
		final String item_name = "New Name";
		final ItemState item_state = ItemState.VALID;
		Description d = new Description(content);
		Item item = new Item(item_name, item_state);
		item.addDescription(d);

		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id);
		Item return_item = itemController.put(Entity.json(item), Item.class);
//		System.out.println(return_item);

		// Do the sanity check of the return item.
		assertNotNull(return_item);
		assertEquals(item_id, return_item.getId());
		assertEquals(item_name, return_item.getName());
		assertEquals(item_state, return_item.getState());
		assertNotNull(return_item.getId());

		// Now Read from Database for this id, and check it is actually added to the
		// database.
		itemController = getBuilder("/app/api/1.0/items/item/" + item_id);

		Item n_item = itemController.get(Item.class);
//		System.out.println(n_item);
		assertNotNull(n_item);
		assertEquals(item_name, n_item.getName());
		assertEquals(item_state, n_item.getState());
		// Now check the descriptions of the n_item.
		var descriptions = n_item.getDescriptions();
		// This item should contain exactly 1 description now, since the new added
		// description list only has 1 entry
		assertEquals(1, descriptions.size());
		// Find the 1st Description;
		Description found_description = descriptions.stream().filter(dd -> dd.getContent().equals(content)).findAny()
				.orElse(null);
		assertNotNull(found_description);
		assertNotNull(found_description.getId());
		assertEquals(content, found_description.getContent());
	}

	@Test(expected = Exception.class)
	public void testUpdateItemWithNonExistingIdThenThrowException() throws Exception {
		final String content = "This is a completely new content.";
		final Long item_id = 20L;
		final String item_name = "New Name";
		final ItemState item_state = ItemState.INVALID;
		Description d = new Description(content);
		Item item = new Item(item_name, item_state);
		item.addDescription(d);

		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id);
		itemController.put(Entity.json(item), Item.class);
	}

	@Test
	public void testDeleteItemWithExistingId() throws Exception {
		final Long item_id = 1L;
		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id);
		Response response = itemController.delete();
		// Check the return status code with the response.
		assertEquals(Status.ACCEPTED.getStatusCode(), response.getStatus());

		// Read all the items from the database again.
		itemController = getBuilder("/app/api/1.0/items");
		var items = itemController.get(new GenericType<Collection<Item>>() {
		});
		// Try to find this item again, it should be null in this case.
		Item find_item = items.stream().filter(it -> it.getId() == item_id).findFirst().orElse(null);
		assertNull(find_item);
	}

	@Test
	public void testDeleteItemWithNonExistingIdThenReturnInternalServerErrorCode() throws Exception {
		final Long item_id = 100L;
		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id);
		Response response = itemController.delete();
		// Check the return status code with the response, we should get Internal server
		// error code.
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
	}

	@Test
	public void testAddNewDescriptionToItem() throws Exception {
		final Long item_id = 1L;
		final String content = "This is a completely new added description for item 1.";
		Description d = new Description(content);
		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id + "/description");
		Item return_item = itemController.post(Entity.json(d), Item.class);

//		System.out.println(return_item);
		// Check if the return_item has the added description.
		var find_in_content = return_item.getDescriptions().stream().filter(dd -> dd.getContent().equals(content))
				.findFirst().orElse(null);
		assertNotNull(find_in_content);
		assertNotNull(find_in_content.getId());
	}
	
	@Test(expected = Exception.class)
	public void testAddNewDescriptionToItemWithInvalidItemIdThrowException() throws Exception {
		final Long item_id = 20L;
		final String content = "This is a completely new added description for item 1.";
		Description d = new Description(content);
		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id + "/description");
		itemController.post(Entity.json(d), Item.class);
	}
	
	@Test
	public void testUpdateDescriptionInItem() throws Exception {
		final Long item_id = 2L;
		final String content = "This is a completely new content.";
		final Long description_id = 3L;
		Description d = new Description(content);
		d.setId(description_id);
		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id + "/description");
		Item return_item = itemController.put(Entity.json(d), Item.class);
		
		System.out.println(return_item);
		// Check if the return_item has the added 
		var find_in_content = return_item.getDescriptions().stream().filter(dd -> dd.getContent().equals(content))
				.findFirst().orElse(null);
		assertNotNull(find_in_content);
	}
	
	@Test(expected = Exception.class)
	public void testUpdateDescriptionInItemWithInvalidItemIdThrownException() throws Exception {
		final Long item_id = 200L;
		final String content = "This is a completely new content.";
		final Long description_id = 3L;
		Description d = new Description(content);
		d.setId(description_id);
		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id + "/description");
		itemController.put(Entity.json(d), Item.class);
	}
	
	@Test(expected = Exception.class)
	public void testUpdateDescriptionInItemWithInvalidDescriptionIdThrownException() throws Exception {
		final Long item_id = 200L;
		final String content = "This is a completely new content.";
		Description d = new Description(content);
		Builder itemController = getBuilder("/app/api/1.0/items/item/" + item_id + "/description");
		itemController.put(Entity.json(d), Item.class);
	}
	
	@Test
	public void testDeleteDescriptionFromItem() throws Exception {
		final Long item_id = 1L;
		Properties queryParam = new Properties();
		queryParam.setProperty("descriptionId", "1");
		Builder itemController = getQueryBuilder("/app/api/1.0/items/item/" + item_id + "/description", queryParam);
		Response response = itemController.delete();
		// Check the return status code with the response.
		assertEquals(Status.ACCEPTED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testDeleteDescriptionFromItemWithNonExistingIdThenReturnInternalServerErrorCode() throws Exception {
		final Long item_id = 10L;
		Properties queryParam = new Properties();
		queryParam.setProperty("descriptionId", "1");
		Builder itemController = getQueryBuilder("/app/api/1.0/items/item/" + item_id + "/description", queryParam);
		Response response = itemController.delete();
//		System.out.println(response.getStatus());
		// Check the return status code with the response, we should get Internal server
		// error code.
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
	}
}
