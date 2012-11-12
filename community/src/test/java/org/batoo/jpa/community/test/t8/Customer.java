package org.batoo.jpa.community.test.t8;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author barreiro
 */
@SuppressWarnings("serial")
@NamedQueries({ @NamedQuery(name = Customer.QUERY_LAST, query = "select a from Customer a where a.lastName = :lastName"),
	@NamedQuery(name = Customer.QUERY_FIRST, query = "select a.firstName from Customer a where a.lastName = :lastName"),
	@NamedQuery(name = Customer.QUERY_ALL, query = "select a from Customer a"),
	@NamedQuery(name = Customer.QUERY_COUNT, query = "select COUNT(a) from Customer a") })
@Entity
@Table(name = "CUSTOMER_")
public class Customer implements Serializable {

	public static final String QUERY_LAST = "Customer.selectLast";
	public static final String QUERY_FIRST = "Customer.selectFirst";
	public static final String QUERY_ALL = "Customer.selectAll";
	public static final String QUERY_COUNT = "Customer.count";

	@Id
	@Column(name = "C_ID")
	private int id;

	@Column(name = "C_FIRST")
	private String firstName;

	@Column(name = "C_LAST")
	private String lastName;

	// @Version
	@Column(name = "C_VERSION")
	private int version = 0;

	protected Customer() {
	}

	public Customer(String first, String last) {
		this.firstName = first;
		this.lastName = last;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer customerId) {
		this.id = customerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return id == ((Customer) o).id;
	}

	@Override
	public int hashCode() {
		return new Integer(id).hashCode();
	}

	@Override
	public String toString() {
		return this.getFirstName() + " " + this.getLastName();
	}
}
