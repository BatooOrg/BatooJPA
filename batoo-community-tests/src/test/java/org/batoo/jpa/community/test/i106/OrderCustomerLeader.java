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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Administrator
 */
@Entity
@Table(name = "ORDER_CUSTOMER_LEADER")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "OrderCustomerLeader.findAll", query = "SELECT o FROM OrderCustomerLeader o"),
	@NamedQuery(name = "OrderCustomerLeader.findByOrderId", query = "SELECT o FROM OrderCustomerLeader o WHERE o.orderCustomerLeaderPK.orderId = :orderId"),
	@NamedQuery(
		name = "OrderCustomerLeader.findByCustomerId",
		query = "SELECT o FROM OrderCustomerLeader o WHERE o.orderCustomerLeaderPK.customerId = :customerId"),
	@NamedQuery(
		name = "OrderCustomerLeader.findByLeaderPointOfContactId",
		query = "SELECT o FROM OrderCustomerLeader o WHERE o.orderCustomerLeaderPK.leaderPointOfContactId = :leaderPointOfContactId"),
	@NamedQuery(name = "OrderCustomerLeader.removeAll", query = "DELETE FROM OrderCustomerLeader o") })
@SuppressWarnings("javadoc")
public class OrderCustomerLeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected OrderCustomerLeaderPK orderCustomerLeaderPK;

	@JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private Orders orders;

	public OrderCustomerLeader() {
		super();
	}

	public OrderCustomerLeader(int orderId, int customerId, int pointOfContactId) {
		this.orderCustomerLeaderPK = new OrderCustomerLeaderPK(orderId, customerId, pointOfContactId);
	}

	public OrderCustomerLeader(OrderCustomerLeaderPK orderCustomerLeaderPK) {
		this.orderCustomerLeaderPK = orderCustomerLeaderPK;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof OrderCustomerLeader)) {
			return false;
		}
		final OrderCustomerLeader other = (OrderCustomerLeader) object;
		if (((this.orderCustomerLeaderPK == null) && (other.orderCustomerLeaderPK != null))
			|| ((this.orderCustomerLeaderPK != null) && !this.orderCustomerLeaderPK.equals(other.orderCustomerLeaderPK))) {
			return false;
		}
		return true;
	}

	public OrderCustomerLeaderPK getOrderCustomerLeaderPK() {
		return this.orderCustomerLeaderPK;
	}

	public Orders getOrders() {
		return this.orders;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.orderCustomerLeaderPK != null ? this.orderCustomerLeaderPK.hashCode() : 0);
		return hash;
	}

	public void setOrderCustomerLeaderPK(OrderCustomerLeaderPK orderCustomerLeaderPK) {
		this.orderCustomerLeaderPK = orderCustomerLeaderPK;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "jpa.entities.OrderCustomerLeader[ orderCustomerLeaderPK=" + this.orderCustomerLeaderPK + " ]";
	}

}
