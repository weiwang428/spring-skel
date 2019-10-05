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

@Entity
@Table(name = "item")
public class Item extends AbstractEntity {

	@Column(name = "Name")
	private String mName;

	@Column(name = "State")
	@Enumerated(EnumType.STRING)
	private ItemState mState;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Column(name = "Descriptions")
	private List<Description> mDescriptions;

	public Item() {
		super();
		mState = ItemState.UNDEFINED;
		mDescriptions = new ArrayList<Description>();
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public ItemState getState() {
		return mState;
	}

	public void setState(ItemState state) {
		mState = state;
	}

	public List<Description> getDescriptions() {
		return mDescriptions;
	}

	public void addDescription(Description description) {
		this.getDescriptions().add(description);
		description.setItem(this);
	}

	public void setDescriptions(List<Description> descriptions) {
		descriptions.forEach(d -> addDescription(d));
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
