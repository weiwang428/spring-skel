package com.cepheid.cloud.skel.model;

import java.util.HashSet;
import java.util.Set;

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
	private Set<Description> mDescriptions;

	public Item() {
		super();
		mState = ItemState.UNDEFINED;
		mDescriptions = new HashSet<Description>();
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

	public Set<Description> getDescriptions() {
		if (mDescriptions == null)
			mDescriptions = new HashSet<Description>();
		return mDescriptions;
	}

	public void addDescription(Description description) {
		this.getDescriptions().add(description);
		description.setItem(this);
	}

	public void setDescriptions(Set<Description> descriptions) {
		mDescriptions = descriptions;
	}
}
