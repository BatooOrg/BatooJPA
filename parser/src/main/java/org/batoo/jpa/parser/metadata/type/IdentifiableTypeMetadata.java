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
package org.batoo.jpa.parser.metadata.type;

import java.util.List;

import org.batoo.jpa.parser.metadata.CallbackMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;

/**
 * The definition for identifiable types.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface IdentifiableTypeMetadata extends ManagedTypeMetadata {

	/**
	 * Returns if the default listeners are excluded.
	 * 
	 * @return true the default listeners are excluded, false otherwise
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	boolean excludeDefaultListeners();

	/**
	 * Returns if the super class listeners are excluded.
	 * 
	 * @return true the super class listeners are excluded, false otherwise
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	boolean excludeSuperclassListeners();

	/**
	 * Returns the callbacks of the identifiable type.
	 * 
	 * @return the callbacks of the identifiable type
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	List<CallbackMetadata> getCallbacks();

	/**
	 * Returns the id class of the identifiable type.
	 * 
	 * @return the id class of the identifiable type
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	String getIdClass();

	/**
	 * Returns the entity listeners of the identifiable type.
	 * 
	 * @return the entity listeners of the identifiable type
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	List<EntityListenerMetadata> getListeners();
}
