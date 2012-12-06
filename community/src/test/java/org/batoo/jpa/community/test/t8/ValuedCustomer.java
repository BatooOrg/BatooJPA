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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author barreiro
 */

@NamedQueries({ @NamedQuery(name = ValuedCustomer.QUERY_CREDIT_TOTAL, query = "select sum(vc.credit)from ValuedCustomer vc") })
@Entity
@DiscriminatorValue(value="2")
@SuppressWarnings({ "serial", "javadoc" })
public class ValuedCustomer extends Customer {

	public static final String QUERY_CREDIT_TOTAL = "ValuedCustomer.creditTotal";

	@Column(name = "C_CREDIT")
	private int credit = 0;

	@OneToMany(fetch=FetchType.EAGER)
	private List<Customer> references;

	protected ValuedCustomer() {
		super();
	}

	public ValuedCustomer(String first, String last, int credit) {
		super(first, last);
		this.credit = credit;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public List<Customer> getReferences() {
		return references;
	}

	public void setReferences(List<Customer> references) {
		this.references = references;
	}

	@Override
	public String toString() {
		return "Sir " + this.getFirstName() + " " + this.getLastName();
	}
}
