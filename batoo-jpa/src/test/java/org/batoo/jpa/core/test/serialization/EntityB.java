/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
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
package org.batoo.jpa.core.test.serialization;

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

/**
 * 
 * @author ylemoigne
 * @since 2.0.1
 */
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

	public Calendar getCalendar() {
		return this.calendar;
	}

	public String getCode() {
		return this.code;
	}

	public ConcreteEntity getConcreteEntity() {
		return this.concreteEntity;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setConcreteEntity(ConcreteEntity concreteEntity) {
		this.concreteEntity = concreteEntity;
	}
}
