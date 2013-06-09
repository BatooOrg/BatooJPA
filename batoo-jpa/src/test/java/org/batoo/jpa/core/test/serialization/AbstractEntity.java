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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author ylemoigne
 * @since 2.0.1
 */
@Entity
@SuppressWarnings("javadoc")
public abstract class AbstractEntity extends TopMappedSuperClass {
	private static final long serialVersionUID = 1L;

	@Basic
	@Column(nullable = false)
	private String myAbstractProperty;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public Date getDate() {
		return this.date;
	}

	public abstract String getFoo();

	public String getMyAbstractProperty() {
		return this.myAbstractProperty;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setMyAbstractProperty(String myAbstractProperty) {
		this.myAbstractProperty = myAbstractProperty;
	}
}
