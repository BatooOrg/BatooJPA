package org.batoo.jpa.core.test.q;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author tolgagokmen
 * 
 */
@MappedSuperclass
@SuppressWarnings("javadoc")
public class OrderBase {

	@Id
	@GeneratedValue(generator = "order_id", strategy = GenerationType.TABLE)
	protected Long id;

	protected Integer quantity;

	public OrderBase() {
		super();
	}

	public Long getId() {
		return this.id;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
