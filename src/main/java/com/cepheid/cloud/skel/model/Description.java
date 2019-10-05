package com.cepheid.cloud.skel.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "description")
public class Description extends AbstractEntity {

	@Column(name = "Content")
	private String mContent;

	// For some reason, mItem naming will cause @JsonIgnore to fail and leads to a
	// loop reference.
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Item_Id")
	@JsonIgnore
	private Item item;

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
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

}
