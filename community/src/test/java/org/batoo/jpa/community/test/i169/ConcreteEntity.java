package org.batoo.jpa.community.test.i169;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@DiscriminatorValue("ConcreteEntity")
public class ConcreteEntity extends AbstractEntity {
	@Basic
	@Column(nullable = false)
	private String myConcreteProperty;
	
	public ConcreteEntity(String myAbstractProperty, String myConcreteProperty) {
		setMyAbstractProperty(myAbstractProperty);
		this.myConcreteProperty = myConcreteProperty;
	}
}
