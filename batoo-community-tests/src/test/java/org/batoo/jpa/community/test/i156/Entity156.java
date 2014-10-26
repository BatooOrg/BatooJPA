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

package org.batoo.jpa.community.test.i156;

import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table
public class Entity156 {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id = null;

	@Version
	@Column(nullable = false)
	protected Long timeStamp = null;

	@Basic
	@Column(nullable = false)
	private String code;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ENTITY156_NAME", joinColumns = @JoinColumn(name = "ID"))
	@MapKeyEnumerated(EnumType.STRING)
	@MapKeyColumn(name = "LOCALE", nullable = false)
	@Column(name = "TRANSLATION", nullable = false)
	private Map<FieldLocale, String> name;

	/**
	 * 
	 * @return the code
	 * 
	 * @since 2.0.1
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 
	 * @return the id
	 * 
	 * @since 2.0.1
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * 
	 * @return the name
	 * 
	 * @since 2.0.1
	 */
	public Map<FieldLocale, String> getName() {
		return this.name;
	}

	/**
	 * 
	 * @return the timeStamp
	 * 
	 * @since 2.0.1
	 */
	public Long getTimeStamp() {
		return this.timeStamp;
	}

	/**
	 * 
	 * @param code
	 *            the code to set
	 * 
	 * @since 2.0.1
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 
	 * @param id
	 *            the id to set
	 * 
	 * @since 2.0.1
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * @param name
	 *            the name to set
	 * 
	 * @since 2.0.1
	 */
	public void setName(Map<FieldLocale, String> name) {
		this.name = name;
	}

	/**
	 * 
	 * @param timeStamp
	 *            the timeStamp to set
	 * 
	 * @since 2.0.1
	 */
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
