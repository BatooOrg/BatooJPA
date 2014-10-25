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
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author Administrator
 */
@Entity
@Table(name = "ORDERS")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Orders.findAll", query = "SELECT o FROM Orders o"),
	@NamedQuery(name = "Orders.findByOrderId", query = "SELECT o FROM Orders o WHERE o.orderId = :orderId"),
	@NamedQuery(name = "Orders.findByTripDateTime", query = "SELECT o FROM Orders o WHERE o.tripDateTime = :tripDateTime"),
	@NamedQuery(name = "Orders.findByReportDateTime", query = "SELECT o FROM Orders o WHERE o.reportDateTime = :reportDateTime"),
	@NamedQuery(name = "Orders.findByReturnDateTime", query = "SELECT o FROM Orders o WHERE o.returnDateTime = :returnDateTime"),
	@NamedQuery(name = "Orders.findByTotalCost", query = "SELECT o FROM Orders o WHERE o.totalCost = :totalCost"),
	@NamedQuery(name = "Orders.findByDiscountRate", query = "SELECT o FROM Orders o WHERE o.discountRate = :discountRate"),
	@NamedQuery(name = "Orders.findByDiscount", query = "SELECT o FROM Orders o WHERE o.discount = :discount"),
	@NamedQuery(name = "Orders.findByBalance", query = "SELECT o FROM Orders o WHERE o.balance = :balance"),
	@NamedQuery(name = "Orders.findByDepositPaid", query = "SELECT o FROM Orders o WHERE o.depositPaid = :depositPaid"),
	@NamedQuery(name = "Orders.findByDepositPaidDate", query = "SELECT o FROM Orders o WHERE o.depositPaidDate = :depositPaidDate"),
	@NamedQuery(name = "Orders.findByPaidInFull", query = "SELECT o FROM Orders o WHERE o.paidInFull = :paidInFull"),
	@NamedQuery(name = "Orders.findByPaidInFullDate", query = "SELECT o FROM Orders o WHERE o.paidInFullDate = :paidInFullDate"),
	@NamedQuery(name = "Orders.findByCancelled", query = "SELECT o FROM Orders o WHERE o.cancelled = :cancelled"),
	@NamedQuery(name = "Orders.findByCancelledDate", query = "SELECT o FROM Orders o WHERE o.cancelledDate = :cancelledDate"),
	@NamedQuery(name = "Orders.findByConfirmed", query = "SELECT o FROM Orders o WHERE o.confirmed = :confirmed"),
	@NamedQuery(name = "Orders.findByConfirmedDate", query = "SELECT o FROM Orders o WHERE o.confirmedDate = :confirmedDate"),
	@NamedQuery(name = "Orders.findByContractSent", query = "SELECT o FROM Orders o WHERE o.contractSent = :contractSent"),
	@NamedQuery(name = "Orders.findByContractSentDate", query = "SELECT o FROM Orders o WHERE o.contractSentDate = :contractSentDate"),
	@NamedQuery(name = "Orders.findByInvoiceSent", query = "SELECT o FROM Orders o WHERE o.invoiceSent = :invoiceSent"),
	@NamedQuery(name = "Orders.findByInvoiceSentDate", query = "SELECT o FROM Orders o WHERE o.invoiceSentDate = :invoiceSentDate"),
	@NamedQuery(name = "Orders.findByQuoteSent", query = "SELECT o FROM Orders o WHERE o.quoteSent = :quoteSent"),
	@NamedQuery(name = "Orders.findByQuoteSentDate", query = "SELECT o FROM Orders o WHERE o.quoteSentDate = :quoteSentDate"),
	@NamedQuery(name = "Orders.findByPassengers", query = "SELECT o FROM Orders o WHERE o.passengers = :passengers"),
	@NamedQuery(name = "Orders.findByCreatedBy", query = "SELECT o FROM Orders o WHERE o.createdBy = :createdBy"),
	@NamedQuery(name = "Orders.findByCreatedDt", query = "SELECT o FROM Orders o WHERE o.createdDt = :createdDt"),
	@NamedQuery(name = "Orders.findByChangedDt", query = "SELECT o FROM Orders o WHERE o.changedDt = :changedDt"),
	@NamedQuery(name = "Orders.removeAll", query = "DELETE FROM Orders o") })
