package org.batoo.jpa.core.test.q;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * @author tolgagokmen
 * 
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public class OrderBase {

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	public Item4 item;

	@Id
	@GeneratedValue(generator = "order_id", strategy = GenerationType.TABLE)
	public Long id;

	public Integer quantity;

	public OrderBase() {
		super();
	}

	public Long getId() {
		return this.id;
	}

	public Item4 getItem() {
		return this.item;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setItem(Item4 item) {
		this.item = item;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
