package org.batoo.jpa.community.test.i157;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class ConcreteEntity extends AbstractEntity {
	@Basic
	@Column(nullable = false)
	private String code;
	
	@Override
	public String getFoo() {
		return "Foo";
	}
}
