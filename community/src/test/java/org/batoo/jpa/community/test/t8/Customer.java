/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.community.test.t8;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 
 * @author barreiro
 */
@NamedQueries({ @NamedQuery(name = Customer.QUERY_LAST, query = "select a from Customer a where a.lastName = :lastName"),
	@NamedQuery(name = Customer.QUERY_FIRST, query = "select a.firstName from Customer a where a.lastName = :lastName"),
	@NamedQuery(name = Customer.QUERY_ALL, query = "select a from Customer a"),
	@NamedQuery(name = Customer.QUERY_COUNT, query = "select COUNT(a) from Customer a") })
@Entity
@Table(name = "CUSTOMER_")
@SuppressWarnings({ "serial", "javadoc" })
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

	@Version
	@Column(name = "C_VERSION")
	private int version = 0;

	protected Customer() {
		super();
	}

	public Customer(String first, String last) {
		this.firstName = first;
		this.lastName = last;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		return this.id == ((Customer) o).id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public int getId() {
		return this.id;
	}

	public String getLastName() {
		return this.lastName;
	}

	public int getVersion() {
		return this.version;
	}

	@Override
	public int hashCode() {
		return new Integer(this.id).hashCode();
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setId(Integer customerId) {
		this.id = customerId;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return this.getFirstName() + " " + this.getLastName();
	}
}
