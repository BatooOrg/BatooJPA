package org.batoo.jpa.core.test.inheritence.complex;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 3, name = "PERSON_TYPE")
public abstract class Person extends BaseEntity {

	@Column(length = 25, nullable = false)
	private String firstName;

	@Column(length = 25, nullable = false)
	private String lastName;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person() {
		super();
	}

	/**
	 * @param firstName
	 *            the first name
	 * @param lastName
	 *            the last name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person(String firstName, String lastName) {
		super();

		this.firstName = firstName;
		this.lastName = lastName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDisplayText() {
		return this.getFirstName() + " " + this.getLastName();
	}

	/**
	 * Returns the firstName of the Person.
	 * 
	 * @return the firstName of the Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Returns the lastName of the Person.
	 * 
	 * @return the lastName of the Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Sets the firstName of the Person.
	 * 
	 * @param firstName
	 *            the firstName to set for Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the lastName of the Person.
	 * 
	 * @param lastName
	 *            the lastName to set for Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
