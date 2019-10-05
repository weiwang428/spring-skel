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

// curl http:/localhost:9443/app/api/1.0/items

@Component
@Path("/api/1.0/items")
@Api()
public class ItemController {

	private final ItemRepository mItemRepository;
	private final DescriptionRepository mDescriptionRepository;

	@Autowired
	public ItemController(ItemRepository itemRepository, DescriptionRepository descriptionRepository) {
		mItemRepository = itemRepository;
		mDescriptionRepository = descriptionRepository;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public Response getItems() {
		Collection<Item> found_items = mItemRepository.findAll();
//		found_items.forEach(System.out::println);
//		mDescriptionRepository.findAll().forEach(System.out::println);
		return Response.status(Status.OK).entity(found_items).build();
	}

	@GET
	@Path("/item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItem(@PathParam(value = "id") Long id) throws ResourceNotFoundException {
		Item item = mItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Item was not found with id: " + id));
		// Return the find Item information, with HTTP status code OK.
		return Response.status(Status.OK).entity(item).build();
	}

	@GET
	@Path("/item")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItem(@QueryParam("name") String name) throws ResourceNotFoundException {
		Collection<Item> found_items = mItemRepository.findAllBymName(name)
				.orElseThrow(() -> new ResourceNotFoundException("Item was not found with name: " + name));
		// Return the find Item information, with HTTP status code OK.
		return Response.status(Status.OK).entity(found_items).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(Item item) {
		Item m_item = mItemRepository.save(item);
//		mDescriptionRepository.findAll().forEach(System.out::println);
		// Return the new added Item information, with HTTP status code Created.
		return Response.status(Status.CREATED).entity(m_item).build();
	}

	@PUT
	@Path("/item/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateItem(@PathParam("id") Long id, Item item) throws ResourceNotFoundException {
		Item m_item = mItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Item was not found with id: " + id));
		// Now, we should update the field information for the updated one.
		m_item.setName(item.getName());
		m_item.setState(item.getState());
		m_item.setDescriptions(item.getDescriptions());
		m_item = mItemRepository.save(m_item);
		// Return the update Item information, with HTTP status code Accepted.
		return Response.status(Status.ACCEPTED).entity(m_item).build();
	}

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
