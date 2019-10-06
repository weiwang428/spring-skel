package com.cepheid.cloud.skel.controller;

import java.util.Collection;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cepheid.cloud.skel.exception.ResourceNotFoundException;
import com.cepheid.cloud.skel.model.Item;
import com.cepheid.cloud.skel.repository.DescriptionRepository;
import com.cepheid.cloud.skel.repository.ItemRepository;

import io.swagger.annotations.Api;

/***
 * This is a ItemController class which provides the REST API to the user to a
 * one-to-many item->description CRUD operation. To List all the items, a query
 * can be done like this: {@code curl http:/localhost:9443/app/api/1.0/items}
 * 
 * @author Wei Wang
 * @version 1.0
 */

@Component
@Path("/api/1.0/items")
@Api()
public class ItemController {

	private final ItemRepository mItemRepository;
	private final DescriptionRepository mDescriptionRepository;

	/**
	 * Constructor for class ItemController with provided services.
	 * 
	 * @param itemRepository        ItemRepository object to manipulate the item
	 *                              objects.
	 * @param descriptionRepository DescriptionRepository object to manipulate the
	 *                              description objects.
	 */
	@Autowired
	public ItemController(ItemRepository itemRepository, DescriptionRepository descriptionRepository) {
		mItemRepository = itemRepository;
		mDescriptionRepository = descriptionRepository;
	}

	/**
	 * Get a collection of all the Item objects from the database server, the given
	 * format will be in application/json.
	 * 
	 * @return A collection of all the item objects from the database.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public Response getItems() {
		Collection<Item> found_items = mItemRepository.findAll();
//		found_items.forEach(System.out::println);
//		mDescriptionRepository.findAll().forEach(System.out::println);
		return Response.status(Status.OK).entity(found_items).build();
	}

	/**
	 * Get a specific Item object with a given item id from the database server, the
	 * given format will be in application/json, it will generate an
	 * ResourceNotFoundException if there is no item with the given id.
	 * 
	 * @return A item object from the database with the given item id.
	 * @exception ResourceNotFoundException
	 */
	@GET
	@Path("/item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItem(@PathParam(value = "id") Long id) throws ResourceNotFoundException {
		Item item = mItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Item was not found with id: " + id));
		// Return the find Item information, with HTTP status code OK.
		return Response.status(Status.OK).entity(item).build();
	}

	/**
	 * Get a collection of Item objects with a given item name from the database
	 * server, the given format will be in application/json, it will generate an
	 * ResourceNotFoundException if there is no item with the given name.
	 * 
	 * @return A collection of item objects from the database with the given item
	 *         name.
	 * @exception ResourceNotFoundException
	 */
	@GET
	@Path("/item")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItem(@QueryParam("name") String name) throws ResourceNotFoundException {
		Collection<Item> found_items = mItemRepository.findAllBymName(name)
				.orElseThrow(() -> new ResourceNotFoundException("Item was not found with name: " + name));
		// Return the find Item information, with HTTP status code OK.
		return Response.status(Status.OK).entity(found_items).build();
	}

	/**
	 * Add a new Item objects to the database server, return the new added item
	 * information, the given format will be in application/json. If the given item
	 * has a ID which is already exist in the database, it will update the exiting
	 * item instead.
	 * 
	 * @return The new added item object information from database.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(Item item) {
		Item m_item = mItemRepository.findById(item.getId()).orElse(null);
		if (m_item == null) {
			m_item = mItemRepository.save(item);
		} else {
//			var old_list = m_item.getDescriptions();
			m_item.setName(item.getName());
			m_item.setState(item.getState());
			m_item.setDescriptions(item.getDescriptions());
			m_item = mItemRepository.save(m_item);
		}
//		mDescriptionRepository.findAll().forEach(System.out::println);
		// Return the new added Item information, with HTTP status code Created.
		return Response.status(Status.CREATED).entity(m_item).build();
	}

	/**
	 * Update an existing Item object in the database server, return the updated
	 * item information, the given format will be in application/json, it will
	 * generate an ResourceNotFoundException if there is no item with the given id.
	 * 
	 * @return The updated item object information from database.
	 * @exception ResourceNotFoundException
	 */
	@PUT
	@Path("/item/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateItem(@PathParam("id") Long id, Item item) throws ResourceNotFoundException {
		Item m_item = mItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Item was not found with id: " + id));
		// Now, we should update the field information for the updated one.
		var old_list = m_item.getDescriptions();
//		var all_new_description = old_list.stream().filter(d -> d.getId() == null);
//		var all_old_description = old_list.stream().filter(d -> d.getId() != null);
		m_item.setName(item.getName());
		m_item.setState(item.getState());
		item.getDescriptions().stream().filter(d -> d.getId() == null).forEach(m_item::addDescription);
		m_item = mItemRepository.save(m_item);
		item.getDescriptions().stream().filter(d -> d.getId() != null).forEach(m_item::addDescription);
		m_item = mItemRepository.save(m_item);
		old_list.stream().forEach(m_item::removeDescription);
		m_item = mItemRepository.save(m_item);
		// Return the update Item information, with HTTP status code Accepted.
		return Response.status(Status.ACCEPTED).entity(m_item).build();
	}

	/**
	 * Delete an existing Item object from the database server, return the HTTP
	 * status 202 which shows the delete operation is OK, it will generate an
	 * ResourceNotFoundException if there is no item with the given id.
	 * 
	 * @return HTTP status Status.ACCEPTED(code: 202), and a message shows the
	 *         deletion is successful.
	 * @exception ResourceNotFoundException
	 */
	@DELETE
	@Path("/item/{id}")
	public Response deleteItem(@PathParam("id") Long id) throws ResourceNotFoundException {
		Item m_item = mItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Item was not found with id: " + id));
		// Now, we should remove the find item.
		mItemRepository.delete(m_item);
		// Return with HTTP status code Accepted.
		return Response.status(Status.ACCEPTED).entity("Item deleted successfully!").build();
	}
}
