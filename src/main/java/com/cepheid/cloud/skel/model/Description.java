package com.cepheid.cloud.skel.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "description")
public class Description extends AbstractEntity {

	@Column(name = "Content")
	private String mContent;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Item_Id")
	private Item mItem;

	public Description() {
		super();
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String content) {
		mContent = content;
	}

	public Item getItem() {
		return mItem;
	}

	public void setItem(Item item) {
		mItem = item;
	}

}