@SuppressWarnings("javadoc")
public class Orders implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@NotNull
	@Column(name = "ORDER_ID")
	private Integer orderId;

	@Basic(optional = false)
	@NotNull
	@Column(name = "TRIP_DATE_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date tripDateTime;

	@Column(name = "REPORT_DATE_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportDateTime;

	@Column(name = "RETURN_DATE_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date returnDateTime;

	@Column(name = "TOTAL_COST")
	private Double totalCost;

	@Column(name = "DEPOSIT")
	private Double deposit;

	@Column(name = "DISCOUNT_RATE")
	private Double discountRate;

	@Column(name = "DISCOUNT")
	private Double discount;

	@Column(name = "BALANCE")
	private Double balance;

	@Column(name = "DEPOSIT_PAID")
	private Character depositPaid;

	@Column(name = "DEPOSIT_PAID_DATE")
	@Temporal(TemporalType.DATE)
	private Date depositPaidDate;

	@Column(name = "PAID_IN_FULL")
	private Character paidInFull;

	@Column(name = "PAID_IN_FULL_DATE")
	@Temporal(TemporalType.DATE)
	private Date paidInFullDate;

	@Column(name = "CANCELLED")
	private Character cancelled;

	@Column(name = "CANCELLED_DATE")
	@Temporal(TemporalType.DATE)
	private Date cancelledDate;

	@Column(name = "CONFIRMED")
	private Character confirmed;

	@Column(name = "CONFIRMED_DATE")
	@Temporal(TemporalType.DATE)
	private Date confirmedDate;

	@Column(name = "CONTRACT_SENT")
	private Character contractSent;

	@Column(name = "CONTRACT_SENT_DATE")
	@Temporal(TemporalType.DATE)
	private Date contractSentDate;

	@Column(name = "INVOICE_SENT")
	private Character invoiceSent;

	@Column(name = "INVOICE_SENT_DATE")
	@Temporal(TemporalType.DATE)
	private Date invoiceSentDate;

	@Column(name = "QUOTE_SENT")
	private Character quoteSent;

	@Column(name = "QUOTE_SENT_DATE")
	@Temporal(TemporalType.DATE)
	private Date quoteSentDate;

	@Column(name = "PASSENGERS")
	private Short passengers;

	@Size(max = 25)
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "INTRASTATE")
	private Character intrastate;

	@Column(name = "CREATED_DT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDt;

	@Column(name = "CHANGED_DT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date changedDt;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "orders")
	private Collection<OrderCustomerLeader> orderCustomerLeaders;

	public Orders() {
		super();
	}

	public Orders(Integer orderId) {
		this.orderId = orderId;
	}

	public Orders(Integer orderId, Date tripDateTime) {
		this.orderId = orderId;
		this.tripDateTime = tripDateTime;
	}

	public void addOrderCustomerLeader(OrderCustomerLeader orderCustomerLeader) {
		this.orderCustomerLeaders.add(orderCustomerLeader);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Orders)) {
			return false;
		}
		final Orders other = (Orders) object;
		if (((this.orderId == null) && (other.orderId != null)) || ((this.orderId != null) && !this.orderId.equals(other.orderId))) {
			return false;
		}
		return true;
	}

	public Double getBalance() {
		return this.balance;
	}

	public Character getCancelled() {
		return this.cancelled;
	}

	public Date getCancelledDate() {
		return this.cancelledDate;
	}

	public Date getChangedDt() {
		return this.changedDt;
	}

	public Character getConfirmed() {
		return this.confirmed;
	}

	public Date getConfirmedDate() {
		return this.confirmedDate;
	}

	public Character getContractSent() {
		return this.contractSent;
	}

	public Date getContractSentDate() {
		return this.contractSentDate;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public Date getCreatedDt() {
		return this.createdDt;
	}

	public Double getDeposit() {
		return this.deposit;
	}

	public Character getDepositPaid() {
		return this.depositPaid;
	}

	public Date getDepositPaidDate() {
		return this.depositPaidDate;
	}

	public Double getDiscount() {
		return this.discount;
	}

	public Double getDiscountRate() {
		return this.discountRate;
	}

	public Character getIntrastate() {
		return this.intrastate;
	}

	public Character getInvoiceSent() {
		return this.invoiceSent;
	}

	public Date getInvoiceSentDate() {
		return this.invoiceSentDate;
	}

	@XmlTransient
	public Collection<OrderCustomerLeader> getOrderCustomerLeaders() {
		return this.orderCustomerLeaders;
	}

	public Integer getOrderId() {
		return this.orderId;
	}

	public Character getPaidInFull() {
		return this.paidInFull;
	}

	public Date getPaidInFullDate() {
		return this.paidInFullDate;
	}

	public Short getPassengers() {
		return this.passengers;
	}

	public Character getQuoteSent() {
		return this.quoteSent;
	}

	public Date getQuoteSentDate() {
		return this.quoteSentDate;
	}

	public Date getReportDateTime() {
		return this.reportDateTime;
	}

	public Date getReturnDateTime() {
		return this.returnDateTime;
	}

	public Double getTotalCost() {
		return this.totalCost;
	}

	public Date getTripDateTime() {
		return this.tripDateTime;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.orderId != null ? this.orderId.hashCode() : 0);
		return hash;
	}

	public void removeOrderCustomerLeader(OrderCustomerLeader orderCustomerLeader) {
		this.orderCustomerLeaders.remove(orderCustomerLeader);
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public void setCancelled(Character cancelled) {
		this.cancelled = cancelled;
	}

	public void setCancelledDate(Date cancelledDate) {
		this.cancelledDate = cancelledDate;
	}

	public void setChangedDt(Date changedDt) {
		this.changedDt = changedDt;
	}

	public void setConfirmed(Character confirmed) {
		this.confirmed = confirmed;
	}

	public void setConfirmedDate(Date confirmedDate) {
		this.confirmedDate = confirmedDate;
	}

	public void setContractSent(Character contractSent) {
		this.contractSent = contractSent;
	}

	public void setContractSentDate(Date contractSentDate) {
		this.contractSentDate = contractSentDate;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}

	public void setDepositPaid(Character depositPaid) {
		this.depositPaid = depositPaid;
	}

	public void setDepositPaidDate(Date depositPaidDate) {
		this.depositPaidDate = depositPaidDate;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public void setDiscountRate(Double discountRate) {
		this.discountRate = discountRate;
	}

	public void setIntrastate(Character intrastate) {
		this.intrastate = intrastate;
	}

	public void setInvoiceSent(Character invoiceSent) {
		this.invoiceSent = invoiceSent;
	}

	public void setInvoiceSentDate(Date invoiceSentDate) {
		this.invoiceSentDate = invoiceSentDate;
	}

	public void setOrderCustomerLeaders(Collection<OrderCustomerLeader> orderCustomerLeaders) {
		this.orderCustomerLeaders = orderCustomerLeaders;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public void setPaidInFull(Character paidInFull) {
		this.paidInFull = paidInFull;
	}

	public void setPaidInFullDate(Date paidInFullDate) {
		this.paidInFullDate = paidInFullDate;
	}

	public void setPassengers(Short passengers) {
		this.passengers = passengers;
	}

	public void setQuoteSent(Character quoteSent) {
		this.quoteSent = quoteSent;
	}

	public void setQuoteSentDate(Date quoteSentDate) {
		this.quoteSentDate = quoteSentDate;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public void setReturnDateTime(Date returnDateTime) {
		this.returnDateTime = returnDateTime;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public void setTripDateTime(Date tripDateTime) {
		this.tripDateTime = tripDateTime;
	}

	@Override
	public String toString() {
		return "jpa.entities.Orders[ orderId=" + this.orderId + " ]";
	}

}
