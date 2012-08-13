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
package org.batoo.jpa.core.test.listener;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class FooListener {

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostLoad
	public void postLoad(Object instance) {
		final String string = "listener1PostLoad";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostPersist
	public void postPersist(Object instance) {
		final String string = "listener1PostPersist";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostRemove
	public void postRemove(Object instance) {
		final String string = "listener1PostRemove";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PostUpdate
	public void preLoad(Object instance) {
		final String string = "listener1PostUpdate";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PrePersist
	public void prePersist(Object instance) {
		final String string = "listener1PrePersist";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PreRemove
	public void preRemove(Object instance) {
		final String string = "listener1PreRemove";
		this.update(instance, string);
	}

	/**
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@PreUpdate
	public void preUpdate(Object instance) {
		final String string = "listener1PreUpdate";
		this.update(instance, string);
	}

	private void update(Object instance, String string) {
		final FooType foo = (FooType) instance;
		foo.setValue(foo.getValue() + string);
	}
}
