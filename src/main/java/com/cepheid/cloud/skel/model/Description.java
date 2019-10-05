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

@Entity
@Table(name = "description")
public class Description extends AbstractEntity {

	@Column(name = "Content")
	private String mContent;

	// For some reason, mItem naming will cause @JsonIgnore to fail and leads to a
	// loop reference.
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "Item_Id")
	@OnDelete(action = OnDeleteAction.CASCADE)
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("Content : ").append(getContent()).append("\n");
		return sb.toString();
	}
}
