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
package org.batoo.jpa.core.test.fetch.lazy;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@MappedSuperclass
public class Phone implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	private String phoneNo;

	/**
	 * @since 2.0.0
	 */
	public Phone() {
		super();
	}

	/**
	 * @param phone
	 *            the phone number
	 * 
	 * @since 2.0.0
	 */
	public Phone(String phone) {
		super();

		this.phoneNo = phone;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the phoneNo.
	 * 
	 * @return the phoneNo
	 * @since 2.0.0
	 */
	public String getPhoneNo() {
		return this.phoneNo;
	}

	/**
	 * Sets the phoneNo.
	 * 
	 * @param phoneNo
	 *            the phoneNo to set
	 * @since 2.0.0
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
}
