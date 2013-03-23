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
package org.batoo.jpa.community.test.criteriaQueryWithCalendar;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table
@SuppressWarnings("javadoc")
public class CalendarTemporalFieldEntity {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id = null;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar calendar;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public CalendarTemporalFieldEntity() {
		super();
	}

	public Calendar getCalendar() {
		return this.calendar;
	}

	public Date getDate() {
		return this.date;
	}

	public Long getId() {
		return this.id;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
