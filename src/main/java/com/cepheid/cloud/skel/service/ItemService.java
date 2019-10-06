package com.cepheid.cloud.skel.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cepheid.cloud.skel.model.Item;
import com.cepheid.cloud.skel.model.ItemState;
import com.cepheid.cloud.skel.repository.ItemRepository;

/***
 * This is a ItemService class which provides the service to manipulate the item
 * objects from/to the database.
 * 
 * @author Wei Wang
 * @version 1.0
 */
@Component
public class ItemService {

	private final ItemRepository mItemRepository;

	/**
	 * Constructor for class ItemService with provided services.
	 * 
	 * @param itemRepository ItemRepository object to manipulate the item objects.
	 */
	@Autowired
	public ItemService(ItemRepository itemRepository) {
		mItemRepository = itemRepository;
	}

	/**
	 * Fetch all the item list from the database.
	 * 
	 * @return All the item list in the database.
	 */
	public Collection<Item> FetchAllItemList() {
		return mItemRepository.findAll();
	}

	/**
	 * Find a specific item from the database with a given item id, if there is no
	 * item found with the given item id, a null will be returned.
	 * 
	 * @param id id of the item which is going to search for.
	 * @return The found item with the given item id or null if no match is found.
	 */
	public Item FindItemById(Long id) {
		return mItemRepository.findById(id).orElse(null);
	}

	/**
	 * Find a collection of specific item list from the database with a given item
	 * name, if there is no any item found with the given item name, a null will be
	 * returned.
	 * 
	 * @param name name of the items which is going to search for.
	 * @return The found items with the given item name or null if no match is
	 *         found.
	 */
	public Collection<Item> FindItemByName(String name) {
		return mItemRepository.findAllBymName(name).orElse(null);
	}

	/**
	 * Find a collection of specific item list from the database with a given item
	 * state, if there is no any item found with the given item state, a null will
	 * be returned.
	 * 
	 * @param state state of the items which is going to search for.
	 * @return The found items with the given item state or null if no match is
	 *         found.
	 */
	public Collection<Item> FindItemByState(ItemState state) {
		return mItemRepository.findAllBymState(state).orElse(null);
	}

	/**
	 * Find a collection of specific item list from the database with a given item
	 * name and state, if there is no any item found with the given item name and
	 * given state, it will return a null, if only name or state is given, it will
	 * return the list match them, otherwise, it will return a list which match both
	 * cases.
	 * 
	 * @param name  name of the items which is going to search for.
	 * @param state state of the items which is going to search for.
	 * @return The found items with the given item name or null if no match is
	 *         found.
	 */
	public Collection<Item> FindItemByNameAndState(String name, ItemState state) {

		// We check for different situations.
		if (name != null && state == null) {
			var found_name_items = FindItemByName(name);
			if (found_name_items != null)
				return found_name_items;
		} else if (name == null && state != null) {
			var found_state_items = FindItemByState(state);
			if (found_state_items != null)
				return found_state_items;
		} else if (name != null && state != null) {
			final var found_name_items = FindItemByName(name);
			final var found_state_items = FindItemByState(state);
			if (found_name_items != null && found_state_items != null) {
				var final_list = found_state_items.stream()
						.filter(found_name_items::contains)
						.collect(Collectors.toList());
				if (final_list != null)
					return final_list;
			}
		}
		return null;
	}

	/**
	 * Add a new item to the database, if the item is found with the item id, it
	 * will replace the existing item instead.
	 * 
	 * @param item New item which is going to be added to the database.
	 * @return The added item information.
	 */
	public Item AddItem(Item item) {
		Item m_item = null;
		// If the given item does not have a id, we can save to add the new item.
		if (item.getId() == null) {
			m_item = mItemRepository.save(item);
		} else {
			m_item = mItemRepository.findById(item.getId()).orElse(null);
			if (m_item == null) {
				m_item = mItemRepository.save(item);
			} else {
				m_item = UpdateItem(item);
			}
		}
		return m_item;
	}

	/**
	 * Update an existing item with the new item information, the given item must
	 * have a valid item id information and there must be an item with the same item
	 * id in the database, otherwise the update will not happen and a null will
	 * return.
	 * 
	 * @param item New item which is going to be update the old item into the
	 *             database.
	 * @return The updated item information, or null if the given item does not have
	 *         a valid item id.
	 */
	public Item UpdateItem(Item item) {
		// If the given item does not have a id, we can skip the update, and return
		// null.
		if (item.getId() == null) {
			return null;
		}
		Item m_item = mItemRepository.findById(item.getId()).orElse(null);
		if (m_item == null) {
			return null;
		} else {
			m_item.setName(item.getName());
			m_item.setState(item.getState());
			m_item.setDescriptions(item.getDescriptions());
			m_item = mItemRepository.save(m_item);
		}
		return m_item;
	}

	/**
	 * Delete an existing item with the given item id, the given id must be a valid
	 * item id information and there must be an item with the same item id in the
	 * database, otherwise the delete will not do anything, and return false.
	 * 
	 * @param id item id which the item is going to be deleted in the database.
	 * @return true if the deletion is successful, or false if the id information is
	 *         not found in the database.
	 */
	public boolean DeleteItem(Long id) {
		Item m_item = mItemRepository.findById(id).orElse(null);
		// Return false if the given id is not a valid item id information.
		if (m_item == null)
			return false;
		mItemRepository.delete(m_item);
		return true;
	}
}
