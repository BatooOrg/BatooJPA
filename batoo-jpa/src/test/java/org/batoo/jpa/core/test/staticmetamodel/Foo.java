package org.batoo.jpa.core.test.staticmetamodel;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@SuppressWarnings("javadoc")
public class Foo {
	@Id
	protected Long id = null;

	@Basic
	private Long timeStamp;

	@Basic
	private String fooName;

	@ManyToOne
	private Bar bar;

	public Bar getBar() {
		return this.bar;
	}

	public String getFooName() {
		return this.fooName;
	}

	public Long getId() {
		return this.id;
	}

	public Long getTimeStamp() {
		return this.timeStamp;
	}

	public void setBar(Bar bar) {
		this.bar = bar;
	}

	public void setFooName(String fooName) {
		this.fooName = fooName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
