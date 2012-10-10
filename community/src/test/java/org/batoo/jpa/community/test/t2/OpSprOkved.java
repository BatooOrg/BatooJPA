package org.batoo.jpa.community.test.t2;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SPI_V_OP_SPR_OKVED")
public class OpSprOkved implements Serializable {

	/** id */
	@Id
	@Column(name = "ID", nullable = false)
	private Long id;

	/** code */
	@Column(name = "CODE")
	private String code;

	/** name */
	@Column(name = "name")
	private String name;

	public String getCode() {
		return this.code;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
