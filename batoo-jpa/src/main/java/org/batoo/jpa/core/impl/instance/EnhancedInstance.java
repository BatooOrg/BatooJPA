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

package org.batoo.jpa.core.impl.instance;

import java.io.Serializable;

import org.batoo.common.reflect.InternalInstance;

/**
 * Interface implemented by enhanced managed instances.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface EnhancedInstance extends Serializable, InternalInstance {

	/**
	 * Returns the managed instance of the instance.
	 * 
	 * @return the managed instance of the instance
	 * 
	 * @since 2.0.0
	 */
	ManagedInstance<?> __enhanced__$$__getManagedInstance();

	/**
	 * Returns if the instance has been initialized.
	 * 
	 * @return true if the instance has been initialized
	 * 
	 * @since 2.0.0
	 */
	boolean __enhanced__$$__isInitialized();

	/**
	 * Marks the instance as initialized.
	 * 
	 * @since 2.0.0
	 */
	void __enhanced__$$__setInitialized();

	/**
	 * Sets the managed instance of the instance.
	 * 
	 * @param instance
	 *            the instance to set
	 * 
	 * @since 2.0.0
	 */
	void __enhanced__$$__setManagedInstance(ManagedInstance<?> instance);
}
