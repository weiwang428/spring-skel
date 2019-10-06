package com.cepheid.cloud.skel.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

/***
 * This is a entity class represent a Description entity.
 * 
 * @author Wei Wang
 * @version 1.0
 */

@Entity
@Table(name = "description")
public class Description extends AbstractEntity {

	@Column(name = "Content")
	private String mContent;

	// Json generate the name for getter/setter methods, we have to match the field
	// name with the getter/setter methods.
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "Item_Id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Item item;

	/**
	 * Default constructor for Description
	 */
	public Description() {
		super();
	}

	/**
	 * Constructor for class Description with a given content.
	 * 
	 * @param content Content of the description object.
	 */
	public Description(String content) {
		setContent(content);
	}

	/**
	 * Constructor for class Description with a given content, and a given item.
	 * 
	 * @param content Content of the description object.
	 * @param item    item which owns the description object.
	 */
	public Description(String content, Item item) {
		this(content);
		this.item = item;
	}

	/**
	 * Getter
	 * 
	 * @return Content of the description.
	 */
	public String getContent() {
		return mContent;
	}

	/**
	 * Setter
	 * 
	 * @param content New content of the description to be set.
	 */
	public void setContent(String content) {
		mContent = content;
	}

	/**
	 * Getter
	 * 
	 * @return Item of the description which owns it.
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Setter
	 * 
	 * @param item New item which the description belongs to.
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("Content : ").append(getContent()).append("\n");
		return sb.toString();
	}
}
