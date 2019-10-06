package com.cepheid.cloud.skel.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/***
 * This is a entity class represent a Item entity.
 * 
 * @author Wei Wang
 * @version 1.0
 */

@Entity
@Table(name = "item")
public class Item extends AbstractEntity {

	@Column(name = "Name")
	private String mName;

	@Column(name = "State")
	@Enumerated(EnumType.STRING)
	private ItemState mState;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@Column(name = "Descriptions")
	private List<Description> mDescriptions;

	/**
	 * Default constructor for Item, set mState to ItemState.UNDEFINED, and
	 * initialize the mDescriptions to an empty ArrayList.
	 */
	public Item() {
		super();
		mState = ItemState.UNDEFINED;
		mDescriptions = new ArrayList<Description>();
	}

	/**
	 * Constructor for class Item with a given name.
	 * 
	 * @param name Name of the item object.
	 */
	public Item(String name) {
		this();
		setName(name);
	}

	/**
	 * Constructor for class Item with a given name.
	 * 
	 * @param name  Name of the item object.
	 * @param state State of the item object.
	 */
	public Item(String name, ItemState state) {
		this(name);
		setState(state);
	}

	/**
	 * Getter
	 * 
	 * @return Name of the item object.
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Setter
	 * 
	 * @param name New name of the item object.
	 */
	public void setName(String name) {
		mName = name;
	}

	/**
	 * Getter
	 * 
	 * @return State of the item object.
	 */
	public ItemState getState() {
		return mState;
	}

	/**
	 * Setter
	 * 
	 * @param state New state of the item object.
	 */
	public void setState(ItemState state) {
		mState = state;
	}

	/**
	 * Getter
	 * 
	 * @return Description list of the item object.
	 */
	public List<Description> getDescriptions() {
		return mDescriptions;
	}

	/**
	 * Setter
	 * 
	 * @param descriptions New description list of the item object.
	 */
	public void setDescriptions(List<Description> descriptions) {
		getDescriptions().clear();
		if (descriptions != null)
			descriptions.forEach(d -> addDescription(d));
	}

	/**
	 * Add a new description into the item object.
	 * 
	 * @param description a new description which adds to the item object.
	 */
	public void addDescription(Description description) {
		if (description != null) {
			this.getDescriptions().add(description);
			description.setItem(this);
		}
	}

	/**
	 * Add a new description into the item object.
	 * 
	 * @param description a new description which adds to the item object.
	 */
	public void removeDescription(Description description) {
		if (description != null) {
			this.getDescriptions().remove(description);
			description.setItem(null);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("Name : ").append(getName()).append("\n");
		sb.append("--------Descriptions-------").append("\n");
		getDescriptions().forEach(d -> sb.append(d));
		sb.append("---------------------------").append("\n");
		return sb.toString();
	}
}
