/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.batoo.jpa.community.test.i106;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author Administrator
 */
@Embeddable
@SuppressWarnings("javadoc")
public class OrderCustomerLeaderPK implements Serializable {
	@Basic(optional = false)
	@NotNull
	@Column(name = "ORDER_ID")
	private int orderId;

	@Basic(optional = false)
	@NotNull
	@Column(name = "CUSTOMER_ID")
	private int customerId;

	@Basic(optional = false)
	@NotNull
	@Column(name = "LEADER_POINT_OF_CONTACT_ID")
	private int leaderPointOfContactId;

	public OrderCustomerLeaderPK() {
		super();
	}

	public OrderCustomerLeaderPK(int orderId, int customerId, int leaderPointOfContactId) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.leaderPointOfContactId = leaderPointOfContactId;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof OrderCustomerLeaderPK)) {
			return false;
		}
		final OrderCustomerLeaderPK other = (OrderCustomerLeaderPK) object;
		if (this.orderId != other.orderId) {
			return false;
		}
		if (this.customerId != other.customerId) {
			return false;
		}
		if (this.leaderPointOfContactId != other.leaderPointOfContactId) {
			return false;
		}
		return true;
	}

	public int getCustomerId() {
		return this.customerId;
	}

	public int getLeaderPointOfContactId() {
		return this.leaderPointOfContactId;
	}

	public int getOrderId() {
		return this.orderId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += this.orderId;
		hash += this.customerId;
		hash += this.leaderPointOfContactId;
		return hash;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setLeaderPointOfContactId(int leaderPointOfContactId) {
		this.leaderPointOfContactId = leaderPointOfContactId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "jpa.entities.OrderCustomerLeaderPK[ orderId=" + this.orderId + ", customerId=" + this.customerId + ", leaderPointOfContactId="
			+ this.leaderPointOfContactId + " ]";
	}
}
