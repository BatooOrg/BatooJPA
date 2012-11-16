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
package org.batoo.jpa.parser.metadata;

import java.util.List;

/**
 * The definition for entity listeners.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface EntityListenerMetadata extends LocatableMatadata {

	/**
	 * 
	 * Entity listener callback types.
	 * 
	 * @since 2.0.0
	 */
	public enum EntityListenerType {
		/**
		 * Callback type that is called prior to persist operations.
		 */
		PRE_PERSIST,

		/**
		 * Callback type that is called prior to update operations.
		 */
		PRE_UPDATE,

		/**
		 * Callback type that is called prior to remove operations.
		 */
		PRE_REMOVE,

		/**
		 * Callback type that is called after load operations.
		 */
		POST_LOAD,

		/**
		 * Callback type that is called after persist operations.
		 */
		POST_PERSIST,

		/**
		 * Callback type that is called after update operations.
		 */
		POST_UPDATE,

		/**
		 * Callback type that is called after remove operations.
		 */
		POST_REMOVE
	}

	/**
	 * Returns the list of callbacks.
	 * 
	 * @return the list of callbacks
	 * 
	 * @since 2.0.0
	 */
	List<CallbackMetadata> getCallbacks();

	/**
	 * Returns the entity listener class.
	 * 
	 * @return the entity listener class
	 * 
	 * @since 2.0.0
	 */
	String getClassName();
}
