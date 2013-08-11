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

package org.batoo.jpa.core.test.fetch.lazy;

import javax.persistence.Entity;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class WorkPhone extends Phone {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @since 2.0.0
	 */
	public WorkPhone() {
		super();
	}

	/**
	 * @param person
	 *            the person
	 * @param phone
	 *            the phone
	 * 
	 * @since 2.0.0
	 */
	public WorkPhone(Person person, String phone) {
		super(phone);

		person.getWorkPhones().add(this);
	}

}
