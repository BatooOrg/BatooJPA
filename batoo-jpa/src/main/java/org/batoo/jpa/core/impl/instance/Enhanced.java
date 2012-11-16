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
package org.batoo.jpa.core.impl.instance;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * A Sample class for the enhanced type. Only exists as a reference
 * 
 * @author hceylan
 * @since 2.0.0
 */
@SuppressWarnings("javadoc")
public class Enhanced implements EnhancedInstance {

	private static final long serialVersionUID = 1L;

	private boolean __enhanced_$$__initialized;
	private transient final Object __enhanced_$$__id;
	private transient final Class<?> __enhanced_$$__type;
	private transient final SessionImpl __enhanced_$$__session;
	private transient ManagedInstance<?> __enhanced__$$__managedInstance;

	public Enhanced() {
		super();

		this.__enhanced_$$__id = null;
		this.__enhanced_$$__type = null;
		this.__enhanced_$$__session = null;
	}

	public Enhanced(Class<?> type, SessionImpl session, Object id, boolean initialized) {
		super();

		this.__enhanced_$$__type = type;
		this.__enhanced_$$__session = session;
		this.__enhanced_$$__id = id;
		this.__enhanced_$$__initialized = initialized;
	}

	@Override
	public ManagedInstance<?> __enhanced__$$__getManagedInstance() {
		return this.__enhanced__$$__managedInstance;
	}

	@Override
	public boolean __enhanced__$$__isInitialized() {
		return this.__enhanced_$$__initialized;
	}

	@Override
	public void __enhanced__$$__setInitialized() {
		this.__enhanced_$$__initialized = true;
	}

	@Override
	public void __enhanced__$$__setManagedInstance(ManagedInstance<?> instance) {
		this.__enhanced__$$__managedInstance = instance;
	}

	@SuppressWarnings("unused")
	private void __enhanced_$$__check() {
		if (!this.__enhanced_$$__initialized) {
			if (this.__enhanced_$$__session == null) {
				throw new PersistenceException("No session to initialize the instance");
			}

			this.__enhanced_$$__session.getEntityManager().find(this.__enhanced_$$__type, this.__enhanced_$$__id);

			this.__enhanced_$$__initialized = true;
		}

		if (this.__enhanced_$$__session != null) {
			this.__enhanced__$$__managedInstance.changed();
		}

		return;
	}

	public Object get__enhanced__$$__id() {
		return this.__enhanced_$$__id;
	}
}
