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
package org.batoo.jpa.core.impl.deployment;

import java.util.concurrent.Callable;

import javax.persistence.metamodel.ManagedType;

import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.model.type.MappedSuperclassTypeImpl;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

/**
 * Implementation for deployment unit tasks.
 * <p>
 * Implements common behavior like ordering and waiting for the super type to perform first.
 * 
 * @author hceylan
 * @since $version
 */
public final class DeploymentUnitTask implements Callable<Void>, Comparable<DeploymentUnitTask> {

	private final Object unit;

	@SuppressWarnings("rawtypes")
	private final DeploymentManager manager;

	/**
	 * @param manager
	 *            the deployment unit manager
	 * @param unit
	 *            the type of this task
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DeploymentUnitTask(@SuppressWarnings("rawtypes") DeploymentManager manager, Object unit) {
		super();

		this.manager = manager;
		this.unit = unit;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final Void call() throws Exception {
		final IdentifiableTypeImpl<?> parent = this.unit instanceof IdentifiableTypeImpl ? ((IdentifiableTypeImpl) this.unit).getSupertype() : null;

		// if the super type hasn't performed yet, then wait for to finish
		while (!this.manager.hasPerformed(parent)) {
			try {
				Thread.sleep(10);
			}
			catch (final InterruptedException e) {
				return null;
			}
		}

		try {
			return this.manager.perform(this.unit);
		}
		finally {
			this.manager.performed(this.unit);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(DeploymentUnitTask o) {
		// named queries has no precedence
		if (this.unit instanceof NamedQueryMetadata) {
			return 0;
		}

		final ManagedType<?> thisUnit = (ManagedType<?>) this.unit;
		final ManagedType<?> otherUnit = (ManagedType<?>) o.unit;

		// if this type is Embeddable and the other type is not then perform this earlier
		if ((thisUnit instanceof EmbeddableTypeImpl) && (!(otherUnit instanceof EmbeddableTypeImpl))) {
			return -1;
		}

		// if the other type is Embeddable and this type is not then perform the other earlier
		if ((otherUnit instanceof EmbeddableTypeImpl) && (!(thisUnit instanceof EmbeddableTypeImpl))) {
			return 1;
		}

		// if this type is a MappedSuperClass and the other type is not then perform this earlier
		if ((thisUnit instanceof MappedSuperclassTypeImpl) && (!(otherUnit instanceof MappedSuperclassTypeImpl))) {
			return -1;
		}

		// if the other type is a MappedSuperClass and this type is not then perform this later
		if ((otherUnit instanceof MappedSuperclassTypeImpl) && (!(thisUnit instanceof MappedSuperclassTypeImpl))) {
			return 1;
		}

		// if the other type is super type of this type then perform other earlier
		Class<?> javaType = thisUnit.getJavaType();
		while (javaType.getSuperclass() != null) {
			javaType = javaType.getSuperclass();
			if (javaType == otherUnit.getJavaType()) {
				return 1;
			}
		}

		// it is safe to perform this earlier.
		return -1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final DeploymentUnitTask other = (DeploymentUnitTask) obj;
		if (this.unit == null) {
			if (other.unit != null) {
				return false;
			}
		}
		else if (!this.unit.equals(other.unit)) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = (prime * result) + ((this.unit == null) ? 0 : this.unit.hashCode());

		return result;
	}

}
