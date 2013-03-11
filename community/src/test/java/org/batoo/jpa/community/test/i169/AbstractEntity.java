package org.batoo.jpa.community.test.i169;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CONCRETE_TYPE", discriminatorType = DiscriminatorType.STRING, length = 50)
@Table(name = "AbstractEntity")
public abstract class AbstractEntity {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id = null;
	
	@Basic
	@Column(nullable = false)
	private String myAbstractProperty;
	
	public String getMyAbstractProperty() {
		return myAbstractProperty;
	}
	
	public void setMyAbstractProperty(String myAbstractProperty) {
		this.myAbstractProperty = myAbstractProperty;
	}
}
