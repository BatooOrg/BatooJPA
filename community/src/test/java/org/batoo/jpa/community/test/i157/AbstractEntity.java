package org.batoo.jpa.community.test.i157;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public abstract class AbstractEntity extends TopMappedSuperClass{
	@Basic
	@Column(nullable = false)
	private String myAbstractProperty;

	public String getMyAbstractProperty() {
		return myAbstractProperty;
	}
	
	public void setMyAbstractProperty(String myAbstractProperty) {
		this.myAbstractProperty = myAbstractProperty;
	}
	
	public abstract String getFoo();
}
