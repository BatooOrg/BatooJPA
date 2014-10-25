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

package org.batoo.jpa.community.test.serializationTestModel;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table
@SuppressWarnings("javadoc")
public class EntityB extends TopMappedSuperClass {
	@Basic
	@Column(nullable = false)
	private String code;
	
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar calendar;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = ConcreteEntity.class)
	@JoinColumn(name = "ID_CONCRETEENTITY", referencedColumnName = "ID")
	private ConcreteEntity concreteEntity;
	
	public EntityB() {
		super();
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	public Calendar getCalendar() {
		return calendar;
	}
	
	public void setConcreteEntity(ConcreteEntity concreteEntity) {
		this.concreteEntity = concreteEntity;
	}
	
	public ConcreteEntity getConcreteEntity() {
		return concreteEntity;
	}
}
