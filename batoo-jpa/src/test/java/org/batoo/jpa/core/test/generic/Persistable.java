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
package org.batoo.jpa.core.test.generic;

import java.io.Serializable;

/**
 * Clone of Spring Persistable
 * 
 * @param <ID>
 *            the type of the identifier
 * @author hceylan
 */
public interface Persistable<ID extends Serializable> extends Serializable {

	/**
	 * Returns the id of the entity.
	 * 
	 * @return the id
	 */
	ID getId();

	/**
	 * Returns if the {@code Persistable} is new or was persisted already.
	 * 
	 * @return if the object is new
	 */
	boolean isNew();
}
