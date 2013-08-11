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
