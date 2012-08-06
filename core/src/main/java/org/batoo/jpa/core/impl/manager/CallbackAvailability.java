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
package org.batoo.jpa.core.impl.manager;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class CallbackAvailability {

	private boolean preRemove;
	private boolean postRemove;
	private boolean preWrite;
	private boolean postWrite;
	private boolean postLoad;

	/**
	 * Returns if there is postRemove callbacks.
	 * 
	 * @return true if there is PreRemove callbacks, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean postRemove() {
		return this.preRemove;
	}

	/**
	 * Returns if there is PostWrite callbacks.
	 * 
	 * @return true if there is PostWrite callbacks, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean postWrite() {
		return this.postWrite;
	}

	/**
	 * Returns if there is PreRemove callbacks.
	 * 
	 * @return true if there is PreRemove callbacks, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean preRemove() {
		return this.preRemove;
	}

	/**
	 * Returns if there is PreWrite callbacks.
	 * 
	 * @return true if there is PreWrite callbacks, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean preWrite() {
		return this.preWrite;
	}

	/**
	 * @param availability
	 *            the availability to update
	 * @param forUpdates
	 *            true if for updates or false for removals
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void updateAvailability(CallbackAvailability availability, Boolean forUpdates) {
		if ((forUpdates == null) || forUpdates) {
			this.preWrite |= availability.preWrite;
			this.postWrite |= availability.postWrite;
		}

		if ((forUpdates == null) || !forUpdates) {
			this.preRemove |= availability.preRemove;
			this.postRemove |= availability.postRemove;
		}

		if (forUpdates == null) {
			this.postLoad |= availability.postLoad;
		}
	}

	/**
	 * Updates the availability.
	 * 
	 * @param callbackManager
	 *            the callback manager
	 * @return the updated availability
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CallbackAvailability updateAvailability(CallbackManager callbackManager) {
		this.preRemove |= callbackManager.preRemove() != null;
		this.postRemove |= callbackManager.postRemove() != null;

		this.preWrite |= (callbackManager.prePersist() != null) || (callbackManager.preUpdate() != null);
		this.postWrite |= (callbackManager.postPersist() != null) || (callbackManager.postUpdate() != null);

		this.postLoad |= callbackManager.postLoad() != null;

		return this;
	}
}
