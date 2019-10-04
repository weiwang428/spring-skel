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
@Table(name = "ITEM")
public class Item extends AbstractEntity {
	
    @Column(name = "Name")
    private String mName;
    
    @Column(name = "State")
    @Enumerated(EnumType.STRING)
    private ItemState mState;
    
    public Item() {
    	this.mState = ItemState.UNDEFINED;
//    	this.mDescriptions = new ArrayList<Description>();
    }
    
    public String getName() {
        return this.mName;
    }
    
    public void setName(String name) {
        this.mName = name;
        
        if (name.length() > 0)
        	this.mState = ItemState.VALID;
        else
        	this.mState = ItemState.INVALID;
    }
    
    public ItemState getState() {
    	return this.mState;
    }
}
