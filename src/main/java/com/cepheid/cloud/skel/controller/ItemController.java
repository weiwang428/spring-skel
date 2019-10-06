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
import com.cepheid.cloud.skel.model.Description;
import com.cepheid.cloud.skel.model.Item;
import com.cepheid.cloud.skel.model.ItemState;
import com.cepheid.cloud.skel.service.ItemService;

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

	private final ItemService mItemService;

	/**
	 * Constructor for class ItemController with provided services.
	 * 
	 * @param ItemService ItemService which provides a number of useful service to
	 *                    manipulate the item objects.
	 */
	@Autowired
	public ItemController(ItemService itemService) {
		mItemService = itemService;
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
		Collection<Item> all_item_list = mItemService.FetchAllItemList();
		return Response.status(Status.OK).entity(all_item_list).build();
	}

	/**
	 * Get a specific Item object with a given item id from the database server, the
	 * given format will be in application/json, it will generate an
	 * ResourceNotFoundException if there is no item with the given id.
	 * 
	 * @param id The id of the searched item
	 * @return A item object from the database with the given item id.
	 * @exception ResourceNotFoundException
	 */
	@GET
	@Path("/item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItem(@PathParam(value = "id") Long id) throws ResourceNotFoundException {
		Item item = mItemService.FindItemById(id);
		if (item == null)
			throw new ResourceNotFoundException("Item was not found with id: " + id);
		// Return the find Item information, with HTTP status code OK.
		return Response.status(Status.OK).entity(item).build();
	}

	/**
	 * Get a collection of Item objects with a given item name from the database
	 * server, the given format will be in application/json, it will generate an
	 * ResourceNotFoundException if there is no item with the given name.
	 * 
	 * @param name  The name information of the searched item, can be null.
	 * @param state The state information of the searched item, can be null, but if
	 *              name is null at the same time, an exception will be thrown.
	 * @return A collection of item objects from the database with the given item
	 *         name.
	 * @exception ResourceNotFoundException
	 */
	@GET
	@Path("/item")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getItem(@QueryParam("name") String name, @QueryParam("state") ItemState state)
			throws ResourceNotFoundException {
		var found_list = mItemService.FindItemByNameAndState(name, state);
		if (found_list == null)
			throw new ResourceNotFoundException("Item was not found with given information");
		return Response.status(Status.OK).entity(found_list).build();
	}

	/**
	 * Add a new Item objects to the database server, return the new added item
	 * information, the given format will be in application/json. If the given item
	 * has a ID which is already exist in the database, it will update the exiting
	 * item instead.
	 * 
	 * @param item The new item which is going to be added.
	 * @return The new added item object information from database.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(Item item) {
		Item m_item = mItemService.AddItem(item);
		// Return the new added Item information, with HTTP status code Created.
		return Response.status(Status.CREATED).entity(m_item).build();
	}

	/**
	 * Update an existing Item object in the database server, return the updated
	 * item information, the given format will be in application/json, it will
	 * generate an ResourceNotFoundException if there is no item with the given id.
	 * 
	 * @param id   Id of the item to be deleted from the database.
	 * @param item the updated item information.
	 * @return The updated item object information from database.
	 * @exception ResourceNotFoundException
	 */
	@PUT
	@Path("/item/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateItem(@PathParam("id") Long id, Item item) throws ResourceNotFoundException {
		item.setId(id);
		Item m_item = mItemService.UpdateItem(item);
		if (m_item == null)
			throw new ResourceNotFoundException("Item was not found with id: " + id);
		// Return the update Item information, with HTTP status code Accepted.
		return Response.status(Status.ACCEPTED).entity(m_item).build();
	}

	/**
	 * Delete an existing Item object from the database server, return the HTTP
	 * status 202 which shows the delete operation is OK, it will generate an
	 * ResourceNotFoundException if there is no item with the given id.
	 * 
	 * @param id Id of the item to be deleted from the database.
	 * @return HTTP status Status.ACCEPTED(code: 202), and a message shows the
	 *         deletion is successful.
	 * @exception ResourceNotFoundException
	 */
	@DELETE
	@Path("/item/{id}")
	public Response deleteItem(@PathParam("id") Long id) throws ResourceNotFoundException {
		if (!mItemService.DeleteItem(id))
			throw new ResourceNotFoundException("Item was not found with id: " + id);
		// Return with HTTP status code Accepted.
		return Response.status(Status.ACCEPTED).entity("Item deleted successfully!").build();
	}

	/**
	 * Add a new description objects to a given item into the database server, the
	 * uploaded description does not need to contain a id information, if it does,
	 * it will be ignored. return the item object with new added information, the
	 * given format will be in application/json.
	 * 
	 * @param id          Id of the item to be added.
	 * @param description New description which is going to be added to the item.
	 * @return The new added information of the item from database.
	 * @exception ResourceNotFoundException
	 */
	@POST
	@Path("/item/{id}/description")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addDescriptionToItem(@PathParam("id") Long id, Description description)
			throws ResourceNotFoundException {
		Item m_item = mItemService.addDescriptionToItem(id, description);
		if (m_item == null)
			throw new ResourceNotFoundException("Item was not found with id: " + id);
		// Return the new added Item information, with HTTP status code Created.
		return Response.status(Status.CREATED).entity(m_item).build();
	}

	/**
	 * Update a description objects in a given item into the database server, the
	 * uploaded description needs to contain a valid description id information, the
	 * description id needs to be included in the item already, otherwise, the
	 * update will fail. return the item object with new added information, the
	 * given format will be in application/json.
	 * 
	 * @param id          The exiting item id which the description update needs to
	 *                    operate on.
	 * @param description The new description information, and it has to have a
	 *                    valid description id information, and it needs to belong
	 *                    to this item already.
	 * @return The new updated information of the item from database.
	 * @exception ResourceNotFoundException
	 */
	@PUT
	@Path("/item/{id}/description")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response UpdateDescriptionInItem(@PathParam("id") Long id, Description description)
			throws ResourceNotFoundException {
		Item m_item = mItemService.updateDescriptionInItem(id, description);
		if (m_item == null)
			throw new ResourceNotFoundException(
					"Either the Item id: " + id + " or the description id: " + description.getId() + " is invalid.");
		// Return the new added Item information, with HTTP status code Created.
		return Response.status(Status.ACCEPTED).entity(m_item).build();
	}

	/**
	 * Delete an existing description object from the existing item in the database
	 * server, return the HTTP status 202 which shows the delete operation is OK, it
	 * will generate an ResourceNotFoundException if there is no item with the given
	 * id or no valid descriptionId in the existing item object.
	 * 
	 * @param id            The exiting item id which the deletion intends to be
	 *                      done on.
	 * @param descriptionId The description id which should be valid and already in
	 *                      the item object.
	 * @return HTTP status Status.ACCEPTED(code: 202), and a message shows the
	 *         deletion is successful.
	 * @exception ResourceNotFoundException
	 */
	@DELETE
	@Path("/item/{id}/description")
	public Response deleteDescriptionFromItem(@PathParam("id") Long id, @QueryParam("descriptionId") Long descriptionId)
			throws ResourceNotFoundException {
		if (!mItemService.deleteDescriptionFromItem(id, descriptionId))
			throw new ResourceNotFoundException(
					"Either the Item id: " + id + " or the description id: " + descriptionId + " is invalid.");

		// Return with HTTP status code Accepted.
		return Response.status(Status.ACCEPTED).entity("Description has been successfully deleted from the item!")
				.build();
	}
}
